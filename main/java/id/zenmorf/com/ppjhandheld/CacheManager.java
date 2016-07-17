package id.zenmorf.com.ppjhandheld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public final class CacheManager 
{

	/** The User id. */
	public static String UserId = "";

	/** The User name. */
	public static String UserName = "";

	/** The Current password. */
	public static String CurrentPassword = "";

	public static String officerCode = "";
	public static String officerId = "";
	public static String officerRank = "";
	public static String officerRankNo = "";
	public static String officerName = "";
	public static String officerZone = "";
	public static String officerDetails = "";
	/** The Log enabled. */
	public static boolean LogEnabled = false;
	
	public static boolean isCheckedRoadTaxExpiryDate = false;
	public static boolean isCheckedLicenseExpiryDate = false;
	public static boolean isCheckedCourtDate = false;
	
	public static BluetoothSerialService mSerialService = null;
	
	public static Context context;

	/** The Is lock. */
	public static boolean IsLock = true;
	
	/** The Is login. */
	public static boolean IsLogin = false;
	
	/** The Is lock back button. */
	public static boolean IsLockBackButton = false;

	/** The retain input values. */
	public static ArrayList<String> retainInputValues = new ArrayList<String>();
	/** The Summon issuance info. */
	public static PPJSummonIssuanceInfo SummonIssuanceInfo;

	public static String functionArray = "";
	
	public static int noticeType = 0;

	public static boolean IsAppOnRunning = false;
	
	public static int InstallWaitingTime = 0;
	
	public static String AlertMessage = "";
	
	public static String SpecialNumberPlate = "";
	
	public static boolean IsClearData = false;
	public static boolean IsClearKesalahan = false;
	public static boolean IsTypeChanged = true;
	public static boolean IsNewSummonsCamera = true;
	
	public static boolean IsYes = false;
	
	// Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    // Message types sent from the BluetoothReadService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;	
    
 // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
	public static final Handler mHandlerBT = new Handler() {
    	
        @Override
        public void handleMessage(Message msg) {        	
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                break;
            case MESSAGE_WRITE:
                break;
/*                
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;              
                mEmulatorView.write(readBuf, msg.arg1);
                
                break;
*/                
            case MESSAGE_DEVICE_NAME:
                break;
            case MESSAGE_TOAST:
                Toast.makeText(CacheManager.context, msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
	
	public int GetPrinterConnectionState() {
		return mSerialService.getState();
	}
	
	public static Date GetCompoundDate()
	{
		Date compoundDate = addDate(new Date(), 14);
        return  compoundDate; 
	}
	
	public static Date GetSummonsCompoundDate()
	{
		Date compoundDate = addDate(new Date(), 28);
        return  compoundDate; 
	}
	
	public static boolean CheckBluetoothStatus()
	{
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		return btAdapter.isEnabled();
	}
	
	public static void EnableBluetooth()
	{
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(!btAdapter.isEnabled())
			btAdapter.enable();
	}
	
	public static void DisableBluetooth()
	{
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(btAdapter.isEnabled())
			btAdapter.disable();
	}
	
	public static void DisableWifi()
	{
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
	}
	
	public static void EnableWifi()
	{
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
	}
	
	public static Date GetSummonsCourtDate()
	{
		Date compoundDate = addDate(new Date(), 28);
		Date courtDate = addMonth(compoundDate, 1);
		switch (courtDate.getDay())
		{
		case 6:
			courtDate = addDate(courtDate, 2);
			break;
		case 0:
			courtDate = addDate(courtDate, 1);
			break;
		default:
			break;
		}
        return  courtDate; 
	}
	
	public static String GetOtherDateString(Date compoundDate)
	{
		String delegate = "dd-MM-yyyy";
		if(compoundDate != null)
			return  (String) DateFormat.format(delegate,compoundDate);
		else
			return "";
	}
	
	static Date addDate(Date date, int d)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, d); // add d days
		date = cal.getTime();

		return date;
	}
	
	static Date addMonth(Date date, int d)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, d); // add d days
		date = cal.getTime();

		return date;
	}
	
	public static void saveProperties(String key, String value) throws IOException {
	  	  String[] fileList = { "app.properties" };
	  	  Properties prop = new Properties();
	  	  for (int i = fileList.length - 1; i >= 0; i--) {
	  	     String file = fileList[i];
	  	     try {
	  	        InputStream fileStream = context.getAssets().open(file);
	  	        prop.load(fileStream);
	  	        fileStream.close();
	  	        
		  	  	prop.setProperty(key, value);
			  	FileOutputStream out = new FileOutputStream(file);
			  	prop.store(out, "---No Comment---");
				  	out.close();
	  	     } catch (FileNotFoundException e) {
	  	        Log.d("ERROR", "Ignoring missing property file " + file);
	  	     }
	  	  }
	  	}

	/**
	 * Format date time string.
	 * 
	 * @param inputDateString
	 *            the input date string
	 * @return the string
	 */
	public static String FormatDateTimeString(String inputDateString)
	{
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMMdd");

		String reformattedStr = inputDateString;

		if (reformattedStr == null || reformattedStr.trim().equals("") || reformattedStr.trim().length() < 8)
		{
			return "";
		}

		try
		{
			fromUser.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));

			Date obj = fromUser.parse(inputDateString);
			String month;
			int monthInt = (obj.getMonth() + 1);
			if (monthInt < 10)
				month = "0" + monthInt;
			else
				month = "" + monthInt;

			int dateInt = obj.getDate();
			String date;
			if (dateInt < 10)
				date = "0" + dateInt;
			else
				date = "" + dateInt;

			reformattedStr = date + "/" + month + "/" + (obj.getYear() + 1900);
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			ErrorLog(e);
		}

		return reformattedStr;
	}
	public static String GetDate()
	{
		String delegate = "dd/MM/yyyy";
        return  (String) DateFormat.format(delegate,Calendar.getInstance().getTime()); 
	}
	public static String GetDateString(Date date)
	{
		String delegate = "dd/MM/yyyy";
		if(date != null)
			return  (String) DateFormat.format(delegate,date);
		else
			return "";
	}
	public static String GetTimeString(Date date)
	{
		String delegate = "hh:mm:ss aaa";
		if(date != null)
			return  (String) DateFormat.format(delegate,date);
		else
			return "";
	}
	public static String GetTime()
	{
		String delegate = "hh:mm:ss aaa"; 
        return  (String) DateFormat.format(delegate,Calendar.getInstance().getTime());
	}
	/**
	 * Gets the time.
	 * 
	 * @return the string
	 */
	public static String GetDateTime()
	{
		Time now = new Time();
		now.setToNow();
		return now.year + "" + now.month + "" + now.monthDay + "" + now.hour
				+ "" + now.minute + "" + now.second + "";
	}

	/**
	 * Inits the setting.
	 * 
	 * @param mcontext
	 *            the mcontext
	 */
	static void InitSetting(Context mcontext)
	{
		Settings.System.putInt(mcontext.getContentResolver(),
				Settings.System.SCREEN_OFF_TIMEOUT, 60000);
	}
	/**
	 * Lock keygaurd.
	 * 
	 * @param appContext
	 *            the app context
	 */
	public static void LockKeygaurd(Context appContext)
	{
		KeyguardManager keyguardManager;
		KeyguardLock lock;

		keyguardManager = (KeyguardManager)
				appContext.getSystemService(Context.KEYGUARD_SERVICE);

		lock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
		lock.disableKeyguard();
	}	
	/**
	 * Inits the.
	 * 
	 * @param mcontext
	 *            the mcontext
	 * @return true, if successful
	 */
	public static boolean Init(Context mcontext)
	{
		context = mcontext;
		IsAppOnRunning = true;
		return DeviceVerify();
	}
	
	private static void CopyFiles()
	{
		try
		{
			File file = new File("/mnt/sdcard/local/");
			if (file.exists())
			{
				File[] files = file.listFiles();
				for(File src : files)
				{
					File dest = new File("/data/data/id.zenmorf.com.ppjhandheld/");
					if(!dest.exists())
					{
						dest.mkdirs();
					}
					copyFile("/mnt/sdcard/local/", src.getName(), "/data/data/id.zenmorf.com.ppjhandheld/");
					src.delete();
				}
			}
			return;
		} catch (Exception localException)
		{

		}
	}
	
	static void copyFile(String inputPath, String inputFile, String outputPath)
	{
		InputStream in = null;
	    OutputStream out = null;
	    try {

	        //create output directory if it doesn't exist
	        File dir = new File (outputPath); 
	        if (!dir.exists())
	        {
	            dir.mkdirs();
	        }


	        in = new FileInputStream(inputPath + inputFile);        
	        out = new FileOutputStream(outputPath + inputFile);

	        byte[] buffer = new byte[1024];
	        int read;
	        while ((read = in.read(buffer)) != -1) {
	            out.write(buffer, 0, read);
	        }
	        in.close();
	        in = null;

	            // write the output file (You have now copied the file)
	            out.flush();
	        out.close();
	        out = null;        

	    }  catch (FileNotFoundException fnfe1) {
	        
	    }
	       catch (Exception e) {
	        
	    }
	}
	
	private static boolean isDevelopment = true;
	public static boolean DeviceVerify()
	{
		//CopyFiles();
		String injectKey = getInjectKey();
		
		String AutoGenInjectKey =	createKey(GetDeviceSerialNo().toUpperCase());

		if(isDevelopment)
		{
			//SettingsHelper.SaveFile();
			return true;
		}
		else
		{
			return injectKey.equals(AutoGenInjectKey);
		}
	}
	
	private static String GetDeviceSerialNo()
	{
		String str = "";

		try
		{
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			str = (String) get.invoke(c, "ro.serialno");
		} catch (Exception ignored)
		{
		}

		return str.toUpperCase();
	}
	
	private static String createKey(String source)
	{
		String hash = Cryptor.CheckSum(source).toUpperCase();
		char[] sourceArray = hash.toCharArray();

		int lengthSource = sourceArray.length;

		String tempArray = "";

		for (int i = lengthSource - 1; i >= 0; i--)
		{
			tempArray = tempArray + sourceArray[i];
		}

		tempArray = Cryptor.CheckSum(tempArray).toUpperCase();

		return tempArray;
	}
	
	private static String getInjectKey()
	{
		File path = new File("/data/data/id.zenmorf.com.ppjhandheld/databases/");
		File file = new File(path, "dbkl.dat");
		StringBuilder text = new StringBuilder();

		if (!file.exists())
		{
			return "";
		}
		// Read text from file

		try
		{
			// copyDirectory(file, dataKeyPath);

			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null)
			{
				text.append(line);
			}

		} catch (IOException e)
		{
			ErrorLog(e);
			return "";
			// You'll need to add proper error handling here
		}

		return text.toString().trim();
	}
	
	/**
	 * Error log.
	 * 
	 * @param e
	 *            the e
	 */
	public static void ErrorLog(Exception e)
	{
		FileHandler fh = null;
		String name;
		if (0 == Environment.getExternalStorageState().compareTo(Environment.MEDIA_MOUNTED))
			name = Environment.getExternalStorageDirectory().getAbsolutePath();
		else
			name = Environment.getDataDirectory().getAbsolutePath();

		name += "/dbkllogfile.log";

		try
		{
			fh = new FileHandler(name, 256 * 1024, 1, true);
			fh.setFormatter(new SimpleFormatter());

			String Msg = e.getLocalizedMessage();

			StackTraceElement[] element = e.getStackTrace();

			for (StackTraceElement stackTraceElement : element)
			{
				Msg += "\n" + stackTraceElement.toString();
			}

			fh.publish(new LogRecord(Level.WARNING, Msg));
		} catch (Exception a)
		{
			Log.e("MyLog", "FileHandler exception", a);
			return;
		} finally
		{
			if (fh != null)
				fh.close();
		}
	}
	
	public static String GenerateCompoundAmountDescription(String strCompoundAmount)
    {
        boolean bValidCompoundAmount = false;
        int nCompoundAmountMaxLen = 12;
        int nCharIndex = 0;
        int nCharProcessed = 0;
        int nChar = 0;
        String strCompoundAmountDes = "";
        String strCharDes = "";
        String strGroupDes = "";
        String strIntegralDes = "";     // Part that is to the left of the decimal separator(.)
        String strFractionalDes = "";   // The part that is to the right of the decimal separator(.)
        String[] strCharDesList = { "", "SATU", "DUA", "TIGA", "EMPAT", "LIMA", "ENAM", "TUJUH", "LAPAN", "SEMBILAN" };

        if (strCompoundAmount.indexOf(".") == -1)
        {
            // Compound amount max length - 3 for ".00" characters
            if (strCompoundAmount.length() > (nCompoundAmountMaxLen - 3))
            {
                ; // Compound amount exceeds max length
            }
            else
            {
                strCompoundAmount += ".00";
                bValidCompoundAmount = true;
            }
        }
        else if ((strCompoundAmount.indexOf(".")) != (strCompoundAmount.length() - 3))
        {
        	if((strCompoundAmount.indexOf(".")) == (strCompoundAmount.length() - 2))
        	{
        		strCompoundAmount += "0";
                bValidCompoundAmount = true;
        	}
            ; // Compound amount has invalid format
        }
        else
            bValidCompoundAmount = true;

        if (bValidCompoundAmount)
        {
            while ((nCharIndex = strCompoundAmount.length() - (nCharProcessed)) > 0)
            {
                if (nCharIndex != 3)
                {
                    nChar = Integer.parseInt(strCompoundAmount.substring(nCharProcessed, nCharProcessed + 1));
                    strCharDes = strCharDesList[nChar];
                }

                switch (nCharIndex)
                {
                    case 1:
                        if (strCharDes != "")
                            strGroupDes += (strGroupDes == "") ? strCharDes : " " + strCharDes;

                        if (strGroupDes != "")
                            strFractionalDes += (strFractionalDes == "") ? strGroupDes : " " + strGroupDes;

                        if (strFractionalDes != "")
                            strFractionalDes += " SEN";
                        break;

                    case 2:
                        strGroupDes = "";
                        if (strCharDes != "")
                            strGroupDes += strCharDes + " PULUH";
                        break;

                    case 3: break;
                    case 4: // SA
                        switch (Integer.parseInt(strCompoundAmount.substring(nCharProcessed - 1, nCharProcessed)))
                        {
                            case 0:
                                if (strCharDes != "")
                                    strGroupDes += (strGroupDes == "") ? strCharDes : (" " + strCharDes);
                                break;

                            case 1:
                                if (nChar == 0)
                                    strGroupDes += (strGroupDes == "") ? "SEPULUH" : " SEPULUH";
                                else if (nChar == 1)
                                    strGroupDes += (strGroupDes == "") ? "SEBELAS" : " SEBELAS";
                                else
                                    strGroupDes += (strGroupDes == "") ? (strCharDes + " BELAS") : (" " + strCharDes + " BELAS");
                                break;

                            default:
                                strGroupDes += (nChar == 0) ? " PULUH" : (" PULUH " + strCharDes);
                                break;
                        }

                        if (strGroupDes != "")
                            strIntegralDes += (strIntegralDes == "") ? strGroupDes : " " + strGroupDes;

                        if (strIntegralDes != "")
                            strIntegralDes += " RINGGIT";
                        break;

                    case 5:  // PULUH
                        if ((strCharDes != "") && (nChar != 1))
                            strGroupDes += (strGroupDes == "") ? strCharDes : (" " + strCharDes);
                        break;

                    case 7: // RIBU
                        if (strCharDes != "")
                            strGroupDes += (strGroupDes == "") ? strCharDes : " " + strCharDes;

                        if (strGroupDes != "")
                        {
                            strGroupDes += " RIBU";
                            strIntegralDes += (strIntegralDes == "") ? strGroupDes : " " + strGroupDes;
                        }
                        break;

                    case 10: // JUTA
                        if (strCharDes != "")
                            strGroupDes += (strGroupDes == "") ? strCharDes : " " + strCharDes;

                        if (strGroupDes != "")
                        {
                            strGroupDes += " JUTA";
                            strIntegralDes += (strIntegralDes == "") ? strGroupDes : " " + strGroupDes;
                        }
                        break;

                    case 8:  // RIBUAN PULUH
                    case 11: // JUTAAN PULUH
                        if (strCharDes != "")
                            strGroupDes += (strGroupDes == "") ? (strCharDes + " PULUH") : (" " + strCharDes + " PULUH");
                        break;

                    case 6:  // RATUS
                    case 9:  // RIBUAN RATUS
                    case 12: // JUTAAN RATUS
                        strGroupDes = "";
                        if (strCharDes != "")
                            strGroupDes += strCharDes + " RATUS";
                        break;
                }

                nCharProcessed++;
            }

            if ((strIntegralDes != "") && (strFractionalDes != ""))
                strCompoundAmountDes = strIntegralDes + " DAN " + strFractionalDes;
            else if (strIntegralDes != "")
                strCompoundAmountDes = strIntegralDes;
            else
                strCompoundAmountDes = strFractionalDes;
        }

        return strCompoundAmountDes;
    }
}
