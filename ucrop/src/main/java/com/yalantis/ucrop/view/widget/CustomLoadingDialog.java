package com.yalantis.ucrop.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.yalantis.ucrop.R;

public class CustomLoadingDialog extends Dialog {
    public CustomLoadingDialog(Context context, int width, int height, View layout, int style) {
        super(context, style);
        setContentView(layout);


        layout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomLoadingDialog.this.dismiss();
            }
        });

        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;

        params.gravity = Gravity.CENTER;

        window.setAttributes(params);
    }


}
