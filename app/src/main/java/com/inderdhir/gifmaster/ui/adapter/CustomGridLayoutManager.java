package com.inderdhir.gifmaster.ui.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.inderdhir.gifmaster.util.Utils;

public class CustomGridLayoutManager extends GridLayoutManager {

    private static final int EXTRA_LAYOUT_SPACE_PORTRAIT = 1000;
    private static final int EXTRA_LAYOUT_SPACE_LANDSCAPE = 2000;
    private Context mContext;

    public CustomGridLayoutManager(final Context context, final int spanCount) {
        super(context, spanCount);
        init(context);
    }

    @Override
    protected int getExtraLayoutSpace(final RecyclerView.State state) {
        if (Utils.isLandscape(mContext)) {
            return EXTRA_LAYOUT_SPACE_LANDSCAPE;
        }
        return EXTRA_LAYOUT_SPACE_PORTRAIT;
    }

    private void init(Context context) {
        mContext = context;
    }
}
