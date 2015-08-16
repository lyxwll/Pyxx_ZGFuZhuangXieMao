package com.pyxx.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.pyxx.baseui.BaseMainActivity;
import com.pyxx.entity.CityLifeApplication;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 标准式布局
 * 
 * @author HeJian
 * 
 */
public class MainActivity extends BaseMainActivity {
	TabHost mTabHost;
	HashMap<String, Boolean> tabmap = new HashMap<String, Boolean>();
	HashSet<String> tabset = new HashSet<String>();
	public int currentpart = R.id.m_part_1;
	Handler mahandler;
	View mOldView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup(getLocalActivityManager());
		if (savedInstanceState != null) {
			currentpart = savedInstanceState.getInt("current_key");
		} else {
			currentpart = getIntent().getIntExtra("current_key", R.id.m_part_1);
		}
		changePart(findViewById(currentpart));
		mahandler = new Handler();
		List<String> tags = getTagsList("city"
				+ PerfHelper.getStringData(PerfHelper.P_CITY_No));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("current_key", currentpart);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 栏目选择
	 * 
	 * @param view
	 */
	public void changePart(View view) {
		if (mOldView != null && view.getId() == mOldView.getId()) {
			return;
		}
		if (mOldView != null) {
			mOldView.setBackgroundDrawable(null);
			switch (mOldView.getId()) {
			case R.id.m_part_1:
				// ((ImageView)
				// mOldView).setImageResource(R.drawable.main_part_1_n);
				break;
			case R.id.m_part_2:
				// ((ImageView)
				// mOldView).setImageResource(R.drawable.main_part_2_n);
				break;
			case R.id.m_part_3:
				// ((ImageView)
				// mOldView).setImageResource(R.drawable.main_part_3_n);
				break;
			case R.id.m_part_4:
				// ((ImageView)
				// mOldView).setImageResource(R.drawable.main_part_4_n);
				break;
			case R.id.m_part_5:
				// ((ImageView)
				// mOldView).setImageResource(R.drawable.main_part_5_n);
				break;
			}
		}
		mOldView = view;
		String tag = "";
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.m_part_1:
			tag = "parg1";
			intent.putExtra("title", "一级栏目首页");
			// ((ImageView) view).setImageResource(R.drawable.main_part_1_h);
			break;
		case R.id.m_part_2:
			tag = "parg2";
			intent.putExtra("title", "一级栏目市场");
			// ((ImageView) view).setImageResource(R.drawable.main_part_2_h);
			break;
		case R.id.m_part_3:
			tag = "parg3";
			intent.putExtra("title", "一级栏目附近");
			// ((ImageView) view).setImageResource(R.drawable.main_part_3_h);
			break;
		case R.id.m_part_4:
			tag = "parg4";
			intent.putExtra("title", "一级栏目资讯");
			// ((ImageView) view).setImageResource(R.drawable.main_part_4_h);
			break;
		case R.id.m_part_5:
			tag = "parg5";
			// ((ImageView) view).setImageResource(R.drawable.main_part_5_h);
			break;
		}
		view.setSelected(false);
		if (tabmap.get(tag) != null && tabmap.get(tag)) {
			mTabHost.setCurrentTabByTag(tag);
		} else {
			intent.putExtra("data", tag);
			TabHost.TabSpec spec = buildTabSpec(mTabHost, tag, intent);
			mTabHost.addTab(spec);
			tabmap.put(tag, true);
			mTabHost.setCurrentTabByTag(tag);
		}
	}

	public void things(View view) {
		switch (view.getId()) {
		}
	}

	/**
	 * 再按一次退出程序
	 */
	private long exitTime = 0;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 2000) {

				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// 完全退出程序；
				System.exit(0);
				android.os.Process.killProcess(android.os.Process.myPid());
				finish();
			}
			return true;
		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	// 建议在APP整体退出之前调用MapApi的destroy()函数，不要在每个activity的OnDestroy中调用，
	// 避免MapApi重复创建初始化，提高效率
	protected void onDestroy() {
		CityLifeApplication app = (CityLifeApplication) this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.destroy();
			app.mBMapManager = null;
		}
		PerfHelper.setInfo(PerfHelper.P_NOW_CITY, "");
		System.exit(0);
		super.onDestroy();
	}

	private List<String> getTagsList(String originalText) {

		List<String> tags = new ArrayList<String>();
		int indexOfComma = originalText.indexOf(',');
		String tag;
		while (indexOfComma != -1) {
			tag = originalText.substring(0, indexOfComma);
			tags.add(tag);

			originalText = originalText.substring(indexOfComma + 1);
			indexOfComma = originalText.indexOf(',');
		}

		tags.add(originalText);
		return tags;
	}
}
