package com.luck.picture.lib.basic;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.PictureSelectorFragment;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/17 9:59 上午
 * @describe：PictureSelectorSupporterActivity
 */
public class PictureSelectorSupporterActivity extends AppCompatActivity implements IBridgePictureBehavior {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        immersive();
        setContentView(R.layout.ps_activity_container);
        setupFragment();
    }

    private void immersive() {
        TitleBarStyle titleBarStyle = PictureSelectionConfig.selectorStyle.getTitleBarStyle();
        int statusBarColor = titleBarStyle.getStatusBarColor();
        int navigationBarColor = titleBarStyle.getNavigationBarColor();
        boolean isDarkStatusBarBlack = titleBarStyle.isDarkStatusBarBlack();
        if (!StyleUtils.checkStyleValidity(statusBarColor)) {
            statusBarColor = ContextCompat.getColor(this, R.color.ps_color_grey);
        }
        if (!StyleUtils.checkStyleValidity(navigationBarColor)) {
            navigationBarColor = ContextCompat.getColor(this, R.color.ps_color_grey);
        }
        ImmersiveManage.immersiveAboveAPI23(this, statusBarColor, navigationBarColor, isDarkStatusBarBlack);
    }

    private void setupFragment() {
        if (ActivityCompatHelper.checkFragmentNonExits(this, PictureSelectorFragment.TAG)) {
            injectFragmentFromScreen(PictureSelectorFragment.TAG, PictureSelectorFragment.newInstance());
        }
    }


    @Override
    public void injectFragmentFromScreen(String tag, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    @Override
    public void onFinish() {
        onBackPressed();
    }

    @Override
    public void onImmediateFinish() {
        exit();
    }

    @Override
    public void onBackPressed() {
        if (ActivityCompatHelper.checkRootFragment(this)) {
            exit();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void exit() {
        if (SdkVersionUtils.isQ()) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
        finish();
        PictureWindowAnimationStyle windowAnimationStyle = PictureSelectionConfig.selectorStyle.getWindowAnimationStyle();
        overridePendingTransition(0, windowAnimationStyle.activityExitAnimation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (ActivityCompatHelper.checkRootFragment(this)) {
                if (PictureSelectionConfig.resultCallListener != null) {
                    PictureSelectionConfig.resultCallListener.onCancel();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(PictureContextWrapper.wrap(newBase,
                PictureSelectionConfig.getInstance().language));
    }
}
