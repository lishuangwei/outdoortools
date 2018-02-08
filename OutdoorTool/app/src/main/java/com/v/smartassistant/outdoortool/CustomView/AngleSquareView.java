package com.v.smartassistant.outdoortool.CustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by lishuangwei on 18-1-29.
 */

public class AngleSquareView extends View implements View.OnTouchListener {
    private static final String TAG = "AngleSquareView";
    private float outer_w;
    private float outer_h;

    private float middle_w;
    private float middle_h;

    private float inner_w;
    private float inner_h;

    // 最外圈的半径
    private float outer_radius;
    // 中间圈半径
    private float middle_radius;
    // 最内圈半径
    private float inner_radius;

    // 绘制弧形的Paint
    private Paint arcPaint;
    // 数字Paint
    private Paint numPaint;
    private Paint middle_numPaint;

    private Paint paintDegree;

    private Paint paint;

    private Paint paintDegreeNumber;

    private int mMeasuredHeight;
    private int mMeasuredWidth;

    private DecimalFormat df;

    //计算角度的两根线
    private Points mPoint1, mPoint2;
    private static final float DEFAULT_LINE_LENGTH = 60f;
    private int mCurrentLine;
    private boolean mTouched;
    // 最长
    private static final float DEFAULT_LONGEST_DEGREE_LENGTH = 60f;
    //长刻度线
    private static final float DEFAULT_LONG_DEGREE_LENGTH = 50f;
    //短刻度线
    private static final float DEFAULT_SHORT_DEGREE_LENGTH = 30f;
    //lsw 触摸误差
    private static final float OFFSET = 30f;

    public AngleSquareView(Context context) {
        super(context);
        initPaint();
    }

    public AngleSquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public AngleSquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    //初始化画笔
    private void initPaint() {
        setBackgroundColor(Color.TRANSPARENT);
        setOnTouchListener(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(5);
        paint.setColor(Color.parseColor("#00ff00"));

        paintDegreeNumber = new Paint();
        paintDegreeNumber.setTextAlign(Paint.Align.CENTER);
        paintDegreeNumber.setColor(Color.parseColor("#ff0000"));
        paintDegreeNumber.setTextSize(60);
        paintDegreeNumber.setFakeBoldText(true);

        df = new DecimalFormat("#.#°");

        /* 弧形画笔 */
        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(Color.BLACK);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(2);

        /* 数字画笔 */
        numPaint = new Paint();
        numPaint.setAntiAlias(true);
        numPaint.setColor(Color.BLACK);
        numPaint.setTextSize(40);
        numPaint.setFakeBoldText(true);
        numPaint.setTextAlign(Paint.Align.CENTER);

        middle_numPaint = new Paint();
        middle_numPaint.setAntiAlias(true);
        middle_numPaint.setColor(Color.BLACK);
        middle_numPaint.setTextSize(30);
        middle_numPaint.setFakeBoldText(true);
        middle_numPaint.setTextAlign(Paint.Align.CENTER);

        paintDegree = new Paint();
        paintDegree.setAntiAlias(true);
        paintDegree.setStrokeWidth(2);
        paintDegree.setColor(Color.BLACK);
        paintDegree.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        outer_w = (float) (w * 0.8);
        outer_radius = outer_w;
        outer_h = outer_w * 2;

        middle_w = outer_w * 0.6f;
        middle_radius = middle_w;
        middle_h = middle_radius * 2;

        inner_w = outer_w * 0.2f;
        inner_radius = inner_w;
        inner_h = inner_radius * 2;

        mMeasuredHeight = h;
        mMeasuredWidth = w;

        mPoint1 = new Points(0, mMeasuredHeight / 2 - outer_radius - DEFAULT_LINE_LENGTH);
        mPoint2 = new Points(outer_radius + DEFAULT_LINE_LENGTH, mMeasuredHeight / 2);

        Log.d(TAG, "onSizeChanged: aaaa" + mPoint1.getpX() + "," + mPoint1.getpY());
        Log.d(TAG, "onSizeChanged: bbb" + mPoint2.getpX() + "," + mPoint2.getpY());
        Log.d(TAG, "onSizeChanged: ccc" + mMeasuredHeight / 2);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAngleSquare(canvas);
        drawAngleLine(canvas);
    }

    //lsw 角度线和度数
    private void drawAngleLine(Canvas canvas) {
        canvas.drawLine(0, mMeasuredHeight / 2, mPoint1.getpX(), mPoint1.getpY(), paint);
        canvas.drawLine(0, mMeasuredHeight / 2, mPoint2.getpX(), mPoint2.getpY(), paint);
        canvas.save();
        canvas.rotate(90, outer_radius + DEFAULT_LINE_LENGTH, mMeasuredHeight / 2 - outer_radius);
        canvas.drawText(df.format(getAngle(mPoint1, mPoint2)), outer_radius + DEFAULT_LINE_LENGTH, mMeasuredHeight / 2 - outer_radius, paintDegreeNumber);
        canvas.restore();
    }

    //lsw 计算夹角值
    private float getAngle(Points p1, Points p2) {
        float pub = outer_radius + DEFAULT_LINE_LENGTH;
        double a = Math.sqrt((p1.getpX() - p2.getpX()) * (p1.getpX() - p2.getpX()) + (p1.getpY() - p2.getpY()) * (p1.getpY() - p2.getpY()));
        double cosa = (pub * pub + pub * pub - a * a) / (2 * pub * pub);
        float anglea = (float) (Math.acos(cosa) * 180 / Math.PI);
        return anglea;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float startx = motionEvent.getRawX();
                float starty = motionEvent.getRawY();
                Log.d(TAG, "onTouchaaaaa: " + startx + "---" + starty);
                mTouched = isTouchLine(startx, starty);
                if (!mTouched) {
                    return true;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float endx = motionEvent.getRawX();
                float endy = motionEvent.getRawY();
                if (mTouched) {
                    if (mCurrentLine == 1) {
                        Log.d(TAG, "onTouch: 1");
                        mPoint1 = calculateAngle(endx, endy);
                    } else {
                        Log.d(TAG, "onTouch: 2");
                        mPoint2 = calculateAngle(endx, endy);
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mTouched) {
                    if (mCurrentLine == 1) {
                        Log.d(TAG, "onTouch: 1");
                        mPoint1 = calculateAngle(motionEvent.getRawX(), motionEvent.getRawY());
                    } else {
                        Log.d(TAG, "onTouch: 2");
                        mPoint2 = calculateAngle(motionEvent.getRawX(), motionEvent.getRawY());
                    }
                }
                mTouched = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouched = false;
                break;

        }

        return true;
    }

    //lsw 计算改变后点坐标
    private Points calculateAngle(float x, float y) {
        Points po = new Points();
        double touchLength = Math.sqrt(x * x + (y - mMeasuredHeight / 2) * (y - mMeasuredHeight / 2));
        float changex = (float) ((outer_radius + DEFAULT_LINE_LENGTH) * x / touchLength);
        po.setpX(changex >= 0 ? changex : 0);
        float changey = 0f;
        if (y > mMeasuredHeight / 2) {
            changey = (float) ((outer_radius + DEFAULT_LINE_LENGTH) * (y - mMeasuredHeight / 2) / touchLength) + mMeasuredHeight / 2;
        } else if (y < mMeasuredHeight / 2) {
            changey = (float) -((outer_radius + DEFAULT_LINE_LENGTH) * (-y + mMeasuredHeight / 2) / touchLength) + mMeasuredHeight / 2;
        }
        po.setpY(changey);
        return po;
    }

    //lsw 触点在线上
    private boolean isTouchLine(float x, float y) {
        float k1 = (mPoint1.getpY() - mMeasuredHeight / 2) / (mPoint1.getpX());
        float k2 = (mPoint2.getpY() - mMeasuredHeight / 2) / (mPoint2.getpX());
        Log.d(TAG, "isTouchLine: " + k1 + "===" + k2);
        if (y <= k1 * x + mMeasuredHeight / 2 + OFFSET && y >= k1 * x + mMeasuredHeight / 2 - OFFSET) {
            mCurrentLine = 1;
            return true;
        } else if (y <= k2 * x + mMeasuredHeight / 2 + OFFSET && y >= k2 * x + mMeasuredHeight / 2 - OFFSET) {
            mCurrentLine = 2;
            return true;
        }
        if ((mPoint1.getpX() == 0 && x <= OFFSET) || mPoint1.getpX() <= OFFSET && x <= (mPoint1.getpX() + OFFSET)) {
            if (y < mMeasuredHeight / 2 && mPoint1.getpY() < mMeasuredHeight / 2 || y > mMeasuredHeight / 2 && mPoint1.getpY() > mMeasuredHeight / 2) {
                mCurrentLine = 1;
                return true;
            }
        }
        if (mPoint2.getpX() == 0 && x <= OFFSET || mPoint2.getpX() <= OFFSET && x <= (mPoint2.getpX() + OFFSET)) {
            if (y < mMeasuredHeight / 2 && mPoint2.getpY() < mMeasuredHeight / 2 || y > mMeasuredHeight / 2 && mPoint2.getpY() > mMeasuredHeight / 2) {
                mCurrentLine = 2;
                return true;
            }
        }
        mCurrentLine = 0;
        return false;
    }

    private void drawAngleSquare(Canvas canvas) {
        RectF outerRectF = new RectF(-outer_radius, mMeasuredHeight / 2 - outer_radius, outer_radius, mMeasuredHeight / 2 + outer_radius);//确定外切矩形范围
        canvas.drawArc(outerRectF, -90, 180, false, arcPaint);

        RectF middleRectF = new RectF(-middle_radius, mMeasuredHeight / 2 - middle_radius, middle_radius, mMeasuredHeight / 2 + middle_radius);
        canvas.drawArc(middleRectF, -90, 180, false, arcPaint);

        RectF innerRectF = new RectF(-inner_radius, mMeasuredHeight / 2 - inner_radius, inner_radius, mMeasuredHeight / 2 + inner_radius);
        canvas.drawArc(innerRectF, -90, 180, false, arcPaint);

        for (int i = 1; i <= 180; i++) {
            float r = (float) (i * Math.PI / 180f);
            float outer_startX;
            float outer_startY;
            float outer_endX;
            float outer_endY;
            float middle_startX;
            float middle_startY;
            float middle_endX;
            float middle_endY;

            outer_startY = mMeasuredHeight / 2 - (float) Math.cos(r) * outer_radius;
            outer_startX = Math.abs((float) Math.sin(r)) * outer_radius;
            float outer_degreeLength;
            float middle_degreeLength = 0f;
            if (i % 10 == 0) {
                outer_degreeLength = DEFAULT_LONGEST_DEGREE_LENGTH;
                middle_degreeLength = DEFAULT_LONG_DEGREE_LENGTH;
            } else if (i % 5 == 0) {
                outer_degreeLength = DEFAULT_LONG_DEGREE_LENGTH;
                middle_degreeLength = DEFAULT_SHORT_DEGREE_LENGTH;
            } else {
                outer_degreeLength = DEFAULT_SHORT_DEGREE_LENGTH;
            }
            outer_endY = mMeasuredHeight / 2 - (float) Math.cos(r) * (outer_radius - outer_degreeLength);
            outer_endX = Math.abs((float) Math.sin(r)) * (outer_radius - outer_degreeLength);
            canvas.drawLine(outer_startX, outer_startY, outer_endX, outer_endY, paintDegree);
            if (i % 5 == 0) {
                middle_startY = mMeasuredHeight / 2 - (float) Math.cos(r) * middle_radius;
                middle_startX = Math.abs((float) Math.sin(r)) * middle_radius;
                middle_endY = mMeasuredHeight / 2 - (float) Math.cos(r) * (middle_radius + middle_degreeLength);
                middle_endX = Math.abs((float) Math.sin(r)) * (middle_radius + middle_degreeLength);
                canvas.drawLine(middle_startX, middle_startY, middle_endX, middle_endY, paintDegree);
            }
            if (i % 10 == 0) {
                float middle_numY = mMeasuredHeight / 2 - (float) Math.cos(r) * (middle_radius + middle_degreeLength + 30);
                float middle_numX = Math.abs((float) Math.sin(r)) * (middle_radius + middle_degreeLength + 30);
                canvas.save();
                canvas.rotate(i, middle_numX, middle_numY);
                if (i > 90) {
                    canvas.drawText((180 - i) + "", middle_numX, middle_numY, middle_numPaint);
                } else {
                    canvas.drawText(i + "", middle_numX, middle_numY, middle_numPaint);
                }
                canvas.restore();
            }
            if (i % 10 == 0) {
                float outer_numY = mMeasuredHeight / 2 - (float) Math.cos(r) * (outer_radius - outer_degreeLength - 40);
                float outer_numX = Math.abs((float) Math.sin(r)) * (outer_radius - outer_degreeLength - 40);
                canvas.save();
                canvas.rotate(i, outer_numX, outer_numY);

                canvas.drawText((i) + "", outer_numX, outer_numY, numPaint);
                canvas.restore();
            }
        }
    }

    class Points {
        float pX;
        float pY;
        float angle;

        public Points(float pX, float pY) {
            this.pX = pX;
            this.pY = pY;
        }

        public Points() {

        }

        @Override
        public String toString() {
            return "pX=" + pX + ";pY=" + pY + ";angle=" + angle;
        }

        public void setpY(float pY) {
            this.pY = pY;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }

        public void setpX(float pX) {

            this.pX = pX;
        }

        public float getpX() {

            return pX;
        }

        public float getpY() {
            return pY;
        }

        public float getAngle() {
            return angle;
        }
    }
}
