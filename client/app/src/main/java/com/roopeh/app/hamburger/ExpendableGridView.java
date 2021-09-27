package com.roopeh.app.hamburger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

public class ExpendableGridView extends GridView {
    private boolean isExpanded = false;


    public ExpendableGridView(Context context) {
        super(context);
    }

    public ExpendableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpendableGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    final public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public void onMeasure(int width, int height) {
        if (isExpanded()) {
            final int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(width, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(width, height);
        }
    }
}
