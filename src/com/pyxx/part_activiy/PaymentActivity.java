package com.pyxx.part_activiy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.pyxx.entity.BaseFragmentActivity;
import com.pyxx.fragment.PaymentListFragment;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 查看清单 页面
 * 
 * @author wll
 */
public class PaymentActivity extends BaseFragmentActivity implements
		OnClickListener {

	private TextView title_text;
	FragmentTransaction fragmentTransaction;
	Fragment m_list_frag_1 = null;
	boolean all = false;
	private String allvalue = "", info = "";
	private String type = "0";

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.payment);

		if (getIntent() != null) {
			allvalue = getIntent().getStringExtra("allvalue");
			info = getIntent().getStringExtra("info");
			type = getIntent().getStringExtra("type");
		}

		initView();
	}

	public void initView() {
		findViewById(R.id.title_back).setOnClickListener(this);
		title_text = (TextView) findViewById(R.id.title_title);
		if ("1".equals(type)) {
			title_text.setText("待付款订单");
		} else if ("2".equals(type)) {
			title_text.setText("我的订单");
		} else {
			title_text.setText("清单");
		}

		addNew();//

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			break;
		}
	}

	public void addNew() {
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		m_list_frag_1 = PaymentListFragment.newInstance("PaymentListFragment",
				"PaymentListFragment", getString(R.string.url_sel_type), info);
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
