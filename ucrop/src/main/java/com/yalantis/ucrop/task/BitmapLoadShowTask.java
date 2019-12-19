package com.yalantis.ucrop.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yalantis.ucrop.callback.BitmapLoadShowCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * Creates and returns a Bitmap for a given Uri(String url).
 * inSampleSize is calculated based on requiredWidth property. However can be adjusted if OOM occurs.
 * If any EXIF config is found - bitmap is transformed properly.
 */
public class BitmapLoadShowTask extends AsyncTask<Void, Void, BitmapLoadShowTask.BitmapWorkerResult> {

    private static final String TAG = "BitmapWorkerTask";

    private final Context mContext;
    private Uri mInputUri;
    private final int mRequiredWidth;
    private final int mRequiredHeight;
    private Uri mOutputUri;
    private final BitmapLoadShowCallback mBitmapLoadShowCallback;

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

    public BitmapLoadShowTask(@NonNull Context context,
                              @NonNull Uri inputUri, @Nullable Uri outputUri,
                              int requiredWidth, int requiredHeight,
                              BitmapLoadShowCallback loadCallback) {
        mContext = context;
        mInputUri = inputUri;
        mOutputUri = outputUri;
        mRequiredWidth = requiredWidth;
        mRequiredHeight = requiredHeight;
        mBitmapLoadShowCallback = loadCallback;
    }

    @Override
    @NonNull
    protected BitmapWorkerResult doInBackground(Void... params) {
        if (mInputUri == null) {
            return new BitmapWorkerResult(new NullPointerException("Input Uri cannot be null"));
        }

        try {
            if (mOutputUri != null && !TextUtils.isEmpty(mOutputUri.getPath())) {
                if (!new File(mOutputUri.getPath()).exists()) {
                    processInputUri();
                } else {
                    mInputUri = mOutputUri;
                }
            }
        } catch (NullPointerException | IOException e) {
            return new BitmapLoadShowTask.BitmapWorkerResult(e);
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

    @Override
    protected void onPostExecute(@NonNull BitmapWorkerResult result) {
        if (result.mBitmapWorkerException == null) {
            mBitmapLoadShowCallback.onBitmapLoaded(result.mBitmapResult);
        } else {
            mBitmapLoadShowCallback.onFailure(result.mBitmapWorkerException);
        }
    }

}
