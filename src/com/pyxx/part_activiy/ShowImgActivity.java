package com.pyxx.part_activiy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.baseui.BaseActivity;
import com.pyxx.entity.Listitem;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 商家Logo查看
 * 
 * @author wll
 */
public class ShowImgActivity extends BaseActivity {
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

		try {
			array = new JSONArray(item.img_list_1);
		} catch (Exception e) {
			System.out.println(e);
		}

		if (array != null) {
			len = array.length();
		}
		init();
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
		if (nowCount > len + 1) {
			nowCount = nowCount % (len + 1);
			if (nowCount == 0) {
				nowCount = 1;
			}
		}
		if (nowCount == 1) {
			if (item.icon != null && item.icon.length() > 10) {
				String[] iconlist = item.icon.split(",");
				ShareApplication.mImageWorker.loadImage(iconlist[0], img_iv);
				img_iv.setVisibility(View.VISIBLE);
			} else {
				img_iv.setImageResource(R.drawable.list_qst);
				img_iv.setVisibility(View.VISIBLE);
			}
		}
		if (nowCount > 1) {
			if (len != 0) {
				JSONObject j;
				try {
					j = array.getJSONObject(nowCount - 2);
					String img_url = j.getString("imgUrl").trim();
					ShareApplication.mImageWorker.loadImage(img_url, img_iv);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		img_title.setText(nowCount + "/" + (len + 1));
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
}
