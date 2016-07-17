package id.zenmorf.com.ppjhandheld;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private Spinner spinnerZone;
	private EditText txtUserName;
	private EditText txtPassword;
	private Button btnLogin;
	static Handler timeHandler;
	
	//private String selectOfficerZone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		addItemsOnSpinnerZone();		
		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(btnloginListener);
		
		
		timeHandler = new Handler();
		Runnable run = new Runnable() {

	        @Override
	        public void run() {
	        	Time now = new Time();
	        	now.setToNow();
	        	TextView tvDateTime = (TextView) findViewById(R.id.tvDateTime);
	        	tvDateTime.setText("  " + CacheManager.GetDate() + "\r\n " + CacheManager.GetTime().toUpperCase());
	        	timeHandler.postDelayed(this, 500);
	        }
	    };
	    timeHandler.postDelayed(run, 500);
	    
		TextView tvHandheldID = (TextView) findViewById(R.id.tvDeviceID);
		tvHandheldID.setText(SettingsHelper.DeviceID);
	}
	
	private OnClickListener btnloginListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if(ValidateLogin())
			{
				doLogin = new Runnable() {
					@Override
					public void run()
					{
						Looper.prepare();
						DoLogin();
						Looper.loop();
						Looper.myLooper().quit();
					}
				};
				m_ProgressDialog = new ProgressDialog(LoginActivity.this);
				m_ProgressDialog.setMessage("Loading");
				m_ProgressDialog.setTitle("");
				m_ProgressDialog.setCancelable(false);
				m_ProgressDialog.setIndeterminate(true);
				m_ProgressDialog.show();
				
				Thread thread = new Thread(null, doLogin, "LoginProcess");
				thread.start();
			}
        }
	};

	private Runnable doLogin;
	private ProgressDialog m_ProgressDialog = null;
	
	private void DoLogin()
	{
		if(ProcessLogin())
		{
			if (m_ProgressDialog != null)
				m_ProgressDialog.dismiss();
			CacheManager.IsClearData = true;
			CacheManager.SummonIssuanceInfo = new PPJSummonIssuanceInfo();
			spinnerZone = (Spinner) findViewById(R.id.spinnerZone);
			CacheManager.officerZone = spinnerZone.getSelectedItem().toString();
			CacheManager.UserId = txtUserName.getText().toString();
			Intent i = new Intent(this,NoticesActivity.class);
			startActivity(i);
			finish();
		}
		else
		{
			if (m_ProgressDialog != null)
				m_ProgressDialog.dismiss();
		}
	}
	
	private boolean ProcessLogin()
	{
		boolean login = false;
		int bResult = DbLocal.DoLogin(txtUserName.getText().toString(), txtPassword.getText().toString(), LoginActivity.this);
		if( bResult == 0)
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Login Gagal. Rekod ID " + txtUserName.getText().toString() + " Tidak Wujud.", 3);
		}
		else if(bResult == 1)
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Login Gagal. Password Tidak Betul.", 3);
		}
		else if(bResult == 2)
		{
			login = true;
		}
		return login;
	}
	
	private boolean ValidateLogin()
	{
		txtUserName=(EditText)findViewById(R.id.etUserName);
		if(txtUserName.getText().toString().isEmpty())
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Sila Masukkan ID Pegawai", 3);
			return false;
		}
		txtPassword = (EditText)findViewById(R.id.etPassword);
		if(txtPassword.getText().toString().isEmpty())
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Sila Masukkan Password", 3);
			return false;
		}
		spinnerZone = (Spinner) findViewById(R.id.spinnerZone);
		if(spinnerZone.getSelectedItemPosition() <= 0)
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Sila Pilih Bhg/Zon/Unit", 3);
			return false;
		}
		
		return true;
	}
	
	public void addListenerOnSpinnerItemSelection() {
		spinnerZone = (Spinner) findViewById(R.id.spinnerZone);
		spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() 
		{
			/**
			 * Called when a new item was selected (in the Spinner)
			 */
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				//BaseEntity g = (BaseEntity) parent.getItemAtPosition(pos);
				//String label = parent.getItemAtPosition(pos).toString();
				//selectOfficerZone = label;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				// Do nothing.
			}
		});
	  }

	public void addItemsOnSpinnerZone() {
		 
		 	spinnerZone = (Spinner) findViewById(R.id.spinnerZone);
		 	 List<String> list = DbLocal.GetListForOfficerZone(this.getApplicationContext());
		 	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		 	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 	dataAdapter.insert("--Sila Pilih--", 0);
		 	//DBKLSpinnerAdapter adapter = new DBKLSpinnerAdapter(DbLocal.GetOfficerZone(this.getApplicationContext()));
		 	spinnerZone.setAdapter(dataAdapter);
		 	spinnerZone.setSelection(0);
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
	public void onBackPressed()
	{
  	  //Intent i = new Intent(LoginActivity.this,MainActivity.class);
  	  //LoginActivity.this.startActivity(i);
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
	    
		txtUserName=(EditText)findViewById(R.id.etUserName);
		txtPassword = (EditText)findViewById(R.id.etPassword);
		spinnerZone = (Spinner) findViewById(R.id.spinnerZone);
		txtUserName.setText("");
		txtPassword.setText("");
		spinnerZone.setSelection(0);
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		super.onResume();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public void onStart()
	{
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		super.onStart();
	}
}
