package com.pyxx.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class OptionsContent extends LinearLayout {
	private static final String TAG = "OptionsScrollView";
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	public OptionsListener mListener;// 刷新操作事件
	public int mCurrentState = 3;
	public View header;
	public float poistion_start;
	private Scroller mScroller;
	public ScrollView mScrollView;
	public int headerHeight;
	public LinearLayout mContent;

	public TextView footer_text;// 加载更多文字提示
	public ProgressBar footer_pb;// 加载更多进度条
	public View mList_footer;// 加载更多
	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	public OptionsContent(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	public OptionsContent(Context context) {
		super(context);
		initViews();
	}

	// 初始化header
	public void initViews() {
		this.setOrientation(LinearLayout.VERTICAL);
		header = inflate(this.getContext(), R.layout.xlistview_header, null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		mScroller = new Scroller(this.getContext(), sInterpolator);
		header.setVisibility(View.INVISIBLE);
		header.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						headerHeight = header.getHeight();
						// smoothScrollTo(header.getHeight());

						int duration = 10;
						int oldScrollX = getScrollX();
						mScroller.startScroll(oldScrollX, getScrollY(),
								oldScrollX, headerHeight, duration);
						invalidate();
						header.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						header.setVisibility(View.VISIBLE);
					}
				});
		addView(header, lp);
		mContent = new LinearLayout(this.getContext());
		// mContent.setOrientation(LinearLayout.VERTICAL);
		addView(mContent, lp);
		mScrollView = new OptionsScrollView(this.getContext());
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		mScrollView.setLayoutParams(rlp);
		mScrollView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		mScrollView.addView(this);
		arrowImageView = (ImageView) header
				.findViewById(R.id.xlistview_header_arrow);
		progressBar = (ProgressBar) header
				.findViewById(R.id.xlistview_header_progressbar);
		tipsTextview = (TextView) header
				.findViewById(R.id.xlistview_header_hint_textview);
		lastUpdatedTextView = (TextView) header
				.findViewById(R.id.xlistview_header_time);
		mList_footer = LayoutInflater.from(this.getContext()).inflate(
				R.layout.footer, null);
		footer_text = (TextView) mList_footer.findViewById(R.id.footer_text);
		footer_pb = (ProgressBar) mList_footer.findViewById(R.id.footer_pb);
		addView(mList_footer, lp);
		animation = new RotateAnimation(0,
				-180,// 图片转动动画初始
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
		mList_footer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeFooterState(0);
				new loadmore().execute();
			}
		});
		changeFooterState(1);
	}

	public class loadmore extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			if (mListener != null) {
				mListener.loadmore();
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			changeFooterState(1);
			super.onPostExecute(result);
		}
	}

	public LinearLayout getContentView() {
		return mContent;
	}

	public ScrollView getScrollView() {
		return mScrollView;
	}

	public void addFooter() {
		mList_footer.setVisibility(View.VISIBLE);
	}

	public void removeFooter() {
		mList_footer.setVisibility(View.GONE);
	}

	public void changeFooterState(int state) {
		switch (state) {
		case 0:// 正在加载
			footer_text.setText("正在加载...");
			footer_pb.setVisibility(View.VISIBLE);
			break;
		case 1:// 加载完成
			footer_text.setText("点击正在加载更多");
			footer_pb.setVisibility(View.GONE);
			break;
		}
	}

	void smoothScrollTo(int dy) {
		int duration = 600;
		int oldScrollX = getScrollX();
		mScroller.startScroll(oldScrollX, getScrollY(), oldScrollX, dy,
				duration);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return super.onTouchEvent(ev);
	}

	void enableChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);
		}
	}

	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				// Keep on drawing until the animation has finished.
				invalidate();
			} else {
				clearChildrenCache();
			}
		} else {
			clearChildrenCache();
		}
	}

	public boolean isRecored;
	public boolean canreturn;
	private float startY;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t - headerHeight, r, b - headerHeight);
	}

	public class OptionsScrollView extends ScrollView {

		public OptionsScrollView(Context context) {
			super(context);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			final float y = ev.getY();
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				poistion_start = ev.getY();
				isRecored = false;
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaY = poistion_start - y;
				if (13 > Math.abs(deltaY)) {
					break;
				}
				if (mCurrentState == REFRESHING) {
					break;
				}
				if (!isRecored && getScrollY() == 0) {
					isRecored = true;
					startY = y;
				}

				float oldScrollY = OptionsContent.this.getScrollY();
				if (oldScrollY < -headerHeight) {
					deltaY = deltaY * Math.abs(headerHeight / oldScrollY);
				}
				if (mScrollView.getScrollY() < 5) {
					if (oldScrollY == headerHeight && deltaY > 0) {
						canreturn = false;
						break;
					} else if (y - startY < (headerHeight * 3 / 2)) {
						if (mCurrentState == RELEASE_To_REFRESH) {
							mCurrentState = PULL_To_REFRESH;
							changeHeaderViewByState();
							canreturn = false;
						}
						// break;
					} else if (oldScrollY < -(headerHeight * 3 / 2)
							&& mCurrentState != RELEASE_To_REFRESH) {
						mCurrentState = RELEASE_To_REFRESH;
						changeHeaderViewByState();
						// return true;
						canreturn = true;
					}
					poistion_start = y;
					OptionsContent.this.scrollTo(getScrollX(), (int) oldScrollY
							+ (int) deltaY);
					return true;
				}

				break;
			case MotionEvent.ACTION_UP:
				// mCurrentState = RELEASE_To_REFRESH;
				oldScrollY = OptionsContent.this.getScrollY();
				if (canreturn && mCurrentState == RELEASE_To_REFRESH) {
					OptionsContent.this
							.smoothScrollTo((int) -OptionsContent.this
									.getScrollY());
					mCurrentState = REFRESHING;
					changeHeaderViewByState();
					new loadData().execute();
				} else if (mCurrentState == DONE
						|| mCurrentState == PULL_To_REFRESH) {
					mCurrentState = DONE;
					OptionsContent.this
							.smoothScrollTo((int) -OptionsContent.this
									.getScrollY() + headerHeight);
					changeHeaderViewByState();
				}
				break;
			}
			return super.dispatchTouchEvent(ev);
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {

			return super.onTouchEvent(ev);
		}
	}

	public class loadData extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			if (mListener != null) {
				mListener.reflashing();
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			mCurrentState = DONE;
			changeHeaderViewByState();
			OptionsContent.this.smoothScrollTo((int) -OptionsContent.this
					.getScrollY() + headerHeight);
			lastUpdatedTextView.setText(sdf.format(new Date()));
			PerfHelper.setInfo(reflashtime, sdf.format(new Date()));
			super.onPostExecute(result);
		}
	}

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	public String reflashtime = "reflash_time";

	public interface OptionsListener {
		public void reflashing();

		public void loadmore();
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (mCurrentState) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);
			tipsTextview.setText("松开刷新");
			Log.i(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (true) {
				// isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			Log.i(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			Log.i(TAG, "当前状态,正在刷新...");
			break;
		case DONE:

			progressBar.setVisibility(View.GONE);
			arrowImageView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			Log.i(TAG, "当前状态，done");
			break;
		}
	}
}
