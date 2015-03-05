package nu.info.zeeshan.rnf;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.utility.Constants;
import nu.info.zeeshan.rnf.utility.Utility;
import nu.info.zeeshan.rnf.utility.Utility.ViewHolder;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity implements
		MaterialTabListener {
	public static final String TAG = "nu.info.zeeshan.utility.MainActivity";

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	FragmentNews fnews;
	FragmentFacebook fface;
	SharedPreferences spf;
	DbHelper dbhelper; // for updating feed states (read/unread)
	static boolean IMG_LDR_INIT;
	MaterialTabHost tabHost;
	Toolbar toolbar;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Utility.log(TAG, "activity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(mSectionsPagerAdapter);

		tabHost = (MaterialTabHost) findViewById(R.id.tabHost);

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			tabHost.addTab(tabHost.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		spf = getSharedPreferences(getString(R.string.pref_filename),
				Context.MODE_PRIVATE);
		if (IMG_LDR_INIT == false) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheOnDisk(true).cacheInMemory(true).build();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					this).diskCacheFileCount(100)
					.diskCacheSize(50 * 1024 * 1024)
					.defaultDisplayImageOptions(options).build();
			ImageLoader.getInstance().init(config);
			IMG_LDR_INIT = true;
		}

		dbhelper = new DbHelper(getApplicationContext());
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Utility.log(TAG, "activity onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		Utility.log(TAG, "activity onResume");
		super.onResume();
		long minutes = Integer.parseInt(spf.getString(
				getString(R.string.pref_update_interval),
				Constants.DEFAULT_UPDATE_INTERVAL_IN_HOURS))*60;
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(this, NewsService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
		am.cancel(pi);
		// by my own convention, minutes <= 0 means notifications are disabled
		if (minutes > 0) {
			am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + minutes * 60 * 1000,
					minutes * 60 * 1000, pi);
			Utility.log(TAG, "alarm set");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			intent.putExtra("name", "setting");
			startActivity(intent);
			return true;
		case R.id.action_about:
			intent = new Intent(this, SettingsActivity.class);
			intent.putExtra("name", "about");
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void read(View view) {

		// change state of feed
		// update button
		ViewHolder holder = (ViewHolder) (((LinearLayout) (view.getParent()
				.getParent())).getTag());
		if ((holder.state = dbhelper.feedRead(holder.id)) == 1)
			((ImageButton) view).setImageDrawable(getResources().getDrawable(
					R.drawable.ic_action_read_white));
		else
			((ImageButton) view).setImageDrawable(getResources().getDrawable(
					R.drawable.ic_action_read_active));
		updateAdapter(holder.type - 1);
	}

	private void updateAdapter(int position) {
		if (position == 0)
			((FragmentNews) (mSectionsPagerAdapter.getItem(0))).updateAdapter();
		else
			((FragmentFacebook) (mSectionsPagerAdapter.getItem(1)))
					.updateAdapter();
	}

	@Override
	public void onTabSelected(MaterialTab tab) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(MaterialTab tab) {

	}

	@Override
	public void onTabUnselected(MaterialTab tab) {

	}

	private class SectionsPagerAdapter extends FragmentPagerAdapter implements
			OnPageChangeListener {

		public SectionsPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			if (position == 0) {

				if (fnews == null) {
					Utility.log(TAG, "reference is null new fragment created");
					fnews = new FragmentNews();
				}
				return fnews;
			} else if (position == 1) {
				if (fface == null) {
					Utility.log(TAG, "reference is null new fragment created");
					fface = new FragmentFacebook();
				}
				return fface;
			} else
				return null;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.news_title);
			case 1:
				return getString(R.string.facebook_title);
			}
			return null;
		}

		@Override
		public void onPageSelected(int position) {
			tabHost.setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}
	}
	/*
	 * private static class Result { int type; boolean wasSuccessful;
	 * 
	 * public Result(int type_, boolean wasSuccessful_) { type = type_;
	 * wasSuccessful = wasSuccessful_; } }
	 */
}
