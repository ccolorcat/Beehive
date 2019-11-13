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
import androidx.lifecycle.LifecycleOwner;

/**
 * Author: cxx
 * Date: 2019-11-13
 * GitHub: https://github.com/ccolorcat
 */
public interface LifeObservable<T> extends Observable<T> {
    void bind(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer);

    void bind(boolean receiveCached, @NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer);

    void bind(@NonNull Timing timing, @NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer);

    void bind(boolean receiveCached, @NonNull Timing timing, @NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer);
}
