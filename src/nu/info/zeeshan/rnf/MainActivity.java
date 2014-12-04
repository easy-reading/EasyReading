package nu.info.zeeshan.rnf;

import nu.info.zeeshan.rnf.FragmentMain.ViewHolder;
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
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	FragmentMain f1,f2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(mSectionsPagerAdapter);
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
	public void reloadRss(View view){
		ViewHolder holder=(ViewHolder)((RelativeLayout)(view.getParent().getParent())).getTag();
		FragmentMain fragment=((FragmentMain)(mSectionsPagerAdapter.getItem(holder.position)));
		fragment.load();
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
				if(f1==null){
					f1=new FragmentMain();
					Bundle b=new Bundle();
					b.putInt(getString(R.string.bundle_arg_position),position);
					f1.setArguments(b);
				}
				return f1;
			}
			else{
				if(f2==null){
					f2=new FragmentMain();
					Bundle b=new Bundle();
					b.putInt(getString(R.string.bundle_arg_position),position);
					f2.setArguments(b);
				}
				return f2;
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
