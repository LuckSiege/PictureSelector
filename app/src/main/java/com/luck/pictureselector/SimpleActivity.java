package com.luck.pictureselector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SimpleActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_activity, btn_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        btn_activity = (Button) findViewById(R.id.btn_activity);
        btn_fragment = (Button) findViewById(R.id.btn_fragment);
        btn_activity.setOnClickListener(this);
        btn_fragment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_activity:
                intent = new Intent(SimpleActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_fragment:
                intent = new Intent(SimpleActivity.this, PhotoFragmentActivity.class);
                startActivity(intent);
                break;
        }
    }
}
