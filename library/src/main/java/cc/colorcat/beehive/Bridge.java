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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * Author: cxx
 * Date: 2019-12-20
 * GitHub: https://github.com/ccolorcat
 */
final class Bridge {
    private static final String ACTION = "cc.colorcat.beehive.bridge";
    private static final String UNIQUE = "bridge_unique_id";
    private static final String CACHE = "bridge_need_cache";
    private static final String CONTENT = "bridge_content";
    private static final String PID = "bridge_pid";

    private final String unique = UUID.randomUUID().toString();
    private final Bus bus;
    private final Context context;
    private String receiverPackageName;
    private BridgeReceiver bridgeReceiver;

    Bridge(@NonNull Bus bus, @NonNull Context context, String receiverPackageName) {
        this.bus = Utils.requireNonNull(bus, "bus == null");
        this.context = context.getApplicationContext();
        this.receiverPackageName = receiverPackageName;
    }

    void setReceiverPackageName(String packageName) {
        receiverPackageName = packageName;
    }

    void postParcelableEvent(@NonNull Parcelable event, boolean cache) {
        Intent intent = createIntent(cache);
        intent.putExtra(CONTENT, event);
        context.sendBroadcast(intent);
    }

    void postSerializableEvent(@NonNull Serializable event, boolean cache) {
        Intent intent = createIntent(cache);
        intent.putExtra(CONTENT, event);
        context.sendBroadcast(intent);
    }

    private Intent createIntent(boolean cache) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(UNIQUE, this.unique);
        intent.putExtra(PID, String.valueOf(Process.myPid()));
        intent.putExtra(CACHE, cache);
        if (receiverPackageName != null) {
            intent.setPackage(receiverPackageName);
        }
        return intent;
    }

    void startReceiveOtherProcessEvent() {
        registerBridgeReceiver();
    }

    void stopReceiveOtherProcessEvent() {
        unregisterBridgeReceiver();
    }

    private void registerBridgeReceiver() {
        if (bridgeReceiver == null) {
            bridgeReceiver = new BridgeReceiver(unique, bus);
            IntentFilter filter = new IntentFilter(ACTION);
            context.registerReceiver(bridgeReceiver, filter);
        }
    }

    private void unregisterBridgeReceiver() {
        if (bridgeReceiver != null) {
            context.unregisterReceiver(bridgeReceiver);
            bridgeReceiver = null;
        }
    }

    private static class BridgeReceiver extends BroadcastReceiver {
        private final String unique;
        private final Bus bus;

        private BridgeReceiver(@NonNull String unique, @NonNull Bus bus) {
            this.unique = unique;
            this.bus = bus;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isValidEvent(intent)) {
                Object data = intent.getParcelableExtra(CONTENT);
                if (data == null) {
                    data = intent.getSerializableExtra(CONTENT);
                }
                if (data != null) {
                    bus.post(data, intent.getBooleanExtra(CACHE, false));
                }
            }
        }

        private boolean isValidEvent(Intent intent) {
            final String unique = intent.getStringExtra(UNIQUE);
            final String pid = intent.getStringExtra(PID);
            return !TextUtils.isEmpty(unique)
                    && !TextUtils.equals(unique, this.unique)
                    && !TextUtils.isEmpty(pid)
                    && !TextUtils.equals(pid, String.valueOf(Process.myPid()));
        }
    }
}