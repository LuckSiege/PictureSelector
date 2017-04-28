package com.yalantis.ucrop.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.model
 * email：893855882@qq.com
 * data：2017/4/28
 */

public class EventEntity implements Serializable {
    public int what;
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
}
