package com.example.car;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.car.activity.RegisteredActivity;
import com.example.car.activity.UiActivity;
import com.example.car.common.HttpConnectionService;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button mBtnLogin;
    private Button mBtnRegistered;
    private EditText mEtId;
    private EditText mEtPwd;

    // 用于记录帐号和密码
    private String strAccount = "";
    private String strPassword = "";

    private HttpConnectionService httpConnectionService = new HttpConnectionService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 获取sharedpreferences对象
        SharedPreferences share = getSharedPreferences("Login",
                Context.MODE_PRIVATE);
        strAccount = share.getString("Account", "");
        strPassword = share.getString("Password", "");

        if (share == null) {
            init();
        } else {
            // 判断是否刚注销
            if (share.getBoolean("LoginBool", false)) {
                // 跳转到注销页面并销毁当前activity
                Intent intent = new Intent(MainActivity.this,
                        UiActivity.class);
                startActivity(intent);
                finish();
            } else {
                init();
            }
        }


    }

    private void init(){
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegistered = findViewById(R.id.btn_registered);
        mEtId = findViewById(R.id.et_userid);
        mEtPwd = findViewById(R.id.et_password);

        mBtnRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisteredActivity.class);
                startActivity(intent);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mEtId.getText().toString();
                String pwd = mEtPwd.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (phone.equals("") || pwd.equals("")) {
                            new Thread() {
                                public void run() {
                                    Log.i("log", "run");
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this, "账户和密码不能为空！", Toast.LENGTH_LONG).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                }
                            }.start();
                        } else {
                            String url = "http://192.168.42.106:8080//api/user/" + phone;
                            String res = httpConnectionService.HttpGetConection(url);
                            System.out.println("--------" + res);
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                if (phone.equals(jsonObject.getString("phone")) && pwd.equals(jsonObject.getString("pwd"))) {

                                    SharedPreferences share = getSharedPreferences("Login",
                                            Context.MODE_PRIVATE);
                                    // 获取编辑器来存储数据到sharedpreferences中
                                    SharedPreferences.Editor editor = share.edit();
                                    editor.putString("Account", phone);
                                    editor.putString("Password", pwd);
                                    editor.putBoolean("LoginBool", true);
                                    // 将数据提交到sharedpreferences中
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this, UiActivity.class);
                                    startActivity(intent);
                                } else {
                                    new Thread() {
                                        public void run() {
                                            Log.i("log", "run");
                                            Looper.prepare();
                                            Toast.makeText(MainActivity.this, "账号或密码错误", Toast.LENGTH_LONG).show();
                                            Looper.loop();// 进入loop中的循环，查看消息队列
                                        }
                                    }.start();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });

    }
}
