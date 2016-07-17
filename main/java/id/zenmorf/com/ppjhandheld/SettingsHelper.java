package id.zenmorf.com.ppjhandheld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;

public class SettingsHelper {
	public static String MACAddressText = "MACAddress=";
	public static String DeviceIDText = "DeviceID=";
	public static String DeviceSerialNumberText = "DeviceSerialNumber=";
	public static String YearNumberText = "YearNumber=";
	public static String FTPServerText = "FTPServer=";
	public static String FTPUserText = "FTPUser=";
	public static String FTPPasswordText = "FTPPassword=";
	public static String FTPUploadText = "FTPUpload=";
	public static String FTPDownloadText = "FTPDownload=";
	public static String MACAddress = "";
	public static String DeviceID = "ZZ9";
	public static String DeviceSerialNumber = "00001";
	public static String YearNumber = "2013";
	public static String FTPServer = "192.168.1.125";
	public static String FTPUser = "dbkl";
	public static String FTPPassword = "pass";
	public static String FTPUpload = "\\upload";
	public static String FTPDownload = "\\download";
	
	public static boolean CheckFile()
	{
		try
		{
			File file = new File("/mnt/sdcard/summons/config/");
			//File file = new File("/mnt/sdcard/local/");

			if(!file.exists())
			{
				file.mkdirs();
			}
			
			File realFile = new File(file, "device.dat");
			if(!realFile.exists())
				return false;
		}
		catch(Exception ex)
		{
			
		}
		return true;
	}
	
	public static void LoadFile()
	{
		try
		{
			File file = new File("/mnt/sdcard/summons/config/");
			//File file = new File("/mnt/sdcard/local/");

			if(!file.exists())
			{
				file.mkdirs();
			}
			
			if (file.exists())
			{
				List<String> lines = new ArrayList<String>();
				BufferedReader reader;
				reader = new BufferedReader(new FileReader(new File(file, "device.dat")));

			    String line = "";
			    while(( line = reader.readLine()) != null)
			    {
			    	lines.add(line);
			    }
			    reader.close();
			    
			    for(int i=0;i<lines.size();i++)
			    {
			    	String[] temp = lines.get(i).split("=");
			    	if(temp[0].equalsIgnoreCase("MACAddress"))
			    	{
			    		if(temp.length > 1)
			    			MACAddress = temp[1];
			    		else
			    			MACAddress = "";
			    	}
			    	if(temp[0].equalsIgnoreCase("DeviceID"))
			    	{
			    		if(temp.length > 1)
			    			DeviceID = temp[1];
			    		else
			    			DeviceID = "";
			    	}
			    	if(temp[0].equalsIgnoreCase("DeviceSerialNumber"))
			    	{
			    		if(temp.length > 1)
			    			DeviceSerialNumber = temp[1];
			    		else
			    			DeviceSerialNumber = "";
			    	}
			    	if(temp[0].equalsIgnoreCase("YearNumber"))
			    	{
			    		if(temp.length > 1)
			    			YearNumber = temp[1];
			    		else
			    			YearNumber = "";
			    	}
			    	if(temp[0].equalsIgnoreCase("FTPServer"))
			    	{
			    		if(temp.length > 1)
			    			FTPServer = temp[1];
			    		else
			    			FTPServer = "";
			    	}
			    	if(temp[0].equalsIgnoreCase("FTPUser"))
			    	{
			    		if(temp.length > 1)
			    			FTPUser = temp[1];
			    		else
			    			FTPUser = "";
			    	}
			    	if(temp[0].equalsIgnoreCase("FTPPassword"))
			    	{
			    		if(temp.length > 1)
			    			FTPPassword = temp[1];
			    		else
			    			FTPPassword = "";
			    	}
			    	if(temp[0].equalsIgnoreCase("FTPUpload"))
			    	{
			    		if(temp.length > 1)
			    			FTPUpload = temp[1];
			    		else
			    			FTPUpload = "";
			    	}
			    	if(temp[0].equalsIgnoreCase("FTPDownload"))
			    	{
			    		if(temp.length > 1)
			    			FTPDownload = temp[1];
			    		else
			    			FTPDownload = "";
			    	}
			    }
			}
		} catch (Exception localException)
		{
		}
	}
	
	public static void SaveFile()
	{
		try
		{
			File file = new File("/mnt/sdcard/summons/config/");
			if(!file.exists())
			{
				file.mkdirs();
			}
			//File file = new File("/mnt/sdcard/local/");
			if (file.exists())
			{
				BufferedWriter writer;
			    writer = new BufferedWriter(new FileWriter(new File(file, "device.dat"), false));
			    writer.write(MACAddressText + MACAddress + "\r\n" + DeviceIDText + DeviceID + "\r\n" + DeviceSerialNumberText + DeviceSerialNumber +
			    		"\r\n" + YearNumberText + YearNumber + "\r\n" + FTPServerText + FTPServer + "\r\n" + FTPUserText + FTPUser + 
			    		"\r\n" + FTPPasswordText + FTPPassword + "\r\n" + FTPUploadText + FTPUpload + "\r\n" + FTPDownloadText + FTPDownload);
			    writer.close();
			}
		} catch (Exception localException)
		{

		}
	}
	
	public static void IncrementSerialNumber()
	{
		int serialNumber = 1;

		try {
			serialNumber = Integer.parseInt(DeviceSerialNumber);
		} catch(Exception e) {

		}
		serialNumber++;
		
		DeviceSerialNumber = String.format("%05d", serialNumber);
		SaveFile();
	}
	
	public static void CheckYear()
	{
		Time now = new Time();
		now.setToNow();
		String year = String.valueOf(now.YEAR);
		if(!YearNumber.equalsIgnoreCase(year))
		{
			YearNumber = year;
			DeviceSerialNumber = "00001";
		}
		SaveFile();
	}
	
	/*
    private static final String PREFS_NAME = "DeviceData";
    private static final String bluetoothAddressKey = "PRINTER_BLUETOOTH_ADDRESS";
    private static final String deviceID = "DEVICE_ID";
    private static final String deviceSerialNumber = "DEVICE_SERIAL_NUMBER";

    public static String getDeviceID(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(deviceID, "");
    }

    public static String getDeviceSerialNumber(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(deviceSerialNumber, "");
    }

    public static String getBluetoothAddress(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(bluetoothAddressKey, "");
    }

    public static void saveDeviceID(Context context, String deviceID) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(deviceID, deviceID);
        editor.commit();
    }

    public static void saveDeviceSerialNumber(Context context, String deviceSerialNumber) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(deviceSerialNumber, deviceSerialNumber);
        editor.commit();
    }

    public static void saveBluetoothAddress(Context context, String address) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(bluetoothAddressKey, address);
        editor.commit();
    }
    */
}