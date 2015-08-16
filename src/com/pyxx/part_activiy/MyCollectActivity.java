package com.pyxx.part_activiy;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pyxx.entity.BaseFragmentActivity;
import com.pyxx.entity.Listitem;
import com.pyxx.fragment.MyCollectListFragment;
import com.pyxx.fragment.OrderListFragment;
import com.pyxx.fragment.PaymentListFragment;
import com.pyxx.fragment.SecondFzListFragment;
import com.pyxx.fragment.ShopCarListFragment;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class MyCollectActivity extends BaseFragmentActivity implements
		OnClickListener {
	private EditText search_et;
	private ImageView search_iv,select_all;
	private TextView all_select,title_text;
	private RelativeLayout all_rl;
	private Button jiesuan_btn;
	FragmentTransaction fragmentTransaction;
	Fragment m_list_frag_1 = null;
	private String categorysId="0",categoryId="0";
	private RelativeLayout bottom_rl;
	boolean all = false;
	private String allvalue="",info="";
	private List<Listitem> list;
	private String type = "0";

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mycollect);
		
		init();
	}

	public void init() {
		findViewById(R.id.title_back)
				.setOnClickListener(this);
		title_text = (TextView)findViewById(R.id.title_title);

		addNew();
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			break;
		}
	}

	public void things(View view){
		
	}
	
	public void addNew() {
//		String str =getString(R.string.url_sel_goods)+"&categorysId="+categorysId+"&categoryId="+categoryId+"&keyword="+search_et.getText().toString();
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		m_list_frag_1 = MyCollectListFragment.newInstance("MyCollectListFragment", "MyCollectListFragment",getString(R.string.url_sel_all_category));
		fragmentTransaction.replace(R.id.part_second_content, m_list_frag_1).commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		switch (resultCode) {
//		case 1:
//			if (data != null) {
//				fragmentTransaction = getSupportFragmentManager()
//						.beginTransaction();
//				Fragment m_list_frag_4 = CollectSupplyFragment.newInstance("22",
//						"22");
//				fragmentTransaction.replace(R.id.part_content, m_list_frag_4)
//						.commitAllowingStateLoss();
//				supply_rl.setBackgroundResource(R.drawable.second_bg2);
//				buy_rl.setBackgroundResource(R.drawable.second_bg1);
//				company_rl.setBackgroundResource(R.drawable.second_bg1);
//			}
//			break;
//		}
	}
}
