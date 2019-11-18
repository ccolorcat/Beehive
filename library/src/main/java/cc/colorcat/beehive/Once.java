/*
 * Copyright 2019 cxx
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.colorcat.beehive;

import androidx.annotation.NonNull;

/**
 * Author: cxx
 * Date: 2019-11-13
 * GitHub: https://github.com/ccolorcat
 */
final class Once<T> {
    private T value;

    void setValue(@NonNull T value) {
        if (this.value != null) {
            throw new IllegalStateException("the value has been set.");
        }
        this.value = Utils.requireNonNull(value, "value == null");
    }

    @NonNull
    T getValue() {
        if (value == null) {
            throw new IllegalStateException("the value has not been set.");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Once<?> once = (Once<?>) o;

        return value != null ? value.equals(once.value) : once.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Once{" +
                "value=" + value +
                '}';
    }
}
