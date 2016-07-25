package id.zenmorf.com.ppjhandheld;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hp on 24/7/2016.
 */
public class KesalahanFragment extends Fragment {
    public KesalahanFragment() {
    }

    public static KesalahanFragment newInstance() {
        KesalahanFragment fragment = new KesalahanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_kesalahan, container, false);

        final Spinner spinnerOffenceLocationArea = (Spinner)rootView.findViewById(R.id.spinnerTempatJalan);
        final EditText etSummonLocation = (EditText)rootView.findViewById(R.id.etSummonLocation);
        final Spinner spinnerOffenceSection = (Spinner)rootView.findViewById(R.id.spinnerSeksyenKaedah);
        final EditText etKesalahan = (EditText)rootView.findViewById(R.id.etKesalahan);
        final Spinner spinnerOffenceAct = (Spinner)rootView.findViewById(R.id.spinnerundangundang);

        List<String> list = new ArrayList<String>();
        list = DbLocal.GetOneFieldListForSpinner(CacheManager.mContext, "SHORT_DESCRIPTION","OFFENCE_ACT");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.insert("--Sila Pilih--", 0);
        spinnerOffenceAct.setAdapter(dataAdapter);
        spinnerOffenceAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                List<String> list = new ArrayList<String>();

                spinnerOffenceSection.getEmptyView();

                list = DbLocal.GetListForOffenceSectionSpinner(CacheManager.mContext, parent.getSelectedItem().toString());
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataAdapter.insert("--Sila Pilih--", 0);
                spinnerOffenceSection.setAdapter(dataAdapter);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerOffenceSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if(pos>0)
                {
                    List<String> list = DbLocal.GetListForOffenceSectionCodeSpinner(CacheManager.mContext, parent.getSelectedItem().toString(), spinnerOffenceAct.getSelectedItem().toString());
                    etKesalahan.setText(list.get(2));
                }
                else
                {
                    etKesalahan.setText("");
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinnerOffenceLocation = (Spinner)rootView.findViewById(R.id.spinnerKawasan);

        list = new ArrayList<String>();
        list = DbLocal.GetOneFieldListForSpinner(CacheManager.mContext,"DESCRIPTION","OFFENCE_LOCATION_AREA");
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.insert("--Sila Pilih--", 0);
        spinnerOffenceLocation.setAdapter(dataAdapter);
        spinnerOffenceLocation.setSelection(CacheManager.SummonIssuanceInfo.OffenceLocationPos);
        spinnerOffenceLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                List<String> list = new ArrayList<String>();

                spinnerOffenceLocationArea.getEmptyView();

                list = DbLocal.GetListForOffenceLocationAreaSpinner(CacheManager.mContext, parent.getSelectedItem().toString());
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataAdapter.insert("--Sila Pilih--", 0);
                dataAdapter.insert("", dataAdapter.getCount());
                spinnerOffenceLocationArea.setAdapter(dataAdapter);
                spinnerOffenceLocationArea.setSelection(CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos);
                spinnerOffenceLocationArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        etSummonLocation.setVisibility(View.GONE);
                        etSummonLocation.setText("");
                        if (pos == parent.getCount() - 1) {
                            etSummonLocation.setVisibility(View.VISIBLE);
                            etSummonLocation.setText(CacheManager.SummonIssuanceInfo.SummonLocation);
                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return rootView;
    }

    private boolean fragmentResume=false;
    private boolean fragmentVisible=false;
    private boolean fragmentOnCreated=false;
    @Override
    public void setUserVisibleHint(boolean visible){
        super.setUserVisibleHint(visible);
        if (visible && isResumed()){   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
        }else  if (visible){        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        }
        else if(!visible && fragmentOnCreated){// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
            fragmentOnCreated = false;
            SaveData();
        }
    }

    public void SaveData() {
        Spinner sOffenceAct = (Spinner)getView().findViewById(R.id.spinnerundangundang);
        CacheManager.SummonIssuanceInfo.OffenceActPos = sOffenceAct.getSelectedItemPosition();
        if(sOffenceAct.getSelectedItemPosition() > 0)
        {
            CacheManager.SummonIssuanceInfo.OffenceAct = sOffenceAct.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.OffenceAct = "";
        }

        Spinner sOffenceSection = (Spinner)getView().findViewById(R.id.spinnerSeksyenKaedah);
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

        EditText etSummonLocation = (EditText)getView().findViewById(R.id.etSummonLocation);
        CacheManager.SummonIssuanceInfo.SummonLocation = etSummonLocation.getText().toString();

        EditText tKesalahan = (EditText)getView().findViewById(R.id.etKesalahan);
        CacheManager.SummonIssuanceInfo.Offence = tKesalahan.getText().toString();

        EditText tButir = (EditText)getView().findViewById(R.id.etButirButir);
        CacheManager.SummonIssuanceInfo.OffenceDetails = tButir.getText().toString();

        Spinner sKawasan = (Spinner)getView().findViewById(R.id.spinnerKawasan);
        CacheManager.SummonIssuanceInfo.OffenceLocationPos = sKawasan.getSelectedItemPosition();
        if(sKawasan.getSelectedItemPosition() > 0)
        {
            CacheManager.SummonIssuanceInfo.OffenceLocation = sKawasan.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.OffenceLocation = "";
        }

        Spinner sTempatJalan = (Spinner)getView().findViewById(R.id.spinnerTempatJalan);
        CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos = sTempatJalan.getSelectedItemPosition();
        if(sTempatJalan.getSelectedItemPosition() > 0)
        {
            CacheManager.SummonIssuanceInfo.OffenceLocationArea = sTempatJalan.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.OffenceLocationArea = "";
        }

        EditText tButirLokasi = (EditText)getView().findViewById(R.id.etButiranLokasi);
        CacheManager.SummonIssuanceInfo.OffenceLocationDetails = tButirLokasi.getText().toString();

        CacheManager.SummonIssuanceInfo.Advertisement = DbLocal.GetAdvertisement(CacheManager.mContext);
        String delegate = "yy";
        String year = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());

        CacheManager.SummonIssuanceInfo.NoticeSerialNo = SettingsHelper.DeviceID +  year + SettingsHelper.DeviceSerialNumber;

        EditText tNoPetakTiang = (EditText)getView().findViewById(R.id.etNoPetakTiang);
        CacheManager.SummonIssuanceInfo.PostNo = tNoPetakTiang.getText().toString();

        if(CacheManager.SummonIssuanceInfo.VehicleType.length() != 0)
        {
            CacheManager.SummonIssuanceInfo.CompoundAmount1 = DbLocal.GetCompundAmountFromVehicleType(CacheManager.mContext, CacheManager.SummonIssuanceInfo.VehicleType);
        }

        if(CacheManager.SummonIssuanceInfo.OffenceSectionCode.length() != 0) {
            Cursor compoundList = DbLocal.GetCompundAmountDescription(CacheManager.mContext, CacheManager.SummonIssuanceInfo.OffenceSectionCode, CacheManager.SummonIssuanceInfo.OffenceActCode);
            try {
                if (compoundList != null) {
                    if (Float.parseFloat(compoundList.getString(1)) != 0) {
                        CacheManager.SummonIssuanceInfo.CompoundAmount1 = Float.parseFloat(compoundList.getString(1));
                        CacheManager.SummonIssuanceInfo.CompoundAmountDesc1 = compoundList.getString(2);
                        if (compoundList.getString(2).length() != 0) {
                            if (compoundList.getString(5).length() != 0) {
                                CacheManager.SummonIssuanceInfo.CompoundAmount2 = Float.parseFloat(compoundList.getString(4));
                                CacheManager.SummonIssuanceInfo.CompoundAmountDesc2 = compoundList.getString(5);
                            }
                            if (compoundList.getString(8).length() != 0) {
                                CacheManager.SummonIssuanceInfo.CompoundAmount3 = Float.parseFloat(compoundList.getString(7));
                                CacheManager.SummonIssuanceInfo.CompoundAmountDesc3 = compoundList.getString(8);
                            }
                            if (compoundList.getString(11).length() != 0) {
                                CacheManager.SummonIssuanceInfo.CompoundAmount4 = Float.parseFloat(compoundList.getString(10));
                                CacheManager.SummonIssuanceInfo.CompoundAmountDesc4 = compoundList.getString(11);
                            }
                            if (compoundList.getString(14).length() != 0) {
                                CacheManager.SummonIssuanceInfo.CompoundAmount5 = Float.parseFloat(compoundList.getString(13));
                                CacheManager.SummonIssuanceInfo.CompoundAmountDesc5 = compoundList.getString(14);
                            }
                        }
                    }
                }
            } catch (Exception ex) {

            }
            CacheManager.SummonIssuanceInfo.CompoundAmountDescription = CacheManager.GenerateCompoundAmountDescription(String.valueOf(CacheManager.SummonIssuanceInfo.CompoundAmount1));
        }
    }

    public void SectionUpdate(String offenceSectionCode, String offenceActDescription)
    {
        List<String> list = DbLocal.GetListForOffenceSectionCodeSpinner(CacheManager.mContext,offenceSectionCode, offenceActDescription);
        CacheManager.SummonIssuanceInfo.OffenceActCode  = list.get(0);
        CacheManager.SummonIssuanceInfo.OffenceSectionCode  = list.get(1);
    }
}
