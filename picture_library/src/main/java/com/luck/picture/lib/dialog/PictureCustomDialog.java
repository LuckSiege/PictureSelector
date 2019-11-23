package com.luck.picture.lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class PictureCustomDialog extends Dialog {

	public PictureCustomDialog(Context context, int width, int height, int layout,
                               int style) {
		super(context, style);
		setContentView(layout);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = width;
		params.height = height;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	}
}
