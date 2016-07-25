package tcubes.dbkl.summons;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Properties;

import tcubes.dbkl.summons.R;

import android.net.wifi.WifiManager;
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.content.res.Configuration;

public class MainActivity extends Activity {

	//private DatePicker dpResult;
	//private TimePicker tpResult;
	private Button btn_OK;
	private EditText txtMACAddress;
	private CheckBox chkNewPrinter;
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private String address;
	
	//Card data buffer
	byte[] cardBuff = new byte[255];
	
	static Handler timeHandler;
	static Handler fileHandler;

	//set up protocol type
	boolean protocol = false;
	Properties prop;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		if(CacheManager.Init(getApplicationContext()))
		{
			if(SettingsHelper.CheckFile())
			{
				SettingsHelper.LoadFile();
			}
			
			try
			{
				if(CacheManager.mSerialService != null)
				{
					CacheManager.mSerialService.stop();
					Thread.sleep(1000);
				}
				CacheManager.mSerialService = new BluetoothSerialService(CacheManager.context, CacheManager.mHandlerBT);
				Thread.sleep(1000);
				IntentFilter filter = new IntentFilter( "android.bluetooth.device.action.PAIRING_REQUEST");
				this.registerReceiver(mReceiver, filter);
			}
			catch(Exception ex)
			{
				
			}
			
			timeHandler = new Handler();
			Runnable run = new Runnable() {
	
		        @Override
		        public void run() {
		        	setCurrentDate();
		        	timeHandler.postDelayed(this, 500);
		        }
		    };
		    timeHandler.postDelayed(run, 500);
		    
		    this.runOnUiThread(run);
			
			txtMACAddress=(EditText)findViewById(R.id.etMACAddress);
			//txtMACAddress.setRawInputType(Configuration.KEYBOARD_QWERTY);
			txtMACAddress.setEnabled(false);
			txtMACAddress.addTextChangedListener(new TextWatcher() {
	            @Override
	            public void onTextChanged(CharSequence s, int start, int before, int count) {
	
	                // TODO Auto-generated method stub
	            }
	
	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	
	                // TODO Auto-generated method stub
	            }
	
	            @Override
	            public void afterTextChanged(Editable s) {
	
	                // TODO Auto-generated method stub
	            	String filtered_str = s.toString();
	                if (filtered_str.matches(".*[^A-F^0-9].*")) {
		                filtered_str = filtered_str.replaceAll("[^A-F^0-9]", "");
		                s.clear();
		                s.insert(0, filtered_str);
	                }
	            }
	        });
			
			TextView tvDeviceID = (TextView) findViewById(R.id.tvDeviceIDMain);
			tvDeviceID.setText(SettingsHelper.DeviceID);
			
		     txtMACAddress.setText(SettingsHelper.MACAddress);
		     addListenerOnChkNewPrinter();
		     mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	
	    	 chkNewPrinter = (CheckBox) findViewById(R.id.cbPrinter);
		     if(txtMACAddress.getText().toString().length() == 0)
		     {
		    	 chkNewPrinter.setChecked(true);
		    	 txtMACAddress=(EditText)findViewById(R.id.etMACAddress);
	 			txtMACAddress.setEnabled(true);
	 			txtMACAddress.setFocusable(true);
		     }
			btn_OK = (Button)findViewById(R.id.buttonOk);
			btn_OK.setOnClickListener(btnOKListener);
			
			Button btnFTP = (Button) findViewById(R.id.buttonFTPTest);
			btnFTP.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
		            Intent i = new Intent(MainActivity.this,TransferActivity.class);
		            MainActivity.this.startActivity(i);
				}
			});
		}
		else
		{
			DBKLAlertDialog.Show(MainActivity.this, "ERROR", "Invalid Device ID", 0);
		}
	}

	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				try {
					byte[] pinBytes = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, "1234");
					Method m = device.getClass().getMethod("setPin", byte[].class);
					m.invoke(device, pinBytes);
					try {
						device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private OnClickListener btnOKListener = new OnClickListener() {
        public void onClick(View v) {
        	Time now = new Time();
        	now.setToNow();
        	
        	EditText etMACAddress = (EditText) findViewById(R.id.etMACAddress);
        	if( txtMACAddress.getText().toString().length() == 0 )
	    	{
        		DBKLAlertDialog.Show(MainActivity.this,"ERROR", "Sila Masukkan MAC Address Printer", 0);
	    	}
        	else
        	{
        		if(etMACAddress.getText().toString().equalsIgnoreCase("C2007"))
	        	{
	        		chkNewPrinter = (CheckBox) findViewById(R.id.cbPrinter);
	        		chkNewPrinter.setChecked(false);

					//Intent startMain = new Intent(Intent.ACTION_MAIN);
					//startMain.addCategory(Intent.CATEGORY_HOME);
					//startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					//startActivity(startMain);
					finish();
	        	}
        		else
        		{
	        		if(txtMACAddress.getText().toString().length() != 12)
		    		{
		    			DBKLAlertDialog.Show(MainActivity.this,"ERROR", "Bluetooth string is not a valid bluetooth address", 0);
		    		}
	        		else
	        		{
			        	AlertMessage(MainActivity.this,"Tarikh Dan Masa", "Sila sahkan Tarikh dan Masa betul : " + 
			            			CacheManager.GetDate() + " " + CacheManager.GetTime().toUpperCase(), 2);
	        		}
        		}
        	}
        }
    };
    
    private void addListenerOnChkNewPrinter()
    {
    	chkNewPrinter = (CheckBox) findViewById(R.id.cbPrinter);
    	 
    	chkNewPrinter.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
	    		if (isChecked)
	    		{
	    			txtMACAddress=(EditText)findViewById(R.id.etMACAddress);
	    			txtMACAddress.setEnabled(true);
	    			txtMACAddress.setFocusable(true);
	    			
	    		}
	    		else
	    		{
	    			txtMACAddress=(EditText)findViewById(R.id.etMACAddress);
	    			txtMACAddress.setText(SettingsHelper.MACAddress);
	    			txtMACAddress.setEnabled(false);
	    		}
    	  }
    	});
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
                	doCheckPrinter = new Runnable() {
            				@Override
            				public void run()
            				{
							Looper.prepare();
							DoCheckPrinter();
							Looper.loop();
							Looper.myLooper().quit();
						}
					};
					m_ProgressDialog = new ProgressDialog(MainActivity.this);
					m_ProgressDialog.setMessage("Loading");
					m_ProgressDialog.setTitle("");
					m_ProgressDialog.setCancelable(false);
					m_ProgressDialog.setIndeterminate(true);
					m_ProgressDialog.show();

					Thread thread = new Thread(null, doCheckPrinter, "LoginProcess");
					thread.start();
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
    
	private Runnable doCheckPrinter;
	private ProgressDialog m_ProgressDialog = null;
    
    private void DoCheckPrinter()
    {
    	if(CheckPrinter())
    	{
    		if (m_ProgressDialog != null)
				m_ProgressDialog.dismiss();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
			finish();
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
    		
    		if (m_ProgressDialog != null)
				m_ProgressDialog.dismiss();
    	}
    }
  
    private boolean CheckPrinter()
    {
    	boolean bStatus = false;
    	
    	try
    	{
	    	txtMACAddress=(EditText)findViewById(R.id.etMACAddress);
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

			try
			{
				address = txtMACAddress.getText().toString();
				address = CompileAddress(address);
				// Get the BLuetoothDevice object
				BluetoothAdapter mBluetoothAdapter = null;
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

				if (CacheManager.mSerialService != null) {
					// Only if the state is STATE_NONE, do we know that we haven't started already
					if (CacheManager.mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
						// Start the Bluetooth chat services
						CacheManager.mSerialService.start();
					}
				}

				// Attempt to connect to the device
				CacheManager.mSerialService.connect(device);
				Thread.sleep(5000);
			}
			catch(Exception ex)
			{
				AlertMessage(this,"ERROR", "FAILED TO CONNECT", 3);
				return false;
			}

			if (CacheManager.mSerialService != null && CacheManager.mSerialService.getState() == BluetoothSerialService.STATE_CONNECTED) {
				SettingsHelper.MACAddress = txtMACAddress.getText().toString();
				SettingsHelper.SaveFile();
				PrintHandler.TestPrint(SettingsHelper.MACAddress);
				bStatus = true;
			}
			else
			{
				AlertMessage(this,"ERROR", "FAILED TO CONNECT", 3);
				bStatus = false;
			}
    	}
    	catch(Exception ex)
    	{
    		AlertMessage(this,"ERROR", "CETAK SAMAN GAGAL", 3);
			bStatus = false;
    	}
    	
		return bStatus;
    	
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
    
    protected void onDestroy() {
		try {
			this.unregisterReceiver(mReceiver);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		super.onDestroy();
	}
    
    // display current date
	public void setCurrentDate() {

		TextView tvDate = (TextView) findViewById(R.id.tvDate);
		tvDate.setText(CacheManager.GetDate());
		TextView tvTime = (TextView) findViewById(R.id.tvTime);
		tvTime.setText(CacheManager.GetTime().toUpperCase());
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
	
	@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
        	
        }
        if (Intent.ACTION_VOICE_COMMAND.equals(intent.getAction())) {
        	
        }
    }

	@Override
	public void onBackPressed()
	{
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
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
