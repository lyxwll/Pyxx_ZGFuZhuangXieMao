package com.pyxx.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.CommonUtil;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 用户注册界面
 * 
 * @author wll
 */
public class UserEmailRegister extends Activity implements OnClickListener {
	private View btn_return;// btn按钮
	private Button btn_register;
	private EditText edit_nickName, edit_pwd, edit_pwd_affirm, email_ed;// 信息输入框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.user_register_email);

		initView();

	}

	private void initView() {
		btn_return = findViewById(R.id.title_back);
		btn_register = (Button) findViewById(R.id.btn_user_register_accomplish_register);
		email_ed = (EditText) findViewById(R.id.edit_Email);// 邮箱
		edit_nickName = (EditText) findViewById(R.id.edit_name);// 用户名
		findViewById(R.id.center_seller_title_text);
		edit_pwd = (EditText) findViewById(R.id.editt_user_register_pwd);// 密码
		edit_pwd_affirm = (EditText) findViewById(R.id.editt_user_register_pwd_affirm);// 确认密码

		btn_return.setOnClickListener(this);
		btn_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_user_register_accomplish_register:
			// 验证密码，邮箱；
			int j = edit_pwd.getText().toString().length();
			int k = edit_pwd_affirm.getText().toString().length();
			// 判断不能为空；
			if (edit_pwd.getText().toString().trim().length() <= 0
					|| edit_pwd_affirm.getText().toString().trim().length() <= 0
					|| email_ed.getText().toString().trim().length() <= 0
					|| edit_nickName.getText().toString().trim().length() <= 0) {
				Toast.makeText(UserEmailRegister.this, "信息填写不完整",
						Toast.LENGTH_SHORT).show();
			} else if (!CommonUtil.emailFormat(email_ed.getText().toString()
					.trim())) {
				Utils.showToast("邮箱格式错误，请确认后再输入");
			} else if (j < 6 || k < 6) {
				Toast.makeText(UserEmailRegister.this, "注意：密码 的长度不能低于6位",
						Toast.LENGTH_SHORT).show();
			} else if (j > 16 || k > 16) {
				Toast.makeText(UserEmailRegister.this, "注意：密码的长度不能高于16位",
						Toast.LENGTH_SHORT).show();
			}

			// 验证密码是否 注册
			else if (!edit_pwd.getText().toString()
					.equals(edit_pwd_affirm.getText().toString())) {
				Toast.makeText(UserEmailRegister.this, "注意：两次输入的密码必须一致",
						Toast.LENGTH_SHORT).show();
			} else {
				new AlertDialog.Builder(UserEmailRegister.this)
						.setMessage("确认注册？")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										new CurrentAync()
												.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
									};
								})
						.setNegativeButton("否",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
									}
								}).show();
			}
			break;
		case R.id.title_back:
			finish();
			break;
		}
	}

	int sex = 1;

	class CurrentAync extends AsyncTask<Void, Void, HashMap<String, Object>> {
		Listitem o = new Listitem();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Utils.showProcessDialog(UserEmailRegister.this, "正在注册...");
		}

		public CurrentAync() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
			// 如果当前没有完成城市信息就停在这；一直到城市信息完成后，再执行后面的；
			ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();

			BasicNameValuePair mPair02 = new BasicNameValuePair(
					"user.passWord", edit_pwd.getText().toString());
			BasicNameValuePair mPair04 = new BasicNameValuePair("user.phone",
					email_ed.getText().toString().trim());
			// 加入集合
			list.add(mPair02);
			list.add(mPair04);
			BasicNameValuePair mPair05 = new BasicNameValuePair("user.nick",
					edit_nickName.getText().toString().trim());
			list.add(mPair05);

			String json = "";
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(
						getResources().getString(R.string.url_user_register),
						list);

				if (ShareApplication.debug) {
					System.out.println("QQ绑定注册返回:" + json);
				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
				mhashmap.put("results", date.list);
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
			if (!Utils.isNetworkAvailable(UserEmailRegister.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(UserEmailRegister.this, "请求失败，请稍后再试",
						Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				Toast.makeText(UserEmailRegister.this, "注册成功",
						Toast.LENGTH_SHORT).show();
				UserEmailRegister.this.finish();
			} else {
				Toast.makeText(UserEmailRegister.this, "注册失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
