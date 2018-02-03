package com.example.admin.kupao.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.Button;

import com.example.admin.kupao.R;

/**
 * Created by admin on 2018/2/3.
 */

@SuppressLint("AppCompatCustomView")
public class CircleWaveButton extends Button {

    private Paint paint = new Paint();

    private Paint textPaint = new Paint();

    private int radiusInt = 0;

    private Boolean isStart = false;

    //这是默认颜色
    private int paintColor= R.color.circle_bule_bbd4e7;
    //这是默认文字内容
    private String text="开始";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
            if (isStart){
                radiusInt++;
                if (radiusInt > 50){
                    radiusInt = 0;
                }
                sendEmptyMessageDelayed(0, 20);
            }
        }
    };


    public CircleWaveButton(Context context) {
        super(context);
        init();
    }

    public CircleWaveButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleWaveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setBackgroundColor(getResources().getColor(R.color.running));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centre = getWidth() / 2;
        int radius = centre;

        Paint newPaint = new Paint();

        newPaint.setAntiAlias(true);
        newPaint.setStyle(Paint.Style.FILL);
        Shader shader = new RadialGradient(centre, centre, radius, getResources().getColor(paintColor), getResources().getColor(R.color.running), Shader.TileMode.CLAMP);

        newPaint.setShader(shader);

        canvas.drawCircle(centre, centre, radius, newPaint);

        paint.setColor(getResources().getColor(paintColor));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        for (int i = 1; i <=5; i++ ){
            paint.setAlpha(20*i);
            canvas.drawCircle(centre, centre, radius * (10- i)/ 10, paint);
        }

        paint.setColor(getResources().getColor(paintColor));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(radius * 2 / 12 );

        for (int i = 0; i < 3; i++){
            paint.setAlpha(60-i*20);
            canvas.drawCircle(centre, centre, radius * (14-i*2 + radiusInt / 50.0f) / 16, paint);
        }

        textPaint.setTextSize(getTextSize());
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(getTextColors().getDefaultColor());

        float length = textPaint.measureText(text);
        canvas.drawText(text, centre - length / 2, centre + getTextSize() / 3, textPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidate();
    }

    public void start() {
        isStart = true;
        handler.sendEmptyMessage(0);
    }

    public void stop() {
        isStart = false;
        handler.removeMessages(0);
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public void  setText(String text){


    }
}
