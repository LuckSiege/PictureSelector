package com.luck.picture.lib.style;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @date：2019-11-22 17:24
 * @describe：相册动态样式参数设置 <p>
 * {@link PictureSelectorUIStyle}
 * </p>
 */
public class PictureParameterStyle {
    /**
     * 是否改变状态栏字体颜色 黑白切换
     */
    public boolean isChangeStatusBarFontColor;
    /**
     * 是否开启 已完成(0/9) 模式
     */
    public boolean isOpenCompletedNumStyle;
    /**
     * 是否开启QQ 数字选择风格
     */
    public boolean isOpenCheckNumStyle;

    /**
     * 开启新选择风格
     */
    public boolean isNewSelectStyle;

    /**
     * 状态栏色值
     */
    @ColorInt
    public int pictureStatusBarColor;

    /**
     * 标题栏背景色
     */
    @ColorInt
    public int pictureTitleBarBackgroundColor;

    /**
     * 相册父容器背景色
     */
    @ColorInt
    public int pictureContainerBackgroundColor;

    /**
     * 相册标题色值
     */
    @ColorInt
    public int pictureTitleTextColor;

    /**
     * 相册标题字体大小
     */
    public int pictureTitleTextSize;

    /**
     * 相册取消按钮色值
     * <p>
     * {@link pictureRightDefaultTextColor or pictureRightDefaultTextColor}
     * </p>
     */
    @ColorInt
    @Deprecated
    public int pictureCancelTextColor;

    /**
     * 相册右侧按钮色值
     */
    @ColorInt
    public int pictureRightDefaultTextColor;

    /**
     * 相册右侧文字字体大小
     */
    public int pictureRightTextSize;

    /**
     * 相册右侧按钮文本
     */
    public String pictureRightDefaultText;

    /**
     * 相册右侧按钮色值
     */
    @ColorInt
    public int pictureRightSelectedTextColor;

    /**
     * 相册列表底部背景色
     */
    @ColorInt
    public int pictureBottomBgColor;

    /**
     * 相册列表已完成按钮色值
     */
    @ColorInt
    public int pictureCompleteTextColor;

    /**
     * 相册列表未完成按钮色值
     */
    @ColorInt
    public int pictureUnCompleteTextColor;

    /**
     * 相册列表完成按钮字体大小
     */
    public int pictureCompleteTextSize;

    /**
     * 相册列表不可预览文字颜色
     */
    @ColorInt
    public int pictureUnPreviewTextColor;

    /**
     * 相册列表预览文字大小
     */
    public int picturePreviewTextSize;

    /**
     * 图片预览编辑文字大小
     */
    public int picturePreviewEditorTextSize;

    /**
     * 图片预览编辑文字颜色
     */
    public int picturePreviewEditorTextColor;

    /**
     * 相册列表未完成按钮文本
     */
    public String pictureUnCompleteText;

    /**
     * 相册列表已完成按钮文本
     */
    public String pictureCompleteText;

    /**
     * 相册列表预览文字颜色
     */
    @ColorInt
    public int picturePreviewTextColor;

    /**
     * 相册列表不可预览文字
     */
    public String pictureUnPreviewText;

    /**
     * 相册列表预览文字
     */
    public String picturePreviewText;

    /**
     * 相册列表预览界面底部背景色
     */
    @ColorInt
    public int picturePreviewBottomBgColor;

    /**
     * # SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
     * 相册导航条颜色
     */
    @ColorInt
    public int pictureNavBarColor;

    /**
     * 原图字体颜色
     */
    @ColorInt
    public int pictureOriginalFontColor;

    /**
     * 原图字体大小
     */
    public int pictureOriginalTextSize;

    /**
     * 相册右侧按钮不可点击背景样式
     */
    @DrawableRes
    public int pictureUnCompleteBackgroundStyle;

    /**
     * 相册右侧按钮可点击背景样式
     */
    @DrawableRes
    public int pictureCompleteBackgroundStyle;

    /**
     * 相册标题右侧箭头
     */
    @DrawableRes
    public int pictureTitleUpResId;
    /**
     * 相册标题右侧箭头
     */
    @DrawableRes
    public int pictureTitleDownResId;

    /**
     * 相册返回图标
     */
    @DrawableRes
    public int pictureLeftBackIcon;

    /**
     * 相册勾选CheckBox drawable样式
     */
    @DrawableRes
    public int pictureCheckedStyle;

    /**
     * 是否使用(%1$d/%2$d)字符串
     */
    public boolean isCompleteReplaceNum;

    /**
     * 文件夹字体颜色
     */
    public int folderTextColor;

    /**
     * 文件夹字体大小
     */
    public int folderTextSize;

    /**
     * WeChatStyle 预览右下角 勾选CheckBox drawable样式
     */
    @DrawableRes
    public int pictureWeChatChooseStyle;

    /**
     * WeChatStyle 预览界面返回键样式
     */
    @DrawableRes
    public int pictureWeChatLeftBackStyle;

    /**
     * WeChatStyle 相册界面标题背景样式
     */
    @DrawableRes
    public int pictureWeChatTitleBackgroundStyle;

    /**
     * WeChatStyle 自定义预览页右下角选择文字大小
     */
    public int pictureWeChatPreviewSelectedTextSize;

    /**
     * WeChatStyle 自定义预览页右下角选择文字文案
     */
    public String pictureWeChatPreviewSelectedText;

    /**
     * 图片已选数量圆点背景色
     */
    @DrawableRes
    public int pictureCheckNumBgStyle;

    /**
     * 相册文件夹列表选中圆点
     */
    @DrawableRes
    public int pictureFolderCheckedDotStyle;

    /**
     * 外部预览图片删除按钮样式
     */
    @DrawableRes
    public int pictureExternalPreviewDeleteStyle;

    /**
     * 原图勾选样式
     */
    @DrawableRes
    public int pictureOriginalControlStyle;


    /**
     * 外部预览图片是否显示删除按钮
     */
    public boolean pictureExternalPreviewGonePreviewDelete;


    /**
     * 选择相册目录背景样式
     */
    @DrawableRes
    public int pictureAlbumStyle;

    /**
     * 标题栏高度
     */
    public int pictureTitleBarHeight;

    /**
     * 标题栏右侧按钮方向箭头left Padding
     */
    public int pictureTitleRightArrowLeftPadding;

    /**
     * 图片已编辑过的标识icon
     */
    public int picture_adapter_item_editor_tag_icon;


    public PictureParameterStyle() {
        super();
    }

    /**
     * 默认主题
     *
     * @return
     */
    public static PictureParameterStyle ofDefaultStyle() {
        PictureParameterStyle uiStyle = new PictureParameterStyle();
        // 是否改变状态栏字体颜色(黑白切换)
        uiStyle.isChangeStatusBarFontColor = false;
        // 是否开启右下角已完成(0/9)风格
        uiStyle.isOpenCompletedNumStyle = false;
        // 是否开启类似QQ相册带数字选择风格
        uiStyle.isOpenCheckNumStyle = false;
        // 相册状态栏背景色
        uiStyle.pictureStatusBarColor = Color.parseColor("#393a3e");
        // 相册列表标题栏背景色
        uiStyle.pictureTitleBarBackgroundColor = Color.parseColor("#393a3e");
        // 相册父容器背景色
        uiStyle.pictureContainerBackgroundColor = Color.parseColor("#000000");
        // 相册列表标题栏右侧上拉箭头
        uiStyle.pictureTitleUpResId = R.drawable.picture_icon_arrow_up;
        // 相册列表标题栏右侧下拉箭头
        uiStyle.pictureTitleDownResId = R.drawable.picture_icon_arrow_down;
        // 相册文件夹列表选中圆点
        uiStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        // 相册返回箭头
        uiStyle.pictureLeftBackIcon = R.drawable.picture_icon_back;
        // 标题栏字体颜色
        uiStyle.pictureTitleTextColor = Color.parseColor("#FFFFFF");
        // 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        uiStyle.pictureCancelTextColor = Color.parseColor("#FFFFFF");
        // 选择相册目录背景样式
        uiStyle.pictureAlbumStyle = R.drawable.picture_item_select_bg;
        // 相册列表勾选图片样式
        uiStyle.pictureCheckedStyle = R.drawable.picture_checkbox_selector;
        // 相册列表底部背景色
        uiStyle.pictureBottomBgColor = Color.parseColor("#393a3e");
        // 已选数量圆点背景样式
        uiStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        uiStyle.picturePreviewTextColor = Color.parseColor("#FA632D");
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        uiStyle.pictureUnPreviewTextColor = Color.parseColor("#FFFFFF");
        // 相册列表已完成色值(已完成 可点击色值)
        uiStyle.pictureCompleteTextColor = Color.parseColor("#FA632D");
        // 相册列表未完成色值(请选择 不可点击色值)
        uiStyle.pictureUnCompleteTextColor = Color.parseColor("#FFFFFF");
        // 预览界面底部背景色
        uiStyle.picturePreviewBottomBgColor = Color.parseColor("#393a3e");
        // 外部预览界面删除按钮样式
        uiStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        uiStyle.pictureOriginalControlStyle = R.drawable.picture_original_wechat_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        uiStyle.pictureOriginalFontColor = Color.parseColor("#FFFFFF");
        // 外部预览界面是否显示删除按钮
        uiStyle.pictureExternalPreviewGonePreviewDelete = true;
        // 设置NavBar Color SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
        uiStyle.pictureNavBarColor = Color.parseColor("#393a3e");

        return uiStyle;
    }

    /**
     * 0/N主题
     *
     * @return
     */
    public static PictureParameterStyle ofSelectTotalStyle() {
        PictureParameterStyle uiStyle = new PictureParameterStyle();
        // 是否改变状态栏字体颜色(黑白切换)
        uiStyle.isChangeStatusBarFontColor = true;
        // 是否开启右下角已完成(0/9)风格
        uiStyle.isOpenCompletedNumStyle = true;
        // 是否开启类似QQ相册带数字选择风格
        uiStyle.isOpenCheckNumStyle = false;
        // 相册状态栏背景色
        uiStyle.pictureStatusBarColor = Color.parseColor("#FFFFFF");
        // 相册列表标题栏背景色
        uiStyle.pictureTitleBarBackgroundColor = Color.parseColor("#FFFFFF");
        // 相册列表标题栏右侧上拉箭头
        uiStyle.pictureTitleUpResId = R.drawable.picture_icon_orange_arrow_up;
        // 相册列表标题栏右侧下拉箭头
        uiStyle.pictureTitleDownResId = R.drawable.picture_icon_orange_arrow_down;
        // 相册文件夹列表选中圆点
        uiStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        // 相册返回箭头
        uiStyle.pictureLeftBackIcon = R.drawable.picture_icon_back_arrow;
        // 标题栏字体颜色
        uiStyle.pictureTitleTextColor = Color.parseColor("#000000");
        // 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        uiStyle.pictureCancelTextColor = Color.parseColor("#000000");
        // 选择相册目录背景样式
        uiStyle.pictureAlbumStyle = R.drawable.picture_item_select_bg;
        // 相册列表勾选图片样式
        uiStyle.pictureCheckedStyle = R.drawable.picture_checkbox_selector;
        // 相册列表底部背景色
        uiStyle.pictureBottomBgColor = Color.parseColor("#FAFAFA");
        // 已选数量圆点背景样式
        uiStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        uiStyle.picturePreviewTextColor = Color.parseColor("#FA632D");
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        uiStyle.pictureUnPreviewTextColor = Color.parseColor("#9b9b9b");
        // 相册列表已完成色值(已完成 可点击色值)
        uiStyle.pictureCompleteTextColor = Color.parseColor("#FA632D");
        // 相册列表未完成色值(请选择 不可点击色值)
        uiStyle.pictureUnCompleteTextColor = Color.parseColor("#9b9b9b");
        // 预览界面底部背景色
        uiStyle.picturePreviewBottomBgColor = Color.parseColor("#FAFAFA");
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        uiStyle.pictureOriginalControlStyle = R.drawable.picture_original_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        uiStyle.pictureOriginalFontColor = Color.parseColor("#53575e");
        // 外部预览界面删除按钮样式
        uiStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_black_delete;
        // 外部预览界面是否显示删除按钮
        uiStyle.pictureExternalPreviewGonePreviewDelete = true;
        return uiStyle;
    }

    /**
     * 数字主题
     *
     * @return
     */
    public static PictureParameterStyle ofSelectNumberStyle() {
        PictureParameterStyle uiStyle = new PictureParameterStyle();
        // 是否改变状态栏字体颜色(黑白切换)
        uiStyle.isChangeStatusBarFontColor = false;
        // 是否开启右下角已完成(0/9)风格
        uiStyle.isOpenCompletedNumStyle = false;
        // 是否开启类似QQ相册带数字选择风格
        uiStyle.isOpenCheckNumStyle = true;
        // 相册状态栏背景色
        uiStyle.pictureStatusBarColor = Color.parseColor("#7D7DFF");
        // 相册列表标题栏背景色
        uiStyle.pictureTitleBarBackgroundColor = Color.parseColor("#7D7DFF");
        // 相册列表标题栏右侧上拉箭头
        uiStyle.pictureTitleUpResId = R.drawable.picture_icon_arrow_up;
        // 相册列表标题栏右侧下拉箭头
        uiStyle.pictureTitleDownResId = R.drawable.picture_icon_arrow_down;
        // 相册文件夹列表选中圆点
        uiStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        // 相册返回箭头
        uiStyle.pictureLeftBackIcon = R.drawable.picture_icon_back;
        // 标题栏字体颜色
        uiStyle.pictureTitleTextColor = Color.parseColor("#FFFFFF");
        // 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        uiStyle.pictureCancelTextColor = Color.parseColor("#FFFFFF");
        // 选择相册目录背景样式
        uiStyle.pictureAlbumStyle = R.drawable.picture_item_select_bg;
        // 相册列表勾选图片样式
        uiStyle.pictureCheckedStyle = R.drawable.picture_checkbox_num_selector;
        // 相册列表底部背景色
        uiStyle.pictureBottomBgColor = Color.parseColor("#FAFAFA");
        // 已选数量圆点背景样式
        uiStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval_blue;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        uiStyle.picturePreviewTextColor = Color.parseColor("#7D7DFF");
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        uiStyle.pictureUnPreviewTextColor = Color.parseColor("#7D7DFF");
        // 相册列表已完成色值(已完成 可点击色值)
        uiStyle.pictureCompleteTextColor = Color.parseColor("#7D7DFF");
        // 相册列表未完成色值(请选择 不可点击色值)
        uiStyle.pictureUnCompleteTextColor = Color.parseColor("#7D7DFF");
        // 预览界面底部背景色
        uiStyle.picturePreviewBottomBgColor = Color.parseColor("#FAFAFA");
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        uiStyle.pictureOriginalControlStyle = R.drawable.picture_original_blue_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        uiStyle.pictureOriginalFontColor = Color.parseColor("#7D7DFF");
        // 外部预览界面删除按钮样式
        uiStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 外部预览界面是否显示删除按钮
        uiStyle.pictureExternalPreviewGonePreviewDelete = true;
        return uiStyle;
    }

    /**
     * 新主题，仿微信
     *
     * @return
     */
    public static PictureParameterStyle ofNewStyle() {
        PictureParameterStyle uiStyle = new PictureParameterStyle();
        // 开启新选择风格
        uiStyle.isNewSelectStyle = true;
        // 是否改变状态栏字体颜色(黑白切换)
        uiStyle.isChangeStatusBarFontColor = false;
        // 是否开启右下角已完成(0/9)风格
        uiStyle.isOpenCompletedNumStyle = false;
        // 是否开启类似QQ相册带数字选择风格
        uiStyle.isOpenCheckNumStyle = true;
        // 状态栏背景色
        uiStyle.pictureStatusBarColor = Color.parseColor("#393a3e");
        // 相册列表标题栏背景色
        uiStyle.pictureTitleBarBackgroundColor = Color.parseColor("#393a3e");
        // 相册父容器背景色
        uiStyle.pictureContainerBackgroundColor = Color.parseColor("#FFFFFF");
        // 相册列表标题栏右侧上拉箭头
        uiStyle.pictureTitleUpResId = R.drawable.picture_icon_wechat_up;
        // 相册列表标题栏右侧下拉箭头
        uiStyle.pictureTitleDownResId = R.drawable.picture_icon_wechat_down;
        // 相册文件夹列表选中圆点
        uiStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        // 相册返回箭头
        uiStyle.pictureLeftBackIcon = R.drawable.picture_icon_close;
        // 标题栏字体颜色
        uiStyle.pictureTitleTextColor = Color.parseColor("#FFFFFF");
        // 相册右侧按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        uiStyle.pictureCancelTextColor = Color.parseColor("#53575e");
        // 相册右侧按钮字体默认颜色
        uiStyle.pictureRightDefaultTextColor = Color.parseColor("#53575e");
        // 相册右侧按可点击字体颜色,只针对isWeChatStyle 为true时有效果
        uiStyle.pictureRightSelectedTextColor = Color.parseColor("#FFFFFF");
        // 相册右侧按钮背景样式,只针对isWeChatStyle 为true时有效果
        uiStyle.pictureUnCompleteBackgroundStyle = R.drawable.picture_send_button_default_bg;
        // 相册右侧按钮可点击背景样式,只针对isWeChatStyle 为true时有效果
        uiStyle.pictureCompleteBackgroundStyle = R.drawable.picture_send_button_bg;
        // 选择相册目录背景样式
        uiStyle.pictureAlbumStyle = R.drawable.picture_item_select_bg;
        // 相册列表勾选图片样式
        uiStyle.pictureCheckedStyle = R.drawable.picture_wechat_num_selector;
        // 相册标题背景样式 ,只针对isWeChatStyle 为true时有效果
        uiStyle.pictureWeChatTitleBackgroundStyle = R.drawable.picture_album_bg;
        // 微信样式 预览右下角样式 ,只针对isWeChatStyle 为true时有效果
        uiStyle.pictureWeChatChooseStyle = R.drawable.picture_wechat_select_cb;
        // 相册返回箭头 ,只针对isWeChatStyle 为true时有效果
        uiStyle.pictureWeChatLeftBackStyle = R.drawable.picture_icon_back;
        // 相册列表底部背景色
        uiStyle.pictureBottomBgColor = Color.parseColor("#393a3e");
        // 已选数量圆点背景样式
        uiStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        uiStyle.picturePreviewTextColor = Color.parseColor("#FFFFFF");
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        uiStyle.pictureUnPreviewTextColor = Color.parseColor("#9b9b9b");
        // 相册列表已完成色值(已完成 可点击色值)
        uiStyle.pictureCompleteTextColor = Color.parseColor("#FFFFFF");
        // 相册列表未完成色值(请选择 不可点击色值)
        uiStyle.pictureUnCompleteTextColor = Color.parseColor("#53575e");
        // 预览界面底部背景色
        uiStyle.picturePreviewBottomBgColor = Color.parseColor("#a0393a3e");
        // 外部预览界面删除按钮样式
        uiStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        uiStyle.pictureOriginalControlStyle = R.drawable.picture_original_wechat_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        uiStyle.pictureOriginalFontColor = Color.parseColor("#FFFFFF");
        // 外部预览界面是否显示删除按钮
        uiStyle.pictureExternalPreviewGonePreviewDelete = true;
        // 设置NavBar Color SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
        uiStyle.pictureNavBarColor = Color.parseColor("#393a3e");
        return uiStyle;
    }


}
