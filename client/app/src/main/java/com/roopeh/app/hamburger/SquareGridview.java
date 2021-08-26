package com.roopeh.app.hamburger;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareGridview extends RelativeLayout {
    public SquareGridview(Context context) {
        super(context);
    }

    public SquareGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int width, int height) {
        super.onMeasure(width, width);
    }
}
