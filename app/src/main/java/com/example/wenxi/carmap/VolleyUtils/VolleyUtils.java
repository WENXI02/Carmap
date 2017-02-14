package com.example.wenxi.carmap.VolleyUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenxi on 2016/12/20.
 */

public class VolleyUtils {
    private  Context context;
    private   Handler handler;
    public VolleyUtils (Context context,Handler handler){
        this.context=context;
        this.handler=handler;
    }
    public void getInfo(String path, final int index){
        
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(path, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("TAG","解析成功"+response.toString());
                //Toast.makeText(context,"解析成功"+response.toString(),Toast.LENGTH_SHORT).show();
                Message message=new Message();
                message.what=index;
                message.obj=response.toString();
                handler.sendMessage(message);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG",error.toString());
                //Toast.makeText(context,"解析失败"+error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Charset", "utf-8");
                headers.put("Content-Type", "text/html");
//                headers.put("Accept-Encoding", "gzip,deflate");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

}
