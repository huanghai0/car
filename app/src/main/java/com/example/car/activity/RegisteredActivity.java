package com.example.car.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.car.MainActivity;
import com.example.car.R;
import com.example.car.common.HttpConnectionService;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisteredActivity extends AppCompatActivity {

    private EditText mEtPhone;
    private EditText mEtName;
    private RadioGroup mRgSex;
    private EditText mEtPwd;
    private EditText mEtRePwd;
    private Button mBtnRegistered;
    private volatile String sexText;

    private HandlerThread handlerThread;
    private Handler handler;
    private final int REGISTER = 1;
    private HttpConnectionService httpConnectionService = new HttpConnectionService();

    String registerUrl = "http://192.168.42.106:8080/api/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        mEtPhone = findViewById(R.id.et_phone);
        mEtName = findViewById(R.id.et_name);
        mRgSex = findViewById(R.id.rg_sex);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtRePwd = findViewById(R.id.et_repwd);
        mBtnRegistered = findViewById(R.id.btn_registered);

        handlerThread = new HandlerThread("http");
        handlerThread.start();
        handler = new HttpHandler(handlerThread.getLooper());

        mRgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectRadioButton();
            }
        });

        mBtnRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(REGISTER);
            }
        });

    }

    private class HttpHandler extends Handler {
        public HttpHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER:
                    try {
                        if (isRegistered()) {
                            Toast.makeText(RegisteredActivity.this, "手机号以被注册！", Toast.LENGTH_LONG).show();
                        } else {
                            String res = httpConnectionService.HttpPostConnection(registerUrl, getDate());
//                            System.out.println(res+"____++++++++++++++++++++++++");
                            Toast.makeText(RegisteredActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisteredActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private boolean isRegistered() {

        boolean flag = false;
        String url = registerUrl+"/" + mEtPhone.getText();

        String res = httpConnectionService.HttpGetConection(url);
        System.out.println("--------" + res);
        try {
            JSONObject jsonObject = new JSONObject(res);
            String phoneNum = mEtPhone.getText().toString();
            if (phoneNum.equals(jsonObject.getString("phone"))) {
                flag = true;
                return flag;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flag;
    }

    private String getDate() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", mEtName.getText());
        jsonObject.put("sex", sexText);
        jsonObject.put("phone", mEtPhone.getText());
        jsonObject.put("pwd", mEtPwd.getText());
        System.out.println("--------------" + jsonObject.toString());
        return jsonObject.toString();
    }

    private void selectRadioButton() {
        //通过radioGroup.getCheckedRadioButtonId()来得到选中的RadioButton的ID，从而得到RadioButton进而获取选中值
        RadioButton rb = RegisteredActivity.this.findViewById(mRgSex.getCheckedRadioButtonId());
        sexText = rb.getText().toString();
        System.out.println("******************" + sexText);
    }

}
