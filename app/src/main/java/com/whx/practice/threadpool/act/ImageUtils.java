package com.whx.practice.threadpool.act;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;

import com.whx.practice.utils.FileUtil;
import com.whx.practice.utils.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ImageUtils {
    public static final int DEFAULT_WIDTH = 1000;
    public static final int DEFAULT_HEIGHT = 1000;
    public static final int DEFAULT_QUALITY = 80;
    public static final String FILE_SCHEME = "file://";
    public static final String FILE_SCHEME_UPPERCASE = "FILE://";
    private final static CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static final String TAG = "ImageUtils";
    private static final String IMAGE_NAME_FORMAT = "image-%s.jpg";
    private static final int DEFAULT_SIZE = 1080;

    public static Camera.Size getPerfectSize(List<Camera.Size> list, int th) {
        Collections.sort(list, sizeComparator);

        Camera.Size size = null;
        for (Camera.Size s : list) {
            if ((s.width > th) && equalRate(s, 1.778f)) {
                size = s;
                break;
            }
        }
        if (size == null) {
            size = list.get(list.size() - 1);
        }

        return size;
    }

    private static boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.1) {
            return true;
        } else {
            return false;
        }
    }


    public static int getExifOrientation(Uri imgUri) {
        if (imgUri == null)
            return 0;
        return getExifOrientation(imgUri.getPath());
    }


    public static int getExifOrientation(String imgPath) {

        int orientation = 0;
        try {
            if (imgPath.startsWith(FILE_SCHEME) || imgPath.startsWith(FILE_SCHEME_UPPERCASE)) {
                imgPath = Uri.parse(imgPath).getPath();
            }
            ExifInterface exif = new ExifInterface(imgPath);
            String rotationAmount = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount)) {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        orientation = 0;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        orientation = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        orientation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        orientation = 270;
                        break;
                }
            }
        } catch (Exception ex) {
        }
        return orientation;
    }

    public static Bitmap getHugeImage(Context context, Uri imageUri) {
        Bitmap result = null;
        long freeMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
        // 如果内存够大，则超大图显示
        if (freeMemory >= 16 * 1024 * 1024) {
            result = getBitmapAsLargeAsPossible(context, imageUri, 1, 4);
        }
        // 没能加载超大图，则按原来的逻辑加载
        if (result == null) {
            result = getCompressBitmapByUri(context, imageUri, 640, 1000);
        }
        return result;
    }

    public static final Bitmap getCompressBitmapByUri(Context context, Uri uri, int reqWith, int reqHeight) {
        BitmapFactory.Options options = getBoundOptionByUri(context, uri);
        if (options == null) {
            return null;
        }
        int sampleSize = findSampleSizeSmallerThanDesire(options.outWidth, options.outHeight, reqWith, reqHeight);
        return getBitmapByUri(context, uri, options, sampleSize);
    }

    /**
     * 算出图片实际大小与imageView的比例
     *
     * @param view
     * @param bitmap
     * @return
     */
    public static float getScale(View view, Bitmap bitmap) {
        float scale = 1.0f;
        if (view != null && bitmap != null) {
            float photoViewWidth = view.getMeasuredWidth();
            float photoViewHeight = view.getMeasuredHeight();
            float bitmapWidth = bitmap.getWidth();
            float bitmapHeight = bitmap.getHeight();
            float widthScale = 1.0f, heightScale = 1.0f;
            if (photoViewWidth != 0) {
                widthScale = bitmapWidth / photoViewWidth;
            }
            if (photoViewHeight != 0) {
                heightScale = bitmapHeight / photoViewHeight;
            }
            scale = Math.max(widthScale, heightScale);
            if (bitmapWidth < photoViewWidth) {
                scale = scale / widthScale;
            }
        }
        return scale;
    }

    public static final File getCompressBitmapFileByUri(Context context, Uri uri, int reqWith, int reqHeight) {
        return getCompressBitmapFileByUri(context, uri, reqWith, reqHeight, DEFAULT_QUALITY);
    }

    /**
     * 注意,此方法耗时较多,请在主线程之外的线程执行
     *
     * @param uri
     * @param reqWith
     * @param reqHeight
     * @return
     */
    public static final File getCompressBitmapFileByUri(Context context, Uri uri, int reqWith, int reqHeight, int quality) {
        if (!FileUtil.isSdcardValid()) {
            return null;
        }
        Bitmap resultBitmap = getCompressBitmapByUri(context, uri, reqWith, reqHeight);
        if (resultBitmap == null) {
            return null;
        }
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String filename = String.format(IMAGE_NAME_FORMAT, UUID.randomUUID().toString());
        File resultFile = new File(path, filename);
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(resultFile));
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
        } catch (Exception e) {
            resultFile = null;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                resultFile = null;
            }
        }
        return resultFile;
    }

    public static final File getCompressBitmapFileByPath(Context context, String filePath, int reqWith, int reqHeight) {
        return getCompressBitmapFileByPath(context, filePath, reqWith, reqHeight, DEFAULT_QUALITY);
    }

    public static final File getCompressBitmapFileByPath(Context context, String filePath, int reqWith, int reqHeight, int quality) {
        if (!FileUtil.isSdcardValid()) {
            return null;
        }
        BitmapFactory.Options options = getBoundOptionByPath(filePath);
        int sampleSize = findSampleSizeSmallerThanDesire(options.outWidth, options.outHeight, reqWith, reqHeight);
        Bitmap resultBitmap = getBitmapByFilename(filePath, options, sampleSize);
        if (resultBitmap == null) {
            return null;
        }
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String filename = String.format(IMAGE_NAME_FORMAT, UUID.randomUUID().toString());
        File resultFile = new File(path, filename);
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(resultFile));
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
        } catch (Exception e) {
            resultFile = null;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                resultFile = null;
            }
        }
        return resultFile;
    }

    /**
     * 获取目标大小对应的取样值
     *
     * @param actualWidth  Actual width of the bitmap
     * @param actualHeight Actual height of the bitmap
     * @param desireWidth  Desired width of the bitmap
     * @param desireHeight Desired height of the bitmap
     */
    public static final int findSampleSizeSmallerThanDesire(
            int actualWidth, int actualHeight, int desireWidth, int desireHeight) {
        int wr = actualWidth / desireWidth;
        int hr = actualHeight / desireHeight;
        int ratio = Math.max(wr, hr);
        if (ratio <= 0) {
            ratio = 1;
        }
        return ratio;
    }

    private static Bitmap getBitmapByUri(Context context, Uri uri, BitmapFactory.Options options, int sampleSize) {
        String filePath = getPathFromUri(context, uri);
        return getBitmapByFilename(filePath, options, sampleSize);
    }

    private static Bitmap getBitmapByFilename(String filePath, BitmapFactory.Options options, int sampleSize) {
        Bitmap result = null;
        if (FileUtil.isFileExist(filePath)) {
            InputStream is = null;
            try {
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                is = new FileInputStream(filePath);
                Bitmap originalBitmap = BitmapFactory.decodeStream(is, null, options);
                // Determine the orientation for this image
                final int orientation = getExifOrientation(filePath);
                if (originalBitmap != null && orientation != 0) {
                    final Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);
                    Bitmap bitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(),
                            originalBitmap.getHeight(), matrix, true);
                    originalBitmap.recycle();
                    result = bitmap;
                } else {
                    result = originalBitmap;
                }
            } catch (FileNotFoundException e) {
            } catch (OutOfMemoryError oome) {
            } finally {
                IOUtils.close(is);
            }
        }
        return result;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String path = null;
        if (uri == null || context == null) {
            return "";
        }
        String uriScheme = uri.getScheme();
        if (uriScheme.equals("file")) {
            path = uri.getSchemeSpecificPart();
        } else if (uriScheme.equals("content")) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(0);
                }
                cursor.close();
            }
        }
        return path;
    }

    public static final Bitmap getBitmapAsLargeAsPossible(Context context, Uri uri, int startSampleSize, int limitSampleSize) {
        Bitmap result = null;
        String filePath = getPathFromUri(context, uri);
        if ((FileUtil.isFileExist(filePath))) {
            BitmapFactory.Options options = getBoundOptionByUri(context, uri);
            if (options == null) {
                return null;
            }
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;


            int inSampleSize = startSampleSize;
            do {
                options.inSampleSize = inSampleSize;
                InputStream is = null;
                try {
                    is = new FileInputStream(filePath);
                    Bitmap originalBitmap = BitmapFactory.decodeStream(is, null, options);

                    // Determine the orientation for this image
                    final int orientation = getExifOrientation(filePath);
                    if (originalBitmap != null && orientation != 0) {
                        final Matrix matrix = new Matrix();
                        matrix.postRotate(orientation);
                        Bitmap bitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(),
                                originalBitmap.getHeight(), matrix, true);
                        originalBitmap.recycle();
                        result = bitmap;
                    } else {
                        result = originalBitmap;
                    }
                } catch (FileNotFoundException e) {
                } catch (OutOfMemoryError oome) {
                } finally {
                    IOUtils.close(is);
                }
                if (result != null || inSampleSize >= limitSampleSize) {
                    break;
                }
                inSampleSize *= 2;
            } while (true);
        }
        return result;
    }

    public static final BitmapFactory.Options getBoundOptionByUri(Context context, Uri uri) {
        if (context == null || uri == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
        } catch (FileNotFoundException e) {
        } finally {
            IOUtils.close(is);
        }
        return options;
    }


    public static final BitmapFactory.Options getBoundOptionByPath(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options;
    }


    public static int getCameraRotationParameter(int cameraId, int orientation) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        orientation = (orientation + 45) / 90 * 90;

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing
            result = (info.orientation + orientation) % 360;
        }
        return result;
    }

    public static Uri getOutputMediaFileUri() {
        String fileName = UUID.randomUUID().toString();
        File outputMediaFile = getOutputMediaFile(fileName);
        return outputMediaFile == null ? null : Uri.fromFile(outputMediaFile);
    }


    public static File getOutputMediaFile(String fileName) {
        if (!FileUtil.isSdcardValid()) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "moma");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return mediaFile;
    }

    public static int getCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    static class CameraSizeComparator implements Comparator<Camera.Size> {
        //按升序排列
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getImageFilePath(Context context, ContentResolver contentResolver, Uri uri) {

        //android4.4特殊处理
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    split[1]
            };
            return getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
        }

        if (contentResolver == null || uri == null) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(index);
                }
            }
        } catch (Exception e) {
            return "";
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception ignore) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
