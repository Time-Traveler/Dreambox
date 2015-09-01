package com.cuccs.dreambox.utils;

/**
 * �˰汾Ϊ�û����½�棬�˺�ֱ��ʹ���û����ֻ�SIM��IMSI����Ϊ�ƶ˱����ʺ�
 * ȫ�ֶ���ȥ��¼�����ע�����
 * �Ķ���Ҫ������AppAccountInfo��Content_Account��
 * 
 * AppAccountInfo 
 * 			-- getisLoginSuccess()���Ƿ���true;
 * 			-- getUsername()ֱ�ӷ���IMSI��;
 */

import com.cuccs.dreambox.LoginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class AppAccountInfo {
	
	public static boolean isLoginSuccess;	//�Ƿ��½�ɹ�
	public static long lastloginTime;	//����¼ʱ��
	
	public	static String username;
	public	static String userId;
	public	static String sessionID;
	
	public static boolean getisLoginSuccess(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        //return isLoginSuccess = mSpreferences.getBoolean("isLoginSuccess", false);
        return isLoginSuccess = mSpreferences.getBoolean("isLoginSuccess", true);
	}
	public static long getlastloginTime(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        return lastloginTime = mSpreferences.getLong("isLoginSuccess", 0);
	}
	public static String getUsername(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        username = mSpreferences.getString("username", null);
		/*if(username == null){
			Toast.makeText(mContext, "����δ��¼Ӧ��", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(mContext, LoginActivity.class);  
			if(!mContext.getClass().getName().equals("LoginActivity")){
				mContext.startActivity(intent);
				((Activity)mContext).finish();
			}
		}
		return username; */
		return getPhoneIMSI(mContext);
	}
	public static String getUserId(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        return userId = mSpreferences.getString("userId", null);
	}
	public static String getSessionID(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        return sessionID = mSpreferences.getString("sessionID", null);
	}
	
	public static void setisLoginSuccess(Context mContext, boolean IsLoginSuccess){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putBoolean("isLoginSuccess", IsLoginSuccess);	//��������
        editor.commit();	//�ύ�޸�
        
        isLoginSuccess = IsLoginSuccess;
	}
	
	public static void setlastloginTime(Context mContext, long LastloginTime){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putLong("lastloginTime", LastloginTime);	//��������
        editor.commit();	//�ύ�޸�
        
        lastloginTime = LastloginTime;
	}
	
	public static void setUsername(Context mContext, String userName){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putString("username", userName);	//��������
        editor.commit();	//�ύ�޸�
        
        username = userName;
	}
	
	public static void setUserId(Context mContext, String userid){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putString("userId", userid);	//��������
        editor.commit();	//�ύ�޸�
        
        userId = userid;
	}
	
	public static void setSessionID(Context mContext, String sessionid){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putString("sessionID", sessionid);	//��������
        editor.commit();	//�ύ�޸�
        
        sessionID = sessionid;
	}
	
	//��¼�ϴα���ʱ��
	public static void setlastSDCard_Backup(Context mContext, long time){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Backup_info",0);
		Editor editor = mSpreferences.edit();        
        editor.putLong("lastSDCard_Backup", time);	//��������
        editor.commit();	//�ύ�޸�
	}
	public static void setlastCloud_Backup(Context mContext, long time){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Backup_info",0);
		Editor editor = mSpreferences.edit();        
        editor.putLong("lastCloud_Backup", time);	//��������
        editor.commit();	//�ύ�޸�
	}
	
	
	
	public static String getPhoneIMSI(Context mContext){
		TelephonyManager telephonyManager = (TelephonyManager)mContext  
	                .getSystemService(Context.TELEPHONY_SERVICE); 
		//��ȡ�ֻ�Ψһ�ı�ʶ
		String IMEI =((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		String mProvidersName = null;  
		// ����Ψһ���û�ID;�������ſ��ı�������  
        String IMSI = telephonyManager.getSubscriberId();
        String number = telephonyManager.getLine1Number();
        // IMSI��ǰ��3λ460�ǹ��ң������ź���2λ00 02���й��ƶ���01���й���ͨ��03���й����š�  
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {  
            mProvidersName = "�й��ƶ�";  
        } else if (IMSI.startsWith("46001")) {  
            mProvidersName = "�й���ͨ";  
        } else if (IMSI.startsWith("46003")) {  
            mProvidersName = "�й�����";  
        } 
        //Log.v("=-=-============", IMSI+" ,"+mProvidersName+"--->"+number+" ,imei:"+IMEI);
		return IMSI;  
	}
}
