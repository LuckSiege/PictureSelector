package com.luck.picture.lib.compress;

import android.graphics.Bitmap;

import java.io.File;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：16/12/31
 */

public class LubanBuilder {

    int maxSize;

    int maxWidth;

    int maxHeight;

    File cacheDir;

    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

    int gear = Luban.THIRD_GEAR;

    LubanBuilder(File cacheDir) {
        this.cacheDir = cacheDir;
    }
}
