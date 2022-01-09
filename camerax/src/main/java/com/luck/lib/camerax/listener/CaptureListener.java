package com.luck.lib.camerax.listener;

/**
 * @author：luck
 * @date：2020-01-04 13:38
 * @describe：CaptureListener
 */
public interface CaptureListener {
    void takePictures();

    void recordShort(long time);

    void recordStart();

    void recordEnd(long time);

    void changeTime(long duration);

    void recordZoom(float zoom);

    void recordError();
}
