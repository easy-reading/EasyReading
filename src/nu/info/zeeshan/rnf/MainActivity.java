package nu.info.zeeshan.rnf;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import nu.info.zeeshan.dao.DbHelper;
import nu.info.zeeshan.utility.ProcessFeed;
import nu.info.zeeshan.utility.ProcessFeed.FeedInput;
import nu.info.zeeshan.utility.Utility.ViewHolder;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity implements
		MaterialTabListener, OnScrollListener {
	private static final String TAG = "nu.info.zeeshan.utility.MainActivity";
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	public static FragmentNews fnews;
	public static FragmentFacebook fface;
	static SharedPreferences spf;
	static DbHelper dbhelper;
	static boolean IMG_LDR_INIT;
	public static boolean updating;
	MaterialTabHost tabHost;
	Toolbar toolbar;
	int tabhost_height, collapsedH, expandedH;
	int pvisibleitemindex, pScollY, cScrollY;
	static boolean toolbar_hidden = false, tabBarMoving, HeightIcrmented,
			toolbarShown = true, SETUP;
	ChangeToolbarState toolbarStateUpdater;
	private static int SCROLL_THRESHOLD = 20, toolbarHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (mSectionsPagerAdapter == null)
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getSupportFragmentManager());
		if (mViewPager == null) {
			mViewPager = (ViewPager) findViewById(R.id.container);
			mViewPager.setAdapter(mSectionsPagerAdapter);
			mViewPager.setOnPageChangeListener(mSectionsPagerAdapter);

		}
		toolbarStateUpdater = new ChangeToolbarState();
		tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
		tabhost_height = (int) getResources().getDimension(
				R.dimen.tab_host_height);
		tabHost.animate().setListener(toolbarStateUpdater);
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			tabHost.addTab(tabHost.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));

		}
		if (spf == null)
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
		if (dbhelper == null)
			dbhelper = new DbHelper(getApplicationContext());
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setBackgroundColor(getResources().getColor(
				R.color.primary_color));
		setSupportActionBar(toolbar);

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
			String msg;
			if (!updating) {
				updating = true;
				String fbfeed = spf.getString(
						getString(R.string.pref_facebookrss), null);
				String newsfeed = spf.getString(
						getString(R.string.pref_newsrss), null);

				if (fbfeed == null && newsfeed == null) {
					msg = getString(R.string.toast_msg_nofeedok);
				} else if (fbfeed == null) {
					msg = getString(R.string.toast_msg_feednewsok);
					new ProcessFeed(getApplicationContext())
							.execute(new FeedInput(newsfeed, 1));
				} else if (newsfeed == null) {
					msg = getString(R.string.toast_msg_feedfbok);
					new ProcessFeed(getApplicationContext())
							.execute(new FeedInput(fbfeed, 2));
				} else {
					msg = getString(R.string.toast_msg_bothfeedok);
					new ProcessFeed(getApplicationContext()).execute(
							new FeedInput(newsfeed, 1),
							new FeedInput(fbfeed, 2));
				}
			} else
				msg = getString(R.string.toast_msg_wait);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
					.show();
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

		if (holder.type == 1)
			fnews.updateAdapter(getApplicationContext());
		else
			fface.updateAdapter(getApplicationContext());

	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter implements
			OnPageChangeListener {

		public SectionsPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
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

	private void hideActionBar() {
		if (!SETUP) {
			collapsedH = mViewPager.getHeight();
			toolbarHeight = toolbar.getHeight();
			expandedH = collapsedH + toolbarHeight;
			SETUP = true;
		}
		if (!tabBarMoving) {

			mViewPager.getLayoutParams().height = expandedH;
			mViewPager.requestLayout();
			HeightIcrmented = true;

			tabHost.animate().translationY(-toolbarHeight).setDuration(100)
					.start();

			mViewPager.animate().translationY(-toolbarHeight).setDuration(100)
					.start();

			toolbarShown = false;
		}

	}

	private void showActionBar() {
		if (!tabBarMoving) {
			// collapse with animation
			tabHost.animate().translationY(0).setDuration(100).start();

			mViewPager.animate().translationY(0).setDuration(100).start();
			new CollapseView().execute();
			toolbarShown = true;
			HeightIcrmented = false;
		}
	}

	private class CollapseView extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mViewPager.getLayoutParams().height = collapsedH;
			mViewPager.requestLayout();
			super.onPostExecute(result);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		View v = view.getChildAt(0);
		cScrollY = (v == null) ? 0 : (v.getTop() - view.getPaddingTop());

		if ((Math.abs(pScollY - cScrollY) > SCROLL_THRESHOLD)
				&& (firstVisibleItem == pvisibleitemindex)) {
			if (pScollY < cScrollY) {

				/**
				 * Scrolling downwards translate back tab bar Translate back
				 * ViewPager dec height of ViewPager
				 * 
				 */
				if (!toolbarShown && HeightIcrmented && !tabBarMoving) {
					Log.d(TAG, "show toolbar");
					showActionBar();
				}

			} else {

				/**
				 * Scrolling upwards hide actionbar translate ViewPager IncHight
				 * of ViewPager
				 */
				if (toolbarShown && !HeightIcrmented && !tabBarMoving) {
					Log.d(TAG, "hide toolbar");
					hideActionBar();
				}
			}
		}
		pScollY = cScrollY;
		pvisibleitemindex = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	public static class ChangeToolbarState implements AnimatorListener {

		@Override
		public void onAnimationCancel(Animator animation) {
			tabBarMoving = false;
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			tabBarMoving = false;
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
			tabBarMoving = true;
		}

		@Override
		public void onAnimationStart(Animator animation) {
			tabBarMoving = true;
		}

	}

}
