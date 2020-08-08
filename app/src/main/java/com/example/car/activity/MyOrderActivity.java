package com.example.car.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.car.R;
import com.example.car.common.GetMyFutureList;
import com.example.car.common.HttpConnectionService;
import com.example.car.common.JsonToList;
import com.example.car.entity.Order;
import com.example.car.entity.User;
import com.example.car.pay.PayDemoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MyOrderActivity extends AppCompatActivity {

    private final int PAYORDER = 1;
    private final int AFTERSALE = 2;
    private final int FINDORDER = 3;

    private Toolbar toolbar;
    private Button mBtnPayOrder;
    private Button mBtnFindOrder;
    private Button mBtnSale;
    private RadioGroup mRadioGroup;
    private RadioGroup mRadioGroup1;
    private ListView mLvMyOrder;

    private EditText mEtSeaSale;
    private String oid;
    private String soid;

    private Handler handler;
    private HandlerThread handlerThread;

    private HttpConnectionService httpConnectionService = new HttpConnectionService();
    private final String URL = "http://192.168.42.106:8080";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("账单");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLvMyOrder = findViewById(R.id.lv_myorder);
        mBtnPayOrder = findViewById(R.id.btn_payorder);
        mBtnFindOrder = findViewById(R.id.btn_find_order);
        mBtnSale = findViewById(R.id.btn_sale);
        OnClick onClick = new OnClick();
        mBtnPayOrder.setOnClickListener(onClick);
        mBtnFindOrder.setOnClickListener(onClick);
        mBtnSale.setOnClickListener(onClick);

        handlerThread = new HandlerThread("http_payorder");
        handlerThread.start();
        handler = new HttpHandler(handlerThread.getLooper());


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    class MyCallable implements Callable<Object> {
        SharedPreferences share = getSharedPreferences("Login",
                Context.MODE_PRIVATE);
        String url = URL + "/api/order" + "/ophone/" + share.getString("Account", "");

        @Override
        public String call() throws Exception {
            HttpConnectionService httpConnectionService = new HttpConnectionService();
            String res = httpConnectionService.HttpGetConection(url);
            return res;
        }
    }

    private void setListView(final ListView text, final List<Order> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] code = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    code[i] = list.get(i).toString();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        MyOrderActivity.this, android.R.layout.simple_list_item_1, code);
                mLvMyOrder.setAdapter(adapter);
            }
        });
    }


    class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_payorder:
                    AlertDialog.Builder builder5 = new AlertDialog.Builder(MyOrderActivity.this);
                    View view = LayoutInflater.from(MyOrderActivity.this).inflate(R.layout.layouy_dialog_payorder, null);

                    Button btnOrder = view.findViewById(R.id.btn_pay);
                    mRadioGroup = view.findViewById(R.id.tabs_ordernum);

                    ArrayList<Order> carlist = new ArrayList<>();

                    try {
                        Callable callable = new MyCallable();
                        GetMyFutureList getMyFutureList = new GetMyFutureList();
                        List<Future> list = getMyFutureList.getObList(callable);
                        for (Future ff : list) {
                            System.out.println(">>>>>>" + ff.get().toString());
                            String str = ff.get().toString();
                            JsonToList<Order> jsonToList = new JsonToList<>(Order.class);
                            carlist = jsonToList.jtl(str);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Iterator<Order> iterator = carlist.iterator();
                    while (iterator.hasNext()) {
                        RadioButton radioButton = (RadioButton) LayoutInflater.from(MyOrderActivity.this).inflate(R.layout.layout_radio_button, null);
                        Order order = iterator.next();
                        if (order.getOstat() == 0) {
                            radioButton.setText(order.getOid() + "");
                            mRadioGroup.addView(radioButton);
                        }
                    }

                    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            int selectedId = mRadioGroup.getCheckedRadioButtonId();
                            RadioButton rb = (RadioButton) mRadioGroup.findViewById(selectedId);
                            oid = rb.getText().toString();
                        }
                    });

                    btnOrder.setOnClickListener(v1 -> {
                        handler.sendEmptyMessage(PAYORDER);
                        //  startActivity(new Intent(MyOrderActivity.this, PayDemoActivity.class));
                        // Toast.makeText(MyOrderActivity.this, "支付成功！", Toast.LENGTH_LONG).show();
                    });
                    builder5.setTitle("待支付账单").setView(view).show();
                    break;
                case R.id.btn_sale:

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MyOrderActivity.this);
                    View view2 = LayoutInflater.from(MyOrderActivity.this).inflate(R.layout.layout_dialog_sale, null);
                    mEtSeaSale = view2.findViewById(R.id.et_sale_season);
                    Button btnSubSale = view2.findViewById(R.id.btn_submit_sale);
                    mRadioGroup1 = view2.findViewById(R.id.tabs_salenum);

                    ArrayList<Order> orderslist = new ArrayList<>();

                    try {
                        Callable callable = new MyCallable();
                        GetMyFutureList getMyFutureList = new GetMyFutureList();
                        List<Future> list = getMyFutureList.getObList(callable);
                        for (Future ff : list) {
                            System.out.println(">>>>>>" + ff.get().toString());
                            String str = ff.get().toString();
                            JsonToList<Order> jsonToList = new JsonToList<>(Order.class);
                            orderslist = jsonToList.jtl(str);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Iterator<Order> iterator1 = orderslist.iterator();
                    while (iterator1.hasNext()) {
                        RadioButton radioButton = (RadioButton) LayoutInflater.from(MyOrderActivity.this).inflate(R.layout.layout_radio_button, null);
                        Order order = iterator1.next();
                        if (order.getOstat() == 1) {
                            radioButton.setText(order.getOid() + "");
                            mRadioGroup1.addView(radioButton);
                        }
                    }

                    mRadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            int selectedId = mRadioGroup1.getCheckedRadioButtonId();
                            RadioButton rb1 = (RadioButton) mRadioGroup1.findViewById(selectedId);
                            soid = rb1.getText().toString();
                        }
                    });

                    btnSubSale.setOnClickListener(v1 -> {
                        handler.sendEmptyMessage(AFTERSALE);
                        Toast.makeText(MyOrderActivity.this, "申请成功！", Toast.LENGTH_LONG).show();
                    });
                    builder1.setTitle("售后申请").setView(view2).show();
                    break;
                case R.id.btn_find_order:
                    handler.sendEmptyMessage(FINDORDER);
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
            SharedPreferences share = getSharedPreferences("Login",
                    Context.MODE_PRIVATE);
            switch (msg.what) {


                case PAYORDER:

                    try {
                        String payurl = URL + "/api/order/";
                        String order = httpConnectionService.HttpGetConection(payurl + oid);
                        JSONObject jsonObject = new JSONObject(order);

                        String opid = jsonObject.getString("opid");
                        String prodUrl = URL + "/api/product/" + opid;
                        String product = httpConnectionService.HttpGetConection(prodUrl);
                        JSONObject prodJson = new JSONObject(product);
                        String pprice = prodJson.getString("pprice");
                        double price = Double.parseDouble(pprice);
//                        System.out.println("+++++++" + price);

                        String userUrl = URL + "/api/user/" + share.getString("Account", "");
                        String user = httpConnectionService.HttpGetConection(userUrl);
                        JSONObject userJson = new JSONObject(user);
                        String vid = userJson.getString("vid");

                        if (!"0".equals(vid)) {
                            String vipUrl = URL + "/api/vip/" + vid;
                            String vip = httpConnectionService.HttpGetConection(vipUrl);
                            JSONObject vipJson = new JSONObject(vip);
                            double vdis = Double.parseDouble(vipJson.getString("vdis"));
                            double money = price * vdis;
                            Toast.makeText(MyOrderActivity.this, "成功！支付了：" + money + "元", Toast.LENGTH_SHORT).show();
                            System.out.println("+++++++" + money);
                        } else {
                            double money = price;
                            Toast.makeText(MyOrderActivity.this, "成功！支付了：" + money + "元", Toast.LENGTH_SHORT).show();
                            // System.out.println("+++++++" + money);
                        }

                        jsonObject.remove("ostat");
                        jsonObject.put("ostat", 1);
                        System.out.println(jsonObject.toString() + "^^^^^^^^^^^^^^^");
                        httpConnectionService.HttpPutConection(payurl, jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case AFTERSALE:
                    String saleurl = URL + "/api/sale/";

                    try {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("soid", soid);
                        jsonObject1.put("sphone", share.getString("Account", ""));
                        jsonObject1.put("sseason", mEtSeaSale.getText());
                        httpConnectionService.HttpPostConnection(saleurl, jsonObject1.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case FINDORDER:
                    String url = URL + "/api/order";
                    httpConnectionService = new HttpConnectionService();
                    msg.obj = httpConnectionService.HttpGetConection(url + "/ophone/" + share.getString("Account", ""));

                    JsonToList<Order> jsonToList = new JsonToList<>(Order.class);
                    ArrayList<Order> list = jsonToList.jtl((String) msg.obj);
                    setListView(mLvMyOrder, list);
                    break;

            }
        }
    }

}
