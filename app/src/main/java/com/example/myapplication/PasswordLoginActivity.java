package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PasswordLoginActivity extends AppCompatActivity {
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
                        editor.commit();
                        finish();
                    }
                    Log.e("ServiceCallBack",jsonObject.getString("msg"));
                    Toast.makeText(PasswordLoginActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private Button btn;
    private EditText number_edit;
    private EditText pwd_edit;
    private ImageView iv_return;
    private LinearLayout toForget;
    private TextView tv_toCaptchaLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_login);
        iv_return = findViewById(R.id.toReturn);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toForget = findViewById(R.id.forgetPwd);
        btn = findViewById(R.id.passwordLoginBtn);
        number_edit = findViewById(R.id.number);
        pwd_edit = findViewById(R.id.password);
        tv_toCaptchaLogin = findViewById(R.id.toCaptchaLogin);
        toForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userSettings = getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = userSettings.edit();
                editor.putString("forgetFlag", "1");
                editor.commit();
                Intent intent = new Intent(PasswordLoginActivity.this, CaptcheLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tv_toCaptchaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordLoginActivity.this, CaptcheLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip = getResources().getString(R.string.service_address);
                final OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder().url("http://"+ip+"/user/login?number="+number_edit.getText()+"&password="+pwd_edit.getText()).build();
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
    }
}
