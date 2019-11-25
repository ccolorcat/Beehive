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
@SuppressWarnings("unused")
public class HandlerSubject<T> extends Subject<T> implements LifeObservable<T> {
    private final Handler handler;

    public HandlerSubject(Handler handler) {
        super();
        this.handler = handler;
    }

    public HandlerSubject(Handler handler, @NonNull Producer<? extends T> producer) {
        super(producer);
        this.handler = handler;
    }

    @Override
    public void bind(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        this.bind(false, Timing.DEFAULT, owner, observer);
    }

    @Override
    public void bind(boolean receiveCached, @NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        this.bind(receiveCached, Timing.DEFAULT, owner, observer);
    }

    @Override
    public void bind(@NonNull Timing timing, @NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        this.bind(false, timing, owner, observer);
    }

    @Override
    public void bind(boolean receiveCached, @NonNull Timing timing, @NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        owner.getLifecycle().addObserver(new ObserverWrapper<>(this, receiveCached, timing, observer));
    }

    @Override
    protected void dispatch(@NonNull final Collection<? extends Observer<? super T>> observers, @NonNull final T event) {
        if (handler == null || handler.getLooper() == Looper.myLooper()) {
            super.dispatch(observers, event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    HandlerSubject.super.dispatch(observers, event);
                }
            });
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "HandlerSubject{" +
                "handler=" + handler +
                "} " + super.toString();
    }
}
