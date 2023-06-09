package com.luck.picture.lib.magical;

import android.os.Parcel;
import android.os.Parcelable;

public class ViewParams implements Parcelable {
    public int left;
    public int top;
    public int width;
    public int height;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.left);
        dest.writeInt(this.top);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public ViewParams() {
    }

    protected ViewParams(Parcel in) {
        this.left = in.readInt();
        this.top = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<ViewParams> CREATOR = new Creator<ViewParams>() {
        @Override
        public ViewParams createFromParcel(Parcel source) {
            return new ViewParams(source);
        }

        @Override
        public ViewParams[] newArray(int size) {
            return new ViewParams[size];
        }
    };
}
