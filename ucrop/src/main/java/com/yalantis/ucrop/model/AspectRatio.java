package com.yalantis.ucrop.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Created by Oleksii Shliama [https://github.com/shliama] on 6/24/16.
 */
public class AspectRatio implements Parcelable {

    @Nullable
    private final String mAspectRatioTitle;
    private final float mAspectRatioX;
    private final float mAspectRatioY;

    public AspectRatio(@Nullable String aspectRatioTitle, float aspectRatioX, float aspectRatioY) {
        mAspectRatioTitle = aspectRatioTitle;
        mAspectRatioX = aspectRatioX;
        mAspectRatioY = aspectRatioY;
    }

    protected AspectRatio(Parcel in) {
        mAspectRatioTitle = in.readString();
        mAspectRatioX = in.readFloat();
        mAspectRatioY = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAspectRatioTitle);
        dest.writeFloat(mAspectRatioX);
        dest.writeFloat(mAspectRatioY);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AspectRatio> CREATOR = new Creator<AspectRatio>() {
        @Override
        public AspectRatio createFromParcel(Parcel in) {
            return new AspectRatio(in);
        }

        @Override
        public AspectRatio[] newArray(int size) {
            return new AspectRatio[size];
        }
    };

    @Nullable
    public String getAspectRatioTitle() {
        return mAspectRatioTitle;
    }

    public float getAspectRatioX() {
        return mAspectRatioX;
    }

    public float getAspectRatioY() {
        return mAspectRatioY;
    }

}
