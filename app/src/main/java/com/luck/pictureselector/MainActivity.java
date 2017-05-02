package com.luck.pictureselector;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.luck.pictureselector.adapter.GridImageAdapter;
import com.luck.pictureselector.util.FullyGridLayoutManager;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：邮箱->893855882@qq.com
 * data：16/12/31
 */

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    public static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private RadioGroup rgbs, rgbs01, rgbs0, rgbs1, rgbs2, rgbs3, rgbs4, rgbs5, rgbs6, rgbs7, rgbs8, rgbs9, rgbs10;
    private int selectMode = FunctionConfig.MODE_MULTIPLE;
    private int maxSelectNum = 9;// 图片最大可选数量
    private ImageButton minus, plus;
    private TextView tv_select_num;
    private EditText et_w, et_h, et_compress_width, et_compress_height;
    private LinearLayout ll_luban_wh;
    private boolean isShow = true;
    private int selectType = FunctionConfig.TYPE_IMAGE;
    private int copyMode = FunctionConfig.CROP_MODEL_DEFAULT;
    private boolean enablePreview = true;
    private boolean isPreviewVideo = true;
    private boolean enableCrop = true;
    private boolean theme = false;
    private boolean selectImageType = false;
    private int cropW = 0;
    private int cropH = 0;
    private int maxB = 0;
    private int compressW = 0;
    private int compressH = 0;
    private boolean isCompress = false;
    private boolean isCheckNumMode = false;
    private int compressFlag = 1;// 1 系统自带压缩 2 luban压缩
    private List<LocalMedia> selectMedia = new ArrayList<>();
    private EditText et_kb;
    private int themeStyle;
    private int previewColor, completeColor, previewBottomBgColor, bottomBgColor, checkedBoxDrawable;
    private boolean mode = false;// 启动相册模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        rgbs = (RadioGroup) findViewById(R.id.rgbs);
        rgbs01 = (RadioGroup) findViewById(R.id.rgbs01);
        rgbs0 = (RadioGroup) findViewById(R.id.rgbs0);
        rgbs1 = (RadioGroup) findViewById(R.id.rgbs1);
        rgbs2 = (RadioGroup) findViewById(R.id.rgbs2);
        rgbs3 = (RadioGroup) findViewById(R.id.rgbs3);
        rgbs4 = (RadioGroup) findViewById(R.id.rgbs4);
        rgbs5 = (RadioGroup) findViewById(R.id.rgbs5);
        rgbs6 = (RadioGroup) findViewById(R.id.rgbs6);
        rgbs7 = (RadioGroup) findViewById(R.id.rgbs7);
        rgbs8 = (RadioGroup) findViewById(R.id.rgbs8);
        rgbs9 = (RadioGroup) findViewById(R.id.rgbs9);
        et_kb = (EditText) findViewById(R.id.et_kb);
        rgbs10 = (RadioGroup) findViewById(R.id.rgbs10);
        findViewById(R.id.left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_luban_wh = (LinearLayout) findViewById(R.id.ll_luban_wh);
        et_compress_width = (EditText) findViewById(R.id.et_compress_width);
        et_compress_height = (EditText) findViewById(R.id.et_compress_height);
        et_w = (EditText) findViewById(R.id.et_w);
        et_h = (EditText) findViewById(R.id.et_h);
        minus = (ImageButton) findViewById(R.id.minus);
        plus = (ImageButton) findViewById(R.id.plus);
        tv_select_num = (TextView) findViewById(R.id.tv_select_num);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(MainActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(MainActivity.this, onAddPicClickListener);
        adapter.setList(selectMedia);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        rgbs.setOnCheckedChangeListener(this);
        rgbs0.setOnCheckedChangeListener(this);
        rgbs1.setOnCheckedChangeListener(this);
        rgbs2.setOnCheckedChangeListener(this);
        rgbs3.setOnCheckedChangeListener(this);
        rgbs4.setOnCheckedChangeListener(this);
        rgbs5.setOnCheckedChangeListener(this);
        rgbs6.setOnCheckedChangeListener(this);
        rgbs7.setOnCheckedChangeListener(this);
        rgbs8.setOnCheckedChangeListener(this);
        rgbs9.setOnCheckedChangeListener(this);
        rgbs01.setOnCheckedChangeListener(this);
        rgbs10.setOnCheckedChangeListener(this);

        minus.setOnClickListener(this);
        plus.setOnClickListener(this);

        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectType) {
                    case FunctionConfig.TYPE_IMAGE:
                        // 预览图片
                        PictureConfig.getInstance().externalPicturePreview(MainActivity.this, position, selectMedia);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // 预览视频
                        if (selectMedia.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(MainActivity.this, selectMedia.get(position).getPath());
                        }
                        break;
                }

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
                     * ThemeStyle -->主题颜色
                     * CheckedBoxDrawable -->图片勾选样式
                     * cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
                     * cropH-->裁剪高度 值不能小于100
                     * isCompress -->是否压缩图片
                     * setEnablePixelCompress 是否启用像素压缩
                     * setEnableQualityCompress 是否启用质量压缩
                     * setRecordVideoSecond 录视频的秒数，默认不限制
                     * setRecordVideoDefinition 视频清晰度  Constants.HIGH 清晰  Constants.ORDINARY 低质量
                     * setImageSpanCount -->每行显示个数
                     * setCheckNumMode 是否显示QQ选择风格(带数字效果)
                     * setPreviewColor 预览文字颜色
                     * setCompleteColor 完成文字颜色
                     * setPreviewBottomBgColor 预览界面底部背景色
                     * setBottomBgColor 选择图片页面底部背景色
                     * setCompressQuality 设置裁剪质量，默认无损裁剪
                     * setSelectMedia 已选择的图片
                     * setCompressFlag 1为系统自带压缩  2为第三方luban压缩
                     * 注意-->type为2时 设置isPreview or isCrop 无效
                     * 注意：Options可以为空，默认标准模式
                     */
                    String ws = et_w.getText().toString().trim();
                    String hs = et_h.getText().toString().trim();
                    String b = et_kb.getText().toString().trim();// 压缩最大大小 单位是b

                    if (!isNull(ws) && !isNull(hs)) {
                        cropW = Integer.parseInt(ws);
                        cropH = Integer.parseInt(hs);
                    }

                    if (!isNull(b)) {
                        maxB = Integer.parseInt(b);
                    }

                    if (!isNull(et_compress_width.getText().toString()) && !isNull(et_compress_height.getText().toString())) {
                        compressW = Integer.parseInt(et_compress_width.getText().toString());
                        compressH = Integer.parseInt(et_compress_height.getText().toString());
                    }

                    if (theme) {
                        // 设置主题样式
                        themeStyle = ContextCompat.getColor(getApplicationContext(), R.color.blue);
                    } else {
                        themeStyle = ContextCompat.getColor(getApplicationContext(), R.color.bar_grey);
                    }

                    if (isCheckNumMode) {
                        // QQ 风格模式下 这里自己搭配颜色
                        previewColor = ContextCompat.getColor(getApplicationContext(), R.color.blue);
                        completeColor = ContextCompat.getColor(getApplicationContext(), R.color.blue);
                    } else {
                        previewColor = ContextCompat.getColor(getApplicationContext(), R.color.tab_color_true);
                        completeColor = ContextCompat.getColor(getApplicationContext(), R.color.tab_color_true);
                    }

                    if (selectImageType) {
                        checkedBoxDrawable = R.drawable.select_cb;
                    } else {
                        checkedBoxDrawable = 0;
                    }

                    FunctionOptions options = new FunctionOptions.Builder()
                            .setType(selectType) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                            .setCropMode(copyMode) // 裁剪模式 默认、1:1、3:4、3:2、16:9
                            .setCompress(isCompress) //是否压缩
                            .setEnablePixelCompress(true) //是否启用像素压缩
                            .setEnableQualityCompress(true) //是否启质量压缩
                            .setMaxSelectNum(maxSelectNum) // 可选择图片的数量
                            .setMinSelectNum(0)// 图片最低选择数量，默认代表无限制
                            .setSelectMode(selectMode) // 单选 or 多选
                            .setShowCamera(isShow) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
                            .setEnablePreview(enablePreview) // 是否打开预览选项
                            .setEnableCrop(enableCrop) // 是否打开剪切选项
                            .setCircularCut(true)// 是否采用圆形裁剪
                            .setPreviewVideo(isPreviewVideo) // 是否预览视频(播放) mode or 多选有效
                            .setCheckedBoxDrawable(checkedBoxDrawable)
                            .setRecordVideoDefinition(FunctionConfig.HIGH) // 视频清晰度
                            .setRecordVideoSecond(60) // 视频秒数
                            .setGif(false)// 是否显示gif图片，默认不显示
                            .setCropW(cropW) // cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
                            .setCropH(cropH) // cropH-->裁剪高度 值不能小于100 如果值大于图片原始宽高 将返回原图大小
                            .setMaxB(maxB) // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb
                            .setPreviewColor(previewColor) //预览字体颜色
                            .setCompleteColor(completeColor) //已完成字体颜色
                            .setPreviewBottomBgColor(previewBottomBgColor) //预览底部背景色
                            .setBottomBgColor(bottomBgColor) //图片列表底部背景色
                            .setGrade(Luban.THIRD_GEAR) // 压缩档次 默认三档
                            .setCheckNumMode(isCheckNumMode)
                            .setCompressQuality(100) // 图片裁剪质量,默认无损
                            .setImageSpanCount(4) // 每行个数
                            .setSelectMedia(selectMedia) // 已选图片，传入在次进去可选中，不能传入网络图片
                            .setCompressFlag(compressFlag) // 1 系统自带压缩 2 luban压缩
                            .setCompressW(compressW) // 压缩宽 如果值大于图片原始宽高无效
                            .setCompressH(compressH) // 压缩高 如果值大于图片原始宽高无效
                            .setThemeStyle(themeStyle) // 设置主题样式
                            .create();

                    if (mode) {
                        // 只拍照
                        PictureConfig.getInstance().init(options).startOpenCamera(MainActivity.this, resultCallback);
                    } else {
                        // 先初始化参数配置，在启动相册
                        PictureConfig.getInstance().init(options).openPhoto(MainActivity.this, resultCallback);
                    }
                    break;
                case 1:
                    // 删除图片
                    selectMedia.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    /**
     * 图片回调方法
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            selectMedia = resultList;
            Log.i("callBack_result", selectMedia.size() + "");
            LocalMedia media = resultList.get(0);
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                String path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                String path = media.getCompressPath();
            } else {
                // 原图地址
                String path = media.getPath();
            }
            if (selectMedia != null) {
                adapter.setList(selectMedia);
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_photo:
                mode = false;
                break;
            case R.id.rb_camera:
                mode = true;
                break;
            case R.id.rb_ordinary:
                isCheckNumMode = false;
                break;
            case R.id.rb_qq:
                isCheckNumMode = true;
                break;
            case R.id.rb_single:
                selectMode = FunctionConfig.MODE_SINGLE;
                break;
            case R.id.rb_multiple:
                selectMode = FunctionConfig.MODE_MULTIPLE;
                break;
            case R.id.rb_image:
                selectType = FunctionConfig.TYPE_IMAGE;
                break;
            case R.id.rb_video:
                selectType = FunctionConfig.TYPE_VIDEO;
                break;
            case R.id.rb_photo_display:
                isShow = true;
                break;
            case R.id.rb_photo_hide:
                isShow = false;
                break;
            case R.id.rb_default:
                copyMode = FunctionConfig.CROP_MODEL_DEFAULT;
                break;
            case R.id.rb_to1_1:
                copyMode = FunctionConfig.CROP_MODEL_1_1;
                break;
            case R.id.rb_to3_2:
                copyMode = FunctionConfig.CROP_MODEL_3_2;
                break;
            case R.id.rb_to3_4:
                copyMode = FunctionConfig.CROP_MODEL_3_4;
                break;
            case R.id.rb_to16_9:
                copyMode = FunctionConfig.CROP_MODEL_16_9;
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
            case R.id.rb_theme1:
                theme = false;
                break;
            case R.id.rb_theme2:
                theme = true;
                break;
            case R.id.rb_select1:
                selectImageType = false;
                break;
            case R.id.rb_select2:
                selectImageType = true;
                break;
            case R.id.rb_compress_false:
                isCompress = false;
                rgbs10.setVisibility(View.GONE);
                et_kb.setVisibility(View.GONE);
                ll_luban_wh.setVisibility(View.GONE);
                et_compress_height.setText("");
                et_compress_width.setText("");
                break;
            case R.id.rb_compress_true:
                isCompress = true;
                et_kb.setVisibility(View.VISIBLE);
                if (compressFlag == 2) {
                    ll_luban_wh.setVisibility(View.VISIBLE);
                }
                rgbs10.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_system:
                compressFlag = 1;
                ll_luban_wh.setVisibility(View.GONE);
                et_compress_height.setText("");
                et_compress_width.setText("");
                break;
            case R.id.rb_luban:
                compressFlag = 2;
                ll_luban_wh.setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * 判断 一个字段的值否为空
     *
     * @param s
     * @return
     * @author Michael.Zhang 2013-9-7 下午4:39:00
     */

    public boolean isNull(String s) {
        if (null == s || s.equals("") || s.equalsIgnoreCase("null")) {
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.minus:
                if (maxSelectNum > 1) {
                    maxSelectNum--;
                }
                tv_select_num.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
                break;
            case R.id.plus:
                maxSelectNum++;
                tv_select_num.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
                break;
        }
    }
}
