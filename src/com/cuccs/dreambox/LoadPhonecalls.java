package com.cuccs.dreambox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class LoadPhonecalls extends Activity{
	/**��ȡ��PhoneCalls���ֶ�**/
    private static final String[] CALLS_PROJECTION = new String[] {
    			Calls.DURATION, Calls.TYPE, Calls.DATE, Calls.NUMBER,Calls._ID};
    
    private ArrayList<Integer> mCallsType = new ArrayList<Integer>();		/*ͨ�����ͣ�����:1,����:2,δ��:3**/  
    private ArrayList<String> mCallsName = new ArrayList<String>();		/*��ϵ������*/
    private ArrayList<String> mCallsNumber = new ArrayList<String>();		/*��ϵ�˺���**/ 
    private ArrayList<String> mCallsDate = new ArrayList<String>();		/*ͨ������**/  
    private ArrayList<Long> mCallsDuration = new ArrayList<Long>();			/*ͨ��ʱ��**/  
    private ArrayList<Long> mCallsId = new ArrayList<Long>();			/*��ϵ��ID*/
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadphonecalls);
		
		/*�õ��ֻ�ͨ����¼**/
        getPhoneCalls();
        ListView mCallsList = (ListView)findViewById(R.id.loadphonecalls_listview);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();		//����list
		
		for(int i=0;i<mCallsNumber.size();i++){
			Map<String,Object> maps = new HashMap<String, Object>();			//ʵ����map����
			if(mCallsType.get(i) == Calls.INCOMING_TYPE){
				maps.put("type", R.drawable.icon_phonecall_incoming);
			}else if(mCallsType.get(i) == Calls.OUTGOING_TYPE){
				maps.put("type", R.drawable.icon_phonecall_outcoming);
			}else{
				maps.put("type", R.drawable.icon_phonecall_missed);
			}
			maps.put("name", mCallsName.get(i).toString());
			maps.put("duration", "ͨ��ʱ��: "+mCallsDuration.get(i).toString()+"��");
			maps.put("date", mCallsDate.get(i).toString());
			listItems.add(maps);
		}
		//�˴�String[]��˳��Ӧ�ú�maps�е����˳��һ�£�����listviewƥ����ʾ�����
		SimpleAdapter adapter = new SimpleAdapter(this,listItems,
				R.layout.loadphonecalls_items,new String[]{"type","name","duration","date"},
				new int[]{R.id.loadphonecalls_type,R.id.loadphonecalls_name,R.id.loadphonecalls_duration,R.id.loadphonecalls_date});
		mCallsList.setAdapter(adapter);
	}
	
	/**�õ��ֻ�ͨ����¼��Ϣ**/
    private void getPhoneCalls() {
    	ContentResolver resolver = this.getContentResolver();
        Cursor callsCursor = resolver.query(Calls.CONTENT_URI,CALLS_PROJECTION, 
        		null, null, Calls.DEFAULT_SORT_ORDER);
        if (callsCursor != null) {
        	 while (callsCursor.moveToNext()) {
        		int type = callsCursor.getInt(callsCursor.getColumnIndex(Calls.TYPE));  
 			    long duration = callsCursor.getLong(callsCursor.getColumnIndex(Calls.DURATION));  
 			    String phonenumber = callsCursor.getString(callsCursor.getColumnIndex(Calls.NUMBER));
 			    long _id = callsCursor.getLong(callsCursor.getColumnIndex(Calls._ID)); 
 			    long _date = callsCursor.getLong(callsCursor.getColumnIndex(Calls.DATE));
 			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy��MM��dd�� HH:mm");
 			    String date = dateFormat.format(_date);
 			    /*��ȡͨ����ϵ�� ����*/
  				Uri personUri = Uri.withAppendedPath(  
  			            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phonenumber);  
  			    Cursor cur = this.getContentResolver().query(personUri,  
  			            new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null );  
  			    if( cur.moveToFirst() ) {  
  			        int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);  
  			        mCallsName.add(cur.getString(nameIdx));
  			    } 
  			    else{
  			    	mCallsName.add(phonenumber); 
  			    }
  			    cur.close();  
  			    
 			     mCallsType.add(type);
 			     mCallsNumber.add(phonenumber);
 			     mCallsDate.add(date);
 			     mCallsDuration.add(duration);
 			     mCallsId.add(_id);
 			    //Log.v("LoadPhonecalls_lines53", _id+" "+type+" "+phonenumber+" "+date+" "+duration+" ");
        	 }
        }
    }
}
