package com.luck.picture.lib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @data：2017/01/18 下午1:00
 * @描述: 预览图片
 */
public class PictureExternalPreviewActivity extends PictureBaseActivity implements View.OnClickListener {
    private ImageButton ibLeftBack;
    private TextView tvTitle;
    private PreviewViewPager viewPager;
    private List<LocalMedia> images = new ArrayList<>();
    private int position = 0;
    private SimpleFragmentAdapter adapter;
    private LayoutInflater inflater;
    private loadDataThread loadDataThread;
    private String downloadPath;
    private String mimeType;
    private ImageButton ibDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_external_preview);
        inflater = LayoutInflater.from(this);
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
        initPictureSelectorStyle();
    }

    /**
     * 设置样式
     */
    private void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureTitleTextColor != 0) {
                tvTitle.setTextColor(config.style.pictureTitleTextColor);
            }
            if (config.style.pictureLeftBackIcon != 0) {
                ibLeftBack.setImageResource(config.style.pictureLeftBackIcon);
            }
            if (config.style.pictureExternalPreviewDeleteStyle != 0) {
                ibDelete.setImageResource(config.style.pictureExternalPreviewDeleteStyle);
            }
        }
        tvTitle.setBackgroundColor(colorPrimary);
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
                // 删除通知用户更新
                Bundle bundle = new Bundle();
                bundle.putInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION, currentItem);
                BroadcastManager.getInstance(this)
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

        @Override
        public int getCount() {
            return images != null ? images.size() : 0;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
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
            View contentView = inflater.inflate(R.layout.picture_image_preview, container, false);
            // 常规图控件
            final PhotoView imageView = contentView.findViewById(R.id.preview_image);
            // 长图控件
            final SubsamplingScaleImageView longImg = contentView.findViewById(R.id.longImg);

            LocalMedia media = images.get(position);
            if (media != null) {
                mimeType = media.getMimeType();
                final String path;
                if (media.isCut() && !media.isCompressed()) {
                    // 裁剪过
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                    path = media.getCompressPath();
                } else {
                    path = SdkVersionUtils.checkedAndroid_Q() ? media.getAndroidQToPath() : media.getPath();
                }
                boolean isGif = PictureMimeType.isGif(mimeType);
                final boolean eqLongImg = MediaUtils.isLongImg(media);
                imageView.setVisibility(eqLongImg && !isGif ? View.GONE : View.VISIBLE);
                longImg.setVisibility(eqLongImg && !isGif ? View.VISIBLE : View.GONE);
                // 压缩过的gif就不是gif了
                if (isGif && !media.isCompressed()) {
                    if (config != null && config.imageEngine != null) {
                        config.imageEngine.loadAsGifImage
                                (PictureExternalPreviewActivity.this,
                                        path, imageView);
                    }
                } else {
                    if (config != null && config.imageEngine != null) {
                        if (eqLongImg) {
                            displayLongPic(SdkVersionUtils.checkedAndroid_Q()
                                    ? Uri.parse(path) : Uri.fromFile(new File(path)), longImg);
                        } else {
                            config.imageEngine.loadImage(contentView.getContext(), path, imageView);
                        }
                    }
                }
                imageView.setOnViewTapListener((view, x, y) -> {
                    finish();
                    exitAnimation();
                });
                longImg.setOnClickListener(v -> {
                    finish();
                    exitAnimation();
                });
                imageView.setOnLongClickListener(v -> {
                    if (config.isNotPreviewDownload) {
                        if (PermissionChecker.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            downloadPath = path;
                            showDownLoadDialog();
                        } else {
                            PermissionChecker.requestPermissions(PictureExternalPreviewActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
                        }
                    }
                    return true;
                });
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
        if (TextUtils.isEmpty(downloadPath)) {
            return;
        }
        final PictureCustomDialog dialog = new PictureCustomDialog(PictureExternalPreviewActivity.this,
                ScreenUtils.getScreenWidth(PictureExternalPreviewActivity.this) * 3 / 4,
                ScreenUtils.getScreenHeight(PictureExternalPreviewActivity.this) / 4,
                R.layout.picture_wind_base_dialog_xml, R.style.Picture_Theme_Dialog);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_commit = dialog.findViewById(R.id.btn_commit);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_content = dialog.findViewById(R.id.tv_content);
        tv_title.setText(getString(R.string.picture_prompt));
        tv_content.setText(getString(R.string.picture_prompt_content));
        btn_cancel.setOnClickListener(view -> dialog.dismiss());
        btn_commit.setOnClickListener(view -> {
            boolean isHttp = PictureMimeType.isHttp(downloadPath);
            if (isHttp) {
                showPleaseDialog();
                loadDataThread = new loadDataThread(downloadPath);
                loadDataThread.start();
            } else {
                // 有可能本地图片
                try {
                    String suffix = PictureMimeType.getLastImgSuffix(mimeType);
                    String dirPath = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                            System.currentTimeMillis() + suffix);
                    PictureFileUtils.copyFile(downloadPath, dirPath);
                    ToastUtils.s(mContext, getString(R.string.picture_save_success) + "\n" + dirPath);

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(dirPath));
                    intent.setData(uri);
                    sendBroadcast(intent);

                    dismissDialog();
                } catch (IOException e) {
                    ToastUtils.s(mContext, getString(R.string.picture_save_error) + "\n" + e.getMessage());
                    dismissDialog();
                    e.printStackTrace();
                }
            }
            dialog.dismiss();
        });
        dialog.show();
    }


    // 进度条线程
    public class loadDataThread extends Thread {
        private String path;

        public loadDataThread(String path) {
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

    // 下载图片保存至手机
    public void showLoadingImage(String urlPath) {
        try {
            URL u = new URL(urlPath);
            String suffix = PictureMimeType.getLastImgSuffix(mimeType);
            String path = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                    System.currentTimeMillis() + suffix);
            byte[] buffer = new byte[1024 * 8];
            int read;
            int ava = 0;
            long start = System.currentTimeMillis();
            BufferedInputStream bin;
            bin = new BufferedInputStream(u.openStream());
            BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(path));
            while ((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
                ava += read;
                long speed = ava / (System.currentTimeMillis() - start);
            }
            bout.flush();
            bout.close();
            Message message = handler.obtainMessage();
            message.what = 200;
            message.obj = path;
            handler.sendMessage(message);
        } catch (IOException e) {
            ToastUtils.s(mContext, getString(R.string.picture_save_error) + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    String path = (String) msg.obj;
                    ToastUtils.s(mContext, getString(R.string.picture_save_success) + "\n" + path);
                    dismissDialog();
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
        if (loadDataThread != null) {
            handler.removeCallbacks(loadDataThread);
            loadDataThread = null;
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
                        ToastUtils.s(mContext, getString(R.string.picture_jurisdiction));
                    }
                }
                break;
        }
    }
}
