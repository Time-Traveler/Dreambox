package com.cuccs.dreambox.layouts;

import com.cuccs.dreambox.R;
import com.cuccs.dreambox.layouts.SlidingMenuContainer.OnSlideListener;
import com.cuccs.dreambox.objects.CustomTextDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SharewithFriendsLayout extends FrameLayout{
	private LinearLayout LLayout_1;
	private LinearLayout LLayout_2;
	private LinearLayout LLayout_3;
	private LinearLayout LLayout_4;
	private Context mContext;
	private Button Btn_Back;
	private Dialog mDialog;
	private OnSlideListener mOnSlideListener;
	
	public SharewithFriendsLayout(Context context) {  
        super(context);  
        this.mContext = context;
        setupViews();  
    }  
  
    public SharewithFriendsLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        this.mContext = context;
        setupViews();  
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {  
        mOnSlideListener = onSlideListener;  
    }
    
    private void setupViews() {  
        final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
        LinearLayout mSharelayout = (LinearLayout) mLayoutInflater.inflate(R.layout.menu_sharewithfriends, null);  
        addView(mSharelayout);
        
        LLayout_1 = (LinearLayout) mSharelayout.findViewById(R.id.sharewithfriends_share_weibo);
        LLayout_2 = (LinearLayout) mSharelayout.findViewById(R.id.sharewithfriends_share_qq);
        LLayout_3 = (LinearLayout) mSharelayout.findViewById(R.id.sharewithfriends_share_wechat);
        LLayout_4 = (LinearLayout) mSharelayout.findViewById(R.id.sharewithfriends_share_android);
        
        LLayout_1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				CustomTextDialog.Builder customBuilder = new
						CustomTextDialog.Builder(mContext);
		            customBuilder.setTitle("��ʾ")
		                .setMessage("Coming soon...")
		                .setPositiveButton("OK", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	dialog.dismiss();
		                    }
		                });
		            mDialog = customBuilder.create();
		            mDialog.show();
			}});
        LLayout_2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				CustomTextDialog.Builder customBuilder = new
						CustomTextDialog.Builder(mContext);
		            customBuilder.setTitle("��ʾ")
		                .setMessage("Coming soon...")
		                .setPositiveButton("OK", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	dialog.dismiss();
		                    }
		                });
		            mDialog = customBuilder.create();
		            mDialog.show();
			}});
        LLayout_3.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				CustomTextDialog.Builder customBuilder = new
						CustomTextDialog.Builder(mContext);
		            customBuilder.setTitle("��ʾ")
		                .setMessage("Coming soon...")
		                .setPositiveButton("OK", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	dialog.dismiss();
		                    }
		                });
		            mDialog = customBuilder.create();
		            mDialog.show();
			}});
        LLayout_4.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_SEND); // ���������͵�����
            	intent.setType("text/plain"); // �����͵���������
            	String msg = "һ��ʵ�õ�App�Ƽ�����ң�Dreambox �������Ա����ֻ���Ҫ���ݣ�������ϵ�ˡ����š�ͨ����¼�ȡ�" +
//            			"���Է��ʹٷ���վ http://www.dreamanster.com ���˽���ϸ����";
						"���Է��� http://gdown.baidu.com/data/wisegame/42a94ae319b6d236/Dreambox_1.apk ���������";
            	intent.putExtra(Intent.EXTRA_TEXT, msg); // ���������
            	mContext.startActivity(Intent.createChooser(intent, "ѡ�����"));// Ŀ��Ӧ��ѡ��Ի���ı��� 
            	//ContentActivity.startNewActivity(Intent.createChooser(intent, "ѡ�����");	//����Activity
			}});
        Btn_Back = (Button) mSharelayout.findViewById(R.id.sharewithfriends_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {
                if (mOnSlideListener != null) { 
                    mOnSlideListener.toRight();  
                }
            }  
        });
    }
}
