package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private ImageView iv_return;
    private Button button;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String result = (String) msg.obj;

                try {

                    JSONObject jsonObject = new JSONObject(result);
                    String k = jsonObject.getString("id");

                    String a = jsonObject.getString("num")+"%";
                    String b = jsonObject.getString("starttime");
                    String c = jsonObject.getString("endtime");
                    textView1.setText(k);
                    textView2.setText(a);
                    textView3.setText(b.substring(11,19));
                    if(c.equals("null")){
                        textView4.setText("正在充电");
                    }
                    else{
                        textView4.setText(c.substring(11,19));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        iv_return = findViewById(R.id.toReturn);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textView1 = findViewById(R.id.id);
        textView2 = findViewById(R.id.state);
        textView3 = findViewById(R.id.state2);
        textView4 = findViewById(R.id.state3);
        button = findViewById(R.id.start);
        getData();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
            }
        private void getData(){
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder().url("http://123.207.233.171:8080/getPowerById?id=1").build();
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
                            String result = response.body().string();
                            mHandler.obtainMessage(1, result).sendToTarget();

                        } else {
                            throw new IOException("Unexpected code:" + response);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }



}
