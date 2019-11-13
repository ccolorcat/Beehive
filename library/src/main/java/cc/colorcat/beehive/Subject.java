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

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Author: cxx
 * Date: 2019-11-13
 * GitHub: https://github.com/ccolorcat
 */
public class Subject<T> implements Observable<T> {
    public static <T> Subject<T> create(@NonNull Producer<? extends T> producer) {
        return new Subject<>(Utils.requireNonNull(producer, "producer == null"));
    }

    public static <T> Subject<T> create(@NonNull Handler deliverer, @NonNull Producer<? extends T> producer) {
        Utils.requireNonNull(producer, "producer == null");
        Utils.requireNonNull(deliverer, "deliverer == null");
        return new HandlerSubject<>(deliverer, producer);
    }

    private final LinkedHashSet<Observer<? super T>> observers = new LinkedHashSet<>();
    private final Producer<? extends T> producer;
    private volatile T cachedEvent;

    public Subject(@NonNull Producer<? extends T> producer) {
        this.producer = Utils.requireNonNull(producer, "producer == null");
    }

    @NonNull
    public T get() {
        return updateAndCache();
    }

    public T getCached() {
        return cachedEvent;
    }

    @Override
    public boolean register(@NonNull Observer<? super T> observer) {
        return register(false, observer);
    }

    @Override
    public boolean register(boolean receiveCached, @NonNull Observer<? super T> observer) {
        Utils.requireNonNull(observer, "observer == null");
        synchronized (observers) {
            boolean result = observers.add(observer);
            if (receiveCached && result && cachedEvent != null) {
                dispatch(Collections.singletonList(observer), cachedEvent);
            }
            return result;
        }
    }

    @Override
    public boolean unregister(@NonNull Observer<? super T> observer) {
        Utils.requireNonNull(observer, "observer == null");
        synchronized (observers) {
            return observers.remove(observer);
        }
    }

    public void clear() {
        synchronized (observers) {
            cachedEvent = null;
            observers.clear();
        }
    }

    public void notifyChanged() {
        synchronized (observers) {
            dispatch(observers, updateAndCache());
        }
    }

    private T updateAndCache() {
        T result = Utils.requireNonNull(producer.produce(), "produce returned null");
        cachedEvent = result;
        return result;
    }

    protected void dispatch(@NonNull Collection<? extends Observer<? super T>> observers, @NonNull T event) {
        Utils.batchDispatch(observers, event);
    }

    @NonNull
    @Override
    public String toString() {
        return "Subject{" +
                "observers=" + observers +
                ", producer=" + producer +
                ", cachedEvent=" + cachedEvent +
                '}';
    }
}