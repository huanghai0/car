package com.example.car.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.car.R;
import com.example.car.common.HttpConnectionService;

import org.json.JSONException;
import org.json.JSONObject;

public class VipActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Button mBtnOpenVip;
    private Button mBtnBigVip;

    private final int OPENVIP = 1;
    private final int BIGVIP = 2;

    private Handler handler;
    private HandlerThread handlerThread;

    private String vipurl = "http://192.168.42.106:8080/api/user/";

    private HttpConnectionService httpConnectionService = new HttpConnectionService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("会员服务");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handlerThread = new HandlerThread("http_vip");
        handlerThread.start();
        handler = new HttpHandler(handlerThread.getLooper());

        mBtnOpenVip = findViewById(R.id.btn_openvip);
        mBtnBigVip = findViewById(R.id.btn_bigvip);


        mBtnOpenVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(OPENVIP);
            }
        });
        mBtnBigVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(BIGVIP);
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void userPut(int vid) {
        SharedPreferences share = getSharedPreferences("Login",
                Context.MODE_PRIVATE);
        String userStr = httpConnectionService.HttpGetConection(vipurl + share.getString("Account", ""));
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(userStr);
            jsonObject.remove("vid");
            jsonObject.put("vid", vid);
            System.out.println(jsonObject.toString() + "^^^^^^^^^^^^^^^");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpConnectionService.HttpPutConection(vipurl, jsonObject.toString());
    }

    private class HttpHandler extends Handler {

        public HttpHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case OPENVIP:
                    userPut(1);
                    break;
                case BIGVIP:
                    userPut(2);
                    break;
            }
        }
    }
}
