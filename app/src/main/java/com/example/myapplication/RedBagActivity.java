package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RedBagActivity extends AppCompatActivity {
    private ImageView iv_return;
    private TextView toAddAddress;
    private Button btn_toNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_bag);
        toAddAddress = findViewById(R.id.toAddAddress);
        btn_toNavigation = findViewById(R.id.toNavigation);
        iv_return = findViewById(R.id.toReturn);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedBagActivity.this,AddAddressActivity.class);
                startActivity(intent);
            }
        });
        btn_toNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RedBagActivity.this, RouteActivity.class);
                SharedPreferences userSettings = RedBagActivity.this.getSharedPreferences("setting", 0);
                String mCurrentLat = userSettings.getString("Latitude","");
                String mCurrentLon = userSettings.getString("Longitude","");
                intent.putExtra("start_pt_lat",Double.parseDouble(mCurrentLat));
                intent.putExtra("start_pt_lon",Double.parseDouble(mCurrentLon));
                intent.putExtra("end_pt_lat",29.132788);
                intent.putExtra("end_pt_lon",119.651841);
                Log.e("ServiceCallBack",mCurrentLat+"ssss"+mCurrentLon);
                startActivity(intent);
            }
        });
    }
}
