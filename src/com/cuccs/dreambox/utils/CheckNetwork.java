package com.cuccs.dreambox.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class CheckNetwork {
	private Context context;

	public CheckNetwork(Context context){
	        this.context = context;
	}

	
	 public boolean isConnectingToInternet(){
		 ConnectivityManager connectivity = (ConnectivityManager)
				 context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo[] info = connectivity.getAllNetworkInfo();
		 if (info != null){
			 for (int i = 0; i < info.length; i++){
				 Log.v("info."+i, info[i].getTypeName());
				 if (info[i].getState() == NetworkInfo.State.CONNECTED)
				 {
					 return true;
				 }
			 }
		 }
		 Toast.makeText(context, "��ѽ��������û������", Toast.LENGTH_SHORT).show();
	 return false;
	 }
	 
	 public boolean isWiFiConnected(){		//�жϵ�ǰ��������Ƿ���  WiFi����
		 ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 boolean wifiOpen = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		 if(wifiOpen == false){
			 Toast.makeText(context, "�������˽� WiFi�»�ȡ����.\n�����Ҫ, �������������и���", Toast.LENGTH_SHORT).show();
			 return false;
		 }
		 String state = connectivity.getActiveNetworkInfo().getTypeName();
		 if(!state.equals("")&&state.equals("WIFI")){
			 return true;
		 }else{
			 Toast.makeText(context, "������ʹ�õ����Ӳ��� WiFi", Toast.LENGTH_SHORT).show();
			 return false;
		 }
	 }
}
