package com.example.tienthanh.myapplication.Custom;

import android.content.Context;
import android.graphics.Typeface;

import android.util.AttributeSet;



public class MyEditText extends android.support.v7.widget.AppCompatEditText {
    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyEditText(Context context) {
        super(context);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/BLACK Personal Use.ttf");
            setTypeface(tf);
        }
    }
}