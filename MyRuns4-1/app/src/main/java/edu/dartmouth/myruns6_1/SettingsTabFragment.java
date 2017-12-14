package edu.dartmouth.myruns6_1;

/**
 * Created by Zizi on 2/7/2017.
 */

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsTabFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        // Everything is set already in the xml, so no code here.
        // See res/xml/preference.xml for details
        addPreferencesFromResource(R.xml.preference);
    }
}
