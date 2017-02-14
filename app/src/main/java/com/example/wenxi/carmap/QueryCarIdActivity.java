package com.example.wenxi.carmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wenxi.carmap.VolleyUtils.QueryUserinfo;
import com.example.wenxi.carmap.VolleyUtils.VolleyPathUtils;
import com.example.wenxi.carmap.VolleyUtils.VolleyUtils;

import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by wenxi on 2016/12/21.
 */

public class QueryCarIdActivity extends AppCompatActivity{
    private EditText car_id;
    private Button button;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    try {
                        JSONObject object = new JSONObject((String) msg.obj);
                        if (TextUtils.equals(object.getString("Prompt"), "User information are:")) {
                            QueryUserinfo userinfo = new QueryUserinfo();
                            userinfo.setUsername(object.getString("Username"));
                            userinfo.setPassword(object.getString("Password"));
                            userinfo.setTelephone(object.getString("Telephone"));
                            userinfo.setLicense_number(object.getString("License number"));
                            Uri telUri = Uri.parse("tel:"+object.getString("Telephone"));
                            Intent intent=new Intent(Intent.ACTION_CALL,telUri);
                            startActivity(intent);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };
    private VolleyPathUtils pathUtils=new VolleyPathUtils();
    private VolleyUtils volleyUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_querycarid);
        car_id=(EditText) findViewById(R.id.query_car_id);
        button=(Button) findViewById(R.id.query);
        volleyUtils=new VolleyUtils(this,handler);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String id= car_id.getText().toString();
                    String path=pathUtils.getGetowner_licensepath(id);
                    volleyUtils.getInfo(path,0);
                }catch (Exception e){

                }

            }
        });
    }
}
