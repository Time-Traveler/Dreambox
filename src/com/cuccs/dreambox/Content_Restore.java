package com.cuccs.dreambox;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.BCSClientException;
import com.baidu.inf.iis.bcs.model.BCSServiceException;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.cuccs.dreambox.layouts.MyRefreshListView;
import com.cuccs.dreambox.objects.CardData_restore;
import com.cuccs.dreambox.objects.CustomListDialog;
import com.cuccs.dreambox.objects.myOnRefreshListener;
import com.cuccs.dreambox.utils.AppAccountInfo;
import com.cuccs.dreambox.utils.AppAutoConstants;
import com.cuccs.dreambox.utils.AppFolderPaths;
import com.cuccs.dreambox.utils.LogRecorder_Backup;
import com.cuccs.dreambox.utils.LogRecorder_Operating;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Content_Restore extends Activity{
	public Button Btn_Back;
	public Context mContext;
	public ImageView mNothingView;
	public int cardsNum = 0;
	public boolean hasFinished = false;
	public int SQL_offset=0, SQL_limit=8;
	HashMap<Integer, Boolean> ItemisSelected;
	SimpleAdapter adapter;
	MyRefreshListView mRestoreList;
	public int[] colorId = new int[]{R.color.card_green,R.color.card_bisque,R.color.card_red,R.color.card_purple, R.color.card_blue};
	ArrayList<CardData_restore> mCardsData = new ArrayList<CardData_restore>();
	List<Map<String,Object>> listItemsData = new ArrayList<Map<String,Object>>();		//����list����
	
	final Handler mhandler = new Handler() {  
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        		case 100:
        			adapter.notifyDataSetChanged();
					mRestoreList.onRefreshFinish();
					break;
        	}
        	super.handleMessage(msg);
        }  
    };   
    
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_restore);
		mContext = this.getApplicationContext();
		
		hasFinished = false;
		cardsNum = getCardsData();
		mRestoreList = (MyRefreshListView)findViewById(R.id.restore_listview);
		mNothingView = (ImageView)findViewById(R.id.restore_there_is_nothing);
		
		if(cardsNum == 0){
			mNothingView.setVisibility(View.VISIBLE);
			mRestoreList.setVisibility(View.GONE);
			Toast.makeText(getApplicationContext(), "����û�б��ݹ��ֻ����ݰ�", Toast.LENGTH_SHORT).show();
		}
		for(int i=0;i<mCardsData.size();i++){
			Map<String,Object> map = new HashMap<String, Object>();			//ʵ����map����
			map.put("markcolor", colorId[i%colorId.length]);
			map.put("title", (i+1)+":  "+mCardsData.get(i).mTitle);
			map.put("content", mCardsData.get(i).mContent);
			if(mCardsData.get(i).mType.equals("true")){
				map.put("tag", "�ƶ˻ָ�");
			}else{
				map.put("tag", "SD���ָ�");
			}
			listItemsData.add(map);
		}
		adapter = new SimpleAdapter(this,listItemsData,
				R.layout.content_restore_items,new String[]{"markcolor","title","content","tag"},
				new int[]{R.id.restore_listview_items_markcolor,R.id.restore_listview_items_title,
				R.id.restore_listview_items_content1,
				R.id.restore_listview_items_tag});
		mRestoreList.setAdapter(adapter);
		mRestoreList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					   long id) {
				// TODO Auto-generated method stub
				CustomListDialog mDialog = null;
				ItemisSelected = new HashMap<Integer, Boolean>();
				for(int i=0;i<6;i++){
					if(mCardsData.get(position-1).mContentInfo[i] != 0){
						ItemisSelected.put(i, true);
					}else{
						ItemisSelected.put(i, false);
					}
				}
				if(mCardsData.get(position-1).mType.equals("true")){	//�ƶ˴洢�ı����ļ�
					mDialog = new CustomListDialog(Content_Restore.this, 
							R.style.Theme_ShareDialog, ItemisSelected, true);
				}else{
					mDialog = new CustomListDialog(Content_Restore.this, 	// ����SD���洢
							R.style.Theme_ShareDialog, ItemisSelected, false);
				}
        		mDialog.setUseType(false);	//ָ��ִ�лָ�����,�����Ǳ���
        		mDialog.setParentDir(mCardsData.get(position-1).mParentDir);
        		mDialog.deliverItemInfo(mCardsData.get(position-1).mContentInfo);	//ÿһ���������
        		mDialog.show();
        		mDialog.setCancelable(false);  //����Ի�����಻�ر�д��
        		mDialog.setIcon(R.drawable.icon_mobile);
        		//mDialog.setTitle("��SD���лָ�");
			}
		});
		mRestoreList.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
					   long id) {
				final int posi = position - 1;
				new AlertDialog.Builder(Content_Restore.this)
			    .setTitle("�� ʾ")
			    .setIcon(R.drawable.clear_cache_normal)
			    .setMessage("    ��ȷ��ɾ������������ ")
			    .setPositiveButton("ȡ ��",new DialogInterface.OnClickListener() {	//���ظó����Activity
			    	public void onClick(DialogInterface dialog, int which) {}
			    })
			    .setNegativeButton("ɾ ��", new DialogInterface.OnClickListener() {
			     @Override
			     public void onClick(DialogInterface dialog, int which) {
			    	 // TODO Auto-generated method stub
					 delectRecordBackup(mCardsData.get(posi).mDate, mCardsData.get(posi).mParentDir, 
								mCardsData.get(posi).mType, mCardsData.get(posi).mContentInfo);
					 SQL_offset = SQL_offset - 1;
					 listItemsData.remove(posi);
					 mCardsData.remove(posi);
					 adapter.notifyDataSetChanged();
			     }
			    }).show();
				
				return true;
			}
		});
		mRestoreList.setOnRefreshListener(new myOnRefreshListener() {
			@Override
			public void onRefresh() {	//����ˢ���¼�
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						SystemClock.sleep(500);
						return null;
					}
					@Override
					protected void onPostExecute(Void result) {
						Message message = new Message(); 
		                message.what = 100 ;
		                mhandler.sendMessage(message);              //�����̷߳���Message����UI���� 
					}
				}.execute(new Void[]{});
			}
			@Override
			public void onLoadMore() {		//���ظ����¼�

				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						cardsNum = getCardsData();
						if(cardsNum == 0){
							SystemClock.sleep(500);
							return null;
						}
						int idStart = SQL_offset - SQL_limit;
						for(int i=0;i<cardsNum;i++){
							Map<String,Object> map = new HashMap<String, Object>();			//ʵ����map����
							map.put("markcolor", colorId[(idStart+i)%colorId.length]);
							map.put("title", (idStart+i+1)+":  "+mCardsData.get(idStart+i).mTitle);
							map.put("content", mCardsData.get(idStart+i).mContent);
							if(mCardsData.get(idStart+i).mType.equals("true")){
								map.put("tag", "�ƶ˻ָ�");
							}else{
								map.put("tag", "SD���ָ�");
							}
							listItemsData.add(map);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						Message message = new Message(); 
		                message.what = 100 ;
		                mhandler.sendMessage(message);              //�����̷߳���Message����UI���� 
					}
				}.execute(new Void[]{});
			}});
		
		
		mNothingView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Content_Restore.this, Content_BackUp.class);  
    	        startActivity(intent);
                overridePendingTransition(R.anim.contend_enlarge_ld, R.anim.translate_right_exit);
                finish();
			}
		});
		Btn_Back = (Button) findViewById(R.id.restore_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	finish();
                overridePendingTransition(R.anim.holding_anima, R.anim.translate_right_exit);
            }  
        });
	}
	
	/**
	 * ��ȡ��Ƭ����
	 */
	@SuppressLint("SdCardPath")
	public int getCardsData(){
		int nums = 0;
		if(hasFinished == true){
			return nums;
		}
		//�򿪻򴴽�backuplog.db���ݿ�  
		File dbFile=new File("/data/data/com.cuccs.dreambox/databases/"
							+AppAccountInfo.getUsername(mContext)+"_backuplog.db");
		if(!dbFile.exists()){
			try {
				dbFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			final File logfile = dbFile;
			new Thread(new Runnable() {		//�������߳����ر�����־�ļ�
				@Override
				public void run() {
					BCSCredentials credentials = new BCSCredentials(AppAutoConstants.Baidu_BCS.accessKey,
							AppAutoConstants.Baidu_BCS.secretKey);
					BaiduBCS baiduBCS = new BaiduBCS(credentials, AppAutoConstants.Baidu_BCS.host);
					baiduBCS.setDefaultEncoding("UTF-8");
					try {
						getObjectWithDestFile(baiduBCS, "/" +
								AppAccountInfo.getUsername(mContext) +"/Log_backup/backuplog.db", logfile);
					}catch (BCSServiceException e) {
						AppAutoConstants.Baidu_BCS.log.warn("Bcs return:" + e.getBcsErrorCode() + ", " + e.getBcsErrorMessage() + ", RequestId=" + e.getRequestId());
					} catch (BCSClientException e) {
						e.printStackTrace();
					}
				}
			}).start();
			if(dbFile.length() <= 0){
				hasFinished = true;
				return nums;
			}
		}else if(dbFile.length() <= 0){
			hasFinished = true;
			return nums;
		}
		SQLiteDatabase mRecorderDB = this.getApplicationContext().openOrCreateDatabase(
						AppAccountInfo.getUsername(mContext)+"_backuplog.db", Context.MODE_PRIVATE, null);
		mRecorderDB.execSQL("CREATE TABLE IF NOT EXISTS backuplog("
				+ "_date VARCHAR, _type VARCHAR, dirname VARCHAR"
				+ ", contactsnum INT, smsnum INT, callsnum INT, photosnum INT, documentsnum INT, musicsnum INT)"); //������
		Cursor mcursor = mRecorderDB.rawQuery("SELECT * FROM backuplog ORDER By _date DESC Limit ? OFFSET ?", 
					new String[]{String.valueOf(SQL_limit),String.valueOf(SQL_offset)}  );
		/*ListView ��ҳ����,��Ҫ��Ҫ���������ݿ�Ĳ�ѯ���� */
		Log.e("Content_Restore_getCount_line104==>", mcursor.getCount()+"");
		
		while (mcursor.moveToNext()) {
			CardData_restore metadata = new CardData_restore();
			SimpleDateFormat minFormat = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy��MM��dd�� HH:mm");  //ע��˴�Сʱ������Сдh����12Сʱ��
			long date = mcursor.getLong(mcursor.getColumnIndex("_date"));
			
			long curdate =  System.currentTimeMillis();    //��ȡ��ǰʱ��,�����ж��Ƿ��ǽ��졢���졢����ǰ��      
			Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));  //��ȡ������ʱ��
			int hour = mCalendar.getTime().getHours();
			int minute = mCalendar.getTime().getMinutes();
			int second = mCalendar.getTime().getSeconds();
			long reduce = (hour*60*60 + minute*60 + second)*1000;
			long sign_yesterday = curdate - reduce;
			long sign_before_yesterday = sign_yesterday - (24*60*60*1000);
			
			if(date >= sign_yesterday && date <= curdate){
				metadata.mTitle = "����  "+minFormat.format(date);
			}else if(date >= sign_before_yesterday && date < sign_yesterday){
				metadata.mTitle = "����  "+minFormat.format(date);
			}else{
				metadata.mTitle = dateFormat.format(date);
			}
			String[] itemname = new String[]{"��ϵ��","����","ͨ����¼","��Ƭ���","�ĵ�","����"};
			metadata.mContentInfo[0] = mcursor.getInt(mcursor.getColumnIndex("contactsnum"));
			metadata.mContentInfo[1] = mcursor.getInt(mcursor.getColumnIndex("smsnum"));
			metadata.mContentInfo[2] = mcursor.getInt(mcursor.getColumnIndex("callsnum"));
			metadata.mContentInfo[3] = mcursor.getInt(mcursor.getColumnIndex("photosnum"));
			metadata.mContentInfo[4] = mcursor.getInt(mcursor.getColumnIndex("documentsnum"));
			metadata.mContentInfo[5] = mcursor.getInt(mcursor.getColumnIndex("musicsnum"));
			int m = 0;
			boolean isEmpty = true;
			for(int i=0;i<6;i++){
				if(metadata.mContentInfo[i] != 0){
					isEmpty = false;
					m++;
					if(m == 4){
						metadata.mContent = metadata.mContent +"\n";
					}
					metadata.mContent = metadata.mContent+"  "+itemname[i]+" "+metadata.mContentInfo[i];
				}
			}
			metadata.mDate = mcursor.getString(mcursor.getColumnIndex("_date"));
			metadata.mType = mcursor.getString(mcursor.getColumnIndex("_type"));
			metadata.mParentDir = mcursor.getString(mcursor.getColumnIndex("dirname"));
			if(isEmpty == false){		//����ָ���Ŀ��Ϊ�գ���ӵ���Ƭ��
				mCardsData.add(metadata);
			}else{						//����ָ���ĿΪ�գ�ɾ����������־��¼��Ϣ
				new LogRecorder_Backup(mContext).deleteRecord(metadata.mDate);
				SQL_offset--;
			}
		}
		nums = mcursor.getCount();
		if(nums < SQL_limit){
			hasFinished = true;
		}
		SQL_offset = SQL_offset + SQL_limit;	//��¼OFFSETֵ
		Log.e("Content_Restore_SQL_offset ---->", SQL_offset+"");
		mcursor.close(); 
		mRecorderDB.close();
		return nums;
	}
	
	private static void getObjectWithDestFile(BaiduBCS baiduBCS, String object, File destFile) {
		GetObjectRequest getObjectRequest = new GetObjectRequest(AppAutoConstants.Baidu_BCS.bucket, object);
		baiduBCS.getObject(getObjectRequest, destFile);
	}
	
	public void delectRecordBackup(String date, String dirname, String type, int[] mContentInfo){
		Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��     
		//�򿪻򴴽�backuplog.db���ݿ�  ,ɾ����¼
		new LogRecorder_Backup(mContext).deleteRecord(date);
		if(type.equals("true")){
			new LogRecorder_Operating(mContext).addRecord(curDate.getTime(), "ɾ���ƶ˱���", "time_str", mContentInfo);	//д����־
			Toast.makeText(getApplicationContext(), "�ƶ˱�����ɾ��", Toast.LENGTH_SHORT).show();
			return;
		}else{
			String filepath = AppFolderPaths.getBackupFilesDir(mContext) + "/" +dirname;
			File mfile = new File(filepath);
			deleteFile(mfile);	//�������ļ�
			new LogRecorder_Operating(mContext).addRecord(curDate.getTime(), "ɾ�����ر���", "time_str", mContentInfo);	//д����־
		}
	}

    public static void deleteFile(File file) {  
        if (file.isFile()) {
            file.delete();  
            return;  
        }  
        if(file.isDirectory()){  
           File[] childFiles = file.listFiles();  
            if (childFiles == null || childFiles.length == 0) {  
                file.delete();  
                return;  
            }  
            for (int i = 0; i < childFiles.length; i++) {  
            	deleteFile(childFiles[i]);  
            }  
            file.delete();  
        }  
    } 
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {     //���ؼ��˳�
	    if(keyCode == KeyEvent.KEYCODE_BACK 
	            && event.getAction() == KeyEvent.ACTION_DOWN){   
	    	finish();
            overridePendingTransition(R.anim.holding_anima, R.anim.contend_minify_ru);
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
