package com.whx.practice.loader;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.whx.practice.R;

import java.io.File;

/**
 * Created by whx on 2017/9/22.
 */

public class AlbumSelectFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mList;
    private static final String ARG_BUCKET = "current_bucket";
    private String currentSelectedBucket;
    private SparseArray<Holder> loadingTask = new SparseArray<>();
    private OnImageBucketSelectedListener selectedListener;

    public static AlbumSelectFragment getInstance(String albumId) {
        AlbumSelectFragment fragment = new AlbumSelectFragment();
        if (!TextUtils.isEmpty(albumId)) {
            Bundle args = new Bundle();
            args.putString(ARG_BUCKET, albumId);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof OnImageBucketSelectedListener) {
            selectedListener = (OnImageBucketSelectedListener) getActivity();
        } else {
            throw new IllegalStateException("activity 先实现OnImageBucketSelectedListener接口");
        }
        if (getArguments() != null) {
            currentSelectedBucket = getArguments().getString(ARG_BUCKET);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        windowDeploy(dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(666, null, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mList = view.findViewById(R.id.list);
        mList.setOnItemClickListener((adapterView, view1, position, id) -> {
            if (selectedListener != null) {
                Holder holder = (Holder) view1.getTag();
                selectedListener.onItemSelected(holder.id, holder.name.getText().toString());
            }
            removeSelf();
        });
        mList.setSmoothScrollbarEnabled(true);
        int height = getResources().getDisplayMetrics().heightPixels;
        mList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                height > 0 ? (int)(height*0.6) : LinearLayout.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == 666) {
            Uri bucketUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("distinct", "true").build();
            String[] projection = {MediaStore.Images.ImageColumns.BUCKET_ID + " AS " + MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};
            return new CursorLoader(getActivity(), bucketUri, projection, null, null, null);
        } else if (args.containsKey("bucketId")) {
            String[] projection = {MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA};
            String selection = MediaStore.Images.ImageColumns.BUCKET_ID + " = ? AND " + MediaStore.Images.ImageColumns.DATA + " > '/0' ";
            String[] selectArgs = {args.getString("bucketId")};

            return new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectArgs, null);
        }
        return null;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderId = loader.getId();
        if (loaderId == 666) {
            if (mList.getAdapter() != null) {
                ((CursorAdapter) mList.getAdapter()).swapCursor(null);
            }
        } else {
            loadingTask.remove(loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int loaderId = loader.getId();
        if (loaderId == 666) {
            // 加载出图片集列表
            if (data != null && data.getCount() > 0) {//有数据则设置adapter
                if (mList.getAdapter() == null) {
                    ImageCursorAdapter adapter = new ImageCursorAdapter(getActivity(), data);
                    mList.setAdapter(adapter);
                } else {
                    ((ImageCursorAdapter) mList.getAdapter()).swapCursor(data);
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
                    Picasso.with(getActivity())
                            .load(imageFile)
                            .into(holder.cover);
                    holder.count.setText("(" + data.getCount() + ")");
                }
            }
        }
    }

    public void removeSelf() {
        if (getFragmentManager() == null)
            return;

        if (getDialog() != null) {
            dismissAllowingStateLoss();
        } else {
            getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    private void loadBucketInfo(int id, Bundle args, Holder holder) {
        loadingTask.put(id, holder);
        getLoaderManager().initLoader(id, args, this);
    }

    private void windowDeploy(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setWindowAnimations(R.style.image_picker_folder_push_top);
        window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        WindowManager.LayoutParams wlp = window.getAttributes();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        wlp.dimAmount = 0.6f;
        window.setAttributes(wlp);
    }

    private class ImageCursorAdapter extends CursorAdapter {

        public ImageCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.image_picker_folder_item, null);
            Holder holder = new Holder();
            holder.cover = view.findViewById(R.id.bucket_cover);
            holder.name = view.findViewById(R.id.bucket_name);
            holder.count = view.findViewById(R.id.image_count);

            view.setTag(holder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Holder holder = (Holder) view.getTag();
            holder.id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
            holder.name.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
            holder.count.setText(null);

            view.findViewById(R.id.image_selected).setVisibility((holder.id != null && holder.id.equals(currentSelectedBucket)) ? View.VISIBLE : View.GONE);

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

    public interface OnImageBucketSelectedListener {
        void onItemSelected(String id, String name);
    }
}
