package com.evil.waveanimationview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by LongYH on 2017/7/12.
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG            #
 * #                                                   #
 */

public class WaveView extends View {
    private int primaryColor = Color.BLACK;//主色，默认为黑色
    private int minorColor = Color.WHITE;//辅色，默认为白色
    private String text = "浪";//默认文字


    private int mWidth, mHeight;//获取控件的宽高
    private int waveWidth, waveHeight;//波浪的宽高。宽，波峰到波谷的x距离；高，波峰到波谷的y距离
    private WaveDensity density = WaveDensity.MID;//波浪的密度
    private WaveSpeed speed = WaveSpeed.MID;//波浪的速度
    private float currentPercent;//当前的进度

    private Paint primaryPaint;//主色画笔
    private Paint minorPaint;//辅色画笔
    private Paint textPaint;//文字画笔

    public WaveView(Context context) {
        super(context);
        init(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 进行初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            //获取参数;
            initAttrs(context, attrs);
        }

        //抗锯齿，样式，颜色，防抖动
        primaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        primaryPaint.setStyle(Paint.Style.FILL);
        primaryPaint.setColor(primaryColor);
        primaryPaint.setDither(true);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);//粗体
        textPaint.setTextAlign(Paint.Align.CENTER);//中央对齐
        textPaint.setDither(true);

        minorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minorPaint.setStyle(Paint.Style.FILL);
        minorPaint.setColor(minorColor);
        minorPaint.setDither(true);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration((long) (speed.getValue() / 2.0 * 1000));
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPercent = animation.getAnimatedFraction();
                invalidate();
            }
        });
        animator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Path o = new Path();
        o.addCircle(mWidth / 2, mHeight / 2, mWidth / 2, Path.Direction.CCW);
        canvas.clipPath(o);

        drawCenterText(canvas);
        Path wavePath = getWavePath();
        canvas.drawPath(wavePath, primaryPaint);
        drawWaveText(canvas, wavePath);


        super.onDraw(canvas);
    }

    private Path getWavePath() {
        Path path = new Path();
        int x = (int) (currentPercent * mWidth) - mWidth;

        path.moveTo(x, mHeight / 2);

        for (int i = 0; i < density.getValue() * 2; i++) {
            if (i % 2 == 0)
                path.rQuadTo(waveWidth / 2, waveHeight / 2, waveWidth, 0);
            else
                path.rQuadTo(waveWidth / 2, -waveHeight / 2, waveWidth, 0);
        }
        path.lineTo(mWidth, mHeight);//画竖线
        path.lineTo(x, mHeight); //画横线

        path.close();//闭合
        return path;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }
        double x = 1.0 / density.getValue();
        waveWidth = (int) (mWidth * x);
        waveHeight = (int) (mHeight * (x / 2));
        textPaint.setTextSize(mWidth / 2);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Wave);
        primaryColor = array.getColor(R.styleable.Wave_primary_color, primaryColor);
        minorColor = array.getColor(R.styleable.Wave_minor_color, minorColor);
        String tmp = array.getString(R.styleable.Wave_text);
        if (!TextUtils.isEmpty(tmp))
            text = tmp.substring(0, 1);//只支持一个字
        //根据值划分密度档
        int des = array.getInt(R.styleable.Wave_density, density.getValue());
        if (des >= WaveDensity.HIGH.getValue()) {
            density = WaveDensity.HIGH;
        } else if (des <= WaveDensity.LOW.getValue()) {
            density = WaveDensity.LOW;
        } else {
            density = WaveDensity.MID;
        }
        //根据值划分速度档
        int sp = array.getInt(R.styleable.Wave_speed, speed.getValue());
        if (sp >= WaveSpeed.LOW.getValue()) {
            speed = WaveSpeed.LOW;
        } else if (sp <= WaveSpeed.HIGH.getValue()) {
            speed = WaveSpeed.HIGH;
        } else {
            speed = WaveSpeed.MID;
        }
        array.recycle();
    }

    private void drawCenterText(Canvas canvas) {
        textPaint.setColor(primaryColor);
        Rect rect = new Rect(0, 0, mWidth, mHeight);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;

        int centerY = (int) (rect.centerY() - top / 2 - bottom / 2);

        canvas.drawText(text, rect.centerX(), centerY, textPaint);
    }

    private void drawWaveText(Canvas canvas, Path wavePath) {
        textPaint.setColor(minorColor);
        Rect rect = new Rect(0, 0, mWidth, mHeight);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;

        int centerY = (int) (rect.centerY() - top / 2 - bottom / 2);

        canvas.clipPath(wavePath);
        canvas.drawText(text, rect.centerX(), centerY, textPaint);
    }

}
