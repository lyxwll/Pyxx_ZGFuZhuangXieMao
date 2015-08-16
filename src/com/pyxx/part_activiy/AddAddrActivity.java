package com.pyxx.part_activiy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.entity.BaseFragmentActivity;
import com.pyxx.fragment.AddAddrListFragment;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 收货地址管理界面
 * 
 * @author wll
 */
public class AddAddrActivity extends BaseFragmentActivity implements
		OnClickListener {
	private TextView title_text, bianji_text;
	private RelativeLayout second_rl;
	FragmentTransaction fragmentTransaction;
	Fragment m_list_frag_1 = null;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addaddr);

		// if(getIntent()!=null){
		// item = (Listitem)getIntent().getExtras().get("item");
		// }

		init();
	}

	public void init() {
		findViewById(R.id.title_back).setOnClickListener(this);
		title_text = (TextView) findViewById(R.id.title_title);
		bianji_text = (TextView) findViewById(R.id.title_right);
		bianji_text.setOnClickListener(this);
		second_rl = (RelativeLayout) findViewById(R.id.part_second_content);
		second_rl.setVisibility(View.VISIBLE);

		addNew();
	}

	public void things(View v) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			break;
		case R.id.title_right:
			finish();
			Intent intent = new Intent();
			intent.setClass(AddAddrActivity.this, WriteAddrActivity.class);
			intent.putExtra("item", "");
			startActivity(intent);
			break;
		}
	}

	public void method() {
		second_rl.setVisibility(View.GONE);
	}

	public void addNew() {
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		m_list_frag_1 = AddAddrListFragment.newInstance("AddAddrListFragment",
				"AddAddrListFragment",
				getString(R.string.url_sel_addr) + "&address.userId="
						+ PerfHelper.getStringData(PerfHelper.P_USERID), this);
		fragmentTransaction.replace(R.id.part_second_content, m_list_frag_1)
				.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// switch (resultCode) {
		// case 1:
		// if (data != null) {
		// fragmentTransaction = getSupportFragmentManager()
		// .beginTransaction();
		// Fragment m_list_frag_4 = CollectSupplyFragment.newInstance("22",
		// "22");
		// fragmentTransaction.replace(R.id.part_content, m_list_frag_4)
		// .commitAllowingStateLoss();
		// supply_rl.setBackgroundResource(R.drawable.second_bg2);
		// buy_rl.setBackgroundResource(R.drawable.second_bg1);
		// company_rl.setBackgroundResource(R.drawable.second_bg1);
		// }
		// break;
		// }
	}
}
