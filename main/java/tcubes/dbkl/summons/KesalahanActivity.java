package tcubes.dbkl.summons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import tcubes.dbkl.summons.R;

public class KesalahanActivity extends Activity {

	private Spinner spinnerOffenceAct;
	private Spinner spinnerOffenceSection;
	private Spinner spinnerOffenceLocation;
	private Spinner spinnerOffenceLocationArea;
	private EditText etKesalahan;
	
	private EditText txtOffence;
	private EditText txtOffenceDetails;
	private EditText txtOffenceLocationDetails;
	private EditText txtOffencePostNo;
	private Button btnKesalahanCetak;
	//private Button btnKesalahanSave;
	
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
		btnKesalahanCetak = (Button)findViewById(R.id.btnKesalahanCetak);
		btnKesalahanCetak.setOnClickListener(btnKesalahanCetakListener);
	  }
	private OnClickListener btnKesalahanCetakListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			//SaveData();	
			//PrintHandler.PrintTraffic(CacheManager.SummonIssuanceInfo);
			//CacheManager.IsClearData = true;
	      	//Intent i = new Intent(KesalahanActivity.this,NoticesActivity.class);
	      	//KesalahanActivity.this.startActivity(i);
        }
		
	};
	private OnClickListener btnSaveListener = new OnClickListener()
	{
		public void onClick(View v)
		{   
			SaveData();			
        }
		
	};
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
		spinnerOffenceLocationArea.setSelection(CacheManager.SummonIssuanceInfo.offenceLocationAreaPos);
		
		spinnerOffenceLocationArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		    	EditText etSummonLocation = (EditText) findViewById(R.id.etSummonLocation);
		    	etSummonLocation.setVisibility(View.GONE);
		    	etSummonLocation.setText("");
    			if(pos == parent.getCount() - 1)
	    		{
    				etSummonLocation.setVisibility(View.VISIBLE);
    				etSummonLocation.setText(CacheManager.SummonIssuanceInfo.summonLocation);
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
		spinnerOffenceSection.setSelection(CacheManager.SummonIssuanceInfo.offenceSectionPos);
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
				CacheManager.SummonIssuanceInfo.offenceActCode = cur.getString(0);
				CacheManager.SummonIssuanceInfo.offenceSectionCode = cur.getString(1);
            } while (cur.moveToNext());
			cur.close();
        }
	}
	public void SaveData()
	{		
		Spinner sOffenceAct = (Spinner) findViewById(R.id.spinnerundangundang);
		CacheManager.SummonIssuanceInfo.offenceActPos = sOffenceAct.getSelectedItemPosition();
		if(sOffenceAct.getSelectedItemPosition() > 0)
		{
			CacheManager.SummonIssuanceInfo.offenceAct = sOffenceAct.getSelectedItem().toString();
		}
		else
		{
			CacheManager.SummonIssuanceInfo.offenceAct = "";
		}
		
		Spinner sOffenceSection = (Spinner) findViewById(R.id.spinnerSeksyenKaedah);
		CacheManager.SummonIssuanceInfo.offenceSectionPos = sOffenceSection.getSelectedItemPosition();
		if(sOffenceSection.getSelectedItemPosition() > 0)
		{
			CacheManager.SummonIssuanceInfo.offenceSection = sOffenceSection.getSelectedItem().toString();
			SectionUpdate(sOffenceSection.getSelectedItem().toString(), sOffenceAct.getSelectedItem().toString());
		}
		else
		{
			CacheManager.SummonIssuanceInfo.offenceSection = "";
			CacheManager.SummonIssuanceInfo.offenceActCode = "";
			CacheManager.SummonIssuanceInfo.offenceSectionCode = "";
		}
		
		EditText etSummonLocation = (EditText) findViewById(R.id.etSummonLocation);
		CacheManager.SummonIssuanceInfo.summonLocation = etSummonLocation.getText().toString();
		
		EditText tKesalahan = (EditText)findViewById(R.id.etKesalahan);
		CacheManager.SummonIssuanceInfo.offence = tKesalahan.getText().toString();
		
		EditText tButir = (EditText)findViewById(R.id.etButirButir);
		CacheManager.SummonIssuanceInfo.offenceDetails = tButir.getText().toString();
		
		Spinner sKawasan = (Spinner) findViewById(R.id.spinnerKawasan);
		CacheManager.SummonIssuanceInfo.offenceLocationPos = sKawasan.getSelectedItemPosition();
		if(sKawasan.getSelectedItemPosition() > 0)
		{
			CacheManager.SummonIssuanceInfo.offenceLocation = sKawasan.getSelectedItem().toString();
		}
		else
		{
			CacheManager.SummonIssuanceInfo.offenceLocation = "";
		}
		
		Spinner sTempatJalan = (Spinner) findViewById(R.id.spinnerTempatJalan);
		CacheManager.SummonIssuanceInfo.offenceLocationAreaPos = sTempatJalan.getSelectedItemPosition();
		if(sTempatJalan.getSelectedItemPosition() > 0)
		{
			CacheManager.SummonIssuanceInfo.offenceLocationArea = sTempatJalan.getSelectedItem().toString();
		}
		else
		{
			CacheManager.SummonIssuanceInfo.offenceLocationArea = "";
		}
		
		EditText tButirLokasi = (EditText)findViewById(R.id.etButiranLokasi);
		CacheManager.SummonIssuanceInfo.offenceLocationDetails = tButirLokasi.getText().toString();

		CacheManager.SummonIssuanceInfo.advertisement = DbLocal.GetAdvertisement(CacheManager.context);
		String delegate = "yy";         
		String year = (String) DateFormat.format(delegate,Calendar.getInstance().getTime()); 
		
		CacheManager.SummonIssuanceInfo.NoticeSerialNo = SettingsHelper.DeviceID +  year + SettingsHelper.DeviceSerialNumber;
		CacheManager.SummonIssuanceInfo.OfficerZone = CacheManager.officerZone;
		
		if(CacheManager.SummonIssuanceInfo.jenisNotisCode != 1)
		{
			EditText tNoPetakTiang = (EditText)findViewById(R.id.etNoPetakTiang);
			CacheManager.SummonIssuanceInfo.postNo = tNoPetakTiang.getText().toString();
		}
		else
		{
			CacheManager.SummonIssuanceInfo.postNo = "";
		}
		
		if(CacheManager.SummonIssuanceInfo.jenisNotisCode != 0)
		{
			if(CacheManager.SummonIssuanceInfo.offenceSectionCode.length() != 0)
			{
				CacheManager.SummonIssuanceInfo.compoundAmount1 = DbLocal.GetCompundAmountFromSection(CacheManager.context, CacheManager.SummonIssuanceInfo.offenceSectionCode, CacheManager.SummonIssuanceInfo.offenceActCode);
			}
		}
		else
		{
			if(CacheManager.SummonIssuanceInfo.jenisBadan.length() != 0)
			{
				CacheManager.SummonIssuanceInfo.compoundAmount1 = DbLocal.GetCompundAmountFromVehicleType(CacheManager.context, CacheManager.SummonIssuanceInfo.jenisBadan);
			}
		}
		
		if(CacheManager.SummonIssuanceInfo.offenceSectionCode.length() != 0)
		{
			Cursor compoundList = DbLocal.GetCompundAmountDescription(CacheManager.context, CacheManager.SummonIssuanceInfo.offenceSectionCode, CacheManager.SummonIssuanceInfo.offenceActCode);
			try
			{
				if(compoundList != null)
				{
					if(Float.parseFloat(compoundList.getString(1)) != 0)
					{
						CacheManager.SummonIssuanceInfo.compoundAmount1 = Float.parseFloat(compoundList.getString(1));
						CacheManager.SummonIssuanceInfo.compoundAmountDesc1 = compoundList.getString(2);
						if(compoundList.getString(2).length() != 0)
						{
							if (compoundList.getString(5).length() != 0)
			                {
								CacheManager.SummonIssuanceInfo.compoundAmount2 = Float.parseFloat(compoundList.getString(4));
			                    CacheManager.SummonIssuanceInfo.compoundAmountDesc2 = compoundList.getString(5);
			                }
			                if (compoundList.getString(8).length() != 0)
			                {
			                	CacheManager.SummonIssuanceInfo.compoundAmount3 = Float.parseFloat(compoundList.getString(7));
			                    CacheManager.SummonIssuanceInfo.compoundAmountDesc3 = compoundList.getString(8);
			                }
			                if (compoundList.getString(11).length() != 0)
			                {
			                	CacheManager.SummonIssuanceInfo.compoundAmount4 = Float.parseFloat(compoundList.getString(10));
			                    CacheManager.SummonIssuanceInfo.compoundAmountDesc4 = compoundList.getString(11);
			                }
			                if (compoundList.getString(14).length() != 0)
			                {
			                	CacheManager.SummonIssuanceInfo.compoundAmount5 = Float.parseFloat(compoundList.getString(13));
			                    CacheManager.SummonIssuanceInfo.compoundAmountDesc5 = compoundList.getString(14);
			                }
						}
					}
				}
			}
			catch(Exception ex)
			{
				
			}
			
			CacheManager.SummonIssuanceInfo.compoundAmountDescription = CacheManager.GenerateCompoundAmountDescription(String.valueOf(CacheManager.SummonIssuanceInfo.compoundAmount1));
		}
		
		if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 2)
		{
			DBKLDatePickerField dtCompoundDate = (DBKLDatePickerField) findViewById(R.id.dtpCompoundDate);
			Date date = new Date(dtCompoundDate.mYear - 1900, dtCompoundDate.mMonth, dtCompoundDate.mDay);
			CacheManager.SummonIssuanceInfo.CompoundDate = date;
			
			DBKLDatePickerField dtCourtDate = (DBKLDatePickerField) findViewById(R.id.dtpCourtDate);
			date = new Date(dtCourtDate.mYear - 1900, dtCourtDate.mMonth, dtCourtDate.mDay);
			CacheManager.SummonIssuanceInfo.courtDate = date;
		}
		else
		{
			CacheManager.SummonIssuanceInfo.CompoundDate = CacheManager.GetCompoundDate();
			CacheManager.SummonIssuanceInfo.courtDate = null;
		}
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
		
		DBKLDatePickerField dtCompoundDate = (DBKLDatePickerField) findViewById(R.id.dtpCompoundDate);
		Date date = CacheManager.GetSummonsCompoundDate();		
		dtCompoundDate.updateDisplay(date.getYear() + 1900, date.getMonth(), date.getDate());
		
		DBKLDatePickerField dtCourtDate = (DBKLDatePickerField) findViewById(R.id.dtpCourtDate);
		date = CacheManager.GetSummonsCourtDate();
		dtCourtDate.updateDisplay(date.getYear() + 1900, date.getMonth(), date.getDate());
	}
	public void FillData()
	{		
		if(!CacheManager.IsClearKesalahan)
		{
			Spinner sOffenceAct = (Spinner) findViewById(R.id.spinnerundangundang);
			sOffenceAct.setSelection(CacheManager.SummonIssuanceInfo.offenceActPos);
			
			Spinner sOffenceSection = (Spinner) findViewById(R.id.spinnerSeksyenKaedah);
			sOffenceSection.setSelection(CacheManager.SummonIssuanceInfo.offenceSectionPos);
			
			EditText tButir = (EditText)findViewById(R.id.etButirButir);
			tButir.setText(CacheManager.SummonIssuanceInfo.offenceDetails);
			
			EditText tButirLokasi = (EditText)findViewById(R.id.etButiranLokasi);
			tButirLokasi.setText(CacheManager.SummonIssuanceInfo.offenceLocationDetails);
			
			if(CacheManager.SummonIssuanceInfo.jenisNotisCode != 1)
			{
				EditText tNoPetakTiang = (EditText)findViewById(R.id.etNoPetakTiang);
				tNoPetakTiang.setText(CacheManager.SummonIssuanceInfo.postNo);
			}
			
			if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 2)
			{
				DBKLDatePickerField dtCompoundDate = (DBKLDatePickerField) findViewById(R.id.dtpCompoundDate);
				Date date = CacheManager.SummonIssuanceInfo.CompoundDate;		
				dtCompoundDate.updateDisplay(date.getYear() + 1900, date.getMonth(), date.getDate());
				
				DBKLDatePickerField dtCourtDate = (DBKLDatePickerField) findViewById(R.id.dtpCourtDate);
				date = CacheManager.SummonIssuanceInfo.courtDate;
				dtCourtDate.updateDisplay(date.getYear() + 1900, date.getMonth(), date.getDate());
			}
		}
		
		Spinner sKawasan = (Spinner) findViewById(R.id.spinnerKawasan);
		sKawasan.setSelection(CacheManager.SummonIssuanceInfo.offenceLocationPos);
		
		Spinner sTempatJalan = (Spinner) findViewById(R.id.spinnerTempatJalan);
		sTempatJalan.setSelection(CacheManager.SummonIssuanceInfo.offenceLocationAreaPos);
	}
	private void SetLayout()
	{
		TextView tvPostNo = (TextView) findViewById(R.id.tvNoPetakTiang);
		TextView tvCompoundDate = (TextView) findViewById(R.id.tvCompoundDate);
		TextView tvCourtDate = (TextView) findViewById(R.id.tvCourtDate);
		EditText tNoPetakTiang = (EditText)findViewById(R.id.etNoPetakTiang);
		FrameLayout layCompoundDate = (FrameLayout) findViewById(R.id.layCompoundDate);
		FrameLayout layCourtDate = (FrameLayout) findViewById(R.id.layCourtDate);
		tvPostNo.setVisibility(View.GONE);
		tNoPetakTiang.setVisibility(View.GONE);
		tvCompoundDate.setVisibility(View.GONE);
		tvCourtDate.setVisibility(View.GONE);
		layCompoundDate.setVisibility(View.GONE);
		layCourtDate.setVisibility(View.GONE);
		
		if(CacheManager.SummonIssuanceInfo.jenisNotisCode != 1)
		{
			tvPostNo.setVisibility(View.VISIBLE);
			tNoPetakTiang.setVisibility(View.VISIBLE);
		}
		if(CacheManager.SummonIssuanceInfo.jenisNotisCode == 2)
		{
			tvCompoundDate.setVisibility(View.VISIBLE);
			tvCourtDate.setVisibility(View.VISIBLE);
			layCompoundDate.setVisibility(View.VISIBLE);
			layCourtDate.setVisibility(View.VISIBLE);
		}
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
		//getMenuInflater().inflate(R.menu.activity_kesalahan, menu);
		return true;
	}
	@Override
	public void onBackPressed()
	{
		//Intent i = new Intent(NoticesActivity.this,LoginActivity.class);
		//NoticesActivity.this.startActivity(i);
		return;
	}
}
