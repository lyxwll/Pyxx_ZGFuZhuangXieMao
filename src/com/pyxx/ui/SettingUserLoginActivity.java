package com.pyxx.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
 * 用户登录界面
 * 
 * @author wll
 */
public class SettingUserLoginActivity extends Activity implements
		OnClickListener {
	EditText mEditTextName, mEditTextPwd;
	TextView mTextViewRegister;
	Button mButtonLogin;
	TextView mButtonRegister;
	private String name = "", pwd = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.user_login_email2);

		initView();

	}

	private void initView() {

		ImageView button = (ImageView) findViewById(R.id.title_back);
		button.setImageResource(R.drawable.return_tb1);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mEditTextName = (EditText) findViewById(R.id.edit_Email);

		mEditTextPwd = (EditText) findViewById(R.id.editt_user_login_pwd);

		mButtonLogin = (Button) findViewById(R.id.btn_user_login_accomplish_login);
		mButtonLogin.setOnClickListener(this);
		mButtonRegister = (TextView) findViewById(R.id.title_register);
		mButtonRegister.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_user_login_accomplish_login) {
			name = mEditTextName.getText().toString();
			pwd = mEditTextPwd.getText().toString();
			if ("".equals(name) || "".equals(pwd)) {
				Utils.showToast("账号或密码不能为空");
			} else {
				new CurrentAync().execute((Void) null);
			}
		} else if (id == R.id.title_register) {
			Intent intent = new Intent();
			intent.setClass(this, UserEmailRegister.class);
			startActivity(intent);
		}
	}

	/**
	 * 登录接口
	 * 
	 * @author wll
	 */
	class CurrentAync extends AsyncTask<Void, Void, HashMap<String, Object>> {
		Listitem o = new Listitem();

		public CurrentAync() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Utils.showProcessDialog(SettingUserLoginActivity.this, "正在登录...");
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("user.phone", name));
			param.add(new BasicNameValuePair("user.passWord", pwd));
			String json;
			String js = ShareApplication.share.getResources().getString(
					R.string.url_user_login);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js, param);
				if (ShareApplication.debug) {
					System.out.println("用户登录返回:" + json);

				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
				mhashmap.put("results", date.list);
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

			JSONObject obj = new JSONObject(jsonobj.getString("userInfo"));
			o.nid = obj.getString("id");
			try {
				if (obj.has("phone")) {// 排序
					o.title = obj.getString("phone");
				}
				if (obj.has("nick")) {// 排序
					o.des = obj.getString("nick");
				}
			} catch (Exception e) {
			}
			o.getMark();
			// o.list_type = obj.getString("type");
			data.list.add(o);
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(SettingUserLoginActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(SettingUserLoginActivity.this, "请求失败，请稍后再试",
						Toast.LENGTH_SHORT).show();
			} else if ("0".equals(result.get("responseCode"))) {
				Toast.makeText(SettingUserLoginActivity.this,
						"登录失败，请输入正确的用户名和密码", Toast.LENGTH_SHORT).show();

			} else if ("1".equals(result.get("responseCode"))) {
				PerfHelper.setInfo(PerfHelper.P_USERID, o.nid);
				PerfHelper.setInfo(PerfHelper.P_SHARE_NAME, o.title);
				PerfHelper.setInfo(PerfHelper.P_SHARE_EMAIL, o.des);
				PerfHelper.setInfo(PerfHelper.P_USER_LOGIN, true);
				SettingUserLoginActivity.this.finish();// 关闭子窗口ChildActivity
				Utils.showToast("登录成功");
			} else {
				Toast.makeText(SettingUserLoginActivity.this, "登录失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
