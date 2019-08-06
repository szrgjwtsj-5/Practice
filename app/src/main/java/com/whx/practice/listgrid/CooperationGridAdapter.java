package com.whx.practice.listgrid;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whx.practice.R;
import com.whx.practice.model.SubMenus;
import com.whx.practice.threadpool.act.CustomImageLoader;


/**
 * Created by wangdan on 16/12/24.
 */
public class CooperationGridAdapter extends BaseListAdapter<SubMenus> {

    private Context context;

    public CooperationGridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cooperation_grid_item, parent, false);
            viewHolder = new GridViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_grid_icon);
            viewHolder.describe = (TextView) convertView.findViewById(R.id.tv_grid_describe);
            viewHolder.subTitle = (TextView) convertView.findViewById(R.id.tv_grid_item_subtitle);
            viewHolder.isNew = (TextView) convertView.findViewById(R.id.grid_item_new);
            viewHolder.verticalDivider = (ImageView) convertView.findViewById(R.id.divider_vertical);
            viewHolder.horizontalDivider = (ImageView) convertView.findViewById(R.id.divider_horizontal);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GridViewHolder) convertView.getTag();
        }

        SubMenus subMenus = getItem(position);

        if (viewHolder != null) {
            viewHolder.reset();
            viewHolder.setData(subMenus, position);
        }
        return convertView;
    }

    public class GridViewHolder {
        private ImageView icon;
        private TextView describe;
        private TextView isNew;
        private TextView subTitle;
        private ImageView verticalDivider;
        private ImageView horizontalDivider;
        public SubMenus subMenus;

        public void reset() {

            describe.setText("");
            subTitle.setVisibility(View.GONE);
            isNew.setVisibility(View.GONE);
            subMenus = null;
        }

        public void setData(SubMenus subMenus, int position) {

            if ((position + 1) % 2 == 0) {
                verticalDivider.setVisibility(View.GONE);
            } else {
                verticalDivider.setVisibility(View.VISIBLE);
            }

            int count = getCount();
            if (getRow(count) == getRow(position + 1)) { //判断是否最后一行，最后一行底部的分割线不展示
                horizontalDivider.setVisibility(View.GONE);
            } else {
                horizontalDivider.setVisibility(View.VISIBLE);
            }

            if (subMenus == null) {
                return;
            }
            this.subMenus = subMenus;

            new CustomImageLoader.Builder(subMenus.iconUrl, context)
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

        private int getRow(int count) {
            int row = count / 2;

            if (count % 2 > 0) {
                row++;
            }
            return row;
        }
    }
}
