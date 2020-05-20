package com.example.myapplication.fragements;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.myapplication.CaptcheLoginActivity;
import com.example.myapplication.CityChangeActivity;
import com.example.myapplication.LogActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.RouteActivity;
import com.example.myapplication.SearchActivity;
import com.example.myapplication.bean.Pile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


public class Fragement1 extends Fragment implements OnGetSuggestionResultListener, AdapterView.OnItemClickListener {
    @SuppressWarnings("unused")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try{
                if (msg.what == 1) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")){
                        JSONArray piles = jsonObject.getJSONObject("extend").getJSONArray("Pile");
                        pileList.clear();
                        for(int i=0;i<piles.length();i++){
                            pileList.add(new Pile(Double.parseDouble(piles.getJSONObject(i).getString("locationX")),
                                    Double.parseDouble(piles.getJSONObject(i).getString("locationY")),
                                    piles.getJSONObject(i).getString("type"),
                                    piles.getJSONObject(i).getString("status"),
                                    piles.getJSONObject(i).getString("id")));
                        }
                        Log.e("test",pileList.size()+"ss"+pileList.get(0).toString());
                        initOverlay();

                    }
                }
                if(msg.what==2){
                    int type = (Integer) msg.obj;
                    if(type==1){
                        updateMap(0);
                        getPilesForPosition(tv_city.getText().toString(),currentCityId);
                        initOverlay();
                    }

                }
                if(msg.what==3){
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")){
                        getPilesForPosition(tv_city.getText().toString(),"");
                        initOverlay();
                        mBaiduMap.hideInfoWindow();
                    }
                    Toast.makeText(getActivity(),jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    LocationClient mLocClient;
    private List<Pile> pileList;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LinearLayout toChangeCity;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private String  mCurrentCity ="";
    private float mCurrentAccracy;
    private int mCurrentDirection = 0;
    private MapView mMapView;
    private AutoCompleteTextView et_key = null;
    private ArrayAdapter<String> sugAdapter = null;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private InfoWindow mInfoWindow;
    private LinearLayout infoView;
    RadioGroup.OnCheckedChangeListener radioButtonListener;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;
    private Button search_btn;
    private TextView tv_city;
    private int currentTarget = -1;
    private LatLng dh_target;
    private String currentCityId="";
    View test1;
    View test2;
    private Button btn_dh;
    private SuggestionSearch mSuggestionSearch = null;
    private Button toMyLocation;
    private TextView infowindow_id;
    private TextView infowindow_type;
    private TextView infowindow_status;
    private Button infowindow_daohang;
    private Button infowindow_booking;
    private Marker currentMarket;
    /*
    sugType=0 输入框搜索结果调监听接口
    sugType=1 切换城市调监听接口
    * */
    private int sugType = 0;

    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
//    BitmapDescriptor bd1 = BitmapDescriptorFactory
//            .fromResource(R.drawable.icon_geo);
    MapStatus.Builder builder;
    ArrayList<String> listkey = new ArrayList<String>();
    ArrayList<LatLng> listpt = new ArrayList<LatLng>();
    ArrayList<String> listcity = new ArrayList<String>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        test1 = inflater.inflate(R.layout.test1, container, false);
        mMapView = (MapView) test1.findViewById(R.id.bmapView);
        toMyLocation = test1.findViewById(R.id.toMyLocation);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        et_key = (AutoCompleteTextView)test1.findViewById(R.id.et_key);
        tv_city = test1.findViewById(R.id.tv_city);
        toChangeCity = test1.findViewById(R.id.toChangeCity);
        sugAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,listkey);
        et_key.setAdapter(sugAdapter);
        et_key.setThreshold(1);
        et_key.setOnItemClickListener(this);
        pileList = new ArrayList<Pile>();
        search_btn = test1.findViewById(R.id.search_btn);
        mBaiduMap = mMapView.getMap();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();

        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        builder = new MapStatus.Builder();
        mLocClient.start();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        toChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CityChangeActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("currentPosition", tv_city.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
        toMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(mCurrentLat,mCurrentLon));
                tv_city.setText(mCurrentCity);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
            }
        });
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                dh_target = marker.getPosition();
                if(currentTarget==-1){
                    mInfoWindow = new InfoWindow(getInfoWindoView(marker),dh_target, -77);
                }
                else {
                    if(!dh_target.equals(listpt.get(currentTarget)))
                    {
                        mInfoWindow = new InfoWindow(getInfoWindoView(marker),dh_target, -77);
                    }

                }

                mBaiduMap.showInfoWindow(mInfoWindow);
                return false;}
            });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //隐藏InfoWindow
                if (mInfoWindow != null) {
                    mBaiduMap.hideInfoWindow();
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTarget<0){
                    Toast.makeText(getActivity(),"请先选择地点",Toast.LENGTH_SHORT).show();
                }
                else {
                    updateMap(currentTarget);

                }
            }
        });

        et_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(et_key.getText().toString())
                        .city(tv_city.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return test1;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Bundle bundle = data.getExtras();
                String currentCity = data.getStringExtra("newPosition");
                currentCityId = data.getStringExtra("newPositionId");
                tv_city.setText(currentCity);

                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(currentCity)
                        .city(currentCity));
                sugType = 1;
                break;
            default:
                break;
        }
    }

    public void initOverlay() {
        for(int i=0;i<pileList.size();i++){
            Pile o = pileList.get(i);
            LatLng temp = new LatLng(o.getLongitude(),o.getLatitude());
            MarkerOptions MarkerOptionsTemp = new MarkerOptions().position(temp).icon(bd)
                    .zIndex(9).draggable(true);
            mMarkerA = (Marker) (mBaiduMap.addOverlay(MarkerOptionsTemp));
        mMarkerA.setTitle(o.getId()+"~"+o.getStatus()+"~"+o.getType());
        }
//
//        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bd)
//                .zIndex(9).draggable(true);
//        MarkerOptions ooB = new MarkerOptions().position(llB).icon(bd)
//                .zIndex(9).draggable(true);
//        MarkerOptions ooC = new MarkerOptions().position(llC).icon(bd)
//                .zIndex(9).draggable(true);
//        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
//        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooB));
//        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooC));

        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bd);


    }
    public void updateOverlay() {

        double lat = listpt.get(currentTarget).latitude;
        double lon = listpt.get(currentTarget).longitude;
        LatLng llA = new LatLng(lat-0.002100, lon-0.000512);
        LatLng llB= new LatLng(lat+0.002100, lon+0.000512);
        LatLng llC= new LatLng(lat-0.005100, lon-0.003512);
        LatLng center = listpt.get(currentTarget);
        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bd)
                .zIndex(9).draggable(true);
        MarkerOptions ooB = new MarkerOptions().position(llB).icon(bd)
                .zIndex(9).draggable(true);
        MarkerOptions ooC = new MarkerOptions().position(llC).icon(bd)
                .zIndex(9).draggable(true);
//        MarkerOptions ooD = new MarkerOptions().position(center).icon(bd1)
//                .zIndex(9).draggable(true);
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooB));
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooC));
//        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooD));
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bd);
//        giflist.add(bd1);


    }
    @Override
    public void onGetSuggestionResult(SuggestionResult msg) {

        if ((msg == null || msg.getAllSuggestions() == null)&&!et_key.getText().equals("")) {
            Toast.makeText(getActivity(), "未检索到当前地址",             Toast.LENGTH_SHORT).show();
            return;
        }
            listkey.clear();
            listpt.clear();
            listcity.clear();

        for (SuggestionResult.SuggestionInfo info : msg.getAllSuggestions()) {
            if (info.pt == null) continue;

            Log.e("info.ccity", "info.city" + info.key + "info.pt" + info.pt+"city:"+info.getCity());
            listkey.add(info.key);
            listpt.add(info.pt);
            listcity.add(info.getCity());
        }
        sugAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                listkey);
        et_key.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
        mHandler.obtainMessage(2,sugType).sendToTarget();
        sugType = 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentTarget = (int) et_key.getAdapter().getItemId(position);
    }

    public class MyLocationListenner extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置

            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            mCurrentCity = location.getCity();
            SharedPreferences userSettings = getActivity().getSharedPreferences("setting", 0);
            SharedPreferences.Editor editor = userSettings.edit();
            editor.putString("Latitude", ""+mCurrentLat);
            editor.putString("Longitude", ""+mCurrentLon);
            editor.commit();

            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
//                initOverlay();
                tv_city.setText(location.getCity());
                getPilesForPosition(location.getCity(),"");
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
    public void updateMap(int currentTarget){
        LatLng targetPt = listpt.get(currentTarget);
        MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(targetPt);
        float zoom = 14.0f; // 默认 11级
        builder.target(targetPt).zoom(zoom);
        getPilesForPosition(listcity.get(currentTarget),"");
        initOverlay();
        tv_city.setText(listcity.get(currentTarget));
        mBaiduMap.setMapStatus(status1);
    }

    public void getPilesForPosition(String cityName,String cityId){

        String ip = getResources().getString(R.string.service_address);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://"+ip+"/pile/getPilesByPosition?cityName="+cityName+"&cityId="+cityId).build();
        //新建一个线程，用于得到服务器响应的参数
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.e("ServiceCallBack","获取充电桩"+jsonObject.getString("msg"));
                        mHandler.obtainMessage(1, jsonObject).sendToTarget();
                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPilesForPosition(tv_city.getText().toString(),"");
        initOverlay();
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
//    baidumap_infowindow = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.infowindow, null);
//    RelativeLayout layout = (RelativeLayout) baidumap_infowindow.getChildAt(0);
//    infowindow_id = (TextView) layout.getChildAt(0);
//    infowindow_type = (TextView) layout.getChildAt(2);
//    infowindow_status = (TextView) layout.getChildAt(4);
//    infowindow_daohang = (Button) layout.getChildAt(5);
    private View  getInfoWindoView(final Marker marker){
        if (null == infoView) {
            infoView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.infowindow, null);
        }
        infowindow_id = (TextView) infoView.findViewById(R.id.id);
        infowindow_status = (TextView) infoView.findViewById(R.id.status);
        infowindow_type = (TextView) infoView.findViewById(R.id.type);
        infowindow_booking = infoView.findViewById(R.id.btn_book);
        infowindow_daohang = (Button) infoView.findViewById(R.id.btn_dh);
        String info = marker.getTitle();
        final String id = info.split("~")[0];
        String status =info.split("~")[1];
        String type =info.split("~")[2];
        infowindow_id.setText("编号： "+id);
        infowindow_type.setText(type.equals("0")?"直流":type.equals("1")?"交流":"直流/交流");
        infowindow_status.setText(status.equals("0")?"空闲":status.equals("1")?"使用中":status.equals("2")?"被预约":"维修中");
        if(status.equals("0")){
            infowindow_status.setTextColor(getResources().getColor(R.color.colorPrimary));
            infowindow_booking.setVisibility(View.VISIBLE);
            infowindow_booking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookingPile(id);
                }
            });
        }else{
            infowindow_status.setTextColor(getResources().getColor(R.color.red_text));
            infowindow_booking.setVisibility(View.GONE);
        }
        infowindow_daohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test","导航按钮点击");
                Intent intent = new Intent(getActivity(), RouteActivity.class);
                intent.putExtra("start_pt_lat",mCurrentLat);
                intent.putExtra("start_pt_lon",mCurrentLon);
                intent.putExtra("end_pt_lat",dh_target.latitude);
                intent.putExtra("end_pt_lon",dh_target.longitude);
                startActivity(intent);
            }
        });
        infoView.setMinimumHeight(200);
        infoView.setMinimumWidth(400);
        return  infoView;
    }

    private void bookingPile(String pileId){
        SharedPreferences userSettings = getActivity().getSharedPreferences("setting", 0);
        String uid = userSettings.getString("uid","");
        if(uid.equals("")){
            Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), CaptcheLoginActivity.class);
            startActivity(intent);
        }
        String ip = getResources().getString(R.string.service_address);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://"+ip+"/pile/bookPile?pid="+pileId+"&uid="+uid).build();
        //新建一个线程，用于得到服务器响应的参数
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        JSONObject jsonObject = new JSONObject(response.body().string());
//                        Log.e("ServiceCallBack",response.body().string());
                        mHandler.obtainMessage(3, jsonObject).sendToTarget();
                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
