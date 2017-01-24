package com.luck.picture.lib.observable;

import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.entity.LocalMediaFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.observable
 * email：893855882@qq.com
 * data：17/1/11
 */
public class ImagesObservable implements SubjectListener {
    //观察者接口集合
    private List<ObserverListener> observers = new ArrayList<>();

    private List<LocalMediaFolder> folders;
    private List<LocalMedia> medias;
    private List<LocalMedia> selectedImages;
    private static ImagesObservable sObserver;

    private ImagesObservable() {
        folders = new ArrayList<>();
        medias = new ArrayList<>();
        selectedImages = new ArrayList<>();
    }

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
     * 存储文件夹图片
     *
     * @param list
     */

    public void saveLocalFolders(List<LocalMediaFolder> list) {
        if (list != null) {
            folders = list;
        }
    }


    /**
     * 存储图片
     *
     * @param list
     */
    public void saveLocalMedia(List<LocalMedia> list) {
        medias = list;
    }


    /**
     * 读取图片
     */
    public List<LocalMedia> readLocalMedias() {
        return medias;
    }

    /**
     * 读取所有文件夹图片
     */
    public List<LocalMediaFolder> readLocalFolders() {
        return folders;
    }


    /**
     * 读取选中的图片
     */
    public List<LocalMedia> readSelectLocalMedias() {
        return selectedImages;
    }


    public void clearLocalFolders() {
        if (folders != null)
            folders.clear();
    }

    public void clearLocalMedia() {
        if (medias != null)
            medias.clear();
    }

    public void clearSelectedLocalMedia() {
        if (selectedImages != null)
            selectedImages.clear();
    }

    @Override
    public void add(ObserverListener observerListener) {
        observers.add(observerListener);
    }

    /**
     * 相册所有列表文件夹
     *
     * @param folders
     */
    @Override
    public void notifyFolderObserver(List<LocalMediaFolder> folders) {
        for (ObserverListener observerListener : observers) {
            observerListener.observerUpFoldersData(folders);
        }
    }

    /**
     * 选中图片集合观察者
     *
     * @param selectMedias
     */
    @Override
    public void notifySelectLocalMediaObserver(List<LocalMedia> selectMedias) {
        for (ObserverListener observerListener : observers) {
            observerListener.observerUpSelectsData(selectMedias);
        }
    }

    @Override
    public void remove(ObserverListener observerListener) {
        if (observers.contains(observerListener)) {
            observers.remove(observerListener);
        }
    }
}