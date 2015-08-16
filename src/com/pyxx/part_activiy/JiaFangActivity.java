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
import com.pyxx.fragment.RecomandProductListFragment;
import com.pyxx.fragment.SecondFzListFragment;
import com.pyxx.fragment.ShopCarListFragment;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class JiaFangActivity extends BaseFragmentActivity implements
		OnClickListener {
	private RelativeLayout all_rl;
	FragmentTransaction fragmentTransaction;
	Fragment m_list_frag_1 = null;
	Fragment m_list_frag_2 = null;
	private RelativeLayout bottom_rl;
	private List<Listitem> lis;
	private TextView title;
	private String statue ="",typeId="";

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.jiafang_main);
        if(getIntent()!=null){
        	statue = getIntent().getExtras().getString("statue");
        }
		init();
	}

	public void init() {
		findViewById(R.id.title_back)
				.setOnClickListener(this);
        title = (TextView)findViewById(R.id.title_title);
        if("1".equals(statue)){
        	title.setText("羽绒被");
        	typeId = "73";
        }else if("2".equals(statue)){
        	title.setText("羽绒垫");
        	typeId = "74";
        }else if("3".equals(statue)){
        	title.setText("羽绒枕套");
        	typeId = "75";
        }else if("4".equals(statue)){
        	title.setText("羽绒护膝");
        	typeId = "76";
        }else if("5".equals(statue)){
        	title.setText("羽绒护腰");
        	typeId = "77";
        }else if("6".equals(statue)){
        	title.setText("羽绒护肩");
        	typeId = "78";
        }else if("7".equals(statue)){
        	title.setText("羽绒鞋");
        	typeId = "79";
        }else if("8".equals(statue)){
        	title.setText("羽绒手套");
        	typeId = "80";
        }
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
		m_list_frag_1 = RecomandProductListFragment.newInstance("RecomandProductListFragment"+statue, "RecomandProductListFragment"+statue,getString(R.string.url_sel_product)+"commodity.menuId=11&commodity.typeId="+typeId);
		fragmentTransaction.replace(R.id.part_second_content, m_list_frag_1).commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
}
