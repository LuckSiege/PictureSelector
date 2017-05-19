package com.luck.picture.lib.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.PermissionChecker;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.util.LightStatusBarUtils;

import java.util.List;

import static android.R.attr.targetSdkVersion;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */
public class PictureBaseActivity extends FragmentActivity {
    protected Context mContext;
    protected int type = 0;
    protected int maxSelectNum = 0;
    protected int minSelectNum = 0;
    protected int spanCount = 4;
    protected int copyMode = 0;
    protected boolean showCamera = false;
    protected boolean circularCut = false;
    protected boolean enablePreview = false;
    protected boolean enableCrop = false;
    protected boolean enablePreviewVideo = true;
    protected int selectMode = FunctionConfig.MODE_MULTIPLE;
    protected int backgroundColor = 0;
    protected int cb_drawable = 0;
    protected int qq_theme = 0;
    protected int cropW = 0;
    protected int cropH = 0;
    protected int recordVideoSecond = 0;
    protected int definition = 3;
    protected boolean isCompress;
    protected long videoS = 0;
    protected boolean is_checked_num;
    protected int previewColor; // 底部预览字体颜色
    protected int completeColor; // 底部完成字体颜色
    protected int bottomBgColor; // 底部背景色
    protected int previewBottomBgColor; // 预览底部背景色
    protected int previewTopBgColor; // 预览图片标题背景色
    protected int compressQuality = 0;// 压缩图片质量
    protected List<LocalMedia> selectMedias;
    protected FunctionOptions options;
    protected int compressFlag = 1;
    protected int mScreenWidth = 720;
    protected int mScreenHeight = 1280;
    protected int compressW;
    protected int compressH;
    protected boolean freeStyleCrop;
    protected int maxB = 0;
    protected int grade;
    protected int leftDrawable;
    protected int title_color;
    protected int right_color;
    protected int statusBar;
    protected boolean isImmersive;
    protected boolean isNumComplete;
    protected boolean clickVideo;
    protected boolean rotateEnabled;
    protected boolean scaleEnabled;
    protected int offsetX;
    protected int offsetY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initScreenWidth();
        options = (FunctionOptions) getIntent().getSerializableExtra(FunctionConfig.EXTRA_THIS_CONFIG);
        if (options == null) {
            options = new FunctionOptions.Builder().create();
        }
        isImmersive = options.isImmersive();
        if (isImmersive) {
            LightStatusBarUtils.setLightStatusBar(this, isImmersive);
        }
        type = options.getType();
        showCamera = options.isShowCamera();
        enablePreview = options.isEnablePreview();
        selectMode = options.getSelectMode();
        enableCrop = options.isEnableCrop();
        maxSelectNum = options.getMaxSelectNum();
        minSelectNum = options.getMinSelectNum();
        circularCut = options.isCircularCut();
        clickVideo = options.isClickVideo();
        rotateEnabled = options.isRotateEnabled();
        scaleEnabled = options.isScaleEnabled();
        title_color = options.getPicture_title_color();
        right_color = options.getPicture_right_color();
        copyMode = options.getCropMode();
        leftDrawable = options.getLeftBackDrawable();
        enablePreviewVideo = options.isPreviewVideo();
        backgroundColor = options.getThemeStyle();
        cb_drawable = options.getCheckedBoxDrawable();
        qq_theme = options.getCustomQQ_theme();
        isNumComplete = options.isNumComplete();
        isCompress = options.isCompress();
        videoS = options.getVideoS();
        spanCount = options.getImageSpanCount();
        cropW = options.getCropW();
        cropH = options.getCropH();
        offsetX = options.getOffsetX();
        offsetY = options.getOffsetY();
        maxB = options.getMaxB();
        grade = options.getGrade();
        statusBar = options.getStatusBar();
        recordVideoSecond = options.getRecordVideoSecond();
        definition = options.getRecordVideoDefinition();
        is_checked_num = options.isCheckNumMode();
        previewColor = options.getPreviewColor();
        completeColor = options.getCompleteColor();
        bottomBgColor = options.getBottomBgColor();
        previewBottomBgColor = options.getPreviewBottomBgColor();
        previewTopBgColor = options.getPreviewTopBgColor();
        compressQuality = options.getCompressQuality();
        selectMedias = options.getSelectMedia();
        compressFlag = options.getCompressFlag();
        compressW = options.getCompressW();
        compressH = options.getCompressH();
        freeStyleCrop = options.isFreeStyleCrop();
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
                result = mContext.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(mContext, permission)
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
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FunctionConfig.READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readLocalMedia();
                } else {
                    showToast("读取内存卡权限已被拒绝");
                }
                break;
            case FunctionConfig.CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    showToast("拍照权限已被拒绝");
                }
                break;
        }
    }

    /**
     * 启动相机
     */
    protected void startCamera() {

    }

    /**
     * 读取相册信息
     */
    protected void readLocalMedia() {

    }

    protected void startActivity(Class act) {
        Intent intent = new Intent();
        intent.setClass(this, act);
        startActivity(intent);
    }

    protected void startActivity(Class act, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, act);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    protected void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 判断某个activity是否存在
     *
     * @return
     */
    protected boolean isActivityExistence(String packageName, String className) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        if (getPackageManager().resolveActivity(intent, 0) == null) {
            // 说明系统中不存在这个activity
            return false;
        } else {
            return true;
        }
    }

    /**
     * 初始化屏幕宽高
     */
    protected void initScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels;
        mScreenWidth = dm.widthPixels;
    }

}
