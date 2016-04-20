package com.example.wenxi.carmap.Utils;


import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.wenxi.carmap.Periphery_Activity;
import com.example.wenxi.carmap.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wenxi on 16/3/23.
 */
public class MapUitls {
    private  int datanumber=0;
    private Handler handler;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                if (mPoiInfo!=null){
                    mPoiInfo.clear();
                }
                mPoiInfo.addAll(utils.getmPoiInfo());
                if (mPoiDetailResultBeen!=null){
                    mPoiDetailResultBeen.clear();
                }
                for (int i=0;i<mPoiInfo.size();i++) {
                    utils.setsearchPoiDetail(mPoiInfo.get(i).uid);

                }


            }else if(msg.what==1){
                if (datanumber<19) {//每页返回20条信息
                    mPoiDetailResultBeen=utils.getPoiDetailResultBeen();
                    datanumber++;
                }else{
                    datanumber=0;
                    Message message=new Message();
                    message.what=Periphery_Activity.OK;
                    message.obj=mPoiDetailResultBeen;
                    Periphery_Activity.handler.sendMessage(message);
                    Log.e("OK","OK");
                }

            }
        }
    };
    public  List<PoiInfo>mPoiInfo=new ArrayList<>();//周边信息集合
    public  List<PoiDetailResultBeen>mPoiDetailResultBeen=new ArrayList<>();//周边信息详情
    private static LatLng MyLatlng;//当前的坐标
    PoiSearchUtils utils;
    LocationClient mLocClient;
    public MyLocationListenner myListener ;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    MapView mMapView;
    BaiduMap mBaiduMap;
    boolean isFirstLoc = true; // 是否首次定位
    private List<Marker> marker=new ArrayList<>();

    public void initMap(MapView mapView, Activity activity,boolean isgo){
        mMapView=mapView;
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
        // 定位初始化
        mLocClient = new LocationClient(activity);
        myListener = new MyLocationListenner(isgo);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//高精度定位模式
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();

    }
    public void initMap(MapView mapView, Activity activity,boolean isgo,boolean isagain){
        mMapView=mapView;
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
        // 定位初始化
        mLocClient = new LocationClient(activity);
        myListener = new MyLocationListenner(isgo);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        if (isagain) {
            option.setScanSpan(1000);
        }
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//高精度定位模式
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        boolean isgo=true;
        public MyLocationListenner(boolean isMapgo){

            isgo= isMapgo;
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            MyLatlng=new LatLng(locData.latitude,locData.longitude);
            if (isFirstLoc) {
                mBaiduMap.setMyLocationData(locData);
                isFirstLoc = false;
                MyLatlng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                if (isgo) {
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(MyLatlng).zoom(19.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
                PoiSearchUtils poiSearchUtils = new PoiSearchUtils();
                poiSearchUtils.setHandler(handler);
                poiSearchUtils.setGetReverseGeoCode(MyLatlng);

            }

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }






    /**
     * MapView显示和隐藏
     */
    public void setMapViewVisibility(int Visibility){

        mMapView.setVisibility(Visibility);
    }

    /**
     * 设置是否开启交通图
     */
    public boolean setTraffic(boolean traffic){
        mBaiduMap.setTrafficEnabled(traffic);
        return traffic;
    }
    /**
     * 设置开启卫星图
     */
     public void setGpsMap(){
         mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

     }
    /**
     * 设置开启普通图
     */
    public void setMap(){
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

    }

    /**
     * 设置重新定位
     */
    public void setmLocClient(){
        if (mLocClient!=null){
            isFirstLoc = true;
            mLocClient.stop();
            mLocClient.start();
        }

    }

    /**
     * 周边搜素
     * @param keyword 关键字
     * @param pagenum 页
     */
    public void setKeyword(String keyword,int pagenum){
        if (MyLatlng!=null) {
            utils = new PoiSearchUtils();
            utils.setPoiSearch(MyLatlng, keyword,pagenum);
            utils.setHandler(mhandler);
        }
    }

    public void setOverlay(LatLng latLng ,boolean isgo,MapView mapView){
        BaiduMap mBaiduMap=mapView.getMap();
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_place_black_24dp);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        Marker marker1=(Marker) mBaiduMap.addOverlay(option);
        marker.add(marker1);
        if (isgo){
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(15.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    public void clearOverlay(){
        for (int i=0;i<marker.size();i++){
            marker.get(i).remove();
        }

    }

    public void VioceMap(String city,String keyword,Handler handler){
        PoiSearchUtils poiSearchUtils = new PoiSearchUtils();
        poiSearchUtils.setHandler(handler);
        poiSearchUtils.setGetGeoCode(keyword,city);
    }
    public void VioceMapkey(String city,String keyword,Handler handler){
        PoiSearchUtils poiSearchUtils = new PoiSearchUtils();
        poiSearchUtils.setHandler(handler);
        poiSearchUtils.setSuggestion(keyword, city);
    }
    public void setHandler(Handler mhandler){handler=mhandler;}
    public static   LatLng getMyLatlng(){
        return MyLatlng;
    }
    public List<PoiInfo> getmPoiInfo(){
        return mPoiInfo;
    }
    public List<PoiDetailResultBeen>getPoiDetailResultBeen(){return mPoiDetailResultBeen;}
}

