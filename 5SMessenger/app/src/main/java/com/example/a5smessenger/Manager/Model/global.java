package com.example.a5smessenger.Manager.Model;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.speech.tts.TextToSpeech.OnInitListener;
import androidx.multidex.MultiDexApplication;

import java.util.ArrayList;
import java.util.List;


public class global extends MultiDexApplication implements OnInitListener {


	public static String NamePACE= "http://FPIT.5stars.com.vn/";
	public static String SOAP_Action= "http://FPIT.5stars.com.vn/";
    public static String NAME_SPACE= "http://FPIT.5stars.com.vn/";

    public static String SPLIT_KEY_1=";";
	public static String SPLIT_KEY_2 = "=";
	public static String URL_Messenger="URL_Messenge";
	public static String TOKEN_KEY="TOKEN_KEY";
	public static String Synch = "0";
	public static List<String> lstLogUp = new ArrayList<>();
	public static ArrayList<String> arrPackageAllow = null;

	private static Boolean AllowSynchroinzeBy3G = true;
	private static String UrlWebserviceToSynchronize = "https://5smsgr.5stars.com.vn:7443/WebService/P5sWebService.asmx";

	private  static String onNotification ="1"; // mặc định khi vào app sẽ không hiện thông báo danh sách app.

	private static Context appContext;

	public static String RegexReplaceStringNonASCII = "[^\\x00-\\x7F]";


	//Bien kiem tra khi nhan Uninstall
	public  static int isUninstall =1;

	@Override
	public void onInit(int status) {

	}
	@Override
	public void onCreate() {
		super.onCreate();
		appContext = getApplicationContext();
	}
	public static Context getAppContext() {
		return appContext;
	}

	public static void setAppContext(Context appContext) {
		global.appContext = appContext;
	}
	public static String getUrlWebserviceToSynchronize() {
		return UrlWebserviceToSynchronize;
	}
	public static Boolean getAllowSynchroinzeBy3G() {
		return AllowSynchroinzeBy3G;
	}

	public  static  void set_onNotification(String value)
	{
		onNotification = value;
	}
	public  static String get_onNotification()
	{
		return onNotification;
	}

	public static int getScreenWidth() {
		return Resources.getSystem().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeight() {
		return Resources.getSystem().getDisplayMetrics().heightPixels;
	}

	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	public static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

}
