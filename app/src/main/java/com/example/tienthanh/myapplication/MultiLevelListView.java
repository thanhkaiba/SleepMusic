package com.example.tienthanh.myapplication;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class MultiLevelListView extends ListView {

    private boolean mEnableTouchIntercept = true;
    private MultiLevelListView mParentListView;

    public MultiLevelListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultiLevelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiLevelListView(Context context) {
        super(context);
    }

    public void setParentListView(MultiLevelListView parentListView) {
        mParentListView = parentListView;
    }

    private void setTouchInterceptEnabled(boolean value) {
        mEnableTouchIntercept = value;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mParentListView != null) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                // Disable intercepting touch to allow children to scroll
                mParentListView.setTouchInterceptEnabled(false);
            } else if (ev.getAction() == MotionEvent.ACTION_UP ||
                    ev.getAction() == MotionEvent.ACTION_CANCEL) {
                // Re-enable after children handles touch
                mParentListView.setTouchInterceptEnabled(true);
            }
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mEnableTouchIntercept) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}