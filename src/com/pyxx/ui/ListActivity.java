package com.pyxx.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyxx.adapter.LvSimpleAdapter;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.AddOrderActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 添加订单页面 的 选择跳转方式选择
 * 
 * @author wll
 */
public class ListActivity extends Activity implements OnItemClickListener {

	private ListView listv;
	private int[] id = { R.id.list_dialog_title_text, R.id.list_dialog_img_imgv };
	private String[] fr = { "list_dialog_title_text", "list_dialog_img_imgv" };
	private List<Map<String, Object>> list_display;
	private String[] list_Stores;
	private LvSimpleAdapter listv_adapter;
	private String intentNum = "", toActivity = "";
	private TextView text_title;
	ImageView back_btn;
	private String Name = "";
	private RelativeLayout city_rl, add_rl;
	private ListView city_lv;
	private Button submit_btn;
	private EditText content_et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_activity);

		// 接收上一个页面传的值
		list_Stores = this.getIntent().getStringArrayExtra("list");
		intentNum = this.getIntent().getStringExtra("Num");
		toActivity = this.getIntent().getStringExtra("toActivity");
		Name = this.getIntent().getStringExtra("Name");

		listv = (ListView) findViewById(R.id.list_avtivity_lst);
		listv.setOnItemClickListener(this);

		text_title = (TextView) findViewById(R.id.share_title);
		city_rl = (RelativeLayout) findViewById(R.id.city_rl);
		// 备注内容
		add_rl = (RelativeLayout) findViewById(R.id.jianjie_content_rl);
		city_lv = (ListView) findViewById(R.id.list_avtivity_lst);
		submit_btn = (Button) findViewById(R.id.title_send);
		content_et = (EditText) findViewById(R.id.jianjie_content_et);

		back_btn = (ImageView) findViewById(R.id.title_back);
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		if ("101".equals(intentNum)) {
			text_title.setText("选择配送方式");
			startList();
		} else if ("102".equals(intentNum)) {
			text_title.setText("选择收货地址");
			startList();
		}else if("103".equals(intentNum)){
			text_title.setText("选择支付方式");
			startList();
		} else if ("104".equals(intentNum)) {
			text_title.setText("备注内容");
			startAddJianjie();
		} else if ("201".equals(intentNum)) {
			text_title.setText("请输入产品信息");
			startAddJianjie();
		}
		// startList();
	}

	public void things(View view) {

	}

	/**
	 * 填写备注内容
	 */
	public void startAddJianjie() {
		city_lv.setVisibility(View.GONE);
		city_rl.setVisibility(View.GONE);
		add_rl.setVisibility(View.VISIBLE);
		submit_btn.setVisibility(View.VISIBLE);
		if (!"".equals(Name)) {
			content_et.setText(Name);
		}

		submit_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("".equals(content_et.getText())) {
					Utils.showToast("输入的内容不能为空");
				} else {
					if ("104".equals(intentNum)
							&& content_et.getText().toString().trim().length() > 50) {
						Utils.showToast("备注内容不能超过50个字");
					} else {
						Intent intent = new Intent();
						int resultCode = Integer.parseInt(intentNum);
						ListActivity.this.setResult(resultCode, intent);
						intent.putExtra("content", content_et.getText()
								.toString());
						ListActivity.this.finish();
					}
				}
			}
		});
	}

	public void startList() {
		list_display = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list_Stores.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list_dialog_title_text", list_Stores[i]);
			if (Name.equals(list_Stores[i])) {
				map.put("list_dialog_img_imgv", "true");
			} else {
				map.put("list_dialog_img_imgv", "false");
			}
			list_display.add(map);
		}
		listv_adapter = new LvSimpleAdapter(ListActivity.this, list_display,
				R.layout.list_dialog_bg, fr, id, "");
		listv.setAdapter(listv_adapter);
	}

	public void returnActivity(String returnStr, String cityNum) {
		Intent intent = new Intent();
		int resultCode = 0;
		if ("AddOrderActivity".equals(toActivity)) {
			intent.setClass(ListActivity.this, AddOrderActivity.class);
			if ("101".equals(intentNum)) {
				resultCode = 101;
			} else if ("102".equals(intentNum)) {
				resultCode = 102;
			} else if("103".equals(intentNum)){
				resultCode = 103;
			}else if ("104".equals(intentNum)) {
				resultCode = 104;
			}
		} else if ("AddBuyActivity".equals(toActivity)) {
			intent.setClass(ListActivity.this, AddOrderActivity.class);
			if ("101".equals(intentNum)) {
				resultCode = 101;
			} else if ("102".equals(intentNum)) {
				resultCode = 102;
			}
		} else if ("AddSupplyActivity".equals(toActivity)) {
			intent.setClass(ListActivity.this, AddOrderActivity.class);
			if ("101".equals(intentNum)) {
				resultCode = 103;
			}
		}
		ListActivity.this.setResult(resultCode, intent);
		intent.putExtra("cityName", returnStr);
		intent.putExtra("cityNum", cityNum);
		ListActivity.this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String str1 = list_Stores[arg2];
		returnActivity(list_Stores[arg2], arg2 + "");
	}

}
