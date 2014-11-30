package nu.info.zeeshan.rnf;

import nu.info.zeeshan.utility.Utility;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class FragmentSettings extends PreferenceFragment  implements OnSharedPreferenceChangeListener {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.activity_settings);
    }
	@Override
	public void onSharedPreferenceChanged(SharedPreferences spf, String key) {
		
		SharedPreferences ospf=getActivity().getSharedPreferences(getString(R.string.pref_filename), Context.MODE_PRIVATE);
		ospf.edit().remove(key).commit();
		ospf.edit().putString(key, spf.getString(key, "nothing")).commit();
		
		if(key.equalsIgnoreCase(getString(R.string.pref_facebookrss))){
			Utility.log("pref","facebook");
		//	FeedSource.fb_source=spf.getString(key, null);
			FragmentMain.url_changed=1;
		}
		else{
			Utility.log("pref","NEWS");
		//	FeedSource.news_source=spf.getString(key, null);
			FragmentMain.url_changed=0;
		}
		Utility.log("NEW PREF",spf.getString(key, null));
		
	}

	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	    	.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
}
