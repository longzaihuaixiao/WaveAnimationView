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
    private int primaryColor = Color.BLACK;
    private int minorColor = Color.WHITE;
    private String text = "龙";
    private int mWidth, mHeight;
    private int waveWidth, waveHeight;
    private WaveDensity density = WaveDensity.LOW;
    private float currentPercent;

    private Paint primaryPaint;
    private Paint minorPaint;
    private Paint textPaint;

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

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            //获取一些xml参数;
            initAttrs(context, attrs);
        }

        primaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        primaryPaint.setStyle(Paint.Style.FILL);
        primaryPaint.setColor(primaryColor);
        primaryPaint.setDither(true);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);

        minorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minorPaint.setStyle(Paint.Style.FILL);
        minorPaint.setColor(minorColor);
        minorPaint.setDither(true);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(2 * 1000);
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


        for (int i = 0; i < density.value * 2; i++) {
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
        setMeasuredDimension(mWidth, mHeight);
        double x = 1.0 / density.value;
        waveWidth = (int) (mWidth * x);
        waveHeight = (int) (mHeight * (x / 2));
        textPaint.setTextSize(mWidth /2);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Wave);
        primaryColor = array.getColor(R.styleable.Wave_primary_color, primaryColor);
        minorColor = array.getColor(R.styleable.Wave_minor_color, minorColor);
        String tmp = array.getString(R.styleable.Wave_text);
        if (!TextUtils.isEmpty(tmp))
            text = tmp.substring(0,1);
        int des = array.getInt(R.styleable.Wave_density, density.value);
        if (des >= WaveDensity.HIGH.value) {
            density = WaveDensity.HIGH;
        } else if (des >= WaveDensity.MID.value && des < WaveDensity.HIGH.value) {
            density = WaveDensity.MID;
        } else {
            density = WaveDensity.LOW;
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

    public static enum WaveDensity {
        HIGH(4), MID(3), LOW(2);

        private int value;

        WaveDensity(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
