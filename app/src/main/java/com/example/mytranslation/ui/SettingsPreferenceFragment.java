package com.example.mytranslation.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.mytranslation.R;

/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.setting);

        findPreference("theme").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Snackbar.make(getView(),"nihaoa",Snackbar.LENGTH_SHORT).show();

                return true;
            }
        });


    }
}
