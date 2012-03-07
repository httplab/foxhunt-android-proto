package com.foxhunt.proto1;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.foxhunt.proto1.R;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 05.03.12
 * Time: 18:11
 * To change this template use File | Settings | File Templates.
 */
public class Preferences extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}