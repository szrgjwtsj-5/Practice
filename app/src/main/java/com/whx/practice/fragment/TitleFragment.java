package com.whx.practice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.whx.practice.R;

/**
 * Created by whx on 2017/9/11.
 */

public class TitleFragment extends ListFragment{
    private boolean mDualPane;
    private int mCurCheckPosition = 0;
    private String[] titles = new String[]{"hhhhh", "llllll", "mmmmm"};

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, titles));

        View detailFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailFrame != null && detailFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    private void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            getListView().setItemChecked(index, true);

            DetailFragment details = (DetailFragment) getFragmentManager().findFragmentById(R.id.details);

            if (details == null || details.getShownIndex() != index) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (index == 0) {
                    if (details != null) {
                        ft.remove(details);
                    }
                    //创建一个新Fragment显示内容
                    details = DetailFragment.newInstance(index);
                    ft.add(R.id.details, details);
                } else {
                    ft.remove(details);
                    details = DetailFragment.newInstance(index);
                    ft.add(R.id.details, details);
                }
                //切换动画
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }
}
