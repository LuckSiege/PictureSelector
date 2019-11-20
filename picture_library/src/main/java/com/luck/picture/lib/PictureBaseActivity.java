package com.luck.picture.lib;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.broadcast.BroadcastAction;
import com.luck.picture.lib.broadcast.BroadcastManager;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.compress.OnCompressListener;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.AndroidQTransformUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;
import com.yalantis.ucrop.model.CutInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: Activity基类
 */
public class PictureBaseActivity extends AppCompatActivity implements Handler.Callback {
    private static final int MSG_CHOOSE_RESULT_SUCCESS = 200;
    private static final int MSG_ASY_COMPRESSION_RESULT_SUCCESS = 300;
    protected Context mContext;
    protected PictureSelectionConfig config;
    protected boolean openWhiteStatusBar, numComplete;
    protected int colorPrimary, colorPrimaryDark;
    protected String cameraPath, outputCameraPath;
    protected String originalPath;
    protected PictureDialog dialog;
    protected PictureDialog compressDialog;
    protected List<LocalMedia> selectionMedias;
    protected Handler mHandler;

    /**
     * 是否使用沉浸式，子类复写该方法来确定是否采用沉浸式
     *
     * @return 是否沉浸式，默认true
     */
    @Override
    public boolean isImmersive() {
        return true;
    }

    /**
     * 具体沉浸的样式，可以根据需要自行修改状态栏和导航栏的颜色
     */
    public void immersive() {
        ImmersiveManage.immersiveAboveAPI23(this
                , colorPrimaryDark
                , colorPrimary
                , openWhiteStatusBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            config = savedInstanceState.getParcelable(PictureConfig.EXTRA_CONFIG);
            cameraPath = savedInstanceState.getString(PictureConfig.BUNDLE_CAMERA_PATH);
            originalPath = savedInstanceState.getString(PictureConfig.BUNDLE_ORIGINAL_PATH);
        } else {
            config = PictureSelectionConfig.getInstance();
        }
        int themeStyleId = config.themeStyleId;
        setTheme(themeStyleId);
        super.onCreate(savedInstanceState);
        mContext = this;
        mHandler = new Handler(Looper.getMainLooper(), this);
        initConfig();
        if (isImmersive()) {
            immersive();
        }
    }

    /**
     * 获取配置参数
     */
    private void initConfig() {
        outputCameraPath = config.outputCameraPath;
        // 是否开启白色状态栏
        openWhiteStatusBar = config.isChangeStatusBarFontColor;
        // 是否是0/9样式
        numComplete = config.isOpenStyleNumComplete;
        // 是否开启数字勾选模式
        config.checkNumMode = config.isOpenStyleCheckNumMode;
        // 标题栏背景色
        colorPrimary = config.titleBarBackgroundColor <= 0 ?
                ContextCompat.getColor(this, R.color.bar_grey)
                : ContextCompat.getColor(this, config.titleBarBackgroundColor);
        // 状态栏背景色
        colorPrimaryDark = config.statusBarColorPrimaryDark <= 0 ?
                ContextCompat.getColor(this, R.color.bar_grey)
                : ContextCompat.getColor(this, config.statusBarColorPrimaryDark);
        // 已选图片列表
        selectionMedias = config.selectionMedias;
        if (selectionMedias == null) {
            selectionMedias = new ArrayList<>();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PictureConfig.BUNDLE_CAMERA_PATH, cameraPath);
        outState.putString(PictureConfig.BUNDLE_ORIGINAL_PATH, originalPath);
        outState.putParcelable(PictureConfig.EXTRA_CONFIG, config);
    }

    protected void startActivity(Class clz, Bundle bundle) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(this, clz);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    protected void startActivity(Class clz, Bundle bundle, int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(this, clz);
            intent.putExtras(bundle);
            startActivityForResult(intent, requestCode);
        }
    }

    /**
     * loading dialog
     */
    protected void showPleaseDialog() {
        if (!isFinishing()) {
            dismissDialog();
            dialog = new PictureDialog(this);
            dialog.show();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * compress loading dialog
     */
    protected void showCompressDialog() {
        if (!isFinishing()) {
            dismissCompressDialog();
            compressDialog = new PictureDialog(this);
            compressDialog.show();
        }
    }

    /**
     * dismiss compress dialog
     */
    protected void dismissCompressDialog() {
        try {
            if (!isFinishing()
                    && compressDialog != null
                    && compressDialog.isShowing()) {
                compressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * compressImage
     */
    protected void compressImage(final List<LocalMedia> result) {
        showCompressDialog();
        if (config.synOrAsy) {
            AsyncTask.SERIAL_EXECUTOR.execute(() -> {
                try {
                    List<File> files =
                            Luban.with(mContext)
                                    .loadMediaData(result, config.cameraFileName)
                                    .setTargetDir(config.compressSavePath)
                                    .setCompressQuality(config.compressQuality)
                                    .ignoreBy(config.minimumCompressSize).get();
                    // 线程切换
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ASY_COMPRESSION_RESULT_SUCCESS, new Object[]{result, files}));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            Luban.with(this)
                    .loadMediaData(result, config.cameraFileName)
                    .ignoreBy(config.minimumCompressSize)
                    .setCompressQuality(config.compressQuality)
                    .setTargetDir(config.compressSavePath)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(List<LocalMedia> list) {
                            BroadcastManager.getInstance(getApplicationContext())
                                    .action(BroadcastAction.ACTION_CLOSE_PREVIEW).broadcast();
                            onResult(list);
                        }

                        @Override
                        public void onError(Throwable e) {
                            BroadcastManager.getInstance(getApplicationContext())
                                    .action(BroadcastAction.ACTION_CLOSE_PREVIEW).broadcast();
                            onResult(result);
                        }
                    }).launch();
        }
    }

    /**
     * 重新构造已压缩的图片返回集合
     *
     * @param images
     * @param files
     */
    private void handleCompressCallBack(List<LocalMedia> images, List<File> files) {
        if (images == null || files == null) {
            closeActivity();
            return;
        }
        if (files.size() == images.size()) {
            for (int i = 0, j = images.size(); i < j; i++) {
                // 压缩成功后的地址
                String path = files.get(i).getPath();
                LocalMedia image = images.get(i);
                // 如果是网络图片则不压缩
                boolean http = PictureMimeType.isHttp(path);
                boolean flag = !TextUtils.isEmpty(path) && http;
                image.setCompressed(flag ? false : true);
                image.setCompressPath(flag ? "" : path);
            }
        }
        BroadcastManager.getInstance(getApplicationContext())
                .action(BroadcastAction.ACTION_CLOSE_PREVIEW).broadcast();
        onResult(images);
    }

    /**
     * 去裁剪
     *
     * @param originalPath
     */
    protected void startCrop(String originalPath) {
        UCrop.Options options = new UCrop.Options();
        int toolbarColor = config.cropTitleBarBackgroundColor <= 0 ?
                ContextCompat.getColor(this, R.color.bar_grey) : ContextCompat.getColor(this, config.cropTitleBarBackgroundColor);
        int statusColor = config.cropStatusBarColorPrimaryDark <= 0 ?
                ContextCompat.getColor(this, R.color.bar_grey) : ContextCompat.getColor(this, config.cropStatusBarColorPrimaryDark);
        int titleColor = config.cropTitleColor <= 0 ?
                ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, config.cropTitleColor);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(config.circleDimmedLayer);
        options.setShowCropFrame(config.showCropFrame);
        options.setShowCropGrid(config.showCropGrid);
        options.setDragFrameEnabled(config.isDragFrame);
        options.setScaleEnabled(config.scaleEnabled);
        options.setRotateEnabled(config.rotateEnabled);
        options.setCompressionQuality(config.cropCompressQuality);
        options.setHideBottomControls(config.hideBottomControls);
        options.setFreeStyleCropEnabled(config.freeStyleCropEnabled);
        boolean isHttp = PictureMimeType.isHttp(originalPath);
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        String imgType = isAndroidQ ? PictureMimeType
                .getLastImgSuffix(PictureMimeType.getMimeType(mContext, Uri.parse(originalPath)))
                : PictureMimeType.getLastImgType(originalPath);
        Uri uri = isHttp || isAndroidQ ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        File file = new File(PictureFileUtils.getDiskCacheDir(this),
                TextUtils.isEmpty(config.cameraFileName) ? System.currentTimeMillis() + imgType : config.cameraFileName + imgType);
        UCrop.of(uri, Uri.fromFile(file))
                .withAspectRatio(config.aspect_ratio_x, config.aspect_ratio_y)
                .withMaxResultSize(config.cropWidth, config.cropHeight)
                .withOptions(options)
                .start(this);
    }

    /**
     * 多图去裁剪
     *
     * @param list
     */
    protected void startCrop(ArrayList<String> list) {
        UCropMulti.Options options = new UCropMulti.Options();
        int toolbarColor = config.cropTitleBarBackgroundColor <= 0 ?
                ContextCompat.getColor(this, R.color.bar_grey) : ContextCompat.getColor(this, config.cropTitleBarBackgroundColor);
        int statusColor = config.cropStatusBarColorPrimaryDark <= 0 ?
                ContextCompat.getColor(this, R.color.bar_grey) : ContextCompat.getColor(this, config.cropStatusBarColorPrimaryDark);
        int titleColor = config.cropTitleColor <= 0 ?
                ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, config.cropTitleColor);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(config.circleDimmedLayer);
        options.setShowCropFrame(config.showCropFrame);
        options.setDragFrameEnabled(config.isDragFrame);
        options.setShowCropGrid(config.showCropGrid);
        options.setScaleEnabled(config.scaleEnabled);
        options.setRotateEnabled(config.rotateEnabled);
        options.setHideBottomControls(config.hideBottomControls);
        options.setCompressionQuality(config.cropCompressQuality);
        options.setCutListData(list);
        options.setFreeStyleCropEnabled(config.freeStyleCropEnabled);
        String path = list.size() > 0 ? list.get(0) : "";
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        boolean isHttp = PictureMimeType.isHttp(path);
        String imgType = isAndroidQ ? PictureMimeType
                .getLastImgSuffix(PictureMimeType.getMimeType(mContext, Uri.parse(path)))
                : PictureMimeType.getLastImgType(path);
        Uri uri = isHttp || isAndroidQ ? Uri.parse(path) : Uri.fromFile(new File(path));
        File file = new File(PictureFileUtils.getDiskCacheDir(this),
                TextUtils.isEmpty(config.cameraFileName) ? System.currentTimeMillis() + imgType : config.cameraFileName + imgType);
        UCropMulti.of(uri, Uri.fromFile(file))
                .withAspectRatio(config.aspect_ratio_x, config.aspect_ratio_y)
                .withMaxResultSize(config.cropWidth, config.cropHeight)
                .withOptions(options)
                .start(this);
    }


    /**
     * 判断拍照 图片是否旋转
     *
     * @param degree
     * @param file
     */
    protected void rotateImage(int degree, File file) {
        if (degree > 0) {
            // 针对相片有旋转问题的处理方式
            try {
                //获取缩略图显示到屏幕上
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                Bitmap bmp = PictureFileUtils.rotaingImageView(degree, bitmap);
                PictureFileUtils.saveBitmapFile(bmp, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * compress or callback
     *
     * @param result
     */
    protected void handlerResult(List<LocalMedia> result) {
        if (config.isCompress) {
            compressImage(result);
        } else {
            onResult(result);
        }
    }


    /**
     * 如果没有任何相册，先创建一个最近相册出来
     *
     * @param folders
     */
    protected void createNewFolder(List<LocalMediaFolder> folders) {
        if (folders.size() == 0) {
            // 没有相册 先创建一个最近相册出来
            LocalMediaFolder newFolder = new LocalMediaFolder();
            String folderName = config.chooseMode == PictureMimeType.ofAudio() ?
                    getString(R.string.picture_all_audio) : getString(R.string.picture_camera_roll);
            newFolder.setName(folderName);
            newFolder.setPath("");
            newFolder.setFirstImagePath("");
            folders.add(newFolder);
        }
    }

    /**
     * 将图片插入到相机文件夹中
     *
     * @param path
     * @param imageFolders
     * @return
     */
    protected LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    /**
     * return image result
     *
     * @param images
     */
    protected void onResult(List<LocalMedia> images) {
        boolean androidQ = SdkVersionUtils.checkedAndroid_Q();
        boolean isVideo = PictureMimeType.eqVideo(images != null && images.size() > 0
                ? images.get(0).getMimeType() : "");
        if (androidQ && !isVideo) {
            showCompressDialog();
        }
        if (androidQ) {
            onResultToAndroidAsy(images);
        } else {
            dismissCompressDialog();
            if (config.camera
                    && config.selectionMode == PictureConfig.MULTIPLE
                    && selectionMedias != null) {
                images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
            }
            Intent intent = PictureSelector.putIntentResult(images);
            setResult(RESULT_OK, intent);
            closeActivity();
        }
    }

    /**
     * 针对Android 异步处理
     *
     * @param images
     */
    private void onResultToAndroidAsy(List<LocalMedia> images) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> {
            // Android Q 版本做拷贝应用内沙盒适配
            int size = images.size();
            for (int i = 0; i < size; i++) {
                LocalMedia media = images.get(i);
                if (media == null || TextUtils.isEmpty(media.getPath())) {
                    continue;
                }
                if (media.isCompressed()) {
                    media.setAndroidQToPath(media.getCompressPath());
                } else if (media.isCut()) {
                    media.setAndroidQToPath(media.getCutPath());
                } else {
                    String path;
                    if (PictureMimeType.eqVideo(media.getMimeType())) {
                        path = AndroidQTransformUtils.parseVideoPathToAndroidQ
                                (getApplicationContext(), media.getPath(), config.cameraFileName, media.getMimeType());
                    } else if (config.chooseMode == PictureMimeType.ofAudio()) {
                        path = AndroidQTransformUtils.parseAudioPathToAndroidQ
                                (getApplicationContext(), media.getPath(), config.cameraFileName, media.getMimeType());
                    } else {
                        path = AndroidQTransformUtils.parseImagePathToAndroidQ
                                (getApplicationContext(), media.getPath(), config.cameraFileName, media.getMimeType());
                    }
                    media.setAndroidQToPath(path);
                }
            }
            // 线程切换
            mHandler.sendMessage(mHandler.obtainMessage(MSG_CHOOSE_RESULT_SUCCESS, images));
        });
    }

    /**
     * Close Activity
     */
    protected void closeActivity() {
        finish();
        if (config.camera) {
            overridePendingTransition(0, R.anim.fade_out);
        } else {
            overridePendingTransition(0, R.anim.a3);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissCompressDialog();
        dismissDialog();
    }


    /**
     * 获取DCIM文件下最新一条拍照记录
     *
     * @return
     */
    protected int getLastImageId(boolean eqVideo) {
        try {
            //selection: 指定查询条件
            String absolutePath = PictureFileUtils.getDCIMCameraPath(this);
            String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
            String selection = eqVideo ? MediaStore.Video.Media.DATA + " like ?" :
                    MediaStore.Images.Media.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {absolutePath + "%"};
            Cursor imageCursor = this.getContentResolver().query(eqVideo ?
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    selection, selectionArgs, ORDER_BY);
            if (imageCursor.moveToFirst()) {
                int id = imageCursor.getInt(eqVideo ?
                        imageCursor.getColumnIndex(MediaStore.Video.Media._ID)
                        : imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
                long date = imageCursor.getLong(eqVideo ?
                        imageCursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                        : imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                int duration = DateUtils.dateDiffer(date);
                imageCursor.close();
                // DCIM文件下最近时间30s以内的图片，可以判定是最新生成的重复照片
                return duration <= 30 ? id : -1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 删除部分手机 拍照在DCIM也生成一张的问题
     *
     * @param id
     * @param eqVideo
     */
    protected void removeImage(int id, boolean eqVideo) {
        try {
            ContentResolver cr = getContentResolver();
            Uri uri = eqVideo ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String selection = eqVideo ? MediaStore.Video.Media._ID + "=?"
                    : MediaStore.Images.Media._ID + "=?";
            cr.delete(uri,
                    selection,
                    new String[]{Long.toString(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 录音
     *
     * @param data
     */
    protected String getAudioPath(Intent data) {
        boolean compare_SDK_19 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
        if (data != null && config.chooseMode == PictureMimeType.ofAudio()) {
            try {
                Uri uri = data.getData();
                final String audioPath;
                if (compare_SDK_19) {
                    audioPath = uri.getPath();
                } else {
                    audioPath = getAudioFilePathFromUri(uri);
                }
                return audioPath;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取刚录取的音频文件
     *
     * @param uri
     * @return
     */
    protected String getAudioFilePathFromUri(Uri uri) {
        String path = "";
        try {
            Cursor cursor = getContentResolver()
                    .query(uri, null, null, null, null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
            path = cursor.getString(index);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }


    /**
     * start to camera、preview、crop
     */
    protected void startOpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            Uri imageUri;
            if (SdkVersionUtils.checkedAndroid_Q()) {
                imageUri = MediaUtils.createImagePathUri(getApplicationContext(), config.cameraFileName);
                cameraPath = imageUri.toString();
            } else {
                int type = config.chooseMode == PictureConfig.TYPE_ALL ? PictureConfig.TYPE_IMAGE
                        : config.chooseMode;
                File cameraFile = PictureFileUtils.createCameraFile(getApplicationContext(),
                        type, config.cameraFileName, config.suffixType);
                cameraPath = cameraFile.getAbsolutePath();
                imageUri = PictureFileUtils.parUri(this, cameraFile);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }


    /**
     * start to camera、video
     */
    protected void startOpenCameraVideo() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            Uri imageUri;
            if (SdkVersionUtils.checkedAndroid_Q()) {
                imageUri = MediaUtils.createImageVideoUri(getApplicationContext(), config.cameraFileName);
                cameraPath = imageUri.toString();
            } else {
                File cameraFile = PictureFileUtils.createCameraFile(getApplicationContext(), config.chooseMode ==
                                PictureConfig.TYPE_ALL ? PictureConfig.TYPE_VIDEO : config.chooseMode, config.cameraFileName,
                        config.suffixType);
                cameraPath = cameraFile.getAbsolutePath();
                imageUri = PictureFileUtils.parUri(this, cameraFile);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, config.recordVideoSecond);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, config.videoQuality);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }

    /**
     * start to camera audio
     */
    public void startOpenCameraAudio() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
            }
        } else {
            PermissionChecker.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PictureConfig.APPLY_AUDIO_PERMISSIONS_CODE);
        }
    }

    /**
     * 多张图片裁剪
     *
     * @param data
     */
    protected void multiCropHandleResult(Intent data) {
        List<LocalMedia> medias = new ArrayList<>();
        List<CutInfo> mCuts = UCropMulti.getOutput(data);
        for (CutInfo c : mCuts) {
            LocalMedia media = new LocalMedia();
            String imageType = PictureMimeType.getImageMimeType(c.getCutPath());
            media.setCut(TextUtils.isEmpty(c.getCutPath()) ? false : true);
            media.setPath(c.getPath());
            media.setCutPath(c.getCutPath());
            media.setMimeType(imageType);
            media.setWidth(c.getImageWidth());
            media.setHeight(c.getImageHeight());
            media.setSize(new File(TextUtils.isEmpty(c.getCutPath())
                    ? c.getPath() : c.getCutPath()).length());
            if (SdkVersionUtils.checkedAndroid_Q()) {
                media.setAndroidQToPath(c.getCutPath());
            }
            media.setChooseModel(config.chooseMode);
            medias.add(media);
        }
        handlerResult(medias);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_CHOOSE_RESULT_SUCCESS:
                // 选择完成回调
                List<LocalMedia> images = (List<LocalMedia>) msg.obj;
                dismissCompressDialog();
                if (images != null) {
                    if (config.camera
                            && config.selectionMode == PictureConfig.MULTIPLE
                            && selectionMedias != null) {
                        images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
                    }
                    Intent intent = PictureSelector.putIntentResult(images);
                    setResult(RESULT_OK, intent);
                    closeActivity();
                }
                break;
            case MSG_ASY_COMPRESSION_RESULT_SUCCESS:
                // 异步压缩回调
                if (msg.obj != null && msg.obj instanceof Object[]) {
                    Object[] objects = (Object[]) msg.obj;
                    if (objects.length > 0) {
                        List<LocalMedia> result = (List<LocalMedia>) objects[0];
                        List<File> files = (List<File>) objects[1];
                        handleCompressCallBack(result, files);
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.APPLY_AUDIO_PERMISSIONS_CODE:
                // 录音权限
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                    }
                } else {
                    ToastUtils.s(mContext, getString(R.string.picture_audio));
                }
                break;
        }
    }
}
