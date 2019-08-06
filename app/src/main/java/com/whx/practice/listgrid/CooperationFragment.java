package com.whx.practice.listgrid;

/**
 * Created by whx on 2017/12/6.
 */


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whx.practice.R;
import com.whx.practice.model.CooperationBean;
import com.whx.practice.model.BaseData;
import com.whx.practice.threadpool.act.CollectionUtils;
import com.whx.practice.utils.CommonUtil;


public class CooperationFragment extends Fragment {

    private ListView listView;
    private CooperationAdapter cooperationAdapter;
    private ListAdapter adapter;

    private String fileName = "list";
    private List<CooperationBean> data;

    @Override
    public void onViewCreated(View contentView, Bundle savedInstanceState) {
        listView = contentView.findViewById(R.id.cooperation_listview);

        loadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }

    private MyHandler handler;
    private void loadData() {
        handler = new MyHandler(this);
        Runnable runnable = () -> {
            String str = "";

            str = CommonUtil.readInputStreamToString(getResources().openRawResource(R.raw.list), "utf-8");

            Gson gson = new Gson();
            BaseData<List<CooperationBean>> baseData = gson.fromJson(str, new TypeToken<BaseData<List<CooperationBean>>>(){}.getType());
            data = baseData.data;

            handler.sendEmptyMessage(123);
        };

        new Thread(runnable).start();
    }

    private static class MyHandler extends Handler {
        private WeakReference<CooperationFragment> reference;

        MyHandler(CooperationFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 123) {
                CooperationFragment fragment = reference.get();
                if (fragment != null) {
                    fragment.initListView(fragment.data);
                }
            }
        }
    }

    private void initListView(List<CooperationBean> list) {

        if (CollectionUtils.isEmpty(list)) {

            return;
        }

        if (cooperationAdapter == null) {
            cooperationAdapter = new CooperationAdapter(getContext());
        }

//        if (adapter == null) {
//            adapter = new ListAdapter(list);
//        }
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
//        listView.setLayoutManager(manager);
//        listView.getRecycledViewPool().setMaxRecycledViews(0, 100);

        listView.setAdapter(cooperationAdapter);
        cooperationAdapter.setData(list);

    }

}

