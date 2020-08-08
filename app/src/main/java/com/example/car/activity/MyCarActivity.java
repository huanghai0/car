package com.example.car.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.car.R;
import com.example.car.common.HttpConnectionService;
import com.example.car.common.JsonToList;
import com.example.car.entity.Car;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyCarActivity extends AppCompatActivity {

    private final int ADDCAR = 1;
    private final int FINDCAR = 2;
    private final int DELETECAR = 3;
    private Handler handler;
    private HandlerThread handlerThread;


    private Toolbar toolbar;
    private Button mBtnAddCar;
    private Button mBtnDeleteCar;
    private Button mBtnFindCar;
    private EditText etUserName;
    private EditText etPassword;
    private TextView mTvFindCar;
    private ListView mLvMyCar;

    private EditText mEtcid;


    private HttpConnectionService httpConnectionService;

    private String url = "http://192.168.42.106:8080/api/car";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("车库");
        setSupportActionBar(toolbar);
        //设置左上角图标是否可点击
        getSupportActionBar().setHomeButtonEnabled(true);
        //左上角加上一个返回图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mLvMyCar = findViewById(R.id.lv_mycar);
        mBtnAddCar = findViewById(R.id.btn_add_car);
        mBtnDeleteCar = findViewById(R.id.btn_delete_car);
        mBtnFindCar = findViewById(R.id.btn_find_car);
        OnClick onClick = new OnClick();
        mBtnAddCar.setOnClickListener(onClick);
        mBtnDeleteCar.setOnClickListener(onClick);
        mBtnFindCar.setOnClickListener(onClick);

        handlerThread = new HandlerThread("http_addcar");
        handlerThread.start();
        handler = new MyCarActivity.HttpHandler(handlerThread.getLooper());


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_car:
                    AlertDialog.Builder builder5 = new AlertDialog.Builder(MyCarActivity.this);
                    View view = LayoutInflater.from(MyCarActivity.this).inflate(R.layout.layout_addcar, null);
                    etUserName = view.findViewById(R.id.et_carnum);
                    etPassword = view.findViewById(R.id.et_carname);
                    Button btnLogin = view.findViewById(R.id.btn_submit_car);
                    btnLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handler.sendEmptyMessage(ADDCAR);
                        }
                    });
                    builder5.setTitle("添加汽车").setView(view).show();
                    break;
                case R.id.btn_delete_car:
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyCarActivity.this);
                    View view1 = LayoutInflater.from(MyCarActivity.this).inflate(R.layout.layout_deletecar, null);
                    mEtcid = view1.findViewById(R.id.et_deletecar_cid);
                    Button mBtnSubmit = view1.findViewById(R.id.btn_submit_delete);
                    mBtnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handler.sendEmptyMessage(DELETECAR);
                        }
                    });
                    builder.setTitle("输入车编号").setView(view1).show();
                    break;
                case R.id.btn_find_car:
                    handler.sendEmptyMessage(FINDCAR);
                    break;
            }

        }
    }

    private void setListView(final ListView text, final List<Car> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //String[] carStr = list.toArray(new String[list.size()]);
                String[] code = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    code[i] = "车号: "+list.get(i).getCid()+"   车名："+list.get(i).getCname()+"   车牌："+list.get(i).getCnumber();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        MyCarActivity.this, android.R.layout.simple_list_item_1, code);
                mLvMyCar.setAdapter(adapter);
            }
        });
    }


//    private Handler mhandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case FINDCAR:
//                    //在这里可以进行UI操作
//                    //对msg.obj进行String强制转换
//                    String string = (String) msg.obj;
//                    mBtnFindCar.setText(string);
//                    break;
//                default:
//                    break;
//            }
//        }
//
//    };

    private class HttpHandler extends Handler {

        public HttpHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            SharedPreferences share = getSharedPreferences("Login",
                    Context.MODE_PRIVATE);
            switch (msg.what) {
                case ADDCAR:
                    try {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("cnumber", etUserName.getText());
                            jsonObject.put("cname", etPassword.getText());
                            jsonObject.put("cphone", share.getString("Account", ""));

                            httpConnectionService = new HttpConnectionService();
                            httpConnectionService.HttpPostConnection(url, jsonObject.toString());
                            Toast.makeText(MyCarActivity.this, "添加成功！", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("--------------" + jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case DELETECAR:
                    httpConnectionService = new HttpConnectionService();
                    httpConnectionService.HttpDeleteConection(url + "/" + mEtcid.getText());
                    System.out.println(url + "/" + mEtcid.getText());
                    Toast.makeText(MyCarActivity.this, "删除成功！", Toast.LENGTH_LONG).show();
                    break;
                case FINDCAR:
                    httpConnectionService = new HttpConnectionService();
                    msg.obj = httpConnectionService.HttpGetConection(url + "/cphone/" + share.getString("Account", ""));

                    JsonToList<Car> jsonToList = new JsonToList<>(Car.class);
                    ArrayList<Car> list = jsonToList.jtl((String) msg.obj);
                    setListView(mLvMyCar,list);
                    break;
            }
        }
    }

}
