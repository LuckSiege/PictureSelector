package com.luck.picture.lib.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.compress.CompressConfig;
import com.luck.picture.lib.compress.CompressImageOptions;
import com.luck.picture.lib.compress.CompressInterface;
import com.luck.picture.lib.compress.LubanOptions;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.luck.picture.lib.observable.ImagesObservable;
import com.yalantis.ucrop.MultiUCrop;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.dialog.SweetAlertDialog;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.entity.LocalMediaFolder;
import com.yalantis.ucrop.util.FileUtils;
import com.yalantis.ucrop.util.ScreenUtils;
import com.yalantis.ucrop.util.ToolbarUtil;
import com.yalantis.ucrop.util.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */
public class PictureImageGridActivity extends PictureBaseActivity implements View.OnClickListener, PictureImageGridAdapter.OnPhotoSelectChangedListener {
    public final String TAG = PictureImageGridActivity.class.getSimpleName();
    private List<LocalMedia> images = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView tv_img_num;
    private TextView tv_ok;
    private RelativeLayout rl_bottom;
    private ImageButton picture_left_back;
    private RelativeLayout rl_picture_title;
    private TextView picture_tv_title, picture_tv_right;

    private Button id_preview;
    private PictureImageGridAdapter adapter;
    private String cameraPath;
    private SweetAlertDialog dialog;
    private List<LocalMediaFolder> folders = new ArrayList<>();
    private boolean is_top_activity;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("app.activity.finish")) {
                finish();
                overridePendingTransition(0, R.anim.slide_bottom_out);
            } else if (action.equals("app.action.refresh.data")) {
                List<LocalMedia> selectImages = (List<LocalMedia>) intent.getSerializableExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST);
                if (selectImages != null)
                    adapter.bindSelectImages(selectImages);
            } else if (action.equals("app.action.crop_data")) {
                // 裁剪返回的数据
                List<LocalMedia> result = (List<LocalMedia>) intent.getSerializableExtra(FunctionConfig.EXTRA_RESULT);
                if (result == null)
                    result = new ArrayList<>();
                handleCropResult(result);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_image_grid);
        registerReceiver(receiver, "app.activity.finish", "app.action.refresh.data", "app.action.crop_data");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        picture_left_back = (ImageButton) findViewById(R.id.picture_left_back);
        rl_picture_title = (RelativeLayout) findViewById(R.id.rl_picture_title);
        picture_tv_title = (TextView) findViewById(R.id.picture_tv_title);
        picture_tv_right = (TextView) findViewById(R.id.picture_tv_right);
        rl_picture_title.setBackgroundColor(backgroundColor);
        ToolbarUtil.setColorNoTranslucent(this, backgroundColor);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        id_preview = (Button) findViewById(R.id.id_preview);
        tv_img_num = (TextView) findViewById(R.id.tv_img_num);
        id_preview.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        picture_left_back.setOnClickListener(this);
        picture_tv_right.setOnClickListener(this);
        is_top_activity = getIntent().getBooleanExtra(FunctionConfig.EXTRA_IS_TOP_ACTIVITY, false);
        if (!is_top_activity) {
            // 第一次启动ImageActivity，没有获取过相册列表
            // 先判断手机是否有读取权限，主要是针对6.0已上系统
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                readLocalMedia();
            } else {
                requestPermission(FunctionConfig.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            selectMedias = (List<LocalMedia>) getIntent().getSerializableExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST);
        }
        String folderName = getIntent().getStringExtra(FunctionConfig.FOLDER_NAME);
        folders = ImagesObservable.getInstance().readLocalFolders();
        if (folders == null) {
            folders = new ArrayList<>();
        }

        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(FunctionConfig.BUNDLE_CAMERA_PATH);
        }

        images = ImagesObservable.getInstance().readLocalMedias();
        if (images == null) {
            images = new ArrayList<>();
        }

        if (selectMedias == null) {
            selectMedias = new ArrayList<>();
        }
        if (enablePreview && selectMode == FunctionConfig.MODE_MULTIPLE) {
            if (type == LocalMediaLoader.TYPE_VIDEO) {
                // 如果是视频不能预览
                id_preview.setVisibility(View.GONE);
            } else {
                id_preview.setVisibility(View.VISIBLE);
            }
        } else if (selectMode == FunctionConfig.MODE_SINGLE) {
            rl_bottom.setVisibility(View.GONE);
        } else {
            id_preview.setVisibility(View.GONE);
        }
        if (folderName != null && !folderName.equals("")) {
            picture_tv_title.setText(folderName);
        } else {
            switch (type) {
                case LocalMediaLoader.TYPE_IMAGE:
                    picture_tv_title.setText(getString(R.string.lately_image));
                    break;
                case LocalMediaLoader.TYPE_VIDEO:
                    picture_tv_title.setText(getString(R.string.lately_video));
                    break;
            }
        }
        rl_bottom.setBackgroundColor(bottomBgColor);
        id_preview.setTextColor(previewColor);
        tv_ok.setTextColor(completeColor);
        picture_tv_right.setText(getString(R.string.cancel));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, ScreenUtils.dip2px(this, 2), false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        // 如果是显示数据风格，则默认为qq选择风格
        if (is_checked_num) {
            tv_img_num.setBackgroundResource(R.drawable.message_oval_blue);
        }
        String titleText = picture_tv_title.getText().toString().trim();
        if (showCamera) {
            if (!Utils.isNull(titleText) && titleText.startsWith("最近") || titleText.startsWith("Recent")) {
                // 只有最近相册 才显示拍摄按钮，不然相片混乱
                showCamera = true;
            } else {
                showCamera = false;
            }
        }
        adapter = new PictureImageGridAdapter(this, showCamera, maxSelectNum, selectMode, enablePreview, enablePreviewVideo, cb_drawable, is_checked_num);
        recyclerView.setAdapter(adapter);
        if (selectMedias.size() > 0) {
            ChangeImageNumber(selectMedias);
            adapter.bindSelectImages(selectMedias);
        }
        adapter.bindImagesData(images);
        adapter.setOnPhotoSelectChangedListener(PictureImageGridActivity.this);
    }

    @Override
    protected void readLocalMedia() {
        /**
         * 根据type决定，查询本地图片或视频。
         */
        showDialog("请稍候...");
        new LocalMediaLoader(this, type).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {

            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                dismiss();
                if (folders.size() > 0) {
                    // 取最近相册或视频数据
                    LocalMediaFolder folder = folders.get(0);
                    images = folder.getImages();
                    adapter.bindImagesData(images);
                    PictureImageGridActivity.this.folders = folders;
                    ImagesObservable.getInstance().saveLocalFolders(folders);
                    ImagesObservable.getInstance().notifyFolderObserver(folders);
                }
            }
        });

    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        int id = view.getId();
        if (id == R.id.picture_left_back) {
            activityFinish(1);
        } else if (id == R.id.picture_tv_right) {
            activityFinish(2);
        } else if (id == R.id.id_preview) {
            if (Utils.isFastDoubleClick()) {
                return;
            }
            List<LocalMedia> selectedImages = adapter.getSelectedImages();
            List<LocalMedia> medias = new ArrayList<>();
            for (LocalMedia media : selectedImages) {
                medias.add(media);
            }
            intent.putExtra(FunctionConfig.EXTRA_PREVIEW_LIST, (Serializable) medias);
            intent.putExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectedImages);
            intent.putExtra(FunctionConfig.EXTRA_POSITION, 0);
            intent.putExtra(FunctionConfig.EXTRA_BOTTOM_PREVIEW, true);
            intent.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
            intent.setClass(mContext, PicturePreviewActivity.class);
            startActivityForResult(intent, FunctionConfig.REQUEST_PREVIEW);
        } else if (id == R.id.tv_ok) {
            List<LocalMedia> images = adapter.getSelectedImages();
            if (enableCrop && type == LocalMediaLoader.TYPE_IMAGE && selectMode == FunctionConfig.MODE_MULTIPLE) {
                // 是图片和选择压缩并且是多张，调用批量压缩
                startMultiCopy(images);
            } else {
                // 图片才压缩，视频不管
                if (isCompress && type == LocalMediaLoader.TYPE_IMAGE) {
                    compressImage(images);
                } else {
                    resultBack(images);
                }
            }
        }
    }

    private void resultBack(List<LocalMedia> images) {
        onResult(images);
    }

    @Override
    public void onTakePhoto() {
        // 启动相机拍照,先判断手机是否有拍照权限
        if (hasPermission(Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            requestPermission(FunctionConfig.CAMERA, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onChange(List<LocalMedia> selectImages) {
        ChangeImageNumber(selectImages);
    }

    /**
     * 图片选中数量
     *
     * @param selectImages
     */
    public void ChangeImageNumber(List<LocalMedia> selectImages) {
        Animation animation = null;
        boolean enable = selectImages.size() != 0;
        if (enable) {
            id_preview.setAlpha(1.0f);
            tv_ok.setEnabled(true);
            tv_ok.setAlpha(1.0f);
            id_preview.setEnabled(true);
            animation = AnimationUtils.loadAnimation(mContext, R.anim.modal_in);
            tv_img_num.startAnimation(animation);
            tv_img_num.setVisibility(View.VISIBLE);
            tv_img_num.setText(selectImages.size() + "");
            tv_ok.setText("已完成");
        } else {
            tv_ok.setEnabled(false);
            id_preview.setAlpha(0.5f);
            id_preview.setEnabled(false);
            tv_ok.setAlpha(0.5f);
            if (selectImages.size() > 0) {
                animation = AnimationUtils.loadAnimation(mContext, R.anim.modal_out);
                tv_img_num.startAnimation(animation);
            }
            tv_img_num.setVisibility(View.INVISIBLE);
            tv_ok.setText("请选择");
        }
    }

    @Override
    public void startCamera() {
        switch (type) {
            case LocalMediaLoader.TYPE_IMAGE:
                // 拍照
                startOpenCamera();
                break;
            case LocalMediaLoader.TYPE_VIDEO:
                // 录视频
                startOpenCameraVideo();
                break;
        }

    }

    @Override
    public void onPictureClick(LocalMedia media, int position) {
        startPreview(adapter.getImages(), position);
    }

    public void startPreview(List<LocalMedia> previewImages, int position) {
        LocalMedia media = previewImages.get(position);
        int type = media.getType();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (type) {
            case LocalMediaLoader.TYPE_IMAGE:
                if (enableCrop && selectMode == FunctionConfig.MODE_SINGLE) {
                    startCopy(media.getPath());
                } else if (!enableCrop && selectMode == FunctionConfig.MODE_SINGLE) {
                    ArrayList<LocalMedia> result = new ArrayList<>();
                    LocalMedia m = new LocalMedia();
                    m.setPath(media.getPath());
                    m.setType(type);
                    result.add(m);
                    if (isCompress) {
                        compressImage(result);
                    } else {
                        onSelectDone(result);
                    }
                } else {
                    // 图片可以预览
                    if (Utils.isFastDoubleClick()) {
                        return;
                    }
                    ImagesObservable.getInstance().saveLocalMedia(previewImages);
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    intent.putExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectedImages);
                    intent.putExtra(FunctionConfig.EXTRA_POSITION, position);
                    intent.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
                    intent.setClass(mContext, PicturePreviewActivity.class);
                    startActivityForResult(intent, FunctionConfig.REQUEST_PREVIEW);
                }
                break;
            case LocalMediaLoader.TYPE_VIDEO:
                // 视频
                if (selectMode == FunctionConfig.MODE_SINGLE) {
                    // 单选
                    ArrayList<LocalMedia> result = new ArrayList<>();
                    LocalMedia m = new LocalMedia();
                    m.setPath(media.getPath());
                    m.setType(type);
                    result.add(m);
                    onSelectDone(result);
                } else {
                    if (Utils.isFastDoubleClick()) {
                        return;
                    }
                    bundle.putString("video_path", media.getPath());
                    bundle.putSerializable(FunctionConfig.EXTRA_THIS_CONFIG, config);
                    startActivity(PictureVideoPlayActivity.class, bundle);
                }
                break;
        }

    }

    /**
     * 裁剪
     *
     * @param path
     */
    protected void startCopy(String path) {
//        // 如果开启裁剪 并且是单选
//        // 去裁剪
        UCrop uCrop = UCrop.of(Uri.parse(path), Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg")));
        UCrop.Options options = new UCrop.Options();
        switch (copyMode) {
            case FunctionConfig.COPY_MODEL_DEFAULT:
                options.withAspectRatio(0, 0);
                break;
            case FunctionConfig.COPY_MODEL_1_1:
                options.withAspectRatio(1, 1);
                break;
            case FunctionConfig.COPY_MODEL_3_2:
                options.withAspectRatio(3, 2);
                break;
            case FunctionConfig.COPY_MODEL_3_4:
                options.withAspectRatio(3, 4);
                break;
            case FunctionConfig.COPY_MODEL_16_9:
                options.withAspectRatio(16, 9);
                break;
        }

        options.setCompressionQuality(compressQuality);
        options.withMaxResultSize(cropW, cropH);
        options.background_color(backgroundColor);
        options.localType(type);
        options.setIsCompress(isCompress);
        uCrop.withOptions(options);
        uCrop.start(PictureImageGridActivity.this);
    }

    /**
     * 多图裁剪
     *
     * @param medias
     */
    protected void startMultiCopy(List<LocalMedia> medias) {
        if (medias != null && medias.size() > 0) {
            LocalMedia media = medias.get(0);
            String path = media.getPath();
            // 去裁剪
            MultiUCrop uCrop = MultiUCrop.of(Uri.parse(path), Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg")));
            MultiUCrop.Options options = new MultiUCrop.Options();
            switch (copyMode) {
                case FunctionConfig.COPY_MODEL_DEFAULT:
                    options.withAspectRatio(0, 0);
                    break;
                case FunctionConfig.COPY_MODEL_1_1:
                    options.withAspectRatio(1, 1);
                    break;
                case FunctionConfig.COPY_MODEL_3_2:
                    options.withAspectRatio(3, 2);
                    break;
                case FunctionConfig.COPY_MODEL_3_4:
                    options.withAspectRatio(3, 4);
                    break;
                case FunctionConfig.COPY_MODEL_16_9:
                    options.withAspectRatio(16, 9);
                    break;
            }
            options.setLocalMedia(medias);
            options.setCompressionQuality(compressQuality);
            options.withMaxResultSize(cropW, cropH);
            options.background_color(backgroundColor);
            options.setIsCompress(isCompress);
            uCrop.withOptions(options);
            uCrop.start(PictureImageGridActivity.this);
        }

    }

    /**
     * start to camera、preview、crop
     */
    public void startOpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = FileUtils.createCameraFile(this, type);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri;
            String authority = getPackageName() + ".provider";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageUri = FileProvider.getUriForFile(mContext, authority, cameraFile);//通过FileProvider创建一个content类型的Uri
            } else {
                imageUri = Uri.fromFile(cameraFile);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, FunctionConfig.REQUEST_CAMERA);
        }
    }

    /**
     * start to camera、video
     */
    public void startOpenCameraVideo() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = FileUtils.createCameraFile(this, type);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri;
            String authority = getPackageName() + ".provider";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageUri = FileProvider.getUriForFile(mContext, authority, cameraFile);//通过FileProvider创建一个content类型的Uri
            } else {
                imageUri = Uri.fromFile(cameraFile);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordVideoSecond);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, definition);
            startActivityForResult(cameraIntent, FunctionConfig.REQUEST_CAMERA);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // on take photo success
            if (requestCode == FunctionConfig.REQUEST_CAMERA) {
                // 拍照返回
                File file = new File(cameraPath);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                if (selectMode == FunctionConfig.MODE_SINGLE) {
                    // 如果是单选 拍照后直接返回
                    if (enableCrop && type == LocalMediaLoader.TYPE_IMAGE) {
                        // 如果允许裁剪，并且是图片
                        startCopy(cameraPath);
                    } else {
                        if (isCompress && type == LocalMediaLoader.TYPE_IMAGE) {
                            // 压缩图片
                            ArrayList<LocalMedia> compresses = new ArrayList<>();
                            LocalMedia compress = new LocalMedia();
                            compress.setPath(cameraPath);
                            compress.setType(type);
                            compresses.add(compress);
                            compressImage(compresses);
                        } else {
                            ArrayList<LocalMedia> result = new ArrayList<>();
                            LocalMedia m = new LocalMedia();
                            m.setPath(cameraPath);
                            m.setType(type);
                            result.add(m);
                            onSelectDone(result);
                        }
                    }
                } else {
                    // 多选 返回列表并选中当前拍照的
                    int duration = 0;
                    if (type == LocalMediaLoader.TYPE_VIDEO) {
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(file.getPath());
                        duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    } else {
                        duration = Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(0, 10));
                    }
                    createNewFolder(folders);
                    // 生成拍照图片对象
                    LocalMedia media = new LocalMedia(file.getPath(), duration, duration, type);
                    // 根据新拍照生成的图片，插入到对应的相册当中，避免重新查询一遍数据库
                    LocalMediaFolder folder = getImageFolder(media.getPath(), folders);
                    // 更新当前图片所属文件夹
                    folder.getImages().add(0, media);// 插入到第一个位置
                    folder.setImageNum(folder.getImageNum() + 1);
                    folder.setFirstImagePath(media.getPath());
                    folder.setType(type);

                    // 取出最近文件夹 插入刚拍的照片或视频，并且如果大于100张，先移除最后一条在保存，因为最近文件夹中只显示100条数据
                    LocalMediaFolder mediaFolder = folders.get(0);
                    mediaFolder.setFirstImagePath(media.getPath());
                    mediaFolder.setType(type);
                    List<LocalMedia> localMedias = mediaFolder.getImages();
                    if (localMedias.size() >= 100) {
                        localMedias.remove(localMedias.size() - 1);
                    }
                    List<LocalMedia> images = adapter.getImages();
                    images.add(0, media);
                    mediaFolder.setImages(images);
                    mediaFolder.setImageNum(mediaFolder.getImages().size());
                    // 没有到最大选择量 才做默认选中刚拍好的
                    if (adapter.getSelectedImages().size() < maxSelectNum) {
                        List<LocalMedia> selectedImages = adapter.getSelectedImages();
                        selectedImages.add(media);
                        adapter.bindSelectImages(selectedImages);
                        ChangeImageNumber(adapter.getSelectedImages());
                    }
                    adapter.bindImagesData(images);
                }

            }
        }
    }

    /**
     * 如果没有任何相册，先创建一个最近相册出来
     *
     * @param folders
     */
    private void createNewFolder(List<LocalMediaFolder> folders) {
        if (folders.size() == 0) {
            // 没有相册 先创建一个最近相册出来
            LocalMediaFolder newFolder = new LocalMediaFolder();
            String folderName = "";
            switch (type) {
                case LocalMediaLoader.TYPE_IMAGE:
                    folderName = getString(R.string.lately_image);
                    break;
                case LocalMediaLoader.TYPE_VIDEO:
                    folderName = getString(R.string.lately_video);
                    break;
            }
            newFolder.setName(folderName);
            newFolder.setPath("");
            newFolder.setFirstImagePath("");
            newFolder.setType(type);
            folders.add(newFolder);
        }
    }

    private void handleCropResult(List<LocalMedia> result) {
        if (result != null) {
            if (isCompress && type == LocalMediaLoader.TYPE_IMAGE) {
                // 压缩图片
                compressImage(result);
            } else {
                onSelectDone(result);
            }
        }
    }

    public void onSelectDone(List<LocalMedia> result) {
        onResult(result);
    }

    public void onResult(List<LocalMedia> images) {
        // 因为这里是单一实例的结果集，重新用变量接收一下在返回，不然会产生结果集被单一实例清空的问题
        List<LocalMedia> result = new ArrayList<>();
        for (LocalMedia media : images) {
            result.add(media);
        }
        PictureConfig.OnSelectResultCallback resultCallback = PictureConfig.getPictureConfig().getResultCallback();
        if (resultCallback != null) {
            resultCallback.onSelectSuccess(result);
        }
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
        sendBroadcast("app.activity.finish");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FunctionConfig.BUNDLE_CAMERA_PATH, cameraPath);
    }


    private LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                activityFinish(1);
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void activityFinish(int type) {
        switch (type) {
            case 1:
                // 返回
                List<LocalMedia> selectedImages = adapter.getSelectedImages();
                ImagesObservable.getInstance().notifySelectLocalMediaObserver(selectedImages);
                finish();
                break;
            case 2:
                // 取消
                sendBroadcast("app.activity.finish");
                finish();
                overridePendingTransition(0, R.anim.slide_bottom_out);
                break;
        }

    }

    /**
     * 处理图片压缩
     */
    private void compressImage(List<LocalMedia> result) {
        showDialog("处理中...");
        CompressConfig compress_config = CompressConfig.ofDefaultConfig();
        switch (compressFlag) {
            case 1:
                // 系统自带压缩
                compress_config.enablePixelCompress(config.isEnablePixelCompress());
                compress_config.enableQualityCompress(config.isEnableQualityCompress());
                break;
            case 2:
                // luban压缩
                LubanOptions option = new LubanOptions.Builder()
                        .setMaxHeight(compressH)
                        .setMaxWidth(compressW)
                        .setMaxSize(FunctionConfig.MAX_COMPRESS_SIZE)
                        .create();
                compress_config = CompressConfig.ofLuban(option);
                break;
        }

        CompressImageOptions.compress(this, compress_config, result, new CompressInterface.CompressListener() {
            @Override
            public void onCompressSuccess(List<LocalMedia> images) {
                // 压缩成功回调
                onResult(images);
                dismiss();
            }

            @Override
            public void onCompressError(List<LocalMedia> images, String msg) {
                // 压缩失败回调 返回原图
                List<LocalMedia> selectedImages = adapter.getSelectedImages();
                onResult(selectedImages);
                dismiss();
            }
        }).compress();
    }

    private void showDialog(String msg) {
        dialog = new SweetAlertDialog(PictureImageGridActivity.this);
        dialog.setTitleText(msg);
        dialog.show();
    }

    private void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
