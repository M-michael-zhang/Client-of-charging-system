package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ChargeManage extends AppCompatActivity {
    private ImageView iv_return;
    private Button applyFix1;
    private Button applyFix2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_manage);
        iv_return = findViewById(R.id.toReturn);
        applyFix1 = findViewById(R.id.applyFix1);
        applyFix2 = findViewById(R.id.applyFix2);
        applyFix1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChargeManage.this,"已发送申请，请保持电话畅通",Toast.LENGTH_SHORT).show();
            }
        });
        applyFix2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChargeManage.this,"已发送申请，请保持电话畅通",Toast.LENGTH_SHORT).show();
            }
        });
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
