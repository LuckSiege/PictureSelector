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

/** Contains the required information to transform a camera preview. */
final class Transformation {

    private final float mScaleX;
    private final float mScaleY;
    private final float mOriginX;
    private final float mOriginY;
    private final float mRotation;

    Transformation(final float scaleX, final float scaleY, final float originX,
                   final float originY, final float rotation) {
        this.mScaleX = scaleX;
        this.mScaleY = scaleY;
        this.mOriginX = originX;
        this.mOriginY = originY;
        this.mRotation = rotation;
    }

    float getScaleX() {
        return mScaleX;
    }

    float getScaleY() {
        return mScaleY;
    }

    float getOriginX() {
        return mOriginX;
    }

    float getOriginY() {
        return mOriginY;
    }

    float getRotation() {
        return mRotation;
    }
}
