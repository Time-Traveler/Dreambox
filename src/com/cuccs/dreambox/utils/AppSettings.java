package com.cuccs.dreambox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppSettings {
	
	public static boolean OnlyWiFi;
	public static boolean SoundOn;
	
	
	public static void readSettings(Context mContext){	//��ȡ����
		SharedPreferences mSpreferences = mContext.getSharedPreferences("App_settings",0);
		OnlyWiFi = mSpreferences.getBoolean("onlywifi", false);
		SoundOn = mSpreferences.getBoolean("soundon", false);
	}
	
	public static void writeDefaultSetup(Context mContext){	//Ӧ�õ�һ�ΰ�װʱд�� Ĭ������
		SharedPreferences mSpreferences = mContext.getSharedPreferences("App_settings",0);
		Editor editor = mSpreferences.edit();        
        editor.putBoolean("onlywifi", true);	//��������
        editor.putBoolean("soundon", true);
        editor.commit();	//�ύ�޸�
        
        OnlyWiFi = mSpreferences.getBoolean("onlywifi", true);
		SoundOn = mSpreferences.getBoolean("soundon", true);
	}
	
	public static void setOnlyWifi(Context mContext, boolean mOnlywifi){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("App_settings",0);
		Editor editor = mSpreferences.edit();        
        editor.putBoolean("onlywifi", mOnlywifi);	//��������
        editor.commit();	//�ύ�޸�
        
        OnlyWiFi = mOnlywifi;
	}
	public static void setSoundOn(Context mContext, boolean mSoundOn){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("App_settings",0);
		Editor editor = mSpreferences.edit();        
        editor.putBoolean("soundon", mSoundOn);	//��������
        editor.commit();	//�ύ�޸�
        
        SoundOn = mSoundOn;
	}
	
}
