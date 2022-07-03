package com.luck.picture.lib.engine;

import android.content.Context;
import android.view.View;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnPlayerListener;

/**
 * @author：luck
 * @date：2022/7/1 22:25 下午
 * @describe：VideoPlayerEngine
 */
public interface VideoPlayerEngine<T> {

    /**
     * Create player instance
     *
     * @param context
     */
    View onCreateVideoPlayer(Context context);

    /**
     * Start playing video
     *
     * @param player
     * @param media
     */
    void onStarPlayer(T player, LocalMedia media);

    /**
     * 恢复播放
     */
    void onResume(T player);

    /**
     * 暂停播放
     */
    void onPause(T player);

    /**
     * Video Playing status
     *
     * @param player
     */
    boolean isPlaying(T player);

    /**
     * addPlayListener
     * {@link OnPlayerListener}
     *
     * @param playerListener
     */
    void addPlayListener(OnPlayerListener playerListener);

    /**
     * removePlayListener
     * <p>
     * {@link OnPlayerListener}
     *
     * @param playerListener
     */
    void removePlayListener(OnPlayerListener playerListener);

    /**
     * Player attached to window
     *
     * @param player
     */
    void onPlayerAttachedToWindow(T player);

    /**
     * Player detached to window
     *
     * @param player
     */
    void onPlayerDetachedFromWindow(T player);

    /**
     * destroy release player
     *
     * @param player
     */
    void destroy(T player);
}
