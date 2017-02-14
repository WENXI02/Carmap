package com.example.wenxi.carmap;

import android.Manifest;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.wear.AndroidWearDatabase;
import com.android.wear.AndroidWearManager;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.MapView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.example.bluetooth.library.BluetoothState;
import com.example.bluetooth.library.Bluetoothinit;
import com.example.wenxi.carmap.Fingerprint.BaseActivity;
import com.example.wenxi.carmap.Fingerprint.DialogFragment_lollipop;
import com.example.wenxi.carmap.Fragment.Fragment2;
import com.example.wenxi.carmap.Fragment.Fragment3;
import com.example.wenxi.carmap.Utils.MapUitls;
import com.example.wenxi.carmap.Utils.PermissionUtil;
import com.example.wenxi.carmap.Utils.RoutePlanUtils;
import com.example.wenxi.carmap.Utils.leidaUtils;
import com.example.wenxi.carmap.VolleyUtils.Carinfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,Toolbar.OnMenuItemClickListener {

    private Fragment2 fragment2;
    private Fragment3 fragment3;//Fragment对象
    private FragmentManager fragmentManager = getFragmentManager();
    private AHBottomNavigation bottomNavigation;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    LocationClient mLocClient;
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};
    private int REQUEST_CONTACTS=0;
    //定位相关参数
    private MapView mMapView;
    private MapUitls mapUitls=new MapUitls();//地图工具类
    boolean isopenTraffic=false;//交通图标志
    private DialogFragment_lollipop dialogFragment_lollipop;
    private String receive_code="";
    private Stage mStage = Stage.ONE;//voice的枚举
    private String city;//定位所在地城市
    private List<String> allSuggest;//语音返回的集合
    private leidaUtils mleidaUtils ;
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if(msg.what== BluetoothState.MESSAGE_UI){
                String data=(String)msg.obj;

                if (TextUtils.equals(receive_code,"OR")){
                    Toast.makeText(MainActivity.this, "当前OR返回"+data, Toast.LENGTH_SHORT).show();
                }else if(TextUtils.equals(receive_code,"OT")){
                    try {
                        JSONObject jsonObject=new JSONObject(data);
                        JSONArray array=jsonObject.getJSONArray("environment");
                        int a=array.getJSONObject(0).getInt("temparature");
                        int b=array.getJSONObject(0).getInt("humidity");
                        int c=array.getJSONObject(0).getInt("oil mass");
                        Toast.makeText(MainActivity.this,"当前湿度"+ String.valueOf(a)+"当前温度"+ String.valueOf(b)
                        +"当前油量"+ String.valueOf(c),Toast.LENGTH_LONG).show();
                       if (c<=24){
                            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("当前汽车油量不足")
                                   .setMessage("是否前往附近的加油站？")
                                    .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent();
                                    intent.putExtra("TAG","加油站");
                                    intent.setClass(MainActivity.this, Periphery_Activity.class);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                       }
                    }catch (Exception e){

                    }
                }else {
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                }

            }else if (msg.what==3){
                Log.e("TAG",msg.obj.toString());
                final RoutePlanUtils utils=new RoutePlanUtils(mMapView,MainActivity.this);
                DrivingRoutePlanOption.DrivingPolicy DrivingPolicy=
                        DrivingRoutePlanOption.DrivingPolicy.valueOf
                                ("ECAR_AVOID_JAM");
                utils.setdrivingSearch(MapUitls.getMyLatlng(),(LatLng)msg.obj,DrivingPolicy);
            }else if(msg.what==4){
                //获取当前城市
                city=(String) msg.obj;

            }else if (msg.what==5){
                final String key=(String) msg.obj;
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                          builder.setTitle("确认您的目的地")
                                  .setMessage("目的地为:"+allSuggest.get(0))
                                  .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          mapUitls.VioceMap(city,key,handler);
                                          dialog.dismiss();
                                      }
                                  }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  dialog.dismiss();
                              }
                          }).create().show();
            }
        }
    };

    private Handler Leidahandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==leidaUtils.LEIDA){
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,RadarAcitivity.class);
                startActivityForResult(intent,1);
                Log.e("radar","radar");
            }else if (msg.what==leidaUtils.LEIDAError){
                Toast.makeText(MainActivity.this,"您周边没有其他车主",Toast.LENGTH_SHORT).show();
            }
        }
    };


    public static Bluetoothinit bluetoothinit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView=(MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mapUitls.setHandler(handler);
        if (Build.VERSION.SDK_INT>=23){
            showContacts(mMapView);
            ((CarmapApplication) getApplication()).inject(this);
            //对指纹和屏幕锁开启的状态进行判断
            fingerprintLockStage();
            //创建公钥
            createKeyPair();
        }else{
            mapUitls.initMap(mMapView,this,true);

        }

        mleidaUtils = new leidaUtils(MainActivity.this, Leidahandler);//初始化周边雷达
        bluetoothinit=new Bluetoothinit(this,handler);
        bluetoothinit.onstatrt();
        bluetoothinit.initBluetooth();
        bluetoothinit.setisAndroid(false);
        this.setBluetoothinit(bluetoothinit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);//R.drawable.ic_maps_local_restaurant
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("位置信息", R.drawable.ic_maps_place, R.color.weizi);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("周边服务", R.drawable.ic_maps_local_bar, R.color.zoubian);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("我的车辆", R.drawable.ic_directions_car_white_24dp, R.color.mycar);
        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        bottomNavigation.addItems(bottomNavigationItems);
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
//        bottomNavigation.setColored(true);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                 fragment2=new Fragment2();
                 fragment3=new Fragment3();
                if (!wasSelected){
                    switch (position){
                        case 0:{
                            findViewById(R.id.fragment_container).setVisibility(View.GONE);
                            mapUitls.setMapViewVisibility(View.VISIBLE);
                            mapUitls.setmLocClient();//重新定位
                            mapUitls.clearOverlay();
                            mStage=Stage.ONE;
                            break;
                        }
                        case 1:{
                            mapUitls.setMapViewVisibility(View.GONE);
                            mStage=Stage.TOWEE;
                            fragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                    .replace(R.id.fragment_container, fragment2)
                                    .commit();
                            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                            break;
                        }
                        case 2:{
                            mapUitls.setMapViewVisibility(View.GONE);
                            mStage=Stage.THREE;
                            fragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                    .replace(R.id.fragment_container, fragment3)
                                    .commit();
                            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                            break;
                        }

                    }
                }
                return true;
            }
        });
    }

    public void showContacts(View v) {
        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestContactsPermissions(v);

        } else {

            mapUitls.initMap(mMapView,this,true,true);
        }
    }

    private void requestContactsPermissions(View v) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)
                ) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(v, "permission_contacts_rationale",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSIONS_CONTACT,
                                            REQUEST_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }
        // END_INCLUDE(contacts_permission_request)
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CONTACTS){
            if (PermissionUtil.verifyPermissions(grantResults)) {
                mapUitls.initMap(mMapView,this,true);

            } else {


            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            if (isopenTraffic==false){
                isopenTraffic=mapUitls.setTraffic(true);
                item.setTitle("关闭交通图");
            }else {
                isopenTraffic
                          =mapUitls.setTraffic(false);
                item.setTitle("开启交通图");
            }
        } else if (id == R.id.nav_gallery) {
                mapUitls.setGpsMap();
        } else if (id == R.id.nav_slideshow) {
            mapUitls.setMap();

        } else if (id == R.id.nav_manage) {
            SharedPreferences sharedPreferences=getSharedPreferences("USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("isloging",false);
            editor.putString("USERID","");
            editor.putString("PASSWORD","");
            editor.putString("PHONE","");
            editor.putString("CAR_ID","");
            editor.apply();
            Toast.makeText(MainActivity.this,"退出成功",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {

            showFingerprint();

        } else if (id == R.id.nav_send) {
            bluetoothinit.connet();
        }else if (id==R.id.leida){
            SharedPreferences sharedPreferences=getSharedPreferences("USER",MODE_PRIVATE);
            boolean islog=sharedPreferences.getBoolean("isloging",false);
            if (islog) {

                mleidaUtils.setLatLng(MapUitls.getMyLatlng(), 0);
                mapUitls.clearOverlay();
            }else{
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,Loging_activity.class);
                startActivity(intent);
            }
        }else if (id== R.id.car){
            SharedPreferences sharedPreferences=getSharedPreferences("USER",MODE_PRIVATE);
            boolean islog=sharedPreferences.getBoolean("isloging",false);
            if (islog){
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(),QueryCarIdActivity.class);
                startActivity(intent);
            }else {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,Loging_activity.class);
                startActivity(intent);
            }

        }else if (id==R.id.det_user){
            mleidaUtils.ClearInfo();
        }else if (id==R.id.wear){
            SharedPreferences sharedPreferences=getSharedPreferences("OPENWEAR",MODE_PRIVATE);
            boolean isopen=sharedPreferences.getBoolean("isopen",false);
            if (isopen){
                item.setTitle("打开手表");
                sharedPreferences.edit().putBoolean("isopen",false).apply();
            }else {
                item.setTitle("关闭手表");
                sharedPreferences.edit().putBoolean("isopen",true).apply();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private  void showFingerprint(){

        if (Build.VERSION.SDK_INT>=23) {
            showFingerprintAuthenticationDialogFragment();
        }else{
            dialogFragment_lollipop=new DialogFragment_lollipop();
            SharedPreferences Fist_SharedPreferences_lollipop=getSharedPreferences("Fist_SharedPreferences_lollipop",MODE_PRIVATE);
            boolean isFist=Fist_SharedPreferences_lollipop.getBoolean("Fist",true);
            if (isFist){
                dialogFragment_lollipop.setStage(DialogFragment_lollipop.Stage.FISIST);
                dialogFragment_lollipop.show(getFragmentManager(),"ABC");
            }else {
                dialogFragment_lollipop.setStage(DialogFragment_lollipop.Stage.FINGERPRINTLOLLIPOP);
                dialogFragment_lollipop.show(getFragmentManager(),"ABC");
            }
        }

    }

    @Override
    public void onPurchaseFailed() {
        Toast.makeText(this,"识别失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchased(byte[] signature) {
        Toast.makeText(this,"成功"+signature.toString(),Toast.LENGTH_SHORT).show();
        Fragment3.isuser=true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.action_voice){
            try {

                Intent intent = new Intent();
                intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                if (mStage == Stage.ONE) {
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出您的目的地");
                } else if (mStage == Stage.TOWEE) {
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出您感兴趣的信息类别");

                } else if (mStage == Stage.THREE) {
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出您要控制汽车的指令");
                }
                startActivityForResult(intent, 0);
            }catch (Exception e){
                Toast.makeText(MainActivity.this,"您的设备不支持语音服务",Toast.LENGTH_SHORT).show();

            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==0&&resultCode==RESULT_OK){
            allSuggest=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (mStage==Stage.ONE){

                if (!TextUtils.isEmpty(allSuggest.get(0))) {
                      if (!TextUtils.isEmpty(city)){
                            mapUitls.VioceMapkey(city,allSuggest.get(0),handler);
                      }
                  }
            }else if (mStage==Stage.TOWEE){
                if (!TextUtils.isEmpty(allSuggest.get(0))) {
                    Intent intent=new Intent();
                    intent.putExtra("TAG",allSuggest.get(0));
                    intent.setClass(this, Periphery_Activity.class);
                    startActivity(intent);
                }

            }else if (mStage==Stage.THREE){
                if (!TextUtils.isEmpty(allSuggest.get(0))) {

                    if(Fragment3.isuser){
                        String key=allSuggest.get(0);
                        boolean Keyisuser=true;
                        Log.e("TAG",key);
                        switch (key){
                            case "开窗":{
                                bluetoothinit.send("OW");
                                Keyisuser=false;
                                break;
                            }
                            case "关窗":{
                                bluetoothinit.send("CW");
                                Keyisuser=false;
                                break;
                            }
                            case "开空调":{
                                Keyisuser=false;
                                bluetoothinit.send("OA");
                                break;
                            }
                            case "关空调":{
                                Keyisuser=false;
                                bluetoothinit.send("CA");
                                break;
                            }
                            case "开车门":{
                                Keyisuser=false;
                                bluetoothinit.send("OD");
                                break;
                            }
                            case "关车门":{
                                Keyisuser=false;
                                bluetoothinit.send("CD");
                                break;
                            }
                            case "监控":{
                                Keyisuser=false;
                                bluetoothinit.send("OC");
                                break;
                            }
                            case "开振动":{
                                Keyisuser=false;
                                bluetoothinit.send("OS");
                                break;
                            }
                            case "关振动":{
                                Keyisuser=false;
                                bluetoothinit.send("CS");
                                break;
                            }
                            case "开启电源":{
                                Keyisuser=false;
                                bluetoothinit.send("OP");
                                break;
                            }
                            case "关闭电源":{
                                Keyisuser=false;
                                bluetoothinit.send("CP");
                                break;
                            }
                            case "汽车信息":{
                                Keyisuser=false;
                                bluetoothinit.send("OT");
                                break;
                            }
                        }
                        if (Keyisuser){
                            Toast.makeText(this,"指令不正确",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        fragment3.show();

                    }

                }
            }
        }
        if (requestCode==1&&resultCode==RESULT_OK){
            double latitude= data.getDoubleExtra("latitude",0);
            double longitude= data.getDoubleExtra("longitude",0);
            LatLng latLng=new LatLng(latitude,longitude);
            mapUitls.setOverlay(latLng,false,mMapView);
        }
        super.onActivityResult(requestCode, resultCode, data);
        bluetoothinit.Result(requestCode, resultCode, data);
    }
    public enum Stage {
        ONE,
        TOWEE,
        THREE

    }
    public void setreceive_code(String date){
        receive_code=date;
        Log.e("TAG",receive_code);
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mleidaUtils.stop();
        super.onDestroy();
    }
}
