package com.whx.practice;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by whx on 2018/1/19.
 */

public class TestListView extends ListView {

    private DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            Log.e("------------", "what the fuck");
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    public TestListView(Context context) {
        super(context);
    }
    public TestListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TestListView(Context context, AttributeSet attrs, int defArr) {
        super(context, attrs, defArr);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (getAdapter() != null && observer != null) {
            getAdapter().unregisterDataSetObserver(observer);
        }
        super.setAdapter(adapter);

        System.gc();
        if (adapter != null) {

            adapter.registerDataSetObserver(observer);
            if (adapter instanceof BaseAdapter) {
                ((BaseAdapter) adapter).notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (getAdapter() != null) {
            getAdapter().unregisterDataSetObserver(observer);
        }
    }
}
