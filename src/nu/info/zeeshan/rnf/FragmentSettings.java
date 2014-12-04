package nu.info.zeeshan.rnf;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Patterns;
import android.widget.Toast;

public class FragmentSettings extends PreferenceFragment  implements OnSharedPreferenceChangeListener {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
    }
	@Override
	public void onSharedPreferenceChanged(SharedPreferences spf, String key) {
		if(valid(spf.getString(key, ""))){
			SharedPreferences ospf=getActivity().getSharedPreferences(getString(R.string.pref_filename), Context.MODE_PRIVATE);
			ospf.edit().remove(key).commit();
			ospf.edit().putString(key, spf.getString(key, "nothing")).commit();
			
			if(key.equalsIgnoreCase(getString(R.string.pref_facebookrss))){
				FragmentMain.url_changed=1;
			}
			else{
				FragmentMain.url_changed=0;
			}
		}
		else{
			Toast.makeText(getActivity(), getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
		}
	}
	public boolean valid(String url){
		if(url.isEmpty() || url.length()==0)
			return false;
		return Patterns.WEB_URL.matcher(url).matches();
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
