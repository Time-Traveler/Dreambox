package com.cuccs.dreambox;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author TimeTraveler
 * ��ȡ�ֻ��������ݲ���ʾ��listview
 * ��ҪȨ�� <uses-permission android:name="android.permission.READ_SMS" />
 * From:  
 */

public class LoadShortMessage extends Activity{
	String[] SMS_PROJECTION = new String[] { 
		     "_id",  
		     "thread_id",
		     "address",  
		     "person", 
		     "date",
		     "body",
		     "type" 
		    };  
	private ArrayList<String> mPersonName = new ArrayList<String>();		/*��ϵ������**/        
    private ArrayList<String> mAddress = new ArrayList<String>();		/*��ϵ�˺���**/ 
    private ArrayList<String> mDate = new ArrayList<String>(); 
    private ArrayList<String> mBody = new ArrayList<String>();    
       
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadshortmessage);
		
		/*�õ��ֻ����ж���*/
		getAllShortMessages();
		ListView listView = (ListView)findViewById(R.id.loadshortmessage_listview);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();		//����list
		
		for(int i=0;i<mAddress.size();i++){
			Map<String,Object> maps = new HashMap<String, Object>();			//ʵ����map����
			if((mPersonName.get(i)).equals(mAddress.get(i))){
				maps.put("nums", "");
			}else{
				maps.put("nums", mAddress.get(i));
			}
			maps.put("title", mPersonName.get(i));
			maps.put("date", mDate.get(i));
			maps.put("body", mBody.get(i));
			listItems.add(maps);
		}
		SimpleAdapter adapter = new SimpleAdapter(this,listItems,
				R.layout.loadshortmessage_items,new String[]{"title","body","date"},
				new int[]{R.id.loadshortmessage_titles,R.id.loadshortmessage_bodys,R.id.loadshortmessage_date});
		listView.setAdapter(adapter);
	}
	
	
	
	/**�õ��ֻ����ж�����Ϣ**/
    private void getAllShortMessages() {
    	final String SMS_URI_ALL = "content://sms/";	/*���еĶ��� */
    	final String SMS_URI_INBOX = "content://sms/inbox";		/* �ռ������*/
    	final String SMS_URI_SEND = "content://sms/sent";
    	final String SMS_URI_DRAFT = "content://sms/draft";		/*�ݸ������*/
    	
    	ContentResolver conResolver = this.getContentResolver();
    	Uri uri = Uri.parse(SMS_URI_ALL);
    	Cursor cusor = this.managedQuery(uri, SMS_PROJECTION, null, null,
    			"date desc");

    	int threadIdColumn = cusor.getColumnIndex("thread_id");
    	int phoneNumberColumn = cusor.getColumnIndex("address");
    	int dateColumn = cusor.getColumnIndex("date");
    	int smsbodyColumn = cusor.getColumnIndex("body");
    	int typeColumn = cusor.getColumnIndex("type");

    	if (cusor != null) {
			while (cusor.moveToNext()) {
				/*��ȡ���ŷ���������
				 * From   http://kevinlynx.iteye.com/blog/845920 */
				Uri personUri = Uri.withAppendedPath(  
			            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, cusor.getString(phoneNumberColumn));  
			    Cursor cur = this.getContentResolver().query(personUri,  
			            new String[] { PhoneLookup.DISPLAY_NAME },  
			            null, null, null ); 
			    int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			    if( cur.moveToFirst() ) {  
			        mPersonName.add(cur.getString(nameIdx)); 
			    }else{
			    	mPersonName.add(cusor.getString(phoneNumberColumn)); 
			    }
			    cur.close(); 
				//mPersonName.add(cusor.getString(nameColumn));
			    mAddress.add(cusor.getString(phoneNumberColumn));
				mDate.add(strToDateLong(cusor.getLong(dateColumn)));
				mBody.add(cusor.getString(smsbodyColumn));
				
			}
			cusor.close();
		}
    }
    /**
    * ��ʱ���ʽ��long����ת����Date����
    * @param strDate
    */
    public static String strToDateLong(Long strDate) {
    	SimpleDateFormat sdf= new SimpleDateFormat("MM��dd��");
    	java.util.Date dt = new Date(strDate);  
    	String sDateTime = sdf.format(dt);  //�õ���ȷ����ı�ʾ��08/31/2006 21:08:00
		return sDateTime;
    }
}
