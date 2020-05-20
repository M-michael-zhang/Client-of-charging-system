package com.example.myapplication.fragements;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MyInfo;
import com.example.myapplication.R;
import com.yzq.zxinglibrary.android.CaptureActivity;


public class Fragement2 extends Fragment {
    private TextView tv;
    final static int REQUEST_CODE_SCAN =1;
    final static int PHOTO_REQUEST_SAOYISAO =0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View test1 = inflater.inflate(R.layout.test2, container, false);
        ImageView img = test1.findViewById(R.id.btn);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 取得相机权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PHOTO_REQUEST_SAOYISAO);
                } else {
                    // 权限已经取得的情况下调用
                    // 调用扫一扫
                    Intent intent = new Intent(getActivity(),
                            CaptureActivity.class);

                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }
            }
        });
        return test1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PHOTO_REQUEST_SAOYISAO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限申请成功，扫一扫
                    Intent intent = new Intent(getActivity(),
                            CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                } else {
                    Toast.makeText(getActivity(),"无相机调用权限，扫一扫功能无法使用，",Toast.LENGTH_SHORT).show();
                }
        }

    }


}