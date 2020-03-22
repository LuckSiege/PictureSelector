/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.camera.view.preview.transform;

import android.graphics.Point;
import android.util.Pair;
import android.util.Size;
import android.view.Display;
import android.view.View;

import androidx.annotation.NonNull;

final class ScaleTransform {

    private ScaleTransform() {
    }

    /**
     * Computes the scales on both the x and y axes so that the preview can fill its container,
     * while maintaining the camera output size's aspect ratio.
     *
     * @param container  A parent {@link View} that wraps {@code view}.
     * @param view       A {@link View} that wraps the camera preview.
     * @param bufferSize A {@link Size} whose aspect ratio must be maintained when
     *                   scaling the preview.
     * @return The scales on both the x and y axes so that the preview can entirely fill its
     * container, while maintaining the camera output size's aspect ratio
     */
    @SuppressWarnings("SuspiciousNameCombination")
    static Pair<Float, Float> fill(@NonNull final View container, @NonNull final View view,
                                   @NonNull final Size bufferSize) {
        // Scaling only makes sense when none of the dimensions are equal to zero. In the
        // opposite case, a default scale of 1 is returned,
        if (container.getWidth() == 0 || container.getHeight() == 0 || view.getWidth() == 0
                || view.getHeight() == 0 || bufferSize.getWidth() == 0
                || bufferSize.getHeight() == 0) {
            return new Pair<>(1F, 1F);
        }

        final int viewRotationDegrees = (int) RotationTransform.getRotationDegrees(view);
        final boolean isNaturalPortrait = isNaturalPortrait(view, viewRotationDegrees);

        final int bufferWidth;
        final int bufferHeight;
        if (isNaturalPortrait) {
            bufferWidth = bufferSize.getHeight();
            bufferHeight = bufferSize.getWidth();
        } else {
            bufferWidth = bufferSize.getWidth();
            bufferHeight = bufferSize.getHeight();
        }

        // Scale the buffers back to the original output size.
        float scaleX = bufferWidth / (float) view.getWidth();
        float scaleY = bufferHeight / (float) view.getHeight();

        int bufferRotatedWidth;
        int bufferRotatedHeight;
        if (viewRotationDegrees == 0 || viewRotationDegrees == 180) {
            bufferRotatedWidth = bufferWidth;
            bufferRotatedHeight = bufferHeight;
        } else {
            bufferRotatedWidth = bufferHeight;
            bufferRotatedHeight = bufferWidth;
        }

        // Scale the buffer so that it completely fills the container.
        final float scale = Math.max(container.getWidth() / (float) bufferRotatedWidth,
                container.getHeight() / (float) bufferRotatedHeight);
        scaleX *= scale;
        scaleY *= scale;

        return new Pair<>(scaleX, scaleY);
    }

    /**
     * Computes the scales on both the x and y axes so that the preview is entirely contained within
     * its container, while maintaining the camera output size's aspect ratio.
     *
     * @param container  A parent {@link View} that wraps {@code view}.
     * @param view       A {@link View} that wraps the camera preview.
     * @param bufferSize A {@link Size} whose aspect ratio must be maintained when
     *                   scaling the preview.
     * @return The scales on both the x and y axes so that the preview is entirely contained within
     * its container, while maintaining the camera output size's aspect ratio.
     */
    static Pair<Float, Float> fit(@NonNull final View container, @NonNull final View view,
                                  @NonNull final Size bufferSize) {
        return new Pair<>(1F, 1F);
    }

    /**
     * Determines whether the current device is a natural portrait-oriented device
     *
     * <p>
     * Using the current app's window to determine whether the device is a natural
     * portrait-oriented device doesn't work in all scenarios, one example of this is multi-window
     * mode.
     * Taking a natural portrait-oriented device in multi-window mode, rotating it 90 degrees (so
     * that it's in landscape), with the app open, and its window's width being smaller than its
     * height. Using the app's width and height would determine that the device isn't
     * naturally portrait-oriented, where in fact it is, which is why it is important to use the
     * size of the device instead.
     * </p>
     *
     * @param view            A {@link View} used to get the current {@link Display}.
     * @param rotationDegrees The device's rotation in degrees from its natural orientation.
     * @return Whether the device is naturally portrait-oriented.
     */
    private static boolean isNaturalPortrait(@NonNull final View view,
                                             final int rotationDegrees) {
        final Display display = view.getDisplay();
        if (display == null) {
            return true;
        }

        final Point deviceSize = new Point();
        display.getRealSize(deviceSize);

        final int width = deviceSize.x;
        final int height = deviceSize.y;
        return ((rotationDegrees == 0 || rotationDegrees == 180) && width < height) || (
                (rotationDegrees == 90 || rotationDegrees == 270) && width >= height);
    }
}
