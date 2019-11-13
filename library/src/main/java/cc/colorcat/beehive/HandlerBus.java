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

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import java.util.Collection;

/**
 * Author: cxx
 * Date: 2019-11-13
 * GitHub: https://github.com/ccolorcat
 */
public class HandlerBus extends Bus implements LifeObservable {
    private final Handler handler;

    public HandlerBus(int maxCachedEvent, Handler handler) {
        super(maxCachedEvent);
        this.handler = handler;
    }

    @Override
    public void bind(@NonNull LifecycleOwner owner, @NonNull Observer observer) {
        this.bind(false, Timing.DEFAULT, owner, observer);
    }

    @Override
    public void bind(boolean receiveCached, @NonNull LifecycleOwner owner, @NonNull Observer observer) {
        this.bind(receiveCached, Timing.DEFAULT, owner, observer);
    }

    @Override
    public void bind(@NonNull Timing timing, @NonNull LifecycleOwner owner, @NonNull Observer observer) {
        this.bind(false, timing, owner, observer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(boolean receiveCached, @NonNull Timing timing, @NonNull LifecycleOwner owner, @NonNull Observer observer) {
        owner.getLifecycle().addObserver(new ObserverWrapper<>(this, receiveCached, timing, observer));
    }

    public void bind(@NonNull LifecycleOwner owner, @NonNull LifeObserver observer) {
        observer.observable = this;
        owner.getLifecycle().addObserver(observer);
    }

    @Override
    protected void dispatchNewEvent(@NonNull final Collection<Observer> observers, @NonNull final Object newEvent) {
        if (handler == null || handler.getLooper() == Looper.myLooper()) {
            super.dispatchNewEvent(observers, newEvent);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    HandlerBus.super.dispatchNewEvent(observers, newEvent);
                }
            });
        }
    }

    @Override
    protected void dispatchCachedEvents(@NonNull final Observer observer, @NonNull final Collection<?> cachedEvents) {
        if (handler == null || handler.getLooper() == Looper.myLooper()) {
            super.dispatchCachedEvents(observer, cachedEvents);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    HandlerBus.super.dispatchCachedEvents(observer, cachedEvents);
                }
            });
        }
    }
}