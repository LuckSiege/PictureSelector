package com.luck.picture.lib.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.yalantis.ucrop.UCrop;

/**
 * @author：luck
 * @date：2020-01-09 13:33
 * @describe： UCrop Configuration items
 */
public class UCropOptions extends UCrop.Options implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public UCropOptions() {
    }

    protected UCropOptions(Parcel in) {
    }

    public static final Parcelable.Creator<UCropOptions> CREATOR = new Parcelable.Creator<UCropOptions>() {
        @Override
        public UCropOptions createFromParcel(Parcel source) {
            return new UCropOptions(source);
        }

        @Override
        public UCropOptions[] newArray(int size) {
            return new UCropOptions[size];
        }
    };
}
