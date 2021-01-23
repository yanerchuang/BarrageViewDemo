package com.ywj.barrageviewdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BarrageView barrageView;
    private List<BarrageViewBean> barrageViews;
    private String TAG = MainActivity.class.getSimpleName();

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
        barrageView.setData(barrageViews, new BarrageView.ViewHolder() {
            @Override
            public View getItemView(Context context, Object item, final int index) {
                return getItem(context, (BarrageViewBean) item, index);
            }
        });
        barrageView.setDisplayLines(30);//设置行数
        barrageView.setMinIntervalTime(200L);//设置最小显示间隔时间
        barrageView.setMaxIntervalTime(500L);//设置最大显示间隔时间
        barrageView.setAnimationTime(6000L);//设置弹幕持续时长
        barrageView.start();
    }

    private RelativeLayout getItem(Context context, BarrageViewBean item, final int index) {
        BarrageViewBean barrageViewBean = item;
        final RelativeLayout itemView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_barrageview, null);
        RelativeLayout.LayoutParams itemLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dip2px(context, 27));
        itemView.setLayoutParams(itemLayoutParams);
        //设置文字//设置图片
        TextView tvContent = itemView.findViewById(R.id.tv_content);
        tvContent.setText(barrageViewBean.getContent());
        TextView tvTime = itemView.findViewById(R.id.tv_time);
        tvTime.setText(barrageViewBean.getTime());

        ImageView iv = itemView.findViewById(R.id.iv_headview);
        Glide
                .with(context)
                .load(barrageViewBean.getHeadPictureUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .override(100, 100)
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .transform(new GlideCircleTransform(context))
                .into(iv);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: index:" + index);
            }
        });
        return itemView;
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

    private int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
