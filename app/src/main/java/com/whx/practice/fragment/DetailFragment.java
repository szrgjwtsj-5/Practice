package com.whx.practice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by whx on 2017/9/11.
 */

public class DetailFragment extends Fragment{

    public static DetailFragment newInstance(int index) {
        DetailFragment fragment = new DetailFragment();

        Bundle data = new Bundle();
        data.putInt("index", index);
        fragment.setArguments(data);

        return fragment;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (container == null) return null;

        ScrollView scroller = new ScrollView(getActivity());
        TextView text = new TextView(getActivity());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getActivity().getResources().getDisplayMetrics());

        text.setPadding(padding, padding, padding, padding);
        scroller.addView(text);
        text.setText(FragmentActivity.DATA[getShownIndex()]);

        return scroller;
    }
}
