package com.example.wenxi.carmap.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;

/**
 * Created by wenxi on 16/4/15.
 */
public class leidaUtils implements RadarUploadInfoCallback, RadarSearchListener{
    RadarSearchManager RadarSearchManagermManager;
    Context mcontext;
    private Handler mhandler;
    public static int LEIDA=0;
    public static int LEIDAError=1;
    private static RadarNearbyResult mRadarNearbyResult;


    public leidaUtils(Context context,Handler handler){
        mcontext=context;
        mhandler=handler;
        RadarSearchManagermManager = RadarSearchManager.getInstance();
        RadarSearchManagermManager.addNearbyInfoListener(this);
    }

    //上传我的位置
    public   void  setLatLng(LatLng latLng,int index){
        SharedPreferences sharedPreferences=mcontext.getSharedPreferences("USER",mcontext.MODE_PRIVATE);
        //周边雷达设置用户身份标识，id为空默认是设备标识
        RadarSearchManagermManager.setUserID(sharedPreferences.getString("USERID",""));
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = sharedPreferences.getString("PHONE","");
        info.pt = MapUitls.getMyLatlng();
        RadarSearchManagermManager.uploadInfoRequest(info);
        RadarNearbySearchOption option = new RadarNearbySearchOption().centerPt(latLng).pageNum(index).radius(5000);
        RadarSearchManagermManager.nearbyInfoRequest(option);

    }
    public  void stop(){
        RadarSearchManagermManager.removeNearbyInfoListener(this);
        RadarSearchManagermManager.clearUserInfo();
        RadarSearchManagermManager.destroy();
        RadarSearchManagermManager = null;
    }

    @Override
    public RadarUploadInfo onUploadInfoCallback() {
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = "我的位置";
        info.pt = MapUitls.getMyLatlng();
        return info;
    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {

        // TODO Auto-generated method stub
        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            //上传成功
            Log.e("单次上传位置成功","单次上传位置成功");

        } else {
            //上传失败
            Log.e("单次上传位置失败","单次上传位置失败");
        }

    }

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {

        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            Log.e("查询周边成功","查询周边成功");
            mRadarNearbyResult=radarNearbyResult;
            Message message=new Message();
            message.what=LEIDA;
            mhandler.sendMessage(message);
        } else {

            //获取失败
            Message message=new Message();
            message.what=LEIDAError;
            mhandler.sendMessage(message);


        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }

    public static RadarNearbyResult getradalist(){
        return mRadarNearbyResult;
    }
}
