package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import android.widget.Toast;

public class Restore_SMS {
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
	
	private Context context;
	private ContentResolver conResolver;
	String smsPath;
	
	public Restore_SMS(Context context) {
		this.context = context;
		conResolver = context.getContentResolver();
		
		File dir = new File(new AppFolderPaths().getRootDir(context));
		String dirname = null;
		for(File f : dir.listFiles()){
			if(f.isDirectory()){
				dirname = f.getName();
			}
		}
		if(!dirname.equals("")){
			smsPath = new AppFolderPaths().getRootDir(context) + "/BackupFiles/" + dirname;
		}
	}
	
	public void getSMSFromXml() {
		XmlPullParser parser = Xml.newPullParser();
		String smsfilePath = smsPath + "/message.xml";
		File file = new File(smsfilePath);
		if (!file.exists()) {
			Toast.makeText(context, "message.xml���ű����ļ�����sd����", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
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
						Cursor cursor = conResolver.query(Uri.parse("content://sms"), new String[] { DATE }, DATE + "=?",
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
							conResolver.insert(Uri.parse("content://sms"), values);
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

					}
					break;
				case XmlPullParser.END_TAG:// �������,��</smsItems>,</smsItem>��
					if ("item".equals(parser.getName())) {
					}
					break;
				}
				event = parser.next();
			}
		} catch (Exception e) {
			Toast.makeText(context, "���Żָ�����", 1).show();
			e.printStackTrace();
		} 
		
	}
}
