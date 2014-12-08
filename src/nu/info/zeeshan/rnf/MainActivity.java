package nu.info.zeeshan.rnf;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(mSectionsPagerAdapter);
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheOnDisk(true)
		.cacheInMemory(true)
		.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		.diskCacheFileCount(100)
		.diskCacheSize(50 * 1024 * 1024)
		.defaultDisplayImageOptions(options)
        .build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent=new Intent(this,SettingsActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter implements OnPageChangeListener{

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			if(position==0){
				if(fnews==null){
					fnews=new FragmentNews();
				}
				return fnews;
			}
			else{
				if(fface==null){
					fface=new FragmentFacebook();
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
		public void onPageSelected(int position){
			if(position==0)
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
