package nu.info.zeeshan.rnf;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import nu.info.zeeshan.rnf.util.Constants;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// get setting

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int updateInterval;
		try{
			updateInterval=Integer.parseInt(prefs.getString(context.getString(R.string.pref_update_interval),Constants.DEFAULT_UPDATE_INTERVAL_IN_HOURS));
		}catch(Exception ex){
			updateInterval=Integer.parseInt(Constants.DEFAULT_UPDATE_INTERVAL_IN_HOURS);
		}
		int minutes = Constants.DEBUG ? 1 : updateInterval * 60;
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent_ = new Intent(context, NewsService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent_, 0);
		am.cancel(pi);
		// by my own convention, minutes <= 0 means notifications are disabled
		if (minutes > 0) {
			am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + minutes * 60 * 1000,
					minutes * 60 * 1000, pi);
		}
	}
}