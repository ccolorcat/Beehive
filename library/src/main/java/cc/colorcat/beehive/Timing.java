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
import androidx.lifecycle.Lifecycle;

/**
 * Author: cxx
 * Date: 2019-11-13
 * GitHub: https://github.com/ccolorcat
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class Timing {
    public static final Timing CREATE_DESTROY = new Timing(Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_DESTROY);
    public static final Timing START_STOP = new Timing(Lifecycle.Event.ON_START, Lifecycle.Event.ON_STOP);
    public static final Timing RESUME_PAUSE = new Timing(Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_PAUSE);

    public static final Timing DEFAULT = CREATE_DESTROY;


    @NonNull
    final Lifecycle.Event register;
    @NonNull
    final Lifecycle.Event unregister;

    private Timing(@NonNull Lifecycle.Event register, @NonNull Lifecycle.Event unregister) {
        this.register = Utils.requireNonNull(register);
        this.unregister = Utils.requireNonNull(unregister);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timing timing = (Timing) o;

        if (register != timing.register) return false;
        return unregister == timing.unregister;
    }

    @Override
    public int hashCode() {
        int result = register.hashCode();
        result = 31 * result + unregister.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Timing{" +
                "register=" + register +
                ", unregister=" + unregister +
                '}';
    }
}
