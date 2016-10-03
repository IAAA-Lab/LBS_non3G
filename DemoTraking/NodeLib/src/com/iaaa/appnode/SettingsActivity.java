package com.iaaa.appnode;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceActivity;



public class SettingsActivity extends PreferenceActivity {

	public final static String KEY_PREF_POWER_MODE = "pref_powerModeType";
	public final static String KEY_PREF_POWER_DATA_TYPE = "pref_powerDataTypeType";

	//public static Context contextOfApplication;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		//contextOfApplication = getApplicationContext();

	}


	/* public static Context getContextOfApplication(){
        return contextOfApplication;
    }*/



}
