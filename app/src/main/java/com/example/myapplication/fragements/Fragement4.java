package com.example.myapplication.fragements;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.CaptcheLoginActivity;
import com.example.myapplication.RedBagActivity;
import com.example.myapplication.ChangeInfo;
import com.example.myapplication.ChargeList;
import com.example.myapplication.ChargeManage;
import com.example.myapplication.LogActivity;
import com.example.myapplication.ManageActivity;
import com.example.myapplication.MessageDetailActivity;
import com.example.myapplication.R;


public class Fragement4 extends Fragment {
    private TextView tv;
    private ImageView uimg;
    private TextView uname_tv;
    private RelativeLayout a;
    private ImageView toManage;
    private RelativeLayout toMessageDetail;
    private RelativeLayout toInvite;
    private Button toChange;
    private Button toTrue;
    private LinearLayout toRedBag;
    String name;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View test1 = inflater.inflate(R.layout.test4, container, false);
        uname_tv = test1.findViewById(R.id.uname);
        toManage = test1.findViewById(R.id.toManage);
        toMessageDetail = test1.findViewById(R.id.toMessageDetail);
        toInvite = test1.findViewById(R.id.toInvite);
        toChange = test1.findViewById(R.id.toChange);
        uimg = test1.findViewById(R.id.uimg);
        toTrue = test1.findViewById(R.id.toTrue);
        toRedBag = test1.findViewById(R.id.toRedBag);
        SharedPreferences userSettings= getActivity().getSharedPreferences("setting", 0);
        name = userSettings.getString("uname","请点击头像登录");
        if(!name.equals("请点击头像登录")){
            uimg.setImageResource(R.drawable.tx_activity);
        }
        uname_tv.setText(name);

        a = test1.findViewById(R.id.toMyInfo);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChargeList.class);
                startActivity(intent);
            }
        });
        uimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        CaptcheLoginActivity.class);
                startActivity(intent);
            }
        });
        toManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageActivity.class);
                startActivity(intent);
            }
        });
        toRedBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RedBagActivity.class);
                startActivity(intent);
            }
        });
        toMessageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
                startActivity(intent);
            }
        });
        toInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"该功能还在开发中",Toast.LENGTH_SHORT).show();
            }
        });
        toTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"该功能还在开发中",Toast.LENGTH_SHORT).show();
            }
        });
        toChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.equals("请点击头像登录")){
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), ChangeInfo.class);
                    startActivity(intent);
                }

            }
        });
        return test1;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences userSettings= getActivity().getSharedPreferences("setting", 0);
        name = userSettings.getString("uname","请点击头像登录");
        uname_tv.setText(name);
        if(!name.equals("请点击头像登录")){
            uimg.setImageResource(R.drawable.tx_activity);
        }
    }
}
