package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ApplyActivity extends AppCompatActivity {
    private ImageView toreturn;
    private Button btn_apply;
    private CheckBox box1;
    private CheckBox box2;
    private CheckBox box3;
    private EditText app_name;
    private EditText app_add;
    private EditText app_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        toreturn = findViewById(R.id.toReturn);
        btn_apply = findViewById(R.id.btn_apply);
        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        box3 = findViewById(R.id.box3);
        app_add = findViewById(R.id.applt_add);
        app_name = findViewById(R.id.apply_name);
        app_phone = findViewById(R.id.apply_phone);

        toreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(app_phone.getText()) ||TextUtils.isEmpty(app_name.getText())||TextUtils.isEmpty(app_add.getText())){
                    Toast.makeText(ApplyActivity.this,"请填写完整信息",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(ApplyActivity.this,InfoActivity.class);
                    startActivity(intent);
                }

            }
        });
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.equals(box1)){
                    if (isChecked){
                        box2.setChecked(false);
                        box3.setChecked(false);
                    }
                }
                if(buttonView.equals(box2)){
                    if (isChecked){
                        box1.setChecked(false);
                        box3.setChecked(false);
                    }
                }
                if(buttonView.equals(box3)){
                    if (isChecked){
                        box2.setChecked(false);
                        box1.setChecked(false);
                    }
                }
            }
        };

        box1.setOnCheckedChangeListener(listener);
        box2.setOnCheckedChangeListener(listener);
        box3.setOnCheckedChangeListener(listener);
    }
}
