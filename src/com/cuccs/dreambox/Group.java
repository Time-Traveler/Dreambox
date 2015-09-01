package com.cuccs.dreambox;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Group extends ActivityGroup implements OnGestureListener{
	private View oneView;
	private View twoView;
	private ArrayList<View> views;
	private ViewPager mViewPager;	//��ҳ�滬���л�Ч��
	GestureDetector mGestureDetector;      //��������

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups);
		mGestureDetector = new GestureDetector((OnGestureListener) this); 
		
		
		//Animation a = AnimationUtils.loadAnimation(this, R.anim.translate_left_enter);
		//Animation b = AnimationUtils.loadAnimation(this, R.anim.translate_right_exit);
		views = new ArrayList<View>();
		mViewPager = (ViewPager)findViewById(R.id.loginviewpager);        
	    mViewPager.setOnPageChangeListener(null);
	    oneView=getViews(LoginActivity.class,"one");
		twoView=getViews(RegisterActivity.class,"two");
		//oneView.startAnimation(a);
		//twoView.startAnimation(b);
		
		views.add(oneView);
		views.add(twoView);
		

		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			@Override
			public int getCount() {
				return views.size();
			}
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
		
	}
	
	
	public View getViews(Class<?> cls,String pageid){
		return getLocalActivityManager().startActivity(pageid, new Intent(Group.this,cls).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)).getDecorView();
	}


	
	
	@Override  /*����activity�����ScrollViewʵ�ֹ���activity��Ч����
	���ⲿ����ΪRelativeLayoutʱ��activity�Ļ���Ч��ȴ�޷���Ч�ˣ�
	ԭ������Ϊactivityû�д�����Ч��������������£�
	ʵ��dispatchTouchEvent������
	����ʵ���е���mGestureDetector.onTouchEvent(ev)������OnTouch��
	*******************************************************/
	public boolean dispatchTouchEvent(MotionEvent ev) {    
       mGestureDetector.onTouchEvent(ev);  
       return super.dispatchTouchEvent(ev);  
	}  
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int Windowwidth = wm.getDefaultDisplay().getWidth(); //��Ļ���
		int Windowheight = wm.getDefaultDisplay().getHeight(); //��Ļ�߶�
		Rect rect= new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);  
		int statusBarHeight = rect.top; //״̬���߶�
		
		if (e2.getY() - e1.getY() > Windowheight/3 && Math.abs(velocityY) > 30) {      //�ж�Ϊ���»���
	    	// �л���Homepage
	    	Intent intent = new Intent(Group.this, Homepage.class);  
	        startActivity(intent);
	        overridePendingTransition(R.anim.translate_up_enter, R.anim.holding_anima);
	        finish();
	    }  
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
