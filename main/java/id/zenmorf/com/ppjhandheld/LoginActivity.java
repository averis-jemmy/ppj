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
	private EditText txtUserName;
	private EditText txtPassword;
	private Button btnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);

		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(btnloginListener);
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
			CacheManager.UserId = txtUserName.getText().toString();
			Intent i = new Intent(this, NoticeIssuanceActivity.class);
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
		
		return true;
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

	@Override
	public void onResume()
	{
		txtUserName=(EditText)findViewById(R.id.etUserName);
		txtPassword = (EditText)findViewById(R.id.etPassword);
		txtUserName.setText("");
		txtPassword.setText("");
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
		super.onStart();
	}
}
