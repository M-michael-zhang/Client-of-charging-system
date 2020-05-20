package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.util.DensityUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChargeList extends AppCompatActivity {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try{
                if (msg.what == 1) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")){
                        JSONArray transList = jsonObject.getJSONObject("extend").getJSONArray("Trans");
                        for(int i=0;i<transList.length();i++){
                            addTrans(transList.getJSONObject(i));
                        }
                        for(int j=0;j<buttonList.size();j++){
                            final int finalJ = j;
                            buttonList.get(j).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    currentTrans = finalJ;
                                    finishTrans(serialList.get(finalJ));
                                }
                            });
                        }
                    }else{
                        Toast.makeText(ChargeList.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                }
                if(msg.what == 2){
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")){
                        JSONObject trans = jsonObject.getJSONObject("extend").getJSONObject("Trans");
                        buttonList.get(currentTrans).setVisibility(View.GONE);
                        tvStatusList.get(currentTrans).setText("已完成");
                        tvTimeList.get(currentTrans).setText(tvTimeList.get(currentTrans).getText().toString()+trans.getString("endTime"));
                        if(trans.getString("type").equals("1")){
                            tvAmountList.get(currentTrans).setText("金额:"+trans.getString("amount"));
                            tvAmountList.get(currentTrans).setVisibility(View.VISIBLE);
                        }

                    }
                    Toast.makeText(ChargeList.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    };
    private ImageView iv_return;

    private LinearLayout transList;
    private List<Button> buttonList;
    private List<String> serialList;
    private List<TextView> tvStatusList;
    private List<TextView> tvTimeList;
    private List<TextView> tvAmountList;
    private LinearLayout linearLayout_outside;
    private LinearLayout linearLayout_left;
    private LinearLayout linearLayout_right;
    private TextView tv_type;
    private TextView tv_address;
    private TextView tv_serial;
    private TextView tv_time;
    private TextView tv_amount;
    private TextView tv_status;
    private Button btn_finish;
    private View view;
    private int currentTrans=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_list);
        transList = findViewById(R.id.transList);
        iv_return = findViewById(R.id.toReturn);
        buttonList = new ArrayList<Button>();
        serialList = new ArrayList<String>();
        tvStatusList = new ArrayList<TextView>();
        tvTimeList = new ArrayList<TextView>();
        tvAmountList = new ArrayList<TextView>();
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadTrans();

    }

    private void loadTrans(){
        final OkHttpClient client = new OkHttpClient();
        String ip = getResources().getString(R.string.service_address);
        SharedPreferences userSettings= ChargeList.this.getSharedPreferences("setting", 0);
        String uid = userSettings.getString("uid","");
        if(uid.equals("")){
            Toast.makeText(ChargeList.this,"您还未登录，请先登录",Toast.LENGTH_SHORT).show();
            finish();
        }
        final Request request = new Request.Builder().url("http://"+ip+"/trans/getTransByUidWithoutPage?uid="+uid).build();
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
    private void finishTrans(String serialNo) {
        final OkHttpClient client = new OkHttpClient();
        String ip = getResources().getString(R.string.service_address);
        final Request request = new Request.Builder().url("http://"+ip+"/pile/endTrans?serial="+serialNo).build();
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
    private void addTrans(JSONObject trans) throws JSONException {
        String title = "";
        if(trans.getString("type").equals("0")){
            title = "预约";
        }else if(trans.getString("type").equals("1")){
            title = "充电";
        }
        String status = trans.getString("status");
        String type = trans.getString("type");
        linearLayout_outside = new LinearLayout(ChargeList.this);
        linearLayout_left = new LinearLayout(ChargeList.this);
        linearLayout_right = new LinearLayout(ChargeList.this);
        tv_type = new TextView(ChargeList.this);
        tv_address = new TextView(ChargeList.this);
        tv_serial = new TextView(ChargeList.this);
        tv_time = new TextView(ChargeList.this);
        tv_amount = new TextView(ChargeList.this);
        tv_amount.setVisibility(View.GONE);
        tv_status = new TextView(ChargeList.this);
        btn_finish = new Button(ChargeList.this);
        btn_finish.setVisibility(View.GONE);
        view = new View(ChargeList.this);
        LinearLayout.LayoutParams linearLayout_outside_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,DensityUtil.dp2px(ChargeList.this,130));
        linearLayout_outside_params.setMargins(DensityUtil.dp2px(ChargeList.this,7f),DensityUtil.dp2px(ChargeList.this,10f),DensityUtil.dp2px(ChargeList.this,7f),0);
        linearLayout_outside.setBackgroundColor(Color.WHITE);
        linearLayout_outside.setWeightSum(4);
        linearLayout_outside.setLayoutParams(linearLayout_outside_params);

        LinearLayout.LayoutParams linearLayout_left_params = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,3);
        linearLayout_left.setPadding(DensityUtil.dp2px(ChargeList.this,15f),0,0,0);
        linearLayout_left.setLayoutParams(linearLayout_left_params);
        linearLayout_left.setOrientation(LinearLayout.VERTICAL);
        linearLayout_left.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams linearLayout_right_params = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1);
        linearLayout_right.setLayoutParams(linearLayout_right_params);
        linearLayout_right.setGravity(Gravity.CENTER);
        linearLayout_right.setOrientation(LinearLayout.VERTICAL);
        //linearLayout_right.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tv_type.setText(title+"订单");
        tv_address.setText(trans.getString("reserve2"));
        tv_serial.setText("订单号:"+trans.getString("serialNo"));
        if(status.equals("0")){
            tv_time.setText("时间:"+trans.getString("transTime")+"~");
            //正在进行的交易 可以结束
            btn_finish.setVisibility(View.VISIBLE);
        }else if(status.equals("1")){
            tv_time.setText("时间:"+trans.getString("transTime")+"~"+trans.getString("endTime"));


        }
        if(status.equals("1")&&type.equals("1")&&!trans.getString("amount").equals("null")){
            tv_amount.setText("金额:"+trans.getString("amount"));
            tv_amount.setVisibility(View.VISIBLE);
        }
        String temp = status.equals("0")?"正在进行":status.equals("1")?"已完成":"";
        tv_status.setText(temp);

        LinearLayout.LayoutParams tv_left_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams tv_right_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        tv_type.setTextSize(DensityUtil.dp2px(ChargeList.this,8));
        tv_type.setTextColor(getResources().getColor(R.color.colorPrimary));
        tv_type.setLayoutParams(tv_left_params);

        tv_address.setTextSize(DensityUtil.dp2px(ChargeList.this,5));
        tv_address.setTextColor(getResources().getColor(R.color.contents_text));
        tv_address.setLayoutParams(tv_left_params);
        tv_serial.setLayoutParams(tv_left_params);
        tv_time.setLayoutParams(tv_left_params);
        tv_time.setTextSize(DensityUtil.dp2px(ChargeList.this,4));
        tv_amount.setLayoutParams(tv_left_params);

        tv_status.setLayoutParams(tv_right_params);
        tv_status.setTextSize(DensityUtil.dp2px(ChargeList.this,7));
        tv_status.setTextColor(getResources().getColor(R.color.red_text));


        LinearLayout.LayoutParams view_params = new LinearLayout.LayoutParams(DensityUtil.dp2px(ChargeList.this,2), LinearLayout.LayoutParams.MATCH_PARENT);
        view_params.setMargins(0,DensityUtil.dp2px(ChargeList.this,20f),0,DensityUtil.dp2px(ChargeList.this,20f));
        view.setLayoutParams(view_params);
        view.setBackgroundColor(getResources().getColor(R.color.background_huise));

        LinearLayout.LayoutParams btn_params = new LinearLayout.LayoutParams(DensityUtil.dp2px(ChargeList.this,70), DensityUtil.dp2px(ChargeList.this,40));
        btn_params.setMargins(0,DensityUtil.dp2px(ChargeList.this,10),0,0);
        btn_finish.setLayoutParams(btn_params);
        btn_finish.setText("结束"+title);
        btn_finish.setBackground(getResources().getDrawable(R.drawable.yuanjiao));
        btn_finish.setTextColor(getResources().getColor(R.color.defaultColor));
        btn_finish.setTextSize(DensityUtil.dp2px(ChargeList.this,5));

        linearLayout_left.addView(tv_type);
        linearLayout_left.addView(tv_address);
        linearLayout_left.addView(tv_serial);
        linearLayout_left.addView(tv_time);
        linearLayout_left.addView(tv_amount);

        linearLayout_right.addView(tv_status);
        linearLayout_right.addView(btn_finish);

        linearLayout_outside.addView(linearLayout_left);
        linearLayout_outside.addView(view);
        linearLayout_outside.addView(linearLayout_right);

        transList.addView(linearLayout_outside);
        buttonList.add(btn_finish);
        serialList.add(trans.getString("serialNo"));
        tvStatusList.add(tv_status);
        tvTimeList.add(tv_time);
        tvAmountList.add(tv_amount);
    }
}
