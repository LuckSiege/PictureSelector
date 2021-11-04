package com.luck.pictureselector.style;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.luck.picture.lib.app.IApp;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureSelectorUIStyle;
import com.luck.pictureselector.R;

public class PictureUtils {

	public static PictureSelectorUIStyle ofDoctalkStyle () {
		PictureSelectorUIStyle uiStyle = new PictureSelectorUIStyle();

		IApp app = PictureAppMaster.getInstance().getApp();
		Context context = app.getAppContext();
		if (context != null) {

			uiStyle.picture_statusBarBackgroundColor = ContextCompat.getColor(context, R.color.colorAccentDark);
			uiStyle.picture_container_backgroundColor = ContextCompat.getColor(context, R.color.background_white);

			//			uiStyle.picture_navBarColor = ContextCompat.getColor(context, R.color.background_gray);

			uiStyle.picture_check_style = R.drawable.selector_check;

			uiStyle.picture_top_leftBack = R.drawable.ic_back_white_normal;
			uiStyle.picture_top_titleRightTextColor = new int[]{ContextCompat.getColor(context, R.color.text_white), ContextCompat.getColor(context, R.color.text_white)};
			uiStyle.picture_top_titleRightTextSize = 15;
			uiStyle.picture_top_titleTextSize = 19;
			uiStyle.picture_top_titleArrowUpDrawable = R.drawable.picture_icon_arrow_up;
			uiStyle.picture_top_titleArrowDownDrawable = R.drawable.picture_icon_arrow_down;
			uiStyle.picture_top_titleTextColor = ContextCompat.getColor(context, R.color.text_white);
			uiStyle.picture_top_titleBarBackgroundColor = ContextCompat.getColor(context, R.color.colorAccent);

			uiStyle.picture_album_textSize = 15;
			uiStyle.picture_album_backgroundStyle = R.drawable.picture_item_select_bg;
			uiStyle.picture_album_textColor = ContextCompat.getColor(context, R.color.text_black);
			uiStyle.picture_album_checkDotStyle = R.drawable.shape_circle_select;

			uiStyle.picture_bottom_previewTextSize = 13;
			uiStyle.picture_bottom_previewTextColor = new int[]{ContextCompat.getColor(context, R.color.text_gray), ContextCompat.getColor(context, R.color.text_black)};

			uiStyle.picture_bottom_completeRedDotTextSize = 11;
			uiStyle.picture_bottom_completeTextSize = 13;
			uiStyle.picture_bottom_completeRedDotTextColor = ContextCompat.getColor(context, R.color.text_white);
			uiStyle.picture_bottom_completeRedDotBackground = R.drawable.shape_circle_select;
			uiStyle.picture_bottom_completeTextColor = new int[]{ContextCompat.getColor(context, R.color.text_gray), ContextCompat.getColor(context, R.color.text_black)};
			uiStyle.picture_bottom_barBackgroundColor = ContextCompat.getColor(context, R.color.background_white);

			uiStyle.picture_adapter_item_camera_backgroundColor = ContextCompat.getColor(context, R.color.background_gray);
			uiStyle.picture_adapter_item_camera_textColor = ContextCompat.getColor(context, R.color.text_white);
			uiStyle.picture_adapter_item_camera_textSize = 15;
			uiStyle.picture_adapter_item_camera_textTopDrawable = R.drawable.ic_camera;

			uiStyle.picture_adapter_item_textSize = 13;
			uiStyle.picture_adapter_item_textColor = ContextCompat.getColor(context, R.color.text_white);
			uiStyle.picture_adapter_item_video_textLeftDrawable = R.drawable.picture_icon_video;
			uiStyle.picture_adapter_item_audio_textLeftDrawable = R.drawable.picture_icon_audio;

			uiStyle.picture_bottom_originalPictureTextSize = 13;
			uiStyle.picture_bottom_originalPictureCheckStyle = R.drawable.picture_original_wechat_checkbox;
			uiStyle.picture_bottom_originalPictureTextColor = ContextCompat.getColor(context, R.color.text_white);
			uiStyle.picture_bottom_previewNormalText = R.string.picture_preview_num;
			uiStyle.picture_bottom_originalPictureText = R.string.picture_original_image;
			uiStyle.picture_bottom_completeDefaultText = R.string.picture_please_select;
			uiStyle.picture_bottom_completeNormalText = R.string.picture_completed;
			uiStyle.picture_adapter_item_camera_text = R.string.picture_take_picture;
			uiStyle.picture_top_titleRightDefaultText = R.string.picture_done;
			uiStyle.picture_bottom_previewDefaultText = R.string.picture_preview;

			// 如果文本内容设置(%1$d/%2$d)，请开启true
			uiStyle.isCompleteReplaceNum = true;
		}
		return uiStyle;
	}


	private PictureParameterStyle getNumStyle () {
		// 相册主题
		PictureParameterStyle pictureParameterStyle = new PictureParameterStyle();

		IApp app = PictureAppMaster.getInstance().getApp();
		Context context = app.getAppContext();
		if (context != null) {
			// 是否改变状态栏字体颜色(黑白切换)
			pictureParameterStyle.isChangeStatusBarFontColor = false;
			// 是否开启右下角已完成(0/9)风格
			pictureParameterStyle.isOpenCompletedNumStyle = false;
			// 是否开启类似QQ相册带数字选择风格
			pictureParameterStyle.isOpenCheckNumStyle = true;
			// 相册状态栏背景色
			pictureParameterStyle.pictureStatusBarColor = Color.parseColor("#7D7DFF");
			// 相册列表标题栏背景色
			pictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#7D7DFF");
			// 相册列表标题栏右侧上拉箭头
			pictureParameterStyle.pictureTitleUpResId = R.drawable.picture_icon_arrow_up;
			// 相册列表标题栏右侧下拉箭头
			pictureParameterStyle.pictureTitleDownResId = R.drawable.picture_icon_arrow_down;
			// 相册文件夹列表选中圆点
			pictureParameterStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
			// 相册返回箭头
			pictureParameterStyle.pictureLeftBackIcon = R.drawable.picture_icon_back;
			// 标题栏字体颜色
			pictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(context, R.color.app_color_white);
			// 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
			pictureParameterStyle.pictureCancelTextColor = ContextCompat.getColor(context, R.color.app_color_white);
			// 选择相册目录背景样式
			pictureParameterStyle.pictureAlbumStyle = R.drawable.picture_new_item_select_bg;
			// 相册列表勾选图片样式
			pictureParameterStyle.pictureCheckedStyle = R.drawable.picture_checkbox_num_selector;
			// 相册列表底部背景色
			pictureParameterStyle.pictureBottomBgColor = ContextCompat.getColor(context, R.color.picture_color_fa);
			// 已选数量圆点背景样式
			pictureParameterStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval_blue;
			// 相册列表底下预览文字色值(预览按钮可点击时的色值)
			pictureParameterStyle.picturePreviewTextColor = ContextCompat.getColor(context, R.color.picture_color_blue);
			// 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
			pictureParameterStyle.pictureUnPreviewTextColor = ContextCompat.getColor(context, R.color.app_color_blue);
			// 相册列表已完成色值(已完成 可点击色值)
			pictureParameterStyle.pictureCompleteTextColor = ContextCompat.getColor(context, R.color.app_color_blue);
			// 相册列表未完成色值(请选择 不可点击色值)
			pictureParameterStyle.pictureUnCompleteTextColor = ContextCompat.getColor(context, R.color.app_color_blue);
			// 预览界面底部背景色
			pictureParameterStyle.picturePreviewBottomBgColor = ContextCompat.getColor(context, R.color.picture_color_fa);
			// 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
			pictureParameterStyle.pictureOriginalControlStyle = R.drawable.picture_original_blue_checkbox;
			// 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
			pictureParameterStyle.pictureOriginalFontColor = ContextCompat.getColor(context, R.color.app_color_blue);
			// 外部预览界面删除按钮样式
			pictureParameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
			// 外部预览界面是否显示删除按钮
			pictureParameterStyle.pictureExternalPreviewGonePreviewDelete = true;
		}
		return pictureParameterStyle;
	}
}
