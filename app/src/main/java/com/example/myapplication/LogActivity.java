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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LogActivity extends AppCompatActivity {
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
                        editor.putString("ulicense",extend.getString("license"));
                        editor.putString("uamount", String.valueOf(extend.getDouble("amount")));
                        editor.commit();
                        finish();
                    }
                    Log.e("aaaa",jsonObject.getString("msg"));
                    Toast.makeText(LogActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private Button btn;
    private EditText uname_edit;
    private EditText pwd_edit;
    private ImageView iv_return;
    private TextView tx_return;
    private TextView toRegister;
    private TextView toForget;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        iv_return = findViewById(R.id.toReturn);
        tx_return = findViewById(R.id.toReturn_text);

        tx_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toForget = findViewById(R.id.toForget);
        btn = findViewById(R.id.login);
        uname_edit = findViewById(R.id.uname);
        toRegister = findViewById(R.id.toRegister);
        pwd_edit = findViewById(R.id.pwd);
        uname_edit.setHint(name);
        pwd_edit.setHint("***********");
        SharedPreferences userSettings= LogActivity.this.getSharedPreferences("setting", 0);
        name = userSettings.getString("uname","请点击头像登录");
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogActivity.this,Register.class);
                startActivity(intent);
            }
        });
        toForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LogActivity.this,"内测版本暂不支持修改密码",Toast.LENGTH_SHORT).show();
            }
        });
        uname_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.hasFocus()){
                    uname_edit.setHint("");
                }
                else uname_edit.setHint(name);

            }
        });
        pwd_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.hasFocus()){
                    pwd_edit.setHint("");
                }
                else pwd_edit.setHint("**********");

            }
        });
//        uname_edit.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                uname_edit.setSelection(uname_edit.getText().length());
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                uname_edit.setSelection(uname_edit.getText().length());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                uname_edit.setSelection(uname_edit.getText().length());
//            }
//        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip = getResources().getString(R.string.service_address);
                final OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder().url("http://"+ip+"/user/login?number="+uname_edit.getText()+"&password="+pwd_edit.getText()).build();
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
