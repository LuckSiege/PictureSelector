package com.luck.pictureselector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.callback.ImageLoder;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.ImageLoderTools;

/**
 * 项目名称：PictureSelector
 * 类描述：
 * 创建人：张志华
 * 创建时间：2018/1/18 17:28@郑州卡卡罗特科技有限公司
 * 修改人：Administrator
 * 修改时间：2018/1/18 17:28
 * 修改备注：
 *
 * @version 1
 *          相关联类：
 * @see
 * @see
 */

public class MyImageLoder implements ImageLoder<ImageView>{
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .sizeMultiplier(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(160, 160);
        Glide.with(context)
                .asBitmap()
                .load(path)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView, int def) {

    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView, int def, int error) {

    }

    @Override
    public void displayImage(final Context context, Object path,final ImageView imageView, String type) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .sizeMultiplier(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(160, 160);
        if (type== ImageLoderTools.C8){
            Glide.with(context)
                    .asBitmap()
                    .load(path)
                    .apply(options)
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.
                                            create(context.getResources(), resource);
                            circularBitmapDrawable.setCornerRadius(8);
                            imageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }else {
            Glide.with(context)
                    .asBitmap()
                    .load(path)
                    .apply(options)
                    .into(imageView);
        }

    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView, String type,final BitmapLoadCallback callback) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .asBitmap()
                .load(path)
                .apply(options)
                .into(new SimpleTarget<Bitmap>(480, 800) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        callback.onFailure(new Exception("ddd"));
                    }
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        callback.onBitmapLoaded(resource,new ExifInfo(0,0,0),"",null);
                    }
                });
    }

    @Override
    public ImageView createImageView(Context context) {
        return null;
    }
}
