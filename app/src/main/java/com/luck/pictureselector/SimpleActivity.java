package com.luck.pictureselector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SimpleActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_activity, btn_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        btn_activity = findViewById(R.id.btn_activity);
        btn_fragment = findViewById(R.id.btn_fragment);
        btn_activity.setOnClickListener(this);
        btn_fragment.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
