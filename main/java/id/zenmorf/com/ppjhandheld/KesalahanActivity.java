package id.zenmorf.com.ppjhandheld;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class KesalahanActivity extends Activity {

	private Spinner spinnerOffenceAct;
	private Spinner spinnerOffenceSection;
	private Spinner spinnerOffenceLocation;
	private Spinner spinnerOffenceLocationArea;
	private EditText etKesalahan;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kesalahan);
		etKesalahan = (EditText) findViewById(R.id.etKesalahan);
		etKesalahan.setEnabled(false);
		addItemsOnSpinnerOffenceActAndLocation();
		SetLayout();
	}

	public void addItemsOnSpinnerOffenceActAndLocation() {
		 
		spinnerOffenceAct = (Spinner) findViewById(R.id.spinnerundangundang);
		
		List<String> list = new ArrayList<String>();		
		list = DbLocal.GetOneFieldListForSpinner(this.getApplicationContext(),"SHORT_DESCRIPTION","OFFENCE_ACT");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataAdapter.insert("--Sila Pilih--", 0);
		spinnerOffenceAct.setAdapter(dataAdapter);
		spinnerOffenceAct.setSelection(1);
		
		spinnerOffenceLocation = (Spinner) findViewById(R.id.spinnerKawasan);
		
		List<String> list1 = new ArrayList<String>();		
		list1 = DbLocal.GetOneFieldListForSpinner(this.getApplicationContext(),"DESCRIPTION","OFFENCE_LOCATION_AREA");
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list1);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataAdapter1.insert("--Sila Pilih--", 0);
		spinnerOffenceLocation.setAdapter(dataAdapter1);
		spinnerOffenceLocation.setSelection(1);
		addListenerOnOffenceActSpinnerItemSelection();
		addListenerOnOffenceSectionSpinnerItemSelection();
		addListenerOnOffenceLocationSpinnerItemSelection();
	  }

	public void addListenerOnOffenceLocationSpinnerItemSelection() 
	{
		spinnerOffenceLocation = (Spinner) findViewById(R.id.spinnerKawasan);
		
		spinnerOffenceLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	
		    		RefreshLocationData(parent.getSelectedItem().toString());
		        
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});		
	}

	public void RefreshLocationData(String strLocation)
	{
		List<String> list = new ArrayList<String>();
		spinnerOffenceLocationArea = (Spinner) findViewById(R.id.spinnerTempatJalan);
		spinnerOffenceLocationArea.getEmptyView();
		
		list = DbLocal.GetListForOffenceLocationAreaSpinner(this.getApplicationContext(),strLocation);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	dataAdapter.insert("--Sila Pilih--", 0);
	 	dataAdapter.insert("", dataAdapter.getCount());
		spinnerOffenceLocationArea.setAdapter(dataAdapter);
		spinnerOffenceLocationArea.setSelection(CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos);
		
		spinnerOffenceLocationArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		    	EditText etSummonLocation = (EditText) findViewById(R.id.etSummonLocation);
		    	etSummonLocation.setVisibility(View.GONE);
		    	etSummonLocation.setText("");
    			if(pos == parent.getCount() - 1)
	    		{
    				etSummonLocation.setVisibility(View.VISIBLE);
    				etSummonLocation.setText(CacheManager.SummonIssuanceInfo.SummonLocation);
	    		}
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
	}
	public void addListenerOnOffenceActSpinnerItemSelection() 
	{
		spinnerOffenceAct = (Spinner) findViewById(R.id.spinnerundangundang);
		
		spinnerOffenceAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	
		    		RefreshData(parent.getSelectedItem().toString());
		        
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});		
	}
	public void addListenerOnOffenceSectionSpinnerItemSelection() 
	{
		spinnerOffenceSection = (Spinner) findViewById(R.id.spinnerSeksyenKaedah);
		
		spinnerOffenceSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	
		    	if(pos>0)
		    	{
		    		spinnerOffenceAct = (Spinner) findViewById(R.id.spinnerundangundang);
		    		SectionRefreshData(parent.getSelectedItem().toString(), spinnerOffenceAct.getSelectedItem().toString());
		    	}
		    	else
		    	{
		    		etKesalahan = (EditText) findViewById(R.id.etKesalahan);
		    		etKesalahan.setText("");
		    	}
		        
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});		
	}
	public void SectionRefreshData(String offenceSectionCode, String offenceActDescription)
	{
		etKesalahan = (EditText) findViewById(R.id.etKesalahan);
		Cursor cur = DbLocal.GetListForOffenceSectionCodeSpinner(this.getApplicationContext(),offenceSectionCode, offenceActDescription);
		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{	
				etKesalahan.setText(cur.getString(2));
            } while (cur.moveToNext());
			cur.close();
        }
	}
	public List<String>PopulateOffenceSection(String offenceShortDescription)
	{		
		List<String> list = new ArrayList<String>();
		Cursor cur = DbLocal.GetListForOffenceSectionSpinner(this.getApplicationContext(),offenceShortDescription);
		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(2) + " " +cur.getString(3) );
            } while (cur.moveToNext());
			cur.close();
        }
		return list;
	}
	public void RefreshData(String offenceShortDescription)
	{
		List<String> list = new ArrayList<String>();
		spinnerOffenceSection = (Spinner) findViewById(R.id.spinnerSeksyenKaedah);
		spinnerOffenceSection.getEmptyView();
		
		list = PopulateOffenceSection(offenceShortDescription);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataAdapter.insert("--Sila Pilih--", 0);
		spinnerOffenceSection.setAdapter(dataAdapter);
		spinnerOffenceSection.setSelection(CacheManager.SummonIssuanceInfo.OffenceSectionPos);
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
	}
	
	public void SectionUpdate(String offenceSectionCode, String offenceActDescription)
	{
		etKesalahan = (EditText) findViewById(R.id.etKesalahan);
		Cursor cur = DbLocal.GetListForOffenceSectionCodeSpinner(this.getApplicationContext(),offenceSectionCode, offenceActDescription);
		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{
				CacheManager.SummonIssuanceInfo.OffenceActCode = cur.getString(0);
				CacheManager.SummonIssuanceInfo.OffenceSectionCode = cur.getString(1);
            } while (cur.moveToNext());
			cur.close();
        }
	}
	public void SaveData()
	{		
		Spinner sOffenceAct = (Spinner) findViewById(R.id.spinnerundangundang);
		CacheManager.SummonIssuanceInfo.OffenceActPos = sOffenceAct.getSelectedItemPosition();
		if(sOffenceAct.getSelectedItemPosition() > 0)
		{
			CacheManager.SummonIssuanceInfo.OffenceAct = sOffenceAct.getSelectedItem().toString();
		}
		else
		{
			CacheManager.SummonIssuanceInfo.OffenceAct = "";
		}
		
		Spinner sOffenceSection = (Spinner) findViewById(R.id.spinnerSeksyenKaedah);
		CacheManager.SummonIssuanceInfo.OffenceSectionPos = sOffenceSection.getSelectedItemPosition();
		if(sOffenceSection.getSelectedItemPosition() > 0)
		{
			CacheManager.SummonIssuanceInfo.OffenceSection = sOffenceSection.getSelectedItem().toString();
			SectionUpdate(sOffenceSection.getSelectedItem().toString(), sOffenceAct.getSelectedItem().toString());
		}
		else
		{
			CacheManager.SummonIssuanceInfo.OffenceSection = "";
			CacheManager.SummonIssuanceInfo.OffenceActCode = "";
			CacheManager.SummonIssuanceInfo.OffenceSectionCode = "";
		}
		
		EditText etSummonLocation = (EditText) findViewById(R.id.etSummonLocation);
		CacheManager.SummonIssuanceInfo.SummonLocation = etSummonLocation.getText().toString();
		
		EditText tKesalahan = (EditText)findViewById(R.id.etKesalahan);
		CacheManager.SummonIssuanceInfo.Offence = tKesalahan.getText().toString();
		
		EditText tButir = (EditText)findViewById(R.id.etButirButir);
		CacheManager.SummonIssuanceInfo.OffenceDetails = tButir.getText().toString();
		
		Spinner sKawasan = (Spinner) findViewById(R.id.spinnerKawasan);
		CacheManager.SummonIssuanceInfo.OffenceLocationPos = sKawasan.getSelectedItemPosition();
		if(sKawasan.getSelectedItemPosition() > 0)
		{
			CacheManager.SummonIssuanceInfo.OffenceLocation = sKawasan.getSelectedItem().toString();
		}
		else
		{
			CacheManager.SummonIssuanceInfo.OffenceLocation = "";
		}
		
		Spinner sTempatJalan = (Spinner) findViewById(R.id.spinnerTempatJalan);
		CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos = sTempatJalan.getSelectedItemPosition();
		if(sTempatJalan.getSelectedItemPosition() > 0)
		{
			CacheManager.SummonIssuanceInfo.OffenceLocationArea = sTempatJalan.getSelectedItem().toString();
		}
		else
		{
			CacheManager.SummonIssuanceInfo.OffenceLocationArea = "";
		}
		
		EditText tButirLokasi = (EditText)findViewById(R.id.etButiranLokasi);
		CacheManager.SummonIssuanceInfo.OffenceLocationDetails = tButirLokasi.getText().toString();

		CacheManager.SummonIssuanceInfo.Advertisement = DbLocal.GetAdvertisement(CacheManager.context);
		String delegate = "yy";         
		String year = (String) DateFormat.format(delegate,Calendar.getInstance().getTime()); 
		
		CacheManager.SummonIssuanceInfo.NoticeSerialNo = SettingsHelper.DeviceID +  year + SettingsHelper.DeviceSerialNumber;
		CacheManager.SummonIssuanceInfo.OfficerZone = CacheManager.officerZone;
		
		EditText tNoPetakTiang = (EditText)findViewById(R.id.etNoPetakTiang);
		CacheManager.SummonIssuanceInfo.PostNo = tNoPetakTiang.getText().toString();
		
		if(CacheManager.SummonIssuanceInfo.VehicleType.length() != 0)
			{
				CacheManager.SummonIssuanceInfo.CompoundAmount1 = DbLocal.GetCompundAmountFromVehicleType(CacheManager.context, CacheManager.SummonIssuanceInfo.VehicleType);
			}
		
		if(CacheManager.SummonIssuanceInfo.OffenceSectionCode.length() != 0)
		{
			Cursor compoundList = DbLocal.GetCompundAmountDescription(CacheManager.context, CacheManager.SummonIssuanceInfo.OffenceSectionCode, CacheManager.SummonIssuanceInfo.OffenceActCode);
			try
			{
				if(compoundList != null)
				{
					if(Float.parseFloat(compoundList.getString(1)) != 0)
					{
						CacheManager.SummonIssuanceInfo.CompoundAmount1 = Float.parseFloat(compoundList.getString(1));
						CacheManager.SummonIssuanceInfo.CompoundAmountDesc1 = compoundList.getString(2);
						if(compoundList.getString(2).length() != 0)
						{
							if (compoundList.getString(5).length() != 0)
			                {
								CacheManager.SummonIssuanceInfo.CompoundAmount2 = Float.parseFloat(compoundList.getString(4));
			                    CacheManager.SummonIssuanceInfo.CompoundAmountDesc2 = compoundList.getString(5);
			                }
			                if (compoundList.getString(8).length() != 0)
			                {
			                	CacheManager.SummonIssuanceInfo.CompoundAmount3 = Float.parseFloat(compoundList.getString(7));
			                    CacheManager.SummonIssuanceInfo.CompoundAmountDesc3 = compoundList.getString(8);
			                }
			                if (compoundList.getString(11).length() != 0)
			                {
			                	CacheManager.SummonIssuanceInfo.CompoundAmount4 = Float.parseFloat(compoundList.getString(10));
			                    CacheManager.SummonIssuanceInfo.CompoundAmountDesc4 = compoundList.getString(11);
			                }
			                if (compoundList.getString(14).length() != 0)
			                {
			                	CacheManager.SummonIssuanceInfo.CompoundAmount5 = Float.parseFloat(compoundList.getString(13));
			                    CacheManager.SummonIssuanceInfo.CompoundAmountDesc5 = compoundList.getString(14);
			                }
						}
					}
				}
			}
			catch(Exception ex)
			{
				
			}
			
			CacheManager.SummonIssuanceInfo.CompoundAmountDescription = CacheManager.GenerateCompoundAmountDescription(String.valueOf(CacheManager.SummonIssuanceInfo.CompoundAmount1));
		}
		
		CacheManager.SummonIssuanceInfo.CompoundDate = CacheManager.GetCompoundDate();
	}
	public void ClearData()
	{		
		Spinner sOffenceAct = (Spinner) findViewById(R.id.spinnerundangundang);
		sOffenceAct.setSelection(0);
		
		Spinner sOffenceSection = (Spinner) findViewById(R.id.spinnerSeksyenKaedah);
		sOffenceSection.setSelection(0);
		
		EditText tButir = (EditText)findViewById(R.id.etButirButir);
		tButir.setText("");
		
		Spinner sKawasan = (Spinner) findViewById(R.id.spinnerKawasan);
		sKawasan.setSelection(0);
		
		Spinner sTempatJalan = (Spinner) findViewById(R.id.spinnerTempatJalan);
		sTempatJalan.setSelection(0);
		
		EditText tButirLokasi = (EditText)findViewById(R.id.etButiranLokasi);
		tButirLokasi.setText("");
		
		EditText tNoPetakTiang = (EditText)findViewById(R.id.etNoPetakTiang);
		tNoPetakTiang.setText("");
	}
	public void FillData()
	{		
		if(!CacheManager.IsClearKesalahan)
		{
			Spinner sOffenceAct = (Spinner) findViewById(R.id.spinnerundangundang);
			sOffenceAct.setSelection(CacheManager.SummonIssuanceInfo.OffenceActPos);
			
			Spinner sOffenceSection = (Spinner) findViewById(R.id.spinnerSeksyenKaedah);
			sOffenceSection.setSelection(CacheManager.SummonIssuanceInfo.OffenceSectionPos);
			
			EditText tButir = (EditText)findViewById(R.id.etButirButir);
			tButir.setText(CacheManager.SummonIssuanceInfo.OffenceDetails);
			
			EditText tButirLokasi = (EditText)findViewById(R.id.etButiranLokasi);
			tButirLokasi.setText(CacheManager.SummonIssuanceInfo.OffenceLocationDetails);
			
			EditText tNoPetakTiang = (EditText)findViewById(R.id.etNoPetakTiang);
			tNoPetakTiang.setText(CacheManager.SummonIssuanceInfo.PostNo);
		}
		
		Spinner sKawasan = (Spinner) findViewById(R.id.spinnerKawasan);
		sKawasan.setSelection(CacheManager.SummonIssuanceInfo.OffenceLocationPos);
		
		Spinner sTempatJalan = (Spinner) findViewById(R.id.spinnerTempatJalan);
		sTempatJalan.setSelection(CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos);
	}

	private void SetLayout()
	{
		TextView tvPostNo = (TextView) findViewById(R.id.tvNoPetakTiang);
		EditText tNoPetakTiang = (EditText)findViewById(R.id.etNoPetakTiang);
		tvPostNo.setVisibility(View.GONE);
		tNoPetakTiang.setVisibility(View.GONE);
		
		tvPostNo.setVisibility(View.VISIBLE);
		tNoPetakTiang.setVisibility(View.VISIBLE);
	}

	@Override
	public void onPause()
	{
		SaveData();
		super.onPause();
	}
	private Window w;
	@Override
	public void onResume()
	{
		w = this.getWindow();
	    w.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
	    w.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	    w.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
	    
		//SaveData();
		SetLayout();
		if(CacheManager.IsClearKesalahan)
		{
			ClearData();
			FillData();
			CacheManager.IsClearKesalahan = false;
		}
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		super.onResume();
	}
	@Override
	public void onStart()
	{
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		//if(!CacheManager.IsClearKesalahan)
			FillData();
		
		super.onStart();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	@Override
	public void onBackPressed()
	{
		return;
	}
}
