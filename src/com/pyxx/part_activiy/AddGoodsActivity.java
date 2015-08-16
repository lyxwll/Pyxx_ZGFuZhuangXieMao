package com.pyxx.part_activiy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 加入购物车:1||立即购买:statue=2
 * 
 * @author wll
 */
public class AddGoodsActivity extends Activity {

	private View btn_back;// 返回按钮
	private TextView txt_title, txt_count, txt_jiage;
	private Button add_btn;
	private ImageView jia_btn, jian_btn;
	private ImageView img_news;
	Listitem select_entity;
	private int total = 1;
	private String statue = "0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 接收从上一个页面传过来的值
		if (getIntent().getExtras() != null) {
			select_entity = (Listitem) getIntent().getExtras().get("item");
			statue = getIntent().getExtras().getString("statue");
		}

		setContentView(R.layout.activity_add_goods);

		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		findViewById(R.id.title_btn_right).setVisibility(View.GONE);
		TextView title = (TextView) findViewById(R.id.title_title);

		// 初始化所有控件
		initView();
		//跳转类型
		if ("2".equals(statue)) {
			title.setText("立即购买");
			add_btn.setText("确定购买");
		} else {
			title.setText("加入购物车");
			add_btn.setText("确定加入");
		}
		// +(加)按钮
		jia_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				total += 1;
				txt_count.setText(total + "");
				txt_jiage.setText("￥" + total
						* (Double.parseDouble(select_entity.other)));
			}
		});
		// -(减)按钮
		jian_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (total > 1) {
					total -= 1;
				} else {
					total = 1;
				}
				txt_count.setText(total + "");
				txt_jiage.setText("￥" + total
						* (Double.parseDouble(select_entity.other)));
			}
		});
		// 确定按钮
		add_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("2".equals(statue)) {
					String str = "[";
					str += "{logo:'";
					str += select_entity.icon;
					str += "',name:'";
					str += select_entity.title;
					str += "',id:'";
					str += select_entity.nid;
					str += "',num:'";
					str += total;
					str += "',price:'";
					str += select_entity.other;
					str += "'}]";
					Intent intent = new Intent();
					intent.setClass(AddGoodsActivity.this,
							AddOrderActivity.class);
					intent.putExtra("ids", select_entity.nid);
					intent.putExtra("statue", "3");
					intent.putExtra("infos", str);
					intent.putExtra("num", total + "");
					intent.putExtra(
							"totalValue",
							"" + total
									* (Double.parseDouble(select_entity.other)));
					startActivity(intent);
				} else {
					//
					new AddShopsCar().execute((Void) null);
					
				}
			}
		});

		// 控件对应注册事件
		// 返回按钮
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddGoodsActivity.this.finish();
			}
		});
	}

	public void initView() {

		btn_back = this.findViewById(R.id.title_back);// 返回按钮
		txt_title = (TextView) this.findViewById(R.id.title_goods_tv);// 标题
		txt_count = (TextView) this.findViewById(R.id.count_text);
		txt_jiage = (TextView) this.findViewById(R.id.total_jiage);

		jia_btn = (ImageView) this.findViewById(R.id.jia_iv);
		jian_btn = (ImageView) this.findViewById(R.id.jian_iv);
		add_btn = (Button) this.findViewById(R.id.add_btn);

		img_news = (ImageView) this.findViewById(R.id.icon);

		img_news.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent();
				// intent.setClass(ZixunArticleActivity.this,
				// ShowImgActivity.class);
				// intent.putExtra("item", select_entity);
				// intent.putExtra("img_list", select_entity.icon.split(","));
				// startActivity(intent);
			}
		});

		if (select_entity != null) {

			if (select_entity.u_date != null && select_entity.u_date != "") {
				select_entity.u_date.substring(0, 10);
			}
			txt_title.setText(select_entity.title);
			txt_jiage.setText("￥" + select_entity.other);

			// 新方法
			// bitmap加载就这一行代码，display还有其他重载，详情查看源码
			if (select_entity.icon != null && select_entity.icon.length() > 10) {
				String img_url = select_entity.icon.split(",")[0].trim();
				ShareApplication.mImageWorker.loadImage(img_url, img_news);
				img_news.setVisibility(View.VISIBLE);
			} else {
				img_news.setImageResource(R.drawable.list_qst);
				img_news.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 加入购物车
	 * @author wll
	 */
	class AddShopsCar extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public AddShopsCar() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;

			String js = ShareApplication.share.getResources().getString(
					R.string.url_save_shopscar);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js + "shop.userId="
						+ PerfHelper.getStringData(PerfHelper.P_USERID)
						+ "&shop.commodityId=" + select_entity.nid
						+ "&shop.menuId=" + select_entity.list_type
						+ "&shop.num=" + total + "&shop.types="
						+ select_entity.other1, param);
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}

		// @SuppressWarnings("unchecked")
		public Data parseJson(String json) throws Exception {
			Data data = new Data();
			JSONObject jsonobj = new JSONObject(json);
			if (jsonobj.has("code")) {
				int code = jsonobj.getInt("code");
				if (code != 1) {
					data.obj1 = jsonobj.getString("code");// 返回状态
					// 0，有数据成功返回，-1，-2，无数据返回，1，返回异常
					return data;
				} else {
					data.obj1 = jsonobj.getString("code");// 返回状态
				}
			}
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(AddGoodsActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(AddGoodsActivity.this, "加入购物车失败",
						Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				Toast.makeText(AddGoodsActivity.this, "加入购物车成功",
						Toast.LENGTH_SHORT).show();
				if (PerfHelper.getStringData(PerfHelper.P_SHARE_SEX) == null
						|| "".equals(PerfHelper
								.getStringData(PerfHelper.P_SHARE_SEX))) {
					PerfHelper.setInfo(PerfHelper.P_SHARE_SEX, "1");
				} else {
					PerfHelper
							.getStringData(PerfHelper.P_SHARE_SEX);
					int allcount = Integer.parseInt(PerfHelper
							.getStringData(PerfHelper.P_SHARE_SEX)) + 1;
					PerfHelper.setInfo(PerfHelper.P_SHARE_SEX, allcount + "");
				}
				AddGoodsActivity.this.finish();
			} else {
				Toast.makeText(AddGoodsActivity.this, "加入购物车失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
