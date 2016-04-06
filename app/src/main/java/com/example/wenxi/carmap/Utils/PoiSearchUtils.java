package com.example.wenxi.carmap.Utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenxi on 16/3/24.
 */
public class PoiSearchUtils {


    public  List<PoiInfo>mPoiInfo;
    public  List<PoiDetailResultBeen>mPoiDetailResultBeen=new ArrayList<>();
    public Handler mhandler;

    public  void setPoiSearch(LatLng latLng,String keyword,int pagenum){
            PoiSearch mPoiSearch = PoiSearch.newInstance();
            mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
            PoiNearbySearchOption option=new PoiNearbySearchOption();
            option.location(latLng);
            option.keyword(keyword);
            option.pageCapacity(20);
            option.pageNum(pagenum);
            option.radius(10000);//10000米内的美食
            mPoiSearch.searchNearby(option);


    }

    public void setsearchPoiDetail(String UID){
        if (!TextUtils.isEmpty(UID)) {
            Log.e("UID","UID不为空");
            PoiSearch mPoiSearch = PoiSearch.newInstance();
            mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
            PoiDetailSearchOption option = new PoiDetailSearchOption();
            mPoiSearch.searchPoiDetail(option.poiUid(UID));
        }else{
            Log.e("UID","UID为空");
        }


    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果
            if (result.getAllPoi()!=null) {
                mPoiInfo=result.getAllPoi();
                Message message=new Message();
                message.what=0;
                mhandler.sendMessage(message);
            }else {
                Log.e("TAG","没有返回结果");
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {

            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                //详情检索失败
                // result.error请参考SearchResult.ERRORNO
                Log.e("TAG","返回结果为空");
            }
            else {
                //检索成功
                //获取Place详情页检索结果
                Log.e("TAG","返回结果不为空");
                PoiDetailResultBeen been=new PoiDetailResultBeen();
                been.setAddress(result.getAddress());
                been.setCheckinNum(result.getCheckinNum());
                been.setCommentNum(result.getCommentNum());
                been.setDetailUrl(result.getDetailUrl());
                been.setEnvironmentRating(result.getEnvironmentRating());
                been.setFacilityRating(result.getFacilityRating());
                been.setGrouponNum(result.getGrouponNum());
                been.setHygieneRating(result.getHygieneRating());
                been.setImageNum(result.getImageNum());
                been.setLocation(result.getLocation());
                been.setOverallRating(result.getOverallRating());
                been.setPrice(result.getPrice());
                been.setServiceRating(result.getServiceRating());
                been.setShopHours(result.getShopHours());
                been.setTag(result.getTag());
                been.setTelephone(result.getTelephone());
                been.setName(result.getName());
                mPoiDetailResultBeen.add(been);
                Message message=new Message();
                message.what=1;
                mhandler.sendMessage(message);
            }

        }
    };
    public List<PoiInfo> getmPoiInfo(){
            return mPoiInfo;
    }
    public List<PoiDetailResultBeen>getPoiDetailResultBeen(){return mPoiDetailResultBeen;}
    public void setHandler(Handler handler){
        mhandler=handler;
    }

}
