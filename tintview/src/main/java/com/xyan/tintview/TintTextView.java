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
    private long animDuration;

    private Drawable bgDrawable;

    private ValueAnimator pressTextAnimator;
    private ValueAnimator upTextAnimator;
    private ValueAnimator pressBgAnimator;
    private ValueAnimator upBgAnimator;
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
        pressedBgColor = ta.getColor(R.styleable.TintTextView_ttv_bg_pressed_color, Color.TRANSPARENT);
        normalTextColor = ta.getColor(R.styleable.TintTextView_ttv_text_normal_color, getCurrentTextColor());
        pressedTextColor = ta.getColor(R.styleable.TintTextView_ttv_text_pressed_color, ContextCompat.getColor(getContext(), R.color.default_pressed_text_color));
        needAnim = ta.getBoolean(R.styleable.TintTextView_ttv_need_anim, false);
        animDuration = ta.getInt(R.styleable.TintTextView_ttv_anim_duration, 150);

        ta.recycle();

        argbEvaluator = new ArgbEvaluator();

        setTextColor(normalTextColor);
        if (hasBg()) {
            setBgColorFilter(normalBgColor);
        }
    }

    public TintTextView setNormalTextColor(@ColorInt int normalTextColor) {
        this.normalTextColor = normalTextColor;
        setTextColor(normalTextColor);
        return this;
    }

    public TintTextView setPressedTextColor(@ColorInt int pressedTextColor) {
        this.pressedTextColor = pressedTextColor;
        return this;
    }

    public TintTextView setNormalBgColorFilter(@ColorInt int normalBgColor) {
        this.normalBgColor = normalBgColor;
        if (hasBg()) {
            setBgColorFilter(normalBgColor);
        }
        return this;
    }

    public TintTextView setPressedBgColorFilter(@ColorInt int pressedBgColor) {
        this.pressedBgColor = pressedBgColor;
        return this;
    }

    public TintTextView setNeedAnim(boolean needAnim) {
        this.needAnim = needAnim;
        return this;
    }

    public TintTextView setAnimDuration(long duration) {
        this.animDuration = duration;
        return this;
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
            pressTextAnimator.setDuration(animDuration);
            pressTextAnimator.setInterpolator(new LinearInterpolator());
            pressTextAnimator.setIntValues(normalTextColor, pressedTextColor);
            pressTextAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentColor = (Integer) animation.getAnimatedValue();
                    setTextColor(currentColor);
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
            upTextAnimator.setDuration(animDuration);
            upTextAnimator.setInterpolator(new LinearInterpolator());
            upTextAnimator.setIntValues(pressedTextColor, normalTextColor);
            upTextAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentColor = (Integer) animation.getAnimatedValue();
                    setTextColor(currentColor);
                }
            });
        }

        if (pressBgAnimator == null) {
            pressBgAnimator = new ValueAnimator();
            pressBgAnimator.setDuration(animDuration);
            pressBgAnimator.setInterpolator(new LinearInterpolator());
            pressBgAnimator.setIntValues(normalBgColor, pressedBgColor);
            pressBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (hasBg()) {
                        int currentColor = (Integer) animation.getAnimatedValue();
                        setBgColorFilter(currentColor);
                    }
                }
            });
            pressBgAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isTouching && hasBg()) {
                        upBgAnimator.setEvaluator(argbEvaluator);
                        upBgAnimator.start();
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

        if (upBgAnimator == null) {
            upBgAnimator = new ValueAnimator();
            upBgAnimator.setDuration(animDuration);
            upBgAnimator.setInterpolator(new LinearInterpolator());
            upBgAnimator.setIntValues(pressedBgColor, normalBgColor);
            upBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (hasBg()) {
                        int currentColor = (Integer) animation.getAnimatedValue();
                        setBgColorFilter(currentColor);
                    }
                }
            });
        }

    }

    private void setBgColorFilter(int color) {
        bgDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void stopAnim() {
        if (pressTextAnimator != null && pressTextAnimator.isRunning()) {
            pressTextAnimator.cancel();
        }
        if (upTextAnimator != null && upTextAnimator.isRunning()) {
            upTextAnimator.cancel();
        }
        if (pressBgAnimator != null && pressBgAnimator.isRunning()) {
            pressBgAnimator.cancel();
        }
        if (upBgAnimator != null && upBgAnimator.isRunning()) {
            upBgAnimator.cancel();
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
                if (hasBg()) {
                    pressBgAnimator.setEvaluator(argbEvaluator);
                    pressBgAnimator.start();
                }
            } else {
                if (!pressTextAnimator.isRunning()) {
                    upTextAnimator.setEvaluator(argbEvaluator);
                    upTextAnimator.start();
                }
                if (!pressBgAnimator.isRunning()) {
                    upBgAnimator.setEvaluator(argbEvaluator);
                    upBgAnimator.start();
                }
            }
        } else {
            setTextColor(isPress ? pressedTextColor : normalTextColor);
            if (hasBg()) {
                setBgColorFilter(isPress ? pressedBgColor : normalBgColor);
            }
        }
    }

    private boolean hasBg() {
        bgDrawable = getBackground();
        return !(bgDrawable == null);
    }
}
