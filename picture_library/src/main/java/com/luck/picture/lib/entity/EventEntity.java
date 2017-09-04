package com.luck.picture.lib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.model
 * email：893855882@qq.com
 * data：2017/4/28
 */

public class EventEntity implements Parcelable {
    public int what;
    public int position;
    public List<LocalMedia> medias = new ArrayList<>();

    public EventEntity() {
        super();
    }

    public EventEntity(int what) {
        super();
        this.what = what;
    }

    public EventEntity(int what, List<LocalMedia> medias) {
        super();
        this.what = what;
        this.medias = medias;
    }

    public EventEntity(int what, int position) {
        super();
        this.what = what;
        this.position = position;
    }

    public EventEntity(int what, List<LocalMedia> medias, int position) {
        super();
        this.what = what;
        this.position = position;
        this.medias = medias;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.what);
        dest.writeInt(this.position);
        dest.writeTypedList(this.medias);
    }

    protected EventEntity(Parcel in) {
        this.what = in.readInt();
        this.position = in.readInt();
        this.medias = in.createTypedArrayList(LocalMedia.CREATOR);
    }

    public static final Parcelable.Creator<EventEntity> CREATOR = new Parcelable.Creator<EventEntity>() {
        @Override
        public EventEntity createFromParcel(Parcel source) {
            return new EventEntity(source);
        }

        @Override
        public EventEntity[] newArray(int size) {
            return new EventEntity[size];
        }
    };
}
