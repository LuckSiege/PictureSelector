package com.yalantis.ucrop.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.adapter.ImageGridAdapter;
import com.yalantis.ucrop.decoration.GridSpacingItemDecoration;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.entity.LocalMediaFolder;
import com.yalantis.ucrop.util.Constants;
import com.yalantis.ucrop.util.FileUtils;
import com.yalantis.ucrop.util.LocalMediaLoader;
import com.yalantis.ucrop.util.ScreenUtils;

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
public class ImageGridActivity extends BaseActivity implements View.OnClickListener, ImageGridAdapter.OnPhotoSelectChangedListener {
    public final String TAG = ImageGridActivity.class.getSimpleName();
    public final static int REQUEST_IMAGE = 88;
    public final static int REQUEST_CAMERA = 99;
    public final static int REQUEST_PREVIEW = 100;
    public final static String FOLDER_NAME = "folderName";
    public final static String EXTRA_IMAGES = "images";
    public final static String REQUEST_OUTPUT = "outputList";

    private int spanCount = 4;
    private List<LocalMedia> images = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageButton left_back;
    private TextView tv_img_num;
    private TextView tv_ok;
    private RelativeLayout rl_bottom;
    private TextView tv_right, tv_title;
    private Button id_preview;
    private ImageGridAdapter adapter;
    private String cameraPath;
    private int maxSelectNum = 0;
    private boolean enableCrop = false;
    private boolean enablePreview = true;
    private boolean enablePreviewVideo = true;
    private boolean showCamera = true;
    protected int type = 0;
    private int copyModel = 0;
    public final static int MODE_MULTIPLE = 1;// 多选
    public final static int MODE_SINGLE = 2;// 单选
    private int selectMode = MODE_MULTIPLE;
    private List<LocalMediaFolder> folders = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<LocalMedia>();// 记录选中的图片

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);
        registerReceiver(broadcastReceiver, Constants.ACTION_FINISH, Constants.ACTION_ADD_PHOTO, Constants.ACTION_REMOVE_PHOTO);
        String folderName = getIntent().getStringExtra(FOLDER_NAME);
        folders = (List<LocalMediaFolder>) getIntent().getSerializableExtra(Constants.EXTRA_FOLDERS);
        if (folders == null) {
            folders = new ArrayList<>();
        }
        type = getIntent().getIntExtra(Constants.EXTRA_TYPE, 0);// 1图片 2视频
        selectImages = (List<LocalMedia>) getIntent().getSerializableExtra(Constants.EXTRA_PREVIEW_SELECT_LIST);
        images = (List<LocalMedia>) getIntent().getSerializableExtra(EXTRA_IMAGES);
        copyModel = getIntent().getIntExtra(Constants.EXTRA_CROP_MODE, 0);// 裁剪模式
        enableCrop = getIntent().getBooleanExtra(Constants.EXTRA_ENABLE_CROP, false);
        enablePreview = getIntent().getBooleanExtra(Constants.EXTRA_ENABLE_PREVIEW, true);// 是否预览
        showCamera = getIntent().getBooleanExtra(Constants.EXTRA_SHOW_CAMERA, true);
        selectMode = getIntent().getIntExtra(Constants.EXTRA_SELECT_MODE, MODE_MULTIPLE);
        enablePreviewVideo = getIntent().getBooleanExtra(Constants.EXTRA_ENABLE_PREVIEW_VIDEO, true);
        maxSelectNum = getIntent().getIntExtra(Constants.EXTRA_MAX_SELECT_NUM, 0);
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(Constants.BUNDLE_CAMERA_PATH);
        }
        if (images == null) {
            images = new ArrayList<>();
        }
        if (selectImages == null) {
            selectImages = new ArrayList<>();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        left_back = (ImageButton) findViewById(R.id.left_back);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        id_preview = (Button) findViewById(R.id.id_preview);
        tv_img_num = (TextView) findViewById(R.id.tv_img_num);
        left_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        id_preview.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        if (enablePreview && selectMode == Constants.MODE_MULTIPLE) {
            if (type == LocalMediaLoader.TYPE_VIDEO) {
                // 如果是视频不能预览
                id_preview.setVisibility(View.GONE);
            } else {
                id_preview.setVisibility(View.VISIBLE);
            }
        } else if (selectMode == Constants.MODE_SINGLE) {
            rl_bottom.setVisibility(View.GONE);
        } else {
            id_preview.setVisibility(View.GONE);
        }
        if (folderName != null && !folderName.equals("")) {
            tv_title.setText(folderName);
        } else {
            switch (type) {
                case LocalMediaLoader.TYPE_IMAGE:
                    tv_title.setText(getString(R.string.all_image));
                    break;
                case LocalMediaLoader.TYPE_VIDEO:
                    tv_title.setText(getString(R.string.all_video));
                    break;
            }
        }
        tv_right.setText(getString(R.string.cancel));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, ScreenUtils.dip2px(this, 2), false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        adapter = new ImageGridAdapter(this, showCamera, maxSelectNum, selectMode, enablePreview, enablePreviewVideo);
        recyclerView.setAdapter(adapter);
        if (selectImages.size() > 0) {
            ChangeImageNumber(selectImages);
            adapter.bindSelectImages(selectImages);
        }
        adapter.bindImagesData(images);
        adapter.setOnPhotoSelectChangedListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        int id = view.getId();
        if (id == R.id.left_back || id == R.id.tv_right) {
            setResult(RESULT_OK, new Intent().putExtra("type", 1).putExtra(Constants.EXTRA_PREVIEW_SELECT_LIST, (Serializable) adapter.getSelectedImages()).putExtra(Constants.EXTRA_FOLDERS, (Serializable) folders));
            finish();
        } else if (id == R.id.id_preview) {
            intent.putExtra(Constants.EXTRA_PREVIEW_LIST, (Serializable) adapter.getSelectedImages());
            intent.putExtra(Constants.EXTRA_PREVIEW_SELECT_LIST, (Serializable) adapter.getSelectedImages());
            intent.putExtra(Constants.EXTRA_POSITION, 0);
            intent.putExtra(Constants.EXTRA_MAX_SELECT_NUM, maxSelectNum);
            intent.setClass(mContext, PreviewActivity.class);
            startActivityForResult(intent, REQUEST_PREVIEW);
        } else if (id == R.id.tv_ok) {
            List<LocalMedia> images = adapter.getSelectedImages();
            ArrayList<String> result = new ArrayList<>();
            for (LocalMedia media : images) {
                result.add(media.getPath());
            }
            if (result.size() > 0) {
                onResult(result);
            }
        }
    }

    @Override
    public void onTakePhoto() {
        // 启动相机拍照,先判断手机是否有拍照权限
        if (hasPermission(Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            requestPermission(Constants.CAMERA, Manifest.permission.CAMERA);
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
                if (enableCrop && selectMode == Constants.MODE_SINGLE) {
                    startCopy(media.getPath());
                } else if (!enableCrop && selectMode == Constants.MODE_SINGLE) {
                    onSelectDone(media.getPath());
                } else {
                    // 图片可以预览
                    intent.putExtra(Constants.EXTRA_PREVIEW_LIST, (Serializable) previewImages);
                    intent.putExtra(Constants.EXTRA_PREVIEW_SELECT_LIST, (Serializable) adapter.getSelectedImages());
                    intent.putExtra(Constants.EXTRA_POSITION, position);
                    intent.putExtra(Constants.EXTRA_MAX_SELECT_NUM, maxSelectNum);
                    intent.setClass(mContext, PreviewActivity.class);
                    startActivityForResult(intent, REQUEST_PREVIEW);
                }
                break;
            case LocalMediaLoader.TYPE_VIDEO:
                // 视频
                if (selectMode == Constants.MODE_SINGLE) {
                    // 单选
                    onSelectDone(media.getPath());
                } else {
                    bundle.putString("video_path", media.getPath());
                    startActivity(VideoPlayActivity.class, bundle);
                }
                break;
        }

    }

    protected void startCopy(String path) {
        // 如果开启裁剪 并且是单选
        // 去裁剪
        UCrop uCrop = UCrop.of(Uri.parse(path), Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg")));
        UCrop.Options options = new UCrop.Options();
        switch (copyModel) {
            case Constants.COPY_MODEL_DEFAULT:
                options.withAspectRatio(0, 0);
                break;
            case Constants.COPY_MODEL_1_1:
                options.withAspectRatio(1, 1);
                break;
            case Constants.COPY_MODEL_3_2:
                options.withAspectRatio(3, 2);
                break;
            case Constants.COPY_MODEL_3_4:
                options.withAspectRatio(3, 4);
                break;
            case Constants.COPY_MODEL_16_9:
                options.withAspectRatio(16, 9);
                break;
        }
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.bar_grey));
        uCrop.withOptions(options);
        uCrop.start(ImageGridActivity.this);
    }

    /**
     * start to camera、preview、crop
     */
    public void startOpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = FileUtils.createCameraFile(this, type);
            cameraPath = cameraFile.getAbsolutePath();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
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
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // on take photo success
            if (requestCode == REQUEST_CAMERA) {
                // 拍照返回
                File file = new File(cameraPath);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                if (selectMode == Constants.MODE_SINGLE) {
                    // 如果是单选 拍照后直接返回
                    if (enableCrop && type == LocalMediaLoader.TYPE_IMAGE) {
                        // 如果允许裁剪，并且是图片
                        startCopy(cameraPath);
                    } else {
                        onSelectDone(cameraPath);
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

                    LocalMedia media = new LocalMedia(file.getPath(), duration, duration, type);

                    // 根据新拍照生成的图片，插入到对应的相册当中，避免重新查询一遍数据库
                    LocalMediaFolder folder = getImageFolder(media.getPath(), folders);
                    Log.i("FolderName", folder.getName());
                    folder.getImages().add(0, media);// 插入到第一个位置
                    folder.setImageNum(folder.getImageNum() + 1);
                    folder.setFirstImagePath(media.getPath());
                    folder.setType(type);
                    addAllFolder(media);// 所有相册或视频也插入


                    Log.i("FolderfsafsaName", folders.size() + "");
                    List<LocalMedia> images = adapter.getImages();
                    images.add(0, media);
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    selectedImages.add(media);
                    adapter.bindImagesData(images);
                    adapter.bindSelectImages(selectedImages);
                    ChangeImageNumber(adapter.getSelectedImages());
                }

            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            } else if (requestCode == REQUEST_PREVIEW) {
                // 预览点击完成
                if (data != null) {
                    ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(Constants.EXTRA_PREVIEW_SELECT_LIST);
                    onResult(images);
                }
            }
        }
    }

    private void addAllFolder(LocalMedia media) {
        for (LocalMediaFolder f : folders) {
            if (f.getName().equals(getString(R.string.all_image)) || f.getName().equals(getString(R.string.all_video))) {
                f.getImages().add(0, media);
                f.setType(media.getType());
                f.setImageNum(f.getImages().size());
                f.setFirstImagePath(media.getPath());
            }
        }
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            onSelectDone(resultUri.getPath());
        }
    }

    public void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        onResult(images);
    }

    public void onResult(ArrayList<String> images) {
        setResult(RESULT_OK, new Intent().putStringArrayListExtra(REQUEST_OUTPUT, images));
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.BUNDLE_CAMERA_PATH, cameraPath);
    }

    /**
     * 刷新图片选中状态
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LocalMedia image = (LocalMedia) intent.getSerializableExtra("media");
            if (action.equals(Constants.ACTION_ADD_PHOTO)) {
                // 预览时新选择了图片
                List<LocalMedia> selectedImages = adapter.getSelectedImages();
                selectedImages.add(image);
                adapter.bindSelectImages(selectedImages);
            } else if (action.equals(Constants.ACTION_REMOVE_PHOTO)) {
                // 预览时取消了之前选中的图片
                List<LocalMedia> selectImages = adapter.getSelectedImages();
                for (LocalMedia media : selectImages) {
                    if (media.getPath().equals(image.getPath())) {
                        selectImages.remove(media);
                        ChangeImageNumber(selectImages);
                        break;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

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
                setResult(RESULT_OK, new Intent().putExtra("type", 1).putExtra(Constants.EXTRA_PREVIEW_SELECT_LIST, (Serializable) adapter.getSelectedImages()).putExtra(Constants.EXTRA_FOLDERS, (Serializable) folders));
                finish();
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
