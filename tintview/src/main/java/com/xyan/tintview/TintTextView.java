package com.xyan.tintview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by chenxueqing on 2017/7/13.
 */

public class TintTextView extends AppCompatTextView {

    public TintTextView(Context context) {
        this(context, null);
    }

    public TintTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
    }
}
