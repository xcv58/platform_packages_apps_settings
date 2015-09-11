/*
 *  @author Ryan Huang <ryanhuang@cs.ucsd.edu>
 *
 *  Copyright (c) 2015, The DefDroid Project
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.android.settings.defense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.settings.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 */
public class DefDroidActionLogAdapter extends BaseAdapter {
    private ArrayList<DefDroidActionLog.ActionLogItem> mItems;
    private LayoutInflater mInflater;

    public DefDroidActionLogAdapter(Context context, ArrayList<DefDroidActionLog.ActionLogItem> items) {
        mItems = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addLogItem(DefDroidActionLog.ActionLogItem item) {
        mItems.add(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.defdroid_action_log_item, null);
            viewHolder = new LogItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LogItemViewHolder) convertView.getTag();
        }
        DefDroidActionLog.ActionLogItem item = mItems.get(position);
        viewHolder.setViews(item);
        return convertView;
    }

    public static class LogItemViewHolder {
        public TextView actionTV;
        public TextView appTV;
        public TextView contentTV;
        public TextView extraTV;

        private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");

        private static int mDefaultColor = -1;

        public LogItemViewHolder(View view) {
            actionTV = (TextView) view.findViewById(R.id.actionText);
            appTV = (TextView) view.findViewById(R.id.appText);
            contentTV = (TextView) view.findViewById(R.id.contentText);
            extraTV = (TextView) view.findViewById(R.id.extraText);
            if (mDefaultColor < 0)
                mDefaultColor = view.getResources().getColor(R.color.defdroid_log_text_color);
        }

        public void setViews(DefDroidActionLog.ActionLogItem item) {
            if (item.event != null) {
                actionTV.setText(item.event.actionType.toString());
                appTV.setText(item.appLabel);
                contentTV.setText(item.event.extra);
                extraTV.setText(SDF.format(new Date(item.event.timeStamp)));
                extraTV.setVisibility(View.GONE);
            }
        }
    }

}
