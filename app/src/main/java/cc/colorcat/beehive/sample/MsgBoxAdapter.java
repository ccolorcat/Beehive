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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Author: cxx
 * Date: 2019-11-18
 */
public class MsgBoxAdapter extends RecyclerView.Adapter<MsgBoxAdapter.MsgBoxHolder> {
    private List<Message> mMessages;

    MsgBoxAdapter(@NonNull List<Message> messages) {
        mMessages = messages;
    }

    @NonNull
    @Override
    public MsgBoxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_text, parent, false);
        return new MsgBoxHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgBoxHolder holder, int position) {
        holder.textView.setText(mMessages.get(position).said());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    static class MsgBoxHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        private MsgBoxHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
