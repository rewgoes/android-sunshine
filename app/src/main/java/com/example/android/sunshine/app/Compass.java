package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by rewgoes on 04/05/16.
 */
public class Compass extends View {

    private double direction;

    public Compass(Context context) {
        super(context);
    }

    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Compass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        float r;
        if(w > h){
            r = h/2;
        }else{
            r = w/2;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.GRAY);

        canvas.drawCircle(w/2, h/2, r, paint);

        paint.setColor(getResources().getColor(R.color.sunshine_dark_blue));
        canvas.drawLine(
                w/2,
                h/2,
                (float)((w/2) + (r * Math.sin(direction))),
                (float)((h/2) - (r * Math.cos(direction))),
                paint);

    }

    public void update(float dir){
        direction = Math.toRadians(dir);

        // Call invalidate to force drawing on page.
        invalidate();
    }
}
