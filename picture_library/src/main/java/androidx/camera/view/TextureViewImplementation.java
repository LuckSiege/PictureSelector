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

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.content.ContextCompat;
import androidx.core.util.Preconditions;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The {@link TextureView} implementation for {@link PreviewView}
 */
public class TextureViewImplementation implements PreviewView.Implementation {

    private static final String TAG = "TextureViewImpl";

    TextureView mTextureView;
    SurfaceTexture mSurfaceTexture;
    private Size mResolution;
    ListenableFuture<Void> mSurfaceReleaseFuture;
    CallbackToFutureAdapter.Completer<Surface> mSurfaceCompleter;

    @Override
    public void init(@NonNull FrameLayout parent) {
        mTextureView = new TextureView(parent.getContext());
        mTextureView.setLayoutParams(
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(final SurfaceTexture surfaceTexture,
                    final int width, final int height) {
                mSurfaceTexture = surfaceTexture;
                tryToProvidePreviewSurface();
            }

            @Override
            public void onSurfaceTextureSizeChanged(final SurfaceTexture surfaceTexture,
                    final int width, final int height) {
                Log.d(TAG, "onSurfaceTextureSizeChanged(width:" + width + ", height: " + height
                        + " )");
            }

            /**
             * If a surface has been provided to the camera (meaning
             * {@link TextureViewImplementation#mSurfaceCompleter} is null), but the camera
             * is still using it (meaning {@link TextureViewImplementation#mSurfaceReleaseFuture} is
             * not null), a listener must be added to
             * {@link TextureViewImplementation#mSurfaceReleaseFuture} to ensure the surface
             * is properly released after the camera is done using it.
             *
             * @param surfaceTexture The {@link SurfaceTexture} about to be destroyed.
             * @return false if the camera is not done with the surface, true otherwise.
             */
            @Override
            public boolean onSurfaceTextureDestroyed(final SurfaceTexture surfaceTexture) {
                mSurfaceTexture = null;
                if (mSurfaceCompleter == null && mSurfaceReleaseFuture != null) {
                    Futures.addCallback(mSurfaceReleaseFuture, new FutureCallback<Void>() {
                        @Override
                        public void onSuccess(@Nullable Void result) {
                            surfaceTexture.release();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            throw new IllegalStateException("SurfaceReleaseFuture should never "
                                    + "fail. Did it get completed by GC?", t);
                        }
                    }, ContextCompat.getMainExecutor(mTextureView.getContext()));
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void onSurfaceTextureUpdated(final SurfaceTexture surfaceTexture) {
            }
        });
        parent.addView(mTextureView);
    }

    @NonNull
    @Override
    public Preview.PreviewSurfaceProvider getPreviewSurfaceProvider() {
        return (resolution, surfaceReleaseFuture) -> {
            mResolution = resolution;
            mSurfaceReleaseFuture = surfaceReleaseFuture;

            return CallbackToFutureAdapter.getFuture(
                    (CallbackToFutureAdapter.Resolver<Surface>) completer -> {
                        completer.addCancellationListener(() -> {
                            Preconditions.checkState(mSurfaceCompleter == completer);
                            mSurfaceCompleter = null;
                            mSurfaceReleaseFuture = null;
                        }, ContextCompat.getMainExecutor(mTextureView.getContext()));
                        mSurfaceCompleter = completer;
                        tryToProvidePreviewSurface();
                        return "provide preview surface";
                    });
        };
    }

    @SuppressWarnings("WeakerAccess")
    void tryToProvidePreviewSurface() {
        /*
          Should only continue if:
          - The preview size has been specified.
          - The textureView's surfaceTexture is available (after TextureView
          .SurfaceTextureListener#onSurfaceTextureAvailable is invoked)
          - The surfaceCompleter has been set (after CallbackToFutureAdapter
          .Resolver#attachCompleter is invoked).
         */
        if (mResolution == null || mSurfaceTexture == null || mSurfaceCompleter == null) {
            return;
        }

        mSurfaceTexture.setDefaultBufferSize(mResolution.getWidth(), mResolution.getHeight());

        final Surface surface = new Surface(mSurfaceTexture);
        final ListenableFuture<Void> surfaceReleaseFuture = mSurfaceReleaseFuture;
        mSurfaceReleaseFuture.addListener(() -> {
            surface.release();
            if (mSurfaceReleaseFuture == surfaceReleaseFuture) {
                mSurfaceReleaseFuture = null;
            }
        }, ContextCompat.getMainExecutor(mTextureView.getContext()));

        mSurfaceCompleter.set(surface);
        mSurfaceCompleter = null;

        transformPreview();
    }

    private void transformPreview() {
        final WindowManager windowManager =
                (WindowManager) mTextureView.getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return;
        }
        final int rotation = windowManager.getDefaultDisplay().getRotation();
        final Matrix transformMatrix = ScaleTypeTransform.transformCenterCrop(mResolution,
                mTextureView, rotation);
        mTextureView.setTransform(transformMatrix);
    }
}
