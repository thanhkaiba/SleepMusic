package com.example.tienthanh.myapplication;

import android.content.Context;
import android.graphics.Typeface;

import android.util.AttributeSet;



public class DigitalTextView extends android.support.v7.widget.AppCompatTextView {
    public DigitalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DigitalTextView(Context context) {
        super(context);
        init();
    }

    public DigitalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/digital-7.ttf");
            setTypeface(tf);
        }
    }
}