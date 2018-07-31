package com.example.tienthanh.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class SongTextView extends android.support.v7.widget.AppCompatTextView {

        public SongTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        public SongTextView(Context context) {
            super(context);
            init();
        }

        public SongTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            if (!isInEditMode()) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
                setTypeface(tf);
            }
        }

}
