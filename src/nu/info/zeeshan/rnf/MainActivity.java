package nu.info.zeeshan.rnf;

import nu.info.zeeshan.utility.ProcessFeed;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends Activity {
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	static FragmentNews fnews;
	static FragmentFacebook fface;
	static SharedPreferences spf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(mSectionsPagerAdapter);
		spf=getSharedPreferences(getString(R.string.pref_filename), Context.MODE_PRIVATE);
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheOnDisk(true).cacheInMemory(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).diskCacheFileCount(100).diskCacheSize(50 * 1024 * 1024)
				.defaultDisplayImageOptions(options).build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.action_settings:
			Intent intent=new Intent(this,SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_refresh:
			//start fetching and inserting news active page
			new ProcessFeed(getApplication()).execute(spf.getString(getString(R.string.pref_newsrss), getString(R.string.empty_feed)),spf.getString(getString(R.string.pref_facebookrss), getString(R.string.empty_feed)));
		default:
			return super.onOptionsItemSelected(item);
		}
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
