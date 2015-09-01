package com.cuccs.dreambox.utils;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClearCache {
	private static SQLiteDatabase mLogDB;
	private static Context mContext;
	private static String mFloderPath;
	
	public ClearCache(Context mContext){
		ClearCache.mContext = mContext;
		mFloderPath = AppFolderPaths.getBackupFilesDir(mContext);
	}
	
	public void clear(){
		File mFloder = new File(mFloderPath);
		//�򿪻򴴽�backuplog.db���ݿ�  
		File dbFile = new File("/data/data/com.cuccs.dreambox/databases/"
				+ AppAccountInfo.getUsername(mContext) + "_backuplog.db");
		
		if(!dbFile.exists()){	//������ݿ��ļ���������ֱ�����Ŀ¼
			File[] childFiles = mFloder.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				deleteFile(childFiles[i].getAbsolutePath());
			}
			return;
		}
		
		mLogDB = mContext.openOrCreateDatabase(
				AppAccountInfo.getUsername(mContext) + "_backuplog.db", Context.MODE_PRIVATE, null);
		if(mFloder.isDirectory() == true){
			File[] childFiles = mFloder.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				boolean hasExist = false;
				Cursor cursor = mLogDB.rawQuery("SELECT dirname FROM backuplog", null);  
				
				while (cursor.moveToNext()) {
					String dirname = cursor.getString(cursor.getColumnIndex("dirname"));
					if(childFiles[i].getName().equals(dirname)){
						hasExist = true;
						break;
					}
				}
				if(hasExist == false){
					deleteFile(childFiles[i].getAbsolutePath());	//ɾ�������ļ�
				}
				
			}
		}
		
		//ɾ���°汾��Ϣ�ļ���Ӧ�ð�װ���ļ�
		File versionfile = new File(AppFolderPaths.getRootDir(mContext)+"/NewVersion.xml");
		if(versionfile.exists()){
			versionfile.delete();
		}
		File apkfile = new File(AppFolderPaths.getRootDir(mContext)+"/DreamBox.apk");
		if(apkfile.exists()){
			apkfile.delete();
		}
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
}
