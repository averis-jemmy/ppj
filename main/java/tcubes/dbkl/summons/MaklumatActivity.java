package tcubes.dbkl.summons;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tcubes.dbkl.summons.R;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
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
import android.text.InputType;
import android.text.TextWatcher;

public class MaklumatActivity extends Activity {

	private Spinner spinnerJenisNotis;
	private Spinner spinnerSamanJenama;
	private Spinner spinnerSamanModel;
	private Spinner spinnerTrafikJenama;
	private Spinner spinnerTrafikModel;
	private Spinner spinnerTrafikJenisBadan;
	private Spinner spinnerAMJenama;
	private Spinner spinnerAMModel;
	private Spinner spinnerAMJenisBadan;
	private static int selectedLayout=0;
	
	public static String strVehicleNo="";
	public static String strCukaiJalan="";
	public static String strNama="";
	public static String strKptNo="";
	public static String strAddress1="";
	public static String strAddress2="";
	public static String strAddress3="";
	public static String strLesenNo="";
	public static String strJenisNotis="";
	public static String strJenama="";
	public static String strModel="";
	public static String strjenisBadan="";
	
	
	private EditText txtTrafikVehicleNo;
	private EditText txtSamanVehicleNo;
	private EditText txtTrafikCukaiJalan;
	private EditText txtAMNama;
	private EditText txtAMNoKPT;
	private EditText txtAMAlamat1;
	private EditText txtAMAlamat2;
	private EditText txtAMAlamat3;
	private EditText txtAMCukaiJalan;
	private EditText txtSamanNama;
	private EditText txtSamanNoKPT;
	private EditText txtSamanAlamat1;
	private EditText txtSamanAlamat2;
	private EditText txtSamanAlamat3;
	private EditText txtSamanNoLesen;
	private EditText txtSamanCukaiJalan;
	
	LinearLayout AMLayout;
	LinearLayout SamanLayout;
	LinearLayout TrafikLayout;
	
	private Button btncapture;
	//DBKLSummonIssuanceInfo summons = new DBKLSummonIssuanceInfo();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maklumat);
		addItemsOnSpinnerJenisNotis();		
		addItemsOnSpinnerSamans();
		addItemsOnSpinnerTrafik();
		addItemsOnSpinnerAM();
		addListenerOnJenisNotisSpinnerItemSelection();
		
		EditText txtNoKP = (EditText) findViewById(R.id.etAMNoKP);
		txtNoKP.setRawInputType(Configuration.KEYBOARD_QWERTY);
		txtNoKP = (EditText) findViewById(R.id.etSamanNoKP);
		txtNoKP.setRawInputType(Configuration.KEYBOARD_QWERTY);
		
		CheckBox chkLicenseExpiryDate = (CheckBox) findViewById(R.id.chkLesenTarikhTamat);
		chkLicenseExpiryDate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
	    		  DBKLDatePickerField LicenseExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanLesenTarikhTamat);
	    		if (isChecked)
	    		{
	    			LicenseExiryDate.setEnabled(true);
	    		}
	    		else
	    		{
	    			LicenseExiryDate.setEnabled(false);
	    		}
	    	  }
	    	});
		CheckBox chkRoadTaxExpiryDate = (CheckBox) findViewById(R.id.chkCukaiTarikhTamat);
		chkRoadTaxExpiryDate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
	    		  DBKLDatePickerField RoadTaxExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanCukaiTarikhTamat);
	    		if (isChecked)
	    		{
	    			RoadTaxExiryDate.setEnabled(true);
	    		}
	    		else
	    		{
	    			RoadTaxExiryDate.setEnabled(false);
	    		}
	    	  }
	    	});
		
		DBKLDatePickerField RoadTaxExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanCukaiTarikhTamat);
		DBKLDatePickerField LicenseExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanLesenTarikhTamat);
		RoadTaxExiryDate.setEnabled(false);
		LicenseExiryDate.setEnabled(false);
		
		ImplementsSpecialTextWatcher(R.id.etSamanNoKenderaan,txtSamanVehicleNo);	
		ImplementsSpecialTextWatcher(R.id.etTrafikNoKenderaan,txtTrafikVehicleNo);
		if(CacheManager.SummonIssuanceInfo == null)
		{
			CacheManager.SummonIssuanceInfo = new DBKLSummonIssuanceInfo();
		}
		btncapture = (Button)findViewById(R.id.btn_continue);
		btncapture.setOnClickListener(btnCaptureListener);
	}
	
	public void EditorActionDone(final int ctrlID, EditText txtCtrlId)
	{
		txtCtrlId = (EditText) findViewById(ctrlID);
		
		txtCtrlId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (actionId == EditorInfo.IME_ACTION_DONE) {

	            	getStringValues(ctrlID);

	                return true;
	            }
	            return false;
	        }
	    });		
	}
	
	public void getStringValues(int ctrlID)
	{		
		String strData;
		EditText txtCtrlId;
		txtCtrlId = (EditText) findViewById(ctrlID);
		strData = txtCtrlId.getText().toString();
		if( (ctrlID == R.id.etSamanNoKenderaan) || (ctrlID == R.id.etTrafikNoKenderaan))
		{
			strVehicleNo = strData;
			CacheManager.SummonIssuanceInfo.noKenderaan = strData;
		}
		else if( (ctrlID == R.id.etAMAlamat1) || (ctrlID == R.id.etSamanAlamat1))
		{
			strAddress1 = strData;
			CacheManager.SummonIssuanceInfo.address1 = strData;
		}
		else if( (ctrlID == R.id.etAMAlamat2) || (ctrlID == R.id.etSamanAlamat2))
		{
			strAddress2 = strData;
			CacheManager.SummonIssuanceInfo.address2 = strData;
		}
		else if( (ctrlID == R.id.etAMAlamat3) || (ctrlID == R.id.etSamanAlamat3))
		{
			strAddress3 = strData;
			CacheManager.SummonIssuanceInfo.address3 = strData;
		}
		else if( (ctrlID == R.id.etAMNoKP) || (ctrlID == R.id.etSamanNoKP))
		{
			strKptNo = strData;
			CacheManager.SummonIssuanceInfo.kptNo = strData;
		}
		else if( (ctrlID == R.id.etAMNama) || (ctrlID == R.id.etSamanNama))
		{
			strNama = strData;
			CacheManager.SummonIssuanceInfo.name = strData;
		}
		else if( (ctrlID == R.id.etAMCukaiJalan) || (ctrlID == R.id.etSamanNoCukaiJalan)|| (ctrlID == R.id.etTrafikNoCukaiJalan))
		{
			strCukaiJalan = strData;
			CacheManager.SummonIssuanceInfo.noCukaiJalan = strData;
		}
		else if(ctrlID == R.id.etSamanNoLesen) 
		{
			strLesenNo = strData;
			CacheManager.SummonIssuanceInfo.LicenseNo = strData;
		}
		 
	}
	public void ImplementsSpecialTextWatcher(int ctrlID, EditText txtCtrlId)
	{	
		txtCtrlId = (EditText) findViewById(ctrlID);		
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
	public void ImplementsTextWatcher(int ctrlID, EditText txtCtrlId)
	{
		txtCtrlId = (EditText) findViewById(ctrlID);
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
            	
            }
        });		
		//SaveData();
	}
	private OnClickListener btnCaptureListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			SaveData(selectedLayout);
			Intent i = new Intent(MaklumatActivity.this, ImageActivity.class);
			startActivity(i);
			finish();
        }
		
	};
	private OnClickListener btnSaveListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			SaveData(selectedLayout);			
        }
		
	};
	public void addListenerOnJenisNotisSpinnerItemSelection() 
	{
		spinnerJenisNotis = (Spinner) findViewById(R.id.spinnerjenisnotis);
		AMLayout = (LinearLayout)findViewById(R.id.AMLayout);
		SamanLayout = (LinearLayout)findViewById(R.id.SamanLayout);
		TrafikLayout= (LinearLayout)findViewById(R.id.TrafikLayout);
		spinnerJenisNotis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {		       
		        int nPos = parent.getSelectedItemPosition();
		        
		        if(CacheManager.IsTypeChanged)
		        {
		        	DBKLSummonIssuanceInfo temp = CacheManager.SummonIssuanceInfo;
					CacheManager.SummonIssuanceInfo = new DBKLSummonIssuanceInfo();
					CacheManager.SummonIssuanceInfo.offenceLocationPos = temp.offenceLocationPos;
					CacheManager.SummonIssuanceInfo.offenceLocationAreaPos = temp.offenceLocationAreaPos;
					CacheManager.IsNewSummonsCamera = true;
					ClearData();
					CacheManager.IsClearKesalahan = true;
		        }
		        else
		        {
		        	CacheManager.IsTypeChanged = true;
		        }
		       setLayout(nPos);
		      
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});		
	}
	public void setLayout(int nLayout)
	{
		selectedLayout = nLayout;
		SamanLayout.setVisibility(View.GONE);
		AMLayout.setVisibility(View.GONE);
		TrafikLayout.setVisibility(View.GONE);
		
		 if (selectedLayout == 1) {
				AMLayout.setVisibility(View.VISIBLE);
			} else if (selectedLayout == 2) {
				SamanLayout.setVisibility(View.VISIBLE);
			} else if (selectedLayout == 0) {
				TrafikLayout.setVisibility(View.VISIBLE);
			}
	}
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
		    			etVehicleMake.setText(CacheManager.SummonIssuanceInfo.vehicleMake);
		    		}
		        
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});		
	}
	
	public void addListenerOnAMJenamaSpinnerItemSelection() 
	{
		spinnerAMJenama = (Spinner) findViewById(R.id.spinnerAMJenama);
		
		spinnerAMJenama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	
		    	EditText etVehicleMake = (EditText) findViewById(R.id.etAMVehicleMake);   
    			etVehicleMake.setVisibility(View.GONE);
    			etVehicleMake.setText("");
    			
		    		AMRefreshData(parent.getSelectedItem().toString());
		    		
		    		if(pos == parent.getCount() - 1)
		    		{
		    			etVehicleMake.setVisibility(View.VISIBLE);
		    			etVehicleMake.setText(CacheManager.SummonIssuanceInfo.vehicleMake);
		    		}
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});		
	}
	
	public void addListenerOnSamanJenamaSpinnerItemSelection() 
	{
		spinnerSamanJenama = (Spinner) findViewById(R.id.spinnerSamanJenama);
		
		spinnerSamanJenama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	
		    	EditText etVehicleMake = (EditText) findViewById(R.id.etSamanVehicleMake);   
    			etVehicleMake.setVisibility(View.GONE);
    			etVehicleMake.setText("");
    			
		    		SamanRefreshData(parent.getSelectedItem().toString());
		    		
		    		if(pos == parent.getCount() - 1)
		    		{
		    			etVehicleMake.setVisibility(View.VISIBLE);
		    			etVehicleMake.setText(CacheManager.SummonIssuanceInfo.vehicleMake);
		    		}
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});		
	}

	public void AMRefreshData(String vehicleMake)
	{
		List<String> list = new ArrayList<String>();
		spinnerAMModel = (Spinner) findViewById(R.id.spinnerAMModel);
		spinnerAMModel.getEmptyView();
		
		list = DbLocal.GetListForVehicleModelSpinner(this.getApplicationContext(),vehicleMake);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	dataAdapter.insert("--Sila Pilih--", 0);
	 	dataAdapter.insert("", dataAdapter.getCount());
		spinnerAMModel.setAdapter(dataAdapter);
		spinnerAMModel.setSelection(CacheManager.SummonIssuanceInfo.modelPos);
		
		spinnerAMModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		    	EditText etVehicleModel = (EditText) findViewById(R.id.etAMVehicleModel);
    			etVehicleModel.setVisibility(View.GONE);
    			etVehicleModel.setText("");
    			if(pos == parent.getCount() - 1)
	    		{
	    			etVehicleModel.setVisibility(View.VISIBLE);
	    			etVehicleModel.setText(CacheManager.SummonIssuanceInfo.vehicleModel);
	    		}
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		//strJenama = vehicleMake;	
		//CacheManager.SummonIssuanceInfo.jenama = vehicleMake;
		//addListenerOnAMModelSpinnerItemSelection();
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
		spinnerTrafikModel.setSelection(CacheManager.SummonIssuanceInfo.modelPos);
		
		spinnerTrafikModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		    	EditText etVehicleModel = (EditText) findViewById(R.id.etTrafikVehicleModel);
    			etVehicleModel.setVisibility(View.GONE);
    			etVehicleModel.setText("");
    			if(pos == parent.getCount() - 1)
	    		{
	    			etVehicleModel.setVisibility(View.VISIBLE);
	    			etVehicleModel.setText(CacheManager.SummonIssuanceInfo.vehicleModel);
	    		}
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		//strJenama = vehicleMake;
		//CacheManager.SummonIssuanceInfo.jenama = vehicleMake;
		//addListenerOnTrafikModelSpinnerItemSelection();
	}
	public void SamanRefreshData(String vehicleMake)
	{
		List<String> list = new ArrayList<String>();
		spinnerSamanModel = (Spinner) findViewById(R.id.spinnerSamanModel);
		spinnerSamanModel.getEmptyView();
		
		list = DbLocal.GetListForVehicleModelSpinner(this.getApplicationContext(),vehicleMake);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	dataAdapter.insert("--Sila Pilih--", 0);
	 	dataAdapter.insert("", dataAdapter.getCount());
		spinnerSamanModel.setAdapter(dataAdapter);
		spinnerSamanModel.setSelection(CacheManager.SummonIssuanceInfo.modelPos);
		
		spinnerSamanModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		    	EditText etVehicleModel = (EditText) findViewById(R.id.etSamanVehicleModel);
    			etVehicleModel.setVisibility(View.GONE);
    			etVehicleModel.setText("");
    			if(pos == parent.getCount() - 1)
	    		{
	    			etVehicleModel.setVisibility(View.VISIBLE);
	    			etVehicleModel.setText(CacheManager.SummonIssuanceInfo.vehicleModel);
	    		}
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		//strJenama = vehicleMake;
		//CacheManager.SummonIssuanceInfo.jenama = vehicleMake;
		//addListenerOnSamanModelSpinnerItemSelection();
	}
	public void addListenerOnAMModelSpinnerItemSelection()
	{
		spinnerAMModel = (Spinner) findViewById(R.id.spinnerAMModel);
	
		spinnerAMModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				//strModel = parent.getSelectedItem().toString();
				//CacheManager.SummonIssuanceInfo.model = parent.getSelectedItem().toString();
				}
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	public void addListenerOnSamanModelSpinnerItemSelection()
	{
		spinnerSamanModel = (Spinner) findViewById(R.id.spinnerSamanModel);
	
		spinnerSamanModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				//strModel = parent.getSelectedItem().toString();
				//CacheManager.SummonIssuanceInfo.model = parent.getSelectedItem().toString();
				}
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	public void addListenerOnTrafikModelSpinnerItemSelection()
	{
		spinnerTrafikModel = (Spinner) findViewById(R.id.spinnerTrafikModel);
	
		spinnerTrafikModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				//strModel = parent.getSelectedItem().toString();
				//CacheManager.SummonIssuanceInfo.model = parent.getSelectedItem().toString();
				}
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}


	public void addItemsOnSpinnerAM() {
		 
		spinnerAMJenama = (Spinner) findViewById(R.id.spinnerAMJenama);
		
		List<String> list = DbLocal.GetListForJenamaSpinner(this.getApplicationContext(),"VEHICLE_MAKE");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	dataAdapter.insert("--Sila Pilih--", 0);
	 	dataAdapter.insert("", dataAdapter.getCount());
		spinnerAMJenama.setAdapter(dataAdapter);
		spinnerAMJenama.setSelection(0);
		
		addListenerOnAMJenamaSpinnerItemSelection();
		
		spinnerAMJenisBadan = (Spinner) findViewById(R.id.spinnerAMJenisBadan);
		list = new ArrayList<String>();
		list = DbLocal.GetListForSpinner(this.getApplicationContext(),"VEHICLE_TYPE");
		
		dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	dataAdapter.insert("--Sila Pilih--", 0);
		spinnerAMJenisBadan.setAdapter(dataAdapter);
		spinnerAMJenisBadan.setSelection(0);		
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

	public void addItemsOnSpinnerSamans() {
		 
		spinnerSamanJenama = (Spinner) findViewById(R.id.spinnerSamanJenama);
		List<String> list = new ArrayList<String>();
		
		list = DbLocal.GetListForJenamaSpinner(this.getApplicationContext(),"VEHICLE_MAKE");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	dataAdapter.insert("--Sila Pilih--", 0);
	 	dataAdapter.insert("", dataAdapter.getCount());
		spinnerSamanJenama.setAdapter(dataAdapter);
		spinnerSamanJenama.setSelection(0);
		addListenerOnSamanJenamaSpinnerItemSelection();
	  }

	public void addItemsOnSpinnerJenisNotis() {
		 
			spinnerJenisNotis = (Spinner) findViewById(R.id.spinnerjenisnotis);
			List<String> list = new ArrayList<String>();
			
			list = DbLocal.GetListForSpinner(this.getApplicationContext(),"OFFENCE_NOTICE_TYPE");
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerJenisNotis.setAdapter(dataAdapter);
			spinnerJenisNotis.setSelection(0);
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
		
		EditText tKptNo = (EditText)findViewById(R.id.etAMNoKP);
		tKptNo.setText("");
		
		EditText tName = (EditText)findViewById(R.id.etAMNama);
		tName.setText("");
		
		EditText tAddress1 = (EditText)findViewById(R.id.etAMAlamat1);
		tAddress1.setText("");
		
		EditText tAddress2 = (EditText)findViewById(R.id.etAMAlamat2);
		tAddress2.setText("");
		
		EditText tAddress3 = (EditText)findViewById(R.id.etAMAlamat3);
		tAddress3.setText("");
		
		EditText tNoCukaiJalan = (EditText)findViewById(R.id.etAMCukaiJalan);
		tNoCukaiJalan.setText("");
		
		sJenama = (Spinner) findViewById(R.id.spinnerAMJenama);				
		sJenama.setSelection(0);
		
		sModel = (Spinner) findViewById(R.id.spinnerAMModel);				
		sModel.setSelection(0);
		
		sJenisBadan= (Spinner) findViewById(R.id.spinnerAMJenisBadan);				
		sJenisBadan.setSelection(0);
		
		tKptNo = (EditText)findViewById(R.id.etSamanNoKP);
		tKptNo.setText("");
		
		tName = (EditText)findViewById(R.id.etSamanNama);
		tName.setText("");
		
		tAddress1 = (EditText)findViewById(R.id.etSamanAlamat1);
		tAddress1.setText("");
		
		tAddress2 = (EditText)findViewById(R.id.etSamanAlamat2);
		tAddress2.setText("");
		
		tAddress3 = (EditText)findViewById(R.id.etSamanAlamat3);
		tAddress3.setText("");
		
		tKenderaan = (EditText)findViewById(R.id.etSamanNoKenderaan);
		tKenderaan.setText("");
		
		EditText tNoLesen = (EditText)findViewById(R.id.etSamanNoLesen);
		tNoLesen.setText("");
		
		tNoCukaiJalan = (EditText)findViewById(R.id.etSamanNoCukaiJalan);
		tNoCukaiJalan.setText("");
		
		sJenama = (Spinner) findViewById(R.id.spinnerSamanJenama);				
		sJenama.setSelection(0);
		
		sModel = (Spinner) findViewById(R.id.spinnerSamanModel);				
		sModel.setSelection(0);
		
		DBKLDatePickerField LicenseExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanLesenTarikhTamat);
		Date Ldate = new Date();				
		LicenseExiryDate.updateDisplay(Ldate.getYear() + 1900, Ldate.getMonth(), Ldate.getDate());
		
		DBKLDatePickerField RoadTaxExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanCukaiTarikhTamat);
		Date Rdate = new Date();
		RoadTaxExiryDate.updateDisplay(Rdate.getYear() + 1900, Rdate.getMonth(), Rdate.getDate());
		
		CheckBox chkLicenseExpiryDate = (CheckBox) findViewById(R.id.chkLesenTarikhTamat);
		CheckBox chkRoadTaxExpiryDate = (CheckBox) findViewById(R.id.chkCukaiTarikhTamat);
		chkLicenseExpiryDate.setChecked(false);
		chkRoadTaxExpiryDate.setChecked(false);
	}
	
	void FillData()
	{
		Spinner sJenisNotis = (Spinner) findViewById(R.id.spinnerjenisnotis);
		sJenisNotis.setSelection(CacheManager.SummonIssuanceInfo.jenisNotisCode);
		selectedLayout = CacheManager.SummonIssuanceInfo.jenisNotisCode;
		CacheManager.IsTypeChanged = false;
		if(selectedLayout == 1)
		{
			EditText tKptNo = (EditText)findViewById(R.id.etAMNoKP);
			tKptNo.setText(CacheManager.SummonIssuanceInfo.kptNo);
			
			EditText tName = (EditText)findViewById(R.id.etAMNama);
			tName.setText(CacheManager.SummonIssuanceInfo.name);
			
			EditText tAddress1 = (EditText)findViewById(R.id.etAMAlamat1);
			tAddress1.setText(CacheManager.SummonIssuanceInfo.address1);
			
			EditText tAddress2 = (EditText)findViewById(R.id.etAMAlamat2);
			tAddress2.setText(CacheManager.SummonIssuanceInfo.address2);
			
			EditText tAddress3 = (EditText)findViewById(R.id.etAMAlamat3);
			tAddress3.setText(CacheManager.SummonIssuanceInfo.address3);
			
			EditText tNoCukaiJalan = (EditText)findViewById(R.id.etAMCukaiJalan);
			tNoCukaiJalan.setText(CacheManager.SummonIssuanceInfo.noCukaiJalan);
			
			Spinner sJenama = (Spinner) findViewById(R.id.spinnerAMJenama);				
			sJenama.setSelection(CacheManager.SummonIssuanceInfo.jenamaPos);
			
			Spinner sModel = (Spinner) findViewById(R.id.spinnerAMModel);				
			sModel.setSelection(CacheManager.SummonIssuanceInfo.modelPos);
			
			Spinner sJenisBadan= (Spinner) findViewById(R.id.spinnerAMJenisBadan);				
			sJenisBadan.setSelection(CacheManager.SummonIssuanceInfo.jenisBadanPos);
		}
		else if( selectedLayout == 0 )
		{
			EditText tKenderaan = (EditText)findViewById(R.id.etTrafikNoKenderaan);
			tKenderaan.setText(CacheManager.SummonIssuanceInfo.noKenderaan);
			
			EditText tTNoCukaiJalan = (EditText)findViewById(R.id.etTrafikNoCukaiJalan);
			tTNoCukaiJalan.setText(CacheManager.SummonIssuanceInfo.noCukaiJalan);
			
			Spinner sJenama = (Spinner) findViewById(R.id.spinnerTrafikJenama);				
			sJenama.setSelection(CacheManager.SummonIssuanceInfo.jenamaPos);
			
			Spinner sModel = (Spinner) findViewById(R.id.spinnerTrafikModel);
			sModel.setSelection(CacheManager.SummonIssuanceInfo.modelPos);
			
			Spinner sJenisBadan= (Spinner) findViewById(R.id.spinnerTrafikJenisBadan);
			sJenisBadan.setSelection(CacheManager.SummonIssuanceInfo.jenisBadanPos);
		}
		else if( selectedLayout == 2 )
		{
			EditText tKptNo = (EditText)findViewById(R.id.etSamanNoKP);
			tKptNo.setText(CacheManager.SummonIssuanceInfo.kptNo );
			
			EditText tName = (EditText)findViewById(R.id.etSamanNama);
			tName.setText(CacheManager.SummonIssuanceInfo.name);
			
			EditText tAddress1 = (EditText)findViewById(R.id.etSamanAlamat1);
			tAddress1.setText(CacheManager.SummonIssuanceInfo.address1 );
			
			EditText tAddress2 = (EditText)findViewById(R.id.etSamanAlamat2);
			tAddress2.setText(CacheManager.SummonIssuanceInfo.address2 );
			
			EditText tAddress3 = (EditText)findViewById(R.id.etSamanAlamat3);
			tAddress3.setText(CacheManager.SummonIssuanceInfo.address3 );
			
			EditText tKenderaan = (EditText)findViewById(R.id.etSamanNoKenderaan);
			tKenderaan.setText(CacheManager.SummonIssuanceInfo.noKenderaan);
			
			EditText tNoLesen = (EditText)findViewById(R.id.etSamanNoLesen);
			tNoLesen.setText(CacheManager.SummonIssuanceInfo.LicenseNo);
			
			EditText tNoCukaiJalan = (EditText)findViewById(R.id.etSamanNoCukaiJalan);
			tNoCukaiJalan.setText(CacheManager.SummonIssuanceInfo.noCukaiJalan);
			
			Spinner sJenama = (Spinner) findViewById(R.id.spinnerSamanJenama);				
			sJenama.setSelection(CacheManager.SummonIssuanceInfo.jenamaPos);
			
			Spinner sModel = (Spinner) findViewById(R.id.spinnerSamanModel);				
			sModel.setSelection(CacheManager.SummonIssuanceInfo.modelPos);
			
			CheckBox chkLicenseExpiryDate = (CheckBox) findViewById(R.id.chkLesenTarikhTamat);
			CheckBox chkRoadTaxExpiryDate = (CheckBox) findViewById(R.id.chkCukaiTarikhTamat);
			chkLicenseExpiryDate.setChecked(CacheManager.isCheckedLicenseExpiryDate);
			chkRoadTaxExpiryDate.setChecked(CacheManager.isCheckedRoadTaxExpiryDate);
			
			DBKLDatePickerField LicenseExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanLesenTarikhTamat);
			Date Ldate = CacheManager.SummonIssuanceInfo.licenseExpiryDate;				
			LicenseExiryDate.updateDisplay(Ldate.getYear() + 1900, Ldate.getMonth(), Ldate.getDate());
			
			DBKLDatePickerField RoadTaxExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanCukaiTarikhTamat);
			Date Rdate = CacheManager.SummonIssuanceInfo.roadtaxExpiryDate;
			RoadTaxExiryDate.updateDisplay(Rdate.getYear() + 1900, Rdate.getMonth(), Rdate.getDate());				
		}
	}
	
	void SaveData(int nLayout)
	{		
		Spinner sJenisNotis = (Spinner) findViewById(R.id.spinnerjenisnotis);
		CacheManager.SummonIssuanceInfo.jenisNotis = sJenisNotis.getSelectedItem().toString();
		CacheManager.SummonIssuanceInfo.jenisNotisCode = sJenisNotis.getSelectedItemPosition();
		if(selectedLayout == 1 )
		{
			
			EditText tKptNo = (EditText)findViewById(R.id.etAMNoKP);
			CacheManager.SummonIssuanceInfo.kptNo = tKptNo.getText().toString();
			
			EditText tName = (EditText)findViewById(R.id.etAMNama);
			CacheManager.SummonIssuanceInfo.name = tName.getText().toString();
			
			EditText tAddress1 = (EditText)findViewById(R.id.etAMAlamat1);
			CacheManager.SummonIssuanceInfo.address1 = tAddress1.getText().toString();
			
			EditText tAddress2 = (EditText)findViewById(R.id.etAMAlamat2);
			CacheManager.SummonIssuanceInfo.address2 = tAddress2.getText().toString();
			
			EditText tAddress3 = (EditText)findViewById(R.id.etAMAlamat3);
			CacheManager.SummonIssuanceInfo.address3 = tAddress3.getText().toString();
			
			EditText tNoCukaiJalan = (EditText)findViewById(R.id.etAMCukaiJalan);
			CacheManager.SummonIssuanceInfo.noCukaiJalan = tNoCukaiJalan.getText().toString();
			
			EditText tVehicleMake = (EditText)findViewById(R.id.etAMVehicleMake);
			CacheManager.SummonIssuanceInfo.vehicleMake = tVehicleMake.getText().toString();
			
			EditText tVehicleModel = (EditText)findViewById(R.id.etAMVehicleModel);
			CacheManager.SummonIssuanceInfo.vehicleModel = tVehicleModel.getText().toString();
			
			Spinner sJenama = (Spinner) findViewById(R.id.spinnerAMJenama);
			CacheManager.SummonIssuanceInfo.jenamaPos = sJenama.getSelectedItemPosition();
			if(sJenama.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.jenama = sJenama.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.jenama = "";
			}
			
			Spinner sModel = (Spinner) findViewById(R.id.spinnerAMModel);
			CacheManager.SummonIssuanceInfo.modelPos = sModel.getSelectedItemPosition();
			if(sModel.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.model = sModel.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.model = "";
			}
			
			Spinner sJenisBadan= (Spinner) findViewById(R.id.spinnerAMJenisBadan);
			CacheManager.SummonIssuanceInfo.jenisBadanPos = sJenisBadan.getSelectedItemPosition();
			if(sJenisBadan.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.jenisBadan = sJenisBadan.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.jenisBadan = "";
			}
		}
		else if( selectedLayout == 0)
		{
			EditText tKenderaan = (EditText)findViewById(R.id.etTrafikNoKenderaan);
			CacheManager.SummonIssuanceInfo.noKenderaan = tKenderaan.getText().toString();
			
			EditText tTNoCukaiJalan = (EditText)findViewById(R.id.etTrafikNoCukaiJalan);
			CacheManager.SummonIssuanceInfo.noCukaiJalan = tTNoCukaiJalan.getText().toString();
			
			EditText tVehicleMake = (EditText)findViewById(R.id.etTrafikVehicleMake);
			CacheManager.SummonIssuanceInfo.vehicleMake = tVehicleMake.getText().toString();
			
			EditText tVehicleModel = (EditText)findViewById(R.id.etTrafikVehicleModel);
			CacheManager.SummonIssuanceInfo.vehicleModel = tVehicleModel.getText().toString();
			
			Spinner sJenama = (Spinner) findViewById(R.id.spinnerTrafikJenama);
			CacheManager.SummonIssuanceInfo.jenamaPos = sJenama.getSelectedItemPosition();
			if(sJenama.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.jenama = sJenama.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.jenama = "";
			}
			
			Spinner sModel = (Spinner) findViewById(R.id.spinnerTrafikModel);
			CacheManager.SummonIssuanceInfo.modelPos = sModel.getSelectedItemPosition();
			if(sModel.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.model = sModel.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.model = "";
			}
			
			Spinner sJenisBadan= (Spinner) findViewById(R.id.spinnerTrafikJenisBadan);
			CacheManager.SummonIssuanceInfo.jenisBadanPos = sJenisBadan.getSelectedItemPosition();
			if(sJenisBadan.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.jenisBadan = sJenisBadan.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.jenisBadan = "";
			}
		}
		else if( selectedLayout == 2 )
		{
			EditText tKptNo = (EditText)findViewById(R.id.etSamanNoKP);
			CacheManager.SummonIssuanceInfo.kptNo = tKptNo.getText().toString();
			
			EditText tName = (EditText)findViewById(R.id.etSamanNama);
			CacheManager.SummonIssuanceInfo.name = tName.getText().toString();
			
			EditText tAddress1 = (EditText)findViewById(R.id.etSamanAlamat1);
			CacheManager.SummonIssuanceInfo.address1 = tAddress1.getText().toString();
			
			EditText tAddress2 = (EditText)findViewById(R.id.etSamanAlamat2);
			CacheManager.SummonIssuanceInfo.address2 = tAddress2.getText().toString();
			
			EditText tAddress3 = (EditText)findViewById(R.id.etSamanAlamat3);
			CacheManager.SummonIssuanceInfo.address3 = tAddress3.getText().toString();
			
			EditText tKenderaan = (EditText)findViewById(R.id.etSamanNoKenderaan);
			CacheManager.SummonIssuanceInfo.noKenderaan = tKenderaan.getText().toString();
			
			EditText tNoLesen = (EditText)findViewById(R.id.etSamanNoLesen);
			CacheManager.SummonIssuanceInfo.LicenseNo = tNoLesen.getText().toString();
			
			EditText tNoCukaiJalan = (EditText)findViewById(R.id.etSamanNoCukaiJalan);
			CacheManager.SummonIssuanceInfo.noCukaiJalan = tNoCukaiJalan.getText().toString();
			
			EditText tVehicleMake = (EditText)findViewById(R.id.etSamanVehicleMake);
			CacheManager.SummonIssuanceInfo.vehicleMake = tVehicleMake.getText().toString();
			
			EditText tVehicleModel = (EditText)findViewById(R.id.etSamanVehicleModel);
			CacheManager.SummonIssuanceInfo.vehicleModel = tVehicleModel.getText().toString();
			
			Spinner sJenama = (Spinner) findViewById(R.id.spinnerSamanJenama);
			CacheManager.SummonIssuanceInfo.jenamaPos = sJenama.getSelectedItemPosition();
			if(sJenama.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.jenama = sJenama.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.jenama = "";
			}
			
			Spinner sModel = (Spinner) findViewById(R.id.spinnerSamanModel);
			CacheManager.SummonIssuanceInfo.modelPos = sModel.getSelectedItemPosition();
			if(sModel.getSelectedItemPosition() > 0)
			{
				CacheManager.SummonIssuanceInfo.model = sModel.getSelectedItem().toString();
			}
			else
			{
				CacheManager.SummonIssuanceInfo.model = "";
			}
			
			CheckBox chkLicenseExpiryDate = (CheckBox) findViewById(R.id.chkLesenTarikhTamat);
			CheckBox chkRoadTaxExpiryDate = (CheckBox) findViewById(R.id.chkCukaiTarikhTamat);
			CacheManager.isCheckedLicenseExpiryDate = chkLicenseExpiryDate.isChecked();
			CacheManager.isCheckedRoadTaxExpiryDate = chkRoadTaxExpiryDate.isChecked();
			
			DBKLDatePickerField LicenseExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanLesenTarikhTamat);
			Date Ldate = new Date(LicenseExiryDate.mYear - 1900, LicenseExiryDate.mMonth, LicenseExiryDate.mDay);
			CacheManager.SummonIssuanceInfo.licenseExpiryDate = Ldate;
			
			DBKLDatePickerField RoadTaxExiryDate = (DBKLDatePickerField) findViewById(R.id.txtDatePicker_SamanCukaiTarikhTamat);
			Date Rdate = new Date(RoadTaxExiryDate.mYear - 1900, RoadTaxExiryDate.mMonth, RoadTaxExiryDate.mDay);
			CacheManager.SummonIssuanceInfo.roadtaxExpiryDate = Rdate;
		}
		
	}
	@Override
	public void onPause()
	{
		SaveData(selectedLayout);
		super.onPause();
	}
	
	@Override
	public void onStart()
	{
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		if(!CacheManager.IsClearData)
		{
			FillData();
		}
		else
		{
			DBKLSummonIssuanceInfo temp = CacheManager.SummonIssuanceInfo;
			CacheManager.SummonIssuanceInfo = new DBKLSummonIssuanceInfo();
			CacheManager.SummonIssuanceInfo.offenceLocationPos = temp.offenceLocationPos;
			CacheManager.SummonIssuanceInfo.offenceLocationAreaPos = temp.offenceLocationAreaPos;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_maklumat, menu);
		return true;
	}

}
