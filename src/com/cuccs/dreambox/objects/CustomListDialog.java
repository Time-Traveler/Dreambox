package com.cuccs.dreambox.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cuccs.dreambox.R;
import com.cuccs.dreambox.utils.AppSettings;
import com.cuccs.dreambox.utils.B_R_AddressBook;
import com.cuccs.dreambox.utils.B_R_PhoneCalls;
import com.cuccs.dreambox.utils.B_R_SMS;
import com.cuccs.dreambox.utils.CheckNetwork;
import com.cuccs.dreambox.utils.LogRecorder_Operating;
import com.cuccs.dreambox.utils.ProgressThread_Backup_cloud;
import com.cuccs.dreambox.utils.ProgressThread_Backup_sdcard;
import com.cuccs.dreambox.utils.ProgressThread_Restore_cloud;
import com.cuccs.dreambox.utils.ProgressThread_Restore_sdcard;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CustomListDialog extends Dialog{
	public static final int MSG_ADDRESSBOOK_WHAT = 100;
	public static final int MSG_SMS_WHAT = 101;
	public static final int MSG_PHONECALLS_WHAT = 102;
	public static final int MSG_UPLOAD_WHAT = 777;
	public static final int MSG_DOWNLOAD_WHAT = 666;
	public static final int MSG_DOWNLOAD_SUCCESS = 665;
	public static final int MSG_CLEARCACHE_WHAT = 888;
	public static final int MSG_SUCCESS_WHAT = 999;
	public static final int MSG_FAILED_WHAT = 1000;
	
	private PowerManager powerManager = null;
	private WakeLock wakeLock = null;
	public HashMap<Integer, Boolean> ItemisSelected;
	private boolean isBackupNotRestore = false;	//ָ��ִ�б��ݲ���,���ǻָ�
	private boolean isCloudUsed = false;
	private String mDirname_restore = null;
	private Context mContext;
	private TextView mTitle;
	private ImageView mIcon;
	private LinearLayout mLinearLayout;
	private Button mBtn_Goon, mBtn_Cancle, mBtn_OK;
	private MediaPlayer mMediaPlayer;
	private ListView mListview;
	private SimpleAdapter adapter;
	private List<Map<String,Object>> listItems;
	final String[] titles = new String[]{"��ϵ��","����","ͨ����¼","��Ƭ���","�ĵ�","����"};
	private int[] itemposition = new int[6];
	private int[] iteminfo = new int[6];
	
	final Handler mhandler = new Handler() {  
        public void handleMessage(Message msg) {
        	Map<String,Object> maps = new HashMap<String, Object>();			//ʵ����map����
        	int total = 0;
        	switch (msg.what) {
        		case MSG_ADDRESSBOOK_WHAT:
        			total = msg.getData().getInt("total"); 
        			Log.e("total-----> ", total+" "+itemposition[0]);
        			listItems.get(itemposition[0]).clear();
        			maps.clear();
        			maps.put("title", titles[0]);
        			maps.put("info", total+"/"+iteminfo[0]);
        			listItems.set(0, maps);
        			adapter.notifyDataSetChanged();
        			break;
        			
        		case MSG_SMS_WHAT:
        			total = msg.getData().getInt("total"); 
        			Log.e("total-----> ", total+" "+itemposition[1]);
        			listItems.get(itemposition[1]).clear();
        			maps.clear();
        			maps.put("title", titles[1]);
        			maps.put("info", total+"/"+iteminfo[1]);
        			listItems.set(itemposition[1], maps);
        			adapter.notifyDataSetChanged();
        			break;
        			
        		case MSG_PHONECALLS_WHAT:
        			total = msg.getData().getInt("total"); 
        			Log.e("total-----> ", total+" "+itemposition[2]);
        			listItems.get(itemposition[2]).clear();
        			maps.clear();
        			maps.put("title", titles[2]);
        			maps.put("info", total+"/"+iteminfo[2]);
        			listItems.set(itemposition[2], maps);
        			adapter.notifyDataSetChanged();
        			break;
        			
        		case MSG_UPLOAD_WHAT:
        			mTitle.setText("�����ϴ����ƶ�...");
        			mIcon.setImageResource(R.drawable.cloud_upload);
        			break;
        			
        		case MSG_DOWNLOAD_WHAT:
        			mTitle.setText("���ڴ��ƶ�����...");
        			mIcon.setImageResource(R.drawable.cloud_download);
        			break;
        			
        		case MSG_DOWNLOAD_SUCCESS:
        			mTitle.setText("�������,�ָ���ʼ...");
        			mIcon.setImageResource(R.drawable.finished_mark_pressed);
        			break;
        			
        		case MSG_CLEARCACHE_WHAT:
        			mTitle.setText("���������ػ���...");
        			mIcon.setImageResource(R.drawable.clear_cache_normal);
        			break;
        			
        		case MSG_FAILED_WHAT:
        			Vibrator mVibrator01 = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
    				mVibrator01.vibrate( new long[]{0,200,0,0}, -1);	 //��ʼ ��
        			mTitle.setText("����ʧ��!");
        			mBtn_Cancle.setClickable(true);		//ȡ�������
        			mIcon.setImageResource(R.drawable.confirm_dialog_warning);
        			mLinearLayout.setVisibility(View.INVISIBLE);	//����������Ϊһ��ȷ����
        			mBtn_OK.setVisibility(View.VISIBLE);
        			mBtn_OK.setClickable(true);
        			break;
        			
        		case MSG_SUCCESS_WHAT:
        			Vibrator mVibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
    				mVibrator.vibrate( new long[]{0,100,300,100}, -1);	 //��ʼ ��
        			AppSettings.readSettings(mContext);
        			if(AppSettings.SoundOn == true){	//��Ч���ÿ������򲥷�����
        				mMediaPlayer.start();
        			}
        			if(isBackupNotRestore == true){
        				mTitle.setText("OK,�������!");		//���ݵĲ�����¼��־ �Ѿ��ڱ��ݹ��������
        			}else{
        				mTitle.setText("OK,�ָ����!");
        				Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��     
        				if(isCloudUsed == false){
        					new LogRecorder_Operating(mContext).addRecord(curDate.getTime(), "SD���ָ�", "time_str", iteminfo);	//д����־
        				}else{
        					new LogRecorder_Operating(mContext).addRecord(curDate.getTime(), "�ƶ˻ָ�", "time_str", iteminfo);	//д����־
        				}
        				
        			}
        			mBtn_Cancle.setClickable(true);		//ȡ�������
        			mIcon.setImageResource(R.drawable.btn_check_on_normal);
        			mLinearLayout.setVisibility(View.INVISIBLE);	//����������Ϊһ��ȷ����
        			mBtn_OK.setVisibility(View.VISIBLE);
        			mBtn_OK.setClickable(true);
        			break;
        	}
        	super.handleMessage(msg);
        }  
    };   
	
    
	public CustomListDialog(Context context, int theme, HashMap<Integer,Boolean> ItemisSelected, boolean isCloudBackup){
        super(context, theme);
        this.mContext = context;
        this.ItemisSelected = ItemisSelected;
        this.isCloudUsed = isCloudBackup;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_backup_cloud);
		powerManager = (PowerManager) mContext.getSystemService(Service.POWER_SERVICE);		//������Ļ����
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Lock");
		wakeLock.setReferenceCounted(false);  //�Ƿ��������������
		wakeLock.acquire();
		
		mMediaPlayer = MediaPlayer.create(mContext, R.raw.done);
		mLinearLayout = (LinearLayout)findViewById(R.id.dialog_backup_cloud_layout_bottom);
		mIcon = (ImageView)findViewById(R.id.dialog_backup_cloud_icon);
		mTitle = (TextView)findViewById(R.id.dialog_backup_cloud_title);
		mTitle.setText("AlertDialog");
		mBtn_Cancle = (Button)findViewById(R.id.dialog_backup_cloud_btn_cancle);
		mBtn_Cancle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CustomListDialog.this.dismiss();
			}
		});
		mBtn_Goon = (Button)findViewById(R.id.dialog_backup_cloud_btn_goon);
		if(isBackupNotRestore == false){
			mBtn_Goon.setText("�� ��");
			mTitle.setText("�ָ���\n"+mDirname_restore);
		}
		mBtn_Goon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(isBackupNotRestore == true && isCloudUsed == true){	//�ƶ˱���
					ProgressThread_Backup_cloud mBcloudthread = new ProgressThread_Backup_cloud(mhandler, mContext, iteminfo, ItemisSelected);
					mBcloudthread.start();
					mTitle.setText("���ڴ������...");
					mBtn_Goon.setClickable(false);
					mBtn_Cancle.setClickable(false);
					return;
				}
				else if(isBackupNotRestore == false && isCloudUsed == true){	//�ƶ˻ָ�
					CheckNetwork checknet = new CheckNetwork(mContext);		//������������Ƿ�����
					if(checknet.isConnectingToInternet() == false){
						return;
					}
					AppSettings.readSettings(mContext);		//��ȡ�û�����
			        if(AppSettings.OnlyWiFi == true && checknet.isWiFiConnected() == true 
			        		|| AppSettings.OnlyWiFi == false){
			        	ProgressThread_Restore_cloud mRthread = new ProgressThread_Restore_cloud(
								mhandler, mContext, mDirname_restore, ItemisSelected); 
						mRthread.start();
						mTitle.setText("���ڴ��ƶ˻ָ�...");
						mBtn_Goon.setClickable(false);
						mBtn_Cancle.setClickable(false);
			        }
					return;
				}
				else if(isBackupNotRestore == true && isCloudUsed == false){	//����SD������
					ProgressThread_Backup_sdcard mBthread = new ProgressThread_Backup_sdcard(
							mhandler, mContext, iteminfo, ItemisSelected);
					mBthread.start();
					mTitle.setText("���ڱ���,���Ժ�...");
					mBtn_Goon.setClickable(false);
					mBtn_Cancle.setClickable(false);
					return;
				}
				else if(isBackupNotRestore == false && isCloudUsed == false){	//����SD���ָ�
					ProgressThread_Restore_sdcard mRthread = new ProgressThread_Restore_sdcard(
							mhandler, mContext, mDirname_restore, ItemisSelected); 
					mRthread.start();
					mTitle.setText("���ڴ�SD���ָ�...");
					mBtn_Goon.setClickable(false);
					mBtn_Cancle.setClickable(false);
					return;
				}
			}
		});
		mBtn_OK = (Button)findViewById(R.id.dialog_backup_cloud_btn_ok);
		mBtn_OK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CustomListDialog.this.dismiss();
			}
		});
		
		mListview = (ListView)findViewById(R.id.dialog_backup_cloud_listview);
		listItems = new ArrayList<Map<String,Object>>();	//����list
		if(isBackupNotRestore == true && ItemisSelected.size() != 0){
			for(int i=0;i<6;i++){
				if(ItemisSelected.get(i) == true){
					Map<String,Object> maps = new HashMap<String, Object>();			//ʵ����map����
					maps.put("title", titles[i]);
					int quantuty = 0;
					switch(i){
						case 0:	//��ϵ��
							quantuty = new B_R_AddressBook(mContext, null).getItemQuantity(mContext);
							break;
						case 1:	//����
							quantuty = new B_R_SMS(mContext, null).getItemQuantity(mContext);
							break;
						case 2:	//ͨ����¼
							quantuty = new B_R_PhoneCalls(mContext, null).getItemQuantity(mContext);
							break;
						case 3:	//��Ƭ���
							break;
						case 4:	//�������ĵ�
							break;
						case 5:	//����
							break;
					}
					iteminfo[i] = quantuty;
					itemposition[i] = listItems.size();
					maps.put("info", "0/"+quantuty);
					listItems.add(maps);
				}
			}
		}
		if(isBackupNotRestore == false && ItemisSelected.size() != 0){
			for(int i=0;i<6;i++){
				if(ItemisSelected.get(i) == true){
					itemposition[i] = listItems.size();
					Map<String,Object> maps = new HashMap<String, Object>();			//ʵ����map����
					maps.put("title", titles[i]);
					maps.put("info", "0/"+iteminfo[i]);
					listItems.add(maps);
				}
			}
		}
		if(listItems.size() == 0){		//��ʾ��δѡ��
			mLinearLayout.setVisibility(View.INVISIBLE);
			mBtn_OK.setVisibility(View.VISIBLE);
			mBtn_OK.setClickable(true);
			
			Map<String,Object> maps0 = new HashMap<String, Object>();			//ʵ����map����
			maps0.put("title", "   ");
			maps0.put("info", "");
			listItems.add(maps0);
			Map<String,Object> maps = new HashMap<String, Object>();
			maps.put("title", "   ��Ǹ, ��û��ѡ�����Ŀ");
			maps.put("info", "");
			listItems.add(maps);
			Map<String,Object> maps2 = new HashMap<String, Object>();
			maps2.put("title", "   ");
			maps2.put("info", "");
			listItems.add(maps2);
		}
		adapter = new SimpleAdapter(mContext,listItems,
				R.layout.dialog_backup_cloud_listview_items,new String[]{"title","info"},
				new int[]{R.id.dialog_backup_cloud_listview_title,R.id.dialog_backup_cloud_listview_info});
		mListview.setAdapter(adapter);
		
	}
	
	public void show(){
		super.show();
		//������
		if (wakeLock != null) {
			wakeLock.acquire();
		}
	}
	public void dismiss() {
		super.dismiss();
		//ȡ����Ļ����
		if (wakeLock != null) {
			wakeLock.release();
		}
	}
	
	public void setTitle(String title){
		mTitle.setText(title);
	}
	public void setIcon(int resId){
		mIcon.setImageResource(resId);
	}
	public void setUseType(boolean isBackupOrNot){
		this.isBackupNotRestore = isBackupOrNot;
	}
	public void setParentDir(String parentDir){
		this.mDirname_restore = parentDir;
	}
	public void deliverItemInfo(int[] iteminfo){	/**Ԥ����Ϣ�����ָ��߳���*/
		this.iteminfo = iteminfo;
	}
}
