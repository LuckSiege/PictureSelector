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

import android.graphics.Matrix;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;

import java.math.BigDecimal;

final class ScaleTypeTransform {
    /**
     * Produce the transform {@link Matrix} to do a center crop, which can be used by
     * {@link android.view.TextureView#setTransform(Matrix)}.
     *
     * @param resolution The resolution of the source {@link android.graphics.SurfaceTexture}
     * @param view The TextureView where the SurfaceTexture will be drawn into.
     * @param displayRotation The rotation of the display relative to the source
     * {@link android.view.Surface}
     */
    static Matrix transformCenterCrop(@NonNull Size resolution, @NonNull View view,
            int displayRotation) {
        if (resolution.getWidth() == 0 || resolution.getHeight() == 0) {
            throw new IllegalArgumentException("Input resolution can not be zero sized");
        }

        if (view.getWidth() == 0 || view.getHeight() == 0) {
            throw new IllegalArgumentException("View resolution can not be zero sized");
        }

        Matrix matrix = new Matrix();

        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        int displayRotationDegrees =
                SurfaceRotation.rotationDegreesFromSurfaceRotation(displayRotation);
        Size scaled = calculateCenterCropDimension(resolution.getWidth(), resolution.getHeight(),
                viewWidth, viewHeight, displayRotationDegrees);

        // Compute the center of the view.
        int centerX = viewWidth / 2;
        int centerY = viewHeight / 2;

        // Do corresponding rotation to correct the preview direction
        matrix.postRotate(-displayRotationDegrees, centerX, centerY);

        // Compute the scale value for center crop mode
        float xScale = scaled.getWidth() / (float) viewWidth;
        float yScale = scaled.getHeight() / (float) viewHeight;

        if (displayRotationDegrees == 90 || displayRotationDegrees == 270) {
            xScale = scaled.getWidth() / (float) viewHeight;
            yScale = scaled.getHeight() / (float) viewWidth;
        }

        // Only two digits after the decimal point are valid for postScale. Need to get ceiling of
        // two digits floating value to do the scale operation. Otherwise, the result may be
        // scaled not large enough and will have some blank lines on the screen.
        xScale = new BigDecimal(xScale).setScale(2, BigDecimal.ROUND_CEILING).floatValue();
        yScale = new BigDecimal(yScale).setScale(2, BigDecimal.ROUND_CEILING).floatValue();

        // Do corresponding scale to resolve the deformation problem
        matrix.postScale(xScale, yScale, centerX, centerY);

        return matrix;
    }

    /**
     * Produces the dimensions for the center cropped surface.
     *
     * @param sourceWidth The height of the source {@link android.view.Surface}.
     * @param sourceHeight The width of the source {@link android.view.Surface}.
     * @param parentWidth The width of the view that is drawn into.
     * @param parentHeight The height of the view that is drawn into.
     * @param displayRotation The rotation of the display relative to the source Surface.
     */
    private static Size calculateCenterCropDimension(int sourceWidth, int sourceHeight,
            int parentWidth,
            int parentHeight, int displayRotation) {
        int inWidth = sourceWidth;
        int inHeight = sourceHeight;
        if (displayRotation == 0 || displayRotation == 180) {
            // Need to reverse the width and height since we're in landscape orientation.
            inWidth = sourceHeight;
            inHeight = sourceWidth;
        }

        int outWidth;
        int outHeight;

        float vfRatio = inWidth / (float) inHeight;
        float parentRatio = parentWidth / (float) parentHeight;

        // Match shortest sides together.
        if (vfRatio < parentRatio) {
            outWidth = parentWidth;
            outHeight = Math.round(parentWidth / vfRatio);
        } else {
            outWidth = Math.round(parentHeight * vfRatio);
            outHeight = parentHeight;
        }

        return new Size(outWidth, outHeight);
    }

    // Prevent creating an instance
    private ScaleTypeTransform() {
    }
}
