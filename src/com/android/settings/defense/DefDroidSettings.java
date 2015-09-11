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
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.defense.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.*;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.HashSet;
import java.util.Set;

/**
 * Settings interface for DefDroid.
 */
public class DefDroidSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "DefDroidSettings";

    private CheckBoxPreference mDefenseEnabledPref;
    private SharedPreferences mWhiteListPref;
    private ListPreference mRateLimitWindowPref;
    private ListPreference mGCWindowPref;

    private CheckBoxPreference mWakelockGuardianEnabledPref;
    private CheckBoxPreference mGPSGuardianEnabledPref;
    private CheckBoxPreference mAlarmGuardianEnabledPref;
    private CheckBoxPreference mSensorGuardianEnabledPref;
    private CheckBoxPreference mNetworkGuardianEnabledPref;
    private CheckBoxPreference mStorageGuardianEnabledPref;
    private CheckBoxPreference mNotificationGuardianEnabledPref;
    private CheckBoxPreference mCPUGuardianEnabledPref;
    private CheckBoxPreference mBluetoothGuardianEnabledPref;

    private ListPreference mWakelockFreqPref;
    private EditTextPreference mWakelockDurationThrottlePref;
    private EditTextPreference mWakelockRateLimitPref;

    private ListPreference mLocationFreqPref;
    private EditTextPreference mLocationDurationThrottlePref;
    private EditTextPreference mLocationRateLimitPref;

    private ListPreference mAlarmFreqPref;
    private EditTextPreference mAlarmRateLimitPref;

    private ListPreference mSensorFreqPref;
    private EditTextPreference mSensorRateLimitPref;

    private ListPreference mNetworkFreqPref;
    private EditTextPreference mNetworkDataLimitPref;
    private EditTextPreference mNetworkMaxBadnessPref;

    private SettingsHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.defdroid_settings);
        mDefenseEnabledPref = (CheckBoxPreference) findPreference("defense_mode");
        // use findPreference and getSharedPreferences wont' work...
        mWhiteListPref = getActivity().getSharedPreferences("whitelist",
                Activity.MODE_PRIVATE);
        mRateLimitWindowPref = (ListPreference) findPreference("rate_limit_window");
        mRateLimitWindowPref.setSummary(mRateLimitWindowPref.getEntry());
        mGCWindowPref = (ListPreference) findPreference("gc_window");
        mGCWindowPref.setSummary(mGCWindowPref.getEntry());

        mWakelockGuardianEnabledPref = (CheckBoxPreference) findPreference("enable_wakelock_guardian");
        mGPSGuardianEnabledPref = (CheckBoxPreference) findPreference("enable_gps_guardian");
        mAlarmGuardianEnabledPref = (CheckBoxPreference) findPreference("enable_alarm_guardian");
        mSensorGuardianEnabledPref = (CheckBoxPreference) findPreference("enable_sensor_guardian");
        mNetworkGuardianEnabledPref = (CheckBoxPreference) findPreference("enable_network_guardian");
        mStorageGuardianEnabledPref = (CheckBoxPreference) findPreference("enable_storage_guardian");
        mNotificationGuardianEnabledPref = (CheckBoxPreference) findPreference("enable_notification_guardian");
        mCPUGuardianEnabledPref = (CheckBoxPreference) findPreference("enable_cpu_guardian");
        mBluetoothGuardianEnabledPref = (CheckBoxPreference) findPreference("enable_bluetooth_guardian");

        mWakelockFreqPref = (ListPreference) findPreference("wakelock_checker_frequency");
        mWakelockFreqPref.setSummary(mWakelockFreqPref.getEntry());
        mWakelockDurationThrottlePref = (EditTextPreference) findPreference("wakelock_duration_cutoff");
        mWakelockDurationThrottlePref.setSummary(mWakelockDurationThrottlePref.getText() + " " + getDurationCutoffUnit());
        mWakelockRateLimitPref = (EditTextPreference) findPreference("wakelock_rate_limit");
        mWakelockRateLimitPref.setSummary(mWakelockRateLimitPref.getText() + " " + getRateLimitUnit());

        mLocationFreqPref = (ListPreference) findPreference("gps_checker_frequency");
        mLocationFreqPref.setSummary(mLocationFreqPref.getEntry());
        mLocationDurationThrottlePref = (EditTextPreference) findPreference("gps_duration_cutoff");
        mLocationDurationThrottlePref.setSummary(mLocationDurationThrottlePref.getText() + " " + getDurationCutoffUnit());
        mLocationRateLimitPref = (EditTextPreference) findPreference("gps_rate_limit");
        mLocationRateLimitPref.setSummary(mLocationRateLimitPref.getText() + " " + getRateLimitUnit());

        mAlarmFreqPref = (ListPreference) findPreference("alarm_checker_frequency");
        mAlarmFreqPref.setSummary(mAlarmFreqPref.getEntry());
        mAlarmRateLimitPref = (EditTextPreference) findPreference("alarm_rate_limit");
        mAlarmRateLimitPref.setSummary(mAlarmRateLimitPref.getText() + " " + getRateLimitUnit());

        mSensorFreqPref = (ListPreference) findPreference("sensor_checker_frequency");
        mSensorFreqPref.setSummary(mSensorFreqPref.getEntry());
        mSensorRateLimitPref = (EditTextPreference) findPreference("sensor_rate_limit");
        mSensorRateLimitPref.setSummary(mSensorRateLimitPref.getText() + " " + getRateLimitUnit());

        mNetworkFreqPref = (ListPreference) findPreference("network_checker_frequency");
        mNetworkFreqPref.setSummary(mNetworkFreqPref.getEntry());
        mNetworkDataLimitPref = (EditTextPreference) findPreference("network_data_limit");
        mNetworkDataLimitPref.setSummary(mNetworkDataLimitPref.getText() + " " + getSizeUnit());
        mNetworkMaxBadnessPref = (EditTextPreference) findPreference("network_max_badness");

        mHandler = new SettingsHandler();
        mHandler.sendEmptyMessage(SettingsHandler.MSG_SYNC_WITH_SECURE_SETTING);

        registerChangeListener();
    }

    private String bytesToMB(long size) {
        return StringUtils.formatDouble((double) size / NetUtils.MB, 2);
    }
    private String millisToMinutes(long time) {
        return StringUtils.formatDouble((double) time / TimeUtils.MILLIS_PER_MINUTE, 2);
    }
    private String getDurationCutoffUnit() {
        return getResources().getString(R.string.defense_duration_cutoff_unit);
    }
    private String getRateLimitUnit() { return getResources().getString(R.string.defense_rate_limit_unit);}
    private String getSizeUnit() { return getResources().getString(R.string.defense_data_unit);}


    private void syncWithSecureSettings() {
        final ContentResolver resolver = getContentResolver();
        DefenseSettings settings = DefenseSettingsUtils.readDefenseSettingsLocked(resolver);

        mDefenseEnabledPref.setChecked(settings.serviceEnabled);
        Set<String> whiteListSet = DefenseSettingsUtils.whitelistToSet(settings.whiteList);
        mWhiteListPref.edit().putStringSet(DefDroidWhiteList.WL_PKGS_KEY,
                whiteListSet).commit();
        updateFreqListPref(mRateLimitWindowPref, settings.rateLimitWindow);
        updateFreqListPref(mGCWindowPref, settings.gcWindow);

        mWakelockGuardianEnabledPref.setChecked(settings.alarmGuardianEnabled);
        mGPSGuardianEnabledPref.setChecked(settings.gpsGuardianEnabled);
        mAlarmGuardianEnabledPref.setChecked(settings.alarmGuardianEnabled);
        mSensorGuardianEnabledPref.setChecked(settings.sensorGuardianEnabled);
        mNetworkGuardianEnabledPref.setChecked(settings.networkGuardianEnabled);
        mStorageGuardianEnabledPref.setChecked(settings.storageGuardianEnabled);
        mNotificationGuardianEnabledPref.setChecked(settings.notificationGuardianEnabled);
        mCPUGuardianEnabledPref.setChecked(settings.cpuGuardianEnabled);
        mBluetoothGuardianEnabledPref.setChecked(settings.bluetoothGuardianEnabled);

        updateFreqListPref(mWakelockFreqPref, settings.wakelockCheckerFrequency);
        updateThrottleEditPref(mWakelockDurationThrottlePref, settings.wakelockDurationThrottle);
        updateRateLimitEditPref(mWakelockRateLimitPref, settings.wakelockRateLimit);

        updateFreqListPref(mLocationFreqPref, settings.locationCheckerFrequency);
        updateThrottleEditPref(mLocationDurationThrottlePref, settings.locationDurationThrottle);
        updateRateLimitEditPref(mLocationRateLimitPref, settings.locationRateLimit);

        updateFreqListPref(mAlarmFreqPref, settings.alarmCheckerFrequency);
        updateRateLimitEditPref(mAlarmRateLimitPref, settings.alarmRateLimit);

        updateFreqListPref(mSensorFreqPref, settings.sensorCheckerFrequency);
        updateRateLimitEditPref(mSensorRateLimitPref, settings.sensorRateLimit);

        updateFreqListPref(mNetworkFreqPref, settings.networkCheckerFrequency);
        updateDataEditPref(mNetworkDataLimitPref, settings.networkDataLimit);
        updateGeneralIntEditPref(mNetworkMaxBadnessPref, settings.networkMaxBadness);
    }

    private void updateFreqListPref(ListPreference preference, long freqInMillis) {
        String minute = millisToMinutes(freqInMillis);
        int index = preference.findIndexOfValue(minute);
        if (index >= 0) {
            preference.setValueIndex(index);
            preference.setSummary(preference.getEntries()[index]);
        }
    }

    private void updateGeneralStringEditPref(EditTextPreference preference, String data) {
        preference.setText(data);
        if (data.length() > 32)
            preference.setSummary(data.substring(0, 32) + "...");
        else
            preference.setSummary(data);
    }

    private void updateGeneralIntEditPref(EditTextPreference preference, int data) {
        String sData = String.valueOf(data);
        preference.setText(sData);
        preference.setSummary(sData);
    }

    private void updateRateLimitEditPref(EditTextPreference preference, float rateLimit) {
        String sRateLimit = String.valueOf(rateLimit);
        preference.setText(sRateLimit);
        preference.setSummary(sRateLimit + " " + getRateLimitUnit());
    }

    private void updateThrottleEditPref(EditTextPreference preference, long throttle) {
        String sThrottle = millisToMinutes(throttle);
        preference.setText(sThrottle);
        preference.setSummary(sThrottle + " " + getDurationCutoffUnit());
    }

    private void updateDataEditPref(EditTextPreference preference, long dataSize) {
        String sData = bytesToMB(dataSize);
        preference.setText(sData);
        preference.setSummary(sData + " " + getSizeUnit());
    }

    public void registerChangeListener() {
        mDefenseEnabledPref.setOnPreferenceChangeListener(this);
        mRateLimitWindowPref.setOnPreferenceChangeListener(this);
        mGCWindowPref.setOnPreferenceChangeListener(this);

        mWakelockGuardianEnabledPref.setOnPreferenceChangeListener(this);
        mGPSGuardianEnabledPref.setOnPreferenceChangeListener(this);
        mAlarmGuardianEnabledPref.setOnPreferenceChangeListener(this);
        mSensorGuardianEnabledPref.setOnPreferenceChangeListener(this);
        mNetworkGuardianEnabledPref.setOnPreferenceChangeListener(this);
        mStorageGuardianEnabledPref.setOnPreferenceChangeListener(this);
        mNotificationGuardianEnabledPref.setOnPreferenceChangeListener(this);
        mCPUGuardianEnabledPref.setOnPreferenceChangeListener(this);
        mBluetoothGuardianEnabledPref.setOnPreferenceChangeListener(this);

        mWakelockFreqPref.setOnPreferenceChangeListener(this);
        mWakelockDurationThrottlePref.setOnPreferenceChangeListener(this);
        mWakelockRateLimitPref.setOnPreferenceChangeListener(this);

        mLocationFreqPref.setOnPreferenceChangeListener(this);
        mLocationDurationThrottlePref.setOnPreferenceChangeListener(this);
        mLocationRateLimitPref.setOnPreferenceChangeListener(this);

        mAlarmFreqPref.setOnPreferenceChangeListener(this);
        mAlarmRateLimitPref.setOnPreferenceChangeListener(this);

        mSensorFreqPref.setOnPreferenceChangeListener(this);
        mSensorRateLimitPref.setOnPreferenceChangeListener(this);

        mNetworkFreqPref.setOnPreferenceChangeListener(this);
        mNetworkDataLimitPref.setOnPreferenceChangeListener(this);
        mNetworkMaxBadnessPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mDefenseEnabledPref) {
            Log.d(TAG, "Enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeServiceEnabled(value, getContentResolver());
            return true;
        }  else if (preference == mRateLimitWindowPref) {
            Log.d(TAG, "Rate limit window changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mRateLimitWindowPref, value);

            long window = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            DefenseSettingsUtils.writeRateLimitWindow(window, getContentResolver());
            return true;
        }  else if (preference == mGCWindowPref) {
            Log.d(TAG, "GC window changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mGCWindowPref, value);

            long window = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            DefenseSettingsUtils.writeGCWindow(window, getContentResolver());
            return true;
        } else if (preference == mWakelockGuardianEnabledPref) {
            Log.d(TAG, "Wakelock guardian enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeWakelockGuardianEnabled(value, getContentResolver());
            return true;
        } else if (preference == mGPSGuardianEnabledPref) {
            Log.d(TAG, "GPS guardian enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeLocationGuardianEnabled(value, getContentResolver());
            return true;
        } else if (preference == mAlarmGuardianEnabledPref) {
            Log.d(TAG, "Alarm guardian enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeAlarmGuardianEnabled(value, getContentResolver());
            return true;
        } else if (preference == mSensorGuardianEnabledPref) {
            Log.d(TAG, "Sensor guardian enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeSensorGuardianEnabled(value, getContentResolver());
            return true;
        } else if (preference == mNetworkGuardianEnabledPref) {
            Log.d(TAG, "Network guardian enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeNetworkGuardianEnabled(value, getContentResolver());
            return true;
        } else if (preference == mStorageGuardianEnabledPref) {
            Log.d(TAG, "Storage guardian enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeStorageGuardianEnabled(value, getContentResolver());
            return true;
        } else if (preference == mNotificationGuardianEnabledPref) {
            Log.d(TAG, "Notification guardian enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeNotificationGuardianEnabled(value, getContentResolver());
            return true;
        } else if (preference == mCPUGuardianEnabledPref) {
            Log.d(TAG, "CPU guardian enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeCPUGuardianEnabled(value, getContentResolver());
            return true;
        } else if (preference == mBluetoothGuardianEnabledPref) {
            Log.d(TAG, "Bluetooth guardian enabled changed to " + newValue);
            boolean value = (Boolean) newValue;
            DefenseSettingsUtils.writeBluetoothGuardianEnabled(value, getContentResolver());
            return true;
        } else if (preference == mWakelockFreqPref) {
            Log.d(TAG, "Wakelock checker frequency changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mWakelockFreqPref, value);

            long freq = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            DefenseSettingsUtils.writeWakelockCheckerFreq(freq, getContentResolver());
            return true;
        } else if (preference == mWakelockDurationThrottlePref) {
            Log.d(TAG, "Wakelock duration throttle changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mWakelockDurationThrottlePref, value + " " + getDurationCutoffUnit());

            long throttle = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            DefenseSettingsUtils.writeWakelockDurationThrottle(throttle, getContentResolver());
            return true;
        }  else if (preference == mWakelockRateLimitPref) {
            Log.d(TAG, "Wakelock rate limit changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mWakelockRateLimitPref, value + " " + getRateLimitUnit());

            float rateLimit = Float.parseFloat(value);
            DefenseSettingsUtils.writeWakeLockRateLimit(rateLimit, getContentResolver());
            return true;
        } else if (preference == mLocationFreqPref) {
            Log.d(TAG, "Location checker frequency changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mLocationFreqPref, value);

            long freq = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            DefenseSettingsUtils.writeLocationCheckerFreq(freq, getContentResolver());
            return true;
        } else if (preference == mLocationDurationThrottlePref) {
            Log.d(TAG, "Location duration throttle changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mLocationDurationThrottlePref, value + " " + getDurationCutoffUnit());

            long throttle = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            DefenseSettingsUtils.writeLocationDurationThrottle(throttle, getContentResolver());
            return true;
        }  else if (preference == mLocationRateLimitPref) {
            Log.d(TAG, "Location rate limit changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mLocationRateLimitPref, value + " " + getRateLimitUnit());

            float rateLimit = Float.parseFloat(value);
            DefenseSettingsUtils.writeLocationRateLimit(rateLimit, getContentResolver());
            return true;
        } else if (preference == mAlarmFreqPref) {
            Log.d(TAG, "Alarm checker frequency changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mAlarmFreqPref, value);

            long freq = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            DefenseSettingsUtils.writeAlarmCheckerFreq(freq, getContentResolver());
            return true;
        }  else if (preference == mAlarmRateLimitPref) {
            Log.d(TAG, "Alarm rate limit changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mAlarmRateLimitPref, value + " " + getRateLimitUnit());

            float rateLimit = Float.parseFloat(value);
            DefenseSettingsUtils.writeAlarmRateLimit(rateLimit, getContentResolver());
            return true;
        }  else if (preference == mSensorFreqPref) {
            Log.d(TAG, "Sensor checker frequency changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mSensorFreqPref, value);

            long freq = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            DefenseSettingsUtils.writeSensorCheckerFreq(freq, getContentResolver());
            return true;
        }  else if (preference == mSensorRateLimitPref) {
            Log.d(TAG, "Sensor rate limit changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mSensorRateLimitPref, value + " " + getRateLimitUnit());

            float rateLimit = Float.parseFloat(value);
            DefenseSettingsUtils.writeSensorRateLimit(rateLimit, getContentResolver());
            return true;
        } else if (preference == mNetworkFreqPref) {
            Log.d(TAG, "Network checker frequency changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mNetworkFreqPref, value);

            long freq = (long) (Float.parseFloat(value) * TimeUtils.MILLIS_PER_MINUTE);
            DefenseSettingsUtils.writeNetworkCheckerFreq(freq, getContentResolver());
            return true;
        } else if (preference == mNetworkDataLimitPref) {
            Log.d(TAG, "Network data limit changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mNetworkDataLimitPref, value  + " " + getSizeUnit());

            long dataLimit = (long) (Float.parseFloat(value) * NetUtils.MB);
            DefenseSettingsUtils.writeNetworkDataLimit(dataLimit, getContentResolver());
            return true;
        } else if (preference == mNetworkMaxBadnessPref) {
            Log.d(TAG, "Network max badness changed to " + newValue);
            String value = (String) newValue;
            updatePrefSummary(mNetworkMaxBadnessPref, value);

            int badness = Integer.parseInt(value);
            DefenseSettingsUtils.writeNetworkMaxBadness(badness, getContentResolver());
            return true;
        }
        Log.d(TAG, "Unrecognized preference change");
        return false;
    }

    private void updatePrefSummary(Preference preference, String newValue) {
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(newValue);
            // Set the summary to reflect the new value.
            listPreference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            if (newValue.length() > 32)
                preference.setSummary(newValue.substring(0, 32) + "...");
            else
                preference.setSummary(newValue);
        }
    }

    private class SettingsHandler extends Handler {
        private static final int MSG_SYNC_WITH_SECURE_SETTING = 1;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SYNC_WITH_SECURE_SETTING:
                    syncWithSecureSettings();
                    break;
            }
        }
    }
}