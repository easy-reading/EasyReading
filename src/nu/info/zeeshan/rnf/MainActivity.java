package nu.info.zeeshan.rnf;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
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
			return true;
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
			Fragment f=new FragmentMain();
			Bundle b=new Bundle();
			if(position==0){
				b.putString("URL", "http://www.thehindu.com/sci-tech/technology/?service=rss");
				
			}
			else{
				b.putString("URL", "https://www.facebook.com/feeds/notifications.php?id=100004366590327&viewer=100004366590327&key=AWjLdGV4a_ncSjfj&format=rss20");
				
			}
			f.setArguments(b);
			return f;
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
				getActionBar().setTitle("News");
				return "News";
			case 1:
				getActionBar().setTitle("Facebook");
				return "Facebook";
			}
			return null;
		}
		@Override
		public void onPageSelected(int position){
			if(position==0)
				getActionBar().setTitle("News");
			else
				getActionBar().setTitle("Facebook");
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
	}

}
