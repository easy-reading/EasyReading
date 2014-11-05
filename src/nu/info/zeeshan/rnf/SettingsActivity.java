package nu.info.zeeshan.rnf;

import nu.info.zeeshan.utility.Utility;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.activity_settings);
	    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {
		if(key.equalsIgnoreCase(getString(R.string.pref_facebookrss))){
			Utility.log("pref","facebook");
			FragmentMain.facebookfeed=arg0.getString(key, null);
			FragmentMain.url_changed=1;
			
		}
		else{
			Utility.log("pref","NEWS");
			FragmentMain.newsfeed=arg0.getString(key, null);
			FragmentMain.url_changed=0;
		}
		Utility.log("NEW PREF",arg0.getString(key, null));
		
	}
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
}
