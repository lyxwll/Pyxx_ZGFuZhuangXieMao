package com.pyxx.view;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.entity.Listitem;
import com.pyxx.zhongguofuzhuangxiemao.R;

 public class GalleryUtils {  
    private Context mContext;  
    private List<Listitem> list;
    private Gallery gallery;
	private RadioGroup group;
	private int nowCount=0;
	private Thread thread=null;
	private TextView title;
	public static int COMPLETED=1;
	private String[] mps;
	private Class clazz;
	private boolean flag=true;
	private LinearLayout parentView;
	private boolean isTitle = true;
	
	private LayoutInflater  inflater = null;
	private RelativeLayout main_rl = null;
	/*
	 * context  上下文
	 * list 需要放入的list集合
	 * flag 暂时由外面控制只被实例化一次
	 * clazz 点击图片需要跳转的页面
	 * parentView 把该gallery效果放入 parentView中
	 * isTitile 是否需要显示标题效果  true是需要  FALSE 是不需要
	 * author  lx
	 * */
    public GalleryUtils(Context context,List<Listitem> list,boolean flag,Class clazz,LinearLayout parentView,boolean isTitle) {  
        this.mContext = context;  
        this.list = list;
        this.flag = flag;
        this.clazz = clazz;
        this.parentView = parentView;
        this.isTitle = isTitle;
        initlayout();
          
    }  
    public void initlayout(){
    	if(this.inflater == null){
        this.inflater =  LayoutInflater.from(mContext);
        main_rl  = (RelativeLayout)inflater.inflate(R.layout.gallery_util, null);
        title = (TextView) main_rl.findViewById(R.id.title);
        gallery = (Gallery)main_rl.findViewById(R.id.gallery); 		
        if (isTitle) {
        	title.setVisibility(View.VISIBLE);
        	((RadioGroup) main_rl.findViewById(R.id.group)).setVisibility(View.VISIBLE);
        	((RadioGroup) main_rl.findViewById(R.id.group2)).setVisibility(View.GONE);
             group = (RadioGroup) main_rl.findViewById(R.id.group);
		}else{
			title.setVisibility(View.GONE);
        	((RadioGroup) main_rl.findViewById(R.id.group2)).setVisibility(View.VISIBLE);
        	((RadioGroup) main_rl.findViewById(R.id.group)).setVisibility(View.GONE);
            group = (RadioGroup) main_rl.findViewById(R.id.group2);
		}
        parentView.addView(main_rl);
    	}else{
    		parentView.removeAllViews();
    		parentView.addView(main_rl);
    	}
    }
    
	public void initShowImg(){
	if(list!=null&&flag){
		if(list.size()>0){
		    flag = false;		
			group.removeAllViews();
			for (int c = 0; c < list.size(); c++) {
				ImageView rb = new ImageView(mContext);
				rb.setPadding(10, 0, 0, 0);
				rb.setImageResource(R.drawable.yuandian_tb2);
				group.addView(rb);
			}
			mps = new String[list.size()];
			for(int m = 0;m<list.size();m++){
				Listitem li = (Listitem)list.get(m);
				mps[m] = li.icon;
			}
	
			gallery.setAdapter(new ImageAdapter(mContext, mps));
			gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View v,
						int position, long id) {
					nowCount = position;
					for (int d = 0; d < list.size(); d++) {
						if (d == position) {
							ImageView rbs = (ImageView) group.getChildAt(position);
							rbs.setImageResource(R.drawable.yuandian_tb1);
						} else {
							ImageView rbs2 = (ImageView) group.getChildAt(d);
							rbs2.setImageResource(R.drawable.yuandian_tb2);
						}
					}
					if (isTitle) {
						Listitem im = (Listitem) list.get(position);
						title.setText(im.title.toString());
					}
				}
	
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// 这里不做响应
				}
			});
			gallery.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					if(clazz!=null){
					 Intent intent = new Intent();
					 intent.setClass(mContext,clazz);
					 intent.putExtra("item", (Listitem)list.get(arg2));
					 mContext.startActivity(intent);
					}
				}
	
			});
			if (thread == null) {
				thread = new WorkThread();
				thread.start();
			}
		}
	}
		
		
		
	}	

		 private Handler handler = new Handler() {  
		     @Override  
		     public void handleMessage(Message msg) {  
		         if (msg.what == COMPLETED) {  
						if(list!=null){
							if(list.size()>0){
							if(nowCount>=list.size()-1){
								nowCount=0;
							}else{
								nowCount=nowCount+1;
							}
		         	gallery.setSelection(nowCount); 
							}
		         }  
		     }
		     }
		 }; 	
	   
		 //工作线程  
		 private class WorkThread extends Thread {  
		     @Override  
		     public void run() {  
		         //......处理比较耗时的操作  
		           
		         //处理完成后给handler发送消息
		     	
					while(true){
		      	 try {
						    Thread.sleep(10000);
				            Message msg = new Message();  
				            msg.what = COMPLETED;  
				            handler.sendMessage(msg);             	 
							
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				
				}        	
		     }  
		 } 
		 
		private class ImageAdapter extends BaseAdapter {  
			    private Context mContext;  
			    private String[] mps;
			    public ImageAdapter(Context context,String[] mps) {  
			        this.mContext = context;  
			        this.mps = mps;
			    }  
			  
			    public int getCount() {   
			        return mps.length;  
			    }  
			  
			    public Object getItem(int position) {  
			        return position;  
			    }  
			  
			    public long getItemId(int position) {  
			        return position;  
			    }  
			  
			    public View getView(int position, View convertView, ViewGroup parent) { 
			        ImageView image = new ImageView(mContext);  
					if (mps[position] != null && mps[position].length() > 10) {
						ShareApplication.mImageWorker.loadImage(mps[position], image);
						image.setVisibility(View.VISIBLE);
					} else {
						image.setImageResource(R.drawable.list_qst);
						image.setVisibility(View.VISIBLE);
					}
//			        image.setAdjustViewBounds(true);  
					image.setScaleType(ImageView.ScaleType.FIT_XY);
			        image.setLayoutParams(new Gallery.LayoutParams(  
			            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));  
			        return image;  
			    }  
			}		 
  
} 	

