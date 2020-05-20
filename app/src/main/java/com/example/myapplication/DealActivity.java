package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DealActivity extends AppCompatActivity {
    private TextView id_tv;
    private TextView state_tv;
    private Button start_btn;
    private TextView type_tv;
    private TextView address_tv;
    private TextView rate_tv;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try{
                if(msg.what==1){
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")){
                        finish();
                    }
                    Toast.makeText(DealActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        id_tv = findViewById(R.id.id);
        state_tv = findViewById(R.id.status);
        start_btn = findViewById(R.id.start);
        type_tv = findViewById(R.id.type);
        address_tv = findViewById(R.id.address);
        rate_tv = findViewById(R.id.rate);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("charge_id");
        String type = "" ;
        String status = "";
        final String address = intent.getStringExtra("charge_address");
        final String rate = intent.getStringExtra("charge_rate");
        switch (intent.getStringExtra("charge_type")){
            case "0": type = "直流";break;
            case "1": type = "交流";break;
            case "2": type = "混合电流";break;
        }
        switch (intent.getStringExtra("charge_status")){
            case "0": status = "空闲";break;
            case "1": status = "使用中";break;
            case "2": status = "被预约";break;
            case "3": status = "维修中";break;
        }
        id_tv.setText(id);
        state_tv.setText(status);
        type_tv.setText(type);
        address_tv.setText(address);
        rate_tv.setText(rate);
        final String finalStatus = intent.getStringExtra("charge_status");
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalStatus.equals("0")){
                    SharedPreferences userSettings = DealActivity.this.getSharedPreferences("setting", 0);
                    String uid = userSettings.getString("uid","");
                    if(uid.equals("")){
                        Toast.makeText(DealActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DealActivity.this, CaptcheLoginActivity.class);
                        startActivity(intent);
                    }
                    String ip = getResources().getString(R.string.service_address);
                    final OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder().url("http://"+ip+"/pile/usePile?pid="+id_tv.getText()+"&uid="+uid).build();
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
                else{
                    Toast.makeText(DealActivity.this,"该设备已被别人使用啦",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
