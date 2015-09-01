package com.cuccs.dreambox.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class AppFolderPaths {
	
	/**��ȡ��Ŀ¼·��*/
	public static String getRootDir(Context context){
		String RootDirectory;		//��Ӧ�ø�Ŀ¼·��
		final String SDcardPath;
		boolean sdCardExist = Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);		//�ж�SD���Ƿ���� 
		if(sdCardExist){ 
			//mShowToast(context, "��,����SD��һ������");
			SDcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			RootDirectory = SDcardPath+"/DreamBox";
			File dir= new File(RootDirectory);  //������Ŀ¼
			if (!dir.exists()) {
				dir.mkdirs();
			}
	    }else{
	    	mShowToast(context, "��,����Ϊ�λ�û��SD����");
	    	SDcardPath = Environment.getDataDirectory().getAbsolutePath();
	    	RootDirectory = SDcardPath+"/DreamBox";
			File dir= new File(RootDirectory);  //�����ڲ��洢�ĸ�Ŀ¼
			if (!dir.exists()) {
				dir.mkdirs();
			}
	    }
		return RootDirectory;
	}
	
	/**��ȡ�����ļ���·��*/
	public static String getBackupFilesDir(Context context){
		String BackupFilesDir = getRootDir(context) + "/BackupFiles";
		return BackupFilesDir;
	}
	
	public static void mShowToast(Context context, String msg){
		if(context != null){
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
	
}
