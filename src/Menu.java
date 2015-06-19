import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

public class Menu extends ActionBarActivity implements TabListener {

	protected static DatabaseHandler db;
	protected static ArrayList<SQLiteLocation> favoriteList;
	private ActionBar actionBar;
	private ViewPager viewPager;
	private int favChange;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if(db == null)
			db = new DatabaseHandler(this);
		
		favoriteList = new ArrayList<SQLiteLocation>(); 
		favChange = favoriteList.size();

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				actionBar.setSelectedNavigationItem(arg0);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab favorite = actionBar.newTab();
		favorite.setText("Favorites");
		favorite.setTabListener(this);

		ActionBar.Tab custom = actionBar.newTab();
		custom.setText("New Location");
		custom.setTabListener(this);

		ActionBar.Tab history = actionBar.newTab();
		history.setText("History");
		history.setTabListener(this);

		actionBar.addTab(favorite);
		actionBar.addTab(custom, true);
		actionBar.addTab(history);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	public void onPause() {
		super.onPause();
		favChange = favoriteList.size();
	}

	public void onResume() {
		super.onResume(); //hack-job update fix
		if(favoriteList.size() > favChange || viewPager.getCurrentItem() == 0){	//added favorite
			viewPager.setCurrentItem(2,false);
			viewPager.setCurrentItem(0,false);
		}
		else if(viewPager.getCurrentItem() == 1){
			viewPager.setCurrentItem(0,false);
			viewPager.setCurrentItem(2,false);
			viewPager.setCurrentItem(1,false);
		}
		else {
			viewPager.setCurrentItem(0,false);
			viewPager.setCurrentItem(2,false);
		}
	}

}

class MyAdapter extends FragmentPagerAdapter {

	public MyAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = null;
		if (arg0 == 0)
			fragment = new FavoriteMark();
		else if (arg0 == 1)
			fragment = new CustomMark();
		else
			fragment = new HistoryMark();
		return fragment;
	}
	
	@Override
	public int getCount() {
		return 3;
	}
}
