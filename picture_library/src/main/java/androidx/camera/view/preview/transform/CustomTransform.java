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
import android.view.View;

import androidx.annotation.NonNull;

final class CustomTransform {

    private CustomTransform() {
    }

    /**
     * Converts a {@link Matrix}, which represents a user provided custom preview transformation,
     * to a {@link Transformation}.
     */
    static Transformation getTransformation(@NonNull final View view,
                                            @NonNull final Matrix matrix) {
        final float[] values = new float[9];
        matrix.getValues(values);

        final float scaleX = values[Matrix.MSCALE_X];
        final float scaleY = values[Matrix.MSCALE_Y];
        final float originX = view.getX() + values[Matrix.MTRANS_X];
        final float originY = view.getY() + values[Matrix.MTRANS_Y];
        final float rotation = getRotationDegrees(values);
        return new Transformation(scaleX, scaleY, originX, originY, rotation);
    }

    private static float getRotationDegrees(final float[] values) {
        /*
          A translation matrix can be represented as:
          (1  0  transX)
          (0  1  transX)
          (0  0  1)

          A rotation Matrix of ψ degrees can be represented as:
          (cosψ  -sinψ  0)
          (sinψ  cosψ   0)
          (0     0      1)

          A scale matrix can be represented as:
          (scaleX  0       0)
          (0       scaleY  0)
          (0       0       0)

          Meaning a transformed matrix can be represented as:
          (scaleX * cosψ    -scaleX * sinψ    transX)
          (scaleY * sinψ    scaleY * cosψ     transY)
          (0                0                 1)

          Using the following 2 equalities:
          scaleX * cosψ = matrix[0][0]
          -scaleX * sinψ = matrix[0][1]

          The following is deduced:
          -tanψ = matrix[0][1] / matrix[0][0]

          Or:
          ψ = -arctan(matrix[0][1] / matrix[0][0])
         */
        final double angle = -Math.atan2(values[Matrix.MSKEW_X], values[Matrix.MSCALE_X]);
        return (float) Math.toDegrees(angle);
    }
}
