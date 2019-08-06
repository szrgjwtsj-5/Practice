package com.whx.practice.listgrid;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whx.practice.R;
import com.whx.practice.model.SubMenus;
import com.whx.practice.threadpool.act.CustomImageLoader;

import java.util.List;

/**
 * Created by whx on 2017/12/6.
 */

public class ReAdapter extends RecyclerView.Adapter<ReAdapter.MyViewHolder>{

    private List<SubMenus> data;

    public ReAdapter(List<SubMenus> data) {
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cooperation_grid_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.setView(data.get(position));
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView describe;
        private TextView isNew;
        private TextView subTitle;
        private ImageView verticalDivider;
        private ImageView horizontalDivider;

        MyViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.iv_grid_icon);
            describe = view.findViewById(R.id.tv_grid_describe);
            subTitle = view.findViewById(R.id.tv_grid_item_subtitle);
            isNew = view.findViewById(R.id.grid_item_new);
            verticalDivider = view.findViewById(R.id.divider_vertical);
            horizontalDivider = view.findViewById(R.id.divider_horizontal);
        }

        private void setView(SubMenus subMenus) {
            new CustomImageLoader.Builder(subMenus.iconUrl, itemView.getContext())
                    .placeHolder(R.drawable.leaf)
                    .build().loadImage(icon, null);

            describe.setText(subMenus.title);
            if (TextUtils.isEmpty(subMenus.subTitle)) {
                subTitle.setVisibility(View.GONE);
            } else {
                subTitle.setVisibility(View.VISIBLE);
                subTitle.setText(subMenus.subTitle);
            }
            if (subMenus.canHighLight) {
                isNew.setVisibility(View.VISIBLE);
            } else {
                isNew.setVisibility(View.GONE);
            }
        }
    }
}
