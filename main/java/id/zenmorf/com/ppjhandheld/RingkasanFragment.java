package id.zenmorf.com.ppjhandheld;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

/**
 * Created by hp on 24/7/2016.
 */
public class RingkasanFragment extends Fragment {
    private Runnable doPrint;
    private ProgressDialog mProgressDialog = null;

    public RingkasanFragment() {
    }

    public static RingkasanFragment newInstance() {
        RingkasanFragment fragment = new RingkasanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
            FillData();
        }else  if (visible){        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        }
        else if(!visible && fragmentOnCreated){// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
            fragmentOnCreated = false;
        }
    }

    public void FillData() {
        EditText tNoKenderaan = (EditText)getView().findViewById(R.id.etSummaryNoKenderaan);
        tNoKenderaan.setText(CacheManager.SummonIssuanceInfo.VehicleNo);

        EditText tNoCukaiJalan = (EditText)getView().findViewById(R.id.etSummaryNoCukaiJalan);
        tNoCukaiJalan.setText(CacheManager.SummonIssuanceInfo.RoadTaxNo);

        EditText tJenama = (EditText)getView().findViewById(R.id.etSummaryJenama);
        if(CacheManager.SummonIssuanceInfo.VehicleMake.length() != 0)
        {
            tJenama.setText(CacheManager.SummonIssuanceInfo.VehicleMake);
        }
        else
        {
            tJenama.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleMake);
        }

        EditText tModel = (EditText)getView().findViewById(R.id.etSummaryModel);
        if(CacheManager.SummonIssuanceInfo.VehicleModel.length() != 0)
        {
            tModel.setText(CacheManager.SummonIssuanceInfo.VehicleModel);
        }
        else
        {
            tModel.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleModel);
        }

        EditText tTempatjalan = (EditText)getView().findViewById(R.id.etSummaryTempatJalan);
        if(CacheManager.SummonIssuanceInfo.OffenceLocationArea.length() != 0)
        {
            tTempatjalan.setText(CacheManager.SummonIssuanceInfo.OffenceLocationArea);
        }
        else
        {
            tTempatjalan.setText(CacheManager.SummonIssuanceInfo.SummonLocation);
        }

        EditText tOffenceAct = (EditText)getView().findViewById(R.id.etSummaryUndangUndang);
        tOffenceAct.setText(CacheManager.SummonIssuanceInfo.OffenceAct);

        EditText tOffenceSection = (EditText)getView().findViewById(R.id.etSummarySeksyenKaedah);
        tOffenceSection.setText(CacheManager.SummonIssuanceInfo.OffenceSection);
    }

    @Override
    public void onViewStateRestored (Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        FillData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ringkasan, container, false);

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

        EditText tNoKenderaan = (EditText)rootView.findViewById(R.id.etSummaryNoKenderaan);
        tNoKenderaan.setText(CacheManager.SummonIssuanceInfo.VehicleNo);

        EditText tNoCukaiJalan = (EditText)rootView.findViewById(R.id.etSummaryNoCukaiJalan);
        tNoCukaiJalan.setText(CacheManager.SummonIssuanceInfo.RoadTaxNo);

        EditText tJenama = (EditText)rootView.findViewById(R.id.etSummaryJenama);
        if(CacheManager.SummonIssuanceInfo.VehicleMake.length() != 0)
        {
            tJenama.setText(CacheManager.SummonIssuanceInfo.VehicleMake);
        }
        else
        {
            tJenama.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleMake);
        }

        EditText tModel = (EditText)rootView.findViewById(R.id.etSummaryModel);
        if(CacheManager.SummonIssuanceInfo.VehicleModel.length() != 0)
        {
            tModel.setText(CacheManager.SummonIssuanceInfo.VehicleModel);
        }
        else
        {
            tModel.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleModel);
        }

        EditText tTempatjalan = (EditText)rootView.findViewById(R.id.etSummaryTempatJalan);
        if(CacheManager.SummonIssuanceInfo.OffenceLocationArea.length() != 0)
        {
            tTempatjalan.setText(CacheManager.SummonIssuanceInfo.OffenceLocationArea);
        }
        else
        {
            tTempatjalan.setText(CacheManager.SummonIssuanceInfo.SummonLocation);
        }

        EditText tOffenceAct = (EditText)rootView.findViewById(R.id.etSummaryUndangUndang);
        tOffenceAct.setText(CacheManager.SummonIssuanceInfo.OffenceAct);

        EditText tOffenceSection = (EditText)rootView.findViewById(R.id.etSummarySeksyenKaedah);
        tOffenceSection.setText(CacheManager.SummonIssuanceInfo.OffenceSection);

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
                    CacheManager.IsNewNotice = true;
                    Intent i = new Intent(getActivity(), NoticeIssuanceActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(i);
                    getActivity().finish();
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
