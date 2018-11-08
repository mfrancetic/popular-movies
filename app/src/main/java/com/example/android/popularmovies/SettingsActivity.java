//package com.example.android.popularmovies;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.ListPreference;
//import android.preference.Preference;
//import android.preference.PreferenceFragment;
//import android.preference.PreferenceManager;
//import android.support.v7.app.AppCompatActivity;
//
//public class SettingsActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.settings_activity);
//    }
//
//    /**
//     * Add the preferences from the settings_main.xml file and find the sort_by preference.
//     * Bind the preference summary to value
//     */
//    public static class PopularMoviesPreferenceFragment extends PreferenceFragment
//            implements Preference.OnPreferenceChangeListener {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.settings_main);
//
//            Preference sortBy = findPreference(getString(R.string.settings_sort_by_key));
//            bindPreferenceSummaryToValue(sortBy);
//        }
//
//        /**
//         * Updates the displayed preference summary after it has been changed
//         */
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object value) {
//            String stringValue = value.toString();
//            if (preference instanceof ListPreference) {
//                ListPreference listPreference = (ListPreference) preference;
//                int prefIndex = listPreference.findIndexOfValue(stringValue);
//                if (prefIndex >= 0) {
//                    CharSequence[] labels = listPreference.getEntries();
//                    preference.setSummary(labels[prefIndex]);
//                } else {
//                    preference.setSummary(stringValue);
//                }
//            }
//            return true;
//        }
//
//        /**
//         * Sets the OnPreferenceChangeListener to the preference and gets default shared preferences,
//         */
//        private void bindPreferenceSummaryToValue(Preference preference) {
//            preference.setOnPreferenceChangeListener(this);
//            SharedPreferences preferences = PreferenceManager
//                    .getDefaultSharedPreferences(preference.getContext());
//            String preferenceString = preferences.getString(preference.getKey(), "");
//            onPreferenceChange(preference, preferenceString);
//        }
//    }
//}