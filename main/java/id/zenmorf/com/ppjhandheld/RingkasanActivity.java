package id.zenmorf.com.ppjhandheld;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.xmlpull.v1.XmlSerializer;

import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateFormat;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

public class RingkasanActivity extends Activity {
	private Button btnRingkasanCetak;
	static Handler timeHandler;
	private int batteryPercentage = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ringkasan);
		btnRingkasanCetak = (Button)findViewById(R.id.btncetak);
		btnRingkasanCetak.setOnClickListener(btnRingkasanCetakListener);	
		
		timeHandler = new Handler();
		Runnable run = new Runnable() {

	        @Override
	        public void run() {
	        	EditText tTarikh = (EditText)findViewById(R.id.etSummaryTarikh);
	    		tTarikh.setText(CacheManager.GetDate());
	    		
	    		EditText tMasa = (EditText)findViewById(R.id.etSummaryMasa);
	    		tMasa.setText(CacheManager.GetTime().toUpperCase());
	    	    timeHandler.postDelayed(this, 500);
	        }
	    };
	    timeHandler.postDelayed(run, 500);
		
	    Button btnStatus = (Button) findViewById(R.id.btnstatus);
	    btnStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CreateStorageDirectory();
				File summonsDirectory = new File("/mnt/sdcard/OfflineSummons/");
				
				int nAmountNoticeIssued = 0;
				File[] files = summonsDirectory.listFiles();
	            for (File file : files)
	            {
	                String strNoticeFile = file.getName();
	                if (strNoticeFile.length() == 14)
	                    nAmountNoticeIssued++;
	            }

	            String status = "Jumlah Notis Yang Telah Di Keluarkan : " + nAmountNoticeIssued + "\n";
	            status += "Bateri : " + batteryPercentage + "%\n";
	            status += "Versi : 1.0.0.0";
				AlertMessage(RingkasanActivity.this, "STATUS", status, 0);
			}
		});

		Button btnLogout = (Button) findViewById(R.id.btnlogout);
		btnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
	    
	    registerBatteryLevelReceiver();
	}
	
	private BroadcastReceiver battery_receiver = new BroadcastReceiver()
    {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                    boolean isPresent = intent.getBooleanExtra("present", false);
                    String technology = intent.getStringExtra("technology");
                    int plugged = intent.getIntExtra("plugged", -1);
                    int scale = intent.getIntExtra("scale", -1);
                    int health = intent.getIntExtra("health", 0);
                    int status = intent.getIntExtra("status", 0);
                    int rawlevel = intent.getIntExtra("level", -1);
        int level = 0;
        
        Bundle bundle = intent.getExtras();
        
        if(isPresent)
        {
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                
                String info = "Battery Level: " + level + "%\n";
                
                info += ("Technology: " + technology + "\n");
                info += ("Plugged: " + getPlugTypeString(plugged) + "\n");
                info += ("Health: " + getHealthString(health) + "\n");
                info += ("Status: " + getStatusString(status) + "\n");

                //setBatteryLevelText(info + "\n\n" + bundle.toString());
                batteryPercentage = level;
        }
        else
        {
            //setBatteryLevelText("Battery not present!!!");
        }
      }
    };
    
    private String getPlugTypeString(int plugged){
            String plugType = "Unknown";
            
            switch(plugged)
            {
                    case BatteryManager.BATTERY_PLUGGED_AC: plugType = "AC";        break;
                    case BatteryManager.BATTERY_PLUGGED_USB: plugType = "USB";      break;
            }
            
            return plugType;
    }
    
    private String getHealthString(int health)
    {
            String healthString = "Unknown";
            
            switch(health)
            {
                    case BatteryManager.BATTERY_HEALTH_DEAD: healthString = "Dead"; break;
                    case BatteryManager.BATTERY_HEALTH_GOOD: healthString = "Good"; break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: healthString = "Over Voltage"; break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT: healthString = "Over Heat"; break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: healthString = "Failure"; break;
            }
            
            return healthString;
    }
    
    private String getStatusString(int status)
    {
            String statusString = "Unknown";
            
            switch(status)
            {
                    case BatteryManager.BATTERY_STATUS_CHARGING: statusString = "Charging"; break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING: statusString = "Discharging"; break;
                    case BatteryManager.BATTERY_STATUS_FULL: statusString = "Full"; break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING: statusString = "Not Charging"; break;
            }
            
            return statusString;
    }
    
    private void registerBatteryLevelReceiver(){
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(battery_receiver, filter);
    }
	
	private OnClickListener btnRingkasanCetakListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			CacheManager.SummonIssuanceInfo.OffenceDateTime = new Date();
			CacheManager.SummonIssuanceInfo.CompoundDate = CacheManager.GetCompoundDate();
			if(ValidateData())
			{
				doPrint = new Runnable() {
					@Override
					public void run()
					{
						Looper.prepare();
						DoPrint();
						Looper.loop();
						Looper.myLooper().quit();
					}
				};
				m_ProgressDialog = new ProgressDialog(RingkasanActivity.this);
				m_ProgressDialog.setMessage("Loading");
				m_ProgressDialog.setTitle("");
				m_ProgressDialog.setCancelable(false);
				m_ProgressDialog.setIndeterminate(true);
				m_ProgressDialog.show();
				
				Thread thread = new Thread(null, doPrint, "PrintProcess");
				thread.start();
			}
        }
		
	};

	private Runnable doPrint;
	private ProgressDialog m_ProgressDialog = null;
	
	private void DoPrint()
	{
		try
		{
			if(CheckPrint())
			{
				PrintHandler.PrintTraffic(CacheManager.SummonIssuanceInfo);
				//GenerateXmlNotice(CacheManager.SummonIssuanceInfo);
				
				AlertMessage(RingkasanActivity.this, "CETAK", "Cetak Salinan Kedua?", 2);
				
				if (m_ProgressDialog != null)
					m_ProgressDialog.dismiss();
			}
			else
			{
				CustomAlertDialog.Show(RingkasanActivity.this, "PRINTER", "CETAK SAMAN GAGAL", 0);
				if (m_ProgressDialog != null)
					m_ProgressDialog.dismiss();
			}
		}
		catch(Exception ex)
		{
			AlertMessage(RingkasanActivity.this, "Printer", "CETAK SAMAN GAGAL", 0);
			if (m_ProgressDialog != null)
				m_ProgressDialog.dismiss();
		}
	}
	
	private void DoPrintCopy()
	{
		try
		{
			if(CheckPrint())
			{
				PrintHandler.PrintTraffic(CacheManager.SummonIssuanceInfo);
				
				Thread.sleep(5000);
				
				AlertMessage(RingkasanActivity.this, "CETAK", "Cetak Salinan Kedua?", 2);
				
				if (m_ProgressDialog != null)
					m_ProgressDialog.dismiss();
			}
			else
			{
				CacheManager.DisableBluetooth();
	    		try {
	    			Thread.sleep(2000);
	    		} catch (InterruptedException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		
				AlertMessage(RingkasanActivity.this, "Printer", "CETAK SAMAN GAGAL", 3);
				if (m_ProgressDialog != null)
					m_ProgressDialog.dismiss();
			}
		}
		catch(Exception ex)
		{
			AlertMessage(RingkasanActivity.this, "Printer", "CETAK SAMAN GAGAL", 3);
			if (m_ProgressDialog != null)
				m_ProgressDialog.dismiss();
		}
	}
	
	private String CompileAddress(String address)
    {
    	String strTemp = "";
    	for(int i=0;i<address.length();i++)
    	{
    		strTemp += address.charAt(i);
    		if(i%2 == 1 && i != (address.length() - 1))
    			strTemp += ':';
    	}
    	
    	return strTemp;
    }
	
	private boolean CheckPrint()
	{
		if(!CacheManager.CheckBluetoothStatus())
		{
			CacheManager.EnableBluetooth();
    		try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean bStatus = false;
		
		if (CacheManager.mSerialService != null) {
	    	// Only if the state is STATE_NONE, do we know that we haven't started already
	    	if (CacheManager.mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
	    		// Start the Bluetooth chat services
	    		CacheManager.mSerialService.start();
	    	}
	    }
		
		if(CacheManager.mSerialService.getState() != BluetoothSerialService.STATE_CONNECTED){
    		String address = CompileAddress(SettingsHelper.MACAddress);
    		BluetoothAdapter mBluetoothAdapter = null;
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            try {
                // Attempt to connect to the device
                CacheManager.mSerialService.connect(device);
                
				Thread.sleep(3000);
			} catch (Exception e) {
				bStatus = false;
			}
    	}
		else
		{
			bStatus = true;
		}
		
		return bStatus;
	}
	
	public void AlertMessage(final Context context, String title,String message,int type)
    {
    	AlertDialog.Builder builder = new Builder(context);
    	builder.setTitle(title);
    	builder.setMessage(message);
    	if( type == 0)
    	{
    		builder.setPositiveButton("OK", null);
    	}
    	if(type == 1)
    	{
    		builder.setPositiveButton("OK", null);
    		builder.setNegativeButton("Cancel",null);
    	}
    	if( type == 2)
    	{
    		builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                	doPrint = new Runnable() {
    					@Override
    					public void run()
    					{
    						Looper.prepare();
    						DoPrintCopy();
    						Looper.loop();
    						Looper.myLooper().quit();
    					}
    				};
    				m_ProgressDialog = new ProgressDialog(RingkasanActivity.this);
    				m_ProgressDialog.setMessage("Loading");
    				m_ProgressDialog.setTitle("");
    				m_ProgressDialog.setCancelable(false);
    				m_ProgressDialog.setIndeterminate(true);
    				m_ProgressDialog.show();
    				
    				Thread thread = new Thread(null, doPrint, "LoginProcess");
    				thread.start();
                }
    		});
    		
    		builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
    			@Override
                public void onClick(DialogInterface dialog, int id) {
    				CacheManager.IsClearData = true;
    		      	Intent i = new Intent(RingkasanActivity.this,NoticesActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		      	RingkasanActivity.this.startActivity(i);
					finish();
    			}
    		});
    	}
    	if(type == 3)
    	{
    		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                	AlertMessage(RingkasanActivity.this, "CETAK", "Cetak Salinan Kedua?", 2);
                }
    		});
    	}
    	builder.show();
    }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_SEARCH || (event.getFlags() == KeyEvent.FLAG_LONG_PRESS))
		{
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME)
		{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	void FillData()
	{
		EditText tNoKenderaan = (EditText)findViewById(R.id.etSummaryNoKenderaan);
		tNoKenderaan.setText(CacheManager.SummonIssuanceInfo.VehicleNo);
		
		EditText tNoCukaiJalan = (EditText)findViewById(R.id.etSummaryNoCukaiJalan);
		tNoCukaiJalan.setText(CacheManager.SummonIssuanceInfo.RoadTaxNo);
		
		EditText tJenama = (EditText)findViewById(R.id.etSummaryJenama);
		if(CacheManager.SummonIssuanceInfo.VehicleMake.length() != 0)
		{
			tJenama.setText(CacheManager.SummonIssuanceInfo.VehicleMake);
		}
		else
		{
			tJenama.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleMake);
		}
		
		EditText tModel = (EditText)findViewById(R.id.etSummaryModel);
		if(CacheManager.SummonIssuanceInfo.VehicleModel.length() != 0)
		{
			tModel.setText(CacheManager.SummonIssuanceInfo.VehicleModel);
		}
		else
		{
			tModel.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleModel);
		}
		
		EditText tTempatjalan = (EditText)findViewById(R.id.etSummaryTempatJalan);
		if(CacheManager.SummonIssuanceInfo.OffenceLocationArea.length() != 0)
		{
			tTempatjalan.setText(CacheManager.SummonIssuanceInfo.OffenceLocationArea);
		}
		else
		{
			tTempatjalan.setText(CacheManager.SummonIssuanceInfo.SummonLocation);
		}
		
		EditText tOffenceAct = (EditText)findViewById(R.id.etSummaryUndangUndang);
		tOffenceAct.setText(CacheManager.SummonIssuanceInfo.OffenceAct);
		
		EditText tOffenceSection = (EditText)findViewById(R.id.etSummarySeksyenKaedah);
		tOffenceSection.setText(CacheManager.SummonIssuanceInfo.OffenceSection);
		
		EditText tTarikh = (EditText)findViewById(R.id.etSummaryTarikh);
		tTarikh.setText(CacheManager.GetDate());
		
		EditText tMasa = (EditText)findViewById(R.id.etSummaryMasa);
		tMasa.setText(CacheManager.GetTime().toUpperCase());
	}
	
	boolean ValidateData()
	{
		if (CacheManager.SummonIssuanceInfo.VehicleNo.length() == 0)
            {
                CustomAlertDialog.Show(RingkasanActivity.this, "NO. KEND.", "Sila Isikan No. Kend.", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.VehicleType.length() == 0)
            {
                CustomAlertDialog.Show(RingkasanActivity.this, "JENIS BADAN", "Sila Pilih Jenis Badan Kenderaan", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.OffenceAct.length() == 0)
            {
            	CustomAlertDialog.Show(RingkasanActivity.this, "UNDANG-UNDANG", "Sila Pilih Peruntukan Undang-Undang", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.OffenceSection.length() == 0)
            {
            	CustomAlertDialog.Show(RingkasanActivity.this, "UNDANG-UNDANG", "Sila Pilih Seksyen/Kaedah", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.OffenceLocationArea.length() == 0 && CacheManager.SummonIssuanceInfo.SummonLocation.length() == 0)
            {
            	CustomAlertDialog.Show(RingkasanActivity.this, "NAMA JALAN", "Sila Pilih Nama Jalan", 3);
                return false;
            }

		return true;
	}
	
	void CreateStorageDirectory()
	{
		// create a File object for the parent directory
		File summonsDirectory = new File("/mnt/sdcard/OfflineSummons/");
		// have the object build the directory structure, if needed.
		if(!summonsDirectory.exists())
		{
			summonsDirectory.mkdirs();
		}
	}
	
	private void GenerateXmlNotice(PPJSummonIssuanceInfo summons){
		CreateStorageDirectory();
		File newxmlfile = new File("/mnt/sdcard/OfflineSummons/" + summons.NoticeSerialNo + ".xml");
        try{
            newxmlfile.createNewFile();
        }catch(IOException e)
        {
        }
        
        BufferedWriter writer = null;
        try{
        	writer = new BufferedWriter(new FileWriter(newxmlfile, false));

        }catch(Exception e)
        {
        }

        String delegateDate = "yyyy-MM-dd";
        String delegateTime = "hh:mm:ss";
		String date = "";
		String time = "";
		String makeModel = "";
	    XmlSerializer serializer = Xml.newSerializer();
	    StringWriter data = new StringWriter();
	    try {
	        serializer.setOutput(data);
	        serializer.startDocument(null, null);
	        serializer.startTag(null, "ns0:Notice");
	        serializer.attribute(null, "xmlns:ns0",  "http://PPJ.MobileEnforcement.Notices.LoadOffenceNotice");
	        serializer.startTag(null, "No");
	        serializer.text(summons.NoticeSerialNo);
	        serializer.endTag(null, "No");
	        serializer.startTag(null, "HandheldID");
	        serializer.text(SettingsHelper.DeviceID);
	        serializer.endTag(null,"HandheldID");
	        serializer.startTag(null, "OfficerID");
	        serializer.text(CacheManager.UserId);
	        serializer.endTag(null,"OfficerID");
	        //serializer.startTag(null, "OfficerZone");
	        //serializer.text(summons.OfficerZone);
	        //serializer.endTag(null,"OfficerZone");
	        
	        //Vehicle
	        serializer.startTag(null, "Vehicle");
	        serializer.startTag(null, "No");
	        serializer.text(summons.VehicleNo);
	        serializer.endTag(null,"No");
	        serializer.startTag(null, "Type");
	        serializer.text(summons.VehicleType);
	        serializer.endTag(null,"Type");
	        serializer.startTag(null, "MakeModel");
	        if(summons.VehicleMake.length() == 0)
	        	makeModel += summons.VehicleMake;
	        else
	        	makeModel += summons.SelectedVehicleMake;
	        
	        if(summons.VehicleModel.length() == 0)
	        	makeModel += " " + summons.VehicleModel;
	        else
	        	makeModel += " " + summons.SelectedVehicleModel;
	        serializer.text(makeModel);
	        serializer.endTag(null, "MakeModel");
	        serializer.startTag(null, "RoadTaxNo");
	        serializer.text(summons.RoadTaxNo);
	        serializer.endTag(null, "RoadTaxNo");
	        serializer.endTag(null,"Vehicle");
	        
	        //Offence
	        serializer.startTag(null, "Offence");
	        serializer.startTag(null, "Date");
	        date = (String) DateFormat.format(delegateDate,summons.OffenceDateTime);
	        time = (String) DateFormat.format(delegateTime,summons.OffenceDateTime);
	        serializer.text(date + "T" + time);
	        serializer.endTag(null,"Date");
	        serializer.startTag(null, "ActCode");
	        serializer.text(summons.OffenceActCode);
	        serializer.endTag(null,"ActCode");
	        serializer.startTag(null, "SectionCode");
	        serializer.text(summons.OffenceSectionCode);
	        serializer.endTag(null,"SectionCode");
	        serializer.startTag(null, "Details");
	        serializer.text(summons.OffenceDetails);
	        serializer.endTag(null,"Details");
	        serializer.startTag(null, "Location");
	        serializer.text(summons.OffenceLocationArea);
	        serializer.endTag(null,"Location");
	        serializer.startTag(null, "LocationDetails");
	        serializer.text(summons.OffenceLocationDetails);
	        serializer.endTag(null,"LocationDetails");
	        serializer.startTag(null, "SquarePoleNo");
	        serializer.text(summons.PostNo);
	        serializer.endTag(null, "SquarePoleNo");
	        serializer.startTag(null, "Image");
	        serializer.startTag(null, "Image1");
	        serializer.endTag(null, "Image1");
	        serializer.startTag(null, "Image2");
	        serializer.endTag(null, "Image2");
	        serializer.startTag(null, "Image3");
	        serializer.endTag(null, "Image3");
	        serializer.startTag(null, "Image4");
	        serializer.endTag(null, "Image4");
	        serializer.startTag(null, "Image5");
	        serializer.endTag(null, "Image5");
	        serializer.endTag(null, "Image");
	        serializer.endTag(null, "Offence");
	        
	        //Compound
	        serializer.startTag(null, "Compound");
	        serializer.startTag(null, "Amount");
	        serializer.text(String.valueOf(summons.CompoundAmount1));
	        serializer.endTag(null,"Amount");
	        serializer.startTag(null, "AmountDescription");
	        serializer.text(summons.CompoundAmountDescription);
	        serializer.endTag(null,"AmountDescription");
	        serializer.startTag(null, "ExpiryDate");
	        if(summons.CompoundDate != null)
	        	date = (String) DateFormat.format(delegateDate,summons.CompoundDate);
	        else
	        	date = "";
	        serializer.text(date);
	        serializer.endTag(null, "ExpiryDate");
	        serializer.endTag(null,"Compound");

	        serializer.endTag(null,"ns0:Notice");
	        serializer.endDocument();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	    
        try {
        	//writer.write(Cryptor.Encrypt(data.toString(), "MUZZAM" + SettingsHelper.DeviceID));
			writer.write(data.toString());
        	writer.close();
		} catch (Exception e) {
		}
        
        String[] projection = new String[]{
	            MediaStore.Images.Media._ID,
	            MediaStore.Images.Media.DATA,
	            MediaStore.Images.Media.DISPLAY_NAME
	    };
	}
	
	@Override
	public void onBackPressed()
	{
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_logout:
			Intent i = new Intent(RingkasanActivity.this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			RingkasanActivity.this.startActivity(i);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume()
	{
		FillData();
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(battery_receiver);
		super.onDestroy();
	}

	@Override
	protected void onStart()
	{
		FillData();
		//SaveData();
			
		super.onStart();
	}
}
