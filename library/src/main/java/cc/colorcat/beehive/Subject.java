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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Author: cxx
 * Date: 2019-11-13
 * GitHub: https://github.com/ccolorcat
 */
public class Subject<T> implements Observable<T> {
    private final LinkedHashSet<Observer<? super T>> observers = new LinkedHashSet<>();
    private final Producer<? extends T> producer;
    private volatile T cachedEvent;

    public Subject(@NonNull Producer<? extends T> producer) {
        this.producer = Utils.requireNonNull(producer, "producer == null");
    }

    @Override
    public boolean register(@NonNull Observer<? super T> observer) {
        return register(false, observer);
    }

    @Override
    public boolean register(boolean receiveCached, @NonNull Observer<? super T> observer) {
        Utils.requireNonNull(observer, "observer == null");
        synchronized (this.observers) {
            boolean result = this.observers.add(observer);
            if (receiveCached && result && cachedEvent != null) {
                dispatch(Collections.singletonList(observer), cachedEvent);
            }
            return result;
        }
    }

    @Override
    public boolean unregister(@NonNull Observer<? super T> observer) {
        Utils.requireNonNull(observer, "observer == null");
        synchronized (this.observers) {
            return this.observers.remove(observer);
        }
    }

    @Override
    public void clear() {
        synchronized (observers) {
            cachedEvent = null;
            observers.clear();
        }
    }

    @NonNull
    public T get() {
        return produceAndCache();
    }

    public T getCached() {
        return cachedEvent;
    }

    public void notifyChanged() {
        synchronized (observers) {
            dispatch(observers, produceAndCache());
        }
    }

    private T produceAndCache() {
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