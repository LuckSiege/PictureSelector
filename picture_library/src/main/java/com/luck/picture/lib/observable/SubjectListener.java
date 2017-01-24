package com.luck.picture.lib.observable;

import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.entity.LocalMediaFolder;

import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.observable
 * email：893855882@qq.com
 * data：17/1/16
 */
public interface SubjectListener {
    void add(ObserverListener observerListener);

    void notifyFolderObserver(List<LocalMediaFolder> folders);

    void notifySelectLocalMediaObserver(List<LocalMedia> medias);

    void remove(ObserverListener observerListener);
}
