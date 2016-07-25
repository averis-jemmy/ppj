package id.zenmorf.com.ppjhandheld;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.text.Editable;
import android.text.TextWatcher;

public class MaklumatActivity extends Activity {
	private Spinner spinnerTrafikJenama;
	private Spinner spinnerTrafikModel;
	private Spinner spinnerTrafikJenisBadan;
	
	public static String strVehicleNo="";
	public static String strCukaiJalan="";
	
	private Button btncapture;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maklumat);
		addItemsOnSpinnerTrafik();

		ImplementsSpecialTextWatcher(R.id.etTrafikNoKenderaan);

		if(CacheManager.SummonIssuanceInfo == null)
		{
			CacheManager.SummonIssuanceInfo = new PPJSummonIssuanceInfo();
		}

		btncapture = (Button)findViewById(R.id.btn_camera);
		btncapture.setOnClickListener(btnCaptureListener);
	}

	public void ImplementsSpecialTextWatcher(int ctrlID)
	{
		EditText txtCtrlId = (EditText) findViewById(ctrlID);
		txtCtrlId.addTextChangedListener(new TextWatcher() {
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
				if (filtered_str.matches(".*[^A-Z^0-9].*")) {
					filtered_str = filtered_str.replaceAll("[^A-Z^0-9]", "");
					s.clear();
					s.insert(0, filtered_str);
				}
			}
		});
	}

	private OnClickListener btnCaptureListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			SaveData();
			//Intent i = new Intent(MaklumatActivity.this, ImageActivity.class);
			//startActivity(i);
			//finish();
			}

		};

	public void addListenerOnJenamaSpinnerItemSelection() 
	{
		spinnerTrafikJenama = (Spinner) findViewById(R.id.spinnerTrafikJenama);
		
		spinnerTrafikJenama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

    			EditText etVehicleMake = (EditText) findViewById(R.id.etTrafikVehicleMake);   
    			etVehicleMake.setVisibility(View.GONE);
    			etVehicleMake.setText("");
    			
		    		RefreshData(parent.getSelectedItem().toString());

		    		if(pos == parent.getCount() - 1)
		    		{
		    			etVehicleMake.setVisibility(View.VISIBLE);
		    			etVehicleMake.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleMake);
		    		}
		        
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});		
	}

	public void RefreshData(String vehicleMake)
	{
		List<String> list = new ArrayList<String>();
		spinnerTrafikModel = (Spinner) findViewById(R.id.spinnerTrafikModel);
		spinnerTrafikModel.getEmptyView();
		
		list = DbLocal.GetListForVehicleModelSpinner(this.getApplicationContext(),vehicleMake);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	dataAdapter.insert("--Sila Pilih--", 0);
	 	dataAdapter.insert("", dataAdapter.getCount());
		spinnerTrafikModel.setAdapter(dataAdapter);
		spinnerTrafikModel.setSelection(CacheManager.SummonIssuanceInfo.VehicleModelPos);
		
		spinnerTrafikModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				EditText etVehicleModel = (EditText) findViewById(R.id.etTrafikVehicleModel);
				etVehicleModel.setVisibility(View.GONE);
				etVehicleModel.setText("");
				if (pos == parent.getCount() - 1) {
					etVehicleModel.setVisibility(View.VISIBLE);
					etVehicleModel.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleModel);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	public void addItemsOnSpinnerTrafik() {
		spinnerTrafikJenama = (Spinner) findViewById(R.id.spinnerTrafikJenama);
		
		List<String> list = DbLocal.GetListForJenamaSpinner(this.getApplicationContext(),"VEHICLE_MAKE");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	dataAdapter.insert("--Sila Pilih--", 0);
	 	dataAdapter.insert("", dataAdapter.getCount());
		spinnerTrafikJenama.setAdapter(dataAdapter);
		spinnerTrafikJenama.setSelection(0);
		addListenerOnJenamaSpinnerItemSelection();
		
		
		spinnerTrafikJenisBadan = (Spinner) findViewById(R.id.spinnerTrafikJenisBadan);
		list = new ArrayList<String>();
		list = DbLocal.GetListForSpinner(this.getApplicationContext(),"VEHICLE_TYPE");
		
		dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	dataAdapter.insert("--Sila Pilih--", 0);
		spinnerTrafikJenisBadan.setAdapter(dataAdapter);
		spinnerTrafikJenisBadan.setSelection(0);
	  }
	
	@Override
	protected void onDestroy()
	{
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
	
	void ClearData()
	{		
		EditText tKenderaan = (EditText)findViewById(R.id.etTrafikNoKenderaan);
		tKenderaan.setText("");
		
		EditText tTNoCukaiJalan = (EditText)findViewById(R.id.etTrafikNoCukaiJalan);
		tTNoCukaiJalan.setText("");
		
		Spinner sJenama = (Spinner) findViewById(R.id.spinnerTrafikJenama);
		sJenama.setSelection(0);
		
		Spinner sModel = (Spinner) findViewById(R.id.spinnerTrafikModel);
		sModel.setSelection(0);
		
		Spinner sJenisBadan= (Spinner) findViewById(R.id.spinnerTrafikJenisBadan);
		sJenisBadan.setSelection(0);
	}

	void FillData()
	{
		EditText tKenderaan = (EditText)findViewById(R.id.etTrafikNoKenderaan);
			tKenderaan.setText(CacheManager.SummonIssuanceInfo.VehicleNo);

			EditText tTNoCukaiJalan = (EditText)findViewById(R.id.etTrafikNoCukaiJalan);
			tTNoCukaiJalan.setText(CacheManager.SummonIssuanceInfo.RoadTaxNo);

			Spinner sJenama = (Spinner) findViewById(R.id.spinnerTrafikJenama);
			sJenama.setSelection(CacheManager.SummonIssuanceInfo.VehicleMakePos);

			Spinner sModel = (Spinner) findViewById(R.id.spinnerTrafikModel);
			sModel.setSelection(CacheManager.SummonIssuanceInfo.VehicleModelPos);

			Spinner sJenisBadan= (Spinner) findViewById(R.id.spinnerTrafikJenisBadan);
			sJenisBadan.setSelection(CacheManager.SummonIssuanceInfo.VehicleTypePos);
	}

	void SaveData()
	{
		EditText tKenderaan = (EditText)findViewById(R.id.etTrafikNoKenderaan);
			CacheManager.SummonIssuanceInfo.VehicleNo = tKenderaan.getText().toString();

			EditText tTNoCukaiJalan = (EditText)findViewById(R.id.etTrafikNoCukaiJalan);
			CacheManager.SummonIssuanceInfo.RoadTaxNo = tTNoCukaiJalan.getText().toString();

			EditText tVehicleMake = (EditText)findViewById(R.id.etTrafikVehicleMake);
			CacheManager.SummonIssuanceInfo.SelectedVehicleMake = tVehicleMake.getText().toString();

			EditText tVehicleModel = (EditText)findViewById(R.id.etTrafikVehicleModel);
			CacheManager.SummonIssuanceInfo.SelectedVehicleModel = tVehicleModel.getText().toString();

			Spinner sJenama = (Spinner) findViewById(R.id.spinnerTrafikJenama);
			CacheManager.SummonIssuanceInfo.VehicleMakePos = sJenama.getSelectedItemPosition();
			if(sJenama.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.VehicleMake = sJenama.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.VehicleMake = "";
			}

			Spinner sModel = (Spinner) findViewById(R.id.spinnerTrafikModel);
			CacheManager.SummonIssuanceInfo.VehicleModelPos = sModel.getSelectedItemPosition();
			if(sModel.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.VehicleModel = sModel.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.VehicleModel = "";
			}

			Spinner sJenisBadan= (Spinner) findViewById(R.id.spinnerTrafikJenisBadan);
			CacheManager.SummonIssuanceInfo.VehicleTypePos = sJenisBadan.getSelectedItemPosition();
			if(sJenisBadan.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.VehicleType = sJenisBadan.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.VehicleType = "";
			}
	}


	@Override
	public void onPause()
	{
		SaveData();
		super.onPause();
	}
	
	@Override
	public void onStart()
	{
		if(!CacheManager.IsClearData)
		{
			FillData();
		}
		else
		{
			PPJSummonIssuanceInfo temp = CacheManager.SummonIssuanceInfo;
			CacheManager.SummonIssuanceInfo = new PPJSummonIssuanceInfo();
			CacheManager.SummonIssuanceInfo.OffenceLocationPos = temp.OffenceLocationPos;
			CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos = temp.OffenceLocationAreaPos;
			CacheManager.IsNewSummonsCamera = true;
			CacheManager.IsClearData = false;
		}
		
		super.onStart();
	}
	@Override
	public void onBackPressed()
	{
		//Intent i = new Intent(NoticesActivity.this,LoginActivity.class);
		//NoticesActivity.this.startActivity(i);
		//return;
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

}
