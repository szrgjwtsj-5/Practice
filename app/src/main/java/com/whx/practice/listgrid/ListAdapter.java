package com.whx.practice.listgrid;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whx.practice.R;
import com.whx.practice.model.CooperationBean;

import java.util.List;

/**
 * Created by whx on 2017/12/6.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private List<CooperationBean> data;
    private RecyclerView.RecycledViewPool viewPool;

    public ListAdapter(List<CooperationBean> data) {
        this.data = data;

        viewPool = new RecyclerView.RecycledViewPool();
        viewPool.setMaxRecycledViews(0, 100);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cooperation_list_item, parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        holder.setView(data.get(position), position);
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView divider;
        private TextView title;
        private RecyclerView gridView;

        ListViewHolder(View view) {
            super(view);

            divider = view.findViewById(R.id.divider_grid);
            title = view.findViewById(R.id.tv_cooperation_title);
            gridView = view.findViewById(R.id.gv_cooperation_grid);
        }

        private void setView(CooperationBean bean, int position) {
            if (bean == null) {
                return;
            }
            title.setText(bean.title);

//            CooperationGridAdapter adapter = new CooperationGridAdapter(context);
//            adapter.setData(cooperationBean.subMenus);

            ReAdapter adapter = new ReAdapter(bean.subMenus);
            GridLayoutManager manager = new GridLayoutManager(itemView.getContext(), 2);
            manager.setRecycleChildrenOnDetach(true);
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
