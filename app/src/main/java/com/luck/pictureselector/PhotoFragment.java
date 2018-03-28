package com.luck.pictureselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.pictureselector.adapter.GridImageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.pictureselector
 * email：893855882@qq.com
 * data：2017/5/30
 */

public class PhotoFragment extends Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    private final static String TAG = PhotoFragment.class.getSimpleName();
    private View rootView;
    private List<LocalMedia> selectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private int maxSelectNum = 9;
    private TextView tv_select_num;
    private ImageView left_back, minus, plus;
    private RadioGroup rgb_crop, rgb_style, rgb_photo_mode;
    private int aspect_ratio_x, aspect_ratio_y;
    private CheckBox cb_voice, cb_choose_mode, cb_isCamera, cb_isGif,
            cb_preview_img, cb_preview_video, cb_crop, cb_compress,
            cb_mode, cb_hide, cb_crop_circular, cb_styleCrop, cb_showCropGrid,
            cb_showCropFrame, cb_preview_audio;
    private int themeId;
    private int chooseMode = PictureMimeType.ofAll();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_main, container, false);
        }
        init();
        return rootView;
    }

    private void init() {
        themeId = R.style.picture_default_style;
        minus = (ImageView) rootView.findViewById(R.id.minus);
        plus = (ImageView) rootView.findViewById(R.id.plus);
        tv_select_num = (TextView) rootView.findViewById(R.id.tv_select_num);
        rgb_crop = (RadioGroup) rootView.findViewById(R.id.rgb_crop);
        rgb_style = (RadioGroup) rootView.findViewById(R.id.rgb_style);
        rgb_photo_mode = (RadioGroup) rootView.findViewById(R.id.rgb_photo_mode);
        cb_voice = (CheckBox) rootView.findViewById(R.id.cb_voice);
        cb_choose_mode = (CheckBox) rootView.findViewById(R.id.cb_choose_mode);
        cb_isCamera = (CheckBox) rootView.findViewById(R.id.cb_isCamera);
        cb_isGif = (CheckBox) rootView.findViewById(R.id.cb_isGif);
        cb_preview_img = (CheckBox) rootView.findViewById(R.id.cb_preview_img);
        cb_preview_video = (CheckBox) rootView.findViewById(R.id.cb_preview_video);
        cb_crop = (CheckBox) rootView.findViewById(R.id.cb_crop);
        cb_styleCrop = (CheckBox) rootView.findViewById(R.id.cb_styleCrop);
        cb_compress = (CheckBox) rootView.findViewById(R.id.cb_compress);
        cb_mode = (CheckBox) rootView.findViewById(R.id.cb_mode);
        cb_showCropGrid = (CheckBox) rootView.findViewById(R.id.cb_showCropGrid);
        cb_showCropFrame = (CheckBox) rootView.findViewById(R.id.cb_showCropFrame);
        cb_preview_audio = (CheckBox) rootView.findViewById(R.id.cb_preview_audio);
        cb_hide = (CheckBox) rootView.findViewById(R.id.cb_hide);
        cb_crop_circular = (CheckBox) rootView.findViewById(R.id.cb_crop_circular);
        rgb_crop.setOnCheckedChangeListener(this);
        rgb_style.setOnCheckedChangeListener(this);
        rgb_photo_mode.setOnCheckedChangeListener(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        left_back = (ImageView) rootView.findViewById(R.id.left_back);
        left_back.setOnClickListener(this);
        minus.setOnClickListener(this);
        plus.setOnClickListener(this);
        cb_crop.setOnCheckedChangeListener(this);
        cb_crop_circular.setOnCheckedChangeListener(this);
        cb_compress.setOnCheckedChangeListener(this);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(getActivity(), onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                LocalMedia media = selectList.get(position);
                String pictureType = media.getPictureType();
                int mediaType = PictureMimeType.pictureToVideo(pictureType);
                switch (mediaType) {
                    case 1:
                        // 预览图片
                        PictureSelector.create(PhotoFragment.this).themeStyle(themeId).openExternalPreview(position, selectList);
                        break;
                    case 2:
                        // 预览视频
                        PictureSelector.create(PhotoFragment.this).externalPictureVideo(media.getPath());
                        break;
                    case 3:
                        // 预览音频
                        PictureSelector.create(PhotoFragment.this).externalPictureAudio(media.getPath());
                        break;
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            boolean mode = cb_mode.isChecked();
            if (mode) {
                // 进入相册 以下是例子：不需要的api可以不写
                PictureSelector.create(PhotoFragment.this)
                        .openGallery(chooseMode)
                        .theme(themeId)
                        .maxSelectNum(maxSelectNum)
                        .minSelectNum(1)
                        .selectionMode(cb_choose_mode.isChecked() ? PictureConfig.MULTIPLE : PictureConfig.SINGLE)
                        .previewImage(cb_preview_img.isChecked())
                        .previewVideo(cb_preview_video.isChecked())
                        .enablePreviewAudio(cb_preview_audio.isChecked()) // 是否可播放音频
                        .isCamera(cb_isCamera.isChecked())
                        .enableCrop(cb_crop.isChecked())
                        .compress(cb_compress.isChecked())
                        .glideOverride(160, 160)
                        .previewEggs(true)
                        .withAspectRatio(aspect_ratio_x, aspect_ratio_y)
                        .hideBottomControls(cb_hide.isChecked() ? false : true)
                        .isGif(cb_isGif.isChecked())
                        .freeStyleCropEnabled(cb_styleCrop.isChecked())
                        .circleDimmedLayer(cb_crop_circular.isChecked())
                        .showCropFrame(cb_showCropFrame.isChecked())
                        .showCropGrid(cb_showCropGrid.isChecked())
                        .openClickSound(cb_voice.isChecked())
                        .selectionMedia(selectList)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            } else {
                // 单独拍照
                PictureSelector.create(PhotoFragment.this)
                        .openCamera(chooseMode)
                        .theme(themeId)
                        .maxSelectNum(maxSelectNum)
                        .minSelectNum(1)
                        .selectionMode(cb_choose_mode.isChecked() ? PictureConfig.MULTIPLE : PictureConfig.SINGLE)
                        .previewImage(cb_preview_img.isChecked())
                        .previewVideo(cb_preview_video.isChecked())
                        .enablePreviewAudio(cb_preview_audio.isChecked()) // 是否可播放音频
                        .isCamera(cb_isCamera.isChecked())
                        .enableCrop(cb_crop.isChecked())
                        .compress(cb_compress.isChecked())
                        .glideOverride(160, 160)
                        .withAspectRatio(aspect_ratio_x, aspect_ratio_y)
                        .hideBottomControls(cb_hide.isChecked() ? false : true)
                        .isGif(cb_isGif.isChecked())
                        .freeStyleCropEnabled(cb_styleCrop.isChecked())
                        .circleDimmedLayer(cb_crop_circular.isChecked())
                        .showCropFrame(cb_showCropFrame.isChecked())
                        .showCropGrid(cb_showCropGrid.isChecked())
                        .openClickSound(cb_voice.isChecked())
                        .selectionMedia(selectList)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择
                    selectList = PictureSelector.obtainMultipleResult(data);
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                getActivity().finish();
                break;
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

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_all:
                chooseMode = PictureMimeType.ofAll();
                cb_preview_img.setChecked(true);
                cb_preview_video.setChecked(true);
                cb_isGif.setChecked(false);
                cb_isGif.setVisibility(View.GONE);
                cb_preview_video.setChecked(true);
                cb_preview_img.setChecked(true);
                cb_preview_video.setVisibility(View.VISIBLE);
                cb_preview_img.setVisibility(View.VISIBLE);
                cb_preview_audio.setVisibility(View.GONE);
                break;
            case R.id.rb_image:
                chooseMode = PictureMimeType.ofImage();
                cb_preview_img.setChecked(true);
                cb_preview_video.setChecked(false);
                cb_isGif.setChecked(false);
                cb_isGif.setVisibility(View.VISIBLE);
                cb_preview_video.setChecked(false);
                cb_preview_video.setVisibility(View.GONE);
                cb_preview_img.setChecked(true);
                cb_preview_img.setVisibility(View.VISIBLE);
                cb_preview_audio.setVisibility(View.GONE);
                break;
            case R.id.rb_video:
                chooseMode = PictureMimeType.ofVideo();
                cb_preview_img.setChecked(false);
                cb_preview_video.setChecked(true);
                cb_isGif.setChecked(false);
                cb_isGif.setVisibility(View.GONE);
                cb_preview_video.setChecked(true);
                cb_preview_video.setVisibility(View.VISIBLE);
                cb_preview_img.setVisibility(View.GONE);
                cb_preview_audio.setVisibility(View.GONE);
                cb_preview_img.setChecked(false);
                break;
            case R.id.rb_audio:
                chooseMode = PictureMimeType.ofAudio();
                cb_preview_audio.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_crop_default:
                aspect_ratio_x = 0;
                aspect_ratio_y = 0;
                break;
            case R.id.rb_crop_1to1:
                aspect_ratio_x = 1;
                aspect_ratio_y = 1;
                break;
            case R.id.rb_crop_3to4:
                aspect_ratio_x = 3;
                aspect_ratio_y = 4;
                break;
            case R.id.rb_crop_3to2:
                aspect_ratio_x = 3;
                aspect_ratio_y = 2;
                break;
            case R.id.rb_crop_16to9:
                aspect_ratio_x = 16;
                aspect_ratio_y = 9;
                break;
            case R.id.rb_default_style:
                themeId = R.style.picture_default_style;
                break;
            case R.id.rb_white_style:
                themeId = R.style.picture_white_style;
                break;
            case R.id.rb_num_style:
                themeId = R.style.picture_QQ_style;
                break;
            case R.id.rb_sina_style:
                themeId = R.style.picture_Sina_style;
                break;
        }
    }

    private int x = 0, y = 0;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_crop:
                rgb_crop.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_hide.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_crop_circular.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                cb_styleCrop.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_showCropFrame.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_showCropGrid.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                break;
            case R.id.cb_crop_circular:
                if (isChecked) {
                    x = aspect_ratio_x;
                    y = aspect_ratio_y;
                    aspect_ratio_x = 1;
                    aspect_ratio_y = 1;
                } else {
                    aspect_ratio_x = x;
                    aspect_ratio_y = y;
                }
                rgb_crop.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                if (isChecked) {
                    cb_showCropFrame.setChecked(false);
                    cb_showCropGrid.setChecked(false);
                } else {
                    cb_showCropFrame.setChecked(true);
                    cb_showCropGrid.setChecked(true);
                }
                break;
        }
    }
}
