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

import android.graphics.Matrix;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.camera.view.PreviewView;

/**
 * Transforms the camera preview using a supported {@link PreviewView.ScaleType} or a custom
 * {@link Transformation}.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class PreviewTransform {

    private PreviewTransform() {
    }

    /**
     * Transforms a preview using a supported {@link PreviewView.ScaleType}.
     *
     * @param container   A parent {@link View} that wraps {@code view}.
     * @param view        A {@link View} that wraps the camera preview.
     * @param aspectRatio A {@link Size} whose aspect ratio must be maintained when scaling the
     *                    preview.
     * @param scaleType   A supported {@link PreviewView.ScaleType} to apply on the camera preview.
     */
    public static void transform(@NonNull final View container, @NonNull final View view,
                                 @NonNull final Size aspectRatio, @NonNull final PreviewView.ScaleType scaleType) {
        final Transformation transformation = ScaleTypeTransform.getTransformation(container,
                view, aspectRatio, scaleType);
        applyTransformationToPreview(transformation, view);
    }

    /**
     * Transforms a preview using a user provided custom {@linkplain Matrix transformation}.
     *
     * @param view   A {@link View} that wraps the camera preview.
     * @param matrix A user provided custom preview transformation
     */
    public static void transform(@NonNull final View view, @NonNull final Matrix matrix) {
        final Transformation transformation = CustomTransform.getTransformation(view, matrix);
        applyTransformationToPreview(transformation, view);
    }

    /**
     * Applies a {@link Transformation} to a preview.
     *
     * @param transformation A transformation to apply on the preview.
     * @param view           A {@link View} that wraps the camera preview.
     */
    private static void applyTransformationToPreview(@NonNull final Transformation transformation,
                                                     @NonNull final View view) {
        view.setScaleX(transformation.getScaleX());
        view.setScaleY(transformation.getScaleY());
        view.setX(transformation.getOriginX());
        view.setY(transformation.getOriginY());
        view.setRotation(transformation.getRotation());
    }
}
