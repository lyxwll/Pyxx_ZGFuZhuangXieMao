package com.pyxx.entity;

import com.pyxx.dao.DBHelper;

import android.database.Cursor;


public class DBCollectUtil {
	public static DBHelper dBHelper = DBHelper.getDBHelper();
	
	public static void insert(Listitem li){
	       String insertDataSql = "INSERT INTO listitemcollect(icon,nid,des,title,address," +
	       		"author,fuwu,img_list_1,img_list_2,isad,ishead,level,list_type,longitude,n_mark,other,other1,other2,other3," +
	       		"phone,preferential,sa,shangjia,share_image,show_type,sugfrom,u_date,vip_id,latitude) VALUES('"+li.icon+"','"
	       		+li.nid+"','"+li.des+"','"+li.title+"','"+li.address+"','"+li.author+"','"+li.fuwu+"','"+li.img_list_1+"','"+li.img_list_2
	       		+"','"+li.isad+"','"+li.ishead+"','"+li.level+"','"+li.list_type+"','"+li.longitude+"','"+li.n_mark+"','"+li.other+"','"
	       		+li.other1+"','"+li.other2+"','"+li.other3+"','"+li.phone+"','"+li.preferential+"','"+li.sa+"','"+li.shangjia+"','"+li.share_image
	       		+"','"+li.show_type+"','"+li.sugfrom+"','"+li.u_date+"','"+li.vip_id+"','"+li.latitude+"')";
	       DBHelper.getDBHelper().getReadableDatabase().execSQL(insertDataSql);		
	}

	public static Data selectlists(String table,String str,String condStr){
		Data data = new Data();
		String sql = "SELECT * FROM "+table+" where ";
		 String str1[] = str.split(",");
		 String str2[] = condStr.split(",");
		 for(int i =0;i<str1.length;i++){
			 if(i!=0){
				 sql+=" and ";
			 }
			 sql+=str1[i]+"='"+str2[i]+"' ";
		 }
	     Cursor cursor = DBHelper.getDBHelper().getReadableDatabase().rawQuery(sql, null); 
	     if(cursor==null){
	    	 
	     }else{
			 while(cursor.moveToNext()){
				 Listitem item = new Listitem();
				 item.c_id = cursor.getInt(0);
				 item.icon = cursor.getString(1);
				 item.nid = cursor.getString(2);
				 item.des = cursor.getString(3);
				 item.title = cursor.getString(4);
				 item.address = cursor.getString(5);
				 item.author = cursor.getString(6);
				 item.fuwu = cursor.getString(7);
				 item.img_list_1 = cursor.getString(8);
				 item.img_list_2 = cursor.getString(9);
				 item.isad = cursor.getString(10);
				 item.ishead = cursor.getString(11);
				 item.level = cursor.getString(12);
				 item.list_type = cursor.getString(13);
				 item.longitude = cursor.getString(14);
				 item.n_mark = cursor.getString(15);
				 item.other = cursor.getString(16);
				 item.other1 = cursor.getString(17);
				 item.other2 = cursor.getString(18);
				 item.other3 = cursor.getString(19);
				 item.phone = cursor.getString(20);
				 item.preferential = cursor.getString(21);
				 item.sa = cursor.getString(22);
				 item.shangjia = cursor.getString(23);
				 item.share_image = cursor.getString(24);
				 item.show_type = cursor.getString(25);
				 item.sugfrom = cursor.getInt(26);
				 item.u_date = cursor.getString(27);
				 item.vip_id = cursor.getString(28);
				 item.latitude = cursor.getString(29);
				 item.getMark();
				 data.list.add(item);
			 }	    	 
	     }
	     cursor.close();
		return data;
	}
	
	public static boolean exit(String table,String info,String str,String condStr){//info一般为*号表示全部
		String result = "";
		String sql = "";
		String str1[] = str.split(",");
		String str2[] = condStr.split(",");
		
		for(int i=0;i<str1.length;i++){
			if(i!=0){
				sql+=" and ";
			}
			sql += str1[i]+"=?";
		}
		result = DBHelper.getDBHelper().select(table, info,sql, str2);
		
		if("".equals(result)){
			return false;
		}else{
			return true;
		}
	}
	
	public static void cancle(String table,String str,String condStr){
		String sql = "";
		String str1[] = str.split(",");
		String str2[] = condStr.split(",");
		for(int i=0;i<str1.length;i++){
			if(i!=0){
				sql+=" and ";
			}
			sql += str1[i]+"=? ";
		}
		DBHelper.getDBHelper().delete(table,sql, str2);
	
	}
	public static void delete(String table,String str,int condStr){
		DBHelper.getDBHelper().delete(table, str+"="+condStr, null);
		
	}
}
