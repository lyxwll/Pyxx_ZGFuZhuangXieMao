package com.pyxx.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;

import com.pyxx.baseui.BaseActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;

public class FavActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_st_fav);
		initFragment();
	}

	Fragment m_list_frag_1 = null;
	Fragment m_list_frag_2 = null;
	Fragment m_list_frag_3 = null;

	Fragment frag = null;

	public void things(View view) {
		switch (view.getId()) {
		case R.id.title_back:
			finish();
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	Fragment findresult = null;

	public void initFragment() {
		// FragmentTransaction fragmentTransaction = getSupportFragmentManager()
		// .beginTransaction();
		// m_list_frag_1 = RedianListFragment.newInstance(DBHelper.FAV_FLAG +
		// "0",
		// DBHelper.FAV_FLAG);
		// fragmentTransaction.replace(R.id.part_content,
		// m_list_frag_1).commit();

	}
}
