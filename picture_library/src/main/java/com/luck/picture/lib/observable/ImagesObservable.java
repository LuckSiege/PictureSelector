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
    //观察者接口集合
    private List<LocalMedia> previewList;
    private static ImagesObservable sObserver;

    public static ImagesObservable getInstance() {
        if (sObserver == null) {
            synchronized (ImagesObservable.class) {
                if (sObserver == null) {
                    sObserver = new ImagesObservable();
                }
            }
        }
        return sObserver;
    }

    /**
     * 存储图片用于预览时用
     *
     * @param previewList
     */
    public void savePreviewMediaData(List<LocalMedia> previewList) {
        this.previewList = previewList;
    }

    /**
     * 读取预览的图片
     */
    public List<LocalMedia> readPreviewMediaData() {
        if (previewList == null) {
            previewList = new ArrayList<>();
        }
        return previewList;
    }

    /**
     * 清空预览的图片
     */
    public void clearPreviewMediaData() {
        if (previewList != null) {
            previewList.clear();
        }
    }
}