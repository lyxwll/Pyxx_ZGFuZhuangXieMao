package com.pyxx.entity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CommonUtil {

	/**
	 * MD5加密算法
	 * 
	 * @param plainText
	 *            要加密的字符串
	 * @return 加密后的字符串
	 */
	public static String Md5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return plainText;
	}

	/**
	 * 判断是否为汉字，如果不是汉字则进行转码
	 * 
	 * @param str
	 * @return
	 */
	public static String changeIos8859ToUtf8(String str) {
		try {
			boolean flag = false;
			byte[] temp = null;
			try {
				temp = str.getBytes("ISO-8859-1");
			} catch (Exception e) {
				flag = false;
			}
			int i = 0;
			for (i = 0; i < temp.length; i++) {
				if (temp[i] < 0) {
					flag = true;
					i = temp.length;
				}
			}

			if (flag) {
				return new String(str.getBytes("ISO-8859-1"), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 验证输入的邮箱格式是否符合
	 * 
	 * @param email
	 * @return 是否合法
	 */
	public static boolean emailFormat(String email) {
		boolean tag = true;
		final String pattern1 = "\\w+[\\w]*@[\\w]+\\.[\\w]+$";
		// final String pattern1 =
		// "^([a-z0-9A-Z]+[-|//.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?//.)+[a-zA-Z]{2,}$";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	// 验证手机号码是否符合格式
	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		try {
			// 13********* ,15********,18*********
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	// 获取网络图片，如果缓存里面有就从缓存里面获取
	public static String getImagePath(Context context, String url) {
		if (url == null)
			return "";
		String imagePath = "";
		String fileName = "";

		// 获取url中图片的文件名与后缀
		if (url != null && url.length() != 0) {
			fileName = url.substring(url.lastIndexOf("/") + 1);
		}

		// 图片在手机本地的存放路径,注意：fileName为空的情况
		imagePath = context.getCacheDir() + "/" + fileName;
		// Log.i(TAG,"imagePath = " + imagePath);
		File file = new File(context.getCacheDir(), fileName);// 保存文件,
		if (!file.exists()) {
			// Log.i(TAG, "file 不存在 ");
			try {
				byte[] data = readInputStream(getRequest(url));
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);

				bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(
						file));

				imagePath = file.getAbsolutePath();
				// Log.i(TAG,"imagePath : file.getAbsolutePath() = " +
				// imagePath);

			} catch (Exception e) {
				// Log.e(TAG, e.toString());
			}
		}
		return imagePath;
	} // getImagePath( )结束。

	public static InputStream getRequest(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000); // 5秒
		if (conn.getResponseCode() == 200) {
			return conn.getInputStream();
		}
		return null;

	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	// 把网络上url的图片转化为bitmap
	public static Bitmap returnBitMap(String url) {
		Log.i("returnBitMap", "url=" + url);
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap getBitmap(String path) throws IOException {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			return bitmap;
		}
		return null;

	}

	/**
	 * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
	 * 
	 * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
	 * 
	 * B.本地路径:url="file://mnt/sdcard/photo/image.png";
	 * 
	 * C.支持的图片格式 ,png, jpg,bmp,gif等等
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap GetLocalOrNetBitmap(String url) {
		Bitmap bitmap = null;
		@SuppressWarnings("unused")
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new URL(url).openStream(),
					20 * 1024 * 1024);
			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, 20 * 1024 * 1024);
			out.flush();
			byte[] data = dataStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			data = null;
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// public static void main(String[] args) {
	// // File f = new File("http://localhost:8080/Greening/uploadImg/zw1.jpg");
	// // try {
	// // f.createNewFile();
	// // } catch (IOException e) {
	// // System.out.println("error");
	// // e.printStackTrace();
	// // }
	// // System.out.println(f.exists());
	// String str1 = "%E8%81%8A%E8%81%8A%E5%A4%A9%";
	// String str2 = changeIos8859ToUtf8(str1);
	// System.out.print(str2);
	//
	// }

	// 随机产生五位数验证码
	public static String random5() {
		Random rnd = new Random();

		int num = rnd.nextInt(89999) + 10000;
		return num + "";

	}

	@SuppressLint("SimpleDateFormat")
	public static String nowTimeString() {
		String str = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmsss");
		str = sdf.format(new Date());
		return str;
	}
}
