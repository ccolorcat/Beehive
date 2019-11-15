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

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author: cxx
 * Date: 2019-11-15
 */
public class MsgBoxHolder extends RecyclerView.ViewHolder {
    public final TextView textView;

    public MsgBoxHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(android.R.id.text1);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
    }
}
