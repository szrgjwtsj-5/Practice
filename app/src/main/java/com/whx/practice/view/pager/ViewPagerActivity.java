package com.whx.practice.view.pager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;
import com.whx.practice.R;
import com.whx.practice.utils.FileUtil;
import com.whx.practice.utils.ImageUtils;
import com.whx.practice.utils.MemoryUtils;
import com.whx.practice.view.leaf.UiUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by whx on 2017/9/26.
 */

public class ViewPagerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPager viewPager;
    private MyPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_pie_view);
//
//        checkView = (CheckView) findViewById(R.id.check_view);

        setContentView(R.layout.activity_test);

        Button start = findViewById(R.id.start);
        viewPager = findViewById(R.id.pager);

        adapter = new MyPagerAdapter(this, new ArrayList<>());
        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(3);

        viewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics()));
        viewPager.setPageTransformer(false, new GalleryTransformer());

        start.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hasPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                } else {
                    getSupportLoaderManager().initLoader(233, null, this);
                }
            } else {
                getSupportLoaderManager().initLoader(233, null, this);
            }
        });

//        ImageView imageView = findViewById(R.id.fuck);
//        File file = new File("/storage/emulated/0/com.inveno.hwread/Interest/nx.jpg");
//
//        Picasso.with(this).load(Uri.fromFile(file)).into(imageView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA};
        String selection = "(" + MediaStore.Images.ImageColumns.DATA + " like '%.jpg' OR " + MediaStore.Images.ImageColumns.DATA + " like '%.png' ) " + " AND " + MediaStore.Images.Media.SIZE + " > 0 ";
        String sort = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC, " + MediaStore.Images.ImageColumns._ID + " DESC limit 6";

        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<Uri> uris = new ArrayList<>();

        while (cursor.moveToNext()) {
            String str = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));

            File file = new File(str);
            if (file.exists()) {
                Uri imageUri = Uri.fromFile(file);
                uris.add(imageUri);
            }
        }

        adapter.setData(uris);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class MyPagerAdapter extends PagerAdapter {

        private List<Uri> data;
        private LayoutInflater inflater;

        MyPagerAdapter(Context context, List<Uri> data) {
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.item_two, container, false);
            ImageView image = view.findViewById(R.id.show_image_iv);

//            try {
//                long subfreeMem = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory();
//                long freeMem = Runtime.getRuntime().freeMemory();
//                Log.e("memory--------------", subfreeMem + " , " + freeMem);
//
//                Log.e("memory2-------------", "available: " + MemoryUtils.getAvailableMem(ViewPagerActivity.this) + " , total:" + MemoryUtils.getTotalMem());
//
//                Bitmap bitmap;
//                if (FileUtil.getFileSize(ViewPagerActivity.this, data.get(position)) > (1 << 20)) {             // 如果图片大于 5 M
//                    bitmap = ImageUtils.getBitmapCompressed(ViewPagerActivity.this, data.get(position));
//                } else {
//                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.get(position)));
//                }
//                image.setImageBitmap(UiUtils.getReverseBitmap(bitmap, 0.5f));
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            if (FileUtil.getFileSize(ViewPagerActivity.this, data.get(position)) > (1 << 20)) {
                Picasso.with(container.getContext())
                        .load(data.get(position))
                        .resize(480, 800)
                        .centerCrop()
                        .transform(new MirrorTransformation())
                        .into(image);
            } else {
                Picasso.with(container.getContext())
                        .load(data.get(position))
                        .transform(new MirrorTransformation())
                        .into(image);
            }


            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            if (object instanceof View) {
                ImageView image = ((View)object).findViewById(R.id.show_image_iv);
                image.setImageBitmap(null);
                container.removeView((View)object);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_UNCHANGED;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        public void setData(List<Uri> data) {
            this.data = data;
            notifyDataSetChanged();
        }

//        @Override
//        public float getPageWidth(int position) {
//            return 0.7f;
//        }
    }

    private class GalleryTransformer implements ViewPager.PageTransformer {
        private static final float MAX_ROTATION = 20.0F;
        private static final float MIN_SCALE = 0.75f;
        private static final float MAX_TRANSLATE = 20.0F;

        private int width = UiUtils.getScreenWidth(ViewPagerActivity.this);
        private boolean firstPage;      // 是否第一页
        private boolean firstTime = true;   // 是否第一遍执行
        private int count;      // 执行次数, 与setOffscreenPageLimit 有关

        GalleryTransformer() {
            firstPage = true;
        }

        // 第一页和第一遍的逻辑，用于处理一页显示多个Item 时，初始状态第二页的状态

        @Override
        public void transformPage(View page, float position) {
            if (count++ == 3) {
                firstTime = false;      // 第一遍执行完
            }

            float offset = (float)(width - page.getWidth()) / 2;        // 因为一页显示多个Item
            if (page.getWidth() != 0) {
                position = position - offset / page.getWidth();         // 所以要对position 进行相关偏移，否则将显示异常
            }

            if (position < -1.0) {                      // (-∞, -1)
                page.setTranslationX(MAX_TRANSLATE);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
                page.setRotationY(-MAX_ROTATION);
            } else if (firstPage || (!firstTime && position <= 1.0)) {      // [-1, 1]
                firstPage = false;

                page.setTranslationX(-MAX_TRANSLATE * position);
                float scale = MIN_SCALE + (1-MIN_SCALE) * (1.0f - Math.abs(position));
                page.setScaleX(scale);      // 缩放
                page.setScaleY(scale);
                page.setRotationY(MAX_ROTATION * position);     // 旋转

            } else if (firstTime || position > 1.0){        // (1, +∞)

                page.setTranslationX(-MAX_TRANSLATE);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
                page.setRotationY(MAX_ROTATION);
            }
        }
    }

    private class ScaleAlphaTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.70f;
        private static final float MIN_ALPHA = 0.5f;
        private int width = UiUtils.getScreenWidth(ViewPagerActivity.this);

        @Override
        public void transformPage(View page, float position) {

            float offset = (float)(width - page.getWidth()) / 2;

            position = position - offset / page.getWidth();

            if (position < -1 || position > 1) {
                page.setAlpha(MIN_ALPHA);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                if (position < 0) {
                    float scaleX = 1 + 0.3f * position;

                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                } else {
                    float scaleX = 1 - 0.3f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                }
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }
        }
    }

    private class RotateTransformer implements ViewPager.PageTransformer {
        private static final float MAX_ROTATION = 20.0F;

        @Override
        public void transformPage(View page, float position) {
            if (position < -1) {
                rotate(page, -MAX_ROTATION);
            } else if (position <= 1) {
                rotate(page, MAX_ROTATION * position);
            } else {
                rotate(page, MAX_ROTATION);
            }
        }

        private void rotate(View view, float rotation) {
            view.setPivotX(view.getWidth() * 0.5f);
            view.setPivotY(view.getHeight());
            view.setRotation(rotation);
        }
    }

    private class ScaleTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(View page, float position) {
            // 在左屏之外
            if (position < -1.0f) {
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
            // 向左滑
            else if (position <= 0.0f) {
                page.setAlpha(1.0f);
                page.setTranslationX(0.0f);
                page.setScaleY(1.0f);
                page.setTranslationY(1.0f);
            }
            //
            else if (position <= 1.0f) {
                page.setAlpha(1.0f - position);
                page.setTranslationX(-page.getWidth() * position);
                float scale = MIN_SCALE + (1.0f - MIN_SCALE) * (1.0f - position);
                page.setScaleX(scale);
                page.setScaleY(scale);
            }
            //
            else {
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSupportLoaderManager().initLoader(233, null, this);
            }
        }
    }


}
