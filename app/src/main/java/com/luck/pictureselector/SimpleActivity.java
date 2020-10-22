package com.luck.pictureselector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.util.LoadDialogUtils;
import com.yalantis.ucrop.util.ScreenUtils;
import com.yalantis.ucrop.view.widget.CustomLoadingDialog;

public class SimpleActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_activity, btn_fragment, btn_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        btn_activity = findViewById(R.id.btn_activity);
        btn_fragment = findViewById(R.id.btn_fragment);
        btn_dialog = findViewById(R.id.btn_dialog);
        btn_activity.setOnClickListener(this);
        btn_fragment.setOnClickListener(this);
        btn_dialog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity:
                startActivity(new Intent(SimpleActivity.this, MainActivity.class));
                break;
            case R.id.btn_fragment:
                startActivity(new Intent(SimpleActivity.this, PhotoFragmentActivity.class));
                break;
            case R.id.btn_dialog:
                View view = getLayoutInflater().inflate(R.layout.ucrop_dialog_loading, null);
                CustomLoadingDialog mMyDialog = new CustomLoadingDialog(this,
                        ScreenUtils.dip2px(this,228),
                        ScreenUtils.dip2px(this,108),
                        view,
                        R.style.ucrop_DialogStyle);
                mMyDialog.setCancelable(true);
                mMyDialog.show();
                break;
            default:
                break;
        }
    }
}
