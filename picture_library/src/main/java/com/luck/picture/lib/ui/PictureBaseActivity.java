package com.luck.picture.lib.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.luck.picture.lib.R;
import com.luck.picture.lib.model.FunctionConfig;
import com.yalantis.ucrop.entity.LocalMedia;
import java.util.ArrayList;
import java.util.List;

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
    protected int spanCount = 4;
    protected int copyMode = 0;
    protected boolean showCamera = false;
    protected boolean enablePreview = false;
    protected boolean enableCrop = false;
    protected boolean enablePreviewVideo = true;
    protected int selectMode = FunctionConfig.MODE_MULTIPLE;
    protected int backgroundColor = 0;
    protected int cb_drawable = 0;
    protected int cropW = 0;
    protected int cropH = 0;
    protected int recordVideoSecond = 0;
    protected int definition = 3;
    protected boolean isCompress;
    protected boolean is_checked_num;
    protected int previewColor; // 底部预览字体颜色
    protected int completeColor; // 底部完成字体颜色
    protected int bottomBgColor; // 底部背景色
    protected int previewBottomBgColor; // 预览底部背景色
    protected int compressQuality = 0;// 压缩图片质量
    protected List<LocalMedia> selectMedias = new ArrayList<>();
    protected FunctionConfig config = new FunctionConfig();
    protected int compressFlag = 1;
    protected int mScreenWidth = 720;
    protected int mScreenHeight = 1280;
    protected int compressW;
    protected int compressH;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initScreenWidth();
        config = (FunctionConfig) getIntent().getSerializableExtra(FunctionConfig.EXTRA_THIS_CONFIG);
        type = config.getType();
        showCamera = config.isShowCamera();
        enablePreview = config.isEnablePreview();
        selectMode = config.getSelectMode();
        enableCrop = config.isEnableCrop();
        maxSelectNum = config.getMaxSelectNum();
        copyMode = config.getCopyMode();
        enablePreviewVideo = config.isPreviewVideo();
        backgroundColor = config.getThemeStyle();
        cb_drawable = config.getCheckedBoxDrawable();
        isCompress = config.isCompress();
        spanCount = config.getImageSpanCount();
        cropW = config.getCropW();
        cropH = config.getCropH();
        recordVideoSecond = config.getRecordVideoSecond();
        definition = config.getRecordVideoDefinition();
        is_checked_num = config.isCheckNumMode();
        previewColor = config.getPreviewColor();
        completeColor = config.getCompleteColor();
        bottomBgColor = config.getBottomBgColor();
        previewBottomBgColor = config.getPreviewBottomBgColor();
        compressQuality = config.getCompressQuality();
        selectMedias = config.getSelectMedia();
        compressFlag = config.getCompressFlag();
        compressW = config.getCompressW();
        compressH = config.getCompressH();
        // 如果是显示数据风格，则默认为qq选择风格
        if (is_checked_num) {
            cb_drawable = R.drawable.checkbox_num_selector;
        }
    }

    /**
     * 针对6.0动态请求权限问题
     * 判断是否允许此权限
     *
     * @param permissions
     * @return
     */
    protected boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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

    /**
     * 广播发送者
     *
     * @param action
     */
    protected void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }


    /**
     * 注册发送者
     *
     * @param action
     */
    protected void registerReceiver(BroadcastReceiver receiver, String... action) {
        IntentFilter intentFilter = new IntentFilter();
        for (int i = 0; i < action.length; i++) {
            intentFilter.addAction(action[i]);
        }
        registerReceiver(receiver, intentFilter);
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
