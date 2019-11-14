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

package cc.colorcat.beehive.sample;

import android.os.Handler;
import android.os.Looper;

import cc.colorcat.beehive.HandlerBus;

/**
 * Author: cxx
 * Date: 2019-11-14
 * GitHub: https://github.com/ccolorcat
 */
public final class GlobalBus extends HandlerBus {

    public static GlobalBus get() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final GlobalBus INSTANCE = new GlobalBus();
    }

    private GlobalBus() {
        super(8, new Handler(Looper.getMainLooper()));
    }
}
