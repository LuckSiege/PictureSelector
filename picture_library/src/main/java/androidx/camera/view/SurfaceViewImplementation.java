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

import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.camera.core.Preview;
import androidx.concurrent.futures.CallbackToFutureAdapter;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The SurfaceView implementation for {@link PreviewView}.
 */
final class SurfaceViewImplementation implements PreviewView.Implementation {

    private static final String TAG = "SurfaceViewPreviewView";

    // Synthetic Accessor
    @SuppressWarnings("WeakerAccess")
    TransformableSurfaceView mSurfaceView;

    // Synthetic Accessor
    @SuppressWarnings("WeakerAccess")
    final CompleterWithSizeCallback mCompleterWithSizeCallback =
            new CompleterWithSizeCallback();

    private Preview.PreviewSurfaceProvider mPreviewSurfaceProvider =
            new Preview.PreviewSurfaceProvider() {
                @NonNull
                @Override
                public ListenableFuture<Surface> provideSurface(@NonNull Size resolution,
                        @NonNull ListenableFuture<Void> surfaceReleaseFuture) {
                    // No-op on surfaceReleaseFuture because the Surface will be destroyed by
                    // SurfaceView.
                    return CallbackToFutureAdapter.getFuture(
                            completer -> {
                                // Post to UI thread for thread safety.
                                mSurfaceView.post(
                                        () -> mCompleterWithSizeCallback.setCompleterAndSize(
                                                completer, resolution));
                                return "SurfaceViewSurfaceCreation";
                            });
                }
            };

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(@NonNull FrameLayout parent) {
        mSurfaceView = new TransformableSurfaceView(parent.getContext());
        mSurfaceView.setLayoutParams(
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
        parent.addView(mSurfaceView);
        mSurfaceView.getHolder().addCallback(mCompleterWithSizeCallback);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Preview.PreviewSurfaceProvider getPreviewSurfaceProvider() {
        return mPreviewSurfaceProvider;
    }

    /**
     * The {@link SurfaceHolder.Callback} on mSurfaceView.
     *
     * <p> SurfaceView creates Surface on its own before we can do anything. This class makes
     * sure only the Surface with correct size will be returned to Preview.
     */
    class CompleterWithSizeCallback implements SurfaceHolder.Callback {

        // Target Surface size. Only complete the ListenableFuture when the size of the Surface
        // matches this value.
        // Guarded by UI thread.
        @Nullable
        private Size mTargetSize;

        // Completer to set when the target size is met.
        // Guarded by UI thread.
        @Nullable
        private CallbackToFutureAdapter.Completer<Surface> mCompleter;

        // The cached size of the current Surface.
        // Guarded by UI thread.
        @Nullable
        private Size mCurrentSurfaceSize;

        /**
         * Sets the completer and the size. The completer will only be set if the current size of
         * the Surface matches the target size.
         */
        @UiThread
        void setCompleterAndSize(CallbackToFutureAdapter.Completer<Surface> completer,
                Size targetSize) {
            cancelCompleter();
            mCompleter = completer;
            mTargetSize = targetSize;
            if (!tryToComplete()) {
                // The current size is incorrect. Wait for it to change.
                Log.d(TAG, "Wait for new Surface creation.");
                mSurfaceView.getHolder().setFixedSize(targetSize.getWidth(),
                        targetSize.getHeight());
            }
        }

        /**
         * Sets the completer if size matches.
         *
         * @return true if the completer is set.
         */
        @UiThread
        private boolean tryToComplete() {
            Surface surface = mSurfaceView.getHolder().getSurface();
            if (mCompleter != null && mTargetSize != null && mTargetSize.equals(
                    mCurrentSurfaceSize)) {
                Log.d(TAG, "Surface set on Preview.");
                mCompleter.set(surface);
                mCompleter = null;
                mTargetSize = null;
                return true;
            }
            return false;
        }

        @UiThread
        private void cancelCompleter() {
            if (mCompleter != null) {
                Log.d(TAG, "Completer canceled.");
                mCompleter.setCancelled();
                mCompleter = null;
            }
            mTargetSize = null;
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "Surface created.");
            // No-op. Handling surfaceChanged() is enough because it's always called afterwards.
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Log.d(TAG, "Surface changed. Size: " + width + "x" + height);
            mCurrentSurfaceSize = new Size(width, height);
            tryToComplete();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "Surface destroyed.");
            mCurrentSurfaceSize = null;
            cancelCompleter();
        }
    }
}
