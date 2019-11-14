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
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * Author: cxx
 * Date: 2019-11-13
 * GitHub: https://github.com/ccolorcat
 */
public abstract class LifeObserver<T> extends TypedObserver<T> implements LifecycleEventObserver {
    protected Observable<T> observable;
    protected final boolean receiveCached;
    protected final Timing timing;

    public LifeObserver() {
        this(false);
    }

    public LifeObserver(boolean receiveCached) {
        this(receiveCached, Timing.DEFAULT);
    }

    public LifeObserver(@NonNull Timing timing) {
        this(false, timing);
    }

    public LifeObserver(boolean receiveCached, @NonNull Timing timing) {
        this(null, receiveCached, null, timing);
    }

    protected LifeObserver(Observable<T> observable, boolean receiveCached, Class<? extends T> receiveType, @NonNull Timing timing) {
        super(receiveType);
        this.observable = observable;
        this.receiveCached = receiveCached;
        this.timing = Utils.requireNonNull(timing, "timing == null");
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event.equals(timing.register)) {
            observable.register(receiveCached, this);
        } else if (event.equals(timing.unregister)) {
            observable.unregister(this);
        }
    }
}