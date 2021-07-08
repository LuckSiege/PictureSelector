package com.luck.picture.lib.style;

import android.content.Context;
import android.graphics.Color;

import com.luck.picture.lib.R;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.tools.ScreenUtils;

/**
 * @author：luck
 * @date：2020/11/22 11:26 AM
 * @describe：PictureUIStyle
 */
public class PictureSelectorUIStyle {
    /**
     * 状态栏背景色
     */
    public int picture_statusBarBackgroundColor;

    /**
     * 是否改变状态字体颜色
     */
    public boolean picture_statusBarChangeTextColor;

    /**
     * 开启新选择风格
     */
    public boolean isNewSelectStyle;

    /**
     * 是否开启0/N选择模式
     */
    public boolean picture_switchSelectTotalStyle;

    /**
     * 是否开启数字风格选择模式
     */
    public boolean picture_switchSelectNumberStyle;

    /**
     * 是否使用(%1$d/%2$d)字符串
     */
    public boolean isCompleteReplaceNum;

    /**
     * 返回按钮
     */
    public int picture_top_leftBack;

    /**
     * 相册背景色
     */
    public int picture_container_backgroundColor;

    /**
     * 标题栏背景色
     */
    public int picture_top_titleBarBackgroundColor;

    /**
     * 标题栏高度
     */
    public int picture_top_titleBarHeight;

    /**
     * 标题字体大小
     */
    public int picture_top_titleTextSize;
    /**
     * 标题字体颜色
     */
    public int picture_top_titleTextColor;

    /**
     * 标题右侧箭头left padding
     */
    public int picture_top_titleArrowLeftPadding;
    /**
     * 标题右侧向上箭头
     */
    public int picture_top_titleArrowUpDrawable;

    /**
     * 标题右侧向下箭头
     */
    public int picture_top_titleArrowDownDrawable;

    /**
     * 相册标题栏背景色
     */
    public int picture_top_titleAlbumBackground;

    /**
     * 标题栏右侧按钮默认文案
     */
    public int picture_top_titleRightDefaultText;

    /**
     * 标题栏右侧按钮正常文案(可点击)
     */
    public int picture_top_titleRightNormalText;

    /**
     * 标题栏右侧文字大小
     */
    public int picture_top_titleRightTextSize;

    /**
     * 标题栏右侧文字颜色
     */
    public int[] picture_top_titleRightTextColor;

    /**
     * 标题栏右侧文字默认背景
     */
    public int picture_top_titleRightTextDefaultBackground;

    /**
     * 标题栏右侧文字正常背景(可点击)
     */
    public int picture_top_titleRightTextNormalBackground;

    /**
     * 是否显示预览页删除按钮
     */
    public boolean picture_top_showHideDeleteButton;

    /**
     * 预览页删除按钮图标
     */
    public int picture_top_deleteButtonStyle;

    /**
     * 选中文字大小
     */
    public int picture_check_textSize;
    /**
     * 选中文字颜色
     */
    public int picture_check_textColor;
    /**
     * 选中样式
     */
    public int picture_check_style;

    /**
     * 底部bar背景色
     */
    public int picture_bottom_barBackgroundColor;

    /**
     * 底部bar高度
     */
    public int picture_bottom_barHeight;

    /**
     * 底部预览默认文字
     */
    public int picture_bottom_previewDefaultText;

    /**
     * 底部预览文字(可点击)
     */
    public int picture_bottom_previewNormalText;

    /**
     * 底部预览文字大小
     */
    public int picture_bottom_previewTextSize;

    /**
     * 底部预览文字颜色
     */
    public int[] picture_bottom_previewTextColor;

    /**
     * 底部预览页面编辑文字大小
     */
    public int picture_bottom_preview_editorTextSize;

    /**
     * 底部预览页面编辑文字大小
     */
    public int picture_bottom_preview_editorTextColor;


    /**
     * 原图勾选样式
     */
    public int picture_bottom_originalPictureCheckStyle;

    /**
     * 原图文案
     */
    @Deprecated
    public int picture_bottom_originalPictureText;

    /**
     * 原图文字大小
     */
    public int picture_bottom_originalPictureTextSize;
    /**
     * 原图文字颜色
     */
    public int picture_bottom_originalPictureTextColor;

    /**
     * 完成默认文案
     */
    public int picture_bottom_completeDefaultText;

    /**
     * 完成正常文案(可点击)
     */
    public int picture_bottom_completeNormalText;

    /**
     * 完成文字大小
     */
    public int picture_bottom_completeTextSize;

    /**
     * 完成文字字体颜色
     */
    public int[] picture_bottom_completeTextColor;

    /**
     * 完成红点字体大小
     */
    public int picture_bottom_completeRedDotTextSize;
    /**
     * 完成红点字体颜色
     */
    public int picture_bottom_completeRedDotTextColor;
    /**
     * 完成红点背景色
     */
    public int picture_bottom_completeRedDotBackground;

    /**
     * 选择按钮文案(新样式支持)
     */
    public int picture_bottom_selectedText;

    /**
     * 选择按钮文案字体大小(新样式支持)
     */
    public int picture_bottom_selectedTextSize;
    /**
     * 选择按钮文案字体颜色(新样式支持)
     */
    public int picture_bottom_selectedTextColor;

    /**
     * 选择按钮勾选样式
     */
    public int picture_bottom_selectedCheckStyle;

    /**
     * 底部gallery 选中背景框样式(新样式支持)
     */
    public int picture_bottom_gallery_frameBackground;

    /**
     * 底部gallery分割线(新样式支持)
     */
    public int picture_bottom_gallery_dividerColor;

    /**
     * 底部gallery背景色(新样式支持)
     */
    public int picture_bottom_gallery_backgroundColor;

    /**
     * 底部gallery高度(新样式支持)
     */
    public int picture_bottom_gallery_height;

    /**
     * NavBarColor
     */
    public int picture_navBarColor;

    /**
     * 专辑栏标题位置居中
     */
    public boolean picture_album_horizontal = false;

    /**
     * 专辑字体大小
     */
    public int picture_album_textSize;
    /**
     * 专辑字体颜色
     */
    public int picture_album_textColor;
    /**
     * 专辑选中提醒样式
     */
    public int picture_album_checkDotStyle;
    /**
     * 专辑item背景
     */
    public int picture_album_backgroundStyle;
    /**
     * adapter 拍照item背景色
     */
    public int picture_adapter_item_camera_backgroundColor;
    /**
     * adapter 拍照 top drawable
     */
    public int picture_adapter_item_camera_textTopDrawable;
    /**
     * adapter 拍照文案
     */
    public int picture_adapter_item_camera_text;
    /**
     * adapter 拍照文字大小
     */
    public int picture_adapter_item_camera_textSize;
    /**
     * adapter 拍照文字颜色
     */
    public int picture_adapter_item_camera_textColor;

    /**
     * adapter item 文字大小
     */
    public int picture_adapter_item_textSize;
    /**
     * adapter item 文字颜色
     */
    public int picture_adapter_item_textColor;
    /**
     * adapter item 文字left drawable
     */
    public int picture_adapter_item_video_textLeftDrawable;
    /**
     * adapter item 文字left drawable
     */
    public int picture_adapter_item_audio_textLeftDrawable;

    /**
     * adapter item标签文案
     */
    public int picture_adapter_item_tag_text;

    /**
     * 是否显示tag
     */
    public boolean picture_adapter_item_gif_tag_show = true;

    /**
     * adapter item标签文字大小
     */
    public int picture_adapter_item_gif_tag_textSize;

    /**
     * adapter item标签文字颜色
     */
    public int picture_adapter_item_gif_tag_textColor;

    /**
     * adapter item标签背景
     */
    public int picture_adapter_item_gif_tag_background;

    /**
     * adapter item 已编辑图标
     */
    public int picture_adapter_item_editor_tag_icon;

    /**
     * 默认样式
     *
     * @return
     */
    public static PictureSelectorUIStyle ofDefaultStyle() {
        PictureSelectorUIStyle uiStyle = new PictureSelectorUIStyle();

        uiStyle.picture_statusBarBackgroundColor = Color.parseColor("#393a3e");
        uiStyle.picture_container_backgroundColor = Color.parseColor("#000000");

        uiStyle.picture_navBarColor = Color.parseColor("#393a3e");

        uiStyle.picture_check_style = R.drawable.picture_checkbox_selector;

        uiStyle.picture_top_leftBack = R.drawable.picture_icon_back;
        uiStyle.picture_top_titleRightTextColor = new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF")};
        uiStyle.picture_top_titleRightTextSize = 14;
        uiStyle.picture_top_titleTextSize = 18;
        uiStyle.picture_top_titleArrowUpDrawable = R.drawable.picture_icon_arrow_up;
        uiStyle.picture_top_titleArrowDownDrawable = R.drawable.picture_icon_arrow_down;
        uiStyle.picture_top_titleTextColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_top_titleBarBackgroundColor = Color.parseColor("#393a3e");

        uiStyle.picture_album_textSize = 16;
        uiStyle.picture_album_backgroundStyle = R.drawable.picture_item_select_bg;
        uiStyle.picture_album_textColor = Color.parseColor("#4d4d4d");
        uiStyle.picture_album_checkDotStyle = R.drawable.picture_orange_oval;

        uiStyle.picture_bottom_previewTextSize = 14;
        uiStyle.picture_bottom_previewTextColor = new int[]{Color.parseColor("#9b9b9b"), Color.parseColor("#FA632D")};

        uiStyle.picture_bottom_preview_editorTextSize = 14;
        uiStyle.picture_bottom_preview_editorTextColor = Color.parseColor("#FFFFFF");

        uiStyle.picture_bottom_completeRedDotTextSize = 12;
        uiStyle.picture_bottom_completeTextSize = 14;
        uiStyle.picture_bottom_completeRedDotTextColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_bottom_completeRedDotBackground = R.drawable.picture_num_oval;
        uiStyle.picture_bottom_completeTextColor = new int[]{Color.parseColor("#9b9b9b"), Color.parseColor("#FA632D")};
        uiStyle.picture_bottom_barBackgroundColor = Color.parseColor("#393a3e");


        uiStyle.picture_adapter_item_camera_backgroundColor = Color.parseColor("#999999");
        uiStyle.picture_adapter_item_camera_textColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_adapter_item_camera_textSize = 14;
        uiStyle.picture_adapter_item_camera_textTopDrawable = R.drawable.picture_icon_camera;

        uiStyle.picture_adapter_item_textSize = 12;
        uiStyle.picture_adapter_item_textColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_adapter_item_video_textLeftDrawable = R.drawable.picture_icon_video;
        uiStyle.picture_adapter_item_audio_textLeftDrawable = R.drawable.picture_icon_audio;

        uiStyle.picture_bottom_originalPictureTextSize = 14;
        uiStyle.picture_bottom_originalPictureCheckStyle = R.drawable.picture_original_wechat_checkbox;
        uiStyle.picture_bottom_originalPictureTextColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_bottom_previewNormalText = R.string.picture_preview_num;
        uiStyle.picture_bottom_completeDefaultText = R.string.picture_please_select;
        uiStyle.picture_bottom_completeNormalText = R.string.picture_completed;
        uiStyle.picture_adapter_item_camera_text = R.string.picture_take_picture;
        uiStyle.picture_top_titleRightDefaultText = R.string.picture_cancel;
        uiStyle.picture_top_titleRightNormalText = R.string.picture_cancel;
        uiStyle.picture_bottom_previewDefaultText = R.string.picture_preview;
        Context appContext = PictureAppMaster.getInstance().getAppContext();
        if (appContext != null) {
            uiStyle.picture_top_titleBarHeight = ScreenUtils.dip2px(appContext, 48);
            uiStyle.picture_bottom_barHeight = ScreenUtils.dip2px(appContext, 45);
            // 如果文本内容设置(%1$d/%2$d)，请开启true
            uiStyle.isCompleteReplaceNum = true;
        }
        return uiStyle;
    }

    /**
     * 0/N样式
     *
     * @return
     */
    public static PictureSelectorUIStyle ofSelectTotalStyle() {
        PictureSelectorUIStyle uiStyle = new PictureSelectorUIStyle();

        uiStyle.picture_switchSelectTotalStyle = true;

        uiStyle.picture_statusBarChangeTextColor = true;

        uiStyle.picture_statusBarBackgroundColor = Color.parseColor("#FFFFFF");

        uiStyle.picture_container_backgroundColor = Color.parseColor("#FFFFFF");

        uiStyle.picture_navBarColor = Color.parseColor("#FFFFFF");

        uiStyle.picture_check_style = R.drawable.picture_checkbox_selector;

        uiStyle.picture_top_leftBack = R.drawable.picture_icon_back_arrow;
        uiStyle.picture_top_titleRightTextColor = new int[]{Color.parseColor("#000000"), Color.parseColor("#000000")};
        uiStyle.picture_top_titleRightTextSize = 14;
        uiStyle.picture_top_titleTextSize = 18;
        uiStyle.picture_top_titleArrowUpDrawable = R.drawable.picture_icon_orange_arrow_up;
        uiStyle.picture_top_titleArrowDownDrawable = R.drawable.picture_icon_orange_arrow_down;
        uiStyle.picture_top_titleTextColor = Color.parseColor("#000000");
        uiStyle.picture_top_titleBarBackgroundColor = Color.parseColor("#FFFFFF");

        uiStyle.picture_album_textSize = 16;
        uiStyle.picture_album_backgroundStyle = R.drawable.picture_item_select_bg;
        uiStyle.picture_album_textColor = Color.parseColor("#4d4d4d");
        uiStyle.picture_album_checkDotStyle = R.drawable.picture_orange_oval;

        uiStyle.picture_bottom_previewTextSize = 14;
        uiStyle.picture_bottom_previewTextColor = new int[]{Color.parseColor("#9b9b9b"), Color.parseColor("#FA632D")};

        uiStyle.picture_bottom_preview_editorTextSize = 14;
        uiStyle.picture_bottom_preview_editorTextColor = Color.parseColor("#4d4d4d");

        uiStyle.picture_bottom_completeTextColor = new int[]{Color.parseColor("#9b9b9b"), Color.parseColor("#FA632D")};
        uiStyle.picture_bottom_barBackgroundColor = Color.parseColor("#FAFAFA");


        uiStyle.picture_adapter_item_camera_backgroundColor = Color.parseColor("#999999");
        uiStyle.picture_adapter_item_camera_textColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_adapter_item_camera_textSize = 14;
        uiStyle.picture_adapter_item_camera_textTopDrawable = R.drawable.picture_icon_camera;

        uiStyle.picture_adapter_item_textSize = 12;
        uiStyle.picture_adapter_item_textColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_adapter_item_video_textLeftDrawable = R.drawable.picture_icon_video;
        uiStyle.picture_adapter_item_audio_textLeftDrawable = R.drawable.picture_icon_audio;

        uiStyle.picture_bottom_originalPictureTextSize = 14;
        uiStyle.picture_bottom_originalPictureCheckStyle = R.drawable.picture_original_checkbox;
        uiStyle.picture_bottom_originalPictureTextColor = Color.parseColor("#53575e");
        uiStyle.picture_bottom_previewNormalText = R.string.picture_preview_num;
        uiStyle.picture_bottom_completeDefaultText = R.string.picture_done;
        uiStyle.picture_bottom_completeNormalText = R.string.picture_done_front_num;
        uiStyle.picture_adapter_item_camera_text = R.string.picture_take_picture;
        uiStyle.picture_top_titleRightDefaultText = R.string.picture_cancel;
        uiStyle.picture_top_titleRightNormalText = R.string.picture_cancel;
        uiStyle.picture_bottom_previewDefaultText = R.string.picture_preview;
        Context appContext = PictureAppMaster.getInstance().getAppContext();
        if (appContext != null) {
            uiStyle.picture_top_titleBarHeight = ScreenUtils.dip2px(appContext, 48);
            uiStyle.picture_bottom_barHeight = ScreenUtils.dip2px(appContext, 45);
            // 如果文本内容设置(%1$d/%2$d)，请开启true
            uiStyle.isCompleteReplaceNum = true;

        }

        return uiStyle;
    }

    /**
     * 数字风格样式
     *
     * @return
     */
    public static PictureSelectorUIStyle ofSelectNumberStyle() {
        PictureSelectorUIStyle uiStyle = new PictureSelectorUIStyle();

        uiStyle.picture_statusBarBackgroundColor = Color.parseColor("#7D7DFF");

        uiStyle.picture_container_backgroundColor = Color.parseColor("#FFFFFF");

        uiStyle.picture_switchSelectNumberStyle = true;

        uiStyle.picture_navBarColor = Color.parseColor("#7D7DFF");

        uiStyle.picture_check_style = R.drawable.picture_checkbox_num_selector;

        uiStyle.picture_top_leftBack = R.drawable.picture_icon_back;

        uiStyle.picture_top_titleRightTextColor = new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF")};
        uiStyle.picture_top_titleRightTextSize = 14;
        uiStyle.picture_top_titleTextSize = 18;
        uiStyle.picture_top_titleArrowUpDrawable = R.drawable.picture_icon_arrow_up;
        uiStyle.picture_top_titleArrowDownDrawable = R.drawable.picture_icon_arrow_down;
        uiStyle.picture_top_titleTextColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_top_titleBarBackgroundColor = Color.parseColor("#7D7DFF");

        uiStyle.picture_album_textSize = 16;
        uiStyle.picture_album_backgroundStyle = R.drawable.picture_item_select_bg;
        uiStyle.picture_album_textColor = Color.parseColor("#4d4d4d");
        uiStyle.picture_album_checkDotStyle = R.drawable.picture_num_oval_blue;

        uiStyle.picture_bottom_previewTextSize = 14;
        uiStyle.picture_bottom_previewTextColor = new int[]{Color.parseColor("#9b9b9b"), Color.parseColor("#7D7DFF")};

        uiStyle.picture_bottom_preview_editorTextSize = 14;
        uiStyle.picture_bottom_preview_editorTextColor = Color.parseColor("#4d4d4d");

        uiStyle.picture_bottom_completeRedDotTextSize = 12;
        uiStyle.picture_bottom_completeTextSize = 14;
        uiStyle.picture_bottom_completeRedDotTextColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_bottom_completeRedDotBackground = R.drawable.picture_num_oval_blue;
        uiStyle.picture_bottom_completeTextColor = new int[]{Color.parseColor("#9b9b9b"), Color.parseColor("#7D7DFF")};
        uiStyle.picture_bottom_barBackgroundColor = Color.parseColor("#FFFFFF");


        uiStyle.picture_adapter_item_camera_backgroundColor = Color.parseColor("#999999");
        uiStyle.picture_adapter_item_camera_textColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_adapter_item_camera_textSize = 14;
        uiStyle.picture_adapter_item_camera_textTopDrawable = R.drawable.picture_icon_camera;

        uiStyle.picture_adapter_item_textSize = 12;
        uiStyle.picture_adapter_item_textColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_adapter_item_video_textLeftDrawable = R.drawable.picture_icon_video;
        uiStyle.picture_adapter_item_audio_textLeftDrawable = R.drawable.picture_icon_audio;

        uiStyle.picture_bottom_originalPictureTextSize = 14;
        uiStyle.picture_bottom_originalPictureCheckStyle = R.drawable.picture_original_blue_checkbox;
        uiStyle.picture_bottom_originalPictureTextColor = Color.parseColor("#7D7DFF");
        uiStyle.picture_bottom_previewNormalText = R.string.picture_preview_num;
        uiStyle.picture_bottom_completeDefaultText = R.string.picture_please_select;
        uiStyle.picture_bottom_completeNormalText = R.string.picture_completed;
        uiStyle.picture_adapter_item_camera_text = R.string.picture_take_picture;
        uiStyle.picture_top_titleRightDefaultText = R.string.picture_cancel;
        uiStyle.picture_top_titleRightNormalText = R.string.picture_cancel;
        uiStyle.picture_bottom_previewDefaultText = R.string.picture_preview;
        Context appContext = PictureAppMaster.getInstance().getAppContext();
        if (appContext != null) {
            uiStyle.picture_top_titleBarHeight = ScreenUtils.dip2px(appContext, 48);
            uiStyle.picture_bottom_barHeight = ScreenUtils.dip2px(appContext, 45);
            // 如果文本内容设置(%1$d/%2$d)，请开启true
            uiStyle.isCompleteReplaceNum = true;
        }
        return uiStyle;
    }


    /**
     * 新样式
     *
     * @return
     */
    public static PictureSelectorUIStyle ofNewStyle() {
        PictureSelectorUIStyle uiStyle = new PictureSelectorUIStyle();

        uiStyle.isNewSelectStyle = true;

        uiStyle.picture_statusBarBackgroundColor = Color.parseColor("#393a3e");

        uiStyle.picture_container_backgroundColor = Color.parseColor("#000000");

        uiStyle.picture_switchSelectNumberStyle = true;

        uiStyle.picture_navBarColor = Color.parseColor("#393a3e");

        uiStyle.picture_check_style = R.drawable.picture_wechat_num_selector;

        uiStyle.picture_top_leftBack = R.drawable.picture_icon_close;

        uiStyle.picture_top_titleRightTextColor = new int[]{Color.parseColor("#53575e"), Color.parseColor("#FFFFFF")};

        uiStyle.picture_top_titleRightTextSize = 14;
        uiStyle.picture_top_titleTextSize = 18;
        uiStyle.picture_top_titleArrowUpDrawable = R.drawable.picture_icon_wechat_up;
        uiStyle.picture_top_titleArrowDownDrawable = R.drawable.picture_icon_wechat_down;
        uiStyle.picture_top_titleTextColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_top_titleBarBackgroundColor = Color.parseColor("#393a3e");
        uiStyle.picture_top_titleAlbumBackground = R.drawable.picture_album_bg;

        uiStyle.picture_album_textSize = 16;
        uiStyle.picture_album_backgroundStyle = R.drawable.picture_item_select_bg;
        uiStyle.picture_album_textColor = Color.parseColor("#4d4d4d");
        uiStyle.picture_album_checkDotStyle = R.drawable.picture_orange_oval;

        uiStyle.picture_bottom_previewTextSize = 16;
        uiStyle.picture_bottom_previewTextColor = new int[]{Color.parseColor("#9b9b9b"), Color.parseColor("#FFFFFF")};

        uiStyle.picture_bottom_preview_editorTextSize = 16;
        uiStyle.picture_bottom_preview_editorTextColor = Color.parseColor("#FFFFFF");

        uiStyle.picture_bottom_completeTextColor = new int[]{Color.parseColor("#9b9b9b"), Color.parseColor("#FA632D")};
        uiStyle.picture_bottom_barBackgroundColor = Color.parseColor("#F2393a3e");

        uiStyle.picture_adapter_item_camera_backgroundColor = Color.parseColor("#999999");
        uiStyle.picture_adapter_item_camera_textColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_adapter_item_camera_textSize = 14;
        uiStyle.picture_adapter_item_camera_textTopDrawable = R.drawable.picture_icon_camera;

        uiStyle.picture_adapter_item_textSize = 12;
        uiStyle.picture_adapter_item_textColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_adapter_item_video_textLeftDrawable = R.drawable.picture_icon_video;
        uiStyle.picture_adapter_item_audio_textLeftDrawable = R.drawable.picture_icon_audio;

        uiStyle.picture_bottom_originalPictureTextSize = 14;
        uiStyle.picture_bottom_originalPictureCheckStyle = R.drawable.picture_original_wechat_checkbox;
        uiStyle.picture_bottom_originalPictureTextColor = Color.parseColor("#FFFFFF");
        uiStyle.picture_top_titleRightTextDefaultBackground = R.drawable.picture_send_button_default_bg;
        uiStyle.picture_top_titleRightTextNormalBackground = R.drawable.picture_send_button_bg;
        Context appContext = PictureAppMaster.getInstance().getAppContext();
        if (appContext != null) {
            uiStyle.picture_top_titleBarHeight = ScreenUtils.dip2px(appContext, 48);
            uiStyle.picture_top_titleRightDefaultText = R.string.picture_send;
            uiStyle.picture_top_titleRightNormalText = R.string.picture_cancel;
            uiStyle.picture_bottom_barHeight = ScreenUtils.dip2px(appContext, 45);
            uiStyle.picture_bottom_previewDefaultText = R.string.picture_preview;
            uiStyle.picture_bottom_previewNormalText = R.string.picture_preview_num;
            uiStyle.picture_bottom_completeDefaultText = R.string.picture_please_select;
            uiStyle.picture_bottom_completeNormalText = R.string.picture_completed;
            uiStyle.picture_adapter_item_camera_text = R.string.picture_take_picture;
            uiStyle.picture_bottom_selectedText = R.string.picture_select;
            uiStyle.picture_bottom_selectedCheckStyle = R.drawable.picture_wechat_select_cb;
            // 如果文本内容设置(%1$d/%2$d)，请开启true
            uiStyle.isCompleteReplaceNum = true;
            uiStyle.picture_top_titleArrowLeftPadding = ScreenUtils.dip2px(appContext, 3);
            uiStyle.picture_bottom_selectedTextColor = Color.parseColor("#FFFFFF");
            uiStyle.picture_bottom_selectedTextSize = 16;
            uiStyle.picture_bottom_gallery_height = ScreenUtils.dip2px(appContext, 80);
            uiStyle.picture_bottom_gallery_backgroundColor = Color.parseColor("#F2393a3e");
            uiStyle.picture_bottom_gallery_dividerColor = Color.parseColor("#666666");
            uiStyle.picture_bottom_gallery_frameBackground = R.drawable.picture_preview_gallery_border_bg;
        }

        return uiStyle;
    }

}
