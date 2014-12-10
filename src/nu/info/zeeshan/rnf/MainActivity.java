package nu.info.zeeshan.rnf;

import nu.info.zeeshan.dao.DbHelper;
import nu.info.zeeshan.utility.ProcessFeed;
import nu.info.zeeshan.utility.ProcessFeed.FeedInput;
import nu.info.zeeshan.utility.Utility;
import nu.info.zeeshan.utility.Utility.ViewHolder;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends Activity {
	private static final String TAG = "nu.info.zeeshan.utility.MainActivity";
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	static FragmentNews fnews;
	static FragmentFacebook fface;
	static SharedPreferences spf;
	static DbHelper dbhelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (mSectionsPagerAdapter == null)
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getFragmentManager());
		if (mViewPager == null) {
			mViewPager = (ViewPager) findViewById(R.id.container);
			mViewPager.setAdapter(mSectionsPagerAdapter);
			mViewPager.setOnPageChangeListener(mSectionsPagerAdapter);
		}
		if (spf == null)
			spf = getSharedPreferences(getString(R.string.pref_filename),
					Context.MODE_PRIVATE);
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheOnDisk(true).cacheInMemory(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).diskCacheFileCount(100).diskCacheSize(50 * 1024 * 1024)
				.defaultDisplayImageOptions(options).build();
		ImageLoader.getInstance().init(config);
		dbhelper = new DbHelper(getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_refresh:
			// start fetching and inserting news active page
			String fbfeed = spf.getString(getString(R.string.pref_facebookrss),
					null);
			String newsfeed = spf.getString(getString(R.string.pref_newsrss),
					null);
			String msg = "";
			if (fbfeed == null && newsfeed == null) {
				msg = getString(R.string.toast_msg_nofeedok);
			} else if (fbfeed == null) {
				msg = getString(R.string.toast_msg_feednewsok);
				new ProcessFeed(getApplicationContext()).execute(new FeedInput(
						newsfeed, 1));
			} else if (newsfeed == null) {
				msg = getString(R.string.toast_msg_feedfbok);
				new ProcessFeed(getApplicationContext()).execute(new FeedInput(
						fbfeed, 2));
			} else {
				msg= getString(R.string.toast_msg_bothfeedok);
				new ProcessFeed(getApplicationContext()).execute(new FeedInput(
						newsfeed, 1), new FeedInput(fbfeed, 2));
			}
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
			.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void read(View view) {
		Utility.log(TAG, "feed deleted");
		// change state of feed
		// update button
		ViewHolder holder = (ViewHolder) (((LinearLayout) (view.getParent()
				.getParent())).getTag());
		if ((holder.state = dbhelper.feedRead(holder.id)) == 1)
			((ImageButton) view).setImageDrawable(getResources().getDrawable(
					R.drawable.ic_action_read_active));
		else
			((ImageButton) view).setImageDrawable(getResources().getDrawable(
					R.drawable.ic_action_read));

		if (holder.type == 1)
			FragmentNews.updateAdapter(getApplicationContext());
		else
			FragmentFacebook.updateAdapter(getApplicationContext());
		
		Toast.makeText(getApplicationContext(), (holder.type==1?"News":"Notification")+" marked as"+(holder.state==0?" unread":" read"),Toast.LENGTH_SHORT).show();	
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter implements
			OnPageChangeListener {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			if (position == 0) {
				if (fnews == null) {
					fnews = new FragmentNews();
				}
				return fnews;
			} else {
				if (fface == null) {
					fface = new FragmentFacebook();
				}
				return fface;
			}
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
				getActionBar().setTitle(getString(R.string.news_title));
				return getString(R.string.news_title);
			case 1:
				getActionBar().setTitle(getString(R.string.facebook_title));
				return getString(R.string.facebook_title);
			}
			return null;
		}

		@Override
		public void onPageSelected(int position) {
			if (position == 0)
				getActionBar().setTitle(getString(R.string.news_title));
			else
				getActionBar().setTitle(getString(R.string.facebook_title));
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

}
