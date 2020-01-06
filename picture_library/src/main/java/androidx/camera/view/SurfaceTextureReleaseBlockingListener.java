/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.camera.view;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of {@link TextureView.SurfaceTextureListener} that prevents the
 * {@link TextureView} from releasing the {@link SurfaceTexture} until it is ready to be released.
 *
 * <p>When using this listener,
 * {@link #setSurfaceTextureSafely(SurfaceTexture, ListenableFuture)} should be used to set the
 * SurfaceTexture instead of calling {@link TextureView#setSurfaceTexture(SurfaceTexture)} in
 * order to prevent the SurfaceTexture from being released prematurely. If a SurfaceTexture is
 * set via setSurfaceTexture() then it will be released immediately when TextureView no longer
 * needs it, instead of waiting to release.
 */
final class SurfaceTextureReleaseBlockingListener implements TextureView.SurfaceTextureListener {
    private ConcurrentHashMap<SurfaceTexture, CallbackToFutureAdapter.Completer<Void>>
            mSurfaceTextureCompleterConcurrentHashMap = new ConcurrentHashMap<>();

    private final TextureView mTextureView;

    SurfaceTextureReleaseBlockingListener(TextureView textureView) {
        mTextureView = textureView;
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        CallbackToFutureAdapter.Completer<Void> completer =
                mSurfaceTextureCompleterConcurrentHashMap.get(surface);
        // Some SurfaceTextures might not have been registered via getSurfaceDestroyedFuture
        if (completer == null) {
            return true;
        }
        completer.set(null);
        mSurfaceTextureCompleterConcurrentHashMap.remove(surface);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    /**
     * Sets the {@link SurfaceTexture} on the {@link TextureView} passed by
     * {@link
     * SurfaceTextureReleaseBlockingListener#SurfaceTextureReleaseBlockingListener(TextureView)}.
     *
     * <p>The SurfaceTexture will not be immediately released when the TextureView is finished
     * with it. Instead, it will be released when both the TextureView is finished with it and
     * the surfaceReleasedBlockingFuture completes.
     *
     * @param surfaceReleaseBlockingFuture When this future completes, regardless of success or
     *                                     failure, the SurfaceTexture can be released by the
     *                                     TextureView.
     * @throws IllegalArgumentException if the same SurfaceTexture is set multiple times.
     */
    void setSurfaceTextureSafely(@NonNull SurfaceTexture surfaceTexture,
            @NonNull ListenableFuture<Void> surfaceReleaseBlockingFuture) {
        if (mSurfaceTextureCompleterConcurrentHashMap.containsKey(surfaceTexture)) {
            throw new IllegalArgumentException("SurfaceTexture already registered for destroy "
                    + "future");
        }

        ListenableFuture<Void> releasedByTextureViewFuture = CallbackToFutureAdapter.getFuture(
                completer -> {
                    mSurfaceTextureCompleterConcurrentHashMap.put(surfaceTexture, completer);
                    return "SurfaceTextureDestroyCompleter";
                });

        // Setup the futures for releasing
        List<ListenableFuture<Void>> futureList = Arrays.asList(surfaceReleaseBlockingFuture,
                releasedByTextureViewFuture);

        // Future should only complete once both safeToRelease completes and
        // onSurfaceTextureDestroyed called from TextureView so it should always release the
        // Surface and SurfaceTexture
        ListenableFuture<List<Void>> future = Futures.successfulAsList(futureList);
        future.addListener(surfaceTexture::release, CameraXExecutors.directExecutor());

        mTextureView.setSurfaceTexture(surfaceTexture);
    }
}
