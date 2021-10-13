package com.roopeh.app.hamburger;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewDivider extends RecyclerView.ItemDecoration {
    final private int verticalDividerHeight;
    final private int horizontalDividerWidth;
    final private int span;

    public RecyclerViewDivider(int height) {
        verticalDividerHeight = height;
        horizontalDividerWidth = 0;
        span = 0;
    }

    public RecyclerViewDivider(int height, int width, int spanCount) {
        verticalDividerHeight = height;
        horizontalDividerWidth = width;
        span = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getAdapter() == null)
            return;

        if (span > 0) {
            final int column = parent.getChildAdapterPosition(view) % span;
            outRect.left = column * horizontalDividerWidth / span;
            outRect.right = horizontalDividerWidth - (column + 1) * horizontalDividerWidth / span;

            // Set bottom divider to correct items in grid
            final int itemCount = parent.getAdapter().getItemCount();
            if (parent.getChildAdapterPosition(view) < (itemCount - (span - column)))
                outRect.bottom = verticalDividerHeight;
        } else if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = verticalDividerHeight;
        }
    }
}
