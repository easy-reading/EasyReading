package nu.info.zeeshan.rnf;

import nu.info.zeeshan.rnf.utility.Constants;
import nu.info.zeeshan.rnf.utility.Utility;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceFragment;
import android.util.Patterns;
import android.widget.Toast;

public class SettingFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = "SettingFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_settings);
		addPreferencesFromResource(R.xml.activity_settings);
		// getSupportedFragmentManager().beginTransaction().replace(R.id.setting_fragment_holder,new
		// FragmentSettings()).commit();
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

	public boolean valid(String url) {
		if (url.isEmpty() || url.length() == 0)
			return false;
		return Patterns.WEB_URL.matcher(url).matches();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences spf, String key) {
		SharedPreferences ospf = this.getActivity().getSharedPreferences(
				getString(R.string.pref_filename), Context.MODE_PRIVATE);
		if (key.equalsIgnoreCase(getString(R.string.pref_limit))) {
			ospf.edit()
					.putString(key,
							spf.getString(key, Constants.DEFAULT_FEED_LIMIT))
					.commit();
		} else if (key
				.equalsIgnoreCase(getString(R.string.pref_update_interval))) {
			ospf.edit()
					.putString(
							key,
							spf.getString(key,
									Constants.DEFAULT_UPDATE_INTERVAL_IN_HOURS))
					.commit();

			Context context = getActivity().getApplicationContext();
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent_ = new Intent(context, NewsService.class);
			PendingIntent pi = PendingIntent.getService(context, 0, intent_, 0);
			am.cancel(pi);
			int minutes = Integer.parseInt(spf.getString(key,
					Constants.DEFAULT_UPDATE_INTERVAL_IN_HOURS));
			// by my own convention, minutes <= 0 means notifications are
			// disabled
			if (minutes > 0) {
				am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + minutes * 60 * 1000,
						minutes * 60 * 1000, pi);
			}
			Utility.log(TAG, "Alarm reset");
			
		} else if (valid(spf.getString(key, ""))) {

			ospf.edit().remove(key).commit();
			ospf.edit()
					.putString(key, spf.getString(key, Constants.EMPTY_FEED))
					.commit();

		} else {
			Toast.makeText(getActivity(), getString(R.string.invalid_input),
					Toast.LENGTH_SHORT).show();
		}
	}
}