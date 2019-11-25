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
import android.os.SystemClock;

import androidx.annotation.NonNull;

import cc.colorcat.beehive.HandlerSubject;
import cc.colorcat.beehive.Producer;

/**
 * Author: cxx
 * Date: 2019-11-18
 */
class GiftSubject extends HandlerSubject<Gift> {
    private volatile boolean mContinueGift = true;
    private String[] mGiftName = {"banana", "pencil", "book", "phone", "computer"};
    private int mIndex = 0;

    GiftSubject() {
        super(new Handler(Looper.getMainLooper()));
        setProducer(new Producer<Gift>() {
            @NonNull
            @Override
            public Gift produce() {
                return new Gift(mGiftName[mIndex]);
            }
        });
    }

    void startProduce() {
        mContinueGift = true;
        new Thread("gift producer") {
            @Override
            public void run() {
                super.run();
                while (mContinueGift) {
                    SystemClock.sleep(3000);
                    mIndex = (mIndex + 1) % mGiftName.length;
                    notifyChanged();
                }
            }
        }.start();
    }

    void stopProduce() {
        mContinueGift = false;
    }
}
