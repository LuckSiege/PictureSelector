package com.yalantis.ucrop.task;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.Log;

import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yalantis.ucrop.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Creates and returns a Bitmap for a given Uri(String url).
 * inSampleSize is calculated based on requiredWidth property. However can be adjusted if OOM occurs.
 * If any EXIF config is found - bitmap is transformed properly.
 */
public class BitmapLoadTask extends AsyncTask<Void, Void, BitmapLoadTask.BitmapWorkerResult> {

    private static final String TAG = "BitmapWorkerTask";

    private final Context mContext;
    private Uri mInputUri;
    private Uri mOutputUri;
    private final int mRequiredWidth;
    private final int mRequiredHeight;

    private final BitmapLoadCallback mBitmapLoadCallback;

    public static class BitmapWorkerResult {

        Bitmap mBitmapResult;
        ExifInfo mExifInfo;
        Exception mBitmapWorkerException;

        public BitmapWorkerResult(@NonNull Bitmap bitmapResult, @NonNull ExifInfo exifInfo) {
            mBitmapResult = bitmapResult;
            mExifInfo = exifInfo;
        }

        public BitmapWorkerResult(@NonNull Exception bitmapWorkerException) {
            mBitmapWorkerException = bitmapWorkerException;
        }

    }

    public BitmapLoadTask(@NonNull Context context,
                          @NonNull Uri inputUri, @Nullable Uri outputUri,
                          int requiredWidth, int requiredHeight,
                          BitmapLoadCallback loadCallback) {
        mContext = context;
        mInputUri = inputUri;
        mOutputUri = outputUri;
        mRequiredWidth = requiredWidth;
        mRequiredHeight = requiredHeight;
        mBitmapLoadCallback = loadCallback;
    }

    @Override
    @NonNull
    protected BitmapWorkerResult doInBackground(Void... params) {
        if (mInputUri == null) {
            return new BitmapWorkerResult(new NullPointerException("Input Uri cannot be null"));
        }

        try {
            processInputUri();
        } catch (NullPointerException | IOException e) {
            return new BitmapWorkerResult(e);
        }

        final ParcelFileDescriptor parcelFileDescriptor;
        try {
            parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(mInputUri, "r");
        } catch (FileNotFoundException e) {
            return new BitmapWorkerResult(e);
        }

        final FileDescriptor fileDescriptor;
        if (parcelFileDescriptor != null) {
            fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        } else {
            return new BitmapWorkerResult(new NullPointerException("ParcelFileDescriptor was null for given Uri: [" + mInputUri + "]"));
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        if (options.outWidth == -1 || options.outHeight == -1) {
            return new BitmapWorkerResult(new IllegalArgumentException("Bounds for bitmap could not be retrieved from the Uri: [" + mInputUri + "]"));
        }

        options.inSampleSize = BitmapLoadUtils.calculateInSampleSize(options, mRequiredWidth, mRequiredHeight);
        options.inJustDecodeBounds = false;

        Bitmap decodeSampledBitmap = null;

        boolean decodeAttemptSuccess = false;
        while (!decodeAttemptSuccess) {
            try {
                decodeSampledBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                decodeAttemptSuccess = true;
            } catch (OutOfMemoryError error) {
                Log.e(TAG, "doInBackground: BitmapFactory.decodeFileDescriptor: ", error);
                options.inSampleSize *= 2;
            }
        }

        if (decodeSampledBitmap == null) {
            return new BitmapWorkerResult(new IllegalArgumentException("Bitmap could not be decoded from the Uri: [" + mInputUri + "]"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            BitmapLoadUtils.close(parcelFileDescriptor);
        }

        int exifOrientation = BitmapLoadUtils.getExifOrientation(mContext, mInputUri);
        int exifDegrees = BitmapLoadUtils.exifToDegrees(exifOrientation);
        int exifTranslation = BitmapLoadUtils.exifToTranslation(exifOrientation);

        ExifInfo exifInfo = new ExifInfo(exifOrientation, exifDegrees, exifTranslation);

        Matrix matrix = new Matrix();
        if (exifDegrees != 0) {
            matrix.preRotate(exifDegrees);
        }
        if (exifTranslation != 1) {
            matrix.postScale(exifTranslation, 1);
        }
        if (!matrix.isIdentity()) {
            return new BitmapWorkerResult(BitmapLoadUtils.transformBitmap(decodeSampledBitmap, matrix), exifInfo);
        }

        return new BitmapWorkerResult(decodeSampledBitmap, exifInfo);
    }

    private void processInputUri() throws NullPointerException, IOException {
        String inputUriScheme = mInputUri.toString();
        Log.d(TAG, "Uri scheme: " + inputUriScheme);
        if (inputUriScheme.startsWith("http") || inputUriScheme.startsWith("https")) {
            try {
                downloadFile(mInputUri, mOutputUri);
            } catch (NullPointerException | IOException e) {
                Log.e(TAG, "Downloading failed", e);
                throw e;
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    ParcelFileDescriptor parcelFileDescriptor =
                            mContext.getContentResolver().openFileDescriptor(mInputUri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    FileInputStream inputStream = new FileInputStream(fileDescriptor);
                    FileUtils.copyFile(inputStream, mOutputUri.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String path = getFilePath();
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    mInputUri = Uri.fromFile(new File(path));
                } else {
                    try {
                        copyFile(mInputUri, mOutputUri);
                    } catch (NullPointerException | IOException e) {
                        Log.e(TAG, "Copying failed", e);
                        throw e;
                    }
                }
            }
        }
    }

    private String getFilePath() {
        if (ContextCompat.checkSelfPermission(mContext, permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return FileUtils.getPath(mContext, mInputUri);
        } else {
            return null;
        }
    }

    private void copyFile(@NonNull Uri inputUri, @Nullable Uri outputUri) throws NullPointerException, IOException {
        Log.d(TAG, "copyFile");

        if (outputUri == null) {
            throw new NullPointerException("Output Uri is null - cannot copy image");
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = mContext.getContentResolver().openInputStream(inputUri);
            outputStream = new FileOutputStream(new File(outputUri.getPath()));
            if (inputStream == null) {
                throw new NullPointerException("InputStream for given input Uri is null");
            }

            byte buffer[] = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            BitmapLoadUtils.close(outputStream);
            BitmapLoadUtils.close(inputStream);

            // swap uris, because input image was copied to the output destination
            // (cropped image will override it later)
            mInputUri = mOutputUri;
        }
    }

    private void downloadFile(@NonNull Uri inputUri, @Nullable Uri outputUri) throws NullPointerException, IOException {
        Log.d(TAG, "downloadFile");

        if (outputUri == null) {
            throw new NullPointerException("Output Uri is null - cannot download image");
        }
        try {
            URL u = new URL(inputUri.toString());
            byte[] buffer = new byte[1024];
            int read;
            BufferedInputStream bin;
            bin = new BufferedInputStream(u.openStream());
            OutputStream outputStream = mContext.getContentResolver().openOutputStream(outputUri);
            BufferedOutputStream bout = new BufferedOutputStream(
                    outputStream);
            while ((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
            }
            bout.flush();
            bout.close();
            bin.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        mInputUri = mOutputUri;
    }

//    private void downloadFile(@NonNull Uri inputUri, @Nullable Uri outputUri) throws NullPointerException, IOException {
//        Log.d(TAG, "downloadFile");
//
//        if (outputUri == null) {
//            throw new NullPointerException("Output Uri is null - cannot download image");
//        }
//
//        OkHttpClient client = new OkHttpClient();
//
//        BufferedSource source = null;
//        Sink sink = null;
//        Response response = null;
//        try {
//            Request request = new Request.Builder()
//                    .url(inputUri.toString())
//                    .build();
//            response = client.newCall(request).execute();
//            source = response.body().source();
//
//            OutputStream outputStream = mContext.getContentResolver().openOutputStream(outputUri);
//            if (outputStream != null) {
//                sink = Okio.sink(outputStream);
//                source.readAll(sink);
//            } else {
//                throw new NullPointerException("OutputStream for given output Uri is null");
//            }
//        } finally {
//            BitmapLoadUtils.close(source);
//            BitmapLoadUtils.close(sink);
//            if (response != null) {
//                BitmapLoadUtils.close(response.body());
//            }
//            client.dispatcher().cancelAll();
//
//            // swap uris, because input image was downloaded to the output destination
//            // (cropped image will override it later)
//            mInputUri = mOutputUri;
//        }
//    }

    @Override
    protected void onPostExecute(@NonNull BitmapWorkerResult result) {
        if (result.mBitmapWorkerException == null) {
            mBitmapLoadCallback.onBitmapLoaded(result.mBitmapResult, result.mExifInfo, mInputUri, (mOutputUri == null) ? null : mOutputUri);
        } else {
            mBitmapLoadCallback.onFailure(result.mBitmapWorkerException);
        }
    }

}
