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

import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Author: cxx
 * Date: 2019-12-20
 * GitHub: https://github.com/ccolorcat
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class HandlerAmphibianBus extends HandlerBus {
    private final Bridge bridge;

    public HandlerAmphibianBus(@NonNull Context context, int maxCachedEvent, Handler handler) {
        this(context, null, maxCachedEvent, handler);
    }

    public HandlerAmphibianBus(@NonNull Context context, String receiverPackageName, int maxCachedEvent, Handler handler) {
        super(maxCachedEvent, handler);
        bridge = new Bridge(this, context, receiverPackageName);
    }

    public void setReceiverPackageName(String receiverPackageName) {
        bridge.setReceiverPackageName(receiverPackageName);
    }

    public void postAmphibian(@NonNull Parcelable event) {
        postAmphibian(event, false);
    }

    public void postAmphibian(@NonNull Parcelable event, boolean cache) {
        Utils.requireNonNull(event, "event == null");
        post(event, cache);
        bridge.postParcelableEvent(event, cache);
    }

    public void postAmphibian(@NonNull Serializable event) {
        postAmphibian(event, false);
    }

    public void postAmphibian(@NonNull Serializable event, boolean cache) {
        Utils.requireNonNull(event, "event == null");
        post(event, cache);
        bridge.postSerializableEvent(event, cache);
    }

    public void startReceiveOtherProcessEvent() {
        bridge.startReceiveOtherProcessEvent();
    }

    public void stopReceiveOtherProcessEvent() {
        bridge.stopReceiveOtherProcessEvent();
    }
}
