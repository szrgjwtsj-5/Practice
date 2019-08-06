package com.whx.practice.threadpool.act;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.whx.practice.R;

import java.util.ArrayList;

public abstract class ImageBasePickerActivity extends BaseFragmentActivity {

    public static final String EXTRA_SELECT_LIMITS = "limits";
    public static final String EXTRA_SELECTED_IMAGES = "selected";
    public static final String EXTRA_COMPLETION_TEXT = "completion_text";
    public static final String EXTRA_CHOOSE_MODEL = "choose_mode";
    public static final String EXTRA_NEED_CROP = "need_crop";

    public static final String EXTRA_RESULT_IMAGES = "results";


    public static final String RESULT_PARAM_TAKE_PHOTO = "crop_source";
    public static final String EXTRA_RESULT_CROP_IMAGE = "crop_result_image";

    public static final String PARAM_CROP_DATA = "crop_photo";

    private static final int DEFAULT_LIMIT = 1;

    /**
     * 头图选择的requestCode
     */
    public static final int ACTIVITY_REQUEST_IMAGE_PICKER = 110;
    public static final int ACTIVITY_REQUEST_MTNB_IMAGE_PICKER = 112;
    /**
     * 选择头图之后裁剪时的requestCode
     */
    public static final int ACTIVITY_REQUEST_IMAGE_CROP = 111;


    /**
     * 商家介绍图选择的requestCode（多图无需裁剪）
     */
    public static final int ACTIVITY_REQUEST_SHOP_INTRO_IMAGE_PICKER = 120;


    /**
     * 产品介绍图选择的requestCode（多图无需裁剪）
     */
    public static final int ACTIVITY_REQUEST_PRODUCT_INTRO_IMAGE_PICKER = 130;



    protected int selectLimits;
    protected boolean isMultiChoose;
    protected boolean needCrop;
    protected ArrayList<Uri> initialSelectedImages;
    protected ArrayList<Uri> resultImages;
    protected String completionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = savedInstanceState;
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
        }

        if (extras != null) {
            initialSelectedImages = extras.getParcelableArrayList(EXTRA_SELECTED_IMAGES);
            selectLimits = extras.getInt(EXTRA_SELECT_LIMITS, DEFAULT_LIMIT);
            completionText = extras.getString(EXTRA_COMPLETION_TEXT);
            isMultiChoose = extras.getBoolean(EXTRA_CHOOSE_MODEL);
            needCrop = extras.getBoolean(EXTRA_NEED_CROP);
            if (!isMultiChoose){
                selectLimits = 1;
            }
            if (TextUtils.isEmpty(completionText)) {
                completionText = getString(R.string.image_picker_complete);
            }
            resultImages = extras.getParcelableArrayList(EXTRA_RESULT_IMAGES);

        }

        if (initialSelectedImages == null) {
            initialSelectedImages = new ArrayList<Uri>(0);
        }
        if (resultImages == null) {
            resultImages = new ArrayList<Uri>(initialSelectedImages);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_SELECT_LIMITS, selectLimits);
        outState.putParcelableArrayList(EXTRA_SELECTED_IMAGES, initialSelectedImages);
        outState.putParcelableArrayList(EXTRA_RESULT_IMAGES, resultImages);
        outState.putString(EXTRA_COMPLETION_TEXT, completionText);
        outState.putBoolean(EXTRA_CHOOSE_MODEL, isMultiChoose);
        outState.putBoolean(EXTRA_NEED_CROP, needCrop);
    }

    /**
     * 选中图片添加到选择数组
     * @param imageUri
     * @param isSelected
     * @return 返回是否成功添加
     */
    public boolean selectImage(Uri imageUri, boolean isSelected) {
        int index = resultImages.indexOf(imageUri);
        if (index >= 0) {
            if (!isSelected) {
                resultImages.remove(index);
            }
        } else {
            if (isSelected) {
                if (resultImages.size() >= selectLimits) {
                    String tips = isMultiChoose? getString(R.string.image_pick_count_prompt, selectLimits) : getString(R.string.image_pick_single_mode);
                    new AlertDialog.Builder(this)
                            .setMessage(tips)
                            .setPositiveButton(R.string.image_pick_count_prompt_ok, null)
                            .show();
                    return false;
                } else {
                    resultImages.add(imageUri);
                }
            }
        }

        return true;
    }

    public void finishWithResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            if (needCrop && !isMultiChoose){
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RESULT_PARAM_TAKE_PHOTO,resultImages.get(0));
                resultIntent.putExtra(EXTRA_NEED_CROP,needCrop);
                setResult(RESULT_OK,resultIntent);
            } else {
                Intent data = new Intent();
                data.putParcelableArrayListExtra(EXTRA_RESULT_IMAGES, resultImages);
                setResult(resultCode, data);
            }
        } else {
            setResult(resultCode);
        }

        finish();
    }
}
