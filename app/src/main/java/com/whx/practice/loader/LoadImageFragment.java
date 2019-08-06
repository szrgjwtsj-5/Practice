package com.whx.practice.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.whx.practice.R;

import java.io.File;

/**
 * Created by whx on 2017/9/21.
 */

public class LoadImageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private GridView mGridView;
    private MyCursorAdapter mAdapter;

    private String curAlbumId, curAlbumName;

    public static LoadImageFragment getInstance(Bundle data) {
        LoadImageFragment fragment = new LoadImageFragment();
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(233, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        mGridView = view.findViewById(R.id.image_grid);

        Bundle data = getArguments();
        if (data != null) {
            curAlbumId = data.getString("album_id");
            curAlbumName = data.getString("album_name");
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA};
        //过滤掉无法显示的缓存图片
        String selection = MediaStore.Images.ImageColumns.DATA + " like '%.%' AND " + MediaStore.Images.ImageColumns.SIZE + " > 0";
        String[] selectArgs = null;

        if (!TextUtils.isEmpty(curAlbumId) && !TextUtils.isEmpty(curAlbumName)) {
            selection = MediaStore.Images.ImageColumns.BUCKET_ID + " = ? AND " + selection;
            selectArgs = new String[]{curAlbumId};
        }

        String sort = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC, " + MediaStore.Images.ImageColumns._ID + " DESC";

        return new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, selection, selectArgs, sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (mAdapter == null) {
            mAdapter = new MyCursorAdapter(getActivity(), data);
            mGridView.setAdapter(mAdapter);
        } else {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }

    public void update(String curAlbumId, String curAlbumName) {
        this.curAlbumId = curAlbumId;
        this.curAlbumName = curAlbumName;

        getLoaderManager().restartLoader(233, null, this);
    }

    private class MyCursorAdapter extends CursorAdapter {

        MyCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, false);
        }

        @Override
        public int getCount() {
            int fuck = super.getCount();
            return super.getCount() + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (position > 0) {
                if (!mCursor.moveToPosition(position - 1)) {
                    throw new IllegalStateException("couldn't move cursor to position " + position);
                }
                if (convertView == null) {
                    view = newView(mContext, mCursor, parent);
                } else {
                    view = convertView;
                }

                bindView(view, mContext, mCursor);
            } else {
                if (convertView == null) {
                    view = newView(mContext, mCursor, parent);
                } else {
                    view = convertView;
                }

                bindView(view, mContext, null);
            }

            return view;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final ImageView imageView = new ImageView(context);
            imageView.setOnClickListener(view -> {

            });

            return imageView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            if (cursor != null) {
                //long imageId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));

                Picasso.with(context)
                        .load(imagePath)
                        .into((ImageView)view);

            }
        }
    }
}
