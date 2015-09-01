package com.cuccs.dreambox;

import com.cuccs.dreambox.layouts.MyVideoView;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class FunctionIntroduction  extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//ȡ��������
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
		              WindowManager.LayoutParams. FLAG_FULLSCREEN);//ȫ��
		setContentView(R.layout.function_introduce);

		/* ����VideoView���� */
		final MyVideoView videoView = (MyVideoView) findViewById(R.id.VideoView01);
		/* ����·�� */
		videoView.setVideoURI(Uri.parse("android.resource://com.cuccs.dreambox/"+R.raw.intro));
		/* ����ģʽ-���Ž����� */
		videoView.setMediaController(null);
		videoView.requestFocus();
		
		/* ��ʼ���� */
		videoView.start();
	}
}
