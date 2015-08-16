package com.pyxx.part_activiy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pyxx.entity.BaseFragmentActivity;
import com.pyxx.fragment.HuoDongListFragment;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 实现 服装关键字搜索 的Activity
 * 
 * @author wll
 */
public class SecondFzActivity extends BaseFragmentActivity implements
		OnClickListener {

	private EditText search_et;
	private ImageView search_iv;
	FragmentTransaction fragmentTransaction;
	Fragment m_list_frag_1 = null;
	Fragment m_list_frag_2 = null;
	private String categorysId = "0", categoryId = "0";
	private TextView search_btn;
	private String keyword;
	private TextView title;

	private String sort = "";

	private TextView type_1_btn, type_2_btn, type_3_btn, type_4_btn;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.secondfz);
		
		//接收从上一个页面传过来的值
		if (getIntent() != null) {
			categorysId = (String) getIntent().getExtras().getString(
					"categorysId");
			categoryId = (String) getIntent().getExtras().getString(
					"categoryId");
			keyword = getIntent().getExtras().getString("keyword");
		}

		initView();

	}

	public void initView() {
		findViewById(R.id.title_back).setOnClickListener(this);
		search_iv = (ImageView) findViewById(R.id.search_iv);
		search_et = (EditText) findViewById(R.id.search_tv);
		search_btn = (TextView) findViewById(R.id.search_btn);
		search_btn.setOnClickListener(this);

		type_1_btn = (TextView) findViewById(R.id.type_1_btn);
		type_1_btn.setOnClickListener(this);
		type_2_btn = (TextView) findViewById(R.id.type_2_btn);
		type_2_btn.setOnClickListener(this);
		type_3_btn = (TextView) findViewById(R.id.type_3_btn);
		type_3_btn.setOnClickListener(this);
		type_4_btn = (TextView) findViewById(R.id.type_4_btn);
		type_4_btn.setOnClickListener(this);

		title = (TextView) findViewById(R.id.title_title);
		title.setText(keyword);

		type_1_btn.setTextColor(getResources().getColor(R.color.biaoti_color));
		type_2_btn.setTextColor(Color.parseColor("#222222"));
		type_3_btn.setTextColor(Color.parseColor("#222222"));
		type_4_btn.setTextColor(Color.parseColor("#222222"));

		addClothesNew();

	}

	public void things(View view) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {// 按钮点击事件
		case R.id.title_back:
			finish();
			break;
		case R.id.type_1_btn:
			sort = "5";
			type_1_btn.setTextColor(getResources().getColor(
					R.color.biaoti_color));
			type_2_btn.setTextColor(Color.parseColor("#222222"));
			type_3_btn.setTextColor(Color.parseColor("#222222"));
			type_4_btn.setTextColor(Color.parseColor("#222222"));
			addClothesNew();
			break;
		case R.id.type_2_btn:
			sort = "6";
			type_2_btn.setTextColor(getResources().getColor(
					R.color.biaoti_color));
			type_1_btn.setTextColor(Color.parseColor("#222222"));
			type_3_btn.setTextColor(Color.parseColor("#222222"));
			type_4_btn.setTextColor(Color.parseColor("#222222"));
			addClothesNew();
			break;
		case R.id.type_3_btn:
			sort = "1";
			type_3_btn.setTextColor(getResources().getColor(
					R.color.biaoti_color));
			type_2_btn.setTextColor(Color.parseColor("#222222"));
			type_1_btn.setTextColor(Color.parseColor("#222222"));
			type_4_btn.setTextColor(Color.parseColor("#222222"));
			addClothesNew();
			break;
		case R.id.type_4_btn:
			sort = "3";
			type_4_btn.setTextColor(getResources().getColor(
					R.color.biaoti_color));
			type_2_btn.setTextColor(Color.parseColor("#222222"));
			type_3_btn.setTextColor(Color.parseColor("#222222"));
			type_1_btn.setTextColor(Color.parseColor("#222222"));
			addClothesNew();
			break;
		case R.id.search_btn:
			break;
		}
	}

	/**
	 * 获取搜索信息
	 */
	public void addClothesNew() {
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		m_list_frag_1 = HuoDongListFragment.newInstance("HuoDongListFragment2",
				"HuoDongListFragment2", getString(R.string.url_sel_product)
						+ "&commodity.menuId=12&commodity.typeId="
						+ categorysId + "&commodity.subTypeId=" + categoryId
						+ "&sort=" + sort);
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
