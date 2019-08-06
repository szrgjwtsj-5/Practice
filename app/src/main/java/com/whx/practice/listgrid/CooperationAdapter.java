package com.whx.practice.listgrid;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whx.practice.R;
import com.whx.practice.model.CooperationBean;


/**
 * Created by wangdan on 16/12/26.
 */
public class CooperationAdapter extends BaseListAdapter<CooperationBean> {


    private Context context;
    private RecyclerView.RecycledViewPool viewPool;

    public CooperationAdapter(Context context) {
        this.context = context;

        viewPool = new RecyclerView.RecycledViewPool();
        viewPool.setMaxRecycledViews(0, 100);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cooperation_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.divider = convertView.findViewById(R.id.divider_grid);
            viewHolder.title = convertView.findViewById(R.id.tv_cooperation_title);
            viewHolder.gridView = convertView.findViewById(R.id.gv_cooperation_grid);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CooperationBean cooperationBean = getItem(position);
        if (viewHolder != null) {
            viewHolder.reset();
            viewHolder.setData(cooperationBean, position);
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView divider;
        private TextView title;
        private RecyclerView gridView;

        public void reset() {
            title.setText("");
            gridView.setVisibility(View.GONE);
        }

        public void setData(CooperationBean cooperationBean, int position) {
            if (cooperationBean == null) {
                return;
            }
            title.setText(cooperationBean.title);

//            CooperationGridAdapter adapter = new CooperationGridAdapter(context);
//            adapter.setData(cooperationBean.subMenus);

            ReAdapter adapter = new ReAdapter(cooperationBean.subMenus);
            GridLayoutManager manager = new GridLayoutManager(context, 2);
            gridView.setLayoutManager(manager);
            gridView.setNestedScrollingEnabled(false);
            gridView.setRecycledViewPool(viewPool);
            gridView.setAdapter(adapter);
            gridView.setVisibility(View.VISIBLE);

            if (position != 0) {
                divider.setVisibility(View.VISIBLE);
            } else {
                divider.setVisibility(View.GONE);
            }
        }

    }
}
