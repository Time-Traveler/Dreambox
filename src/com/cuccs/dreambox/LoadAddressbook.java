package com.cuccs.dreambox;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author TimeTraveler
 * ��ȡ�ֻ���SIM����ϵ�ˣ�ʵ�ֲ���绰�����Ͷ��Ź���
 * ��ҪȨ��   <uses-permission android:name="android.permission.READ_CONTACTS"/>
 * From:  www.apkbus.com/android-13445-1-1.html
 */

public class LoadAddressbook extends Activity{
	/**��ȡ��Phone���ֶ�**/
    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;		/*��ϵ����ʾ����*/    
    private static final int PHONES_NUMBER_INDEX = 1;		/*�绰����*/    
    private static final int PHONES_PHOTO_ID_INDEX = 2;		/*ͷ��ID*/     
    private static final int PHONES_CONTACT_ID_INDEX = 3;		/*��ϵ�˵�ID*/
    
    private ArrayList<String> mContactsName = new ArrayList<String>();		/*��ϵ������**/        
    private ArrayList<String> mContactsNumber = new ArrayList<String>();		/*��ϵ�˺���**/    
    private ArrayList<Bitmap> mContactsPhoto = new ArrayList<Bitmap>();		/*��ϵ��ͷ��**/  
    private ArrayList<Long> mContactsId = new ArrayList<Long>();			/*��ϵ��ID*/

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadaddressbook);
		
		/*�õ��ֻ�ͨѶ¼��ϵ����Ϣ*/
        getPhoneContacts();
		//getSIMContacts(); 
        ListView listView = (ListView)findViewById(R.id.loadaddressbook_listview);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();		//����list
		
		Map<String,Object> map = new HashMap<String, Object>();			//��ӵ�һ��map������ʾ������xx����ϵ�ˡ�
		map.put("image", null);
		map.put("title", "========"+mContactsNumber.size()+"λ��ϵ��=======");
		map.put("nums", null);
		listItems.add(map);
		for(int i=0;i<mContactsNumber.size();i++){
			Map<String,Object> maps = new HashMap<String, Object>();			//ʵ����map����
			maps.put("image", R.drawable.user);			/**SIM����ϵ��û��ͷ��,С�ĳ���RuntimeException�쳣**/
			maps.put("title", mContactsName.get(i).toString());
			maps.put("nums", mContactsNumber.get(i).toString());
			listItems.add(maps);
		}
		
		
		SimpleAdapter adapter = new SimpleAdapter(this,listItems,
				R.layout.loadaddressbook_items,new String[]{"image","title","nums"},new int[]{R.id.loadaddressbook_icons,R.id.loadshortmessage_titles,R.id.loadaddressbook_nums});
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                    int position, long id) {
            	if(position==0){
            		return;}
            	AlertDialog alert = new AlertDialog.Builder(LoadAddressbook.this,AlertDialog.THEME_HOLO_LIGHT).create();
            	alert.setIcon(R.drawable.user);
            	alert.setTitle(mContactsName.get(position-1));
            	alert.setMessage("�绰��  "+mContactsNumber.get(position-1));
            	final int posi = position-1;
            	alert.setButton(DialogInterface.BUTTON_NEUTRAL, "����", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//����ϵͳ��������绰
		                Intent dialIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + mContactsNumber.get(posi)));
		                startActivity(dialIntent);
		                //ֱ�Ӳ���绰
		                //startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:"+"15010824958")));
					}
				});
            	alert.setButton(DialogInterface.BUTTON_POSITIVE, "������", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//���Ͷ���
						Uri uri = Uri.parse("smsto:"+ mContactsNumber.get(posi)); 	
		            	Intent it = new Intent(Intent.ACTION_SENDTO, uri); 
		            	it.putExtra("sms_body", "From Dreambox: "); 
		            	startActivity(it);
					}
				});
            	alert.show();
            	
                
            	//�鿴ĳ����ϵ�ˣ���Ȼ������ACTION_VIEW�����Ϊѡ�񲢷���action��ΪACTION_PICK����Ȼ����intentʱ������Ҫ�õ� startActivityforResult 
            	/*Log.i("list", id+"");
            	Log.i("lister", mContactsId.get(position)+"");
            	Uri personUri = ContentUris.withAppendedId(People.CONTENT_URI, 10);//����ID����Ϊ��ϵ��Provider�е����ݿ�BaseID������һ�� 
            	Intent dialIntent = new Intent(); 
            	dialIntent.setAction(Intent.ACTION_VIEW); 
            	dialIntent.setData(personUri);
            	startActivity(dialIntent);*/
            	                                
            }

        });
	}
	
	
	/**�õ��ֻ�ͨѶ¼��ϵ����Ϣ**/
    private void getPhoneContacts() {
        ContentResolver resolver = this.getContentResolver();

        // ��ȡ�ֻ���ϵ��
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null,
        		android.provider.ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //�õ��ֻ�����
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                
                //�õ���ϵ������
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                
                //�õ���ϵ��ID
                long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                //�õ���ϵ��ͷ��ID
                long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                
                //�õ���ϵ��ͷ��Bitamp
                Bitmap contactPhoto = null;

                //photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
                if(photoid > 0 ) {
                    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                }else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.user);
                }
                
                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
                mContactsPhoto.add(contactPhoto);
                mContactsId.add(contactid);
            }

            phoneCursor.close();
        }
    }
    
    /**�õ��ֻ�SIM����ϵ������Ϣ**/
    private void getSIMContacts() {
        ContentResolver resolver = this.getContentResolver();
        // ��ȡSims����ϵ��
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
                null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // �õ��ֻ�����
                String phoneNumber = phoneCursor.getString(2);
                // ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // �õ���ϵ������
                String contactName = phoneCursor.getString(1);
                //�õ���ϵ��ID
                Long contactid = phoneCursor.getLong(0);
                
                //Sim����û����ϵ��ͷ��
                
                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
                mContactsId.add(contactid);
                Log.v("LoadAddressbook_name_lines204", contactName);
                Log.v("LoadAddressbook_number_lines205", phoneNumber);
            }
            phoneCursor.close();
        }
    }
}
