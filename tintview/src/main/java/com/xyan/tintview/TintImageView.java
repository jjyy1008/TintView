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

    private final String TAG = "TintImageView";

    @ColorInt
    private int normalColor;
    @ColorInt
    private int pressedColor;
    @ColorInt
    private int currentColor;
    private boolean needAnim;
    private long animMills;

    private ValueAnimator animator;
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
        pressedColor = ta.getColor(R.styleable.TintImageView_tiv_pressed_color, ContextCompat.getColor(getContext(), R.color.default_pressed_color));
        currentColor = normalColor;
        needAnim = ta.getBoolean(R.styleable.TintImageView_tiv_need_anim, false);
        animMills = ta.getInt(R.styleable.TintImageView_tiv_anim_time, 300);
        ta.recycle();

        setColorFilter(normalColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event.getAction());
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
        //second protect
        if (animator == null) {
            animator = new ValueAnimator();
            argbEvaluator = new ArgbEvaluator();
            animator.setDuration(animMills);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentColor = (Integer) animation.getAnimatedValue();
                    setColorFilter(currentColor);
                    if (currentColor == pressedColor && isTouching) {
//                        animation.cancel();
                    }
                }
            });
        }
    }

    private void handleTouchAction(boolean isPress) {
        isTouching = isPress;
        if (needAnim) {
            if (animator == null) {
                initAnimator();
            }
            if (!isTouching && animator.isRunning()) {
                Log.d(TAG, "return: ");
                return;
            }
            if (animator.isRunning()) {
                animator.cancel();
            }
            animator.setIntValues(currentColor, isPress ? pressedColor : normalColor);
            animator.setRepeatCount(isTouching ? 1 : 0);
            animator.setEvaluator(argbEvaluator);
            animator.start();
            Log.d(TAG, "anim start");
        } else {
            setColorFilter(isPress ? pressedColor : normalColor);
        }
    }
}
