package com.whx.practice;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.whx.practice.activity.PreferenceTestActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * Created by whx on 2017/8/31.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().add(android.R.id.content, MyFragment.getInstance()).commit();

        Log.e("-----------", System.getProperty("java.vm.version"));
    }

    @Override
    protected void onDestroy() {
//        Log.e("----------", "TestActivity is destroy");
        super.onDestroy();
    }

    public static class MyFragment extends Fragment {
        private Button hello;
        private ListView listView;
        private MyAdapter adapter;
        private ArrayList<String> data;
        private MyHandler handler;

        public static MyFragment getInstance() {
            return new MyFragment();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_list, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            hello = view.findViewById(R.id.hello);
            listView = view.findViewById(R.id.list);

            adapter = new MyAdapter();
            listView.setAdapter(adapter);

            handler = new MyHandler(this);
            data = new ArrayList<>();

            loadData();

            hello.setOnClickListener(v -> {
                ComponentName name = new ComponentName("com.whx.practice.activity", PreferenceTestActivity.class.getName());
                Intent intent = new Intent();
                intent.setComponent(name);

                startActivity(intent);
            });
        }

        private void loadData() {
            new Thread(() -> {
                for (int i = 0; i < 50; i++) {
                    data.add("number # " + i);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                listView.setAdapter(adapter);
                handler.sendEmptyMessage(233);

            }).start();
        }

        private static class MyHandler extends Handler {
            private WeakReference<MyFragment> reference;

            MyHandler(MyFragment fragment) {
                reference = new WeakReference<>(fragment);
            }

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 233) {
                    MyFragment fragment = reference.get();
                    if (fragment != null) {
                        fragment.adapter.addData(fragment.data);
                    }
                }

            }
        }
        private class MyAdapter extends BaseAdapter {
            private ArrayList<String> data = new ArrayList<>();

            public void addData(ArrayList<String> data) {
                this.data.addAll(data);
                notifyDataSetChanged();
            }

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;
                View view;
                if (convertView == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.activity_test, parent, false);

                    viewHolder = new ViewHolder(view);

                    view.setTag(viewHolder);
                } else {
                    view = convertView;
                    viewHolder = (ViewHolder) view.getTag();
                }

                viewHolder.text.setText(data.get(position));

                return view;
            }

            private class ViewHolder {
                TextView text;

                ViewHolder(View view) {
                    text = view.findViewById(R.id.text);
                }

            }
        }
    }

}
