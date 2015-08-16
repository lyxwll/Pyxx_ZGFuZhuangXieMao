package com.pyxx.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.baseui.BaseActivity;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 关于版本信息
 * 
 * @author wll
 */
public class Version extends BaseActivity {
	View actionbar_title;
	TextView t_uid;
	TextView t_aid;
	TextView t_mail;
	TextView t_version;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_st_version);

		t_version = (TextView) findViewById(R.id.version_code_number);
		t_version.setText("版本号: V " + ShareApplication.getVersion());

	}

	public void showTime(View v) {
		String time = PerfHelper.getStringData(PerfHelper.P_ALARM_TIME);
		if (time != null && !"".equals(time)) {
			Utils.showToast("提醒时间：" + time.split(" ")[1].substring(0, 5) + "\n"
					+ "UID: " + PerfHelper.getStringData(PerfHelper.P_USERID)
					+ "\n" + "A M: 1.0.0");
			Intent intent = new Intent();
			intent.setClass(this, FeedBack.class);
			startActivity(intent);
		}
	}

	public void things(View v) {
		if (v.getId() == R.id.title_back) {
			Version.this.finish();
		}
	}

}
