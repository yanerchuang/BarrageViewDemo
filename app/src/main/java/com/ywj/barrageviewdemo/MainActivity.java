package com.ywj.barrageviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BarrageView barrageView;
    private List<BarrageViewBean> barrageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barrageView = findViewById(R.id.barrageview);

        init();
    }

    private void init() {
        barrageViews = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            barrageViews.add(new BarrageViewBean("小灰灰" + (i + 1), "16:1" + i % 10, "https://avatar.csdn.net/B/7/D/3_u011106915.jpg"));
        }
        barrageView.setData(barrageViews);
        barrageView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barrageView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barrageView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barrageView.onDestroy();
    }
}
