package tcubes.dbkl.summons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import tcubes.dbkl.summons.R;

import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
	                //else
	                //    nAmountPictureIssued++;
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
				if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 0)
				{
					PrintHandler.PrintTraffic(CacheManager.SummonIssuanceInfo);
				}
				if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 1)
				{
					PrintHandler.PrintGeneral(CacheManager.SummonIssuanceInfo);
				}
				if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 2)
				{
					PrintHandler.PrintBusLane(CacheManager.SummonIssuanceInfo);
				}
				GenerateXmlNotice(CacheManager.SummonIssuanceInfo);
				SettingsHelper.IncrementSerialNumber();
				
				//Thread.sleep(5000);
				
				AlertMessage(RingkasanActivity.this, "CETAK", "Cetak Salinan Kedua?", 2);
				
				if (m_ProgressDialog != null)
					m_ProgressDialog.dismiss();
			}
			else
			{
				DBKLAlertDialog.Show(RingkasanActivity.this, "PRINTER", "CETAK SAMAN GAGAL", 0);
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
				if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 0)
				{
					PrintHandler.PrintTraffic(CacheManager.SummonIssuanceInfo);
				}
				if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 1)
				{
					PrintHandler.PrintGeneral(CacheManager.SummonIssuanceInfo);
				}
				if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 2)
				{
					PrintHandler.PrintBusLane(CacheManager.SummonIssuanceInfo);
				}
				
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
		
		/*
		try
		{
			if(CacheManager.woosim != null)
			{
				CacheManager.woosim.closeConnection();
				Thread.sleep(1000);
			}
			CacheManager.woosim = new WoosimPrinter();
			Thread.sleep(1000);
		}
		catch(Exception ex)
		{
			
		}
		String address = CompileAddress(SettingsHelper.MACAddress);
		int reVal = CacheManager.woosim.BTConnection(address, false);
		if(reVal== 1){
			bStatus = true;
		}else if(reVal== -2){
			bStatus= false;
		}else if(reVal== -5){
			bStatus=false;
		}else if(reVal == -6){
			bStatus = true;
		}else if(reVal == -8){
			bStatus = false;
		}
		*/
		
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
		EditText tJenisNotis = (EditText)findViewById(R.id.etSummaryJenisNotice);
		tJenisNotis.setText(CacheManager.SummonIssuanceInfo.jenisNotis);
		//tJenisNotis.setText(MaklumatActivity.strJenisNotis);
		
		EditText tNoKenderaan = (EditText)findViewById(R.id.etSummaryNoKenderaan);
		tNoKenderaan.setText(CacheManager.SummonIssuanceInfo.noKenderaan);
		//tNoKenderaan.setText(MaklumatActivity.strVehicleNo);
		
		EditText tNoCukaiJalan = (EditText)findViewById(R.id.etSummaryNoCukaiJalan);
		tNoCukaiJalan.setText(CacheManager.SummonIssuanceInfo.noCukaiJalan);
		//tNoCukaiJalan.setText(MaklumatActivity.strCukaiJalan);
		
		EditText tJenama = (EditText)findViewById(R.id.etSummaryJenama);
		if(CacheManager.SummonIssuanceInfo.jenama.length() != 0)
		{
			tJenama.setText(CacheManager.SummonIssuanceInfo.jenama);
		}
		else
		{
			tJenama.setText(CacheManager.SummonIssuanceInfo.vehicleMake);
		}
		//tJenama.setText(MaklumatActivity.strJenama);
		
		EditText tModel = (EditText)findViewById(R.id.etSummaryModel);
		if(CacheManager.SummonIssuanceInfo.model.length() != 0)
		{
			tModel.setText(CacheManager.SummonIssuanceInfo.model);
		}
		else
		{
			tModel.setText(CacheManager.SummonIssuanceInfo.vehicleModel);
		}
		//tModel.setText(MaklumatActivity.strModel);
		
		EditText tTempatjalan = (EditText)findViewById(R.id.etSummaryTempatJalan);
		if(CacheManager.SummonIssuanceInfo.offenceLocationArea.length() != 0)
		{
			tTempatjalan.setText(CacheManager.SummonIssuanceInfo.offenceLocationArea);
		}
		else
		{
			tTempatjalan.setText(CacheManager.SummonIssuanceInfo.summonLocation);
		}
		
		EditText tOffenceAct = (EditText)findViewById(R.id.etSummaryUndangUndang);
		tOffenceAct.setText(CacheManager.SummonIssuanceInfo.offenceAct);
		
		EditText tOffenceSection = (EditText)findViewById(R.id.etSummarySeksyenKaedah);
		tOffenceSection.setText(CacheManager.SummonIssuanceInfo.offenceSection);
		
		EditText tTarikh = (EditText)findViewById(R.id.etSummaryTarikh);
		tTarikh.setText(CacheManager.GetDate());
		
		EditText tMasa = (EditText)findViewById(R.id.etSummaryMasa);
		tMasa.setText(CacheManager.GetTime().toUpperCase());
	}
	void SaveData()
	{
	}
	
	boolean ValidateData()
	{
		if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 0)
		{
			if (CacheManager.SummonIssuanceInfo.noKenderaan.length() == 0)
            {
                DBKLAlertDialog.Show(RingkasanActivity.this, "NO. KEND.", "Sila Isikan No. Kend.", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.jenisBadan.length() == 0)
            {
                DBKLAlertDialog.Show(RingkasanActivity.this, "JENIS BADAN", "Sila Pilih Jenis Badan Kenderaan", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.offenceAct.length() == 0)
            {
            	DBKLAlertDialog.Show(RingkasanActivity.this, "UNDANG-UNDANG", "Sila Pilih Peruntukan Undang-Undang", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.offenceSection.length() == 0)
            {
            	DBKLAlertDialog.Show(RingkasanActivity.this, "UNDANG-UNDANG", "Sila Pilih Seksyen/Kaedah", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.offenceLocationArea.length() == 0 && CacheManager.SummonIssuanceInfo.summonLocation.length() == 0)
            {
            	DBKLAlertDialog.Show(RingkasanActivity.this, "NAMA JALAN", "Sila Pilih Nama Jalan", 3);
                return false;
            }
		}
		if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 1)
		{
			if (CacheManager.SummonIssuanceInfo.name.length() == 0)
            {
				DBKLAlertDialog.Show(RingkasanActivity.this, "NAMA/SYKT.", "Sila Isikan Nama/Sykt.", 3);
                return false;
            }
			if (CacheManager.SummonIssuanceInfo.offenceAct.length() == 0)
            {
            	DBKLAlertDialog.Show(RingkasanActivity.this, "UNDANG-UNDANG", "Sila Pilih Peruntukan Undang-Undang", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.offenceSection.length() == 0)
            {
            	DBKLAlertDialog.Show(RingkasanActivity.this, "UNDANG-UNDANG", "Sila Pilih Seksyen/Kaedah", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.offenceLocationArea.length() == 0 && CacheManager.SummonIssuanceInfo.summonLocation.length() == 0)
            {
            	DBKLAlertDialog.Show(RingkasanActivity.this, "NAMA JALAN", "Sila Pilih Nama Jalan", 3);
                return false;
            }
		}
		if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 2)
		{
			if (CacheManager.SummonIssuanceInfo.name.length() == 0)
            {
				DBKLAlertDialog.Show(RingkasanActivity.this, "NAMA/SYKT.", "Sila Isikan Nama/Sykt.", 3);
                return false;
            }
			if (CacheManager.SummonIssuanceInfo.kptNo.length() == 0)
            {
				DBKLAlertDialog.Show(RingkasanActivity.this, "NO. K.P.", "Sila Isikan No. K.P.", 3);
            }
			if (CacheManager.SummonIssuanceInfo.offenceAct.length() == 0)
            {
            	DBKLAlertDialog.Show(RingkasanActivity.this, "UNDANG-UNDANG", "Sila Pilih Peruntukan Undang-Undang", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.offenceSection.length() == 0)
            {
            	DBKLAlertDialog.Show(RingkasanActivity.this, "UNDANG-UNDANG", "Sila Pilih Seksyen/Kaedah", 3);
                return false;
            }
            if (CacheManager.SummonIssuanceInfo.offenceLocationArea.length() == 0 && CacheManager.SummonIssuanceInfo.summonLocation.length() == 0)
            {
            	DBKLAlertDialog.Show(RingkasanActivity.this, "NAMA JALAN", "Sila Pilih Nama Jalan", 3);
                return false;
            }
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
	
	private void GenerateXmlNotice(DBKLSummonIssuanceInfo summons){
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
	        serializer.attribute(null, "xmlns:ns0",  "http://DBKL.MobileEnforcement.Notices.LoadOffenceNotice");
	        serializer.startTag(null, "No");
	        serializer.text(summons.NoticeSerialNo);
	        serializer.endTag(null, "No");
	        serializer.startTag(null, "Type");
	        serializer.text(String.valueOf(summons.jenisNotisCode + 1));
	        serializer.endTag(null, "Type");
	        serializer.startTag(null, "HandheldID");
	        serializer.text(SettingsHelper.DeviceID);
	        serializer.endTag(null,"HandheldID");
	        serializer.startTag(null, "OfficerID");
	        serializer.text(CacheManager.UserId);
	        serializer.endTag(null,"OfficerID");
	        serializer.startTag(null, "OfficerZone");
	        serializer.text(summons.OfficerZone);
	        serializer.endTag(null,"OfficerZone");
	        
	        //Offender
	        serializer.startTag(null, "Offender");
	        serializer.startTag(null, "Name");
	        serializer.text(summons.name);
	        serializer.endTag(null,"Name");
	        serializer.startTag(null, "KPTNo");
	        serializer.text(summons.kptNo);
	        serializer.endTag(null,"KPTNo");
	        serializer.startTag(null, "AddressLine1");
	        serializer.text(summons.address1);
	        serializer.endTag(null,"AddressLine1");
	        serializer.startTag(null, "AddressLine2");
	        serializer.text(summons.address2);
	        serializer.endTag(null,"AddressLine2");
	        serializer.startTag(null, "AddressLine3");
	        serializer.text(summons.address3);
	        serializer.endTag(null,"AddressLine3");
	        serializer.startTag(null, "LicenseNo");
	        serializer.text(summons.LicenseNo);
	        serializer.endTag(null,"LicenseNo");
	        serializer.startTag(null, "LicenceExipryDate");
	        if(summons.licenseExpiryDate != null)
	        	date = (String) DateFormat.format(delegateDate,summons.licenseExpiryDate);
	        else
	        	date = "";
	        serializer.text(date);
	        serializer.endTag(null,"LicenceExipryDate");
	        serializer.endTag(null,"Offender");
	        
	        //Vehicle
	        serializer.startTag(null, "Vehicle");
	        serializer.startTag(null, "No");
	        serializer.text(summons.noKenderaan);
	        serializer.endTag(null,"No");
	        serializer.startTag(null, "Type");
	        serializer.text(summons.jenisBadan);
	        serializer.endTag(null,"Type");
	        serializer.startTag(null, "MakeModel");
	        if(summons.jenama.length() == 0)
	        	makeModel += summons.jenama;
	        else
	        	makeModel += summons.vehicleMake;
	        
	        if(summons.model.length() == 0)
	        	makeModel += " " + summons.model;
	        else
	        	makeModel += " " + summons.vehicleModel;
	        serializer.text(makeModel);
	        serializer.endTag(null,"MakeModel");
	        serializer.startTag(null, "RoadTaxNo");
	        serializer.text(summons.noCukaiJalan);
	        serializer.endTag(null,"RoadTaxNo");
	        serializer.startTag(null, "RoadTaxExpiryDate");
	        if(summons.roadtaxExpiryDate != null)
	        	date = (String) DateFormat.format(delegateDate,summons.roadtaxExpiryDate);
	        else
	        	date = "";
	        serializer.text(date);
	        serializer.endTag(null,"RoadTaxExpiryDate");
	        serializer.endTag(null,"Vehicle");
	        
	        //Offence
	        serializer.startTag(null, "Offence");
	        serializer.startTag(null, "Date");
	        date = (String) DateFormat.format(delegateDate,summons.OffenceDateTime);
	        time = (String) DateFormat.format(delegateTime,summons.OffenceDateTime);
	        serializer.text(date + "T" + time);
	        serializer.endTag(null,"Date");
	        serializer.startTag(null, "ActCode");
	        serializer.text(summons.offenceActCode);
	        serializer.endTag(null,"ActCode");
	        serializer.startTag(null, "SectionCode");
	        serializer.text(summons.offenceSectionCode);
	        serializer.endTag(null,"SectionCode");
	        serializer.startTag(null, "Details");
	        serializer.text(summons.offenceDetails);
	        serializer.endTag(null,"Details");
	        serializer.startTag(null, "Location");
	        serializer.text(summons.offenceLocationArea);
	        serializer.endTag(null,"Location");
	        serializer.startTag(null, "LocationDetails");
	        serializer.text(summons.offenceLocationDetails);
	        serializer.endTag(null,"LocationDetails");
	        serializer.startTag(null, "SquarePoleNo");
	        serializer.text(summons.postNo);
	        serializer.endTag(null,"SquarePoleNo");
	        serializer.startTag(null, "Image");
	        serializer.startTag(null, "Image1");
	        serializer.endTag(null,"Image1");
	        serializer.startTag(null, "Image2");
	        serializer.endTag(null,"Image2");
	        serializer.startTag(null, "Image3");
	        serializer.endTag(null,"Image3");
	        serializer.startTag(null, "Image4");
	        serializer.endTag(null,"Image4");
	        serializer.startTag(null, "Image5");
	        serializer.endTag(null,"Image5");
	        serializer.endTag(null,"Image");
	        serializer.endTag(null,"Offence");
	        
	        //Compound
	        serializer.startTag(null, "Compound");
	        serializer.startTag(null, "Amount");
	        serializer.text(String.valueOf(summons.compoundAmount1));
	        serializer.endTag(null,"Amount");
	        serializer.startTag(null, "AmountDescription");
	        serializer.text(summons.compoundAmountDescription);
	        serializer.endTag(null,"AmountDescription");
	        serializer.startTag(null, "ExpiryDate");
	        if(summons.CompoundDate != null)
	        	date = (String) DateFormat.format(delegateDate,summons.CompoundDate);
	        else
	        	date = "";
	        serializer.text(date);
	        serializer.endTag(null,"ExpiryDate");
	        serializer.startTag(null, "CourtDate");
	        if(summons.courtDate != null)
	        	date = (String) DateFormat.format(delegateDate,summons.courtDate);
	        else
	        	date = "";
	        serializer.text(date);
	        serializer.endTag(null,"CourtDate");
	        serializer.endTag(null,"Compound");

	        serializer.startTag(null, "Advertisement");
	        serializer.text(summons.advertisement);
	        serializer.endTag(null,"Advertisement");
	        serializer.endTag(null,"ns0:Notice");
	        serializer.endDocument();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	    
        try {
        	writer.write(Cryptor.Encrypt(data.toString(), "TRICUBES" + SettingsHelper.DeviceID));
        	writer.close();
		} catch (Exception e) {
		}
        
        String[] projection = new String[]{
	            MediaStore.Images.Media._ID,
	            MediaStore.Images.Media.DATA,
	            MediaStore.Images.Media.DISPLAY_NAME
	    };

        try
        {
		    // Get the base URI for the People table in the Contacts content provider.
		    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	
		    // Make the query.
		    Cursor cur = managedQuery(images,
		            projection, // Which columns to return
		            "",         // Which rows to return (all rows)
		            null,       // Selection arguments (none)
		            ""          // Ordering
		            );
	
			List<String> list = new ArrayList<String>();
	
			if (cur != null)
			{
			    if (cur.moveToFirst()) {
			        int nameColumn = cur.getColumnIndex(
			            MediaStore.Images.Media.DATA);
			        do {
			        	list.add(cur.getString(nameColumn));
			        } while (cur.moveToNext());
			    }
			}
			
			if(list.size() > 0)
			{
				for (int i = 0; i< list.size(); i++)
				{
					//File image = new File(list.get(i));
		    		//File newImage = new File("/mnt/sdcard/OfflineSummons/" + summons.NoticeSerialNo + "Photo" + String.valueOf(i) + ".xml");
		    		//image.renameTo(newImage);
		    		Bitmap image = decodeFile(list.get(i));
		    		File newImage = new File("/mnt/sdcard/OfflineSummons/" + summons.NoticeSerialNo + "Photo" + String.valueOf(i) + ".xml");
		    		try {
		    		       FileOutputStream output = new FileOutputStream(newImage);
		    		       image.compress(Bitmap.CompressFormat.JPEG, 90, output);
		    		       output.close();
		    		} catch (Exception e) {
		    		       e.printStackTrace();
		    		}
				}
			}
        }
        catch(Exception ex)
        {
        	
        }
	}
	
	private Bitmap decodeFile(String path){
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(path),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=120;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        return BitmapFactory.decodeStream(new FileInputStream(path), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}
	
	@Override
	public void onBackPressed()
	{
		//Intent i = new Intent(NoticesActivity.this,LoginActivity.class);
		//NoticesActivity.this.startActivity(i);
		return;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_ringkasan, menu);
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
	
	private Window w;
	@Override
	public void onResume()
	{		
		w = this.getWindow();
	    w.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
	    w.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	    w.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
	    
		FillData();
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
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
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		FillData();
		//SaveData();
			
		super.onStart();
	}
}
