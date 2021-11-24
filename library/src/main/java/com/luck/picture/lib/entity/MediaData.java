package com.luck.picture.lib.entity;

import java.util.List;

/**
 * @author：luck
 * @date：2020-04-17 13:52
 * @describe：MediaData
 */
public class MediaData {

    /**
     * Is there more
     */
    public boolean isHasNextMore;

    /**
     * data
     */
    public List<LocalMedia> data;


    public MediaData() {
        super();
    }

    public MediaData(boolean isHasNextMore, List<LocalMedia> data) {
        super();
        this.isHasNextMore = isHasNextMore;
        this.data = data;
    }
}
