package com.pyxx.part_asynctask;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 
 * @author Administrator
 * 
 */
public class UploadFileTask extends AsyncTask<String, Void, String> {
	// 改地址；
	public static final String requestURL = ShareApplication.share
			.getResources().getString(
					R.string.citylife_FileImageUploadServlet_url);
	/**
	 * 可变长的输入参数，与AsyncTask.exucute()对应
	 */
	private ProgressDialog pdialog;
	private Activity context = null;
	Handler hander;

	public UploadFileTask(Activity ctx, Handler hander) {
		this.context = ctx;
		this.hander = hander;
		pdialog = ProgressDialog.show(context, "正在加载...", "系统正在处理您的请求");
	}

	@Override
	protected void onPostExecute(String result) {
		// 返回HTML页面的内容
		pdialog.dismiss();
		if (UploadUtils.FAILURE.equalsIgnoreCase(result)) {

			Toast.makeText(context, "未知错误，上传失败!", Toast.LENGTH_LONG).show();
			// System.out.println("上传失败:" + result);
		} else {
			Toast.makeText(context, "恭喜，上传成功!", Toast.LENGTH_LONG).show();
			// System.out.println("user_img:" + result);
			// 修改用户图片
			PerfHelper.setInfo(PerfHelper.P_SHARE_USER_IMAGE, result);
			hander.obtainMessage(0).sendToTarget();
		}
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected String doInBackground(String... params) {

		// 接受传递过来的参数，这里默认第一个参数为sd卡图片地址；
		File file = new File(params[0]);
		String username = params[1];
		// 调用上传头像的工具类，执行图片上传操作；
		// System.out.println("sc图片地址：" + requestURL + username);

		return UploadUtils
				.uploadFile(file, requestURL + "username=" + username);

	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}

}