package cn.sharesdk.onekeyshare;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;

/**
 * 分享内容自定义回调
 * 
 * @author wll
 */
public interface ShareContentCustomizeCallback {

	/**
	 * @param platform
	 *            平台
	 * @param paramsToShare
	 *            分享内容参数
	 */
	public void onShare(Platform platform, ShareParams paramsToShare);

}
