package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.widget.Toast;

/**
 * @author TimeTraveler
 * ���ݶ��ţ����ֻ����ŵ�����Ϊxml��ʽ���ļ�
 *From   http://www.open-open.com/lib/view/open1337693444949.html
 */

public class B_R_SMS {
	public static final int MSG_SMS_WHAT = 101;
	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String PROTOCOL = "protocol";
	public static final String READ = "read";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String REPLY_PATH_PRESENT = "reply_path_present";
	public static final String BODY = "body";
	public static final String LOCKED = "locked";
	public static final String ERROR_CODE = "error_code";
	public static final String SEEN = "seen";
	public static String[] projection = new String[] { ADDRESS, PERSON, DATE, PROTOCOL, 
			READ, STATUS,TYPE, REPLY_PATH_PRESENT,
			BODY,LOCKED,ERROR_CODE, SEEN }; // type=1���ռ��䣬==2�Ƿ�����;read=0��ʾδ����read=1��ʾ������seen=0��ʾδ����seen=1��ʾ����
	
	public static final String SMS_URI_ALL = "content://sms/";
	private FileOutputStream outStream = null;
	private XmlSerializer serializer;
	public Handler mHandler;  
	private Context context;
	private String smsPath;
	private String dirname;
	
	public B_R_SMS(Context context, String dirname) {
		this.context = context;
		this.dirname = dirname;
		this.smsPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
	}
	public B_R_SMS(Context context, String dirname, Handler mHandler) {
		this.context = context;
		this.mHandler = mHandler;
		this.dirname = dirname;
		this.smsPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
	}

	public void xmlStart() {

		File file = new File(smsPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File file2 = new File(smsPath, "message.xml");
		try {
			outStream = new FileOutputStream(file2);
			serializer = Xml.newSerializer();
			serializer.setOutput(outStream, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "sms");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**����XML�ļ���Ȼ��д��SMS��Ϣ
	 * */
	public boolean backupToXml() throws Exception {

		this.xmlStart();
		Cursor cursor = null;
		try {
			int counter = 0;
			Message msg = mHandler.obtainMessage();  
    		Bundle b = new Bundle();  
    		
			ContentResolver conResolver = context.getContentResolver();
			Uri uri = Uri.parse(SMS_URI_ALL);
			cursor = conResolver.query(uri, projection, null, null, "_id asc");
			if (cursor.moveToFirst()) {
				// �鿴���ݿ�sms���֪ subject��service_centerʼ����null��������Ͳ���ȡ���ǵ������ˡ�
				String address;
				String person;
				String date;
				String protocol;
				String read;
				String status;
				String type;
				String reply_path_present;
				String body;
				String locked;
				String error_code;
				String seen;
				do {
					// ���address == null��xml�ļ����ǲ������ɸ����Ե�,Ϊ�˱�֤����ʱ�������ܹ���������һһ��Ӧ������Ҫ��֤���е�item��ǵ�����������˳����һ�µ�
					address = cursor.getString(cursor.getColumnIndex(ADDRESS));
					if (address == null) {
						address = "";
					}
					person = cursor.getString(cursor.getColumnIndex(PERSON));
					if (person == null) {
						person = "";
					}
					date = cursor.getString(cursor.getColumnIndex(DATE));
					if (date == null) {
						date = "";
					}
					protocol = cursor.getString(cursor.getColumnIndex(PROTOCOL));
					if (protocol == null) {// Ϊ�˱���xml����
						protocol = "";
					}
					read = cursor.getString(cursor.getColumnIndex(READ));
					if (read == null) {
						read = "";
					}
					status = cursor.getString(cursor.getColumnIndex(STATUS));
					if (status == null) {
						status = "";
					}
					type = cursor.getString(cursor.getColumnIndex(TYPE));
					if (type == null) {
						type = "";
					}
					reply_path_present = cursor.getString(cursor.getColumnIndex(REPLY_PATH_PRESENT));
					if (reply_path_present == null) {// Ϊ�˱���XML����
						reply_path_present = "";
					}
					body = cursor.getString(cursor.getColumnIndex(BODY));
					if (body == null) {
						body = "";
					}
					locked = cursor.getString(cursor.getColumnIndex(LOCKED));
					if (locked == null) {
						locked = "";
					}
					error_code = cursor.getString(cursor.getColumnIndex(ERROR_CODE));
					if (error_code == null) {
						error_code = "";
					}
					seen = cursor.getString(cursor.getColumnIndex(SEEN));
					if (seen == null) {
						seen = "";
					}
					// ����xml�ӱ��
					// ��ʼ���
					serializer.startTag(null, "item");
					// ��������
					serializer.attribute(null, ADDRESS, address);
					serializer.attribute(null, PERSON, person);
					serializer.attribute(null, DATE, date);
					serializer.attribute(null, PROTOCOL, protocol);
					serializer.attribute(null, READ, read);
					serializer.attribute(null, STATUS, status);
					serializer.attribute(null, TYPE, type);
					serializer.attribute(null, REPLY_PATH_PRESENT, reply_path_present);
					serializer.attribute(null, BODY, body);
					serializer.attribute(null, LOCKED, locked);
					serializer.attribute(null, ERROR_CODE, error_code);
					serializer.attribute(null, SEEN, seen);
					// �������
					serializer.endTag(null, "item");
					
					msg = mHandler.obtainMessage();
	        		b.putInt("total", ++counter); 
	        		msg.what = MSG_SMS_WHAT;
	        		msg.setData(b);  
	        		mHandler.sendMessage(msg);
	        		
				} while (cursor.moveToNext());
			} else {
				return false;
			}

		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}
		if(cursor != null) {
			cursor.close();//�ֶ��ر�cursor����ʱ����
		}
		serializer.endTag(null, "sms");
		serializer.endDocument();
		outStream.flush();
		outStream.close();
		return true;
	}
	
	//��XML�ļ��ָ���Ϣ
	public void restoreFromXml(){
		
		XmlPullParser parser = Xml.newPullParser();
		String Xmlfilepath = smsPath + "/message.xml";
		File file = new File(Xmlfilepath);
		if (!file.exists()) {
			Toast.makeText(context, "���ű����ļ�����sd����", Toast.LENGTH_LONG).show();
			new LogRecorder_Backup(context).updateRecord_SMS(dirname, 0);	//���±�����־��Ϣ���������ÿ�
			return;
		}
		try {
			int counter = 0;
			Message msg = mHandler.obtainMessage();  
			Bundle b = new Bundle(); 
			
			FileInputStream fis = new FileInputStream(file);
			parser.setInput(fis, "UTF-8");
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG: // ���������ʼ��ǣ���<smsItems>,<smsItem>��
					if ("item".equals(parser.getName())) {

						// �ж϶������ݿ����Ƿ��Ѱ����������ţ�����У�����Ҫ�ָ�
						ContentResolver resolver = context.getContentResolver();
						Cursor cursor = resolver.query(Uri.parse("content://sms"), new String[] { DATE }, DATE + "=?",
								new String[] { parser.getAttributeValue(2) }, null);
						
						if (!cursor.moveToFirst()) {// û�и�������
							
							ContentValues values = new ContentValues();
							values.put(ADDRESS, parser.getAttributeValue(0));
							// ����ǿ��ַ���˵��ԭ����ֵ��null���������ﻹԭΪnull�������ݿ�
							values.put(PERSON, parser.getAttributeValue(1).equals("") ? null : parser.getAttributeValue(1));
							values.put(DATE, parser.getAttributeValue(2));
							values.put(PROTOCOL, parser.getAttributeValue(3).equals("") ? null : parser.getAttributeValue(3));
							values.put(READ, parser.getAttributeValue(4));
							values.put(STATUS, parser.getAttributeValue(5));
							values.put(TYPE, parser.getAttributeValue(6));
							values.put(REPLY_PATH_PRESENT, parser.getAttributeValue(7).equals("") ? null : parser.getAttributeValue(7));
							values.put(BODY, parser.getAttributeValue(8));
							values.put(LOCKED, parser.getAttributeValue(9));
							values.put(ERROR_CODE, parser.getAttributeValue(10));
							values.put(SEEN, parser.getAttributeValue(11));
							resolver.insert(Uri.parse("content://sms"), values);
						}
						cursor.close();
//						smsItem.setAddress(parser.getAttributeValue(0));
//						smsItem.setPerson(parser.getAttributeValue(1));
//						smsItem.setDate(parser.getAttributeValue(2));
//						smsItem.setProtocol(parser.getAttributeValue(3));
//						smsItem.setRead(parser.getAttributeValue(4));
//						smsItem.setStatus(parser.getAttributeValue(5));
//						smsItem.setType(parser.getAttributeValue(6));
//						smsItem.setReply_path_present(parser.getAttributeValue(7));
//						smsItem.setBody(parser.getAttributeValue(8));
//						smsItem.setLocked(parser.getAttributeValue(9));
//						smsItem.setError_code(parser.getAttributeValue(10));
//						smsItem.setSeen(parser.getAttributeValue(11));
						
						msg = mHandler.obtainMessage();
		        		b.putInt("total", ++counter); 
		        		msg.what = MSG_SMS_WHAT;
		        		msg.setData(b);  
		        		mHandler.sendMessage(msg);
					}
					break;
				case XmlPullParser.END_TAG:// �������,��</smsItems>,</smsItem>��
					break;
				}
				event = parser.next();
			}
		} catch (Exception e) {
			Toast.makeText(context, "���Żָ�����", 1).show();
			e.printStackTrace();
		} 
	}
	
	/**��ö��ŵ�����*/
	public static int getItemQuantity(Context context){
		int quantity = 0;
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse(SMS_URI_ALL);
		Cursor smsCursor = resolver.query(uri, projection, null, null, "_id asc");
		if (smsCursor != null) {
			while (smsCursor.moveToNext()){
				quantity++;
			}
		}
		smsCursor.close();
        return quantity;
	}
}