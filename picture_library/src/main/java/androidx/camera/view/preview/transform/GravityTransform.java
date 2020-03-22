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
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Computes the x and y coordinates of the top left corner of the preview in order to position it
 * at the start (top left), center or end (bottom right) of its parent.
 */
final class GravityTransform {

    private GravityTransform() {
    }

    /**
     * Computes the x and y coordinates of the top left corner of {@code view} so that it's
     * aligned to the top left corner of its parent {@code container}.
     */
    static Point start() {
        return new Point(0, 0);
    }

    /**
     * Computes the x and y coordinates of the top left corner of {@code view} so that it's
     * centered in its parent {@code container}.
     */
    static Point center(@NonNull final View container, @NonNull final View view) {
        final int offsetX = (container.getWidth() - view.getWidth()) / 2;
        final int offsetY = (container.getHeight() - view.getHeight()) / 2;
        return new Point(offsetX, offsetY);
    }

    /**
     * Computes the x and y coordinates of the top left corner of {@code view} so that it's
     * aligned to the bottom right corner of its parent {@code container}.
     */
    static Point end(@NonNull final View container, @NonNull final View view) {
        final int offsetX = container.getWidth() - view.getWidth();
        final int offsetY = container.getHeight() - view.getHeight();
        return new Point(offsetX, offsetY);
    }
}
