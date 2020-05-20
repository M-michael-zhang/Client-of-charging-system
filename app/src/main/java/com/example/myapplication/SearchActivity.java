package com.example.myapplication;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.myapplication.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends FragmentActivity implements
        OnGetPoiSearchResultListener,OnGetSuggestionResultListener{
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> lists = new ArrayList<>();
    private ArrayList<String> listjl = new ArrayList<>();
    private Button btn1;
    private ArrayList<SuggestionResult.SuggestionInfo> listinfo = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        btn1 = findViewById(R.id.button1);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mPoiSearch = PoiSearch.newInstance();
        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPoiSearch.searchInCity(new PoiCitySearchOption()
//                        .city("北京") //必填
//                        .keyword("美食") //必填
//                        .pageNum(10));
                citySearch(1);
            }
        });
        OnGetPoiSearchResultListener listener2 = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                Toast.makeText(SearchActivity.this,
                        result.getTotalPageNum()+"sss",
                        Toast.LENGTH_SHORT).show();
                if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    Toast.makeText(SearchActivity.this, "未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                Toast.makeText(SearchActivity.this,
                        "bb",
                        Toast.LENGTH_SHORT).show();
                if (poiDetailSearchResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(SearchActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                } else {
                    List<PoiDetailInfo> poiDetailInfoList = poiDetailSearchResult.getPoiDetailInfoList();
                    if (null == poiDetailInfoList || poiDetailInfoList.isEmpty()) {
                        Toast.makeText(SearchActivity.this, "抱歉，检索结果为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (int i = 0; i < poiDetailInfoList.size(); i++) {
                        PoiDetailInfo poiDetailInfo = poiDetailInfoList.get(i);
                        if (null != poiDetailInfo) {
                            Toast.makeText(SearchActivity.this,
                                    poiDetailInfo.getName() + ": " + poiDetailInfo.getAddress(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
            //废弃
            @Override
            public void onGetPoiDetailResult(PoiDetailResult result) {
                Toast.makeText(SearchActivity.this,
                        "cc",
                        Toast.LENGTH_SHORT).show();
                if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(SearchActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchActivity.this,
                            result.getName() + ": " + result.getAddress(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult msg) {
                //处理sug检索结果

                if (msg == null || msg.getAllSuggestions() == null) {
                    Toast.makeText(SearchActivity.this, "未检索到当前地址",             Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        };
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mPoiSearch.setOnGetPoiSearchResultListener(listener2);

    }
    private void citySearch(int page) {

        // 设置检索参数
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        citySearchOption.city("金华");
        // 城市
        citySearchOption.keyword("浙江师范大学");
        // 关键字
        citySearchOption.pageCapacity(15);
        // 默认每页10条
        citySearchOption.pageNum(page);
        // 分页编号 // 发起检索请求
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword("浙江")
                .city("北京"));
    }
    public void onGetSuggestionResult(SuggestionResult msg) {

        // TODO Auto-generated method stub
        if (msg == null || msg.getAllSuggestions() == null) {
            Toast.makeText(this, "未检索到当前地址",             Toast.LENGTH_SHORT).show();
            return;
        }

        if (list != null) {
            list.clear();
        }

        if (lists != null) {
            lists.clear();
        }

        if (listjl != null) {
            listjl.clear();
        }

        if (listinfo != null) {
            listinfo.clear();
        }

        for (SuggestionResult.SuggestionInfo info : msg.getAllSuggestions()) {
            if (info.pt == null) continue;
            Log.e("info.ccity", "info.city" + info.city + "info.district" + info.district + "info.key" + info.key+"address"+info.toString());
            listinfo.add(info);
            list.add(info.key);
            lists.add(info.city + info.district + info.key);
            DecimalFormat df = new DecimalFormat("######0");
            String distance = df.format(DistanceUtil.getDistance(listinfo.get(0).pt, info.pt));
            listjl.add(distance);
        }


        if (listinfo.size() == 0) {
            Toast.makeText(SearchActivity.this, "未检索到当前地址", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
