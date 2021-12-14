package com.luck.picture.lib.camera.listener;

/**
 * @author：luck
 * @date：2020-01-04 13:56
 */
public interface CaptureListener {
    void takePictures();

    void recordShort(long time);

    void recordStart();

    void recordEnd(long time);

    void recordZoom(float zoom);

    void recordError();
}
