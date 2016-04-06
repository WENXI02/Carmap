package com.example.wenxi.carmap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.example.wenxi.carmap.Utils.MapUitls;
import com.example.wenxi.carmap.Utils.RoutePlanUtils;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wenxi on 16/4/5.
 */
public class RoutePlanActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private MapView mapView;
    private boolean isOK=false;
    private Animation growAnimation;
    private TextToSpeech textToSpeech;
    private  MapUitls mapUitls;
//    private Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what==0){
//                LatLng latLng=(LatLng) msg.obj;
//                Log.e("TAG obj",latLng.toString());
//            }
//        }
//    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_routeplan);
        mapView=(MapView) findViewById(R.id.RoutePlan_map);
//        mapUitls=new MapUitls();
//        mapUitls.getMyll(this,true,handler);

        Bundle bundle=getIntent().getExtras();
        Log.e("TAG",String.valueOf(bundle.getDouble("latitude")));
        Log.e("TAG",String.valueOf(bundle.getDouble("longitude")));
        Log.e("MapUitls",String.valueOf(MapUitls.getMyLatlng().latitude));
        Log.e("MapUitls",String.valueOf(MapUitls.getMyLatlng().longitude));
        final LatLng start=MapUitls.getMyLatlng();
        final LatLng end=new LatLng(bundle.getDouble("latitude"),bundle.getDouble("longitude"));

        final RoutePlanUtils utils=new RoutePlanUtils(mapView,this);
        DrivingRoutePlanOption.DrivingPolicy DrivingPolicy=
                DrivingRoutePlanOption.DrivingPolicy.valueOf
                        ("ECAR_AVOID_JAM");
        utils.setdrivingSearch(start,end,DrivingPolicy);
        Intent intent=new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent,0);
        FloatingActionButton floatingActionButton=(FloatingActionButton) findViewById(R.id.fab_daohang);
        growAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.startAnimation(growAnimation);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RouteParaOption para = new RouteParaOption();
                    para.startPoint(start).endPoint(end);
                    BaiduMapRoutePlan.setSupportWebRoute(true);
                    BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, RoutePlanActivity.this);
                }catch (Exception e){

                }

            }
        });
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                // 需要做的事:发送消息
                speek("如果要开启导航，请点击屏幕下方的按钮");
                speek("距离目的地"+String.valueOf(utils.getdrivingjuli())+"米");
            }
        };
        timer.schedule(task,3000);
        Timer timer1 = new Timer();
        TimerTask task1 = new TimerTask() {

            @Override
            public void run() {
                // 需要做的事:发送消息
                RouteNode node=new RouteNode();
                node.setLocation(MapUitls.getMyLatlng());
                utils.setdriving(node);
            }
        };
        timer1.schedule(task1,3000,5000);
    }

    private  void speek(String data){
        if (isOK){
            textToSpeech.speak(data,TextToSpeech.QUEUE_ADD,null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0){
            switch (resultCode){
                case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:{
                    textToSpeech=new TextToSpeech(this,this);
                    isOK=true;
                    Log.e("TAG","CHECK_VOICE_DATA_PASS");
                    break;
                }
                case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:{
                    Log.e("TAG","CHECK_VOICE_DATA_BAD_DATA");
                    break;
                }
                case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:{
                    Log.e("TAG","CHECK_VOICE_DATA_MISSING_DATA");
                    break;
                }
                case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:{
                    Log.e("TAG","CHECK_VOICE_DATA_MISSING_VOLUME");
                    Intent intent=new Intent();
                    intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(intent);
                    break;
                }
                case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:{
                    Log.e("TAG","CHECK_VOICE_DATA_FAIL");
                    break;
                }
            }
        }
    }
    @Override
    public void onInit(int status) {
        textToSpeech.setLanguage(Locale.CHINESE);
    }
}
