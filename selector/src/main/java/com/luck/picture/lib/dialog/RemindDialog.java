package com.luck.picture.lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @date：2021/11/19 5:11 下午
 * @describe：RemindDialog
 */
public class RemindDialog extends Dialog implements View.OnClickListener {

    public RemindDialog(Context context, String tips) {
        super(context, R.style.Picture_Theme_Dialog);
        setContentView(R.layout.ps_remind_dialog);
        TextView btnOk = findViewById(R.id.btnOk);
        TextView tvContent = findViewById(R.id.tv_content);
        tvContent.setText(tips);
        btnOk.setOnClickListener(this);
        setDialogSize();
    }

    public static void showTipsDialog(Context context, String tips) {
        new RemindDialog(context, tips).show();
    }

    private void setDialogSize() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        getWindow().setWindowAnimations(R.style.PictureThemeDialogWindowStyle);
        getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnOk) {
            dismiss();
        }
    }
}
