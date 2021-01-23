package com.ywj.barrageviewdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import java.util.List;

/**
 * 弹幕视图，使用属性动画
 */
public class BarrageView extends FrameLayout {
    private String Tag = BarrageView.class.getSimpleName();

    private final int CODE_START = 1000;
    private final int CODE_NEXT = 1001;
    private final int CODE_END = 1002;

    //数据源
    private List<?> datas;
    private ViewHolder viewHolder;
    //控件宽
    private int barrageViewWidth;
    //控件高
    private int barrageViewHeight;


    //弹幕行数
    private int displayLines = 10;
    //是否循环显示
    private boolean isRepeat = true;
    //动画时间
    private long animationTime = 6 * 1000L;

    //两条弹幕最小间隔时间
    private long minIntervalTime = 1000L;
    //两条弹幕最大间隔时间
    private long maxIntervalTime = 3000L;


    //大当前弹幕索引
    private int currentIndex;

    //弹幕状态
    private boolean isStart;

    //上一次出现的行数
    private int lastLine = -1;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_START:
                    handler.sendEmptyMessage(CODE_NEXT);
                    break;
                case CODE_NEXT:
                    if (isStart && datas != null && currentIndex < datas.size()) {
                        addView();
                        currentIndex++;
                        long interval = maxIntervalTime - minIntervalTime;
                        long randomSleepTime =  minIntervalTime + (long)(interval > 0 ? Math.random() * interval : 0);
                        handler.sendEmptyMessageDelayed(CODE_NEXT, randomSleepTime);
                    } else {
                        handler.sendEmptyMessage(CODE_END);
                    }
                    break;
                case CODE_END:
                    Log.d(Tag, "CODE_END");
                    if (isRepeat) {
                        if (currentIndex != 0) {
                            currentIndex = 0;
                            handler.sendEmptyMessage(CODE_NEXT);
                        }
                    }
                    break;
            }

        }
    };
    private LinearInterpolator linearInterpolator;

    public BarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void addView() {
        final View itemView = viewHolder.getItemView(getContext(), datas.get(currentIndex), currentIndex);
        addView(itemView);
        itemView.setY(getItemRandomY());
        itemView.measure(0, 0);
        int itemViewWidth = itemView.getMeasuredWidth();
        itemView.setX(this.barrageViewWidth);

        if (linearInterpolator == null) {
            linearInterpolator = new LinearInterpolator();
        }

        final ObjectAnimator anim = ObjectAnimator.ofFloat(itemView, "translationX", -itemViewWidth);
        anim.setDuration(animationTime);
        anim.setInterpolator(linearInterpolator);
        //释放资源
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                anim.cancel();
                itemView.clearAnimation();
                removeView(itemView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    /**
     * 获得随机的Y轴的值
     */
    private float getItemRandomY() {

        //随机选择弹幕出现的行数位置，跟上一条位置不同行
        int randomLine = lastLine;
        if (displayLines > 1) {
            while (randomLine == lastLine) {
                randomLine = (int) (Math.random() * displayLines + 1);
            }
        }

        lastLine =randomLine ;
        //当前itemView y值
        return (float) (barrageViewHeight*1.0 / displayLines * (randomLine - 1));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        barrageViewWidth = getWidth();
        barrageViewHeight = getHeight();

    }

    private int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //设置数据
    public void setData(List<?> list, ViewHolder viewHolder) {
        datas = list;
        this.viewHolder = viewHolder;
    }

    public void start() {
        isStart = true;
        handler.sendEmptyMessage(CODE_START);
    }


    public void onResume() {
        if (!isStart) {
            isStart = true;
            handler.sendEmptyMessage(CODE_NEXT);
        }
    }

    public void onPause() {
        isStart = false;
        handler.removeMessages(CODE_NEXT);
    }

    public void cancle() {
        isStart = false;
        currentIndex = 0;
        if (datas != null) {
            datas.clear();
        }
        removeAllViews();
        handler.removeMessages(CODE_NEXT);
    }

    public void onDestroy() {
        cancle();
    }


    /**
     * 获取显示行数
     *
     * @return 行数
     */
    public int getDisplayLines() {
        return displayLines;
    }

    /**
     * 设置显示行数
     *
     * @param displayLines 行数
     */
    public void setDisplayLines(int displayLines) {
        if (displayLines <= 0) {
            return;
        }
        this.displayLines = displayLines;
    }

    /**
     * 是否重复
     *
     * @return 是否
     */
    public boolean isRepeat() {
        return isRepeat;
    }

    /**
     * 设置是否重复
     *
     * @param repeat 是否
     */
    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    /**
     * 获取动画持续时间
     *
     * @return 时长ms
     */
    public long getAnimationTime() {
        return animationTime;
    }

    /**
     * 设置动画持续时长
     *
     * @param animationTime ms
     */
    public void setAnimationTime(long animationTime) {
        this.animationTime = animationTime;
    }


    /**
     * 获取最小间隔时间
     *
     * @return ms
     */
    public long getMinIntervalTime() {
        return minIntervalTime;
    }

    /**
     * 设置最小间间隔时间
     *
     * @param minIntervalTime ms
     */
    public void setMinIntervalTime(long minIntervalTime) {
        if (minIntervalTime <= 0) {
            return;
        }
        this.minIntervalTime = minIntervalTime;
    }

    /**
     * 获取最大间隔时间
     *
     * @return ms
     */
    public long getMaxIntervalTime() {
        return maxIntervalTime;
    }

    /**
     * 设置最大间间隔时间
     *
     * @param maxIntervalTime ms
     */
    public void setMaxIntervalTime(long maxIntervalTime) {
        if (maxIntervalTime <= 0) {
            return;
        }
        this.maxIntervalTime = maxIntervalTime;
    }


    public interface ViewHolder {
        View getItemView(Context context, Object item, int index);
    }
}
