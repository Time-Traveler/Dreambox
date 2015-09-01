package com.cuccs.dreambox.layouts;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cuccs.dreambox.R;
import com.cuccs.dreambox.objects.myOnRefreshListener;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MyRefreshListView extends ListView implements OnScrollListener {

	private int downY;		// ����ʱy���ƫ����
	private View headerView;		// ͷ����
	private int headerViewHeight;	// ͷ���ֵĸ߶�
	private int firstVisibleItemPosition;		// ����ʱ������ʾ�ڶ�����item��position
	private DisplayMode currentState = DisplayMode.Pull_Down;		// ͷ���ֵ�ǰ��״̬, ȱʡֵΪ����״̬
	private Animation upAnim;		// ������ת�Ķ���
	private Animation downAnim;		// ������ת�Ķ���
	private ImageView ivArrow;		// ͷ���ֵļ�ͷ
	private TextView tvState;		// ͷ����ˢ��״̬
	private ProgressBar mProgressBar;	// ͷ���ֵĽ�����
	private TextView tvLastUpdateTime;	// ͷ���ֵ����ˢ��ʱ��
	private myOnRefreshListener mOnRefreshListener;
	private boolean isScroll2Bottom = false;	// �Ƿ�������ײ�
	private View footerView;		// �Ų���
	private int footerViewHeight;	// �Ų��ֵĸ߶�
	private boolean isLoadMoving = false;	// �Ƿ����ڼ��ظ�����

	public MyRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initHeader();
		initFooter();
		this.setOnScrollListener(this);
	}
	
	/**
	 * ��ʼ���Ų���
	 */
	private void initFooter() {
		footerView = LayoutInflater.from(getContext()).inflate(R.layout.content_restore_listview_refresh_footer, null);
		
		measureView(footerView);		// ����һ�½Ų��ֵĸ߶�
		
		footerViewHeight = footerView.getMeasuredHeight();
		
		footerView.setPadding(0, -footerViewHeight, 0, 0);		// ���ؽŲ���
		
		this.addFooterView(footerView);
	}

	/**
	 * ��ʼ��ͷ����
	 */
	private void initHeader() {
		headerView = LayoutInflater.from(getContext()).inflate(R.layout.content_restore_listview_refresh_header, null);
		ivArrow = (ImageView) headerView.findViewById(R.id.iv_listview_header_down_arrow);
		mProgressBar = (ProgressBar) headerView.findViewById(R.id.pb_listview_header_progress);
		tvState = (TextView) headerView.findViewById(R.id.tv_listview_header_state);
		tvLastUpdateTime = (TextView) headerView.findViewById(R.id.tv_listview_header_last_update_time);
		
		ivArrow.setMinimumWidth(50);
		ivArrow.setVisibility(View.GONE);
		tvLastUpdateTime.setText("���ˢ��ʱ��: " + getLastUpdateTime());
		
		measureView(headerView);
		headerViewHeight = headerView.getMeasuredHeight();
		Log.i("RefreshListView", "ͷ���ֵĸ߶�: " + headerViewHeight);
		
		headerView.setPadding(0, -headerViewHeight, 0, 0);
		
		this.addHeaderView(headerView);
		
		initAnimation();
	}
	
	/**
	 * ������ˢ�µ�ʱ��
	 * @return
	 */
	private String getLastUpdateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	/**
	 * ��ʼ������
	 */
	private void initAnimation() {
		upAnim = new RotateAnimation(
				0, -180, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		upAnim.setDuration(500);
		upAnim.setFillAfter(true);
		
		downAnim = new RotateAnimation(
				-180, -360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		downAnim.setDuration(500);
		downAnim.setFillAfter(true);
	}
	
	/**
	 * ����������View�Ŀ�͸�, ����֮��, ���Եõ�view�Ŀ�͸�
	 * @param child
	 */
	private void measureView(View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
        	lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        
        int lpHeight = lp.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = (int) ev.getY();
			break;
			/**
			 * ��������ˢ�´���
			*/
		case MotionEvent.ACTION_MOVE:  
			
			if(currentState == DisplayMode.Refreshing) {
				// ��ǰ��״̬������ˢ����, ��ִ����������
				break;
			}	
			int moveY = (int) ev.getY();	// �ƶ��е�y���ƫ����
			int diffY = moveY - downY;		
			int paddingTop = -headerViewHeight + (diffY / 2);
			
			if(firstVisibleItemPosition == 0
					&& paddingTop > -headerViewHeight) {				
				/**
				 * paddingTop > 0   ��ȫ��ʾ
				 * currentState == DisplayMode.Pull_Down ����������״̬ʱ
				 */
				if(paddingTop > 0 	// ��ȫ��ʾ
						&& currentState == DisplayMode.Pull_Down) {		// ��ȫ��ʾ, ���뵽ˢ��״̬  
					Log.i("RefreshListView", "�ɿ�ˢ��");
					currentState = DisplayMode.Release_Refresh;		// �ѵ�ǰ��״̬��Ϊ�ɿ�ˢ��
					refreshHeaderViewState();
				} else if(paddingTop < 0
						&& currentState == DisplayMode.Release_Refresh) {		// û����ȫ��ʾ, ���뵽����״̬
					Log.i("RefreshListView", "����ˢ��");
					currentState = DisplayMode.Pull_Down;
					refreshHeaderViewState();
				}
				
				headerView.setPadding(0, paddingTop, 0, 0);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			downY = -1;
			
			if(currentState == DisplayMode.Pull_Down) {		// �ɿ�ʱ, ��ǰ��ʾ��״̬Ϊ����״̬, ִ������headerView�Ĳ���
				
				headerView.setPadding(0, -headerViewHeight, 0, 0);
			} else if(currentState == DisplayMode.Release_Refresh) {	// �ɿ�ʱ, ��ǰ��ʾ��״̬Ϊ�ɿ�ˢ��״̬, ִ��ˢ�µĲ���
				headerView.setPadding(0, 0, 0, 0);
				currentState = DisplayMode.Refreshing;
				refreshHeaderViewState();
				
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onRefresh();
				}
			}
			
			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * ��ˢ������ִ�����ʱ, �ص��˷���, ȥˢ�½���
	 */
	public void onRefreshFinish() {
		if(isLoadMoving) {	// ���ؽŲ���
			isLoadMoving = false;
			isScroll2Bottom = false;
			footerView.setPadding(0, -footerViewHeight, 0, 0);
		} else {	// ����ͷ����
			headerView.setPadding(0, -headerViewHeight, 0, 0);
			mProgressBar.setVisibility(View.GONE);
			//ivArrow.setVisibility(View.VISIBLE);
			tvLastUpdateTime.setText("�������ʱ��: " + getLastUpdateTime());
			currentState = DisplayMode.Pull_Down;
		}
	}
	
	/**
	 * ˢ��ͷ���ֵ�״̬
	 */
	private void refreshHeaderViewState() {
		if(currentState == DisplayMode.Pull_Down) {	// ��ǰ��������״̬
			ivArrow.setVisibility(View.GONE);
			ivArrow.startAnimation(downAnim);
			tvState.setText("����ɾ����Ŀ");
		} else if(currentState == DisplayMode.Release_Refresh) { //��ǰ�����ɿ�ˢ��״̬
			ivArrow.setVisibility(View.GONE);
			ivArrow.startAnimation(upAnim);
			tvState.setText("������лָ�");
		} else if(currentState == DisplayMode.Refreshing) {  //��ǰ��������ˢ����
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.GONE);
			//mProgressBar.setVisibility(View.VISIBLE);
			tvState.setText("������ɾ����Ŀ");
		}
	}

	/**
	 * ��ListView����״̬�ı�ʱ�ص�
	 * SCROLL_STATE_IDLE		// ��ListView����ֹͣʱ
	 * SCROLL_STATE_TOUCH_SCROLL // ��ListView��������ʱ
	 * SCROLL_STATE_FLING		// ���ٵĹ���(��ָ���ٵĴ����ƶ�)
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE
				|| scrollState == OnScrollListener.SCROLL_STATE_FLING) {
			if(isScroll2Bottom && !isLoadMoving) {		// �������ײ�
				// ���ظ���
				footerView.setPadding(0, 0, 0, 0);
				this.setSelection(this.getCount());		// ������ListView�ĵײ�
				isLoadMoving = true;
				
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onLoadMore();
				}
			}
		}
	}

	/**
	 * ��ListView����ʱ����
	 * firstVisibleItem ��Ļ����ʾ�ĵ�һ��Item��position
	 * visibleItemCount ��ǰ��Ļ��ʾ���ܸ���
	 * totalItemCount   ListView��������
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		firstVisibleItemPosition = firstVisibleItem;
		
		//Log.i("RefreshListView", "onScroll: " + firstVisibleItem + ", " + visibleItemCount + ", " + totalItemCount);
		
		if((firstVisibleItem + visibleItemCount) >= totalItemCount
				&& totalItemCount > 0) {
			isScroll2Bottom = true;
		} else {
			isScroll2Bottom = false;
		}
	}
	
	/**
	 * @author andong
	 * ����ͷ���ļ�����ʾ״̬
	 */
	public enum DisplayMode {
		Pull_Down, // ����ˢ�µ�״̬
		Release_Refresh, // �ɿ�ˢ�µ�״̬
		Refreshing	// ����ˢ���е�״̬
	}
	
	/**
	 * ����ˢ�µļ����¼�
	 * @param listener
	 */
	public void setOnRefreshListener(myOnRefreshListener listener) {
		this.mOnRefreshListener = listener;
	}
}
