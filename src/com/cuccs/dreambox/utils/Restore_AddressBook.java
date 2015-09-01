package com.cuccs.dreambox.utils;

import java.io.File;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * @author TimeTraveler
 * �����ж���ϵ���Ƿ���ں���  From:    http://bbs.csdn.net/topics/340231857
 */

public class Restore_AddressBook {
	Context context;
	SQLiteDatabase mDB;
	private static final String[] PHONES_PROJECTION = new String[] {
        Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID };
	String DBPath;
	String databaseFilename;
	
	public Restore_AddressBook(Context context) {	//���캯��,��ȡ��ǰcontext
		this.context = context;
		
		File dir = new File(new AppFolderPaths().getRootDir(context));
		String dirname = null;
		for(File f : dir.listFiles()){
			if(f.isDirectory()){
				dirname = f.getName();
			}
		}
		if(!dirname.equals("")){
			DBPath = new AppFolderPaths().getRootDir(context) + "/" + dirname;
		}
	}
	
	// ��ʼ����SD����ȡ���ݿ��ļ�
    private boolean openDatabase() {
        try {
        	databaseFilename = DBPath+"/"+"contact.db";
            File dir = new File(DBPath);
            if (!dir.exists()){
                dir.mkdir();}
            if (!(new File(databaseFilename)).exists()) {
                Toast.makeText(context, "Oops��û�ҵ����ݵ�ͨѶ¼", Toast.LENGTH_SHORT).show();
            }
            mDB = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public void getContactsFromDB(){
    	if(openDatabase()==true){
    		Cursor DBcursor = mDB.rawQuery("select * from addressbook", null);
    		
    		if (DBcursor != null) {
                while (DBcursor.moveToNext()) {
                	// �õ��ֻ�����
                    String phoneNumber = DBcursor.getString(2);
                    // ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    // �õ���ϵ������
                    String contactName = DBcursor.getString(1);
                    //�õ���ϵ��ID
                    Long contactid = DBcursor.getLong(0);
                    
                    if(!this.hasExist(contactName, phoneNumber)){
                    	Log.v("addressbook_Restore_Copyed", contactid+" "+contactName+" "+phoneNumber);
                    	ContentValues values = new ContentValues();
                    	Uri rawContactUri = context.getContentResolver().insert(RawContacts.CONTENT_URI, values);
                    	long rawContactId = ContentUris.parseId(rawContactUri);

                    	values.clear();
                    	values.put(Data.RAW_CONTACT_ID, rawContactId);
                    	values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
                    	values.put(StructuredName.GIVEN_NAME, contactName);
                    	//values.put(StructuredName.FAMILY_NAME, "Mike");
                    	

                    	context.getContentResolver().insert(Data.CONTENT_URI, values);

                    	values.clear();
                    	values.put(Data.RAW_CONTACT_ID, rawContactId);
                    	values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                    	values.put(Phone.NUMBER, phoneNumber);
                    	context.getContentResolver().insert(Data.CONTENT_URI, values);
                    }
                }
            }
    		DBcursor.close();
    	}
    }
    
    public boolean hasExist(String contactName, String PhoneNumber){
    	ContentResolver resolver = context.getContentResolver();
    	Cursor phonesCursor = resolver.query(Phone.CONTENT_URI,
    			PHONES_PROJECTION, ContactsContract.CommonDataKinds.Phone.NUMBER + " = \'" + PhoneNumber + "\'", null, null);//��ѯ���а���content������
        if (phonesCursor != null && phonesCursor.moveToFirst()) {
        	while(phonesCursor.moveToFirst()){
        		Log.e("Restore_AddressBook_hasExist()", phonesCursor.getString(0)+" "+phonesCursor.getString(1));
        		String stringNumber = phonesCursor.getString(1);
        		if(stringNumber.equals(PhoneNumber)){
        			phonesCursor.close();
        			return true;
        		}
        	}
        } 
        phonesCursor.close();
		return false;
    }
}
