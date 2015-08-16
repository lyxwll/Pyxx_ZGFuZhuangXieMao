package com.pyxx.part_activiy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.pyxx.app.ShareApplication;
import com.pyxx.baseui.BaseActivity;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 图片查看
 * 
 * @author wll
 */
public class ShowBigImgActivity extends BaseActivity {
	private Listitem item = null;
	private String[] imglist;
	private int nowCount = 1;
	private ImageView img_iv;
	private TextView img_title;
	private int len = 0;
	private JSONArray array = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_part_img);

		if (getIntent().getExtras() != null) {
			nowCount = getIntent().getExtras().getInt("nowCount");
			item = (Listitem) getIntent().getExtras().get("item");
		}

		findViewById(R.id.text_title_back).setVisibility(View.VISIBLE);
		findViewById(R.id.text_title_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

		img_title = ((TextView) findViewById(R.id.text_title_img));
		img_iv = (ImageView) findViewById(R.id.img_iv);

		new SelectImgs().execute((Void) null);
		img_iv.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				nowCount++;
				init();
				return false;
			}

		});
	}

	public void init() {
		len = imglist.length;
		if (nowCount >= len) {
			nowCount = nowCount % (len);
		}

		if (imglist[nowCount] != null && imglist[nowCount].length() > 10) {
			String[] iconlist = imglist[nowCount].split(",");
			ShareApplication.mImageWorker.loadImage(iconlist[0], img_iv);
			img_iv.setVisibility(View.VISIBLE);
		} else {
			img_iv.setImageResource(R.drawable.list_qst);
			img_iv.setVisibility(View.VISIBLE);
		}

		img_title.setText((nowCount + 1) + "/" + len);
	}

	public void things(View view) {
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	class SelectImgs extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public SelectImgs() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				String jsurl = ShareApplication.share.getResources().getString(
						R.string.url_sel_imgs)
						+ "img.menuId="
						+ item.list_type
						+ "&img.pid="
						+ item.nid;
				;

				json = DNDataSource.list_FromNET(jsurl, param);

				if (ShareApplication.debug) {
					System.out.println("图片查看返回:" + json);
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
			data.obj1 = jsonobj.getString("code");
			try {
				JSONArray array = jsonobj.getJSONArray("lists");
				if (array != null && array.length() > 0) {
					imglist = new String[array.length()];
					for (int i = 0; i < array.length(); i++) {
						JSONObject js = array.getJSONObject(i);
						imglist[i] = js.getString("url");
					}
				} else {
					if (item.icon == null || "".equals(item.icon)) {
						imglist = new String[0];
					} else {
						imglist = new String[1];
						imglist[0] = item.icon;
					}
				}
			} catch (Exception e) {

			}
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(ShowBigImgActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if ("1".equals(result.get("responseCode"))) {
				init();
			} else {
			}
		}
	}

}
