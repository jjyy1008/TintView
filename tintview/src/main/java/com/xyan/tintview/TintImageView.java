package com.xyan.tintview;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

/**
 * Created by chenxueqing on 2017/7/12.
 */

public class TintImageView extends AppCompatImageView {

    @ColorInt
    private int normalColor;
    @ColorInt
    private int pressedColor;
    private boolean needAnim;
    private long animDuration;

    private ValueAnimator pressAnimator;
    private ValueAnimator upAnimator;
    private ArgbEvaluator argbEvaluator;

    private boolean isTouching;

    public TintImageView(Context context) {
        this(context, null);
    }

    public TintImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TintImageView);
        normalColor = ta.getColor(R.styleable.TintImageView_tiv_normal_color, Color.TRANSPARENT);
        pressedColor = ta.getColor(R.styleable.TintImageView_tiv_pressed_color, ContextCompat.getColor(getContext(), R.color.default_pressed_bg_color));
        needAnim = ta.getBoolean(R.styleable.TintImageView_tiv_need_anim, false);
        animDuration = ta.getInt(R.styleable.TintImageView_tiv_anim_duration, 120);
        ta.recycle();

        argbEvaluator = new ArgbEvaluator();
        setColorFilter(normalColor);
    }

    public TintImageView setNormalColorFilter(@ColorInt int normalColor) {
        this.normalColor = normalColor;
        setColorFilter(normalColor);
        return this;
    }

    public TintImageView setPressedColorFilter(@ColorInt int pressedColor) {
        this.pressedColor = pressedColor;
        return this;
    }

    public TintImageView setNeedAnim(boolean needAnim) {
        this.needAnim = needAnim;
        return this;
    }

    public TintImageView setAnimDuration(long duration) {
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
            case MotionEvent.ACTION_MOVE:
                if (!isPointInView(event.getX(), event.getY())) {
                    handleTouchAction(false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                handleTouchAction(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isPointInView(float x, float y) {
        return !(x < 0 || y < 0 || x > getWidth() || y > getHeight());
    }

    private void initAnimator() {
        if (pressAnimator == null) {
            pressAnimator = new ValueAnimator();
            pressAnimator.setDuration(animDuration);
            pressAnimator.setInterpolator(new LinearInterpolator());
            pressAnimator.setIntValues(normalColor, pressedColor);
            pressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentColor = (Integer) animation.getAnimatedValue();
                    setColorFilter(currentColor);
                }
            });
            pressAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isTouching) {
                        upAnimator.setEvaluator(argbEvaluator);
                        upAnimator.start();
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
        if (upAnimator == null) {
            upAnimator = new ValueAnimator();
            upAnimator.setDuration(animDuration);
            upAnimator.setInterpolator(new LinearInterpolator());
            upAnimator.setIntValues(pressedColor, normalColor);
            upAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentColor = (Integer) animation.getAnimatedValue();
                    setColorFilter(currentColor);
                }
            });
        }
    }

    private void stopAnim() {
        if (pressAnimator != null && pressAnimator.isRunning()) {
            pressAnimator.cancel();
        }
        if (upAnimator != null && upAnimator.isRunning()) {
            upAnimator.cancel();
        }
    }

    private void handleTouchAction(boolean isPress) {
        if (isTouching == isPress) {
            return;
        }
        isTouching = isPress;
        if (needAnim) {
            initAnimator();
            if (isPress) {
                stopAnim();
                pressAnimator.setEvaluator(argbEvaluator);
                pressAnimator.start();
            } else {
                if (!pressAnimator.isRunning()) {
                    upAnimator.setEvaluator(argbEvaluator);
                    upAnimator.start();
                }
            }
        } else {
            setColorFilter(isPress ? pressedColor : normalColor);
        }
    }
}
