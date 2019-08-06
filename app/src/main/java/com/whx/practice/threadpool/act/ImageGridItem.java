package com.whx.practice.threadpool.act;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.whx.practice.R;


public class ImageGridItem extends RelativeLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageView mImageView = null;
    private ImageView mCameraView = null;
    private CompoundButton mSecletView = null;
    private int position;
    private long imageId;
    private Uri imageUri;
    private OnGridClickListener listener;
    private boolean preventSelectListener;

    public ImageGridItem(Context context) {
        this(context, null, 0);
    }

    public ImageGridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageGridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.image_picker_grid_item, this);
        mImageView = (ImageView) findViewById(R.id.image);
        mCameraView = (ImageView) findViewById(R.id.camera);
        mSecletView = (CompoundButton) findViewById(R.id.select);
        mImageView.setOnClickListener(this);
        mCameraView.setOnClickListener(this);
        mSecletView.setOnCheckedChangeListener(this);
    }

    public void setData(boolean select) {
        position = 0;
        imageId = 0;
        imageUri = null;
        preventSelectListener = true;
        mSecletView.setChecked(select);
        preventSelectListener = false;
        mImageView.setVisibility(position == 0 ? GONE : VISIBLE);
        mCameraView.setVisibility(position != 0 ? GONE : VISIBLE);
        mSecletView.setVisibility(position == 0 ? GONE : VISIBLE);
    }


    public void setData(int position, long imageId, Uri imageUri, boolean select, boolean isMultiChoose) {
        this.position = position;
        this.imageId = imageId;
        this.imageUri = imageUri;
        new CustomImageLoader.Builder(imageUri, getContext())
                .placeHolder(R.mipmap.ic_launcher)
                .build().loadImage(mImageView, null);
        preventSelectListener = true;
        mSecletView.setChecked(select);
        preventSelectListener = false;
        mImageView.setVisibility(position == 0 ? GONE : VISIBLE);
        mCameraView.setVisibility(position != 0 ? GONE : VISIBLE);
        mSecletView.setVisibility(position == 0 || !isMultiChoose ? GONE : VISIBLE);
    }


    public void setListener(OnGridClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v, position, imageId, imageUri);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!preventSelectListener && listener != null && imageUri != null) {
            listener.onSelect(buttonView, position, imageId, imageUri, isChecked);
        }
    }

    public void setPreventSelectListener(boolean preventSelectListener) {
        this.preventSelectListener = preventSelectListener;
    }

    public interface OnGridClickListener {
        void onClick(View imageView, int position, long imageId, Uri imageUri);

        void onSelect(CompoundButton selectView, int position, long imageId, Uri imageUri, boolean isSelected);
    }
}