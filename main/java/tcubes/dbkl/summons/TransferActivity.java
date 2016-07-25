package tcubes.dbkl.summons;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tcubes.dbkl.summons.R;

import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.content.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;

public class TransferActivity extends Activity {
	Runnable connectFTP;
	private int batteryPercentage = 0;
	TextView tvBattery;
	TextView tvVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_transfer);
		
		Button btnUpload = (Button) findViewById(R.id.buttonUpload);
		btnUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connectFTP = new Runnable() {
					@Override
					public void run()
					{
						Looper.prepare();
						UploadFile();
						Looper.loop();
						Looper.myLooper().quit();
					}
				};
				m_ProgressDialog = new ProgressDialog(TransferActivity.this);
				m_ProgressDialog.setMessage("Loading");
				m_ProgressDialog.setTitle("");
				m_ProgressDialog.setCancelable(false);
				m_ProgressDialog.setIndeterminate(true);
				m_ProgressDialog.show();
				
				Thread thread = new Thread(null, connectFTP, "FTPProcess");
				thread.start();
				//ConnectFTP();
			}
		});
		
		Button btnDownload = (Button) findViewById(R.id.buttonDownload);
		btnDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connectFTP = new Runnable() {
					@Override
					public void run()
					{
						Looper.prepare();
						DownloadFile();
						Looper.loop();
						Looper.myLooper().quit();
					}
				};
				m_ProgressDialog = new ProgressDialog(TransferActivity.this);
				m_ProgressDialog.setMessage("Loading");
				m_ProgressDialog.setTitle("");
				m_ProgressDialog.setCancelable(false);
				m_ProgressDialog.setIndeterminate(true);
				m_ProgressDialog.show();
				
				Thread thread = new Thread(null, connectFTP, "FTPProcess");
				thread.start();
				//ConnectFTP();
			}
		});
		
		TextView tvNumberOfNotices = (TextView) findViewById(R.id.tvNumberOfNotice);
		tvNumberOfNotices.setText(String.valueOf(GetNumberOfNotices()));
		
		tvVersion = (TextView) findViewById(R.id.tvVersion);
		tvVersion.setText("1.0.0.0");
		
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
                tvBattery = (TextView) findViewById(R.id.tvBattery);
                tvBattery.setText(String.valueOf(batteryPercentage));
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
	
	private ProgressDialog m_ProgressDialog = null;
	
	void CreateStorageDirectory()
	{
		// create a File object for the parent directory
		File summonsDirectory = new File("/mnt/sdcard/OfflineSummons/");
		// have the object build the directory structure, if needed.
		if(!summonsDirectory.exists())
		{
			summonsDirectory.mkdirs();
		}
		
		File summonsZipDirectory = new File("/mnt/sdcard/OfflineSummonsZip/");
		if(!summonsZipDirectory.exists())
		{
			summonsZipDirectory.mkdirs();
		}
		
		File DownloadDirectory = new File("/mnt/sdcard/DownloadLookupTables/");
		if(!DownloadDirectory.exists())
		{
			DownloadDirectory.mkdirs();
		}
		
		File[] files = summonsZipDirectory.listFiles();
        for (File file : files)
        {
            file.delete();
        }
	}
	
	private int GetNumberOfNotices()
	{
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
        return nAmountNoticeIssued;
	}
	
	FTPClient ftpClient = new FTPClient();
	
	public void UploadFile()
	{
		if(ConnectFTP())
		{
			try {
				File summonsDirectory = new File("/mnt/sdcard/OfflineSummons/");
				
				File[] files = summonsDirectory.listFiles();
				String[] filenames = new String[files.length];
		        for (int i = 0;i<files.length;i++)
		        {
		        	filenames[i] = files[i].getAbsolutePath();
		        }
		        Date now = new Date();
		        String delegate = "yyyyMMddhhmmss";
		        SimpleDateFormat formatter = new SimpleDateFormat("SSS");
		        int dateString = Integer.parseInt(formatter.format(now));
				String current = (String) DateFormat.format(delegate,now) + String.format("%05d", dateString);
				File zipFilePath = new File("/mnt/sdcard/OfflineSummonsZip/" + current + ".zip");
		        ZipUtil zipFile = new ZipUtil(filenames, zipFilePath.getAbsolutePath());
		        zipFile.zip();
		        
		        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		        ftpClient.changeWorkingDirectory(SettingsHelper.FTPUpload);
                FileInputStream in = new FileInputStream(zipFilePath);
                String name = zipFilePath.getName().substring(0, zipFilePath.getName().indexOf("."));
		        ftpClient.storeFile(name + ".H", in);
		        ftpClient.rename(name + ".H",name + ".S");
		        in.close();
		        ftpClient.changeWorkingDirectory("/");
		        ftpClient.logout();
		        ftpClient.disconnect();
		        
		        zipFilePath.delete();
		        
		        for (File file : files)
		        {
		            file.delete();
		        }
		        
		        //DBKLAlertDialog.Show(TransferActivity.this, "MAKLUMAT", "Muat Naik Notis Berjaya", 0);
		        
		        Intent i = new Intent(this, MainActivity.class);
				startActivity(i);
			} catch (Exception e) {
				DBKLAlertDialog.Show(TransferActivity.this, "ERROR", "Muat Naik Notis Gagal", 0);
			} 
		}
		else
		{
			DBKLAlertDialog.Show(TransferActivity.this, "ERROR", "Muat Naik Notis Gagal", 0);
		}
		if (m_ProgressDialog != null)
			m_ProgressDialog.dismiss();
	}
	
	public void UnzipFile(String file) throws Exception
	{
		File zipFile = new File(file);
		try
		{
			ZipUtil zipUtil = new ZipUtil(file, "/mnt/sdcard/DownloadLookupTables/");
			zipUtil.unzip();
			zipFile.delete();
		}
		catch(Exception ex)
		{
			zipFile.delete();
			throw ex;
		}
	}
	
	public void ProcessLookupTables(String location) throws Exception
	{
		boolean okay = true;
		File directory = new File(location);
		
		String[] fileList = directory.list();
		for(String file : fileList)
		{
			File xml = new File(directory.getAbsolutePath(), file);
			String tableName = xml.getName().substring(0, xml.getName().indexOf("."));
			DbUtils util = new DbUtils(this.getApplicationContext());
			try
			{
				util.Open();
				util.deleteTable(tableName);
				
				String tableData = XmlUtility.getXmlFile(xml.getAbsolutePath());
				
				DBKLMessage obj = new DBKLMessage(tableData);
				if (obj != null)
				{
					Node node = obj.GetNode(tableName);

					if (node != null)
					{
						NodeList child = node.getChildNodes();
						int length = child.getLength();
						for (int i = 0; i < length; i++)
						{
							if(child.item(i)!= null && child.item(i).getNodeName().equalsIgnoreCase(tableName))
							{
								NodeList data = child.item(i).getChildNodes();
								int dataLength = child.getLength();
								ArrayList<String> column = new ArrayList<String>();
								ArrayList<String> value = new ArrayList<String>();
								
								for (int j = 0; j < dataLength; j++)
								{
									if(data.item(j) != null && !data.item(j).getNodeName().contains("#"))
									{
										column.add(data.item(j).getNodeName().trim());
										value.add(data.item(j).getTextContent().trim());
									}
								}
								
								String[] columnsName = new String[column.size()];
								String[] values = new String[value.size()];
								
								for(int j = 0; j < column.size(); j++)
								{
									columnsName[j] = column.get(j);
									values[j] = value.get(j);
								}
								
								util.insertData(tableName, columnsName, values);
							}
						}
					}
				}
				
				util.Close();
				xml.delete();
			}
			catch(Exception ex)
			{
				util.Close();
				xml.delete();
				okay = false;
			}
		}
		
		if(!okay)
		{
			throw new Exception("Failed");
		}
	}
	 	
	public void DownloadFile()
	{
		if(ConnectFTP())
		{
			try
			{
				//File DBDirectory = new File("/data/data/tcubes.dbkl.summons/databases/");
						 
		        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.changeWorkingDirectory(SettingsHelper.FTPDownload);
				String[] filelist = ftpClient.listNames();
				int index = -1;

				for (int i = 0; i < filelist.length; i++)
				{
					if (filelist[i].trim().contains(SettingsHelper.DeviceID))
					{
						index = i;
					}
				}

				if (index == -1)
				{
					ftpClient.logout();
					DBKLAlertDialog.Show(TransferActivity.this, "ERROR", "Muat Turun Fail Handheld Gagal", 0);
					if (m_ProgressDialog != null)
						m_ProgressDialog.dismiss();
					return;
				}

				FTPFile obj = ftpClient.listFiles()[index];
				InputStream input = ftpClient.retrieveFileStream(obj.getName());
				OutputStream output = new FileOutputStream("/mnt/sdcard/DownloadLookupTables/LookupTables.S");

				byte data[] = new byte[1024];

				int count;
				input.available();

				long lenghtOfFile = obj.getSize();

				while ((count = input.read(data)) != -1)
				{
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				
				ftpClient.deleteFile(obj.getName());
				
		        ftpClient.changeWorkingDirectory("/");
		        ftpClient.logout();
		        ftpClient.disconnect();
		        
		        UnzipFile("/mnt/sdcard/DownloadLookupTables/LookupTables.S");
		        
		        ProcessLookupTables("/mnt/sdcard/DownloadLookupTables/");
		        
		        //DBKLAlertDialog.Show(TransferActivity.this, "MAKLUMAT", "Muat Turun Fail Handheld Berjaya", 0);
		        
		        Intent i = new Intent(this, MainActivity.class);
				startActivity(i);
			} catch (Exception e) {
				DBKLAlertDialog.Show(TransferActivity.this, "ERROR", "Muat Turun Fail Handheld Gagal", 0);
			} 
		}
		else
		{
			DBKLAlertDialog.Show(TransferActivity.this, "ERROR", "Muat Turun Fail Handheld Gagal", 0);
		}
		if (m_ProgressDialog != null)
			m_ProgressDialog.dismiss();
	}
	
	public boolean ConnectFTP()
	{
	      try {
	    	  ftpClient.connect(SettingsHelper.FTPServer,21);				
	    	  ftpClient.login(SettingsHelper.FTPUser,SettingsHelper.FTPPassword);
	      } catch (Exception e) {
				return false;
	      }
	      return true;
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
                	
                }
    		});
    		
    		builder.setNegativeButton("No",null);
    	}
    	if(type == 3)
    	{
    		builder.setPositiveButton("OK", null);
    		builder.setCancelable(true);
    	}
    	builder.show();
    }
    
    protected void onDestroy() {
		unregisterReceiver(battery_receiver);
		super.onDestroy();
	}
    
    
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
	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		return;
	}
	private Window w;
	@Override
	public void onResume()
	{
		w = this.getWindow();
	    w.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
	    w.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	    w.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
	    
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		super.onResume();
	}
	
	@Override
	public void onStart()
	{
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		super.onStart();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
