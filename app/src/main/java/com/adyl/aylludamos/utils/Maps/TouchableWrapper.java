package com.adyl.aylludamos.utils.Maps;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.adyl.aylludamos.activity.MenuActivity;


/**
 * Created by guima on 27/01/2017.
 */

public class TouchableWrapper extends FrameLayout {

    public TouchableWrapper(Context context) {
        super(context);
    }

    public void setTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    private OnTouchListener onTouchListener;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                MenuActivity.mMapIsTouched = true;
                //onTouchListener.onTouch();
                break;
            case MotionEvent.ACTION_UP:
                MenuActivity.mMapIsTouched = false;
                //onTouchListener.onRelease();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    public interface OnTouchListener {
        public void onTouch();

        public void onRelease();
    }
}