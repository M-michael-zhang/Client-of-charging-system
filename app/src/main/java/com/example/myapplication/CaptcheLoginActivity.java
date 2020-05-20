package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class CaptcheLoginActivity extends AppCompatActivity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try{
                if (msg.what == 1) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if(jsonObject.getString("code").equals("100")){
                        JSONObject extend = jsonObject.getJSONObject("extend").getJSONObject("user");
                        SharedPreferences userSettings = getSharedPreferences("setting", 0);
                        SharedPreferences.Editor editor = userSettings.edit();
                        editor.putString("uname", extend.getString("name"));
                        editor.putString("upassword",extend.getString("password"));
                        editor.putString("uid",extend.getString("id"));
                        editor.putString("ucontact",extend.getString("contact"));
                        if(extend.getString("license").equals("null")){
                            editor.putString("ulicense","");
                        }else{
                            editor.putString("ulicense",extend.getString("license"));
                        }

                        editor.putString("uamount", String.valueOf(extend.getDouble("amount")));
                        String forgetFlag = userSettings.getString("forgetFlag","");
                        editor.commit();
                        Log.e("serviceCallBack",jsonObject.toString());
                        if(extend.getString("reserve1").equals("1")||forgetFlag.equals("1")){
                            Intent intent = new Intent(CaptcheLoginActivity.this, SetPasswordActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }

                    Toast.makeText(CaptcheLoginActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 2){
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    Toast.makeText(CaptcheLoginActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private Button btn;
    private EditText number_edit;
    private EditText captcha_edit;
    private ImageView iv_return;
    private TextView tv_toPwdLogin;
    private LinearLayout getCaptche;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha_login);
        iv_return = findViewById(R.id.toReturn);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userSettings = getSharedPreferences("setting", 0);
                if(userSettings.getString("forgetFlag","").equals("1")){
                    SharedPreferences.Editor editor = userSettings.edit();
                    editor.putString("forgetFlag", "");
                    editor.commit();
                }
                finish();
            }
        });
        btn = findViewById(R.id.CaptcheLoginBtn);
        number_edit = findViewById(R.id.number);
        captcha_edit = findViewById(R.id.captcha);
        tv_toPwdLogin = findViewById(R.id.toPwdLogin);
        getCaptche = findViewById(R.id.getCaptche);

//        uname_edit.setHint(name);
//        pwd_edit.setHint("***********");

//        number_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(v.hasFocus()){
//                    uname_edit.setHint("");
//                }
//                else uname_edit.setHint(name);
//
//            }
//        });
//        pwd_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(v.hasFocus()){
//                    pwd_edit.setHint("");
//                }
//                else pwd_edit.setHint("**********");
//
//            }
//        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip = getResources().getString(R.string.service_address);
                final OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder().url("http://"+ip+"/user/loginByCaptcha?number="+number_edit.getText()+"&captcha="+captcha_edit.getText()).build();
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
        });

        tv_toPwdLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaptcheLoginActivity.this, PasswordLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        getCaptche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = getResources().getString(R.string.service_address);
                final OkHttpClient client = new OkHttpClient();
                //判断手机号格式
                if(!judgePhoneNumber(number_edit.getText().toString())){
                    Toast.makeText(CaptcheLoginActivity.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                    return;
                }
                final Request request = new Request.Builder().url("http://"+ip+"/user/sendCaptcha?number="+number_edit.getText()).build();
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

    }
    private Boolean judgePhoneNumber(String phone){
        String REGEX_PHONENUMBER = "^1\\d{10}$";
        return Pattern.compile(REGEX_PHONENUMBER).matcher(phone).matches();
    }
}
