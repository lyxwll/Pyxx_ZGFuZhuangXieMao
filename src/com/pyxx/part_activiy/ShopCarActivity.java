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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.entity.BaseFragmentActivity;
import com.pyxx.entity.Listitem;
import com.pyxx.fragment.ShopCarListFragment;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 购物车 显示界面
 * 
 * @author wll
 */
public class ShopCarActivity extends BaseFragmentActivity implements
		OnClickListener {

	private ImageView select_all;
	private TextView all_select, jiage_text, bianji_btn;
	private RelativeLayout all_rl;
	private Button jiesuan_btn;
	FragmentTransaction fragmentTransaction;
	Fragment m_list_frag_1 = null;
	Fragment m_list_frag_2 = null;
	public static ShopCarListFragment slf;
	boolean all = false;
	private String allvalue = "";
	private List<Listitem> lis;
	Listitem listitem;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (getIntent().getExtras() != null) {
			listitem = (Listitem) getIntent().getExtras().get("item");
		}

		setContentView(R.layout.shop_car_main);
		initView();

	}

	public void initView() {

		findViewById(R.id.shopcar_title_back).setOnClickListener(this);
		all_select = (TextView) this.findViewById(R.id.qx_tv);
		jiage_text = (TextView) this.findViewById(R.id.jiage_text);
		jiesuan_btn = (Button) this.findViewById(R.id.jiesuan_iv);
		bianji_btn = (TextView) this.findViewById(R.id.shop_bianji_tv);
		bianji_btn.setText("编辑");
		bianji_btn.setOnClickListener(this);
		all_rl = (RelativeLayout) this.findViewById(R.id.qx_rl);
		all_rl.setOnClickListener(this);
		jiesuan_btn.setOnClickListener(this);
		select_all = (ImageView) this.findViewById(R.id.select_all);
		jiage_text.setText("总价：￥0.0");//总价

		addNew();//

	}

	public void method(ShopCarListFragment slf) {
		this.slf = slf;
	}

	public void things(View view) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shopcar_title_back:
			finish();
			break;
		case R.id.shop_bianji_tv:
			if ("编辑".equals(bianji_btn.getText().toString().trim())) {
				bianji_btn.setText("完成");
				fragmentTransaction = getSupportFragmentManager()
						.beginTransaction();
				m_list_frag_1 = ShopCarListFragment
						.newInstance(
								"bianji",
								"1",
								getString(R.string.url_save_shopscarlist)
										+ "shop.userId="
										+ PerfHelper
												.getStringData(PerfHelper.P_USERID)
										+ "&shop.menuId=" + listitem.fuwu
										+ "&shop.status=1",
								ShopCarActivity.this, false);
				fragmentTransaction.replace(R.id.shop_car_content,
						m_list_frag_1).commit();
			} else {
				bianji_btn.setText("编辑");
				fragmentTransaction = getSupportFragmentManager()
						.beginTransaction();
				m_list_frag_1 = ShopCarListFragment
						.newInstance(
								"3",
								"1",
								getString(R.string.url_save_shopscarlist)
										+ "shop.userId="
										+ PerfHelper
												.getStringData(PerfHelper.P_USERID)
										+ "&shop.menuId=" + listitem.fuwu
										+ "&shop.status=1",
								ShopCarActivity.this, false);
				fragmentTransaction.replace(R.id.shop_car_content,
						m_list_frag_1).commit();
			}
			break;
		case R.id.qx_rl:// 全选按钮
			select_all.setImageResource(R.drawable.selected2);
			fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			m_list_frag_1 = ShopCarListFragment.newInstance("1", "2",
					getString(R.string.url_save_shopscarlist) + "shop.userId="
							+ PerfHelper.getStringData(PerfHelper.P_USERID)
							+ "&shop.menuId=" + listitem.fuwu
							+ "&shop.status=1", ShopCarActivity.this, true);
			fragmentTransaction.replace(R.id.shop_car_content, m_list_frag_1)
					.commit();
			break;
		case R.id.jiesuan_iv:// 结算按钮
			if (lis == null || lis.size() == 0) {
				Utils.showToast("请选择需要购买的商品");
			} else {
				String ids = "";
				String str = "[";
				for (int i = 0; i < lis.size(); i++) {
					Listitem l = lis.get(i);
					if (i == lis.size() - 1) {
						ids += l.nid;
					} else {
						ids += l.nid + ",";
					}
					str += "{logo:'";
					str += l.icon;
					str += "',name:'";
					str += l.title;
					str += "',id:'";
					str += l.nid;
					str += "',num:'";
					str += l.fuwu;
					str += "',price:'";
					str += l.other;
					str += "'},";
				}
				str = str.substring(0, str.length() - 1);
				str += "]";

				Intent intent = new Intent();
				intent.setClass(this, AddOrderActivity.class);
				intent.putExtra("totalValue", this.allvalue);
				intent.putExtra("ids", ids);
				intent.putExtra("statue", "1");
				intent.putExtra("infos", str);
				startActivity(intent);
			}
			break;
		}
	}

	public void method1(String allvalue) {
		this.allvalue = allvalue;
		jiage_text.setText("总价：￥" + this.allvalue);
	}

	public void method2() {
		select_all.setImageResource(R.drawable.selected1);
	}

	public void method3(List lis) {
		this.lis = lis;
	}

	public void method4() {
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		m_list_frag_1 = ShopCarListFragment.newInstance("bianji",
				"ShopCarListFragment",
				getString(R.string.url_save_shopscarlist) + "shop.userId="
						+ PerfHelper.getStringData(PerfHelper.P_USERID)
						+ "&shop.menuId=" + listitem.fuwu + "&shop.status=1",
				ShopCarActivity.this, false);
		fragmentTransaction.replace(R.id.shop_car_content, m_list_frag_1)
				.commit();
	}

	/**
	 * 加载获取数据
	 */
	public void addNew() {
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		m_list_frag_1 = ShopCarListFragment.newInstance("ShopCarListFragment",
				"ShopCarListFragment",
				getString(R.string.url_save_shopscarlist) + "shop.userId="
						+ PerfHelper.getStringData(PerfHelper.P_USERID)
						+ "&shop.menuId=" + listitem.fuwu + "&shop.status=1",
				ShopCarActivity.this, false);
		fragmentTransaction.replace(R.id.shop_car_content, m_list_frag_1)
				.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.allvalue = "0.0";
		jiage_text.setText("总价：￥" + this.allvalue);
		select_all.setImageResource(R.drawable.selected1);

		addNew();//

	}

}
