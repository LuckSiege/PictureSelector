package com.luck.picture.lib.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.luck.picture.lib.R;
import com.luck.picture.lib.model.FunctionConfig;
import com.yalantis.ucrop.entity.LocalMedia;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：邮箱->893855882@qq.com
 * data：17/01/18
 */
public class PictureImagePreviewFragment extends Fragment {
    public static final String PATH = "path";
    private List<LocalMedia> selectImages = new ArrayList<>();

    public static PictureImagePreviewFragment getInstance(String path, List<LocalMedia> medias) {
        PictureImagePreviewFragment fragment = new PictureImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PATH, path);
        bundle.putSerializable(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) medias);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.picture_fragment_image_preview, container, false);
        final ImageView imageView = (ImageView) contentView.findViewById(R.id.preview_image);
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
        selectImages = (List<LocalMedia>) getArguments().getSerializable(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST);
        String path = getArguments().getString(PATH);
        Glide.with(container.getContext())
                .load(path)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new SimpleTarget<Bitmap>(480, 800) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(resource);
                        mAttacher.update();
                    }
                });
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (getActivity() instanceof PicturePreviewActivity) {
                    activityFinish();
                } else {
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, R.anim.toast_out);
                }
            }
        });
        return contentView;
    }

    protected void activityFinish() {
        getActivity().setResult(getActivity().RESULT_OK, new Intent().putExtra("type", 1).putExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectImages));
        getActivity().finish();
    }
}
