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
    private static final String PREFS_NAME = "DeviceData";
    private static final String bluetoothAddressKey = "PRINTER_BLUETOOTH_ADDRESS";
    private static final String deviceID = "DEVICE_ID";
    private static final String deviceSerialNumber = "DEVICE_SERIAL_NUMBER";

	public static String MACAddress = "";
	public static String DeviceID = "ZZ9";
	public static String DeviceSerialNumber = "00001";

	public static void LoadFile(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		MACAddress = settings.getString(bluetoothAddressKey, "");
		//DeviceID = settings.getString(deviceID, "");
		//DeviceSerialNumber = settings.getString(deviceSerialNumber, "");
	}

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

    public static void saveDeviceID(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(deviceID, DeviceID);
        editor.commit();
    }

    public static void saveDeviceSerialNumber(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(deviceSerialNumber, DeviceSerialNumber);
        editor.commit();
    }

    public static void saveBluetoothAddress(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(bluetoothAddressKey, MACAddress);
        editor.commit();
    }
}