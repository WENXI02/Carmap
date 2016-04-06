package com.example.wenxi.carmap;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.MapView;

import com.example.bluetooth.library.BluetoothState;
import com.example.bluetooth.library.Bluetoothinit;
import com.example.wenxi.carmap.Fingerprint.BaseActivity;
import com.example.wenxi.carmap.Fragment.Fragment2;
import com.example.wenxi.carmap.Fragment.Fragment3;
import com.example.wenxi.carmap.Utils.MapUitls;
import com.example.wenxi.carmap.Utils.PermissionUtil;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg.what== BluetoothState.MESSAGE_UI){
                String data=(String)msg.obj;
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                Log.e("date",data);

            }
        }
    };
    private Bluetoothinit bluetoothinit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView=(MapView) findViewById(R.id.bmapView);
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


        bluetoothinit=new Bluetoothinit(this,handler);
        bluetoothinit.onstatrt();
        bluetoothinit.initBluetooth();
        bluetoothinit.setisAndroid(false);
        this.setBluetoothinit(bluetoothinit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("位置信息", R.drawable.ic_maps_place, Color.parseColor("#455C65"));
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("周边服务", R.drawable.ic_maps_local_bar, Color.parseColor("#00886A"));
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("我的车辆", R.drawable.ic_maps_local_restaurant, Color.parseColor("#8B6B62"));
        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        bottomNavigation.addItems(bottomNavigationItems);
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
//        bottomNavigation.setColored(true);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                Fragment2 fragment2=new Fragment2();
                Fragment3 fragment3=new Fragment3();
                if (!wasSelected){
                    switch (position){
                        case 0:{
                            findViewById(R.id.fragment_container).setVisibility(View.GONE);
                            mapUitls.setMapViewVisibility(View.VISIBLE);
                            mapUitls.setmLocClient();//重新定位
                            break;
                        }
                        case 1:{
                            mapUitls.setMapViewVisibility(View.GONE);

                            fragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                    .replace(R.id.fragment_container, fragment2)
                                    .commit();
                            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                            break;
                        }
                        case 2:{
                            mapUitls.setMapViewVisibility(View.GONE);

                            fragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                    .replace(R.id.fragment_container, fragment3)
                                    .commit();
                            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                            break;
                        }

                    }
                }
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

        } else if (id == R.id.nav_share) {

            if (Build.VERSION.SDK_INT>=23) {

                showFingerprintAuthenticationDialogFragment();
            }else{
                bluetoothinit.send("hello");
            }

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPurchaseFailed() {
        Toast.makeText(this,"识别失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchased(byte[] signature) {
        Toast.makeText(this,"成功"+signature.toString(),Toast.LENGTH_SHORT).show();
    }
}
