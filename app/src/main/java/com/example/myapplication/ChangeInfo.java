package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangeInfo extends AppCompatActivity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try{
                if (msg.what == 1) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")){
                        Log.e("serviceCallBack",jsonObject.toString());
                        SharedPreferences userSettings = getSharedPreferences("setting", 0);
                        SharedPreferences.Editor editor = userSettings.edit();
                        editor.putString("uname",et_name.getText().toString());
                        editor.putString("ucontact",et_number.getText().toString());
                        editor.putString("ulicense",et_license.getText().toString());
                        editor.commit();
                        finish();
                    }
                    Toast.makeText(ChangeInfo.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 2){
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    Toast.makeText(ChangeInfo.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 3){
                    String info = (String) msg.obj;
                    Toast.makeText(ChangeInfo.this,info,Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private EditText et_name;
    private EditText et_number;
    private EditText et_license;
    private EditText et_captcha;
    private LinearLayout getCaptcha;
    private LinearLayout linearCaptcha;
    private Button btn_chang_info;
    private ImageView iv_return;
    private TextView tv_toChangePassword;
    private boolean changNumberFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        et_name = findViewById(R.id.name);
        et_number = findViewById(R.id.number);
        et_license = findViewById(R.id.license);
        getCaptcha = findViewById(R.id.getCaptcha);
        et_captcha = findViewById(R.id.captcha);
        linearCaptcha = findViewById(R.id.linearCaptcha);
        btn_chang_info = findViewById(R.id.changInfoBtn);
        iv_return = findViewById(R.id.toReturn);
        tv_toChangePassword = findViewById(R.id.toChangePassword);
        final SharedPreferences userSettings = getSharedPreferences("setting", 0);
        et_name.setText(userSettings.getString("uname",""));
        et_number.setText(userSettings.getString("ucontact",""));
        et_license.setText(userSettings.getString("ulicense",""));
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(userSettings.getString("ucontact",""))){
                    getCaptcha.setVisibility(View.VISIBLE);
                    linearCaptcha.setVisibility(View.VISIBLE);
                    changNumberFlag = true;
                }else {
                    getCaptcha.setVisibility(View.GONE);
                    linearCaptcha.setVisibility(View.GONE);
                    changNumberFlag = false;
                }
            }
        });

        btn_chang_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(et_name.getText().toString().equals("")){
                    Toast.makeText(ChangeInfo.this,"请输入用户名",Toast.LENGTH_SHORT).show();
                }else if(et_number.getText().toString().equals("")){
                    Toast.makeText(ChangeInfo.this,"请输入手机号",Toast.LENGTH_SHORT).show();
                }else if(changNumberFlag&&et_captcha.getText().toString().equals("")){
                    Toast.makeText(ChangeInfo.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                }else if(!et_license.getText().toString().equals("")&&!judgeLicense(et_license.getText().toString())){
                    Toast.makeText(ChangeInfo.this,"请输入正确格式的车牌信息",Toast.LENGTH_SHORT).show();
                } else if(changNumberFlag){
                    String ip = getResources().getString(R.string.service_address);
                    final OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder().url("http://"+ip+"/user/checkCaptchaWithChangeNumber?number="+et_number.getText()+"&captcha="+et_captcha.getText()).build();
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
                                    if(jsonObject.getString("code").equals("100")){
                                        saveInfo();
                                    }else {
                                        mHandler.obtainMessage(3, jsonObject.getString("msg")).sendToTarget();
                                    }
                                } else {
                                    mHandler.obtainMessage(3, "校验验证码失败").sendToTarget();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    saveInfo();
                }
            }
        });

        getCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = getResources().getString(R.string.service_address);
                final OkHttpClient client = new OkHttpClient();
                //判断手机号格式
                if(!judgePhoneNumber(et_number.getText().toString())){
                    Toast.makeText(ChangeInfo.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                    return;
                }
                final Request request = new Request.Builder().url("http://"+ip+"/user/sendCaptcha?number="+et_number.getText()).build();
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
        });
        tv_toChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangeInfo.this,ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    private Boolean judgePhoneNumber(String phone){
        String REGEX_PHONENUMBER = "^1\\d{10}$";
        return Pattern.compile(REGEX_PHONENUMBER).matcher(phone).matches();
    }
    private Boolean judgeLicense(String phone){
        String REGEX_LICENSE = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]{1}[A-Z]{1}([A-Z0-9]{5,6}|[A-Z0-9]{4}[挂学警港澳]{1})$";
        return Pattern.compile(REGEX_LICENSE).matcher(phone).matches();
    }

    private void saveInfo(){
        SharedPreferences userSettings= ChangeInfo.this.getSharedPreferences("setting", 0);
        String id = userSettings.getString("uid","");
        if(id.equals("")||id==null){
            Toast.makeText(ChangeInfo.this,"账号未登录，请重新登录",Toast.LENGTH_SHORT).show();
            finish();
        }
        String ip = getResources().getString(R.string.service_address);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://"+ip+"/user/updateUser?id="+id+"&contact="+et_number.getText()+"&name="+et_name.getText()+"&license="+et_license.getText()).build();
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
}
