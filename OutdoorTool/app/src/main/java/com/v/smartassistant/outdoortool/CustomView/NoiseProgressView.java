package com.v.smartassistant.outdoortool.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.v.smartassistant.outdoortool.R;

import java.text.DecimalFormat;

/**
 * Created by lishuangwei on 18-2-5.
 */

public class NoiseProgressView extends View {
    /*圆弧线宽*/
    private float circleBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
    /*内边距*/
    private float circlePadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
    /*字体大小*/
    private float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 50, getResources().getDisplayMetrics());
    /*绘制圆周的画笔*/
    private Paint backCirclePaint;
    /*绘制圆周白色分割线的画笔*/
    private Paint linePaint;
    /*绘制文字的画笔*/
    private Paint textPaint;
    /*百分比*/
    private int percent = 0;
    /*渐变圆周颜色数组*/
    private int[] gradientColorArray = new int[]{Color.GREEN, Color.parseColor("#fe751a"), Color.parseColor("#13be23"), Color.GREEN};
    private Paint gradientCirclePaint;

    public NoiseProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public NoiseProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NoiseProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.NoiseView, defStyle, 0);
        circleBorderWidth = a.getDimension(R.styleable.NoiseView_circleBorderWidth, circleBorderWidth);
        circlePadding = a.getDimension(R.styleable.NoiseView_circlePadding, circlePadding);
        textSize = a.getDimension(R.styleable.NoiseView_textSize, textSize);
        a.recycle();

        backCirclePaint = new Paint();
        backCirclePaint.setStyle(Paint.Style.STROKE);
        backCirclePaint.setAntiAlias(true);
        backCirclePaint.setColor(Color.LTGRAY);
        backCirclePaint.setStrokeWidth(circleBorderWidth);

        gradientCirclePaint = new Paint();
        gradientCirclePaint.setStyle(Paint.Style.STROKE);
        gradientCirclePaint.setAntiAlias(true);
        gradientCirclePaint.setColor(Color.LTGRAY);
        gradientCirclePaint.setStrokeWidth(circleBorderWidth);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(5);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.BLACK);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(measureWidth, measureHeight), Math.min(measureWidth, measureHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //灰色背景圆环
        canvas.drawArc(new RectF(circlePadding * 2, circlePadding * 2,
                        getMeasuredWidth() - circlePadding * 2, getMeasuredHeight() - circlePadding * 2), -90, 360, false, backCirclePaint);
        //颜色渐变圆环
        LinearGradient linearGradient = new LinearGradient(circlePadding, circlePadding,
                getMeasuredWidth() - circlePadding,
                getMeasuredHeight() - circlePadding,
                gradientColorArray, null, Shader.TileMode.MIRROR);
        gradientCirclePaint.setShader(linearGradient);
        gradientCirclePaint.setShadowLayer(10, 10, 10, Color.RED);
        canvas.drawArc(
                new RectF(circlePadding * 2, circlePadding * 2,
                        getMeasuredWidth() - circlePadding * 2, getMeasuredHeight() - circlePadding * 2), -180, (float) (percent / 100.0) * 360, false, gradientCirclePaint);

        //半径
        float radius = (getMeasuredWidth() - circlePadding * 3) / 2;
        int centerX = getMeasuredWidth() / 2;

        //切分空心圆弧
        for (float i = 0; i < 360; i += 3.6) {
            double rad = i * Math.PI / 180;
            float startX = (float) (centerX + (radius - circleBorderWidth) * Math.sin(rad));
            float startY = (float) (centerX + (radius - circleBorderWidth) * Math.cos(rad));

            float stopX = (float) (centerX + radius * Math.sin(rad) + 1);
            float stopY = (float) (centerX + radius * Math.cos(rad) + 1);

            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
        }

        float textWidth = textPaint.measureText(percent + "db");
        int textHeight = (int) (Math.ceil(textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent) + 2);
        canvas.drawText(new DecimalFormat("#").format(percent * 1.5) + "db", centerX - textWidth / 2, centerX + textHeight / 4, textPaint);
    }

    public void setPercent(int percent) {
        Log.d("shuang", "setPercent: " + percent);
        if (percent < 0) {
            percent = 0;
        } else if (percent > 100) {
            percent = 100;
        }

        this.percent = percent;
        invalidate();
    }
}
