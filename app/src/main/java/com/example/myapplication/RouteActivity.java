package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.myapplication.util.DrivingRouteOverlay;
import com.example.myapplication.util.OverlayManager;

import java.util.List;

public class RouteActivity extends AppCompatActivity {
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RoutePlanSearch mSearch = null;

    LatLng start_pt =null;

    LatLng end_pt = null;
    MapView mMapView = null;    // 地图View
    private BaiduMap mBaidumap = null;
    OverlayManager routeOverlay = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Intent intent = getIntent();
        start_pt = new LatLng(intent.getDoubleExtra("start_pt_lat",39.915071),intent.getDoubleExtra("start_pt_lon",116.403907));
        end_pt = new LatLng(intent.getDoubleExtra("end_pt_lat",39.915071),intent.getDoubleExtra("end_pt_lon",116.403907));
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        mSearch = RoutePlanSearch.newInstance();

        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaidumap);
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(RouteActivity.this, "抱歉，未找到结果"+result.error.toString(), Toast.LENGTH_SHORT).show();
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    if (result.getRouteLines().size() > 0) {
                        //获取路径规划数据,(以返回的第一条路线为例）
                        //为DrivingRouteOverlay实例设置数据
                        overlay.setData(result.getRouteLines().get(0));
                        //在地图上绘制DrivingRouteOverlay
                        overlay.addToMap();
                    MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(start_pt);
                    MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
                    mBaidumap.setMapStatus(msu);
                    mBaidumap.setMapStatus(status1);
                    }
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        };

        mSearch.setOnGetRoutePlanResultListener(listener);
        PlanNode stNode = PlanNode.withLocation(start_pt);
        PlanNode enNode = PlanNode.withLocation(end_pt);
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }

}
