package com.luck.picture.lib.style;

/**
 * @author：luck
 * @date：2021/11/15 4:12 下午
 * @describe：PictureSelectorUIStyle
 */
public class PictureSelectorStyle {
    /**
     * Album Window Style
     */
    private AlbumWindowStyle albumWindowStyle;

    /**
     * TitleBar UI Style
     */
    private TitleBarStyle titleBarStyle;

    /**
     * Middle Adapter UI Style
     */
    private MediaAdapterStyle mediaAdapterStyle;
    /**
     * BottomBar UI Style
     */
    private BottomNavBarStyle bottomBarStyle;

    /**
     * PictureSelector Window AnimationStyle
     */
    private PictureWindowAnimationStyle windowAnimationStyle;

    public TitleBarStyle getTitleBarStyle() {
        return titleBarStyle == null ? new TitleBarStyle() : titleBarStyle;
    }

    public void setTitleBarStyle(TitleBarStyle titleBarStyle) {
        this.titleBarStyle = titleBarStyle;
    }

    public MediaAdapterStyle getAdapterStyle() {
        return mediaAdapterStyle == null ? new MediaAdapterStyle() : mediaAdapterStyle;
    }

    public void setAdapterStyle(MediaAdapterStyle mediaAdapterStyle) {
        this.mediaAdapterStyle = mediaAdapterStyle;
    }

    public BottomNavBarStyle getBottomBarStyle() {
        return bottomBarStyle == null ? new BottomNavBarStyle() : bottomBarStyle;
    }

    public void setBottomBarStyle(BottomNavBarStyle bottomBarStyle) {
        this.bottomBarStyle = bottomBarStyle;
    }

    public PictureWindowAnimationStyle getWindowAnimationStyle() {
        if (windowAnimationStyle == null) {
            windowAnimationStyle = PictureWindowAnimationStyle.ofDefaultWindowAnimationStyle();
        }
        return windowAnimationStyle;
    }

    public void setWindowAnimationStyle(PictureWindowAnimationStyle windowAnimationStyle) {
        this.windowAnimationStyle = windowAnimationStyle;
    }

    public AlbumWindowStyle getAlbumWindowStyle() {
        return albumWindowStyle == null ? new AlbumWindowStyle() : albumWindowStyle;
    }

    public void setAlbumWindowStyle(AlbumWindowStyle albumWindowStyle) {
        this.albumWindowStyle = albumWindowStyle;
    }
}
