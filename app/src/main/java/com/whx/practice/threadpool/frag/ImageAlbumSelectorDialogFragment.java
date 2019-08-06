package com.whx.practice.threadpool.frag;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.whx.practice.R;
import com.whx.practice.threadpool.act.CustomImageLoader;

import java.io.File;

public class ImageAlbumSelectorDialogFragment extends AbsoluteDialogFragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ARG_CURRENT_BUCKET = "current_bucket";
    private String currentSelectedBucket;
    private OnImageBucketSelectedListener selectedListener;
    //    private OnNoPermissionListener permissionListener;
    private static final int QUERY_IMAGE_BUCKET_LIST = -1;
    private SparseArray<Holder> loadingTask = new SparseArray<Holder>();
    private ListView mList;

    public static ImageAlbumSelectorDialogFragment newInstance(String albumId) {
        ImageAlbumSelectorDialogFragment fragment = new ImageAlbumSelectorDialogFragment();
        if (!TextUtils.isEmpty(albumId)) {
            Bundle args = new Bundle();
            args.putString(ARG_CURRENT_BUCKET, albumId);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getParentFragment() instanceof OnImageBucketSelectedListener) {
            selectedListener = (OnImageBucketSelectedListener) getParentFragment();
        } else if (getTargetFragment() instanceof OnImageBucketSelectedListener) {
            selectedListener = (OnImageBucketSelectedListener) getTargetFragment();
        } else if (activity instanceof OnImageBucketSelectedListener) {
            selectedListener = (OnImageBucketSelectedListener) activity;
        } else {
            throw new IllegalStateException(
                    "Activity, parent fragment or target fragment must implement ItemSelectedListener.");
        }


        if (getArguments() != null) {
            currentSelectedBucket = getArguments().getString(ARG_CURRENT_BUCKET);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectedListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_picker_album, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int result = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
//            if (permissionListener != null) {
//                permissionListener.onNoPermission();
//            }
            return;
        }
        getLoaderManager().initLoader(QUERY_IMAGE_BUCKET_LIST, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mList = (ListView) view.findViewById(R.id.filter_list);
        mList.setOnItemClickListener(this);
        mList.setSmoothScrollbarEnabled(true);
        mList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDisplayMetrics().heightPixels > 0 ? (int) (0.6 * getResources().getDisplayMetrics().heightPixels) : LinearLayout.LayoutParams.WRAP_CONTENT));
        view.findViewById(R.id.block_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }

        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == QUERY_IMAGE_BUCKET_LIST) {
            Uri bucketUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("distinct", "true").build();
            String[] projection = new String[]{MediaStore.Images.ImageColumns.BUCKET_ID + " AS " + MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};
            return new CursorLoader(getActivity(), bucketUri, projection, null, null, null);
        } else if (args.containsKey("bucketId")) {
            String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA};
            return new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.ImageColumns.BUCKET_ID + "=? AND " + MediaStore.Images.ImageColumns.DATA + ">'/0'", new String[]{args.getString("bucketId")}, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        if (loaderId == QUERY_IMAGE_BUCKET_LIST) {
            // 加载出图片集列表
            if (data != null && data.getCount() > 0) {//有数据则设置adapter
                if (getListAdapter() == null) {
                    ImageBucketAdapter adapter = new ImageBucketAdapter(getActivity(), data);
                    mList.setAdapter(adapter);
                } else {
                    ((ImageBucketAdapter) getListAdapter()).swapCursor(data);
                }
            } else {//无数据设置为null,否则new CursorAdapter时会有异常
                mList.setAdapter(null);
            }
        } else {
            Holder holder = loadingTask.get(loaderId);
            loadingTask.remove(loaderId);
            if (holder != null && data != null && data.moveToLast()) {
                // 设置Cover的缩略图和图片张数
                File imageFile = new File(data.getString(data.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                if (imageFile.exists()) {
                    new CustomImageLoader.Builder(imageFile, getContext())
                            .placeHolder(R.mipmap.ic_launcher)
                            .build().loadImage(holder.cover, null);
                    holder.count.setText("(" + data.getCount() + ")");
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderId = loader.getId();
        if (loaderId == QUERY_IMAGE_BUCKET_LIST) {
            if (getListAdapter() != null) {
                ((CursorAdapter) getListAdapter()).swapCursor(null);
            }
        } else {
            loadingTask.remove(loaderId);
        }
    }

    private void loadBucketInfo(int id, Bundle args, Holder holder) {
        loadingTask.put(id, holder);
        getLoaderManager().initLoader(id, args, this);
    }


    protected ListAdapter getListAdapter() {
        return mList.getAdapter();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (selectedListener != null) {
            Holder holder = (Holder) view.getTag();
            selectedListener.onItemSelected(holder.id, holder.name.getText().toString());
        }
        removeSelf();
    }

    public interface OnImageBucketSelectedListener {
        void onItemSelected(String id, String name);
    }

    public interface OnNoPermissionListener {
        void onNoPermission();
    }

    private class ImageBucketAdapter extends CursorAdapter {

        public ImageBucketAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.image_picker_folder_item, null);
            Holder holder = new Holder();
            holder.cover = (ImageView) view.findViewById(R.id.bucket_cover);
            holder.name = (TextView) view.findViewById(R.id.bucket_name);
            holder.count = (TextView) view.findViewById(R.id.image_count);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Holder holder = (Holder) view.getTag();
            holder.id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
            holder.name.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
            holder.count.setText(null);

            view.findViewById(R.id.image_selected).setVisibility(
                    (holder.id != null && holder.id.equals(currentSelectedBucket)) ? View.VISIBLE : View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("bucketId", holder.id);
            loadBucketInfo(cursor.getPosition(), bundle, holder);
        }
    }

    private static final class Holder {
        String id;
        ImageView cover;
        TextView name;
        TextView count;
    }
}

