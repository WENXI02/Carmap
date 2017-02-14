package com.example.wenxi.carmap.VolleyUtils;

import android.util.Log;

import java.net.URLEncoder;

/**
 * Created by wenxi on 2016/12/21.
 */

public class VolleyPathUtils {
   private String getinfo_path="http://120.77.38.19:8080/server/servlet/s?oper=getinfo";
   private String save_path="http://120.77.38.19:8080/server/servlet/s?oper=save&";
   private String register_path="http://120.77.38.19:8080/server/servlet/s?oper=register&";
   private String getowner_licensepath="http://120.77.38.19:8080/server/servlet/s?oper=getowner_license&";
   private String delete_path="http://120.77.38.19:8080/server/servlet/s?oper=delete_id&";
   private String login_path="http://120.77.38.19:8080/server/servlet/s?oper=login&";
   private String getowner_idpath="http://120.77.38.19:8080/server/servlet/s?oper=getowner_id&id=";
   public String getRegister_path(String id,String password,String tell,String license) {
      try {
         Log.e("PATH",register_path+"id="+URLEncoder.encode(id,"utf-8")+"&password="+password+"&tell="+tell+"&license="+URLEncoder.encode(license,"utf-8"));
         return register_path+"id="+URLEncoder.encode(id,"utf-8")+"&password="+password+"&tell="+tell+"&license="+URLEncoder.encode(license,"utf-8");
      }catch (Exception e){
         e.printStackTrace();
      }
      return null;
   }

   public String setGetowner_idpath(String id) {
      Log.e("PATH",getowner_idpath+id);
      try {
         return getowner_idpath + URLEncoder.encode(id, "utf-8");
      }catch (Exception e){
         return null;
      }
   }

   public String getGetinfo_path() {
      Log.e("PATH",getinfo_path);
      return getinfo_path;
   }

   public String getSave_path(String name,String NF) {
      Log.e("PATH",save_path+name+"="+NF);
      return save_path+name+"="+NF;
   }

   public String getGetowner_licensepath(String license) {
      try {
         Log.e("PATH",getowner_licensepath+"license="+URLEncoder.encode(license,"utf-8"));
         return getowner_licensepath+"license="+URLEncoder.encode(license,"utf-8");
      }catch (Exception e){

      }
      return null;
   }

   public String getDelete_path(String id) {
      try {
         Log.e("PATH", delete_path + "&id=" + URLEncoder.encode(id, "utf-8"));
         return delete_path + "&id=" + URLEncoder.encode(id, "utf-8");
      }catch (Exception e){
         return null;
      }
   }

   public String getLogin_path(String id,String password) {
      try {
         Log.e("PATH", login_path + "id=" + URLEncoder.encode(id, "utf-8") + "&password=" + password);
         return login_path + "id=" + URLEncoder.encode(id, "utf-8") + "&password=" + password;
      }catch (Exception e){
         return null;
      }
   }
}
