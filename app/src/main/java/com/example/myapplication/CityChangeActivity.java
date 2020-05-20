package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CityChangeActivity extends AppCompatActivity {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try{
                if (msg.what == 1) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")){
                        JSONArray provinceArray = jsonObject.getJSONObject("extend").getJSONArray("Province");
                        String provinceName;
                        String provinceId;
                        for(int i=0;i<provinceArray.length();i++){
                            provinceName = provinceArray.getJSONObject(i).getString("provinceName");
                            provinceId = provinceArray.getJSONObject(i).getString("provinceId");
                            provinceId_list.add(provinceId);
                            province_list.add(provinceName);
                        }
                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(CityChangeActivity.this,android.R.layout.simple_list_item_1,province_list);
                        province_listview.setAdapter(adapter);
                    }
                }
                if (msg.what == 2) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")){
                        JSONArray cityArray = jsonObject.getJSONObject("extend").getJSONArray("City");
                        String cityName;
                        String cityId;
                        city_list.clear();
                        cityId_list.clear();
                        for(int i=0;i<cityArray.length();i++){
                            cityName = cityArray.getJSONObject(i).getString("cityName");
                            cityId = cityArray.getJSONObject(i).getString("cityId");
                            cityId_list.add(cityId);
                            city_list.add(cityName);
                        }
                        ArrayAdapter<String> cityAdapter=new ArrayAdapter<String>(CityChangeActivity.this,android.R.layout.simple_list_item_1,city_list);
                        city_listview.setAdapter(cityAdapter);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private TextView tv_current_city;
    private ImageView iv_return;
    //省份列表
    private ListView province_listview;
    List<String> province_list;
    List<String> provinceId_list;
    //城市列表
    private ListView city_listview;
    List<String> city_list;
    List<String> cityId_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_change);
        tv_current_city = findViewById(R.id.tv_current_city);
        iv_return = findViewById(R.id.toReturn);
        province_listview=(ListView)findViewById(R.id.listv_province);
        province_list = new ArrayList<String>();
        provinceId_list = new ArrayList<String>();
        city_listview=(ListView)findViewById(R.id.listv_city);
        city_list = new ArrayList<String>();
        cityId_list = new ArrayList<String>();

        Bundle bundle=this.getIntent().getExtras();
        String currentPosition=bundle.getString("currentPosition");
        tv_current_city.setText(currentPosition);

        //初始化
        getProvince();
        getCity("11");

        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,province_list);
//        province_listview.setAdapter(adapter);
        province_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getCity(provinceId_list.get(i));
            }
        });
        city_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent();
                intent.putExtra("newPosition", city_list.get(i));
                intent.putExtra("newPositionId", cityId_list.get(i));
                setResult(RESULT_OK, intent);//回传数据到主Activity
                finish();
            }
        });
//        city_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                getCounty(cityId_list.get(i));
//            }
//        });

//        county_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent=new Intent();
//                intent.putExtra("newPosition", county_list.get(i));
//                setResult(RESULT_OK, intent);//回传数据到主Activity
//                finish();
//            }
//        });
    }
    private void getProvince(){
        String ip = getResources().getString(R.string.service_address);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://"+ip+"/position/getProvince").build();
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
                        Log.e("ServiceCallBack",jsonObject.toString());
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

    private void getCity(String provinceId){

        String ip = getResources().getString(R.string.service_address);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://"+ip+"/position/getCity?provinceId="+provinceId).build();
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
                        Log.e("ServiceCallBack",jsonObject.toString());
                        mHandler.obtainMessage(2, jsonObject).sendToTarget();
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
