package com.cuccs.dreambox.utils;


import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * @author TimeTraveler
 *�첽ͼƬ����
 */
public class AsyncImageLoader {
	
	public SoftReference<Bitmap> loadImageFromSrcPath(final String imageSourcePath, final ImageCallback imageCallback){
		
		final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.NotifyImageLoaded(null, (Bitmap) message.obj);
            }
        };
        new Thread() {
            @Override
            public void run() {
            	// ��ͼƬ·���ж�ȡͼƬ��Դ
        		Bitmap bitmap = null;
        		try {
        			BitmapFactory.Options options = new BitmapFactory.Options();		//��������ͼ
        			/**
        			 * *  ����inJustDecodeBoundsΪtrue��decodeFile��������ռ䣬���ɼ����ԭʼͼƬ�ĳ��ȺͿ�ȣ���opts.width��opts.height�������˴�����һ��Ҫ�ǵý�ֵ����Ϊfalse
        			 * */
        			options.inJustDecodeBounds = false;
        			int be = options.outHeight/40;
        			if (be <= 0) {
        				be = 5;
        			}
        			options.inSampleSize = be;
        			
        			bitmap = BitmapFactory.decodeFile(imageSourcePath,options);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
                
                Message message = handler.obtainMessage(0, bitmap);
                handler.sendMessage(message);
            }
        }.start();
        return null;
	}
	
	public interface ImageCallback {
        public void NotifyImageLoaded(ImageView imageview,Object data);
    }
}