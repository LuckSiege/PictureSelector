package com.luck.picture.lib.observable;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2017-1-12 21:30
 * @describe：解决预览时传值过大问题
 */
public class ImagesObservable {
    private List<LocalMedia> mData = new ArrayList<>();

    private static final ImagesObservable mInstance = new ImagesObservable();

    public static ImagesObservable getInstance() {
        return mInstance;
    }

    /**
     * 存储图片用于预览时用
     *
     * @param data
     */
    public void saveData(List<LocalMedia> data) {
        this.mData = data;
    }

    /**
     * 读取预览的图片
     */
    public List<LocalMedia> getData() {
        return mData;
    }

    /**
     * 清空预览的图片
     */
    public void clearData() {
        mData.clear();
    }
}