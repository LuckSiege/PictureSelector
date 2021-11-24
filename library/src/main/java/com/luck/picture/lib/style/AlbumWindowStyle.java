package com.luck.picture.lib.style;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author：luck
 * @date：2021/11/16 7:56 下午
 * @describe：AlbumWindowStyle
 */
public class AlbumWindowStyle implements Parcelable {
    /**
     * 专辑列表item背景色值
     */
    private int albumAdapterItemBackground;
    /**
     * 专辑列表选中样式
     */
    private int albumAdapterItemSelectStyle;
    /**
     * 专辑名称字体大小
     */
    private int albumAdapterItemTitleSize;
    /**
     * 专辑名称字体色值
     */
    private int albumAdapterItemTitleColor;

    public AlbumWindowStyle() {

    }

    protected AlbumWindowStyle(Parcel in) {
        albumAdapterItemBackground = in.readInt();
        albumAdapterItemSelectStyle = in.readInt();
        albumAdapterItemTitleSize = in.readInt();
        albumAdapterItemTitleColor = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(albumAdapterItemBackground);
        dest.writeInt(albumAdapterItemSelectStyle);
        dest.writeInt(albumAdapterItemTitleSize);
        dest.writeInt(albumAdapterItemTitleColor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlbumWindowStyle> CREATOR = new Creator<AlbumWindowStyle>() {
        @Override
        public AlbumWindowStyle createFromParcel(Parcel in) {
            return new AlbumWindowStyle(in);
        }

        @Override
        public AlbumWindowStyle[] newArray(int size) {
            return new AlbumWindowStyle[size];
        }
    };

    public int getAlbumAdapterItemBackground() {
        return albumAdapterItemBackground;
    }

    public void setAlbumAdapterItemBackground(int albumAdapterItemBackground) {
        this.albumAdapterItemBackground = albumAdapterItemBackground;
    }

    public int getAlbumAdapterItemSelectStyle() {
        return albumAdapterItemSelectStyle;
    }

    public void setAlbumAdapterItemSelectStyle(int albumAdapterItemSelectStyle) {
        this.albumAdapterItemSelectStyle = albumAdapterItemSelectStyle;
    }

    public int getAlbumAdapterItemTitleSize() {
        return albumAdapterItemTitleSize;
    }

    public void setAlbumAdapterItemTitleSize(int albumAdapterItemTitleSize) {
        this.albumAdapterItemTitleSize = albumAdapterItemTitleSize;
    }

    public int getAlbumAdapterItemTitleColor() {
        return albumAdapterItemTitleColor;
    }

    public void setAlbumAdapterItemTitleColor(int albumAdapterItemTitleColor) {
        this.albumAdapterItemTitleColor = albumAdapterItemTitleColor;
    }
}
