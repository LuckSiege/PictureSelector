package com.luck.pictureselector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.luck.picture.ui.AlbumDirectoryActivity;
import com.luck.picture.ui.ImageGridActivity;
import com.luck.picture.util.Constants;
import com.luck.picture.util.LocalMediaLoader;
import com.luck.picture.util.Options;
import com.luck.pictureselector.adapter.GridImageAdapter;
import com.luck.pictureselector.util.FullyGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：邮箱->893855882@qq.com
 * data：16/12/31
 */

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private List<String> images = new ArrayList<>();
    private RadioGroup rgbs0, rgbs1, rgbs2, rgbs3, rgbs4, rgbs5, rgbs6;
    private int selectMode = Constants.MODE_MULTIPLE;
    private int maxSelectNum = 9;// 图片最大可选数量
    private ImageButton minus, plus;
    private EditText select_num;
    private boolean isShow = true;
    private int selectType = LocalMediaLoader.TYPE_IMAGE;
    private int copyMode = Constants.COPY_MODEL_DEFAULT;
    private boolean enablePreview = true;
    private boolean isPreviewVideo = true;
    private boolean enableCrop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        rgbs0 = (RadioGroup) findViewById(R.id.rgbs0);
        rgbs1 = (RadioGroup) findViewById(R.id.rgbs1);
        rgbs2 = (RadioGroup) findViewById(R.id.rgbs2);
        rgbs3 = (RadioGroup) findViewById(R.id.rgbs3);
        rgbs4 = (RadioGroup) findViewById(R.id.rgbs4);
        rgbs5 = (RadioGroup) findViewById(R.id.rgbs5);
        rgbs6 = (RadioGroup) findViewById(R.id.rgbs6);
        minus = (ImageButton) findViewById(R.id.minus);
        plus = (ImageButton) findViewById(R.id.plus);
        select_num = (EditText) findViewById(R.id.select_num);
        select_num.setText(maxSelectNum + "");
        FullyGridLayoutManager manager = new FullyGridLayoutManager(MainActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(MainActivity.this, onAddPicClickListener);
        recyclerView.setAdapter(adapter);
        rgbs0.setOnCheckedChangeListener(this);
        rgbs1.setOnCheckedChangeListener(this);
        rgbs2.setOnCheckedChangeListener(this);
        rgbs3.setOnCheckedChangeListener(this);
        rgbs4.setOnCheckedChangeListener(this);
        rgbs5.setOnCheckedChangeListener(this);
        rgbs6.setOnCheckedChangeListener(this);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxSelectNum--;
                select_num.setText(maxSelectNum + "");
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxSelectNum++;
                select_num.setText(maxSelectNum + "");
            }
        });

    }

    /**
     * 删除图片回调接口
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    // 进入相册
                    /**
                     * type --> 1图片 or 2视频
                     * copyMode -->裁剪比例，默认、1:1、3:4、3:2、16:9
                     * maxSelectNum --> 可选择图片的数量
                     * selectMode         --> 单选 or 多选
                     * isShow       --> 是否显示拍照选项 这里自动根据type 启动拍照或录视频
                     * isPreview    --> 是否打开预览选项
                     * isCrop       --> 是否打开剪切选项
                     * isPreviewVideo -->是否预览视频(播放) mode or 多选有效
                     * 注意-->type为2时 设置isPreview or isCrop 无效
                     *
                     */
                    Options options = new Options();
                    options.setType(selectType);
                    options.setCopyMode(copyMode);
                    options.setMaxSelectNum(maxSelectNum);
                    options.setSelectMode(selectMode);
                    options.setShowCamera(isShow);
                    options.setEnablePreview(enablePreview);
                    options.setEnableCrop(enableCrop);
                    options.setPreviewVideo(isPreviewVideo);
                    AlbumDirectoryActivity.startPhoto(MainActivity.this, options);
                    break;
                case 1:
                    // 删除图片
                    images.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImageGridActivity.REQUEST_IMAGE:
                    images = (ArrayList<String>) data.getSerializableExtra(ImageGridActivity.REQUEST_OUTPUT);
                    if (images != null) {
                        adapter.setList(images);
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_single:
                selectMode = Constants.MODE_SINGLE;
                break;
            case R.id.rb_multiple:
                selectMode = Constants.MODE_MULTIPLE;
                break;
            case R.id.rb_image:
                selectType = LocalMediaLoader.TYPE_IMAGE;
                break;
            case R.id.rb_video:
                selectType = LocalMediaLoader.TYPE_VIDEO;
                break;
            case R.id.rb_photo_display:
                isShow = true;
                break;
            case R.id.rb_photo_hide:
                isShow = false;
                break;
            case R.id.rb_default:
                copyMode = Constants.COPY_MODEL_DEFAULT;
                break;
            case R.id.rb_to1_1:
                copyMode = Constants.COPY_MODEL_1_1;
                break;
            case R.id.rb_to3_2:
                copyMode = Constants.COPY_MODEL_3_2;
                break;
            case R.id.rb_to3_4:
                copyMode = Constants.COPY_MODEL_3_4;
                break;
            case R.id.rb_to16_9:
                copyMode = Constants.COPY_MODEL_16_9;
                break;
            case R.id.rb_preview:
                enablePreview = true;
                break;
            case R.id.rb_preview_false:
                enablePreview = false;
                break;
            case R.id.rb_preview_video:
                isPreviewVideo = true;
                break;
            case R.id.rb_preview_video_false:
                isPreviewVideo = false;
                break;
            case R.id.rb_yes_copy:
                enableCrop = true;
                break;
            case R.id.rb_no_copy:
                enableCrop = false;
                break;
        }
    }
}
