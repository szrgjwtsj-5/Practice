package com.whx.practice.threadpool.act;

/**
 * Created by whx on 2017/11/24.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.whx.practice.MyApplication;
import com.whx.practice.R;
import com.whx.practice.threadpool.MyCursorLoader;
import com.whx.practice.threadpool.frag.ImageAlbumSelectorDialogFragment;


import java.io.File;
import java.util.ArrayList;

public class ImagePickActivity extends ImageBasePickerActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ImageAlbumSelectorDialogFragment.OnImageBucketSelectedListener, View.OnClickListener {
    public static final String ACTION = "com.whx.practice.ACTION_IMAGE_PICKER";

    private static final int REQUEST_CODE_VIEW_IMAGE = 1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 2;


    // UI elements
    private GridView imageGridView;
    private TextView btnPreview;
    private TextView btnComplete;
    private LinearLayout progressCotainer;
    private Toolbar toolbar;
    private String curAlbumId, curAlbumName;
    private ImageGridAdapter adapter;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        fetchView();
        if (savedInstanceState == null) {
            imageGridView.setEmptyView(getLayoutInflater().inflate(R.layout.image_picker_empty_image_view, null));
        }

        btnComplete.setOnClickListener(this);
        btnPreview.setOnClickListener(this);

        refreshPreviewButton();
        refreshCompleteButton();
        addActionBarImageAlbumButton();
        getSupportLoaderManager().initLoader(0, null, this);

        MyCursorLoader cursorLoader = new MyCursorLoader(MyApplication.getAppContext());

        Log.e("-----------", cursorLoader.toString());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_VIEW_IMAGE && data != null) {
                resultImages = data.getParcelableArrayListExtra(EXTRA_RESULT_IMAGES);
                if (adapter != null) {
                    adapter.notifyDataSetInvalidated();
                }
                refreshCompleteButton();
                refreshPreviewButton();
            } else if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RESULT_PARAM_TAKE_PHOTO, resultUri);
                resultIntent.putExtra(EXTRA_NEED_CROP, needCrop);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    private void fetchView() {
        imageGridView = (GridView) findViewById(R.id.gridview);
        btnPreview = (TextView) findViewById(R.id.btn_preview);
        btnComplete = (TextView) findViewById(R.id.btn_complete);
        toolbar = (Toolbar) findViewById(R.id.choose_photo_toolbar);
        progressCotainer = (LinearLayout) findViewById(R.id.progress_container);
    }

    private void addActionBarImageAlbumButton() {
        final LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.button_image_picker_album, null);
        Button actionBarRightButton = (Button) v.findViewById(R.id.text);
        actionBarRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.album);
                if (fragment instanceof ImageAlbumSelectorDialogFragment) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
                } else {
                    getSupportFragmentManager().beginTransaction().add(R.id.album, ImageAlbumSelectorDialogFragment.newInstance(curAlbumId)).commitAllowingStateLoss();
                }
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_back_navigation);
        toolbar.setTitle("选图");
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.PhotoChooserToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Gravity.RIGHT);
        toolbar.addView(v, params);
    }

    /**
     * 相册点选监听事件
     *
     * @param id   被选择的相册id
     * @param name 被选择的相册名
     */
    @Override
    public void onItemSelected(String id, String name) {
        curAlbumId = id;
        curAlbumName = name;
        //这里因为是添加之后的View为toolbar的第三个child
        if (toolbar != null && toolbar.getChildAt(2) != null) {
            ((Button) toolbar.getChildAt(2).findViewById(R.id.text)).setText(name);
        }
        getSupportLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA};
        //过滤掉无法显示的缓存图片
        String selection = MediaStore.Images.ImageColumns.DATA + " like '%.%' AND " + MediaStore.Images.Media.SIZE + " > 0 ";
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(curAlbumId) && !TextUtils.isEmpty(curAlbumName)) {
            selection = MediaStore.Images.ImageColumns.BUCKET_ID + "=? AND " + selection;
            selectionArgs = new String[]{curAlbumId};
        }

        String sort = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC, " + MediaStore.Images.ImageColumns._ID + " DESC";

        CursorLoader tmp = new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sort);
        return tmp;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (adapter == null) {
            adapter = new ImageGridAdapter(this, data);
            imageGridView.setAdapter(adapter);
        } else {
            adapter.swapCursor(data);
        }
        progressCotainer.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null) {
            adapter.swapCursor(null);
        }
        progressCotainer.setVisibility(View.GONE);
    }

    private void refreshCompleteButton() {
        String suffix = CollectionUtils.isEmpty(resultImages) ? "" : "(" + resultImages.size() + ")";
        btnComplete.setText(suffix + completionText);
        btnComplete.setEnabled(!CollectionUtils.isEmpty(resultImages));
        btnComplete.setTextColor(!CollectionUtils.isEmpty(resultImages) ? getResources().getColor(R.color.bg_blue) : getResources().getColor(R.color.black3));
    }

    private void refreshPreviewButton() {
        btnPreview.setEnabled(!CollectionUtils.isEmpty(resultImages));
        btnPreview.setTextColor(!CollectionUtils.isEmpty(resultImages) ? getResources().getColor(R.color.bg_blue) : getResources().getColor(R.color.black3));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_preview) {
            startPreviewActivity(0, true);
        } else if (i == R.id.btn_complete) {
            finishWithResult(RESULT_OK);
        }
    }

    private void startPreviewActivity(int position, boolean selectedMode) {
//        Intent intent = new Intent(this, ImagePreviewActivity.class);
//        intent.putExtra(ImagePreviewActivity.EXTRA_SELECT_MODE, selectedMode);
//        intent.putExtra(EXTRA_CHOOSE_MODEL, isMultiChoose);
//        intent.putParcelableArrayListExtra(ImagePreviewActivity.EXTRA_SELECTED_IMAGES, resultImages);
//        intent.putExtra(ImagePreviewActivity.EXTRA_SELECT_LIMITS, selectLimits);
//        if (!selectedMode) {
//            intent.putExtra(ImagePreviewActivity.EXTRA_ALBUM_ID, curAlbumId);
//            intent.putExtra(ImagePreviewActivity.EXTRA_ALBUM_NAME, curAlbumName);
//            intent.putExtra(ImagePreviewActivity.EXTRA_POS, position);
//        }
//        startActivityForResult(intent, REQUEST_CODE_VIEW_IMAGE);
    }

    private class ImageGridAdapter extends CursorAdapter {

        public ImageGridAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public int getCount() {
            return super.getCount() + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if (position > 0) {
                if (!mCursor.moveToPosition(position - 1)) {
                    throw new IllegalStateException("couldn't move cursor to position " + position);
                }

                if (convertView == null) {
                    v = newView(mContext, mCursor, parent);
                } else {
                    v = convertView;
                }

                bindView(v, mContext, mCursor);
            } else {
                if (convertView == null) {
                    v = newView(mContext, mCursor, parent);
                } else {
                    v = convertView;
                }

                bindView(v, mContext, null);
            }
            return v;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final ImageGridItem view = new ImageGridItem(context);
            view.setListener(new ImageGridItem.OnGridClickListener() {
                @Override
                public void onClick(View imageView, int position, long imageId, Uri imageUri) {
                    if (position > 0) {
                        if (isMultiChoose) {
                            startPreviewActivity(position - 1, false);
                        } else {
                            boolean selectResult = selectImage(imageUri, true);
                            if (selectResult) {
                                finishWithResult(RESULT_OK);
                            }
                        }
                    } else {
                        Intent takePhotoIntent = TakePhotoActivity.getTakePhotoActivity(needCrop, isMultiChoose);
                        takePhotoIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(takePhotoIntent);
                        finish();
                    }
                }

                @Override
                public void onSelect(CompoundButton selectView, int position, long imageId, Uri imageUri, boolean isSelected) {
                    if (!selectImage(imageUri, isSelected)) {
                        view.setPreventSelectListener(true);
                        selectView.setChecked(!isSelected);
                        view.setPreventSelectListener(false);
                    }

                    refreshPreviewButton();
                    refreshCompleteButton();
                }
            });
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (cursor != null) {
                long imageId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                File file = new File(imagePath);
                if (file.exists()) {
                    Uri imageUri = Uri.fromFile(file);
                    ((ImageGridItem) view).setData(cursor.getPosition() + 1, imageId, imageUri, resultImages.contains(imageUri), isMultiChoose);
                    // ((ImageGridItem) view).setData(cursor.getPosition() + 1, imageId, imageUri, mImageTaskManager.contains(imageUri));
                } else {
                    ((ImageGridItem) view).setData(cursor.getPosition() + 1, 0, null, false, isMultiChoose);

                }
            } else {
                ((ImageGridItem) view).setData(false);
            }
        }
    }

    public static Intent getImagePickActivity(int selectedLimits, String completionText,
                                              ArrayList<Uri> selectedImages,
                                              ArrayList<Uri> resultImages, boolean isMultiChoose, boolean needCrop) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_SELECT_LIMITS, selectedLimits);
        intent.putExtra(EXTRA_COMPLETION_TEXT, completionText);
        intent.putParcelableArrayListExtra(EXTRA_SELECTED_IMAGES, selectedImages);
        intent.putParcelableArrayListExtra(EXTRA_RESULT_IMAGES, resultImages);
        intent.putExtra(EXTRA_CHOOSE_MODEL, isMultiChoose);
        intent.putExtra(EXTRA_NEED_CROP, needCrop);
        return intent;
    }


    public static Intent getImagePickActivity(int selectedLimits, String completionText,
                                              boolean isMultiChoose, boolean needCrop) {
        return getImagePickActivity(selectedLimits, completionText, null, null, isMultiChoose, needCrop);
    }

}

