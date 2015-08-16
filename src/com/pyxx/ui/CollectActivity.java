package com.pyxx.ui;

import com.pyxx.entity.BaseFragmentActivity;
import com.pyxx.fragment.CollectHuoDongListFragment;
import com.pyxx.fragment.CollectJiaFangProductListFragment;
import com.pyxx.fragment.CollectProductListFragment;
import com.pyxx.fragment.CollectRecomandProductListFragment;
import com.pyxx.fragment.CollectZgspShopsListFragment;
import com.pyxx.fragment.CollectZxxxListFragment;
import com.pyxx.fragment.SearchProductListFragment;
import com.pyxx.zhongguofuzhuangxiemao.R;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


@SuppressLint("CutPasteId")
public class CollectActivity extends BaseFragmentActivity implements
		OnClickListener {
	Fragment m_list_frag_1 = null;
	Fragment m_list_frag_2 = null;
	Fragment m_list_frag_3 = null;
	Fragment m_list_frag_4 = null;
	RadioButton btn_rl1,btn_rl2,btn_rl3;
	ImageView title_back;
	FragmentTransaction fragmentTransaction;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_part_collect);
		example();
	}

	public void example() {
		btn_rl1 = (RadioButton) findViewById(R.id.collect_rb_1);
		btn_rl1.setOnClickListener(this);
		btn_rl2 = (RadioButton) findViewById(R.id.collect_rb_2);
		btn_rl2.setOnClickListener(this);
//		btn_rl3 = (RadioButton) findViewById(R.id.collect_rb_3);
//		btn_rl3.setOnClickListener(this);

		title_back = (ImageView) findViewById(R.id.title_back);
		title_back.setImageResource(R.drawable.return_tb1);
		title_back.setOnClickListener(this);
		findViewById(R.id.title_btn_right).setVisibility(View.GONE);
		
		TextView title = (TextView)findViewById(R.id.title_title);
		title.setText("我的收藏");
		
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		m_list_frag_1 = CollectRecomandProductListFragment.newInstance("CollectRecomandProductListFragment", "CollectRecomandProductListFragment",getString(R.string.url_sel_type));
		fragmentTransaction.replace(R.id.part_content, m_list_frag_1).commit();
		btn_rl1.setTextColor(getResources().getColor(R.color.biaoti_color));
		btn_rl2.setTextColor(Color.parseColor("#222222"));
//		btn_rl3.setBackgroundColor(getResources().getColor(R.color.the_main_bg));
	
	}

	public void things(View v) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.collect_rb_1:
			fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			m_list_frag_1 = CollectRecomandProductListFragment.newInstance("CollectRecomandProductListFragment", "CollectRecomandProductListFragment",getString(R.string.url_sel_type));
			fragmentTransaction.replace(R.id.part_content, m_list_frag_1)
					.commit();
			btn_rl1.setTextColor(getResources().getColor(R.color.biaoti_color));
			btn_rl2.setTextColor(Color.parseColor("#222222"));
//			btn_rl3.setBackgroundColor(getResources().getColor(R.color.the_main_bg));
			break;
		case R.id.collect_rb_2:
			fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			m_list_frag_2 = CollectJiaFangProductListFragment.newInstance("CollectJiaFangProductListFragment", "CollectJiaFangProductListFragment",getString(R.string.url_sel_type));
			fragmentTransaction.replace(R.id.part_content, m_list_frag_2)
					.commit();
			btn_rl2.setTextColor(getResources().getColor(R.color.biaoti_color));
			btn_rl1.setTextColor(Color.parseColor("#222222"));
//			btn_rl3.setBackgroundColor(getResources().getColor(R.color.the_main_bg));
			break;
//		case R.id.collect_rb_3:
//			fragmentTransaction = getSupportFragmentManager()
//					.beginTransaction();
//			m_list_frag_3 = CollectHuoDongListFragment.newInstance("CollectHuoDongListFragment", "CollectHuoDongListFragment",getString(R.string.url_sel_type));
//			fragmentTransaction.replace(R.id.part_content, m_list_frag_3)
//					.commit();
//			btn_rl1.setBackgroundColor(getResources().getColor(R.color.the_main_bg));
//			btn_rl2.setBackgroundColor(getResources().getColor(R.color.the_main_bg));
//			btn_rl3.setBackgroundColor(getResources().getColor(R.color.the_main_bg2));
//			break;
		case R.id.title_back:
			finish();
			break;
		}
	}
}
