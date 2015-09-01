package com.cuccs.dreambox;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Website_Dreamanster extends Activity{
	Button Btn_Back;
	TextView mTitle;
	ProgressBar progressHorizontal;
	WebView webview;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.website_dreamanster);
		
		mTitle = (TextView) findViewById(R.id.website_dreamanster_Title);
		progressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
		webview = (WebView) findViewById(R.id.website_dreamanster_webview); 
        webview.getSettings().setJavaScriptEnabled(true); 
        webview.loadUrl("http://www.dreamanster.com"); 
        webview.setWebChromeClient(new WebChromeClient(){
        	public void onProgressChanged(WebView view, int newProgress) {
        		super.onProgressChanged(view, newProgress);
                //����progressHorizontal����
                if (newProgress < 100) {
                	progressHorizontal.setVisibility(View.VISIBLE);
                }
        		progressHorizontal.setProgress(newProgress);
        		progressHorizontal.postInvalidate();
        		if (newProgress == 100) {
                	progressHorizontal.setVisibility(View.GONE);
                }
        	}
        	@Override 
            public void onReceivedTitle(WebView view, String title) { 
                //���õ�ǰactivity�ı����� 
                super.onReceivedTitle(view, title); 
                mTitle.setText(title);
            } 
        });
        webview.setWebViewClient(new WebViewClient(){
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		view.loadUrl(url);   //�ڵ�ǰ��webview����ת���µ�url
				return true;
        	}
        });
		Btn_Back = (Button) findViewById(R.id.website_dreamanster_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
                //�趨�µ�Activity����͵�ǰActivity�˳�ʱ�Ķ�����
                //��startActivity������finish�����󣬵���overridePendingTransition����.
                overridePendingTransition(R.anim.translate_left_enter, R.anim.translate_right_exit);      
            }  
        });
	}
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {       
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {       
            webview.goBack();       
            return true;       
        }
        finish();
        overridePendingTransition(R.anim.translate_left_enter, R.anim.translate_right_exit);
        return super.onKeyDown(keyCode, event);       
    }   
}
