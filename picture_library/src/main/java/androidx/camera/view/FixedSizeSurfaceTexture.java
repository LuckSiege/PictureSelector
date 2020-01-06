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
import android.os.Build.VERSION_CODES;
import android.util.Size;

import androidx.annotation.RequiresApi;

/**
 * An implementation of {@link SurfaceTexture} with a fixed default buffer size.
 *
 * <p>The fixed default buffer size used at construction time cannot be changed through the {@link
 * #setDefaultBufferSize(int, int)} method. This should only be used for the
 * {@link TextureViewImplementation} as a temporary solution to prevent the
 * {@link android.view.TextureView} from changing the resolution of the {@link SurfaceTexture},
 * because the {@link android.hardware.camera2.CameraCaptureSession} can not handle any arbitrary
 * resolution.
 */
final class FixedSizeSurfaceTexture extends SurfaceTexture {
    /**
     * Construct a new SurfaceTexture to stream images to a given OpenGL texture.
     *
     * @param texName   the OpenGL texture object name (e.g. generated via glGenTextures)
     * @param fixedSize the fixed default buffer size
     * @throws android.view.Surface.OutOfResourcesException If the SurfaceTexture cannot be created.
     */
    FixedSizeSurfaceTexture(int texName, Size fixedSize) {
        super(texName);
        super.setDefaultBufferSize(fixedSize.getWidth(), fixedSize.getHeight());
    }

    /**
     * Construct a new SurfaceTexture to stream images to a given OpenGL texture.
     *
     * <p>In single buffered mode the application is responsible for serializing access to the image
     * content buffer. Each time the image content is to be updated, the {@link #releaseTexImage()}
     * method must be called before the image content producer takes ownership of the buffer. For
     * example, when producing image content with the NDK ANativeWindow_lock and
     * ANativeWindow_unlockAndPost functions, {@link #releaseTexImage()} must be called before each
     * ANativeWindow_lock, or that call will fail. When producing image content with OpenGL ES,
     * {@link #releaseTexImage()} must be called before the first OpenGL ES function call each
     * frame.
     *
     * @param texName          the OpenGL texture object name (e.g. generated via glGenTextures)
     * @param singleBufferMode whether the SurfaceTexture will be in single buffered mode.
     * @param fixedSize        the fixed default buffer size
     * @throws android.view.Surface.OutOfResourcesException If the SurfaceTexture cannot be created.
     */
    FixedSizeSurfaceTexture(int texName, boolean singleBufferMode, Size fixedSize) {
        super(texName, singleBufferMode);
        super.setDefaultBufferSize(fixedSize.getWidth(), fixedSize.getHeight());
    }

    /**
     * Construct a new SurfaceTexture to stream images to a given OpenGL texture.
     *
     * <p>In single buffered mode the application is responsible for serializing access to the image
     * content buffer. Each time the image content is to be updated, the {@link #releaseTexImage()}
     * method must be called before the image content producer takes ownership of the buffer. For
     * example, when producing image content with the NDK ANativeWindow_lock and
     * ANativeWindow_unlockAndPost functions, {@link #releaseTexImage()} must be called before each
     * ANativeWindow_lock, or that call will fail. When producing image content with OpenGL ES,
     * {@link #releaseTexImage()} must be called before the first OpenGL ES function call each
     * frame.
     *
     * <p>Unlike {@link SurfaceTexture(int, boolean)}, which takes an OpenGL texture object name,
     * this constructor creates the SurfaceTexture in detached mode. A texture name must be passed
     * in using {@link #attachToGLContext} before calling {@link #releaseTexImage()} and producing
     * image content using OpenGL ES.
     *
     * @param singleBufferMode whether the SurfaceTexture will be in single buffered mode.
     * @param fixedSize        the fixed default buffer size
     * @throws android.view.Surface.OutOfResourcesException If the SurfaceTexture cannot be created.
     */
    @RequiresApi(api = VERSION_CODES.O)
    FixedSizeSurfaceTexture(boolean singleBufferMode, Size fixedSize) {
        super(singleBufferMode);
        super.setDefaultBufferSize(fixedSize.getWidth(), fixedSize.getHeight());
    }

    /**
     * This method has no effect.
     *
     * <p>Unlike {@link SurfaceTexture}, this method does not affect the default buffer size. The
     * default buffer size will remain what it was set to during construction.
     *
     * @param width  ignored width
     * @param height ignored height
     */
    @Override
    public void setDefaultBufferSize(int width, int height) {
        // No-op
    }
}

