package com.yalantis.ucrop.callback;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yalantis.ucrop.model.ExifInfo;

public interface BitmapLoadCallback {

    void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull Uri imageInputUri, @Nullable Uri imageOutputUri);

    void onFailure(@NonNull Exception bitmapWorkerException);

}