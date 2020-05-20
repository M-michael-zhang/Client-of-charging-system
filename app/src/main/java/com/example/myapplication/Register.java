package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Register extends AppCompatActivity {
    private EditText uname;
    private EditText pwd;
    private EditText pwd2;
    private Button register;
    String s_name;
    String s_pwd;
    String s_pwd2;
    private ImageView iv_return;
    private TextView tx_return;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String log_state = (String) msg.obj;
                if(log_state.equals("该账号已被注册")){
                    Toast.makeText(Register.this,"该用户名已被注册，请重新注册",Toast.LENGTH_SHORT).show();
                }
                if(log_state.equals("成功注册")){
                    Toast.makeText(Register.this,"注册成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        uname = findViewById(R.id.uname);
        pwd = findViewById(R.id.pwd);
        pwd2 = findViewById(R.id.pwd2);
        register = findViewById(R.id.register);
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
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_name = uname.getText().toString();
                s_pwd  = pwd.getText().toString();
                s_pwd2 = pwd2.getText().toString();
                if(s_name.equals("")||s_pwd.equals("")||s_pwd2.equals("")){
                    Toast.makeText(Register.this,"内容不能为空！",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(s_pwd2.equals(s_pwd)){
                        toRegisterApi(s_name,s_pwd);
                    }
                    else{
                        Toast.makeText(Register.this,"两次密码不同",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
    private void toRegisterApi(String myname,String mypwd){
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://123.207.233.171:8080/user/register?uname="+myname+"&pwd="+mypwd).build();
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
