package com.pyxx.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

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
import com.pyxx.entity.CommonUtil;
import com.pyxx.entity.Data;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 用户反馈 界面
 * 
 * @author wll
 */
public class FeedBack extends BaseActivity {
	ImageView titleimg, send;
	Vibrator vibrator;
	TelephonyManager tm;
	public EditText info, email;
	public TextView zishu_textView;
	public boolean isEmailNotNull = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_st_feedback);
		
		info = (EditText) findViewById(R.id.suggest_info);
		email = (EditText) findViewById(R.id.qq_info);
		zishu_textView = (TextView) findViewById(R.id.zishu_textView);
		int ns = num - getCharacterNum(info.getText().toString());
		zishu_textView.setText("" + ns / 2);
		onfindview();
		addListener();
		findViewById(R.id.title_send).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Integer.parseInt(zishu_textView.getText().toString()) < 0) {
					Utils.showToast("字数不能超过140个,请编辑后再发送……");
				} else {
					send();
				}
			}
		});
	}

	public void onfindview() {

	}

	/**
	 * 反馈意见数提交
	 */
	public int num = 280;

	public void send() {
		if (info.getText() != null && info.getText().length() > 0) {
			PerfHelper.setInfo("suggest", info.getText().toString());
			if (info.getText().length() > 1000) {
				Utils.showToast(R.string.too_long_tip);
				return;
			}
			if (!(CommonUtil.emailFormat(email.getText().toString().trim()))) {
				Utils.showToast("邮箱格式错误，请确认后再输入");
				return;
			}
		} else {
			Utils.showToast("请输入您的反馈意见");
			return;
		}
		String e_mail = email.getText().toString();

		// ClientInfo.senduser_feedback(e_mail, info.getText().toString(), h);
		new AlertDialog.Builder(FeedBack.this).setMessage("是否确认提交？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Utils.showProcessDialog(FeedBack.this, "正在提交...");
						new Thread() {
							public void run() {
								new FeedbackAync().execute((Void) null);
							};
						}.start();
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).show();
	}

	public void things(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			break;
		case R.id.title_send:
			break;
		}
	}

	public Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Utils.dismissProcessDialog();
			switch (msg.what) {
			case 5:
				break;
			case 1:
				Utils.showToast("反馈成功");
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
				int number = num - getCharacterNum(info.getText().toString());
				zishu_textView.setText("" + number / 2);
				zishu_textView.setTextColor(getResources().getColor(
						R.color.main_head_color));
				if (number / 2 <= 0) {
					zishu_textView.setTextColor(getResources().getColor(
							R.color.red));
				} else {
					zishu_textView.setTextColor(getResources().getColor(
							R.color.black));
				}
				selectionStart = info.getSelectionStart();
				selectionEnd = info.getSelectionEnd();

				if (getCharacterNum(info.getText().toString()) >= num) {
					if (number / 2 < 0) {
					} else {
						s.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						info.setText(s);
						info.setSelection(tempSelection);
					}
				}
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

	class FeedbackAync extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public FeedbackAync() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("feedback.content", info.getText()
					.toString()));
			param.add(new BasicNameValuePair("feedback.email", email.getText()
					.toString()));
			String json;
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(
						getResources().getString(R.string.url_save_feedback),
						param);
				if (ShareApplication.debug) {
					System.out.println("用户反馈返回:" + json);
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
			if (!Utils.isNetworkAvailable(FeedBack.this)) {
				Utils.showToast(R.string.network_error);
				finish();
				return;
			}

			if (result == null) {
				Toast.makeText(FeedBack.this, "提交失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			} else if ("0".equals(result.get("responseCode"))) {
				Toast.makeText(FeedBack.this, "提交失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			} else if ("1".equals(result.get("responseCode"))) {
				Toast.makeText(FeedBack.this, "提交成功", Toast.LENGTH_SHORT)
						.show();

				FeedBack.this.finish();
			}
		}
	}
}
