package com.xyan.tintview;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

/**
 * Created by chenxueqing on 2017/7/13.
 */

public class TintTextView extends AppCompatTextView {

    @ColorInt
    private int normalBgColor;
    @ColorInt
    private int pressedBgColor;
    @ColorInt
    private int normalTextColor;
    @ColorInt
    private int pressedTextColor;
    private boolean needAnim;
    private long animMills;

    private Drawable bgDrawable;

    private ValueAnimator pressTextAnimator;
    private ValueAnimator upTextAnimator;
    private ArgbEvaluator argbEvaluator;

    private boolean isTouching;

    public TintTextView(Context context) {
        this(context, null);
    }

    public TintTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TintTextView);
        normalBgColor = ta.getColor(R.styleable.TintTextView_ttv_bg_normal_color, Color.TRANSPARENT);
        pressedBgColor = ta.getColor(R.styleable.TintTextView_ttv_bg_pressed_color, ContextCompat.getColor(getContext(), R.color.default_pressed_bg_color));
        normalTextColor = ta.getColor(R.styleable.TintTextView_ttv_text_normal_color, getCurrentTextColor());
        pressedTextColor = ta.getColor(R.styleable.TintTextView_ttv_text_pressed_color, ContextCompat.getColor(getContext(), R.color.default_pressed_text_color));
        needAnim = ta.getBoolean(R.styleable.TintImageView_tiv_need_anim, false);
        animMills = ta.getInt(R.styleable.TintImageView_tiv_anim_time, 150);



        ta.recycle();


        hasBg = !(getBackground() == null);

        getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleTouchAction(true);
                break;
            case MotionEvent.ACTION_UP:
                handleTouchAction(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                handleTouchAction(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void initAnimator() {
        if (pressTextAnimator == null) {
            pressTextAnimator = new ValueAnimator();
            pressTextAnimator.setDuration(animMills);
            pressTextAnimator.setInterpolator(new LinearInterpolator());
            pressTextAnimator.setIntValues(normalColor, pressedColor);
            pressTextAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentColor = (Integer) animation.getAnimatedValue();
                    setColorFilter(currentColor);
                }
            });
            pressTextAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isTouching) {
                        upTextAnimator.setEvaluator(argbEvaluator);
                        upTextAnimator.start();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (upTextAnimator == null) {
            upTextAnimator = new ValueAnimator();
            upTextAnimator.setDuration(animMills);
            upTextAnimator.setInterpolator(new LinearInterpolator());
            upTextAnimator.setIntValues(pressedColor, normalColor);
            upTextAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentColor = (Integer) animation.getAnimatedValue();
                    setColorFilter(currentColor);
                }
            });
        }
    }

    private void stopAnim() {
        if (pressTextAnimator != null && pressTextAnimator.isRunning()) {
            pressTextAnimator.cancel();
        }
        if (upTextAnimator != null && upTextAnimator.isRunning()) {
            upTextAnimator.cancel();
        }
    }

    private void handleTouchAction(boolean isPress) {
        isTouching = isPress;
        if (needAnim) {
            initAnimator();
            if (isPress) {
                stopAnim();
                pressTextAnimator.setEvaluator(argbEvaluator);
                pressTextAnimator.start();
            } else {
                if (!pressTextAnimator.isRunning()) {
                    upTextAnimator.setEvaluator(argbEvaluator);
                    upTextAnimator.start();
                }
            }
        } else {
            setColorFilter(isPress ? pressedColor : normalColor);
        }
    }

    private boolean hasBg() {
        bgDrawable = getBackground();
        return (bgDrawable == null);
    }

}
