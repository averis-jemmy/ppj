package id.zenmorf.com.ppjhandheld;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 23/7/2016.
 */
public class PageFragment extends Fragment {
    private static final String ARG_PAGE_NUMBER = "page_number";
    private Runnable doPrint;
    private ProgressDialog mProgressDialog = null;

    public PageFragment() {
    }

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int page = getArguments().getInt(ARG_PAGE_NUMBER, -1);

        View rootView = inflater.inflate(R.layout.fragment_maklumat, container, false);

        if (page == 1) {
            rootView = inflater.inflate(R.layout.fragment_maklumat, container, false);

            final EditText etVehicleMake = (EditText)rootView.findViewById(R.id.etTrafikVehicleMake);
            final Spinner spinnerTrafikModel = (Spinner)rootView.findViewById(R.id.spinnerTrafikModel);
            final EditText etVehicleModel = (EditText)rootView.findViewById(R.id.etTrafikVehicleModel);

            Spinner spinnerTrafikJenama = (Spinner)rootView.findViewById(R.id.spinnerTrafikJenama);
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

                    if(pos == parent.getCount() - 1)
                    {
                        etVehicleMake.setVisibility(View.VISIBLE);
                        etVehicleMake.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleMake);
                    }
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


            Spinner spinnerTrafikJenisBadan = (Spinner)rootView.findViewById(R.id.spinnerTrafikJenisBadan);
            list = new ArrayList<String>();
            list = DbLocal.GetListForSpinner(CacheManager.mContext,"VEHICLE_TYPE");
            dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.insert("--Sila Pilih--", 0);
            spinnerTrafikJenisBadan.setAdapter(dataAdapter);
            EditText txtCtrlId = (EditText)rootView.findViewById(R.id.etTrafikNoKenderaan);
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

            if(CacheManager.SummonIssuanceInfo == null)
            {
                CacheManager.SummonIssuanceInfo = new PPJSummonIssuanceInfo();
            }

            Button btncapture = (Button)rootView.findViewById(R.id.btn_camera);
            btncapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ImageActivity.class);
                    startActivity(i);
                }
            });
        }
        else if (page == 2) {
            rootView = inflater.inflate(R.layout.fragment_kesalahan, container, false);

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
                    spinnerOffenceSection.setSelection(CacheManager.SummonIssuanceInfo.OffenceSectionPos);
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
        }
        else if (page == 3) {
            rootView = inflater.inflate(R.layout.fragment_ringkasan, container, false);

            final EditText tTarikh = (EditText)rootView.findViewById(R.id.etSummaryTarikh);
            final EditText tMasa = (EditText)rootView.findViewById(R.id.etSummaryMasa);
            Button btnStatus = (Button)rootView.findViewById(R.id.btnstatus);
            btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nAmountNoticeIssued = 0;

                    String status = "Jumlah Notis Yang Telah Di Keluarkan : " + nAmountNoticeIssued + "\n";
                    status += "Bateri : " + CacheManager.BatteryPercentage + "%\n";
                    status += "Versi : 1.0.0.0";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("STATUS");
                    builder.setMessage(status);
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            });

            Button btnCetak = (Button)rootView.findViewById(R.id.btncetak);
            btnCetak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CacheManager.SummonIssuanceInfo.OffenceDateTime = new Date();
                    CacheManager.SummonIssuanceInfo.CompoundDate = CacheManager.GetCompoundDate();
                    if(ValidateData())
                    {
                        doPrint = new Runnable() {
                            @Override
                            public void run()
                            {
                                Looper.prepare();
                                DoPrint();
                                Looper.loop();
                                Looper.myLooper().quit();
                            }
                        };

                        mProgressDialog = new ProgressDialog(getActivity());
                        mProgressDialog.setMessage("Loading");
                        mProgressDialog.setTitle("");
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.show();

                        Thread thread = new Thread(null, doPrint, "PrintProcess");
                        thread.start();
                    }
                }
            });

            final Handler timeHandler = new Handler();
            Runnable run = new Runnable() {

                @Override
                public void run() {
                    tTarikh.setText(CacheManager.GetDate());
                    tMasa.setText(CacheManager.GetTime().toUpperCase());
                    timeHandler.postDelayed(this, 500);
                }
            };
            timeHandler.postDelayed(run, 500);
        }

        return rootView;
    }

    public void AlertMessage(final Context context, String title,String message,int type)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                    doPrint = new Runnable() {
                        @Override
                        public void run()
                        {
                            Looper.prepare();
                            DoPrintCopy();
                            Looper.loop();
                            Looper.myLooper().quit();
                        }
                    };
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setMessage("Loading");
                    mProgressDialog.setTitle("");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.show();

                    Thread thread = new Thread(null, doPrint, "LoginProcess");
                    thread.start();
                }
            });

            builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //CacheManager.IsClearData = true;
                    //Intent i = new Intent(RingkasanActivity.this,NoticesActivity.class);
                    //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //RingkasanActivity.this.startActivity(i);
                    //finish();
                }
            });
        }
        if(type == 3)
        {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    AlertMessage(getActivity(), "CETAK", "Cetak Salinan Kedua?", 2);
                }
            });
        }
        builder.show();
    }

    boolean ValidateData()
    {
        if (CacheManager.SummonIssuanceInfo.VehicleNo.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "NO. KEND.", "Sila Isikan No. Kend.", 3);
            return false;
        }
        if (CacheManager.SummonIssuanceInfo.VehicleType.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "JENIS BADAN", "Sila Pilih Jenis Badan Kenderaan", 3);
            return false;
        }
        if (CacheManager.SummonIssuanceInfo.OffenceAct.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "UNDANG-UNDANG", "Sila Pilih Peruntukan Undang-Undang", 3);
            return false;
        }
        if (CacheManager.SummonIssuanceInfo.OffenceSection.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "UNDANG-UNDANG", "Sila Pilih Seksyen/Kaedah", 3);
            return false;
        }
        if (CacheManager.SummonIssuanceInfo.OffenceLocationArea.length() == 0 && CacheManager.SummonIssuanceInfo.SummonLocation.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "NAMA JALAN", "Sila Pilih Nama Jalan", 3);
            return false;
        }

        return true;
    }

    private boolean CheckPrint()
    {
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
        boolean bStatus = false;

        if (CacheManager.mSerialService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (CacheManager.mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
                // Start the Bluetooth chat services
                CacheManager.mSerialService.start();
            }
        }

        if(CacheManager.mSerialService.getState() != BluetoothSerialService.STATE_CONNECTED){
            String address = CacheManager.CompileAddress(SettingsHelper.MACAddress);
            BluetoothAdapter mBluetoothAdapter = null;
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            try {
                // Attempt to connect to the device
                CacheManager.mSerialService.connect(device);

                Thread.sleep(3000);
            } catch (Exception e) {
                bStatus = false;
            }
        }
        else
        {
            bStatus = true;
        }

        return bStatus;
    }

    private void DoPrint()
    {
        try
        {
            if(CheckPrint())
            {
                PrintHandler.PrintTraffic(CacheManager.SummonIssuanceInfo);
                //GenerateXmlNotice(CacheManager.SummonIssuanceInfo);

                AlertMessage(getActivity(), "CETAK", "Cetak Salinan Kedua?", 2);

                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
            }
            else
            {
                CustomAlertDialog.Show(getActivity(), "PRINTER", "CETAK SAMAN GAGAL", 0);
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
            }
        }
        catch(Exception ex)
        {
            AlertMessage(getActivity(), "Printer", "CETAK SAMAN GAGAL", 0);
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }
    }

    private void DoPrintCopy()
    {
        try
        {
            if(CheckPrint())
            {
                PrintHandler.PrintTraffic(CacheManager.SummonIssuanceInfo);

                Thread.sleep(5000);

                AlertMessage(getActivity(), "CETAK", "Cetak Salinan Kedua?", 2);

                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
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

                AlertMessage(getActivity(), "Printer", "CETAK SAMAN GAGAL", 3);
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
            }
        }
        catch(Exception ex)
        {
            AlertMessage(getActivity(), "Printer", "CETAK SAMAN GAGAL", 3);
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }
    }
}
