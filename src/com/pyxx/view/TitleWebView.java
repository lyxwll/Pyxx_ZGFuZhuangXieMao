package com.pyxx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public class TitleWebView extends WebView implements OnClickListener {

	ScrollInterface mt;

	public TitleWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TitleWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TitleWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		// Log.e("hhah",""+l+" "+t+" "+oldl+" "+oldt);
		if (mt != null) {
			mt.onSChanged(l, t, oldl, oldt);
		}
	}

	public void setOnCustomScroolChangeListener(ScrollInterface scrollInterface) {
		this.mt = scrollInterface;
	}

	public interface ScrollInterface {
		public void onSChanged(int l, int t, int oldl, int oldt);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
