package com.example.wenxi.carmap.Utils;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.wenxi.carmap.R;

/**
 * Created by wenxi on 16/4/4.
 */
public class RoutePlanUtils implements OnGetRoutePlanResultListener {
    private MapView mMapView;
    private BaiduMap baiduMap;
    boolean useDefaultIcon = false;
    private Activity activity;
    private DrivingRouteResult mDrivingRouteResult;
    private DrivingRouteOverlay overlay;
    private int juli=0;
    public RoutePlanUtils(MapView mapView,Activity m){
        mMapView=mapView;
        baiduMap=mMapView.getMap();
        activity=m;
    }



    public void setdrivingSearch(LatLng start, LatLng end, DrivingRoutePlanOption.DrivingPolicy DrivingPolicy){
        RoutePlanSearch mPlanSearch=RoutePlanSearch.newInstance();
        mPlanSearch.setOnGetRoutePlanResultListener(this);
        PlanNode stNode=PlanNode.withLocation(start);
        PlanNode enNode=PlanNode.withLocation(end);
        DrivingRoutePlanOption option=new DrivingRoutePlanOption();
        option.from(stNode).to(enNode);
        option.policy(DrivingPolicy);
        option.trafficPolicy( DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC);
        mPlanSearch.drivingSearch(option);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        //获取步行线路规划结果
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        //获取公交换乘路径规划结果
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        //获取驾车线路规划结果
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(activity, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            mDrivingRouteResult=drivingRouteResult;
            overlay = new MyDrivingRouteOverlay(baiduMap);
            baiduMap.setOnMarkerClickListener(overlay);
            juli=mDrivingRouteResult.getRouteLines().get(0).getDistance();
            overlay.setData(mDrivingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();

        }

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
        //骑车路线
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }
    public void setdriving(RouteNode node){
        if (overlay!=null){
//            overlay.removeFromMap();
            DrivingRouteLine r= mDrivingRouteResult.getRouteLines().get(0);
            r.setStarting(node);
            overlay.setData(r);
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }
    public int getdrivingjuli(){
        return juli;
    }
}
