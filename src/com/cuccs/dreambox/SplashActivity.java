package com.cuccs.dreambox;

import com.cuccs.dreambox.utils.AppSettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
	//ʹ��SharedPreferences����¼�����ʹ�ô���
    SharedPreferences spreferences;
    
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashscreen);

        //**********��ʾ�汾����
        /*PackageManager pm = getPackageManager();
        try {
            PackageInfo packi = pm.getPackageInfo("com.cuccs.dreambox", 0);
            TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
            versionNumber.setText("Version " + packi.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }*/
        TextView textview = (TextView) findViewById(R.id.versionNumber);
        textview.setText("Dream  Box");
                       
        
        new Handler().postDelayed(new Runnable() {
            public void run() {
            	//��ȡSharedPreferences����Ҫ������
                spreferences = getSharedPreferences("App_StartCount",MODE_WORLD_READABLE);
                int count = spreferences.getInt("count", 0);
            	//�жϳ�����ڼ������У�����ǵ�һ����������ת������ҳ��
                if (count == 0) {
					//createSystemSwitcherShortCut(getApplicationContext());	//������ݷ�ʽ��ͼ��
                	AppSettings.writeDefaultSetup(getApplicationContext());	//Ĭ������
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),Whatsnew.class);
                    intent.putExtra("whereFrom", "MainActivity");
                    startActivity(intent);
                    finish();
                }else{
                	startActivity(new Intent(SplashActivity.this, Homepage.class));
                    finish();
                }              
                Editor editor = spreferences.edit();        
                editor.putInt("count", 1);	//��������
                editor.commit();	//�ύ�޸�
            }
        }, 1500);

    }
	
	// ������ݷ�ʽ��ͼ��
	public static void createSystemSwitcherShortCut(Context context) {
		final Intent addIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		final Parcelable icon = Intent.ShortcutIconResource.fromContext(
				context, R.drawable.ic_launcher); // ��ȡ��ݼ���ͼ��
		addIntent.putExtra("duplicate", false);		//�������ظ�����  
		final Intent myIntent = new Intent(context,
				SplashActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				context.getString(R.string.app_name));// ��ݷ�ʽ�ı���
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// ��ݷ�ʽ��ͼ��
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);// ��ݷ�ʽ�Ķ���
		context.sendBroadcast(addIntent);
	}
}