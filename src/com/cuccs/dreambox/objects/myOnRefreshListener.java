package com.cuccs.dreambox.objects;

public interface myOnRefreshListener {

	/**
	 * ����ˢ��ִ�е�ˢ������, ʹ��ʱ, 
	 * ��ˢ�����֮��, ��Ҫ�ֶ��ĵ���onRefreshFinish(), ȥ����ͷ����
	 */
	public void onRefresh();
	
	/**
	 * �����ظ���ʱ�ص�
	 * �����ظ������֮��, ��Ҫ�ֶ��ĵ���onRefreshFinish(), ȥ���ؽŲ���
	 */
	public void onLoadMore();
}
