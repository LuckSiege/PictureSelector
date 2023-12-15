package com.luck.picture.lib.basic;

import android.app.Activity;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.luck.picture.lib.config.FileSizeUnit;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;
import com.luck.picture.lib.interfaces.OnQueryDataSourceListener;
import com.luck.picture.lib.interfaces.OnQueryFilterListener;
import com.luck.picture.lib.loader.IBridgeMediaLoader;
import com.luck.picture.lib.loader.LocalMediaLoader;
import com.luck.picture.lib.loader.LocalMediaPageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2022/2/17 22:12 晚上
 * @describe：PictureSelectionQueryModel
 */
public class PictureSelectionQueryModel {
    private final SelectorConfig selectionConfig;
    private final PictureSelector selector;

    public PictureSelectionQueryModel(PictureSelector selector, int selectMimeType) {
        this.selector = selector;
        selectionConfig = new SelectorConfig();
        SelectorProviders.getInstance().addSelectorConfigQueue(selectionConfig);
        selectionConfig.chooseMode = selectMimeType;
    }

    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @return
     */
    public PictureSelectionQueryModel isPageStrategy(boolean isPageStrategy) {
        selectionConfig.isPageStrategy = isPageStrategy;
        return this;
    }

    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @param pageSize       Maximum number of pages {@link PageSize is preferably no less than 20}
     * @return
     */
    public PictureSelectionQueryModel isPageStrategy(boolean isPageStrategy, int pageSize) {
        selectionConfig.isPageStrategy = isPageStrategy;
        selectionConfig.pageSize = pageSize < PictureConfig.MIN_PAGE_SIZE ? PictureConfig.MAX_PAGE_SIZE : pageSize;
        return this;
    }

    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @param pageSize            Maximum number of pages {@link  PageSize is preferably no less than 20}
     * @param isFilterInvalidFile Whether to filter invalid files {@link Some of the query performance is consumed,Especially on the Q version}
     * @return
     */
    public PictureSelectionQueryModel isPageStrategy(boolean isPageStrategy, int pageSize, boolean isFilterInvalidFile) {
        selectionConfig.isPageStrategy = isPageStrategy;
        selectionConfig.pageSize = pageSize < PictureConfig.MIN_PAGE_SIZE ? PictureConfig.MAX_PAGE_SIZE : pageSize;
        selectionConfig.isFilterInvalidFile = isFilterInvalidFile;
        return this;
    }

    /**
     * You need to filter out what doesn't meet the standards
     *
     * @param listener
     * @return
     */
    public PictureSelectionQueryModel setQueryFilterListener(OnQueryFilterListener listener) {
        selectionConfig.onQueryFilterListener = listener;
        return this;
    }

    /**
     * query local data source sort
     * {@link MediaStore.MediaColumns.DATE_MODIFIED # DATE_ADDED # _ID}
     * <p>
     * example:
     * MediaStore.MediaColumns.DATE_MODIFIED + " DESC";  or MediaStore.MediaColumns.DATE_MODIFIED + " ASC";
     * </p>
     *
     * @param sortOrder
     * @return
     */
    public PictureSelectionQueryModel setQuerySortOrder(String sortOrder) {
        if (!TextUtils.isEmpty(sortOrder)) {
            selectionConfig.sortOrder = sortOrder;
        }
        return this;
    }

    /**
     * @param isGif Whether to open gif
     * @return
     */
    public PictureSelectionQueryModel isGif(boolean isGif) {
        selectionConfig.isGif = isGif;
        return this;
    }

    /**
     * @param isWebp Whether to open .webp
     * @return
     */
    public PictureSelectionQueryModel isWebp(boolean isWebp) {
        selectionConfig.isWebp = isWebp;
        return this;
    }

    /**
     * @param isBmp Whether to open .isBmp
     * @return
     */
    public PictureSelectionQueryModel isBmp(boolean isBmp) {
        selectionConfig.isBmp = isBmp;
        return this;
    }

    /**
     * @param isHeic Whether to open .isHeic
     * @return
     */
    public PictureSelectionQueryModel isHeic(boolean isHeic) {
        selectionConfig.isHeic = isHeic;
        return this;
    }


    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     * @return
     */
    public PictureSelectionQueryModel setFilterMaxFileSize(long fileKbSize) {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.filterMaxFileSize = fileKbSize;
        } else {
            selectionConfig.filterMaxFileSize = fileKbSize * FileSizeUnit.KB;
        }
        return this;
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter min file size
     * @return
     */
    public PictureSelectionQueryModel setFilterMinFileSize(long fileKbSize) {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.filterMinFileSize = fileKbSize;
        } else {
            selectionConfig.filterMinFileSize = fileKbSize * FileSizeUnit.KB;
        }
        return this;
    }

    /**
     * filter max seconds video
     *
     * @param videoMaxSecond filter video max second
     * @return
     */
    public PictureSelectionQueryModel setFilterVideoMaxSecond(int videoMaxSecond) {
        selectionConfig.filterVideoMaxSecond = videoMaxSecond * 1000;
        return this;
    }

    /**
     * filter min seconds video
     *
     * @param videoMinSecond filter video min second
     * @return
     */
    public PictureSelectionQueryModel setFilterVideoMinSecond(int videoMinSecond) {
        selectionConfig.filterVideoMinSecond = videoMinSecond * 1000;
        return this;
    }


    /**
     * build local media Loader
     */
    public IBridgeMediaLoader buildMediaLoader() {
        Activity activity = selector.getActivity();
        if (activity == null) {
            throw new NullPointerException("Activity cannot be null");
        }
        return selectionConfig.isPageStrategy
                ? new LocalMediaPageLoader(activity, selectionConfig)
                : new LocalMediaLoader(activity, selectionConfig);
    }


    /**
     * obtain album data source
     *
     * @param call
     */
    public void obtainAlbumData(OnQueryDataSourceListener<LocalMediaFolder> call) {
        Activity activity = selector.getActivity();
        if (activity == null) {
            throw new NullPointerException("Activity cannot be null");
        }
        if (call == null) {
            throw new NullPointerException("OnQueryDataSourceListener cannot be null");
        }
        IBridgeMediaLoader loader = selectionConfig.isPageStrategy
                ? new LocalMediaPageLoader(activity, selectionConfig)
                : new LocalMediaLoader(activity, selectionConfig);
        loader.loadAllAlbum(new OnQueryAllAlbumListener<LocalMediaFolder>() {
            @Override
            public void onComplete(List<LocalMediaFolder> result) {
                call.onComplete(result);
            }
        });
    }


    /**
     * obtain data source
     *
     * @param call
     */
    public void obtainMediaData(OnQueryDataSourceListener<LocalMedia> call) {
        Activity activity = selector.getActivity();
        if (activity == null) {
            throw new NullPointerException("Activity cannot be null");
        }
        if (call == null) {
            throw new NullPointerException("OnQueryDataSourceListener cannot be null");
        }
        IBridgeMediaLoader loader = selectionConfig.isPageStrategy
                ? new LocalMediaPageLoader(activity, selectionConfig)
                : new LocalMediaLoader(activity, selectionConfig);
        loader.loadAllAlbum(new OnQueryAllAlbumListener<LocalMediaFolder>() {
            @Override
            public void onComplete(List<LocalMediaFolder> result) {
                if (result != null && result.size() > 0) {
                    LocalMediaFolder all = result.get(0);
                    if (selectionConfig.isPageStrategy) {
                        loader.loadPageMediaData(all.getBucketId(), 1, selectionConfig.pageSize,
                                new OnQueryDataResultListener<LocalMedia>() {
                                    @Override
                                    public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                                        call.onComplete(result);
                                    }
                                });
                    } else {
                        ArrayList<LocalMedia> data = all.getData();
                        call.onComplete(data);
                    }
                }
            }
        });
    }
}
