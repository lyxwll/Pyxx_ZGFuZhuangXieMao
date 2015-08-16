package com.pyxx.part_activiy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.ui.ListActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 添加订单 填写订单界面
 * 
 * @author wll
 */
public class AddOrderActivity extends Activity {

	private View btn_back;// 返回按钮
	private TextView txt_ckqd, txt_psfs, txt_beizhu, txt_zhifu, txt_value,
			txt_addr, txt_myinfo;
	private RelativeLayout rl_ckqd, rl_psfs, rl_zhifu, rl_beizhu, rl_addr;
	private Button pay_btn;
	private GridView list_cm = null, list_ys = null;
	private ListAdapter adapter;
	private ListAdapter2 adapter2;
	Listitem select_entity;
	private int selected1 = -1, selected2 = -1;
	private String a1[], a2[];
	private String statue = "0", allvalue = "";
	private Intent intent = null;
	private String[] str_zffs, str_psfs, str_addr;
	ArrayList<Listitem> list_zffs, list_psfs, list_addr;
	private String zffsId = "", psfsId = "", addrId = "";
	private Data psfs_data = null;
	private Data zffs_data = null;
	private String info = "";
	private boolean flag = false;
	private Listitem item = null;
	public static String addr = "", name = "", zffstitle = "", psfstitle = "",
			beizhu = "", phone = "";
	public static Double emailfree = 0.0, befor_emailfree = 0.0;
	public String ids = "";
	public String[] shopsCarId;
	private String num = "0";
	// 送货类型
	private String[] str_psname = new String[] { "免费配送(0元)", "快递配送(10元)",
			"EMS配送(20元)", "普通配送(5元)" };
	private String[] str_psvalue = new String[] { "0", "10", "20", "5" };
	private String[] str_payname = new String[] { "货到付款(当前只支持)" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.addorder);

		// new SelectPsfs().execute((Void) null);

		getZffs();//
		getPsfs();//

		if (getIntent().getExtras() != null) {
			befor_emailfree = 0.0;
			allvalue = getIntent().getExtras().getString("totalValue");
			ids = getIntent().getExtras().getString("ids");
			statue = getIntent().getExtras().getString("statue");
			num = getIntent().getExtras().getString("num");

			if ("2".equals(statue)) {
				item = (Listitem) getIntent().getExtras().get("item");
				info = item.img_list_2;
				addrId = item.other1;
				psfstitle = item.other;
				for (int i = 0; i < str_psname.length; i++) {
					if (item.other.equals(str_psname[i])) {
						emailfree = Double.parseDouble(str_psvalue[i]);
						psfsId = i + "";
					}
				}
				zffstitle = item.other3;
				beizhu = item.des;
				allvalue = item.other2;
			} else {
				info = getIntent().getStringExtra("infos");
				beizhu = "";
				psfstitle = "";
				zffstitle = "";
			}
		}

		findViewById(R.id.title_back).setVisibility(View.VISIBLE);

		// 初始化所有控件
		initView();

		// 查看清单
		rl_ckqd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent();
				intent.setClass(AddOrderActivity.this, PaymentActivity.class);
				intent.putExtra("info", info);
				intent.putExtra("allvalue", allvalue);
				// intent.putExtra("type", type);
				startActivity(intent);
			}
		});

		// 配送方式
		rl_psfs.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(AddOrderActivity.this, ListActivity.class);
				intent.putExtra("Num", "101");
				intent.putExtra("toActivity", "AddOrderActivity");
				intent.putExtra("list", str_psfs);
				if (!"".equals(txt_psfs.getText())) {
					intent.putExtra("Name", txt_psfs.getText());
				} else {
					intent.putExtra("Name", "");
				}
				startActivityForResult(intent, 101);
			}
		});

		// 支付方式
		rl_zhifu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(AddOrderActivity.this, ListActivity.class);
				intent.putExtra("Num", "103");
				intent.putExtra("toActivity", "AddOrderActivity");
				intent.putExtra("list", str_zffs);
				if (!"".equals(txt_zhifu.getText())) {
					intent.putExtra("Name", txt_zhifu.getText());
				} else {
					intent.putExtra("Name", "");
				}
				startActivityForResult(intent, 103);
			}
		});

		// 备注
		rl_beizhu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(AddOrderActivity.this, ListActivity.class);
				intent.putExtra("Num", "104");
				intent.putExtra("toActivity", "AddOrderActivity");
				if (!"".equals(txt_beizhu.getText())) {
					intent.putExtra("Name", txt_beizhu.getText());
				} else {
					intent.putExtra("Name", "");
				}
				startActivityForResult(intent, 104);
			}
		});

		// 收货地址
		rl_addr.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (list_addr == null || list_addr.size() == 0) {
					if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
						intent = new Intent();
						intent.setClass(AddOrderActivity.this,
								AddAddrActivity.class);
						startActivity(intent);
					} else {
						Utils.showToast("请先登录");
					}
				} else {
					intent = new Intent(AddOrderActivity.this,
							ListActivity.class);
					intent.putExtra("Num", "102");
					intent.putExtra("toActivity", "AddOrderActivity");
					intent.putExtra("list", str_addr);
					if (!"".equals(txt_addr.getText())) {
						intent.putExtra("Name", txt_addr.getText());
					} else {
						intent.putExtra("Name", "");
					}
					startActivityForResult(intent, 102);//
				}
			}
		});

		// 提交订单
		pay_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("".equals(addrId) || "".equals(psfsId) || "".equals(zffsId)) {
					Utils.showToast("请把信息填写完整");
				} else {
					if (!flag) {
						flag = true;
						new SaveOrder().execute((Void) null);
					}
				}
			}
		});

		// 控件对应注册事件
		// 返回按钮
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddOrderActivity.this.finish();
			}
		});
	}

	/**
	 * 获取支付方式信息
	 */
	public void getZffs() {
		zffs_data = new Data();
		list_zffs = (ArrayList<Listitem>) new ArrayList();
		for (int i = 0; i < str_payname.length; i++) {
			Listitem list = new Listitem();
			list.nid = i + "";
			list.title = str_payname[i];
			list_zffs.add(list);
			zffs_data.list.add(list);
		}
		str_zffs = new String[list_zffs.size()];
		for (int i = 0; i < list_zffs.size(); i++) {
			str_zffs[i] = list_zffs.get(i).title;
		}
	}

	/**
	 * 获取 配送方式信息
	 */
	public void getPsfs() {
		psfs_data = new Data();
		list_psfs = (ArrayList<Listitem>) new ArrayList();
		for (int i = 0; i < str_psname.length; i++) {
			Listitem o = new Listitem();
			o.nid = i + "";
			o.title = str_psname[i];
			o.isad = str_psvalue[i];
			list_psfs.add(o);
			psfs_data.list.add(o);
		}
		str_psfs = new String[list_psfs.size()];
		for (int i = 0; i < list_psfs.size(); i++) {
			str_psfs[i] = list_psfs.get(i).title;
		}
	}

	public void initView() {
		btn_back = this.findViewById(R.id.title_back);// 返回按钮
		txt_addr = (TextView) this.findViewById(R.id.addr_tv);
		txt_ckqd = (TextView) this.findViewById(R.id.ckqd_tv);
		txt_psfs = (TextView) this.findViewById(R.id.psfs_text);
		txt_zhifu = (TextView) this.findViewById(R.id.zhifu_text);
		txt_beizhu = (TextView) this.findViewById(R.id.beizhu_text);
		txt_value = (TextView) this.findViewById(R.id.total_value);
		txt_myinfo = (TextView) this.findViewById(R.id.my_info);

		pay_btn = (Button) this.findViewById(R.id.fukuan_btn);

		rl_addr = (RelativeLayout) this.findViewById(R.id.addr_rl);
		rl_ckqd = (RelativeLayout) this.findViewById(R.id.ckqd_rl);
		rl_psfs = (RelativeLayout) this.findViewById(R.id.psfs_rl);
		rl_zhifu = (RelativeLayout) this.findViewById(R.id.zhifu_rl);
		rl_beizhu = (RelativeLayout) this.findViewById(R.id.beizhu_rl);

		new SelectAddr().execute((Void) null);

		if ("2".equals(statue)) {
			pay_btn.setVisibility(View.INVISIBLE);
		} else {
			pay_btn.setVisibility(View.VISIBLE);
		}

		if (psfstitle != null && !"".equals(psfstitle)) {
			txt_psfs.setText(psfstitle);
		} else {
			txt_psfs.setText("");
			txt_psfs.setHint("请选择");
		}

		if (zffstitle != null && !"".equals(zffstitle)) {
			txt_zhifu.setText(zffstitle);
		} else {
			txt_zhifu.setText("");
			txt_zhifu.setHint("请选择");
		}

		if (beizhu != null && !"".equals(beizhu)) {
			txt_beizhu.setText(beizhu);
		} else {
			txt_beizhu.setText("");
			txt_beizhu.setHint("限50字以内");
		}

		txt_value.setText("￥" + allvalue);
	}

	/**
	 * 显示默认的地址
	 */
	public void showDefaultAddr() {
		if ("2".equals(statue)) {
			for (int j = 0; j < list_addr.size(); j++) {
				Listitem itm = list_addr.get(j);
				if (addrId.equals(itm.nid)) {
					txt_addr.setText(itm.title);
					txt_myinfo.setText(itm.des + "    " + itm.phone);
				}
			}
		} else {
			if (!"".equals(addr) && addr != null) {
				txt_addr.setText(addr);
				txt_myinfo.setText(name + "    " + phone);
			} else {
				if (list_addr == null) {
					txt_addr.setText("您还没有创建收货地址哦！");
					txt_myinfo.setText("");
				} else {
					Listitem li = null;
					for (int i = 0; i < list_addr.size(); i++) {
						Listitem it = list_addr.get(i);
						if ("1".equals(it.isad)) {
							li = it;
						}
					}
					if (li != null) {
						addrId = li.nid;
						txt_addr.setText(li.title);
						txt_myinfo.setText(li.des + "    " + li.phone);
					} else {
						txt_addr.setText("您还没有默认地址哦！");
						txt_myinfo.setText("");
					}
				}
			}
		}
	}

	public void noAddr() {
		txt_addr.setText("您还没有创建收货地址哦！");
		txt_myinfo.setText("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 101:
			if (data != null) {
				if (!"".equals(data.getStringExtra("cityName"))) {
					txt_psfs.setText(data.getStringExtra("cityName"));
					psfsId = list_psfs.get(Integer.parseInt(data
							.getStringExtra("cityNum"))).nid;
					Listitem lis = (Listitem) psfs_data.list.get(Integer
							.parseInt(data.getStringExtra("cityNum")));
					if ("2".equals(statue)) {
						txt_value
								.setText("￥"
										+ (Double.parseDouble(allvalue)
												+ Double.parseDouble(lis.isad) - emailfree));
						allvalue = Double.parseDouble(allvalue)
								+ Double.parseDouble(lis.isad) - emailfree + "";
					} else {
						txt_value
								.setText("￥"
										+ (Double.parseDouble(allvalue)
												+ Double.parseDouble(lis.isad) - befor_emailfree));
						allvalue = Double.parseDouble(allvalue)
								+ Double.parseDouble(lis.isad)
								- befor_emailfree + "";
					}
					for (int a = 0; a < str_psvalue.length; a++) {
						if ((a + "").equals(psfsId)) {
							befor_emailfree = Double
									.parseDouble(str_psvalue[a]);
							emailfree = Double.parseDouble(str_psvalue[a]);
						}
					}
					// new SelectArea().execute((Void) null);
				}
			}
			break;
		case 102:
			if (data != null) {
				if (!"".equals(data.getStringExtra("cityName"))) {
					txt_addr.setText(data.getStringExtra("cityName"));
					addrId = list_addr.get(Integer.parseInt(data
							.getStringExtra("cityNum"))).nid;
					String str_name = list_addr.get(Integer.parseInt(data
							.getStringExtra("cityNum"))).des;
					String str_phone = list_addr.get(Integer.parseInt(data
							.getStringExtra("cityNum"))).phone;
					txt_myinfo.setText(str_name + "    " + str_phone);
				}
			}
			break;
		case 103:
			if (data != null) {
				if (!"".equals(data.getStringExtra("cityName"))) {
					txt_zhifu.setText(data.getStringExtra("cityName"));
					zffsId = list_zffs.get(Integer.parseInt(data
							.getStringExtra("cityNum"))).nid;
					Listitem lis = (Listitem) zffs_data.list.get(Integer
							.parseInt(data.getStringExtra("cityNum")));
				}
			}
			break;
		case 104:
			if (data != null) {
				if (!"".equals(data.getStringExtra("content"))) {
					txt_beizhu.setText(data.getStringExtra("content"));
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * class SelectPsfs extends AsyncTask<Void, Void, HashMap<String, Object>> {
	 * 
	 * public SelectPsfs() { }
	 * 
	 * @Override protected HashMap<String, Object> doInBackground(Void...
	 * params) {
	 * 
	 * List<NameValuePair> param = new ArrayList<NameValuePair>(); String json;
	 * 
	 * String js = ShareApplication.share.getResources().getString(
	 * R.string.url_sel_psfs); // 加入集合 HashMap<String, Object> mhashmap = new
	 * HashMap<String, Object>(); try { json = DNDataSource.list_FromNET(js,
	 * param); if (ShareApplication.debug) { System.out.println("用户登录返回:" +
	 * json);
	 * 
	 * } Data date = parseJson(json); mhashmap.put("responseCode", date.obj1);
	 * mhashmap.put("result", date.list); } catch (Exception e) {
	 * e.printStackTrace(); mhashmap = null; } return mhashmap; }
	 * 
	 * @SuppressWarnings("unchecked") public Data parseJson(String json) throws
	 * Exception { Data data = new Data(); JSONObject jsonobj = new
	 * JSONObject(json); if (jsonobj.has("responseCode")) { int code =
	 * jsonobj.getInt("responseCode"); if (code != 0) { data.obj1 =
	 * jsonobj.getString("responseCode");// 返回状态 // 0，有数据成功返回，-1，-2，无数据返回，1，返回异常
	 * return data; } else { data.obj1 = jsonobj.getString("responseCode");//
	 * 返回状态 } } JSONArray jsonay = jsonobj.getJSONArray("results"); int count =
	 * jsonay.length(); for (int i = 0; i < count; i++) { Listitem o = new
	 * Listitem(); JSONObject obj = jsonay.getJSONObject(i); o.nid =
	 * obj.getString("id"); try { if (obj.has("name")) {// 排序 o.title =
	 * obj.getString("name"); } if (obj.has("price")) {// 排序 o.isad =
	 * obj.getString("price"); } } catch (Exception e) { } o.getMark(); //
	 * o.list_type = obj.getString("type"); data.list.add(o); } psfs_data =
	 * data; return data; }
	 * 
	 * @Override protected void onPostExecute(HashMap<String, Object> result) {
	 * super.onPostExecute(result); Utils.dismissProcessDialog(); // 无网提示； if
	 * (!Utils.isNetworkAvailable(AddOrderActivity.this)) {
	 * Utils.showToast(R.string.network_error); return; } if (result == null) {
	 * Toast.makeText(AddOrderActivity.this, "请求失败，请稍后再试",
	 * Toast.LENGTH_SHORT).show(); } else if
	 * ("0".equals(result.get("responseCode"))) { list_psfs =
	 * (ArrayList<Listitem>) result.get("result"); str_psfs = new
	 * String[list_psfs.size()]; for (int i = 0; i < list_psfs.size(); i++) {
	 * str_psfs[i] = list_psfs.get(i).title; } } else {
	 * Toast.makeText(AddOrderActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
	 * } } }
	 */

	/**
	 * 获取 选择用户收货地址
	 * 
	 * @author wll
	 */
	class SelectAddr extends AsyncTask<Void, Void, HashMap<String, Object>> {

		/**
		 * 获取 选择用户收货地址
		 */
		public SelectAddr() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;

			String js = ShareApplication.share.getResources().getString(
					R.string.url_sel_addr);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js + "address.userId="
						+ PerfHelper.getStringData(PerfHelper.P_USERID), param);
				// PerfHelper.getStringData(PerfHelper.P_USERID)
				if (ShareApplication.debug) {
					// System.out.println("用户登录返回:" + json);
				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
				mhashmap.put("result", date.list);
			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}

		@SuppressWarnings("unchecked")
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
			JSONArray jsonay = jsonobj.getJSONArray("lists");
			int count = jsonay.length();
			for (int i = 0; i < count; i++) {
				Listitem o = new Listitem();
				JSONObject obj = jsonay.getJSONObject(i);
				o.nid = obj.getString("id");
				try {
					if (obj.has("address")) {// 排序
						o.title = obj.getString("address");
					}
					if (obj.has("name")) {
						o.des = obj.getString("name");
					}
					if (obj.has("tel")) {
						o.phone = obj.getString("tel");
					}
					if (obj.has("isDefualt")) {
						o.isad = obj.getString("isDefualt");
					}
				} catch (Exception e) {
				}
				o.getMark();
				// o.list_type = obj.getString("type");
				data.list.add(o);
			}
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(AddOrderActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(AddOrderActivity.this, "请求失败，请稍后再试",
						Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {

				list_addr = (ArrayList<Listitem>) result.get("result");
				if (list_addr == null || list_addr.size() == 0) {
					noAddr();
				} else {
					str_addr = new String[list_addr.size()];
					for (int i = 0; i < list_addr.size(); i++) {
						str_addr[i] = list_addr.get(i).title;
					}
					showDefaultAddr();
				}
			} else {
				Toast.makeText(AddOrderActivity.this, "收件人地址获取失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 保存订单
	 * 
	 * @author wll
	 */
	class SaveOrder extends AsyncTask<Void, Void, HashMap<String, Object>> {

		/**
		 * 保存订单
		 */
		public SaveOrder() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String str = txt_value.getText().toString().trim();
			str = str.substring(1, str.length());
			param.add(new BasicNameValuePair("userOrder.userId", PerfHelper
					.getStringData(PerfHelper.P_USERID)));
			param.add(new BasicNameValuePair("userOrder.addressId", addrId));
			param.add(new BasicNameValuePair("userOrder.express", txt_psfs
					.getText().toString().trim()));
			param.add(new BasicNameValuePair("userOrder.brief", txt_beizhu
					.getText().toString().trim()));
			param.add(new BasicNameValuePair("userOrder.total", allvalue));
			if ("3".equals(statue)) {
				param.add(new BasicNameValuePair("userOrder.num", num));
				param.add(new BasicNameValuePair("userOrder.menuId", "6"));
				param.add(new BasicNameValuePair("userOrder.commodityId", ids));
			} else {
				param.add(new BasicNameValuePair("userOrder.shops", ids));
			}
			String json;
			String js;
			if ("1".equals(statue)) {
				js = ShareApplication.share.getResources().getString(
						R.string.url_save_order);
			} else {
				js = ShareApplication.share.getResources().getString(
						R.string.url_save_order_now);
			}
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js, param);
				// PerfHelper.getStringData(PerfHelper.P_USERID)
				if (ShareApplication.debug) {
					// System.out.println("用户登录返回:" + json);

				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}

		@SuppressWarnings("unchecked")
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
			if (!Utils.isNetworkAvailable(AddOrderActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(AddOrderActivity.this, "请求失败，请稍后再试",
						Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				Toast.makeText(AddOrderActivity.this, "添加成功",
						Toast.LENGTH_SHORT).show();
				AddOrderActivity.this.finish();
			} else {
				Toast.makeText(AddOrderActivity.this, "添加失败",
						Toast.LENGTH_SHORT).show();
			}
			flag = false;
		}
	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private String[] imgs;

		public ListAdapter(Context context, String[] imgs) {

			this.inflater = LayoutInflater.from(context);
			this.imgs = imgs;
		}

		@Override
		public int getCount() {
			return imgs.length;
		}

		@Override
		public Object getItem(int position) {

			return imgs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.ys_cm_item, null);
				holder = new ViewHolder();
				holder.tv = (TextView) convertView.findViewById(R.id.xz_iv);
				holder.rl_xz = (RelativeLayout) convertView
						.findViewById(R.id.rl_xz_item);
				holder.tv.setTextColor(Color.BLACK);
				holder.tv.setBackgroundResource(R.drawable.szk_bg1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (selected1 == position) {
				holder.tv.setTextColor(Color.WHITE);
				holder.tv.setBackgroundResource(R.drawable.szk_bg2);
			}
			holder.tv.setText(imgs[position]);
			holder.rl_xz.setOnClickListener(new TextViewListener(position));

			return convertView;
		}

		private class ViewHolder {
			TextView tv;
			RelativeLayout rl_xz;
		}

		private class TextViewListener implements View.OnClickListener {
			int position;

			public TextViewListener(int position) {
				this.position = position;
			}

			public void onClick(View v) {
				selected1 = position;
				adapter = new ListAdapter(AddOrderActivity.this, a1);
				list_ys.setAdapter(adapter);
			}
		}
	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter2 extends BaseAdapter {
		private LayoutInflater inflater;
		private String[] imgs;

		public ListAdapter2(Context context, String[] imgs) {

			this.inflater = LayoutInflater.from(context);
			this.imgs = imgs;
		}

		@Override
		public int getCount() {
			return imgs.length;
		}

		@Override
		public Object getItem(int position) {

			return imgs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.ys_cm_item, null);
				holder = new ViewHolder();
				holder.tv = (TextView) convertView.findViewById(R.id.xz_iv);
				holder.rl_xz = (RelativeLayout) convertView
						.findViewById(R.id.rl_xz_item);
				holder.tv.setTextColor(Color.BLACK);
				holder.tv.setBackgroundResource(R.drawable.szk_bg1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (selected2 == position) {
				holder.tv.setTextColor(Color.WHITE);
				holder.tv.setBackgroundResource(R.drawable.szk_bg2);
			}
			holder.tv.setText(imgs[position]);
			holder.rl_xz.setOnClickListener(new TextViewListener(position));

			return convertView;
		}

		private class ViewHolder {
			TextView tv;
			RelativeLayout rl_xz;
		}

		private class TextViewListener implements View.OnClickListener {
			int position;

			public TextViewListener(int position) {
				this.position = position;
			}

			public void onClick(View v) {
				selected2 = position;
				adapter2 = new ListAdapter2(AddOrderActivity.this, a2);
				list_cm.setAdapter(adapter2);
			}
		}
	}

}
