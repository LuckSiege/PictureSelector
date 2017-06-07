package com.luck.picture.lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context, int width, int height, int layout,
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

	public float getDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}

}
