package com.cuccs.dreambox.layouts;

import com.cuccs.dreambox.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class LeftMenuPanelLayout extends LinearLayout{
	public LinearLayout RLayout_1_1;
	private LinearLayout RLayout_1_2;
	private LinearLayout RLayout_1_3;
	private LinearLayout RLayout_1_4;
	
	private LinearLayout RLayout_2_1;
	private LinearLayout RLayout_2_2;
	private LinearLayout RLayout_2_3;
	private LinearLayout RLayout_2_4;
	private OnMenuSeletedListener monMenuSeletedListener;
	
	public LeftMenuPanelLayout(Context context) {  
        super(context);  
        setupViews(); 
    }  
  
    public LeftMenuPanelLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        setupViews();  
    }
	
    private void setupViews() {  
        final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
        LinearLayout left_menu_flat = (LinearLayout) mLayoutInflater.inflate(R.layout.left_menu_flat, null);  
        addView(left_menu_flat);
        
        RLayout_1_1 = (LinearLayout) left_menu_flat.findViewById(R.id.left_menu_1_1);
        RLayout_1_2 = (LinearLayout) left_menu_flat.findViewById(R.id.left_menu_1_2);
        RLayout_1_3 = (LinearLayout) left_menu_flat.findViewById(R.id.left_menu_1_3);
        RLayout_1_4 = (LinearLayout) left_menu_flat.findViewById(R.id.left_menu_1_4);
        RLayout_2_1 = (LinearLayout) left_menu_flat.findViewById(R.id.left_menu_2_1);
        RLayout_2_2 = (LinearLayout) left_menu_flat.findViewById(R.id.left_menu_2_2);
        RLayout_2_3 = (LinearLayout) left_menu_flat.findViewById(R.id.left_menu_2_3);
        RLayout_2_4 = (LinearLayout) left_menu_flat.findViewById(R.id.left_menu_2_4);
        RLayout_1_1.setBackgroundResource(R.drawable.left_menuitem_selected);	//��ʼ��Ĭ��ѡ����ҳ��
        
        RLayout_1_1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (monMenuSeletedListener == null) {
                }
				monMenuSeletedListener.seletedMenuChild(0, 0);  
				ClearSelectedView();
				RLayout_1_1.setBackgroundResource(R.drawable.left_menuitem_selected);
			}});
        RLayout_1_2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (monMenuSeletedListener == null) {
                }
				monMenuSeletedListener.seletedMenuChild(0, 1);  
				ClearSelectedView();
				RLayout_1_2.setBackgroundResource(R.drawable.left_menuitem_selected);
			}});
        RLayout_1_3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (monMenuSeletedListener == null) {
                }
				monMenuSeletedListener.seletedMenuChild(0, 2);  
				ClearSelectedView();
				RLayout_1_3.setBackgroundResource(R.drawable.left_menuitem_selected);
			}});
        RLayout_1_4.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (monMenuSeletedListener == null) {
                }
				monMenuSeletedListener.seletedMenuChild(0, 3);  
				ClearSelectedView();
				RLayout_1_4.setBackgroundResource(R.drawable.left_menuitem_selected);
			}});
        //*************************************�����˵����ַָ���*************************************//
        RLayout_2_1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (monMenuSeletedListener == null) {
                }
				monMenuSeletedListener.seletedMenuChild(1, 0);  
				ClearSelectedView();
				RLayout_2_1.setBackgroundResource(R.drawable.left_menuitem_selected);
			}});
        RLayout_2_2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (monMenuSeletedListener == null) {
                }
				monMenuSeletedListener.seletedMenuChild(1, 1);  
				ClearSelectedView();
				RLayout_2_2.setBackgroundResource(R.drawable.left_menuitem_selected);
			}});
        RLayout_2_3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (monMenuSeletedListener == null) {
                }
				monMenuSeletedListener.seletedMenuChild(1, 2); 
				ClearSelectedView();
				RLayout_2_3.setBackgroundResource(R.drawable.left_menuitem_selected);
			}});
        RLayout_2_4.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (monMenuSeletedListener == null) {
                }
				monMenuSeletedListener.seletedMenuChild(1, 3); 
				ClearSelectedView();
				RLayout_2_4.setBackgroundResource(R.drawable.left_menuitem_selected);
			}});
    }
    
    public void ClearSelectedView(){
    	RLayout_1_1.setBackgroundResource(R.drawable.left_menuitem_normal);
    	RLayout_1_2.setBackgroundResource(R.drawable.left_menuitem_normal);
    	RLayout_1_3.setBackgroundResource(R.drawable.left_menuitem_normal);
    	RLayout_1_4.setBackgroundResource(R.drawable.left_menuitem_normal);
    	RLayout_2_1.setBackgroundResource(R.drawable.left_menuitem_normal);
    	RLayout_2_2.setBackgroundResource(R.drawable.left_menuitem_normal);
    	RLayout_2_3.setBackgroundResource(R.drawable.left_menuitem_normal);
    	RLayout_2_4.setBackgroundResource(R.drawable.left_menuitem_normal);
    }
    
    /** 
     * ����ѡ�е�Item�¼������� 
     * @param seletedListener 
     */  
    public void setOnMenuSeletedListener(OnMenuSeletedListener seletedListener) {  
        monMenuSeletedListener = seletedListener;  
    }
    
    /** 
     * �໬�˵���ѡ�е�Item�¼������� 
     */  
    public interface OnMenuSeletedListener {  
        /** 
         * ��ǰѡ�е�Item�¼������� 
         * @param groupPosition ������Id 
         * @param childPosition ���������ڵ�λ�� 
         */  
    	public abstract void seletedMenuChild(int groupPosition, int childPosition);  
    }
}
