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

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Author: cxx
 * Date: 2019-11-13
 * GitHub: https://github.com/ccolorcat
 */
class Utils {
    private static final boolean DEBUG = true;
    private static final String TAG = "Observable";
    private static final String METHOD_NAME_OBSERVER;

    static {
        Class<?> observerClass = Observer.class;
        String methodName = "onReceive";
        for (Method method : observerClass.getMethods()) {
            if (method.getDeclaringClass() == observerClass) {
                methodName = method.getName();
                break;
            }
        }
        METHOD_NAME_OBSERVER = methodName;
        printLog(Log.VERBOSE, "init, found receive method name in Observer: " + METHOD_NAME_OBSERVER);
    }

    static Class findReceiveType(Observer observer) {
        Type receiveType = null;
        if (observer instanceof TypedObserver) {
            receiveType = ((TypedObserver) observer).receiveType;
        }
        if (receiveType == null) {
            receiveType = quickFindObserverReceiveType(observer);
        }
        if (receiveType == null) {
            printLog(Log.WARN, "find receiveType failed and will continue... " + observer);
            receiveType = findObserverReceiveType(observer);
        }
        if (!(receiveType instanceof Class)) {
            throw new UnsupportedOperationException("unsupported receiveType " + receiveType);
        }
        return (Class) receiveType;
    }

    private static Type quickFindObserverReceiveType(Observer<?> observer) {
        Type type = observer.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType) type).getActualTypeArguments();
            if (args.length == 1) {
                return args[0];
            }
        }
        return null;
    }

    /**
     * this method always returns {@link Object} class when observer is lambda function.
     */
    @NonNull
    private static Type findObserverReceiveType(Observer<?> observer) {
        Method[] methods = observer.getClass().getMethods();
        Once<Type> once = new Once<>();
        for (Method method : methods) {
            if (METHOD_NAME_OBSERVER.equals(method.getName())
                    && method.getReturnType() == Void.TYPE
                    && !method.isBridge()
                    && !method.isVarArgs()) {
                Type[] args = method.getGenericParameterTypes();
                if (args.length == 1) {
                    once.setValue(args[0]);
                }
            }
        }
        return once.getValue();
    }

    @SuppressWarnings("unchecked")
    static void batchDispatch(Collection<? extends Observer> observers, Object event) {
        for (Observer observer : observers) {
            observer.onReceive(event);
        }
    }

    @SuppressWarnings("unchecked")
    static void dispatchBatchEvent(Observer observer, Collection<?> events) {
        for (Object event : events) {
            observer.onReceive(event);
        }
    }

    static <T> T requireNonNull(T t, String msg) {
        if (t == null) {
            throw new NullPointerException(msg);
        }
        return t;
    }

    static <T> T requireNonNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    static void printLog(int priority, String message) {
        if (DEBUG) {
            Log.println(priority, TAG, message);
        }
    }

    private Utils() {
        throw new AssertionError("no instance");
    }
}
