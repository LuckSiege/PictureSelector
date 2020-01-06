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
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.core.util.Preconditions;

import static androidx.camera.view.ScaleTypeTransform.transformCenterCrop;

/**
 * A {@link TextureView} implementation for {@link PreviewView} that uses a FixedSizeSurfaceTexture.
 * TODO: This is a temporary fix for the distorted preview seen on both PreviewViewFragment and 
 * CameraViewFragment after onPause/onResume (b/146048690), and will be removed after 
 * the TextureView's view parameters are modified instead of applying a transform matrix on it.
 */
public class FixedSizeTextureViewImplementation implements PreviewView.Implementation {

    private TextureView mTextureView;
    private SurfaceTextureReleaseBlockingListener mSurfaceTextureListener;

    @Override
    public void init(@NonNull final FrameLayout parent) {
        mTextureView = new TextureView(parent.getContext());
        mTextureView.setLayoutParams(
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));

        // Access the setting of the SurfaceTexture safely through the listener instead of
        // directly on the TextureView
        mSurfaceTextureListener = new SurfaceTextureReleaseBlockingListener(mTextureView);

        parent.addView(mTextureView);
    }

    @NonNull
    @Override
    public Preview.PreviewSurfaceProvider getPreviewSurfaceProvider() {
        return (resolution, surfaceReleaseFuture) -> {
            // Create the SurfaceTexture. Using a FixedSizeSurfaceTexture, because the
            // TextureView might try to change the size of the SurfaceTexture if layout has not
            // yet completed.
            final SurfaceTexture surfaceTexture = new FixedSizeSurfaceTexture(0, resolution);
            surfaceTexture.detachFromGLContext();
            final Surface surface = new Surface(surfaceTexture);

            final WindowManager windowManager =
                    (WindowManager) mTextureView.getContext().getSystemService(
                            Context.WINDOW_SERVICE);
            Preconditions.checkNotNull(windowManager);
            final Display display = windowManager.getDefaultDisplay();

            // Setup the TextureView for the correct transformation
            final Matrix matrix = transformCenterCrop(resolution, mTextureView,
                    display.getRotation());
            mTextureView.setTransform(matrix);

            final ViewGroup parent = (ViewGroup) mTextureView.getParent();
            parent.removeView(mTextureView);
            parent.addView(mTextureView);

            // Set the SurfaceTexture safely instead of directly calling
            // mTextureView.setSurfaceTexture(surfaceTexture);
            mSurfaceTextureListener.setSurfaceTextureSafely(surfaceTexture, surfaceReleaseFuture);
            surfaceReleaseFuture.addListener(surface::release, CameraXExecutors.directExecutor());

            return Futures.immediateFuture(surface);
        };
    }
}
