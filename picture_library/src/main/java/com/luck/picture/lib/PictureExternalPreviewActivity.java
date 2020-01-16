package com.luck.picture.lib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.luck.picture.lib.broadcast.BroadcastAction;
import com.luck.picture.lib.broadcast.BroadcastManager;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.dialog.PictureCustomDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.ImageCompleteCallback;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.BitmapUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.JumpUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.ValueOf;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @data：2017/01/18 下午1:00
 * @描述: 预览图片
 */
public class PictureExternalPreviewActivity extends PictureBaseActivity implements View.OnClickListener {
    private static final int SAVE_IMAGE_ERROR = -1;
    private static final int SAVE_IMAGE_SUCCESSFUL = 0;

    private ImageButton ibLeftBack;
    private TextView tvTitle;
    private PreviewViewPager viewPager;
    private List<LocalMedia> images = new ArrayList<>();
    private int position = 0;
    private SimpleFragmentAdapter adapter;
    private LoadDataThread mLoadDataThread;
    private String downloadPath;
    private String mMimeType;
    private ImageButton ibDelete;
    private boolean isAndroidQ;
    private View titleViewBg;

    @Override
    public int getResourceId() {
        return R.layout.picture_activity_external_preview;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        titleViewBg = findViewById(R.id.titleViewBg);
        tvTitle = findViewById(R.id.picture_title);
        ibLeftBack = findViewById(R.id.left_back);
        ibDelete = findViewById(R.id.ib_delete);
        viewPager = findViewById(R.id.preview_pager);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        images = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        ibLeftBack.setOnClickListener(this);
        ibDelete.setOnClickListener(this);
        ibDelete.setVisibility(config.style != null ? config.style.pictureExternalPreviewGonePreviewDelete
                ? View.VISIBLE : View.GONE : View.GONE);
        initViewPageAdapterData();
    }

    /**
     * 设置样式
     */
    @Override
    public void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureTitleTextColor != 0) {
                tvTitle.setTextColor(config.style.pictureTitleTextColor);
            }
            if (config.style.pictureTitleTextSize != 0) {
                tvTitle.setTextSize(config.style.pictureTitleTextSize);
            }
            if (config.style.pictureLeftBackIcon != 0) {
                ibLeftBack.setImageResource(config.style.pictureLeftBackIcon);
            }
            if (config.style.pictureExternalPreviewDeleteStyle != 0) {
                ibDelete.setImageResource(config.style.pictureExternalPreviewDeleteStyle);
            }
            if (config.style.pictureTitleBarBackgroundColor != 0) {
                titleViewBg.setBackgroundColor(colorPrimary);
            }
        } else {
            int previewBgColor = AttrsUtils.getTypeValueColor(getContext(), R.attr.picture_ac_preview_title_bg);
            if (previewBgColor != 0) {
                titleViewBg.setBackgroundColor(previewBgColor);
            } else {
                titleViewBg.setBackgroundColor(colorPrimary);
            }
        }
    }

    private void initViewPageAdapterData() {
        tvTitle.setText(getString(R.string.picture_preview_image_num,
                position + 1, images.size()));
        adapter = new SimpleFragmentAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int index) {
                tvTitle.setText(getString(R.string.picture_preview_image_num,
                        index + 1, images.size()));
                position = index;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.left_back) {
            finish();
            exitAnimation();
        } else if (id == R.id.ib_delete) {
            if (images != null && images.size() > 0) {
                int currentItem = viewPager.getCurrentItem();
                images.remove(currentItem);
                adapter.removeCacheView(currentItem);
                // 删除通知用户更新
                Bundle bundle = new Bundle();
                bundle.putInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION, currentItem);
                BroadcastManager.getInstance(getContext())
                        .action(BroadcastAction.ACTION_DELETE_PREVIEW_POSITION)
                        .extras(bundle).broadcast();
                if (images.size() == 0) {
                    onBackPressed();
                    return;
                }
                tvTitle.setText(getString(R.string.picture_preview_image_num,
                        position + 1, images.size()));
                position = currentItem;
                adapter.notifyDataSetChanged();
            }
        }
    }

    public class SimpleFragmentAdapter extends PagerAdapter {

        /**
         * 最大缓存图片数量
         */
        private static final int MAX_CACHE_SIZE = 20;
        /**
         * 缓存view
         */
        private SparseArray<View> mCacheView;

        private void clear() {
            if (null != mCacheView) {
                mCacheView.clear();
                mCacheView = null;
            }
        }

        public void removeCacheView(int position) {
            if (mCacheView != null && position < mCacheView.size()) {
                mCacheView.removeAt(position);
            }
        }

        public SimpleFragmentAdapter() {
            super();
            this.mCacheView = new SparseArray<>();
        }

        @Override
        public int getCount() {
            return images != null ? images.size() : 0;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
            if (mCacheView.size() > MAX_CACHE_SIZE) {
                mCacheView.remove(position);
            }
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View contentView = mCacheView.get(position);
            if (contentView == null) {
                contentView = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.picture_image_preview, container, false);
                // 常规图控件
                final PhotoView imageView = contentView.findViewById(R.id.preview_image);
                // 长图控件
                final SubsamplingScaleImageView longImageView = contentView.findViewById(R.id.longImg);
                // 视频播放按钮
                ImageView ivPlay = contentView.findViewById(R.id.iv_play);
                LocalMedia media = images.get(position);
                if (media != null) {
                    final String path;
                    if (media.isCut() && !media.isCompressed()) {
                        // 裁剪过
                        path = media.getCutPath();
                    } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                        // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                        path = media.getCompressPath();
                    } else {
                        path = media.getPath();
                    }
                    boolean isHttp = PictureMimeType.isHttp(path);
                    String mimeType = isHttp ? PictureMimeType.getImageMimeType(media.getPath()) : media.getMimeType();
                    boolean eqVideo = PictureMimeType.eqVideo(mimeType);
                    ivPlay.setVisibility(eqVideo ? View.VISIBLE : View.GONE);
                    boolean isGif = PictureMimeType.isGif(mimeType);
                    final boolean eqLongImg = MediaUtils.isLongImg(media);
                    imageView.setVisibility(eqLongImg && !isGif ? View.GONE : View.VISIBLE);
                    longImageView.setVisibility(eqLongImg && !isGif ? View.VISIBLE : View.GONE);
                    // 压缩过的gif就不是gif了
                    if (isGif && !media.isCompressed()) {
                        if (config != null && config.imageEngine != null) {
                            config.imageEngine.loadAsGifImage
                                    (getContext(), path, imageView);
                        }
                    } else {
                        if (config != null && config.imageEngine != null) {
                            if (isHttp) {
                                // 网络图片
                                config.imageEngine.loadImage(contentView.getContext(), path,
                                        imageView, longImageView, new ImageCompleteCallback() {
                                            @Override
                                            public void onShowLoading() {
                                                showPleaseDialog();
                                            }

                                            @Override
                                            public void onHideLoading() {
                                                dismissDialog();
                                            }
                                        });
                            } else {
                                if (eqLongImg) {
                                    displayLongPic(isAndroidQ
                                            ? Uri.parse(path) : Uri.fromFile(new File(path)), longImageView);
                                } else {
                                    config.imageEngine.loadImage(contentView.getContext(), path, imageView);
                                }
                            }
                        }
                    }
                    imageView.setOnViewTapListener((view, x, y) -> {
                        finish();
                        exitAnimation();
                    });
                    longImageView.setOnClickListener(v -> {
                        finish();
                        exitAnimation();
                    });
                    if (!eqVideo) {
                        longImageView.setOnLongClickListener(v -> {
                            if (config.isNotPreviewDownload) {
                                if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    downloadPath = path;
                                    String currentMimeType = PictureMimeType.isHttp(path) ? PictureMimeType.getImageMimeType(media.getPath()) : media.getMimeType();
                                    mMimeType = PictureMimeType.isJPG(currentMimeType) ? PictureMimeType.MIME_TYPE_JPEG : currentMimeType;
                                    showDownLoadDialog();
                                } else {
                                    PermissionChecker.requestPermissions(PictureExternalPreviewActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
                                }
                            }
                            return true;
                        });
                    }
                    if (!eqVideo) {
                        imageView.setOnLongClickListener(v -> {
                            if (config.isNotPreviewDownload) {
                                if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    downloadPath = path;
                                    String currentMimeType = PictureMimeType.isHttp(path) ? PictureMimeType.getImageMimeType(media.getPath()) : media.getMimeType();
                                    mMimeType = PictureMimeType.isJPG(currentMimeType) ? PictureMimeType.MIME_TYPE_JPEG : currentMimeType;
                                    showDownLoadDialog();
                                } else {
                                    PermissionChecker.requestPermissions(PictureExternalPreviewActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
                                }
                            }
                            return true;
                        });
                    }
                    ivPlay.setOnClickListener(v -> {
                        if (config.customVideoPlayCallback != null) {
                            config.customVideoPlayCallback.startPlayVideo(media);
                        } else {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString(PictureConfig.EXTRA_VIDEO_PATH, path);
                            intent.putExtras(bundle);
                            JumpUtils.startPictureVideoPlayActivity(container.getContext(), bundle, PictureConfig.PREVIEW_VIDEO_CODE);
                        }
                    });
                }
                mCacheView.put(position, contentView);
            }
            (container).addView(contentView, 0);
            return contentView;
        }
    }

    /**
     * 加载长图
     *
     * @param uri
     * @param longImg
     */
    private void displayLongPic(Uri uri, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.uri(uri), new ImageViewState(0, new PointF(0, 0), 0));
    }

    /**
     * 下载图片提示
     */
    private void showDownLoadDialog() {
        if (!isFinishing() && !TextUtils.isEmpty(downloadPath)) {
            final PictureCustomDialog dialog =
                    new PictureCustomDialog(getContext(), R.layout.picture_wind_base_dialog);
            Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
            Button btn_commit = dialog.findViewById(R.id.btn_commit);
            TextView tv_title = dialog.findViewById(R.id.tv_title);
            TextView tv_content = dialog.findViewById(R.id.tv_content);
            tv_title.setText(getString(R.string.picture_prompt));
            tv_content.setText(getString(R.string.picture_prompt_content));
            btn_cancel.setOnClickListener(v -> {
                if (!isFinishing()) {
                    dialog.dismiss();
                }
            });
            btn_commit.setOnClickListener(view -> {
                boolean isHttp = PictureMimeType.isHttp(downloadPath);
                showPleaseDialog();
                if (isHttp) {
                    mLoadDataThread = new LoadDataThread(downloadPath);
                    mLoadDataThread.start();
                } else {
                    // 有可能本地图片
                    try {
                        if (isAndroidQ) {
                            savePictureAlbumAndroidQ(downloadPath.startsWith("content://") ? Uri.parse(downloadPath) : Uri.fromFile(new File(downloadPath)));
                        } else {
                            // 把文件插入到系统图库
                            savePictureAlbum();
                        }
                    } catch (Exception e) {
                        ToastUtils.s(getContext(), getString(R.string.picture_save_error) + "\n" + e.getMessage());
                        dismissDialog();
                        e.printStackTrace();
                    }
                }
                if (!isFinishing()) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * 保存相片至本地相册
     *
     * @throws Exception
     */
    private void savePictureAlbum() throws Exception {
        String suffix = PictureMimeType.getLastImgSuffix(mMimeType);
        String state = Environment.getExternalStorageState();
        File rootDir = state.equals(Environment.MEDIA_MOUNTED)
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                : getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (rootDir != null && !rootDir.exists() && rootDir.mkdirs()) {
        }
        File folderDir = new File(isAndroidQ || !state.equals(Environment.MEDIA_MOUNTED)
                ? rootDir.getAbsolutePath() : rootDir.getAbsolutePath() + File.separator + "Camera" + File.separator);
        if (folderDir != null && !folderDir.exists() && folderDir.mkdirs()) {
        }
        String fileName = DateUtils.getCreateFileName("IMG_") + suffix;
        File file = new File(folderDir, fileName);
        PictureFileUtils.copyFile(downloadPath, file.getAbsolutePath());
        Message message = mHandler.obtainMessage();
        message.what = SAVE_IMAGE_SUCCESSFUL;
        message.obj = file.getAbsolutePath();
        mHandler.sendMessage(message);
    }

    /**
     * 保存图片到picture 目录，Android Q适配，最简单的做法就是保存到公共目录，不用SAF存储
     *
     * @param inputUri
     */
    private void savePictureAlbumAndroidQ(Uri inputUri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, ValueOf.toString(System.currentTimeMillis()));
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mMimeType);
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, PictureMimeType.DCIM);
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri == null) {
            mHandler.sendEmptyMessage(SAVE_IMAGE_ERROR);
            return;
        }
        AsyncTask.SERIAL_EXECUTOR.execute(() -> {
            OutputStream outputStream = null;
            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                outputStream = getContentResolver().openOutputStream(uri);
                parcelFileDescriptor = getContentResolver().openFileDescriptor(inputUri, "r");
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                opts.inSampleSize = 2;
                opts.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor(), null, opts);
                if (bitmap != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N && PictureMimeType.isJPEG(mMimeType)) {
                        ExifInterface exifInterface = new ExifInterface(parcelFileDescriptor.getFileDescriptor());
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                        int rotationAngle = BitmapUtils.getRotationAngle(orientation);
                        bitmap = BitmapUtils.rotatingImage(bitmap, rotationAngle);
                    }
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.close();
                        String path = PictureFileUtils.getPath(getContext(), uri);
                        Message message = mHandler.obtainMessage();
                        message.what = SAVE_IMAGE_SUCCESSFUL;
                        message.obj = path;
                        mHandler.sendMessage(message);
                        bitmap.recycle();
                    }
                } else {
                    mHandler.sendEmptyMessage(SAVE_IMAGE_ERROR);
                }
            } catch (Exception e) {
                mHandler.sendEmptyMessage(SAVE_IMAGE_ERROR);
                e.printStackTrace();
            } finally {
                PictureFileUtils.close(parcelFileDescriptor);
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public class LoadDataThread extends Thread {
        private String path;

        public LoadDataThread(String path) {
            super();
            this.path = path;
        }

        @Override
        public void run() {
            try {
                showLoadingImage(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 针对Q版本创建uri
     *
     * @return
     */
    private Uri createOutImageUri() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, ValueOf.toString(System.currentTimeMillis()));
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mMimeType);
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, PictureMimeType.DCIM);

        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    // 下载图片保存至手机
    public void showLoadingImage(String urlPath) {
        BufferedOutputStream bout = null;
        Uri outImageUri = null;
        try {
            URL u = new URL(urlPath);
            String path;
            if (isAndroidQ) {
                outImageUri = createOutImageUri();
                if (outImageUri == null) {
                    mHandler.sendEmptyMessage(SAVE_IMAGE_ERROR);
                    return;
                }
                bout = new BufferedOutputStream(getContentResolver().openOutputStream(outImageUri));
                path = PictureFileUtils.getPath(this, outImageUri);
            } else {
                String suffix = PictureMimeType.getLastImgSuffix(mMimeType);
                String state = Environment.getExternalStorageState();
                File rootDir =
                        state.equals(Environment.MEDIA_MOUNTED)
                                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                                : getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (rootDir != null && !rootDir.exists() && rootDir.mkdirs()) {
                }
                File folderDir = new File(!state.equals(Environment.MEDIA_MOUNTED)
                        ? rootDir.getAbsolutePath() : rootDir.getAbsolutePath() + File.separator + "Camera" + File.separator);
                if (folderDir != null && !folderDir.exists() && folderDir.mkdirs()) {
                }
                String fileName = DateUtils.getCreateFileName("IMG_") + suffix;
                File file = new File(folderDir, fileName);
                path = file.getAbsolutePath();
                bout = new BufferedOutputStream(new FileOutputStream(path));
            }
            if (bout == null) {
                mHandler.sendEmptyMessage(SAVE_IMAGE_ERROR);
                return;
            }
            byte[] buffer = new byte[1024 * 8];
            int read;
            int ava = 0;
            long start = System.currentTimeMillis();
            BufferedInputStream bin = new BufferedInputStream(u.openStream());
            while ((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
                ava += read;
                long speed = ava / (System.currentTimeMillis() - start);
            }
            bout.flush();
            Message message = mHandler.obtainMessage();
            message.what = SAVE_IMAGE_SUCCESSFUL;
            message.obj = path;
            mHandler.sendMessage(message);
        } catch (IOException e) {
            mHandler.sendEmptyMessage(SAVE_IMAGE_ERROR);
            if (outImageUri != null) {
                getContentResolver().delete(outImageUri, null, null);
            }
            e.printStackTrace();
        } finally {
            try {
                if (bout != null) {
                    bout.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SAVE_IMAGE_SUCCESSFUL:
                    try {
                        String path = (String) msg.obj;
                        if (!TextUtils.isEmpty(path)) {
                            if (!SdkVersionUtils.checkedAndroid_Q()) {
                                File file = new File(path);
                                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), null);
                                new PictureMediaScannerConnection(getContext(), file.getAbsolutePath(), () -> {
                                });
                            }
                            ToastUtils.s(getContext(), getString(R.string.picture_save_success) + "\n" + path);
                        }
                        dismissDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SAVE_IMAGE_ERROR:
                    ToastUtils.s(getContext(), getString(R.string.picture_save_error));
                    dismissDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        exitAnimation();
    }

    private void exitAnimation() {
        overridePendingTransition(R.anim.picture_anim_fade_in, config.windowAnimationStyle != null
                && config.windowAnimationStyle.activityPreviewExitAnimation != 0
                ? config.windowAnimationStyle.activityPreviewExitAnimation : R.anim.picture_anim_exit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadDataThread != null) {
            mHandler.removeCallbacks(mLoadDataThread);
            mLoadDataThread = null;
        }
        if (adapter != null) {
            adapter.clear();
        }

        if (config.customVideoPlayCallback != null) {
            config.customVideoPlayCallback = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE:
                // 存储权限
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        showDownLoadDialog();
                    } else {
                        ToastUtils.s(getContext(), getString(R.string.picture_jurisdiction));
                    }
                }
                break;
        }
    }
}
