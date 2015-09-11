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

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.defense.DefenseActionEvent;
import android.defense.DefenseFileLogger;
import android.defense.IDefenseLogReader;
import android.defense.IDefenseLogger;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settings.R;

import java.util.ArrayList;

/**
 *
 */
public class DefDroidActionLog extends Fragment implements AbsListView.OnItemClickListener {
    private static final String TAG = "DefDroidActionLog";
    private Activity mActivity;
    private TextView mNoLogs;
    private ListView mLogList;
    private DefDroidActionLogAdapter mAdapter;
    private PackageManager mPm;

    private static final IDefenseLogger mLogger = DefenseFileLogger.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        return inflater.inflate(R.layout.defdroid_action_log, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLogs();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPm = mActivity.getPackageManager();
        mNoLogs = (TextView) mActivity.findViewById(R.id.error);
        mLogList = (ListView) mActivity.findViewById(R.id.log_list);
        mLogList.setOnItemClickListener(this);
        loadLogs();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView extraTV = (TextView) view.findViewById(R.id.extraText);
        if (extraTV != null) {
            if (extraTV.getVisibility() == View.GONE) {
                extraTV.setVisibility(View.VISIBLE);
            } else {
                extraTV.setVisibility(View.GONE);
            }
        }
    }

    public class ActionLogItem {
        public String appLabel;
        public DefenseActionEvent event;
        public ActionLogItem(String label, DefenseActionEvent event) {
            this.appLabel = label;
            this.event = event;
        }
    }

    private void loadLogs() {
        IDefenseLogReader reader = mLogger.newLogReader();
        mAdapter = new DefDroidActionLogAdapter(mActivity, new ArrayList<ActionLogItem>());
        mNoLogs.setVisibility(View.GONE);
        mLogList.setVisibility(View.VISIBLE);
        mLogList.setAdapter(mAdapter);
        new DefenseLogReadTask(reader).execute();
    }

    private class DefenseLogReadTask extends AsyncTask<Void, ActionLogItem, Void> {
        private IDefenseLogReader mReader;
        private boolean mEmpty;

        public DefenseLogReadTask(IDefenseLogReader reader) {
            mReader = reader;
            mEmpty = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mReader != null) {
                DefenseActionEvent event;
                while ((event = mReader.next()) != null) {
                    String label;
                    try {
                        final ApplicationInfo info = mPm.getApplicationInfo(event.targetPkg, 0);
                        label = info.loadLabel(mPm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        label = "U" + event.targetUid;
                    }
                    mEmpty = false;
                    publishProgress(new ActionLogItem(label, event));
                }
                mReader.close();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ActionLogItem...items) {
            for (ActionLogItem item:items) {
                mAdapter.addLogItem(item);
            }
            mAdapter.notifyDataSetChanged();
        }

        protected void onPostExecute(Void result) {
            if (mEmpty) {
                mNoLogs.setText(R.string.defdroid_no_action_logs);
                mNoLogs.setVisibility(View.VISIBLE);
                mLogList.setVisibility(View.GONE);
                mLogList.setAdapter(null);
            } else {
                mAdapter.notifyDataSetChanged();
                mLogList.setSelection(mAdapter.getCount() - 1);
            }
        }
    }
}
