package com.example.myapplication;

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

public class ChangePasswordActivity extends AppCompatActivity {
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
                        editor.putString("upassword",et_newPassword.getText().toString());
                        editor.commit();
                        finish();
                    }
                    Toast.makeText(ChangePasswordActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 2){
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    Toast.makeText(ChangePasswordActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 3){
                    String info = (String) msg.obj;
                    Toast.makeText(ChangePasswordActivity.this,info,Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private TextView tv_number;
    private EditText et_captcha;
    private EditText et_newPassword;
    private Button btn_saveNewPassword;
    private LinearLayout getCaptcha;
    private ImageView iv_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        tv_number = findViewById(R.id.number);
        et_newPassword = findViewById(R.id.newPassword);
        et_captcha = findViewById((R.id.captcha));
        btn_saveNewPassword = findViewById(R.id.saveNewPassword);
        getCaptcha = findViewById(R.id.getCaptcha);
        iv_return = findViewById(R.id.toReturn);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SharedPreferences userSettings= ChangePasswordActivity.this.getSharedPreferences("setting", 0);
        tv_number.setText(userSettings.getString("ucontact",""));

        getCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = getResources().getString(R.string.service_address);
                final OkHttpClient client = new OkHttpClient();
                //判断手机号格式
                if(tv_number.getText().toString().equals("")){
                    Toast.makeText(ChangePasswordActivity.this,"登录异常，请重新登录",Toast.LENGTH_SHORT).show();
                    finish();
                }
                final Request request = new Request.Builder().url("http://"+ip+"/user/sendCaptcha?number="+tv_number.getText()).build();
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
        btn_saveNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_captcha.getText().toString().equals("")){
                    Toast.makeText(ChangePasswordActivity.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                } else if(et_newPassword.getText().toString().equals("")||!judgePassword(et_newPassword.getText().toString())){
                    Toast.makeText(ChangePasswordActivity.this,"密码格式错误，需包含数字和英文字母，且长度大于8位",Toast.LENGTH_SHORT).show();
                } else{
                    String ip = getResources().getString(R.string.service_address);
                    final OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder().url("http://"+ip+"/user/checkCaptchaWithChangePassword?number="+tv_number.getText()+"&captcha="+et_captcha.getText()).build();
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
                                        savePassword();
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
                }
            }

        });
    }

    private void savePassword() {
        SharedPreferences userSettings= ChangePasswordActivity.this.getSharedPreferences("setting", 0);
        String id = userSettings.getString("uid","");
        if(id.equals("")||id==null){
            Toast.makeText(ChangePasswordActivity.this,"账号未登录，请重新登录",Toast.LENGTH_SHORT).show();
            finish();
        }
        String ip = getResources().getString(R.string.service_address);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://"+ip+"/user/updateUser?id="+id+"&password="+et_newPassword.getText()).build();
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
    private boolean judgePassword(String password) {
        String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(.{8,20})$";
        return Pattern.compile(REGEX_PASSWORD).matcher(password).matches();
    }
}
