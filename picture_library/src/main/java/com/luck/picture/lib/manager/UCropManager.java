package com.luck.picture.lib.manager;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.CutInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * @author：luck
 * @date：2020/11/19 5:33 PM
 * @describe：UCropManager
 */
public class UCropManager {

    /**
     * 裁剪
     *
     * @param activity
     * @param originalPath
     * @param mimeType
     */
    public static void ofCrop(Activity activity, String originalPath, String mimeType) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        if (TextUtils.isEmpty(originalPath)) {
            ToastUtils.s(activity.getApplicationContext(), activity.getString(R.string.picture_not_crop_data));
            return;
        }
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        boolean isHttp = PictureMimeType.isHasHttp(originalPath);
        String suffix = mimeType.replace("image/", ".");
        File file = new File(PictureFileUtils.getDiskCacheDir(activity.getApplicationContext()),
                TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_CROP_") + suffix : config.renameCropFileName);
        Uri uri = isHttp || SdkVersionUtils.checkedAndroid_Q() ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        UCrop.Options options = UCropManager.basicOptions(activity);
        UCrop.of(uri, Uri.fromFile(file))
                .withOptions(options)
                .startAnimationActivity(activity, PictureSelectionConfig.windowAnimationStyle.activityCropEnterAnimation);
    }

    /**
     * 裁剪
     *
     * @param activity
     * @param list
     */
    public static void ofCrop(Activity activity, ArrayList<CutInfo> list) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        if (list == null || list.size() == 0) {
            ToastUtils.s(activity.getApplicationContext(), activity.getString(R.string.picture_not_crop_data));
            return;
        }
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        UCrop.Options options = UCropManager.basicOptions(activity);
        options.setCutListData(list);
        int size = list.size();
        int index = 0;
        if (config.chooseMode == PictureMimeType.ofAll() && config.isWithVideoImage) {
            String mimeType = size > 0 ? list.get(index).getMimeType() : "";
            boolean isHasVideo = PictureMimeType.isHasVideo(mimeType);
            if (isHasVideo) {
                for (int i = 0; i < size; i++) {
                    CutInfo cutInfo = list.get(i);
                    if (cutInfo != null && PictureMimeType.isHasImage(cutInfo.getMimeType())) {
                        index = i;
                        break;
                    }
                }
            }
        }
        if (index < size) {
            CutInfo info = list.get(index);
            boolean isHttp = PictureMimeType.isHasHttp(info.getPath());
            Uri uri;
            if (TextUtils.isEmpty(info.getAndroidQToPath())) {
                uri = isHttp || SdkVersionUtils.checkedAndroid_Q() ? Uri.parse(info.getPath()) : Uri.fromFile(new File(info.getPath()));
            } else {
                uri = Uri.fromFile(new File(info.getAndroidQToPath()));
            }
            String suffix = info.getMimeType().replace("image/", ".");
            File file = new File(PictureFileUtils.getDiskCacheDir(activity),
                    TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_CROP_")
                            + suffix : config.camera || size == 1 ? config.renameCropFileName : StringUtils.rename(config.renameCropFileName));
            UCrop.of(uri, Uri.fromFile(file)).withOptions(options)
                    .startAnimationMultipleCropActivity(activity, PictureSelectionConfig.windowAnimationStyle.activityCropEnterAnimation);
        }
    }


    /**
     * basicOptions
     *
     * @param context
     * @return
     */
    public static UCrop.Options basicOptions(Context context) {
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        int toolbarColor = 0, statusColor = 0, titleColor = 0, cropNavBarColor = 0;
        boolean isChangeStatusBarFontColor;
        if (PictureSelectionConfig.uiStyle != null) {
            cropNavBarColor = PictureSelectionConfig.uiStyle.picture_navBarColor;
            isChangeStatusBarFontColor = PictureSelectionConfig.uiStyle.picture_statusBarChangeTextColor;
            if (PictureSelectionConfig.uiStyle.picture_top_titleBarBackgroundColor != 0) {
                toolbarColor = PictureSelectionConfig.uiStyle.picture_top_titleBarBackgroundColor;
            }
            if (PictureSelectionConfig.uiStyle.picture_statusBarBackgroundColor != 0) {
                statusColor = PictureSelectionConfig.uiStyle.picture_statusBarBackgroundColor;
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleTextColor != 0) {
                titleColor = PictureSelectionConfig.uiStyle.picture_top_titleTextColor;
            }
        } else if (PictureSelectionConfig.cropStyle != null) {
            cropNavBarColor = PictureSelectionConfig.cropStyle.cropNavBarColor;
            isChangeStatusBarFontColor = PictureSelectionConfig.cropStyle.isChangeStatusBarFontColor;
            if (PictureSelectionConfig.cropStyle.cropTitleBarBackgroundColor != 0) {
                toolbarColor = PictureSelectionConfig.cropStyle.cropTitleBarBackgroundColor;
            }
            if (PictureSelectionConfig.cropStyle.cropStatusBarColorPrimaryDark != 0) {
                statusColor = PictureSelectionConfig.cropStyle.cropStatusBarColorPrimaryDark;
            }
            if (PictureSelectionConfig.cropStyle.cropTitleColor != 0) {
                titleColor = PictureSelectionConfig.cropStyle.cropTitleColor;
            }
        } else {
            isChangeStatusBarFontColor = config.isChangeStatusBarFontColor;
            if (!isChangeStatusBarFontColor) {
                isChangeStatusBarFontColor = AttrsUtils.getTypeValueBoolean(context, R.attr.picture_statusFontColor);
            }
            if (config.cropTitleBarBackgroundColor != 0) {
                toolbarColor = config.cropTitleBarBackgroundColor;
            } else {
                toolbarColor = AttrsUtils.getTypeValueColor(context, R.attr.picture_crop_toolbar_bg);
            }
            if (config.cropStatusBarColorPrimaryDark != 0) {
                statusColor = config.cropStatusBarColorPrimaryDark;
            } else {
                statusColor = AttrsUtils.getTypeValueColor(context, R.attr.picture_crop_status_color);
            }
            if (config.cropTitleColor != 0) {
                titleColor = config.cropTitleColor;
            } else {
                titleColor = AttrsUtils.getTypeValueColor(context, R.attr.picture_crop_title_color);
            }
        }
        UCrop.Options options = config.uCropOptions == null ? new UCrop.Options() : config.uCropOptions;
        options.isOpenWhiteStatusBar(isChangeStatusBarFontColor);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(config.circleDimmedLayer);
        options.setDimmedLayerColor(config.circleDimmedColor);
        options.setDimmedLayerBorderColor(config.circleDimmedBorderColor);
        options.setCircleStrokeWidth(config.circleStrokeWidth);
        options.setShowCropFrame(config.showCropFrame);
        options.setDragFrameEnabled(config.isDragFrame);
        options.setShowCropGrid(config.showCropGrid);
        options.setScaleEnabled(config.scaleEnabled);
        options.setRotateEnabled(config.rotateEnabled);
        options.isMultipleSkipCrop(config.isMultipleSkipCrop);
        options.setHideBottomControls(config.hideBottomControls);
        options.setCompressionQuality(config.cropCompressQuality);
        options.setRenameCropFileName(config.renameCropFileName);
        options.isCamera(config.camera);
        options.setNavBarColor(cropNavBarColor);
        options.isWithVideoImage(config.isWithVideoImage);
        options.setFreeStyleCropEnabled(config.freeStyleCropEnabled);
        options.setCropExitAnimation(PictureSelectionConfig.windowAnimationStyle.activityCropExitAnimation);
        options.withAspectRatio(config.aspect_ratio_x, config.aspect_ratio_y);
        options.isMultipleRecyclerAnimation(config.isMultipleRecyclerAnimation);
        if (config.cropWidth > 0 && config.cropHeight > 0) {
            options.withMaxResultSize(config.cropWidth, config.cropHeight);
        }
        return options;
    }

}
