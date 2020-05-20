package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.util.DensityUtil;
import com.example.myapplication.util.JWebSocketClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessageDetailActivity extends AppCompatActivity {

    ScrollView mscrollView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try{
                if (msg.what == 1) {
                    String receive_meseage = (String) msg.obj;
                    addmes_left(receive_meseage);
                    readMessage();
                    mscrollView.post(new Runnable() {
                        public void run() {
                            mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
                if(msg.what == 2){
                    JSONArray jsonArray = (JSONArray) msg.obj;
                    if(jsonArray!=null){
                        String message;
                        String type;
                        for(int i=0;i<jsonArray.length();i++){
                            message = jsonArray.getJSONObject(i).getString("content");
                            type = jsonArray.getJSONObject(i).getString("type");
                            if(type.equals("0")){
                                addmes_right(message);
                            }else{
                                addmes_left(message);
                            }

                        }
                        mscrollView.post(new Runnable() {
                            public void run() {
                                mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }else{
                        Toast.makeText(MessageDetailActivity.this,"获取消息记录失败",Toast.LENGTH_SHORT).show();
                    }

                }
                if(msg.what == 3){
                    Toast.makeText(MessageDetailActivity.this,"更新消息阅读状态失败",Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 4){
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")) {
                        String content = jsonObject.getJSONObject("extend").getString("content");
                        addmes_right(content);
                    }
                    Toast.makeText(MessageDetailActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                    mscrollView.post(new Runnable() {
                        public void run() {
                            mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private LinearLayout lay;
    private Button bo;
    private Button send_btn;
    private EditText send_mes;
    private  LinearLayout con_linear;
    private RelativeLayout con_rea;
    private LinearLayout con_linear2;
    private TextView con_text;
    private ImageView con_img;
    private ImageView iv_return;
    JWebSocketClient client;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        lay = findViewById(R.id.content);
        send_btn = findViewById(R.id.send_btn);
        send_mes = findViewById(R.id.send_mes);
        mscrollView = (ScrollView) findViewById(R.id.messageScroll);
        iv_return = findViewById(R.id.toReturn);
        //获取用户id
        SharedPreferences userSettings= MessageDetailActivity.this.getSharedPreferences("setting", 0);
        uid = userSettings.getString("uid","");
        if(uid.equals("")){
            finish();
        }
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info  = send_mes.getText().toString();
                if (info.equals(""))
                {
                    Toast.makeText(MessageDetailActivity.this,"消息不能为空噢",Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(info);
                    send_mes.setText("");
                }


            }
        });

        loadmes();

        String ip = getResources().getString(R.string.service_address);
        URI uri = URI.create("ws://"+ip+"/imserver/"+uid);

        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                //message就是接收到的消息
                Log.e("JWebSClientService", message);
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    mHandler.obtainMessage(1, jsonObject.getString("content")).sendToTarget();
                    readMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void addmes_right(String info){
        con_linear = new LinearLayout(MessageDetailActivity.this);
        con_rea = new RelativeLayout(MessageDetailActivity.this);
        con_linear2 = new LinearLayout(MessageDetailActivity.this);
        con_text = new TextView(MessageDetailActivity.this);
        con_img = new ImageView(MessageDetailActivity.this);
        con_img.setImageResource(R.drawable.tx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dp2px(MessageDetailActivity.this,40),DensityUtil.dp2px(MessageDetailActivity.this,40));
        params.setMargins(DensityUtil.dp2px(MessageDetailActivity.this,10f),0,0,0);
        con_img.setLayoutParams(params);



        RelativeLayout.LayoutParams rea_params = new RelativeLayout.LayoutParams(DensityUtil.dp2px(MessageDetailActivity.this,300),RelativeLayout.LayoutParams.WRAP_CONTENT);
        rea_params.setMargins(0,DensityUtil.dp2px(MessageDetailActivity.this,20f),0,0);
        con_rea.setLayoutParams(rea_params);
        con_rea.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams lin2_params = new LinearLayout.LayoutParams(DensityUtil.dp2px(MessageDetailActivity.this,240),LinearLayout.LayoutParams.WRAP_CONTENT);
        con_linear2.setLayoutParams(lin2_params);
        con_linear2.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams tv_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        con_text.setLayoutParams(tv_params);
        con_text.setPadding(DensityUtil.dp2px(MessageDetailActivity.this,10f),DensityUtil.dp2px(MessageDetailActivity.this,10f),DensityUtil.dp2px(MessageDetailActivity.this,10f),DensityUtil.dp2px(MessageDetailActivity.this,10f));
        con_text.setBackgroundResource(R.drawable.yuanjiao2);
        con_text.setText(info);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,0);
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        con_linear.setLayoutParams(layoutParams);
        con_linear.setGravity(Gravity.RIGHT);
        con_linear2.addView(con_text);
        con_linear2.addView(con_img);
        con_rea.addView(con_linear2);
        con_linear.addView(con_rea);
        lay.addView(con_linear);
    }
    private void addmes_left(String info){
        con_linear = new LinearLayout(MessageDetailActivity.this);
        con_rea = new RelativeLayout(MessageDetailActivity.this);
        con_linear2 = new LinearLayout(MessageDetailActivity.this);
        con_text = new TextView(MessageDetailActivity.this);
        con_img = new ImageView(MessageDetailActivity.this);
        con_img.setImageResource(R.drawable.tx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dp2px(MessageDetailActivity.this,40),DensityUtil.dp2px(MessageDetailActivity.this,40));
        params.setMargins(0,0,DensityUtil.dp2px(MessageDetailActivity.this,10f),0);
        con_img.setLayoutParams(params);



        RelativeLayout.LayoutParams rea_params = new RelativeLayout.LayoutParams(DensityUtil.dp2px(MessageDetailActivity.this,300),RelativeLayout.LayoutParams.WRAP_CONTENT);
        rea_params.setMargins(0,DensityUtil.dp2px(MessageDetailActivity.this,20f),0,0);
        con_rea.setLayoutParams(rea_params);
        con_rea.setGravity(Gravity.LEFT);

        LinearLayout.LayoutParams lin2_params = new LinearLayout.LayoutParams(DensityUtil.dp2px(MessageDetailActivity.this,240),LinearLayout.LayoutParams.WRAP_CONTENT);
        con_linear2.setLayoutParams(lin2_params);
        con_linear2.setGravity(Gravity.LEFT);

        LinearLayout.LayoutParams tv_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        con_text.setLayoutParams(tv_params);
        con_text.setPadding(DensityUtil.dp2px(MessageDetailActivity.this,10f),DensityUtil.dp2px(MessageDetailActivity.this,10f),DensityUtil.dp2px(MessageDetailActivity.this,10f),DensityUtil.dp2px(MessageDetailActivity.this,10f));
        con_text.setBackgroundResource(R.drawable.yuanjiao2);
        con_text.setText(info);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,0);
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        con_linear.setLayoutParams(layoutParams);
        con_linear.setGravity(Gravity.LEFT);
        con_linear2.addView(con_img);
        con_linear2.addView(con_text);
        con_rea.addView(con_linear2);
        con_linear.addView(con_rea);
        lay.addView(con_linear);
    }
    private void loadmes(){
        final OkHttpClient client = new OkHttpClient();
        String ip = getResources().getString(R.string.service_address);
        final Request request = new Request.Builder().url("http://"+ip+"/message/getMessageByUid?uid="+uid).build();
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
                        //String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(!jsonObject.getString("code").equals("100")){
                            //
                            mHandler.obtainMessage(2, null).sendToTarget();
                        }else{
                            JSONArray jsonArray = jsonObject.getJSONObject("extend").getJSONArray("message");
                            //
                            mHandler.obtainMessage(2, jsonArray).sendToTarget();
                        }

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

    private void sendMessage(String info) {
        final OkHttpClient client = new OkHttpClient();
        String ip = getResources().getString(R.string.service_address);
        final Request request = new Request.Builder().url("http://"+ip+"/message/sendMessage?uid="+uid+"&content="+info).build();
        //新建一个线程，用于得到服务器响应的参数
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        mHandler.obtainMessage(4, jsonObject).sendToTarget();
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

    private void readMessage() {
        final OkHttpClient client = new OkHttpClient();
        String ip = getResources().getString(R.string.service_address);
        final Request request = new Request.Builder().url("http://"+ip+"/message/readMessageForUser?uid="+uid).build();
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
                        //String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(!jsonObject.getString("code").equals("100")){
                            //更新消息阅读状态失败
                            mHandler.obtainMessage(3, "0").sendToTarget();
                        }

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
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }
}
