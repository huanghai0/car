package com.example.car.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.car.MainActivity;
import com.example.car.R;
import com.example.car.common.FindArrayList;
import com.example.car.common.HttpConnectionService;
import com.example.car.entity.Car;
import com.example.car.entity.Product;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class UiActivity extends AppCompatActivity {

    private final int UPKEER = 1;
    private final int REPAIR = 2;

    private TextView mTvUserName;
    private Button mBtnUpKeep;
    private Button mBtnRepair;
    private Button mBtnSpot;
    private DrawerLayout drawerLayout;
    private NavigationView navigation;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private EditText mEtOrderDate;
    private EditText mEtRepairTime;

    private String cid;
    private String pid;

    private HandlerThread handlerThread;
    private Handler handler;

    private final String URL = "http://192.168.42.106:8080";
    private HttpConnectionService httpConnectionService = new HttpConnectionService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);

        drawerLayout = findViewById(R.id.main_drawer);
        navigation = findViewById(R.id.main_navigationview);
        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("首页");
        //设置toolbar
        setSupportActionBar(toolbar);
        //设置左上角图标是否可点击
        getSupportActionBar().setHomeButtonEnabled(true);
        //左上角加上一个返回图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences share = getSharedPreferences("Login",
                Context.MODE_PRIVATE);
        mTvUserName = findViewById(R.id.tv_uesrname);
        mTvUserName.setText("你好" + share.getString("Account", "") + "用户！");


        mBtnUpKeep = findViewById(R.id.btn_upkeep);
        mBtnRepair = findViewById(R.id.btn_repair);
        mBtnSpot = findViewById(R.id.btn_spot);
        OnClick onClick = new OnClick();
        mBtnUpKeep.setOnClickListener(onClick);
        mBtnRepair.setOnClickListener(onClick);
        mBtnSpot.setOnClickListener(onClick);


        handlerThread = new HandlerThread("uihttp");
        handlerThread.start();
        handler = new HttpHandler(handlerThread.getLooper());

        //初始化ActionBarDrawerToggle(ActionBarDrawerToggle就是一个开关一样用来打开或者关闭drawer)
        drawerToggle = new ActionBarDrawerToggle(UiActivity.this, drawerLayout, toolbar, R.string.openString, R.string.closeString) {
            /*
             * 抽屉菜单打开监听
             * */
            @Override
            public void onDrawerOpened(View drawerView) {
//                Toast.makeText(UiActivity.this,"菜单打开了",Toast.LENGTH_SHORT).show();
                super.onDrawerOpened(drawerView);
            }

            /*
             * 抽屉菜单关闭监听
             * */
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        /*
         * NavigationView设置点击监听
         * */
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_item_mycar:
                        startActivity(new Intent(UiActivity.this, MyCarActivity.class));
                        break;
                    case R.id.menu_item_myorder:
                        startActivity(new Intent(UiActivity.this, MyOrderActivity.class));
                        break;
                    case R.id.menu_item_vip:
                        startActivity(new Intent(UiActivity.this, VipActivity.class));
                        break;
                    case R.id.menu_item_quitaccount:
                        SharedPreferences share = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        share.edit().putBoolean("LoginBool", false).apply();
                        Intent intent = new Intent(UiActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.menu_item_quitapp:
                        System.exit(0);
                        break;
                }
                Toast.makeText(UiActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return false;
            }
        });
        drawerToggle.syncState();
        //设置DrawerLayout的抽屉开关监听
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class MyCallable implements Callable<Object> {
        SharedPreferences share = getSharedPreferences("Login",
                Context.MODE_PRIVATE);
        String url = URL + "/api/car" + "/cphone/" + share.getString("Account", "");

        @Override
        public String call() throws Exception {
            String res = httpConnectionService.HttpGetConection(url);
            return res;
        }
    }

    class MyRepairCallable implements Callable<Object> {
        String url = URL + "/api/product";

        @Override
        public String call() throws Exception {
            String res = httpConnectionService.HttpGetConection(url);
            System.out.println(res + "__________+++++");
            return res;
        }
    }

    private ArrayList<Car> findCarList() {

        FindArrayList<Car> findArrayList = new FindArrayList<>(Car.class);

        MyCallable myCallable = new MyCallable();
        ArrayList<Car> carlist = new ArrayList<>();
        try {
            carlist = findArrayList.findList(myCallable);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return carlist;
    }

    private ArrayList<Product> findProductList() {

        FindArrayList<Product> findArrayList = new FindArrayList<>(Product.class);

        MyRepairCallable myRepairCallable = new MyRepairCallable();
        ArrayList<Product> productlist = new ArrayList<>();

        try {
            productlist = findArrayList.findList(myRepairCallable);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return productlist;
    }


    class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_upkeep:
                    AlertDialog.Builder builder5 = new AlertDialog.Builder(UiActivity.this);
                    View view = LayoutInflater.from(UiActivity.this).inflate(R.layout.layout_dialog_upkeep, null);
                    mEtOrderDate = view.findViewById(R.id.et_upkeep_time);
                    Button btnOrder = view.findViewById(R.id.btn_upkeep);
                    RadioGroup mRadioGroup = view.findViewById(R.id.tabs_type);

                    ArrayList<Car> carlist = findCarList();

                    Iterator<Car> iterator = carlist.iterator();
                    while (iterator.hasNext()) {
                        RadioButton radioButton = (RadioButton) LayoutInflater.from(UiActivity.this).inflate(R.layout.layout_radio_button, null);
                        radioButton.setText(iterator.next().getCid() + "");
                        mRadioGroup.addView(radioButton);
                    }

                    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            int selectedId = mRadioGroup.getCheckedRadioButtonId();
                            RadioButton rb = (RadioButton) mRadioGroup.findViewById(selectedId);
                            cid = rb.getText().toString();
                        }
                    });

                    btnOrder.setOnClickListener(v12 -> handler.sendEmptyMessage(UPKEER));
                    builder5.setTitle("保养预约").setView(view).show();
                    break;

                case R.id.btn_repair:
                    AlertDialog.Builder builder4 = new AlertDialog.Builder(UiActivity.this);
                    View view4 = LayoutInflater.from(UiActivity.this).inflate(R.layout.layout_dialog_repair, null);
                    Button mBtnSubRepair = view4.findViewById(R.id.btn_submit_repair);
                    mEtRepairTime = view4.findViewById(R.id.et_repair_time);

                    RadioGroup mRgCarId = view4.findViewById(R.id.tabs_type_repair);
                    ArrayList<Car> carlist1 = findCarList();
                    Iterator<Car> iterator1 = carlist1.iterator();
                    while (iterator1.hasNext()) {
                        RadioButton radioButton = (RadioButton) LayoutInflater.from(UiActivity.this).inflate(R.layout.layout_radio_button, null);
                        radioButton.setText(iterator1.next().getCid() + "");
                        mRgCarId.addView(radioButton);
                    }
                    mRgCarId.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            int selectedId = mRgCarId.getCheckedRadioButtonId();
                            RadioButton rb = (RadioButton) mRgCarId.findViewById(selectedId);
                            cid = rb.getText().toString();
                            System.out.println("cid--->>>" + cid);
                        }
                    });

                    RadioGroup mRgProduct = view4.findViewById(R.id.tabs_repair);
                    ArrayList<Product> productlist1 = findProductList();
                    Iterator<Product> iterator11 = productlist1.iterator();
                    while (iterator11.hasNext()) {
                        RadioButton radioButton = (RadioButton) LayoutInflater.from(UiActivity.this).inflate(R.layout.layout_radio_button, null);
                        Product product = iterator11.next();
                        if (!"汽车保养".equals(product.getPname())) {
                            radioButton.setText(product.getPname() + " : " + product.getPprice() + " RMP");
                            radioButton.setId(product.getPid());
                            mRgProduct.addView(radioButton);
                        }
                    }
                    mRgProduct.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            int selectedId = mRgProduct.getCheckedRadioButtonId();
                            RadioButton rb = (RadioButton) mRgProduct.findViewById(selectedId);
                            pid = rb.getId() + "";
                            System.out.println(pid + ">>>>>>>>>>>>>>>>");
                        }
                    });

                    mBtnSubRepair.setOnClickListener(v1 -> {
                        handler.sendEmptyMessage(REPAIR);
                    });
                    builder4.setTitle("汽车维修").setView(view4).show();
                    break;
                case R.id.btn_spot:
                    startActivity(new Intent(UiActivity.this, MapSpotActivity.class));
                    break;
            }
        }
    }

    private class HttpHandler extends Handler {
        public HttpHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPKEER:
                    try {
                        String orderurl = URL + "/api/order";
                        String res = httpConnectionService.HttpPostConnection(orderurl, getOrderJson());
//                        System.out.println(res + "____++++++++++++++++++++++++");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REPAIR:
                    try {
                        String prodUrl = URL + "/api/product/" + pid;
                        String product = httpConnectionService.HttpGetConection(prodUrl);
                        JSONObject prodjson = new JSONObject(product);
                        String mid = prodjson.getString("pmid");
                        //    System.out.println("mid=>>>>" + mid);
                        if (!"0".equals(mid)) {
                            String materUrl = URL + "/api/materout/id/" + mid;
                            String materout = httpConnectionService.HttpGetConection(materUrl);
                            JSONObject materjson = new JSONObject(materout);
                            String maternum = materjson.getString("mnum");
                            int number = Integer.parseInt(maternum);
                            if (number > 0) {
                                int nNum = number - 1;
                                httpConnectionService.HttpPutConection(URL + "/api/materout/" + mid + "/" + nNum, "");

                                String orderurl = URL + "/api/order";
                                String res = httpConnectionService.HttpPostConnection(orderurl, getOrderJson1());
                                Toast.makeText(UiActivity.this, "下单成功", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(UiActivity.this, "库存不足，无法下单", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            String orderurl = URL + "/api/order";
                            String res = httpConnectionService.HttpPostConnection(orderurl, getOrderJson1());
                            Toast.makeText(UiActivity.this, "下单成功", Toast.LENGTH_SHORT).show();
                        }

//                        System.out.println(res + "____++++++++++++++++++++++++");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("下单异常");
                    }
                    break;
            }
        }
    }

    private String getOrderJson() throws JSONException {

        SharedPreferences share = getSharedPreferences("Login",
                Context.MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("odate", mEtOrderDate.getText());
        jsonObject.put("ophone", share.getString("Account", ""));
        jsonObject.put("opid", "1");
        jsonObject.put("ocid", cid);
        System.out.println("--------------" + jsonObject.toString());
        return jsonObject.toString();
    }

    private String getOrderJson1() throws JSONException {

        SharedPreferences share = getSharedPreferences("Login",
                Context.MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("odate", mEtRepairTime.getText());
        jsonObject.put("ophone", share.getString("Account", ""));
        jsonObject.put("opid", pid);
        jsonObject.put("ocid", cid);
        System.out.println("--------------" + jsonObject.toString());
        return jsonObject.toString();
    }
}
