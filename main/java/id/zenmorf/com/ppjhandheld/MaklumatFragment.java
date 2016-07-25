package id.zenmorf.com.ppjhandheld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 24/7/2016.
 */
public class MaklumatFragment extends Fragment {
    public MaklumatFragment() {
    }

    public static MaklumatFragment newInstance() {
        MaklumatFragment fragment = new MaklumatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maklumat, container, false);

        final EditText etVehicleMake = (EditText) rootView.findViewById(R.id.etTrafikVehicleMake);
        final Spinner spinnerTrafikModel = (Spinner) rootView.findViewById(R.id.spinnerTrafikModel);
        final EditText etVehicleModel = (EditText) rootView.findViewById(R.id.etTrafikVehicleModel);

        Spinner spinnerTrafikJenama = (Spinner) rootView.findViewById(R.id.spinnerTrafikJenama);
        List<String> list = DbLocal.GetListForJenamaSpinner(CacheManager.mContext, "VEHICLE_MAKE");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.insert("--Sila Pilih--", 0);
        dataAdapter.insert("", dataAdapter.getCount());
        spinnerTrafikJenama.setAdapter(dataAdapter);
        spinnerTrafikJenama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                etVehicleMake.setVisibility(View.GONE);
                etVehicleMake.setText("");

                List<String> list = new ArrayList<String>();
                spinnerTrafikModel.getEmptyView();

                list = DbLocal.GetListForVehicleModelSpinner(CacheManager.mContext, parent.getSelectedItem().toString());
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, list);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataAdapter.insert("--Sila Pilih--", 0);
                dataAdapter.insert("", dataAdapter.getCount());
                spinnerTrafikModel.setAdapter(dataAdapter);
                spinnerTrafikModel.setSelection(CacheManager.SummonIssuanceInfo.VehicleModelPos);
                spinnerTrafikModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
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

                if (pos == parent.getCount() - 1) {
                    etVehicleMake.setVisibility(View.VISIBLE);
                    etVehicleMake.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleMake);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Spinner spinnerTrafikJenisBadan = (Spinner) rootView.findViewById(R.id.spinnerTrafikJenisBadan);
        list = new ArrayList<String>();
        list = DbLocal.GetListForSpinner(CacheManager.mContext, "VEHICLE_TYPE");
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.insert("--Sila Pilih--", 0);
        spinnerTrafikJenisBadan.setAdapter(dataAdapter);
        EditText txtCtrlId = (EditText) rootView.findViewById(R.id.etTrafikNoKenderaan);
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

        if (CacheManager.SummonIssuanceInfo == null) {
            CacheManager.SummonIssuanceInfo = new PPJSummonIssuanceInfo();
        }

        Button btncapture = (Button) rootView.findViewById(R.id.btn_camera);
        btncapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ImageActivity.class);
                startActivity(i);
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
        CacheManager.SummonIssuanceInfo.VehicleNo = ((EditText)getView().findViewById(R.id.etTrafikNoKenderaan)).getText().toString();
        CacheManager.SummonIssuanceInfo.RoadTaxNo = ((EditText)getView().findViewById(R.id.etTrafikNoCukaiJalan)).getText().toString();
        CacheManager.SummonIssuanceInfo.SelectedVehicleMake = ((EditText)getView().findViewById(R.id.etTrafikVehicleMake)).getText().toString();
        CacheManager.SummonIssuanceInfo.SelectedVehicleModel = ((EditText)getView().findViewById(R.id.etTrafikVehicleModel)).getText().toString();

        Spinner sJenama = (Spinner)getView().findViewById(R.id.spinnerTrafikJenama);
        CacheManager.SummonIssuanceInfo.VehicleMakePos = sJenama.getSelectedItemPosition();
        if(sJenama.getSelectedItemPosition() > 0)
        {
            CacheManager.SummonIssuanceInfo.VehicleMake = sJenama.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.VehicleMake = "";
        }

        Spinner sModel = (Spinner)getView().findViewById(R.id.spinnerTrafikModel);
        CacheManager.SummonIssuanceInfo.VehicleModelPos = sModel.getSelectedItemPosition();
        if(sModel.getSelectedItemPosition() > 0)
        {
            CacheManager.SummonIssuanceInfo.VehicleModel = sModel.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.VehicleModel = "";
        }

        Spinner sJenisBadan= (Spinner)getView().findViewById(R.id.spinnerTrafikJenisBadan);
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
}
