package com.example.wenxi.carmap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wenxi.carmap.VolleyUtils.QueryUserinfo;
import com.example.wenxi.carmap.VolleyUtils.VolleyPathUtils;
import com.example.wenxi.carmap.VolleyUtils.VolleyUtils;

import org.json.JSONObject;

/**
 * Created by wenxi on 16/4/16.
 */
public class Loging_activity extends AppCompatActivity {
    private EditText first_name,last_initial,user_phone,car_id;
    private Button re_login,bt_register;
    private boolean issave=false;
    private VolleyUtils volleyUtils;
    private VolleyPathUtils pathUtils=new VolleyPathUtils();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:{
                    try {
                        JSONObject object=new JSONObject((String)msg.obj);
                        String Prompt=object.getString("Prompt");
                        if (TextUtils.equals(Prompt,"Register success")){
                            Toast.makeText(Loging_activity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(Loging_activity.this,"注册失败",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
                case 1:{
                    try {
                        JSONObject object=new JSONObject((String)msg.obj);
                        Toast.makeText(Loging_activity.this,object.getString("Prompt"),Toast.LENGTH_SHORT).show();
                        if (!TextUtils.equals(object.getString("Prompt"),"Login failure")) {
                            String path = pathUtils.setGetowner_idpath(first_name.getText().toString());
                            volleyUtils.getInfo(path,2);
                        }
                    }catch (Exception e){

                    }
                    break;
                }
                case 2:{
                    try {
                        JSONObject object=new JSONObject((String)msg.obj);
                        if (TextUtils.equals(object.getString("Prompt"),"User information are:")) {
                            QueryUserinfo userinfo = new QueryUserinfo();
                            userinfo.setUsername(object.getString("Username"));
                            userinfo.setPassword(object.getString("Password"));
                            userinfo.setTelephone(object.getString("Telephone"));
                            userinfo.setLicense_number(object.getString("License number"));
                            SharedPreferences sharedPreferences=getSharedPreferences("USER",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putBoolean("isloging",true);
                            editor.putString("USERID",userinfo.getUsername());
                            editor.putString("PASSWORD",userinfo.getPassword());
                            editor.putString("PHONE",userinfo.getTelephone());
                            editor.putString("CAR_ID",userinfo.getLicense_number());
                            editor.apply();
                            finish();
                        }else {

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_ativity);
        volleyUtils=new VolleyUtils(this,handler);
        first_name =(EditText)findViewById(R.id.first_name);
        last_initial =(EditText)findViewById(R.id.last_initial);
        user_phone =(EditText)findViewById(R.id.user_phone);
        car_id =(EditText)findViewById(R.id.car_id);
        re_login=(Button) findViewById(R.id.re_login);
        bt_register=(Button) findViewById(R.id.bt_register);
        re_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(first_name.getText().toString())&&
                        !TextUtils.isEmpty(last_initial.getText().toString())&&
                        !TextUtils.isEmpty(user_phone.getText().toString())){
                    issave=true;
                }
                if (issave){
                    String path=pathUtils.getLogin_path(first_name.getText().toString(),last_initial.getText().toString());
                    volleyUtils.getInfo(path,1);

                }
            }
        });
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(first_name.getText().toString())&&
                        !TextUtils.isEmpty(last_initial.getText().toString())&&
                        !TextUtils.isEmpty(user_phone.getText().toString())&&
                        !TextUtils.isEmpty(car_id.getText().toString())){

                    String path=pathUtils.getRegister_path(first_name.getText().toString(),last_initial.getText().toString(),user_phone.getText().toString(),car_id.getText().toString());
                    volleyUtils.getInfo(path,0);
                }
            }
        });

    }
}
