package com.pyxx.part_activiy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.baseui.BaseActivity;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 评论界面
 * 
 * @author wll
 */
public class Review extends BaseActivity {
	ActionBar actionBar;
	ImageView titleimg, returniv;
	Vibrator vibrator;
	TelephonyManager tm;
	public EditText info;
	public TextView title, send;
	public int zscount;
	public Listitem plitem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_write_pl);

		if (getIntent().getExtras() != null) {
			plitem = (Listitem) getIntent().getExtras().get("item");
			// System.out.println("select_entity:sss" + select_entity);
		}
		title = (TextView) findViewById(R.id.title_title);
		title.setText("写评论");
		send = (TextView) findViewById(R.id.tijiao_tv);
		send.setVisibility(View.VISIBLE);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (zscount < 0) {
					Utils.showToast("字数不能超过140个,请编辑后再发送……");
				} else {
					send();
				}
			}
		});
		returniv = (ImageView) findViewById(R.id.title_back);
		returniv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		info = (EditText) findViewById(R.id.xpl_tv);
		zscount = (num - getCharacterNum(info.getText().toString())) / 2;

		onfindview();
		addListener();

	}

	public void onfindview() {

	}

	/**
	 * 评论数提交
	 */
	public int num = 280;

	public void send() {
		if (info.getText() != null && info.getText().length() > 0) {
			PerfHelper.setInfo("suggest", info.getText().toString());
			if (info.getText().length() > 1000) {
				Utils.showToast(R.string.too_long_tip);
				return;
			}
		} else {
			Utils.showToast("请输入您的评论内容");
			return;
		}

		new AlertDialog.Builder(Review.this).setMessage("是否确认提交？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Utils.showProcessDialog(Review.this, "正在提交...");

						new ReviewAync().execute((Void) null);

					};
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).show();

	}

	@Override
	public void things(View v) {
		// switch (v.getId()) {
		// case R.id.btn_title_return:
		// finish();
		// break;
		// case R.id.btn_title_right:
		// if (zscount < 0) {
		// Utils.showToast("字数不能超过140个,请编辑后再发送……");
		// } else {
		// send();
		// }
		// break;
		// }
	}

	public Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Utils.dismissProcessDialog();
			switch (msg.what) {
			case 5:
				break;
			case 1:
				Utils.showToast("评论成功");
				finish();
				break;
			case 2:
				Utils.showToast("发送失败");
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void addListener() {
		info.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				selectionStart = info.getSelectionStart();
				selectionEnd = info.getSelectionEnd();

			}
		});
	}

	/**
	 * @description 获取一段字符串的字符个数（包含中英文，一个中文算2个字符）
	 * @param content
	 * @return
	 */
	public static int getCharacterNum(String content) {
		if (null == content || "".equals(content)) {
			return 0;
		} else {
			return (content.length() + getChineseNum(content));
		}
	}

	/**
	 * @description 返回字符串里中文字或者全角字符的个数
	 * @param s
	 * @return
	 */
	public static int getChineseNum(String s) {

		int num = 0;
		char[] myChar = s.toCharArray();
		for (int i = 0; i < myChar.length; i++) {
			if ((char) (byte) myChar[i] != myChar[i]) {
				num++;
			}
		}
		return num;
	}

	/**
	 * 提交评论的接口
	 * 
	 * @author wll
	 */
	class ReviewAync extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public ReviewAync() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("comment.content", info.getText()
					.toString()));
			param.add(new BasicNameValuePair("comment.pid", plitem.nid));
			param.add(new BasicNameValuePair("comment.userId", PerfHelper
					.getStringData(PerfHelper.P_USERID)));
			param.add(new BasicNameValuePair("comment.menuId", plitem.list_type));
			String json;
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(
						getResources().getString(R.string.url_save_review),
						param);
				if (ShareApplication.debug) {
					System.out.println("用户评论返回:" + json);
				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
			} catch (NotFoundException e) {
				e.printStackTrace();
				mhashmap = null;
			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}

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
			if (!Utils.isNetworkAvailable(Review.this)) {
				Utils.showToast(R.string.network_error);
				finish();
				return;
			}
			if (result == null) {
				Toast.makeText(Review.this, "提交失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			}
			// 登录成功加入用户信息到public_info类中；
			else if ("1".equals(result.get("responseCode"))) {
				Toast.makeText(Review.this, "提交成功", Toast.LENGTH_SHORT).show();
				Review.this.finish();
			} else {
				Toast.makeText(Review.this, "提交失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
