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
		/*	SharedPreferences settings = getSharedPreferences(getString(R.string.pref_filename),Context.MODE_PRIVATE);
			
		      SharedPreferences.Editor editor = settings.edit();
		      editor.putString(getString(R.string.pref_facebookrss), "nothing");
		      editor.putString(getString(R.string.pref_newsrss), "nothing");
		      editor.commit();
		      Utility.log("MAIN", "reset All");*/
		
		
		
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
					b.putInt("position",position);
					f1.setArguments(b);
				}
				return f1;
			}
			else{
				if(f2==null){
					f2=new FragmentMain();
					Bundle b=new Bundle();
					b.putInt("position",position);
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
