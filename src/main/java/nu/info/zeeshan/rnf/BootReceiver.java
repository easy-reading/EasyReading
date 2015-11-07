package nu.info.zeeshan.rnf;

import nu.info.zeeshan.rnf.utility.Constants;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// get setting
		boolean DEBUG = false;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int minutes = DEBUG ? 0 : Integer.parseInt(prefs.getString(
				context.getString(R.string.pref_update_interval),
				Constants.DEFAULT_UPDATE_INTERVAL_IN_HOURS)) * 60;
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