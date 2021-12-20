package com.luck.pictureselector;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.PictureSelectorFragment;
import com.luck.picture.lib.PictureSelectorPreviewFragment;
import com.luck.picture.lib.basic.IBridgePictureBehavior;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.basic.PictureContextWrapper;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.SdkVersionUtils;

import java.util.List;

/**
 * @author：luck
 * @date：2021/12/20 1:40 下午
 * @describe：InjectFragmentActivity
 */
public class InjectFragmentActivity extends AppCompatActivity implements IBridgePictureBehavior {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inject_fragment);
        PictureSelectorFragment fragment = new PictureSelectorFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment, PictureSelectorFragment.TAG)
                .addToBackStack(PictureSelectorFragment.TAG)
                .commitAllowingStateLoss();
    }

    @Override
    public void onSelectFinish(boolean isForcedExit, PictureCommonFragment.SelectorResult result) {
        if (isForcedExit) {
            exit();
        } else {
            onBackPressed();
        }
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
        PictureSelectionConfig.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (ActivityCompatHelper.checkRootFragment(this)) {
                if (PictureSelectionConfig.resultCallListener != null) {
                    PictureSelectionConfig.resultCallListener.onCancel();
                }
            } else {
                PictureSelectorPreviewFragment previewFragment = getPreviewFragment();
                if (PictureSelectionConfig.getInstance().isPreviewZoomEffect && previewFragment != null) {
                    previewFragment.onKeyDownBackToMin();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private PictureSelectorPreviewFragment getPreviewFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (fragment instanceof PictureSelectorPreviewFragment) {
                return (PictureSelectorPreviewFragment) fragment;
            }
        }
        return null;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(PictureContextWrapper.wrap(newBase,
                PictureSelectionConfig.getInstance().language));
    }
}
