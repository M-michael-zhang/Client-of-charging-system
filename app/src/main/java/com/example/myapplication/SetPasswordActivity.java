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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetPasswordActivity extends AppCompatActivity {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try{
                if (msg.what == 1) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    Toast.makeText(SetPasswordActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                    if(jsonObject.getString("code").equals("100")){
                        SharedPreferences userSettings = getSharedPreferences("setting", 0);
                        SharedPreferences.Editor editor = userSettings.edit();
                        if(userSettings.getString("forgetFlag","").equals("1")){
                            editor.putString("forgetFlag", "");
                        }
                        editor.putString("uname", et_name.getText().toString());
                        editor.putString("upassword",et_password.getText().toString());
                        editor.commit();
                        finish();
                    }
                    Log.e("serviceCallBack",jsonObject.toString());

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private EditText et_password;
    private EditText et_password_again;
    private EditText et_name;
    private Button btn_set_password;
    final String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(.{8,20})$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        et_password = findViewById(R.id.newPassword);
        et_password_again = findViewById(R.id.newPasswordAgain);
        et_name = findViewById(R.id.newName);
        btn_set_password = findViewById(R.id.setPassword);
        SharedPreferences userSettings = getSharedPreferences("setting", 0);
        et_name.setText(userSettings.getString("uname",""));
        btn_set_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_name.getText().toString().equals("")){
                    Toast.makeText(SetPasswordActivity.this,"请输入用户名",Toast.LENGTH_SHORT).show();
                }
                else if(!Pattern.compile(REGEX_PASSWORD).matcher(et_password.getText().toString()).matches()){
                    Toast.makeText(SetPasswordActivity.this,"密码格式错误，需包含数字和英文字母，且长度大于8位",Toast.LENGTH_SHORT).show();
                }else if(!et_password_again.getText().toString().equals(et_password.getText().toString())){
                    Toast.makeText(SetPasswordActivity.this,"两次密码不相同，请重新输入",Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences userSettings= SetPasswordActivity.this.getSharedPreferences("setting", 0);
                    String id = userSettings.getString("uid","");
                    if(id.equals("")||id==null){
                        Toast.makeText(SetPasswordActivity.this,"账号未登录，请重新登录",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    String ip = getResources().getString(R.string.service_address);
                    final OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder().url("http://"+ip+"/user/updateUser?reserve1=&id="+id+"&password="+et_password.getText()+"&name="+et_name.getText()).build();
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
        });

    }
}
