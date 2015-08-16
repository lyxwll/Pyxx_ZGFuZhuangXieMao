package com.pyxx.part_activiy;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pyxx.dao.MySSLSocketFactory;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 文章评论界面
 * 
 */
public class AritcleCommentActivity extends Activity {
	public static final String TAG = "CommentActivity";
	public View mLoad;
	public TextView mLoad_Text;
	public EditText mEditText;
	public TextView mBtnSend;
	public String mArticleId;
	public TextView zishu_textView;
	public int num = 280;
	private boolean mCommentNeedBind = false;
	private RatingBar mBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_comment);
		init();
	}

	/**
	 * 初始化数据
	 */
	public void init() {
		mArticleId = getIntent().getStringExtra("id");
		mLoad = findViewById(R.id.loading);
		mLoad_Text = (TextView) findViewById(R.id.loading_text);
		mLoad_Text.setText("正在发送...");
		mLoad.setVisibility(View.GONE);
		mBar = (RatingBar) findViewById(R.id.rb_addSeller_grade);
		initLayout();
	}

	/**
	 * 初始化控件
	 */
	public void initLayout() {
		mEditText = (EditText) findViewById(R.id.comment_article);
		mBtnSend = (TextView) findViewById(R.id.send_btn);
		zishu_textView = (TextView) findViewById(R.id.zishu_textView);
		mEditText.addTextChangedListener(new TextWatcher() {
			public CharSequence temp;
			public int selectionStart;
			public int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				int number = num
						- getCharacterNum(mEditText.getText().toString());
				zishu_textView.setText("" + number / 2);
				if (number / 2 <= 0) {
					zishu_textView.setTextColor(getResources().getColor(
							R.color.red));
				} else {
					zishu_textView.setTextColor(getResources().getColor(
							android.R.color.black));
				}
				selectionStart = mEditText.getSelectionStart();
				selectionEnd = mEditText.getSelectionEnd();
				// if (getCharacterNum(mEditText.getText().toString()) >= num) {
				// if (number / 2 < 0) {
				//
				// } else {
				// s.delete(selectionStart - 1, selectionEnd);
				// int tempSelection = selectionEnd;
				// mEditText.setText(s);
				// mEditText.setSelection(tempSelection);
				// }
				// }

			}
		});
	}

	String name = "游客";

	/**
	 * 点击事件
	 * 
	 * @param view
	 */
	public void things(View view) {
		final int action = view.getId();
		if (action == R.id.comment_back) {
			this.finish();
		} else if (action == R.id.send_btn) {
			if (PerfHelper.getBooleanData(PerfHelper.P_SHARE_STATE + "sina")) {
				name = PerfHelper.getStringData(PerfHelper.P_SHARE_NAME
						+ "sina");
			} else if (PerfHelper.getBooleanData(PerfHelper.P_SHARE_STATE
					+ "qq")) {
				name = PerfHelper.getStringData(PerfHelper.P_SHARE_NAME + "qq");
			}
			final String content = mEditText.getText().toString();
			if (TextUtils.isEmpty(content)) {
				Utils.showToast("评论不能为空");
				return;
			}
			View v = getWindow().peekDecorView();
			if (v != null) {
				InputMethodManager inputmanger = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			if ("游客".equals(name)) {
				mLoad.setVisibility(View.VISIBLE);
				new Thread() {
					public void run() {
						article_comment(mArticleId, "游客", content,
								mBar.getRating() + "", mHandler);
					};
				}.start();
				return;
			} else {
				mLoad.setVisibility(View.VISIBLE);
				new Thread() {
					public void run() {
						article_comment(mArticleId, name, content,
								mBar.getRating() + "", mHandler);
					};
				}.start();

			}
		} else {

		}
	}

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int action = msg.what;
			mLoad.setVisibility(View.GONE);
			switch (action) {
			case 1:
				mEditText.setText("");
				Utils.showToast("评论成功");
				Intent intent = new Intent();
				setResult(0x2, intent);
				// 关闭当前，回到现实评论的界面；
				finish();
				break;
			case 2:
				Utils.showToast("评论失败");
				break;
			default:
				break;
			}
		}

	};

	public void article_comment(String id, String username, String comment,
			String level, Handler mHandler) {
		final List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("sellerId", id));
		param.add(new BasicNameValuePair("user", username));
		param.add(new BasicNameValuePair("content", comment));
		param.add(new BasicNameValuePair("level", level));
		String json;
		Message msg = new Message();
		String str = null;
		try {
			json = MySSLSocketFactory.getinfo(
					getResources().getString(
							R.string.citylife_putComment_list_url), param);
			JSONObject jo = new JSONObject(json);
			if (jo.getString("responseCode").equals("0")) {
				str = "评论成功";
				msg.what = 1;
			} else {
				str = "失败";
				msg.what = 2;
			}
		} catch (Exception e) {
			str = "";
			msg.what = 2;
		}
		msg.obj = str;
		mHandler.sendMessage(msg);
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
}
