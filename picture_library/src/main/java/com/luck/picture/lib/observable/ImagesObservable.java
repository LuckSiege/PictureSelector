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
    private List<LocalMedia> data;
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
     * @param data
     */
    public void savePreviewMediaData(List<LocalMedia> data) {
        this.data = data;
    }

    /**
     * 读取预览的图片
     */
    public List<LocalMedia> readPreviewMediaData() {
        return data == null ? new ArrayList<>() : data;
    }

    /**
     * 清空预览的图片
     */
    public void clearPreviewMediaData() {
        if (data != null) {
            data.clear();
        }
    }
}