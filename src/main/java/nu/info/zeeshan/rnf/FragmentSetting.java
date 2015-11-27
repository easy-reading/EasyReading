package nu.info.zeeshan.rnf;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.util.Set;

import nu.info.zeeshan.rnf.util.Constants;
import nu.info.zeeshan.rnf.util.Util;

public class FragmentSetting extends PreferenceFragment implements
        OnSharedPreferenceChangeListener {
    public static final String TAG = "FragmentSetting";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences spf, String key) {
        SharedPreferences ospf = this.getActivity().getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE);
        if (key.equalsIgnoreCase(getString(R.string.pref_news_keywords))) {
            Set<String> keywords = spf.getStringSet(key, Constants.DEFAULT_NEWS_KEYWORDS);
            if (keywords.size() == 0) {
                keywords = Constants.DEFAULT_NEWS_KEYWORDS;
                Toast.makeText(getActivity().getApplicationContext(),"set to default 'latest'",Toast.LENGTH_SHORT).show();
            }
            ospf.edit().putStringSet(key, keywords).commit();
            Util.log(TAG, "news keywords" + spf.getStringSet(key, Constants.DEFAULT_NEWS_KEYWORDS));
        } else if (key.equalsIgnoreCase(getString(R.string.pref_limit))) {
            String limit;
            try {
                limit=spf.getString(key, Constants.DEFAULT_FEED_LIMIT);
                Integer.parseInt(limit);
            } catch (Exception ex) {
                limit = Constants.DEFAULT_FEED_LIMIT;
                Toast.makeText(getActivity().getApplicationContext(),"Set to default "+limit,Toast.LENGTH_SHORT).show();
            }
            ospf.edit().putString(key, limit).commit();
            Util.log(TAG, "item limit" + limit);
        } else if (key
                .equalsIgnoreCase(getString(R.string.pref_update_interval))) {
            String updateInterval;
            try{
                updateInterval=spf.getString(key,Constants.DEFAULT_UPDATE_INTERVAL_IN_HOURS);
                Integer.parseInt(updateInterval);
            }catch(Exception ex){
                updateInterval=Constants.DEFAULT_UPDATE_INTERVAL_IN_HOURS;
                Toast.makeText(getActivity().getApplicationContext(),"Set to default "+updateInterval,Toast.LENGTH_SHORT).show();
            }
            ospf.edit().putString(key, updateInterval).commit();

            Context context = getActivity().getApplicationContext();
            AlarmManager am = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            Intent intent_ = new Intent(context, NewsService.class);
            PendingIntent pi = PendingIntent.getService(context, 0, intent_, 0);
            am.cancel(pi);
            int minutes = Constants.DEBUG?1:Integer.parseInt(updateInterval)*60;
            // by my own convention, minutes <= 0 means notifications are
            // disabled
            if (minutes > 0) {
                am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + minutes * 60 * 1000,
                        minutes * 60 * 1000, pi);
            }
            Util.log(TAG, "Alarm reset after " + minutes * 60 * 1000 + "ms");

        } else {
            Toast.makeText(getActivity(), getString(R.string.invalid_input),
                    Toast.LENGTH_SHORT).show();
        }
    }
}