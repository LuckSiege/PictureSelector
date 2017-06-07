package com.luck.picture.lib.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.luck.picture.lib.R;

public class PictureDialog extends Dialog {
    public Context context;

    public PictureDialog(Context context) {
        super(context, R.style.picture_alert_dialog);
        this.context = context;
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setWindowAnimations(R.style.DialogWindowStyle);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_alert_dialog);
    }
}