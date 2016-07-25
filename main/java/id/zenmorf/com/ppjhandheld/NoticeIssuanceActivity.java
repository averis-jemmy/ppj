package id.zenmorf.com.ppjhandheld;

import id.zenmorf.com.ppjhandheld.R;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class NoticeIssuanceActivity extends AppCompatActivity {

	TabLayout tabLayout;
	ViewPager viewPager;

	public static String POSITION = "POSITION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noticeissuance);

		// Get the ViewPager and set it's PagerAdapter so that it can display items
		tabLayout = (TabLayout) findViewById(R.id.tabs);
		viewPager = (ViewPager) findViewById(R.id.pager);
		TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

		// Give the TabLayout the ViewPager
		viewPager.setAdapter(adapter);
		tabLayout.setupWithViewPager(viewPager);

		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});

		if(CacheManager.IsNewNotice) {
			PPJSummonIssuanceInfo temp = CacheManager.SummonIssuanceInfo;
			CacheManager.SummonIssuanceInfo = new PPJSummonIssuanceInfo();
			CacheManager.SummonIssuanceInfo.OffenceLocationPos = temp.OffenceLocationPos;
			CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos = temp.OffenceLocationAreaPos;
			CacheManager.imageIndex = 0;
			CacheManager.IsNewNotice = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.menu_logout) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		return;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
	}
}
