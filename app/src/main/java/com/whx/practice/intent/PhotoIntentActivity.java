package com.whx.practice.intent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.whx.practice.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by whx on 2017/9/5.
 */

public class PhotoIntentActivity extends AppCompatActivity{

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final int ACTION_TAKE_VIDEO = 3;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;

    private static final String VIDEO_STORAGE_KEY = "viewvideo";
    private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
    private VideoView mVideoView;
    private Uri mVideoUri;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_photo);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mVideoView = (VideoView) findViewById(R.id.video_view);

        Button takeBigBtn = (Button) findViewById(R.id.take_big);
        setBtnListenerOrDisable(takeBigBtn, takeBigListener, MediaStore.ACTION_IMAGE_CAPTURE);

        Button takeSmallBtn = (Button) findViewById(R.id.take_small);
        setBtnListenerOrDisable(takeSmallBtn, takeSmallListener, MediaStore.ACTION_IMAGE_CAPTURE);

        Button takeVideoBtn = (Button) findViewById(R.id.take_video);
        setBtnListenerOrDisable(takeVideoBtn, takeVideoListener, MediaStore.ACTION_VIDEO_CAPTURE);


        mAlbumStorageDirFactory = new FroyoAlbumDirFactory();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B:
                if (resultCode == RESULT_OK) {
                    handleBigPhoto();
                }
                break;
            case ACTION_TAKE_PHOTO_S:
                if (resultCode == RESULT_OK) {
                    handleSmallPhoto(data);
                }
                break;
            case ACTION_TAKE_VIDEO:
                if (resultCode == RESULT_OK) {
                    handleVideo(data);
                }
        }
    }

    private void handleVideo(Intent intent) {
        mVideoUri = intent.getData();
        mVideoView.setVideoURI(mVideoUri);
        mImageBitmap = null;

        mVideoView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);
    }

    private void handleSmallPhoto(Intent data) {
        Bundle extras = data.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        mImageView.setImageBitmap(mImageBitmap);

        mVideoUri = null;
        mImageView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.GONE);
    }

    private void handleBigPhoto() {
        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }
    }

    /**
     * 显示大图
     */
    private void setPic(){
        //由于内存限制，需要对图片进行一定的缩放

        //获取imageView宽高
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        //获取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, options);

        int photoW = options.outWidth;
        int photoH = options.outHeight;

        //计算那个边需要缩放的更多
        int scale = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scale = Math.min(photoW/targetW, photoH/targetH);
        }

        //设置options 来缩放bitmap的解码目标
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        options.inPurgeable = true;

        //将JPEG格式图片解析成bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        mImageView.setImageBitmap(bitmap);

        mImageView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.GONE);
    }

    /**
     * 将图片添加到图库
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File file = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);

        sendBroadcast(mediaScanIntent);
    }

    /**
     * 根据action创建的Intent能否使用来为button设置点击事件或者不可用
     */
    private void setBtnListenerOrDisable(Button btn, Button.OnClickListener listener, String action) {
        if (isIntentAvailable(this, action)) {
            btn.setOnClickListener(listener);
        } else {
            btn.setClickable(false);
        }
    }

    /**
     * 判断使用指定Action的Intent能否找到要启动的组件
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }

    Button.OnClickListener takeBigListener = view -> dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
    Button.OnClickListener takeSmallListener = view -> dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);
    Button.OnClickListener takeVideoListener = view -> dispatchTakeVideoIntent(ACTION_TAKE_VIDEO);

    private void dispatchTakePictureIntent(int actionCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;
                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;
            default:
                break;
        }
        startActivityForResult(intent, actionCode);
    }

    private void dispatchTakeVideoIntent(int actionCode) {
        if (actionCode == ACTION_TAKE_VIDEO) {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
        }
    }

    private File setUpPhotoFile() throws IOException{
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
        String fileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(fileName, JPEG_FILE_SUFFIX, albumF);

        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "fail to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    private String getAlbumName() {
        return "wtf";
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);

        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null));

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);

        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? View.VISIBLE : View.INVISIBLE);

        mVideoView.setVideoURI(mVideoUri);
        mVideoView.setVisibility(savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ? View.VISIBLE : View.INVISIBLE);
    }
}
