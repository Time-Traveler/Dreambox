package com.cuccs.dreambox.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.BCSClientException;
import com.baidu.inf.iis.bcs.model.BCSServiceException;
import com.baidu.inf.iis.bcs.model.ObjectListing;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.model.ObjectSummary;
import com.baidu.inf.iis.bcs.request.ListObjectRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import com.cuccs.dreambox.objects.BaiduCloud_BCS;

/** Nested class that performs progress calculations (counting) */
public class ProgressThread_Backup_cloud extends Thread {
	public static final int MSG_UPLOAD_WHAT = 777;
	public static final int MSG_CLEARCACHE_WHAT = 888;
	public static final int MSG_SUCCESS_WHAT = 999;
	public static final int MSG_FAILED_WHAT = 1000;
	public static final int STATE_DONE = 0;
	public static final int STATE_RUNNING = 1;
	public HashMap<Integer, Boolean> ItemisSelected;
	private int[] iteminfo = new int[6];
	public Handler mHandler;
	private static Context mContext;
	private int mState;


	public ProgressThread_Backup_cloud(Handler h, Context mContext, int[] Iteminfo,
			HashMap<Integer, Boolean> ItemisSelected) {
		mHandler = h;
		ProgressThread_Backup_cloud.mContext = mContext;
		this.iteminfo = Iteminfo;
		this.ItemisSelected = ItemisSelected;
	}

	public void run() {
		Looper.prepare();
		Message msg;
		mState = STATE_RUNNING;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		final String time_str = formatter.format(curDate);
		try {
			for (int i = 0; i < 6; i++) {
				if (ItemisSelected.get(i) == true) {
					switch (i) {
					case 0:
						new B_R_AddressBook(mContext, time_str, mHandler).backupToDB();
						break;
					case 1:
						try {
							new B_R_SMS(mContext, time_str, mHandler).backupToXml();
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case 2:
						new B_R_PhoneCalls(mContext, time_str, mHandler).backupToDB();
						break;
					default:
						break;
					}
				}
			}
			msg = mHandler.obtainMessage();
			msg.what = MSG_UPLOAD_WHAT;
			mHandler.sendMessage(msg);
			
			BCSCredentials credentials = new BCSCredentials(AppAutoConstants.Baidu_BCS.accessKey,
					AppAutoConstants.Baidu_BCS.secretKey);
			BaiduBCS baiduBCS = new BaiduBCS(credentials, AppAutoConstants.Baidu_BCS.host);
			baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
			for (int i = 0; i < 6; i++) {
				String filename = "";
				String dirpath = AppFolderPaths.getBackupFilesDir(mContext) + "/" + time_str;
				
				if (ItemisSelected.get(i) == true) {
					switch (i) {
					case 0:
						filename = "contacts.db";
						break;
					case 1:
						filename = "message.xml";
						break;
					case 2:
						filename = "phonecalls.db";
						break;
					default:
						break;
					}
					
					if("".equals(filename) == false){
						try {
							File f = new File(dirpath + "/" + filename);
							new BaiduCloud_BCS(mContext).putObjectByFile(baiduBCS, time_str, f);
						} catch (BCSServiceException e) {
							AppAutoConstants.Baidu_BCS.log.warn("Bcs return:" + e.getBcsErrorCode() + ", "+ e.getBcsErrorMessage() + ", " +
									"RequestId="+ e.getRequestId());
							msg = mHandler.obtainMessage();
							msg.what = MSG_FAILED_WHAT;
							mHandler.sendMessage(msg);
							return;
						} catch (BCSClientException e) {
							e.printStackTrace();
							msg = mHandler.obtainMessage();
							msg.what = MSG_FAILED_WHAT;
							mHandler.sendMessage(msg);
							return;
						}
					}
				}
				
			}
			
			new LogRecorder_Backup(mContext).addRecord(curDate.getTime(), true, time_str, iteminfo);	//д����־
			new LogRecorder_Operating(mContext).addRecord(curDate.getTime(), "�ƶ˱���", time_str, iteminfo);	//д����־
			//�򿪻򴴽���־backuplog.db���ݿ� �ļ�
			File dbFile=new File("/data/data/com.cuccs.dreambox/databases/" +
						AppAccountInfo.getUsername(mContext) + "_backuplog.db");
			if(dbFile.exists()){
				try {
					//�ϴ���־�ļ�
					new BaiduCloud_BCS(mContext).putObjectByFile(baiduBCS, "Log_backup", dbFile);
					new BaiduCloud_BCS(mContext).listObject_getSpacesize(baiduBCS); //��¼�ƶ����ÿռ�
				} catch (BCSServiceException e) {
					AppAutoConstants.Baidu_BCS.log.warn("Bcs return:" + e.getBcsErrorCode() + ", "+ e.getBcsErrorMessage() + ", " +
							"RequestId="+ e.getRequestId());
				} catch (BCSClientException e) {
					e.printStackTrace();
				}
			}
			msg = mHandler.obtainMessage();
			msg.what = MSG_CLEARCACHE_WHAT;
			mHandler.sendMessage(msg);
			
			deleteFile(AppFolderPaths.getBackupFilesDir(mContext) + "/"
					+ time_str);		//�������ļ�
		} catch (Exception e) {
			e.printStackTrace();
		}
		//��¼����ʱ��
		AppAccountInfo.setlastSDCard_Backup(mContext, curDate.getTime());
		
		msg = mHandler.obtainMessage();
		msg.what = MSG_SUCCESS_WHAT;
		mHandler.sendMessage(msg);
		
		Looper.loop();
	}

	/*
	 * sets the current state for the thread, used to stop the thread
	 */
	public void setState(int state) {
		mState = state;
	}

	public static void deleteFile(String fpath) {
		File file = new File(fpath);
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				deleteFile(childFiles[i].getAbsolutePath());
			}
			file.delete();
		}
	}
	
	
	public static String getPhoneIMSI(){
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