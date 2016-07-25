package id.zenmorf.com.ppjhandheld;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class NoticesActivity extends TabActivity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_notices);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		if(CacheManager.SummonIssuanceInfo == null)
		{
			CacheManager.SummonIssuanceInfo = new PPJSummonIssuanceInfo();
		}
		TabHost tabHost = getTabHost();
		 
        // Tab for Photos
        TabSpec maklumatspec = tabHost.newTabSpec("Maklumat");
        // setting Title and Icon for the Tab
        maklumatspec.setIndicator("Maklumat");
        Intent maklumatIntent = new Intent(this, MaklumatActivity.class);
        maklumatspec.setContent(maklumatIntent);
 
        // Tab for Songs
        TabSpec kesalahanspec = tabHost.newTabSpec("Kesalahan");
        //songspec.setIndicator("Songs", getResources().getDrawable(R.drawable.icon_songs_tab));
        kesalahanspec.setIndicator("Kesalahan");
        Intent KesalahanIntent = new Intent(this, KesalahanActivity.class);
        kesalahanspec.setContent(KesalahanIntent);
 
        // Tab for Videos
        TabSpec ringkasanspec = tabHost.newTabSpec("Ringkasan");
        //videospec.setIndicator("Videos", getResources().getDrawable(R.drawable.icon_videos_tab));
        ringkasanspec.setIndicator("Ringkasan");
        Intent ringkasanIntent = new Intent(this, RingkasanActivity.class);
        ringkasanspec.setContent(ringkasanIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(maklumatspec); // Adding photos tab
        tabHost.addTab(kesalahanspec); // Adding songs tab
        tabHost.addTab(ringkasanspec); // Adding videos tab
        getTabHost().setOnTabChangedListener(new OnTabChangeListener() 
        {
        	@Override
        	public void onTabChanged(String tabId)
        	{
        		int i = getTabHost().getCurrentTab();
        		if (i == 0)
        		{
        			
        		}
        		else if (i ==1)
        		{
        			
        		}
        		else if (i ==2)
        		{
        			
        			//CacheManager.objMaklumatActivity.SaveData();
        		}
        	}
        });      
	}

	void FillData()
	{
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_notices, menu);
		return true;
	}

	@Override
	public void onBackPressed()
	{
		//Intent i = new Intent(NoticesActivity.this,LoginActivity.class);
		//NoticesActivity.this.startActivity(i);
		return;
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

}
