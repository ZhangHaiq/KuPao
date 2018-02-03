package com.example.admin.kupao.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by adu on 16/10/23.
 */
public class WaveView extends View {

    private Paint wavePaint;

    private int waveColor = 0xff0099CC;

    private Path path1;
    private Path path2;
    private Path path3;

    // 振幅
    private float swing = 0;

    private int height;
    private int width;

    private int ms = 30;

    private float isPause=0f;

    private boolean isRun = false;

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context) {
        super(context);
        init();
    }

    private void init() {
        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setColor(waveColor);
        wavePaint.setStrokeWidth(5);
        wavePaint.setStyle(Paint.Style.STROKE);
        path1 = new Path();
        path2 = new Path();
        path3 = new Path();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setPath();

        wavePaint.setStrokeWidth(6);
        wavePaint.setAlpha(100);
        canvas.drawPath(path1, wavePaint);
        wavePaint.setStrokeWidth(3);
        wavePaint.setAlpha(80);
        canvas.drawPath(path2, wavePaint);
        wavePaint.setAlpha(60);
        canvas.drawPath(path3, wavePaint);

    }

    private void setPath() {
        int x = 0;
        int y = 0;
        path1.reset();
        for (int i = 0; i < width; i++) {
            x = i;
            y = (int) (isPause*40* Math.sin(i * 2*0.7f * Math.PI / width+swing) + height*0.5);
            if (i == 0) {
                path1.moveTo(x, y);
            }
            path1.quadTo(x, y, x + 1, y);
        }

        path2.reset();
        for (int i = 0; i < width; i++) {
            x = i;
            y = (int) (isPause*40* Math.sin(i * 2*0.7f * Math.PI / width+swing+0.3f) + height*0.5);
            if (i == 0) {
                path2.moveTo(x, y);
            }
            path2.quadTo(x, y, x + 1, y);
        }

        path3.reset();
        for (int i = 0; i < width; i++) {
            x = i;
            y = (int) (isPause*40* Math.sin(i * 2*0.7f * Math.PI / width+swing-0.3f) + height*0.5);
            if (i == 0) {
                path3.moveTo(x, y);
            }
            path3.quadTo(x, y, x + 1, y);
        }

        path1.close();
        path2.close();
        path3.close();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initLayoutParams();
    }

    private void initLayoutParams() {
        height = this.getHeight();
        width = this.getWidth();
    }

    public void start(){
        this.isRun=true;
        this.isPause=1.0f;
        new MyThread().start();
    }
    public void stop(){
        this.isRun=false;
        this.isPause=0.0f;
        invalidate();
    }

    public WaveView setWaveColor(int color) {
        this.waveColor = color;
        wavePaint.setColor(waveColor);
        return this;
    }


    private class MyThread extends Thread {

        @Override
        public void run() {
            while (isRun) {
                swing+=-0.25f;
                mHandler.sendEmptyMessage(1);
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                invalidate();
            }
        }
    };
}

