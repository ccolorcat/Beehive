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

import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: cxx
 * Date: 2019-11-13
 * GitHub: https://github.com/ccolorcat
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Bus implements Observable {
    /**
     * key —— receiveType
     * value —— observers which can consume the event of receiveType
     */
    private final ConcurrentMap<Class<?>, Set<Observer<?>>> observers = new ConcurrentHashMap<>();
    /**
     * key —— eventType
     * value —— the instance of eventType
     */
    private final LruCache<Class<?>, Object> cachedEvents;

    public Bus(int maxCachedEvent) {
        this.cachedEvents = new LruCache<>(maxCachedEvent);
    }

    public void post(@NonNull Object event) {
        post(event, false);
    }

    public void post(@NonNull Object event, boolean cache) {
        Class<?> eventType = event.getClass();
        if (cache) {
            cachedEvents.put(eventType, event);
        }
        Collection<Observer> observers = findObservers(eventType);
        dispatchNewEvent(observers, event);
    }

    private Collection<Observer> findObservers(@NonNull Class<?> eventType) {
        Collection<Observer> obs = new LinkedList<>();
        for (Map.Entry<Class<?>, Set<Observer<?>>> entry : this.observers.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventType)) {
                obs.addAll(entry.getValue());
            }
        }
        return obs;
    }

    @Override
    public boolean register(@NonNull Observer observer) {
        return register(false, observer);
    }

    @Override
    public boolean register(boolean receiveCached, @NonNull Observer observer) {
        Utils.requireNonNull(observer, "observer == null");
        Class<?> receiveType = Utils.findReceiveType(observer);
        Set<Observer<?>> obs = this.observers.get(receiveType);
        if (obs == null) {
            obs = Collections.synchronizedSet(new LinkedHashSet<Observer<?>>());
            this.observers.put(receiveType, obs);
        }
        boolean result = obs.add(observer);
        if (Utils.DEBUG) {
            Utils.printLog(Log.VERBOSE, "register " + result + ":\n\t" + this.observers);
        }
        if (result && receiveCached) {
            Collection<Object> cachedEvents = findCachedEvents(receiveType);
            dispatchCachedEvents(observer, cachedEvents);
        }
        return result;
    }

    @Override
    public boolean unregister(final @NonNull Observer observer) {
        Utils.requireNonNull(observer, "observer == null");
        boolean result = false;
        Class<?> receiveType = Utils.findReceiveType(observer);
        Set<Observer<?>> obs = this.observers.get(receiveType);
        if (obs != null && obs.remove(observer)) {
            if (obs.isEmpty()) {
                this.observers.remove(receiveType);
            }
            result = true;
        }
        if (Utils.DEBUG) {
            Utils.printLog(Log.VERBOSE, "unregister " + result + ":\n\t" + this.observers);
        }
        return result;
    }

    @Override
    public void clear() {
        observers.clear();
        cachedEvents.evictAll();
    }

    private Collection<Object> findCachedEvents(@NonNull Class<?> receiveType) {
        Collection<Object> cachedEvents = new LinkedList<>();
        for (Map.Entry<Class<?>, Object> entry : this.cachedEvents.snapshot().entrySet()) {
            if (receiveType.isAssignableFrom(entry.getKey())) {
                cachedEvents.add(entry.getValue());
            }
        }
        return cachedEvents;
    }

    protected void dispatchNewEvent(@NonNull final Collection<Observer> observers, @NonNull final Object newEvent) {
        Utils.batchDispatch(observers, newEvent);
    }

    protected void dispatchCachedEvents(@NonNull final Observer observer, @NonNull final Collection<?> cachedEvents) {
        Utils.dispatchBatchEvent(observer, cachedEvents);
    }
}
