package com.whx.practice.listgrid;

import android.widget.BaseAdapter;

import com.whx.practice.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: whx
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    private List<T> list = new ArrayList<>();

    /**
     * 增加数据
     */
    public void addData(List<T> list) {
        if (CommonUtil.isNotEmpty(list)) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 增加单个数据
     */
    public void addData(T data) {
        list.add(data);
        notifyDataSetChanged();
    }

    /**
     * 得到数据
     */
    public List<T> getData() {
        return list;
    }

    /**
     * 清空数据
     */
    public void clearData() {
        if (CommonUtil.isNotEmpty(list)) {
            list.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 设置数据(替换)
     */
    public void setData(List<T> list) {
        if (list != null) {
            this.list = list;
            notifyDataSetChanged();
        }
    }

    /**
     * 增加数据
     */
    public void appendData(List<T> list) {
        addData(list);
    }

    /**
     * 移除数据
     */
    public void removeData(T t) {
        if (list != null && list.contains(t)) {
            list.remove(t);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public T getItem(int i) {
        if (i >= 0 && i < list.size()) {
            return list.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
