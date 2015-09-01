package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class B_R_AddressBook {
	public static final int MSG_ADDRESSBOOK_WHAT = 100;
	public Handler mHandler;  
	public String dirname;
	public static Context mContext;
	private static SQLiteDatabase mDB;
	private static final String[] PHONES_PROJECTION = new String[] {
        Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID };
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;		/*��ϵ����ʾ����*/    
    private static final int PHONES_NUMBER_INDEX = 1;		/*�绰����*/     
    private static final int PHONES_CONTACT_ID_INDEX = 2;		/*��ϵ�˵�ID*/
    String DBPath;
    
	public B_R_AddressBook(Context context, String dirname) {	//���캯��,��ȡ��ǰcontext
			B_R_AddressBook.mContext = context;
			this.dirname = dirname;
			this.DBPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
	}
	public B_R_AddressBook(Context context, String dirname, Handler mHandler) {
		B_R_AddressBook.mContext = context;
		this.mHandler = mHandler;
		this.dirname = dirname;
		this.DBPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
}
	
	public boolean creatDBfile(){
		 File dbp=new File(DBPath);
		 File dbf=new File(DBPath+"/"+"contacts.db");
	        
		 if(!dbp.exists()){
			 dbp.mkdir();
		}
		//���ݿ��ļ��Ƿ񴴽��ɹ�
         boolean isFileCreateSuccess=false; 
         if(dbf.exists()){
        	 dbf.delete();
        }
         try{                 
            isFileCreateSuccess = dbf.createNewFile();
         }catch(IOException ioex){
        	 Log.e("B_R_AddressBook", "���ݿ��ļ�����ʧ�ܣ���");
        	 return false;
         }
         
         if(isFileCreateSuccess){
        	//���������ݿ��ļ�Ĭ�Ϸ���ϵͳ��,��ô�������ݿ�mysql���²�����
        	//SQLiteDatabase mysql = myOpenHelper.getWritableDatabase(); // ʵ�����ݿ�
        	//�����ʹ�õ��ǽ����ݿ���ļ�������SD���У���ô�������ݿ�mysql���²�����
        	mDB = SQLiteDatabase.openOrCreateDatabase(dbf, null);
        	
        	String CREATE_TABLE="CREATE TABLE addressbook(cid varchar, cname varchar, cnumber varchar)";		//������
        	mDB.execSQL(CREATE_TABLE);
        }
         return true;
   }
	
	
	public void backupToDB(){
		if(creatDBfile()==true){	//�ȴ������ݿ��ļ�
			int counter = 0;
			Message msg = mHandler.obtainMessage();  
    		Bundle b = new Bundle();  
    		
			ContentResolver resolver = mContext.getContentResolver();
	        // ��ȡ�ֻ���ϵ��
	        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, 
	        		android.provider.ContactsContract.Contacts._ID + " ASC"); 
	        Log.e("phoneCursor.getCount()", phoneCursor.getCount()+"");
	        if (phoneCursor != null) {
	            while (phoneCursor.moveToNext()) {

	                //�õ��ֻ�����
	                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
	                //���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
	                if (TextUtils.isEmpty(phoneNumber))
	                    continue;
	                
	                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);	//�õ���ϵ������
	                String contactid = phoneCursor.getString(PHONES_CONTACT_ID_INDEX);	//�õ���ϵ��ID
	                Log.i("addressbook", contactid+" "+contactName+" "+phoneNumber);
	                
	                String INSERT_DATA = "INSERT INTO addressbook(cid, cname, cnumber) " +
	                		"VALUES("+contactid+",\" "+contactName+"\", '"+phoneNumber+"')";	//ע��˴���  \" ����������˫����
	                mDB.execSQL(INSERT_DATA);
	                
	                msg = mHandler.obtainMessage();
	        		b.putInt("total", ++counter); 
	        		msg.what = MSG_ADDRESSBOOK_WHAT;
	        		msg.setData(b);  
	        		mHandler.sendMessage(msg);
	            }
	        }
	        phoneCursor.close();
	        mDB.close();
		};
	}
	
	/**
	 *��DataBase�ļ��ָ���ϵ�˵��ֻ�
	 */
	public void restoreFromDB(){
		String DBfilepath = DBPath+"/"+"contacts.db";
		Log.v("DBfilepath: ", DBfilepath);
		int counter = 0;
		Message msg = mHandler.obtainMessage();  
		Bundle b = new Bundle(); 
		
		//�򿪻򴴽�test.db���ݿ�  
		File dbFile=new File(DBfilepath);
		if(!dbFile.exists()){
			Toast.makeText(mContext, "��ϵ�˱����ļ�δ�ҵ�", Toast.LENGTH_LONG).show();
			new LogRecorder_Backup(mContext).updateRecord_Contacts(dirname, 0);	//���±�����־��Ϣ������ϵ���ÿ�
			return;
		}
		SQLiteDatabase restore_db = mContext.openOrCreateDatabase(DBfilepath, Context.MODE_PRIVATE, null);
		Cursor cursor_from = restore_db.rawQuery("SELECT * FROM addressbook", null);  
		Log.e("getCount", cursor_from.getCount()+"");
		
		while (cursor_from.moveToNext()) {
			String mName = cursor_from.getString(cursor_from.getColumnIndex("cname"));
			String mNumber = cursor_from.getString(cursor_from.getColumnIndex("cnumber"));
			
			ContentResolver resolver = mContext.getContentResolver();
			// ��ȡ�ֻ���ϵ��
            Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null,
            		android.provider.ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            Log.e("getCount", phoneCursor.getCount()+"");
            if (phoneCursor != null) {
            	boolean hasExist = false;
                while (phoneCursor.moveToNext()) {
                	//�õ��ֻ�����
                    String phoneNumber = phoneCursor.getString(1);
                    if(phoneNumber.equals(mNumber)){
                    	hasExist = true;
                    	break;
                    }
                }
                if(hasExist == false){
                	this.insertContacts(mName, mNumber, null);
                	Log.i("B_R_AddressBook_lines141", mName+" "+mNumber);
                }
            }
            phoneCursor.close();
            
            msg = mHandler.obtainMessage();
    		b.putInt("total", ++counter); 
    		msg.what = MSG_ADDRESSBOOK_WHAT;
    		msg.setData(b);  
    		mHandler.sendMessage(msg);
		}
		cursor_from.close();
		restore_db.close();	//�ر����ݿ�
	}
	
	private void insertContacts(String name, String phoneNo, String email) {
		
		/** From:   http://blog.csdn.net/fly413413/article/details/7763436  */
	    //����һ���յ�ContentValues
		ContentValues  values=new ContentValues();
		//��rawcontent��content����uriִ��һ����ֵ����	
		//Ŀ���ǻ�ȡϵͳ���ص�raw_contact_id		
		Uri  rawcontacturi = mContext.getContentResolver().insert(RawContacts.CONTENT_URI, values);
		long  rawcontactid = ContentUris.parseId(rawcontacturi);	
		values.clear();		
		values.put(Data.RAW_CONTACT_ID, rawcontactid);
		//������������		
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);	  // mimitype_id �ֶΣ��������������ݵ����ͣ��绰���룿Email��....	
		//������ϵ������
		values.put(StructuredName.GIVEN_NAME, name);
		//����ϵ��URI�����ϵ������
		mContext.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
		
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawcontactid);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		//������ϵ�˵绰����
		values.put(Phone.NUMBER, phoneNo);
		//���õ绰����		
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);		
		//����ϵ�˵绰����URI��ӵ绰����		
		mContext.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);		
		
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawcontactid);
		values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
		//������ϵ��email�ĵ�ַ
		values.put(Email.DATA, email);		
		//���øĵ����ʼ�����
		values.put(Email.TYPE, Email.TYPE_WORK);		
		//����ϵ��email  URI��� email����
		mContext.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);		
	}
	
	/**�����ϵ�˵ĸ���*/
	public static int getItemQuantity(Context context){
		int quantity = 0;
		ContentResolver resolver = context.getContentResolver();
        // ��ȡ�ֻ���ϵ��
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, 
        		android.provider.ContactsContract.Contacts._ID + " ASC"); 
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
            	quantity++;
            }
        }
        phoneCursor.close();
        return quantity;
	}
}
