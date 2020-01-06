/*
 * Copyright (C) 2019 The Android Open Source Project
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

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Looper;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.UiThread;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraOrientationUtil;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCapture.OnImageCapturedCallback;
import androidx.camera.core.ImageCapture.OnImageSavedCallback;
import androidx.camera.core.LensFacingConverter;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.core.UseCase;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCapture.OnVideoSavedCallback;
import androidx.camera.core.VideoCaptureConfig;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.CameraView.CaptureMode;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static androidx.camera.core.ImageCapture.FLASH_MODE_OFF;

/**
 * CameraX use case operation built on @{link androidx.camera.core}.
 */
final class CameraXModule {
    public static final String TAG = "CameraXModule";

    private static final float UNITY_ZOOM_SCALE = 1f;
    private static final float ZOOM_NOT_SUPPORTED = UNITY_ZOOM_SCALE;
    private static final Rational ASPECT_RATIO_16_9 = new Rational(16, 9);
    private static final Rational ASPECT_RATIO_4_3 = new Rational(4, 3);
    private static final Rational ASPECT_RATIO_9_16 = new Rational(9, 16);
    private static final Rational ASPECT_RATIO_3_4 = new Rational(3, 4);

    private final Preview.Builder mPreviewBuilder;
    private final VideoCaptureConfig.Builder mVideoCaptureConfigBuilder;
    private final ImageCapture.Builder mImageCaptureBuilder;
    private WeakReference<CameraView> mCameraViewWeakReference;
    final AtomicBoolean mVideoIsRecording = new AtomicBoolean(false);
    private CaptureMode mCaptureMode = CaptureMode.IMAGE;
    private long mMaxVideoDuration = CameraView.INDEFINITE_VIDEO_DURATION;
    private long mMaxVideoSize = CameraView.INDEFINITE_VIDEO_SIZE;
    @ImageCapture.FlashMode
    private int mFlash = FLASH_MODE_OFF;
    @Nullable
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
            Camera mCamera;
    @Nullable
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
            CallbackToFutureAdapter.Completer<Size> mResolutionUpdateCompleter;
    @Nullable
    private ImageCapture mImageCapture;
    @Nullable
    private VideoCapture mVideoCapture;
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
    @Nullable
    Preview mPreview;
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
    @Nullable
    LifecycleOwner mCurrentLifecycle;
    private final LifecycleObserver mCurrentLifecycleObserver =
            new LifecycleObserver() {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                public void onDestroy(LifecycleOwner owner) {
                    if (owner == mCurrentLifecycle) {
                        clearCurrentLifecycle();
                        mPreview.setPreviewSurfaceProvider(null);
                    }
                }
            };
    @Nullable
    private LifecycleOwner mNewLifecycle;
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
    @Nullable
    Integer mCameraLensFacing = CameraSelector.LENS_FACING_BACK;
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
    @Nullable
    ProcessCameraProvider mCameraProvider;

    CameraXModule(CameraView view) {
        mCameraViewWeakReference = new WeakReference<>(view);
        Futures.addCallback(ProcessCameraProvider.getInstance(getCameraView().getContext()),
                new FutureCallback<ProcessCameraProvider>() {
                    // TODO(b/124269166): Rethink how we can handle permissions here.
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(@Nullable ProcessCameraProvider provider) {
                        Preconditions.checkNotNull(provider);
                        mCameraProvider = provider;
                        if (mCurrentLifecycle != null) {
                            bindToLifecycle(mCurrentLifecycle);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        throw new RuntimeException("CameraX failed to initialize.", t);
                    }
                }, CameraXExecutors.mainThreadExecutor());

        mPreviewBuilder = new Preview.Builder().setTargetName("Preview");

        mImageCaptureBuilder = new ImageCapture.Builder().setTargetName("ImageCapture");

        mVideoCaptureConfigBuilder =
                new VideoCaptureConfig.Builder().setTargetName("VideoCapture");
    }

    @RequiresPermission(permission.CAMERA)
    void bindToLifecycle(LifecycleOwner lifecycleOwner) {
        mNewLifecycle = lifecycleOwner;

        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            bindToLifecycleAfterViewMeasured();
        }
    }

    private CameraView getCameraView() {
        return mCameraViewWeakReference.get();
    }

    @RequiresPermission(permission.CAMERA)
    void bindToLifecycleAfterViewMeasured() {
        if (mNewLifecycle == null) {
            return;
        }

        clearCurrentLifecycle();
        mCurrentLifecycle = mNewLifecycle;
        mNewLifecycle = null;
        if (mCurrentLifecycle.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            mCurrentLifecycle = null;
            throw new IllegalArgumentException("Cannot bind to lifecycle in a destroyed state.");
        }

        if (mCameraProvider == null) {
            // try again once the camera provider is no longer null
            return;
        }

        ListenableFuture<Size> resolutionUpdateFuture = CallbackToFutureAdapter.getFuture(
                completer -> {
                    mResolutionUpdateCompleter = completer;
                    return "PreviewResolutionUpdate";
                });

        Set<Integer> available = getAvailableCameraLensFacing();

        if (available.isEmpty()) {
            Log.w(TAG, "Unable to bindToLifeCycle since no cameras available");
            mCameraLensFacing = null;
        }

        // Ensure the current camera exists, or default to another camera
        if (mCameraLensFacing != null && !available.contains(mCameraLensFacing)) {
            Log.w(TAG, "Camera does not exist with direction " + mCameraLensFacing);

            // Default to the first available camera direction
            mCameraLensFacing = available.iterator().next();

            Log.w(TAG, "Defaulting to primary camera with direction " + mCameraLensFacing);
        }

        // Do not attempt to create use cases for a null cameraLensFacing. This could occur if
        // the user explicitly sets the LensFacing to null, or if we determined there
        // were no available cameras, which should be logged in the logic above.
        if (mCameraLensFacing == null) {
            return;
        }

        // Set the preferred aspect ratio as 4:3 if it is IMAGE only mode. Set the preferred aspect
        // ratio as 16:9 if it is VIDEO or MIXED mode. Then, it will be WYSIWYG when the view finder
        // is in CENTER_INSIDE mode.

        boolean isDisplayPortrait = getDisplayRotationDegrees() == 0
                || getDisplayRotationDegrees() == 180;

        Rational targetAspectRatio;
        if (getCaptureMode() == CaptureMode.IMAGE) {
            mImageCaptureBuilder.setTargetAspectRatio(AspectRatio.RATIO_4_3);
            targetAspectRatio = isDisplayPortrait ? ASPECT_RATIO_3_4 : ASPECT_RATIO_4_3;
        } else {
            mImageCaptureBuilder.setTargetAspectRatio(AspectRatio.RATIO_16_9);
            targetAspectRatio = isDisplayPortrait ? ASPECT_RATIO_9_16 : ASPECT_RATIO_16_9;
        }

        mImageCaptureBuilder.setTargetRotation(getDisplaySurfaceRotation());
        mImageCapture = mImageCaptureBuilder.build();

        mVideoCaptureConfigBuilder.setTargetRotation(getDisplaySurfaceRotation());
        mVideoCapture = mVideoCaptureConfigBuilder.build();

        // Adjusts the preview resolution according to the view size and the target aspect ratio.
        int height = (int) (getMeasuredWidth() / targetAspectRatio.floatValue());
        mPreviewBuilder.setTargetResolution(new Size(getMeasuredWidth(), height));

        mPreview = mPreviewBuilder.build();
        mPreview.setPreviewSurfaceProvider((resolution, safeToCancelFuture) -> {
            // The PreviewSurfaceProvider#createSurfaceFuture() might come asynchronously.
            // It cannot guarantee the callback time, so we store the resolution result in
            // the listenableFuture.
            mResolutionUpdateCompleter.set(resolution);
            // Create SurfaceTexture and Surface.
            SurfaceTexture surfaceTexture = new FixedSizeSurfaceTexture(0, resolution);
            surfaceTexture.setDefaultBufferSize(resolution.getWidth(),
                    resolution.getHeight());
            surfaceTexture.detachFromGLContext();
            CameraXModule.this.setSurfaceTexture(surfaceTexture);
            Surface surface = new Surface(surfaceTexture);
            safeToCancelFuture.addListener(() -> {
                surface.release();
                surfaceTexture.release();
            }, CameraXExecutors.directExecutor());
            return Futures.immediateFuture(surface);
        });

        CameraSelector cameraSelector =
                new CameraSelector.Builder().requireLensFacing(mCameraLensFacing).build();
        if (getCaptureMode() == CaptureMode.IMAGE) {
            mCamera = mCameraProvider.bindToLifecycle(mCurrentLifecycle, cameraSelector,
                    mImageCapture,
                    mPreview);
        } else if (getCaptureMode() == CaptureMode.VIDEO) {
            mCamera = mCameraProvider.bindToLifecycle(mCurrentLifecycle, cameraSelector,
                    mVideoCapture,
                    mPreview);
        } else {
            mCamera = mCameraProvider.bindToLifecycle(mCurrentLifecycle, cameraSelector,
                    mImageCapture,
                    mVideoCapture, mPreview);
        }

        // Register the listener on the resolutionUpdateFuture, and we can get the resolution
        // result immediately if it has been set or it will callback once the resolution result
        // is ready.
        Futures.addCallback(resolutionUpdateFuture, new FutureCallback<Size>() {
            @Override
            public void onSuccess(@Nullable Size result) {
                if (result == null) {
                    Log.w(TAG, "PreviewSourceDimensUpdate fail");
                    return;
                }

                int cameraOrientation =
                        mCamera != null ? mCamera.getCameraInfo().getSensorRotationDegrees() : 0;
                boolean needReverse = cameraOrientation != 0 && cameraOrientation != 180;
                int textureWidth = needReverse ? result.getHeight() : result.getWidth();
                int textureHeight = needReverse ? result.getWidth() : result.getHeight();
                onPreviewSourceDimensUpdated(textureWidth, textureHeight);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "PreviewSourceDimensUpdate fail", t);
            }
        }, CameraXExecutors.mainThreadExecutor());

        setZoomRatio(UNITY_ZOOM_SCALE);
        mCurrentLifecycle.getLifecycle().addObserver(mCurrentLifecycleObserver);
        // Enable flash setting in ImageCapture after use cases are created and binded.
        setFlash(getFlash());
    }

    public void open() {
        throw new UnsupportedOperationException(
                "Explicit open/close of camera not yet supported. Use bindtoLifecycle() instead.");
    }

    public void close() {
        throw new UnsupportedOperationException(
                "Explicit open/close of camera not yet supported. Use bindtoLifecycle() instead.");
    }

    public void takePicture(Executor executor, OnImageCapturedCallback callback) {
        if (mImageCapture == null) {
            return;
        }

        if (getCaptureMode() == CaptureMode.VIDEO) {
            throw new IllegalStateException("Can not take picture under VIDEO capture mode.");
        }

        if (callback == null) {
            throw new IllegalArgumentException("OnImageCapturedCallback should not be empty");
        }

        mImageCapture.takePicture(executor, callback);
    }

    public void takePicture(File saveLocation, Executor executor, OnImageSavedCallback callback) {
        if (mImageCapture == null) {
            return;
        }

        if (getCaptureMode() == CaptureMode.VIDEO) {
            throw new IllegalStateException("Can not take picture under VIDEO capture mode.");
        }

        if (callback == null) {
            throw new IllegalArgumentException("OnImageSavedCallback should not be empty");
        }

        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        metadata.setReversedHorizontal(
                mCameraLensFacing != null && mCameraLensFacing == CameraSelector.LENS_FACING_FRONT);
        mImageCapture.takePicture(saveLocation, metadata, executor, callback);
    }

    public void startRecording(File file, Executor executor, final OnVideoSavedCallback callback) {
        if (mVideoCapture == null) {
            return;
        }

        if (getCaptureMode() == CaptureMode.IMAGE) {
            throw new IllegalStateException("Can not record video under IMAGE capture mode.");
        }

        if (callback == null) {
            throw new IllegalArgumentException("OnVideoSavedCallback should not be empty");
        }

        mVideoIsRecording.set(true);
        mVideoCapture.startRecording(
                file,
                executor,
                new OnVideoSavedCallback() {
                    @Override
                    public void onVideoSaved(@NonNull File savedFile) {
                        mVideoIsRecording.set(false);
                        callback.onVideoSaved(savedFile);
                    }

                    @Override
                    public void onError(
                            @VideoCapture.VideoCaptureError int videoCaptureError,
                            @NonNull String message,
                            @Nullable Throwable cause) {
                        mVideoIsRecording.set(false);
                        Log.e(TAG, message, cause);
                        callback.onError(videoCaptureError, message, cause);
                    }
                });
    }

    public void stopRecording() {
        if (mVideoCapture == null) {
            return;
        }

        mVideoCapture.stopRecording();
    }

    public boolean isRecording() {
        return mVideoIsRecording.get();
    }

    // TODO(b/124269166): Rethink how we can handle permissions here.
    @SuppressLint("MissingPermission")
    public void setCameraLensFacing(@Nullable Integer lensFacing) {
        // Setting same lens facing is a no-op, so check for that first
        if (!Objects.equals(mCameraLensFacing, lensFacing)) {
            // If we're not bound to a lifecycle, just update the camera that will be opened when we
            // attach to a lifecycle.
            mCameraLensFacing = lensFacing;

            if (mCurrentLifecycle != null) {
                // Re-bind to lifecycle with new camera
                bindToLifecycle(mCurrentLifecycle);
            }
        }
    }

    @RequiresPermission(permission.CAMERA)
    public boolean hasCameraWithLensFacing(@CameraSelector.LensFacing int lensFacing) {
        String cameraId;
        try {
            cameraId = CameraX.getCameraWithLensFacing(lensFacing);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to query lens facing.", e);
        }

        return cameraId != null;
    }

    @Nullable
    public Integer getLensFacing() {
        return mCameraLensFacing;
    }

    public void toggleCamera() {
        // TODO(b/124269166): Rethink how we can handle permissions here.
        @SuppressLint("MissingPermission")
        Set<Integer> availableCameraLensFacing = getAvailableCameraLensFacing();

        if (availableCameraLensFacing.isEmpty()) {
            return;
        }

        if (mCameraLensFacing == null) {
            setCameraLensFacing(availableCameraLensFacing.iterator().next());
            return;
        }

        if (mCameraLensFacing == CameraSelector.LENS_FACING_BACK
                && availableCameraLensFacing.contains(CameraSelector.LENS_FACING_FRONT)) {
            setCameraLensFacing(CameraSelector.LENS_FACING_FRONT);
            return;
        }

        if (mCameraLensFacing == CameraSelector.LENS_FACING_FRONT
                && availableCameraLensFacing.contains(CameraSelector.LENS_FACING_BACK)) {
            setCameraLensFacing(CameraSelector.LENS_FACING_BACK);
            return;
        }
    }

    public float getZoomRatio() {
        if (mCamera != null) {
            return mCamera.getCameraInfo().getZoomRatio().getValue();
        } else {
            return UNITY_ZOOM_SCALE;
        }
    }

    public void setZoomRatio(float zoomRatio) {
        if (mCamera != null) {
            mCamera.getCameraControl().setZoomRatio(zoomRatio);
        } else {
            Log.e(TAG, "Failed to set zoom ratio");
        }
    }

    public float getMinZoomRatio() {
        if (mCamera != null) {
            return mCamera.getCameraInfo().getMinZoomRatio().getValue();
        } else {
            return UNITY_ZOOM_SCALE;
        }
    }

    public float getMaxZoomRatio() {
        if (mCamera != null) {
            return mCamera.getCameraInfo().getMaxZoomRatio().getValue();
        } else {
            return ZOOM_NOT_SUPPORTED;
        }
    }

    public boolean isZoomSupported() {
        return getMaxZoomRatio() != ZOOM_NOT_SUPPORTED;
    }

    // TODO(b/124269166): Rethink how we can handle permissions here.
    @SuppressLint("MissingPermission")
    private void rebindToLifecycle() {
        if (mCurrentLifecycle != null) {
            bindToLifecycle(mCurrentLifecycle);
        }
    }

    int getRelativeCameraOrientation(boolean compensateForMirroring) {
        int rotationDegrees = 0;
        if (mCamera != null) {
            rotationDegrees =
                    mCamera.getCameraInfo().getSensorRotationDegrees(getDisplaySurfaceRotation());
            if (compensateForMirroring) {
                rotationDegrees = (360 - rotationDegrees) % 360;
            }
        }

        return rotationDegrees;
    }

    public void invalidateView() {
        transformPreview();
        updateViewInfo();
    }

    void clearCurrentLifecycle() {
        if (mCurrentLifecycle != null && mCameraProvider != null) {
            // Remove previous use cases
            List<UseCase> toUnbind = new ArrayList<>();
            if (mImageCapture != null && mCameraProvider.isBound(mImageCapture)) {
                toUnbind.add(mImageCapture);
            }
            if (mVideoCapture != null && mCameraProvider.isBound(mVideoCapture)) {
                toUnbind.add(mVideoCapture);
            }
            if (mPreview != null && mCameraProvider.isBound(mPreview)) {
                toUnbind.add(mPreview);
            }

            if (!toUnbind.isEmpty()) {
                mCameraProvider.unbind(toUnbind.toArray((new UseCase[0])));
            }
        }
        mCamera = null;
        mCurrentLifecycle = null;
    }

    @UiThread
    private void transformPreview() {
        int previewWidth = getPreviewWidth();
        int previewHeight = getPreviewHeight();
        int displayOrientation = getDisplayRotationDegrees();

        Matrix matrix = new Matrix();

        // Apply rotation of the display
        int rotation = -displayOrientation;

        int px = (int) Math.round(previewWidth / 2d);
        int py = (int) Math.round(previewHeight / 2d);

        matrix.postRotate(rotation, px, py);

        if (displayOrientation == 90 || displayOrientation == 270) {
            // Swap width and height
            float xScale = previewWidth / (float) previewHeight;
            float yScale = previewHeight / (float) previewWidth;

            matrix.postScale(xScale, yScale, px, py);
        }

        setTransform(matrix);
    }

    // Update view related information used in use cases
    private void updateViewInfo() {
        if (mImageCapture != null) {
            mImageCapture.setTargetAspectRatioCustom(new Rational(getWidth(), getHeight()));
            mImageCapture.setTargetRotation(getDisplaySurfaceRotation());
        }

        if (mVideoCapture != null) {
            mVideoCapture.setTargetRotation(getDisplaySurfaceRotation());
        }
    }

    @RequiresPermission(permission.CAMERA)
    private Set<Integer> getAvailableCameraLensFacing() {
        // Start with all camera directions
        Set<Integer> available = new LinkedHashSet<>(Arrays.asList(LensFacingConverter.values()));

        // If we're bound to a lifecycle, remove unavailable cameras
        if (mCurrentLifecycle != null) {
            if (!hasCameraWithLensFacing(CameraSelector.LENS_FACING_BACK)) {
                available.remove(CameraSelector.LENS_FACING_BACK);
            }

            if (!hasCameraWithLensFacing(CameraSelector.LENS_FACING_FRONT)) {
                available.remove(CameraSelector.LENS_FACING_FRONT);
            }
        }

        return available;
    }

    @ImageCapture.FlashMode
    public int getFlash() {
        return mFlash;
    }

    public void setFlash(@ImageCapture.FlashMode int flash) {
        this.mFlash = flash;

        if (mImageCapture == null) {
            // Do nothing if there is no imageCapture
            return;
        }

        mImageCapture.setFlashMode(flash);
    }

    public void enableTorch(boolean torch) {
        if (mCamera == null) {
            return;
        }
        mCamera.getCameraControl().enableTorch(torch);
    }

    public boolean isTorchOn() {
        if (mCamera == null) {
            return false;
        }
        return mCamera.getCameraInfo().getTorchState().getValue() == TorchState.ON;
    }

    public Context getContext() {
        return getCameraView().getContext();
    }

    public int getWidth() {
        return getCameraView().getWidth();
    }

    public int getHeight() {
        return getCameraView().getHeight();
    }

    public int getDisplayRotationDegrees() {
        return CameraOrientationUtil.surfaceRotationToDegrees(getDisplaySurfaceRotation());
    }

    protected int getDisplaySurfaceRotation() {
        return getCameraView().getDisplaySurfaceRotation();
    }

    public void setSurfaceTexture(SurfaceTexture st) {
        getCameraView().setSurfaceTexture(st);
    }

    private int getPreviewWidth() {
        return getCameraView().getPreviewWidth();
    }

    private int getPreviewHeight() {
        return getCameraView().getPreviewHeight();
    }

    private int getMeasuredWidth() {
        return getCameraView().getMeasuredWidth();
    }

    private int getMeasuredHeight() {
        return getCameraView().getMeasuredHeight();
    }

    void setTransform(final Matrix matrix) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            getCameraView().post(
                    () -> setTransform(matrix));
        } else {
            getCameraView().setTransform(matrix);
        }
    }

    /**
     * Notify the view that the source dimensions have changed.
     *
     * <p>This will allow the view to layout the preview to display the correct aspect ratio.
     *
     * @param width  width of camera source buffers.
     * @param height height of camera source buffers.
     */
    void onPreviewSourceDimensUpdated(int width, int height) {
        getCameraView().onPreviewSourceDimensUpdated(width, height);
    }

    @Nullable
    public Camera getCamera() {
        return mCamera;
    }

    @NonNull
    public CaptureMode getCaptureMode() {
        return mCaptureMode;
    }

    public void setCaptureMode(@NonNull CaptureMode captureMode) {
        this.mCaptureMode = captureMode;
        rebindToLifecycle();
    }

    public long getMaxVideoDuration() {
        return mMaxVideoDuration;
    }

    public void setMaxVideoDuration(long duration) {
        mMaxVideoDuration = duration;
    }

    public long getMaxVideoSize() {
        return mMaxVideoSize;
    }

    public void setMaxVideoSize(long size) {
        mMaxVideoSize = size;
    }

    public boolean isPaused() {
        return false;
    }

}
