package com.luck.picture.lib;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.dialog.CustomDialog;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.Constant;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.R.attr.targetSdkVersion;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：邮箱->893855882@qq.com
 * data：17/01/18
 */
public class PictureImagePreviewFragment extends Fragment {
    private PictureDialog dialog;
    private loadDataThread loadDataThread;
    private boolean isSave;
    private String directory_path;
    private String path = "";

    public static PictureImagePreviewFragment getInstance(LocalMedia media, boolean isSave, String directory_path) {
        PictureImagePreviewFragment fragment = new PictureImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PictureConfig.EXTRA_MEDIA, media);
        bundle.putBoolean("isSave", isSave);
        bundle.putString(PictureConfig.DIRECTORY_PATH, directory_path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.picture_fragment_image_preview, container, false);
        final PhotoView imageView = (PhotoView) contentView.findViewById(R.id.preview_image);
        LocalMedia media = (LocalMedia) getArguments().getSerializable(PictureConfig.EXTRA_MEDIA);
        if (media.isCut() && !media.isCompressed()) {
            // 裁剪过
            path = media.getCutPath();
        } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
            path = media.getCompressPath();
        } else {
            path = media.getPath();
        }
        String pictureType = media.getPictureType();
        isSave = getArguments().getBoolean("isSave");
        directory_path = getArguments().getString(PictureConfig.DIRECTORY_PATH);
        if (!isSave && path.startsWith("http")) {
            showPleaseDialog();
        }
        boolean isGif = PictureMimeType.isGif(pictureType);
        // 压缩过的gif就不是gif了
        if (isGif && !media.isCompressed()) {
            Glide.with(container.getContext())
                    .load(path)
                    .asGif()
                    .override(480, 800)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH)
                    .into(imageView);
            dismissDialog();
        } else {
            Glide.with(container.getContext())
                    .load(path)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(new SimpleTarget<Bitmap>(480, 800) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            imageView.setImageBitmap(resource);
                            dismissDialog();
                        }
                    });
        }
        imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                getActivity().finish();
                getActivity().overridePendingTransition(0, R.anim.a3);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 内部预览不保存
                if (!isSave) {
                    if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showDownLoadDialog(path);
                    } else {
                        requestPermission(Constant.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
                return true;
            }
        });
        return contentView;
    }

    /**
     * 下载图片提示
     */
    private void showDownLoadDialog(final String path) {
        final CustomDialog dialog = new CustomDialog(getContext(), ScreenUtils.getScreenWidth(getContext()) * 3 / 4,
                ScreenUtils.getScreenHeight(getContext()) / 4, R.layout.picture_wind_base_dialog_xml, R.style.Theme_dialog);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        tv_title.setText("提示");
        tv_content.setText("是否保存图片至手机？");
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPleaseDialog();
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith("http") || path.startsWith("https")) {
                        loadDataThread = new loadDataThread(path);
                        loadDataThread.start();
                    } else {
                        // 有可能本地图片
                        String dirPath = PictureFileUtils.createDir(getActivity(), System.currentTimeMillis() + ".png", directory_path);
                        try {
                            PictureFileUtils.copyFile(path, dirPath);
                            Toast.makeText(getActivity(), "图片保存成功至\n" + dirPath, Toast.LENGTH_SHORT).show();
                            dismissDialog();
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), "图片保存失败\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            dismissDialog();
                            e.printStackTrace();
                        }
                    }
                }
                dialog.dismiss();
            }
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
            String path = PictureFileUtils.createDir(getActivity(),
                    System.currentTimeMillis() + ".png", directory_path);
            byte[] buffer = new byte[1024 * 8];
            int read;
            int ava = 0;
            long start = System.currentTimeMillis();
            BufferedInputStream bin;
            HttpURLConnection urlConn = (HttpURLConnection) u.openConnection();
            double fileLength = (double) urlConn.getContentLength();
            bin = new BufferedInputStream(u.openStream());
            BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(path));
            while ((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
                ava += read;
                int a = (int) Math.floor((ava / fileLength * 100));
                long speed = ava / (System.currentTimeMillis() - start);
                System.out.println("Download: " + ava + " byte(s)"
                        + "    avg speed: " + speed + "  (kb/s)");
            }
            bout.flush();
            bout.close();
            Message message = handler.obtainMessage();
            message.what = 200;
            message.obj = path;
            handler.sendMessage(message);
        } catch (IOException e) {
            Toast.makeText(getActivity(), "图片保存失败\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "图片保存成功至\n" + path, Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    break;
            }
        }
    };


    /**
     * 提示框
     */
    private void showPleaseDialog() {
        if (!getActivity().isFinishing()) {
            dialog = new PictureDialog(getActivity());
            dialog.show();
        }
    }

    /**
     * 关闭提示框
     */
    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    /**
     * 针对6.0动态请求权限问题
     * 判断是否允许此权限
     *
     * @param permission
     * @return
     */
    protected boolean hasPermission(String permission) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = getContext().checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(getContext(), permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        return result;
    }

    /**
     * 动态请求权限
     *
     * @param code
     * @param permissions
     */
    protected void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(getActivity(), permissions, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getContext(), "读取内存卡权限已被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadDataThread != null) {
            handler.removeCallbacks(loadDataThread);
            loadDataThread = null;
        }
    }
}
