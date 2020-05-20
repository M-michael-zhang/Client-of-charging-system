package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.fragements.Fragement1;
import com.example.myapplication.fragements.Fragement2;
import com.example.myapplication.fragements.Fragement4;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_LOCATION = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限
    private Fragement1 fragement1;
    private Fragement2 fragement2;
    private Fragement4 fragement4;
    private View view1;
    private View view2;
    private View view4;
    private ImageView img1;
    private ImageView img2;
    private ImageView img4;
    private TextView text1;
    private TextView text2;
    private TextView text4;
    private TextView title;
    private FragmentManager fragmentManager;
    private ImageView toMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);

            }
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initViews();
        toMessage = findViewById(R.id.toMessage);
        toMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MessageDetailActivity.class);
                startActivity(intent);
            }
        });
        fragmentManager = getFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(1);
    }


    private void initViews() {
        view1 = findViewById(R.id.message_layout);
        view2 = findViewById(R.id.contacts_layout);
        view4 = findViewById(R.id.setting_layout);
        img1 = findViewById(R.id.icon_map);
        img2 = findViewById(R.id.icon_charge);
        img4 = findViewById(R.id.icon_my);
        text1 = findViewById(R.id.map_text);
        text2 = findViewById(R.id.charge_text);
        text4 = findViewById(R.id.my_text);
        view1.setOnClickListener(this);
        view2.setOnClickListener(this);
        view4.setOnClickListener(this);
        title = findViewById(R.id.title);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_layout:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(1);
                break;
            case R.id.contacts_layout:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(2);
                break;
            case R.id.setting_layout:
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(4);
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted，do sth；
                } else {
                    // permission denied,
                    Toast.makeText(MainActivity.this, "请手动开启定位权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 1:
                img1.setColorFilter(Color.parseColor("#FFF6a07b"));
                text1.setTextColor(Color.parseColor("#FFF6a07b"));
                title.setText("地图");
                if (fragement1 == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fragement1 = new Fragement1();
                    transaction.add(R.id.content, fragement1);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragement1);
                }
                break;
            case 2:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                img2.setColorFilter(Color.parseColor("#FFF6a07b"));
                text2.setTextColor(Color.parseColor("#FFF6a07b"));
                title.setText("充电");
                if (fragement2 == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    fragement2 = new Fragement2();
                    transaction.add(R.id.content, fragement2);
                } else {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    transaction.show(fragement2);
                }
                break;
            case 4:
            default:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                img4.setColorFilter(Color.parseColor("#FFF6a07b"));
                text4.setTextColor(Color.parseColor("#FFF6a07b"));
                title.setText("我的");
                if (fragement4 == null) {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    fragement4 = new Fragement4();
                    transaction.add(R.id.content, fragement4);
                } else {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    transaction.show(fragement4);
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fragement1 != null) {
            transaction.hide(fragement1);
        }
        if (fragement2 != null) {
            transaction.hide(fragement2);
        }
        if (fragement4 != null) {
            transaction.hide(fragement4);
        }
    }
    private void clearSelection() {
        img1.setColorFilter(Color.parseColor("#FFFFFFFF"));
        text1.setTextColor(Color.parseColor("#FFFFFFFF"));
        img2.setColorFilter(Color.parseColor("#FFFFFFFF"));
        text2.setTextColor(Color.parseColor("#FFFFFFFF"));
        img4.setColorFilter(Color.parseColor("#FFFFFFFF"));
        text4.setTextColor(Color.parseColor("#FFFFFFFF"));
//        view1.setBackgroundColor(0xffffffff);
//        view2.setBackgroundColor(0xffffffff);
//        view3.setBackgroundColor(0xffffffff);
//        view4.setBackgroundColor(0xffffffff);
    }
}

