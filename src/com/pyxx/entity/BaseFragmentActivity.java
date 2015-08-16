package com.pyxx.entity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.pyxx.app.ShareApplication;
import com.pyxx.loadimage.ImageCache;
import com.pyxx.loadimage.ImageCache.ImageCacheParams;
import com.pyxx.loadimage.ImageFetcher;

public abstract class BaseFragmentActivity extends FragmentActivity {
	public boolean isNightMode = false;
	public View actionbar_title;

	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (ShareApplication.mImageWorker == null) {
			ImageCacheParams cacheParams = new ImageCacheParams(
					ShareApplication.IMAGE_CACHE_DIR);
			ImageFetcher mImageWorker = new ImageFetcher(this, 800);
			// mImageWorker.setLoadingImage(R.drawable.empty_photo);
			mImageWorker.setImageCache(ImageCache.findOrCreateCache(this,
					cacheParams));
			ShareApplication.mImageWorker = mImageWorker;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	};

}
