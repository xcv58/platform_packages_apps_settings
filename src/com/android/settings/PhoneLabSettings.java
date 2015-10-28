/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;
import android.util.Log;

public class PhoneLabSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "PhoneLabSettings";

    private static final String KEY_NAME = "name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AGE = "age";
    private static final String KEY_LAPTOP = "have_laptop";
    private static final String KEY_DESKTOP = "have_desktop";
    private static final String KEY_ANOTHER_PHONE = "have_another_phone";

    private ListPreference mAgePreference;
    private ListPreference mGenderPreference;
    private CheckBoxPreference mLaptopPreference;
    private CheckBoxPreference mDesktopPreference;
    private CheckBoxPreference mAnotherPhonePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.phonelab_settings);
        updateState();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateState();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateState() {
        mAgePreference = (ListPreference) getPreference(mAgePreference, KEY_AGE);
        if (mAgePreference != null) {
            String age = mAgePreference.getValue();
            mAgePreference.setSummary(age);
        }

        mGenderPreference = (ListPreference) getPreference(mGenderPreference, KEY_GENDER);
        if (mGenderPreference != null) {
            String gender = mGenderPreference.getValue();
            mGenderPreference.setSummary(gender);
        }

        mLaptopPreference = (CheckBoxPreference) getPreference(mLaptopPreference, KEY_LAPTOP);
        mDesktopPreference = (CheckBoxPreference) getPreference(mDesktopPreference, KEY_DESKTOP);
        mAnotherPhonePreference = (CheckBoxPreference) getPreference(mAnotherPhonePreference, KEY_ANOTHER_PHONE);
    }

    private Preference getPreference(Preference preference, String key) {
        if (preference == null) {
            preference = findPreference(key);
            preference.setOnPreferenceChangeListener(this);
        }
        return preference;
    }

    private void updateGender(String gender) {
        if (gender == null || gender.isEmpty()) {
            gender = getString(R.string.unknown);
        }
        mGenderPreference.setSummary(gender);
        Settings.Global.putString(getActivity().getContentResolver(),
                Settings.Global.PHONELAB_GENDER, gender);
    }

    private void updateAge(String age) {
        if (age == null || age.isEmpty()) {
            age = getString(R.string.unknown);
        }
        mAgePreference.setSummary(age);
        Settings.Global.putString(getActivity().getContentResolver(),
                Settings.Global.PHONELAB_AGE, age);
    }

    private void updateLaptop(String value) {
        Settings.Global.putString(getActivity().getContentResolver(),
                Settings.Global.PHONELAB_LAPTOP, value);
    }

    private void updateDesktop(String value) {
        Settings.Global.putString(getActivity().getContentResolver(),
                Settings.Global.PHONELAB_DESKTOP, value);
    }

    private void updateAnotherPhone(String value) {
        Settings.Global.putString(getActivity().getContentResolver(),
                Settings.Global.PHONELAB_ANOTHER_PHONE, value);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange: " + preference + " new value: " + newValue);
        if (preference == mAgePreference) {
            updateAge(newValue.toString());
        } else if (preference == mGenderPreference) {
            updateGender(newValue.toString());
        } else if (preference == mLaptopPreference) {
            updateLaptop(newValue.toString());
        } else if (preference == mDesktopPreference) {
            updateDesktop(newValue.toString());
        } else if (preference == mAnotherPhonePreference) {
            updateAnotherPhone(newValue.toString());
        }
        return true;
    }

    // @Override
    // public boolean onPreferenceChange(Preference preference, Object objValue) {
    //     final String key = preference.getKey();
    //     Log.d(TAG, "onPreferenceChange: " + key + " " + objValue);
    //     if (KEY_SCREEN_TIMEOUT.equals(key)) {
    //         try {
    //             int value = Integer.parseInt((String) objValue);
    //             Settings.System.putInt(getContentResolver(), SCREEN_OFF_TIMEOUT, value);
    //             updateTimeoutPreferenceDescription(value);
    //         } catch (NumberFormatException e) {
    //             Log.e(TAG, "could not persist screen timeout setting", e);
    //         }
    //     }
    //     if (KEY_FONT_SIZE.equals(key)) {
    //         writeFontSizePreference(objValue);
    //     }
    //     if (preference == mAutoBrightnessPreference) {
    //         boolean auto = (Boolean) objValue;
    //         Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE,
    //                 auto ? SCREEN_BRIGHTNESS_MODE_AUTOMATIC : SCREEN_BRIGHTNESS_MODE_MANUAL);
    //     }
    //     if (preference == mLiftToWakePreference) {
    //         boolean value = (Boolean) objValue;
    //         Settings.Secure.putInt(getContentResolver(), WAKE_GESTURE_ENABLED, value ? 1 : 0);
    //     }
    //     if (preference == mDozePreference) {
    //         boolean value = (Boolean) objValue;
    //         Settings.Secure.putInt(getContentResolver(), DOZE_ENABLED, value ? 1 : 0);
    //     }
    //     return true;
    // }
}
