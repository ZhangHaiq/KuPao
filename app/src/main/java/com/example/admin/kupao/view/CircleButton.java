package com.example.admin.kupao.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

import com.example.admin.kupao.R;

/**
 * Created by admin on 2018/2/3.
 */

@SuppressLint("AppCompatCustomView")
public class CircleButton extends Button{

    private Paint paint = new Paint();

    private Paint textPaint = new Paint();

    private int color;

    public CircleButton(Context context) {
        super(context);
        init();
    }

    public CircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setBackgroundColor(getResources().getColor(R.color.running));
        color = getResources().getColor(R.color.running);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centre = getWidth() / 2;
        int radius = centre;

        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawCircle(centre, centre, radius, paint);
        textPaint.setTextSize(getTextSize());
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(getTextColors().getDefaultColor());

        float length = textPaint.measureText(getText().toString());
        canvas.drawText(getText().toString(), centre - length / 2, centre + getTextSize() / 3, textPaint);

    }

    public void setPaintColor(int color){
        this.color = getResources().getColor(color);
    }
}
