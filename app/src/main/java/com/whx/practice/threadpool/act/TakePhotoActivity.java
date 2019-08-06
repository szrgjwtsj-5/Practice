package com.whx.practice.threadpool.act;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whx.practice.R;
import com.whx.practice.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TakePhotoActivity extends BaseFragmentActivity implements SurfaceHolder.Callback, View.OnClickListener, SensorEventListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String ACTION = "com.whx.practice.ACTION_TAKE_PHOTO";

    private static final String[] REQUEST_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int REQUEST_PERMISSIONS_CODE = 1;

    private static final String SAVED_STATE_REQUESTING_PERMISSION = "is_requesting_permission";
    private static final String SAVED_STATE_SHOW_RATIONAL = "is_show_rational";

    private SurfaceHolder surfaceHolder;
    private Camera camera;

    private LinearLayout takePhotoBlock;
    private LinearLayout photoPreviewBlock;
    private ImageView photoResult;
    private TextView cancelPreview;
    private TextView submitPreview;

    private SurfaceView surfaceView;
    private TextView flashLight;
    private View btnFlashLight;
    private ImageView bgCamera;
    private SensorManager sensorManager;
    private Sensor accelSensor;

    private Uri resultUri;
    private boolean isMultiChoose;
    private boolean needCrop;


    private boolean usingBackCamera = true;
    private int sensorOrientation;
    private OrientationEventListener orientationEventListener;

    private boolean isResumed;
    private boolean isRquestingPermission;
    private boolean shouldShowRational;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.image_picker_activity_take_picture);
        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (sensorOrientation != orientation) {
                    sensorOrientation = orientation;
                    setupCameraRoationParameter();
                }
            }
        };

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            isMultiChoose = extras.getBoolean(ImagePickActivity.EXTRA_CHOOSE_MODEL);
            needCrop = extras.getBoolean(ImagePickActivity.EXTRA_NEED_CROP);
        }

        fetchView();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        initData();
        setUpListener();

        if (savedInstanceState != null) {
            isRquestingPermission = savedInstanceState.getBoolean(SAVED_STATE_REQUESTING_PERMISSION, false);
            shouldShowRational = savedInstanceState.getBoolean(SAVED_STATE_SHOW_RATIONAL, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE_REQUESTING_PERMISSION, isRquestingPermission);
        outState.putBoolean(SAVED_STATE_SHOW_RATIONAL, shouldShowRational);
    }

    private void fetchView() {
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        flashLight = (TextView) findViewById(R.id.flash_light);
        bgCamera = (ImageView) findViewById(R.id.bg_camera);
        btnFlashLight = findViewById(R.id.btn_flash_light);
        takePhotoBlock = (LinearLayout) findViewById(R.id.image_picker_take_photo);
        photoPreviewBlock = (LinearLayout) findViewById(R.id.image_picker_photo_preview);
        photoResult = (ImageView) findViewById(R.id.image_picker_result_photo);
        cancelPreview = (TextView) findViewById(R.id.btn_preview_cancel);
        submitPreview = (TextView) findViewById(R.id.btn_preview_complete);
    }


    private void initData() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setKeepScreenOn(true);
    }

    private void setUpListener() {
        findViewById(R.id.btn_complete).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_turn_camera).setOnClickListener(this);
        findViewById(R.id.btn_take_picture).setOnClickListener(this);
        findViewById(R.id.btn_flash_light).setOnClickListener(this);
        cancelPreview.setOnClickListener(this);
        submitPreview.setOnClickListener(this);
        surfaceView.setOnClickListener(this);
    }

    private void openCamera() {
        if (isRquestingPermission) {
            return;
        }
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            openCameraBypassPermission();
        } else {
            isRquestingPermission = true;
            shouldShowRational = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
            ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS, REQUEST_PERMISSIONS_CODE);
        }
    }

    private void openCameraBypassPermission() {
        camera = Camera.open();
        startCamera(camera);
        orientationEventListener.enable();
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            for (int i = 0; i < permissions.length; ++i) {
                String permission = permissions[i];
                if (Manifest.permission.CAMERA.equals(permission)) {
                    int result = grantResults[i];
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        isRquestingPermission = false;
                        if (isResumed) {
                            try {
                                openCameraBypassPermission();
                            } catch (Exception e) {
                                finish();
                                //Utils.msgShow(this, getString(R.string.image_picker_open_camera_failure));
                                return;
                            }
                        } else {
                            // will do in onResume
                        }

                    } else {
                        boolean currentShouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
                        if (shouldShowRational || shouldShowRational != currentShouldShow) {
                            finish();
                            return;
                        }
                        if (!currentShouldShow) {
                            new AlertDialog.Builder(this)
                                    .setMessage("未开启相机")
                                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // goto permission page
                                            isRquestingPermission = false;
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("不去", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        try {
            openCamera();
        } catch (Exception e) {
            finish();
            //Utils.msgShow(this, getString(R.string.image_picker_open_camera_failure));
            return;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
        if (camera != null) {
            sensorManager.unregisterListener(this, accelSensor);
            try {
                camera.stopPreview();
            } catch (Exception e) {
                //camera may already released
            }

            camera.release();
            camera = null;
        }

        orientationEventListener.disable();
    }

    private void startCamera(Camera camera) {

        int orientation = 90;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            orientation = ImageUtils.getCameraDisplayOrientation(TakePhotoActivity.this, cameraIndex);
        }

        try {
            //部分的三星手机会crash
            camera.setDisplayOrientation(orientation);
        } catch (Exception e) {
        }

        try {
            Camera.Parameters cameraParams = camera.getParameters();
            List<String> modes = cameraParams.getSupportedFlashModes();
            if (modes != null && modes.contains(Camera.Parameters.FLASH_MODE_ON) && modes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                btnFlashLight.setVisibility(View.VISIBLE);
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                flashLight.setText("关闭");
            } else {
                btnFlashLight.setVisibility(View.GONE);
            }
            Camera.Size perfectPreviewSize = ImageUtils.getPerfectSize(cameraParams.getSupportedPreviewSizes(), getResources().getDisplayMetrics().widthPixels);
            if (perfectPreviewSize != null) {
                cameraParams.setPreviewSize(perfectPreviewSize.width, perfectPreviewSize.height);
            }
            Camera.Size perfectPicSize = ImageUtils.getPerfectSize(cameraParams.getSupportedPictureSizes(), getResources().getDisplayMetrics().widthPixels);
            if (perfectPicSize != null) {
                cameraParams.setPictureSize(perfectPicSize.width, perfectPicSize.height);
            }

            cameraParams.setRotation(ImageUtils.getCameraRotationParameter(cameraIndex, sensorOrientation));

            camera.setParameters(cameraParams);

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            finish();
            //Utils.msgShow(this, getString(R.string.image_picker_open_camera_failure));
            return;
        }

    }

    private void setupCameraRoationParameter() {
        if (camera == null)
            return;
        try {
            Camera.Parameters cameraParams = camera.getParameters();

            cameraParams.setRotation(ImageUtils.getCameraRotationParameter(cameraIndex, sensorOrientation));

            camera.setParameters(cameraParams);
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera != null)
            startCamera(camera);
    }

    private void updateImage(Uri imgPath) {
        showPhotoPreviewUI(imgPath);
    }

    //照相UI
    private void showTakePhotoUI() {
        photoPreviewBlock.setVisibility(View.GONE);
        takePhotoBlock.setVisibility(View.VISIBLE);
    }


    //相片预览UI
    private void showPhotoPreviewUI(Uri uri) {
        new CustomImageLoader.Builder(uri, this)
                .build().loadImage(photoResult, null);
        photoPreviewBlock.setVisibility(View.VISIBLE);
        takePhotoBlock.setVisibility(View.GONE);
        resultUri = uri;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mAutoFocus) {
            setCameraFocus();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_flash_light) {
            if (camera != null) {

                Camera.Parameters parameters;
                try {
                    // MC-3008
                    parameters = camera.getParameters();
                } catch (Exception ex) {
                    parameters = null;
                }

                if (parameters != null && !TextUtils.isEmpty(parameters.getFlashMode())) {
                    if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_ON)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        flashLight.setText("关闭");
                    } else if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        flashLight.setText("开启");
                    }
                    try {
                        camera.setParameters(parameters);
                    } catch (Exception e) {
                    }
                } else {
                    //Utils.showSnackbar(TakePhotoActivity.this, R.string.camera_flash_mode_error);
                }
            }

        } else if (i == R.id.surface_view) {
            if (camera != null) {
                try {
                    if (mAutoFocus) {
                        setCameraFocus();
                    }
                } catch (Throwable t) {

                }
            }

        } else if (i == R.id.btn_turn_camera) {
            turnCamera();
        } else if (i == R.id.btn_cancel) {
            finish();
        } else if (i == R.id.btn_take_picture) {
            bgCamera.setBackgroundResource(R.drawable.ic_add_white_24dp);
            takePicture();
        } else if (i == R.id.btn_preview_complete) {
            if (needCrop && !isMultiChoose) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(ImagePickActivity.RESULT_PARAM_TAKE_PHOTO, resultUri);
                resultIntent.putExtra(ImagePickActivity.EXTRA_NEED_CROP, needCrop);
                setResult(RESULT_OK, resultIntent);
            } else {
                Intent data = new Intent();
                ArrayList<Uri> resultUriList = new ArrayList<>();
                resultUriList.add(resultUri);
                data.putExtra(ImagePickActivity.EXTRA_RESULT_IMAGES, resultUriList);
                setResult(RESULT_OK, data);
            }
            finish();
        } else if (i == R.id.btn_preview_cancel) {
            finish();
        }
    }

    private int cameraIndex = 0;


    private void restartCamera(int i) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        try {
            camera = Camera.open(i);
        } catch (Exception e) {
        }
        cameraIndex = i;
        startCamera(camera);
        usingBackCamera = !usingBackCamera;

    }

    private void turnCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (usingBackCamera) {
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        restartCamera(i);
                        break;
                    }
                } else {
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        restartCamera(i);
                        break;
                    }
                }

            }
        }
    }

    private boolean mInitialized = false;
    private float mLastX;
    private float mLastY;
    private float mLastZ;
    private boolean mAutoFocus = true;
    private Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean autoFocusSuccess, Camera arg1) {
            mAutoFocus = true;
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }
        float deltaX = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if (deltaX > .5 && mAutoFocus) { //AUTOFOCUS (while it is not autofocusing)
            setCameraFocus();
        }
        if (deltaY > .5 && mAutoFocus) { //AUTOFOCUS (while it is not autofocusing)
            setCameraFocus();
        }
        if (deltaZ > .5 && mAutoFocus) { //AUTOFOCUS (while it is not autofocusing) */
            setCameraFocus();
        }

        mLastX = x;
        mLastY = y;
        mLastZ = z;
    }

    private void setCameraFocus() {
        mAutoFocus = false;
        if (camera != null) {
            try {
                camera.autoFocus(myAutoFocusCallback);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class ImageThread extends Thread {
        private byte[] data;

        public ImageThread(byte[] data) {
            this.data = data;
        }

        @Override
        public void run() {
            super.run();

            if (ActivityCompat.checkSelfPermission(TakePhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                return;
            }

            long startTime = System.currentTimeMillis();
            File pictureFile = ImageUtils.getOutputMediaFile(UUID.randomUUID().toString() + ".jpg");
            if (pictureFile == null) {
                return;
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(pictureFile);
                fos.write(data);
            } catch (Throwable e) {
                //ignore
            } finally {
                IOUtils.close(fos);
            }

            final Uri uri = Uri.fromFile(pictureFile);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TakePhotoActivity.this.isFinishing()) {
                        updateImage(uri);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    }
                }
            });

        }
    }

    private void takePicture() {
        try {
            if (camera == null)
                return;

            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(final byte[] data, Camera camera) {
                    if (data != null && data.length > 0) {
                        new ImageThread(data).start();
                    }
                    // 拍照是异步的如果拍照较慢我们已经切到了后台那么可能出现此种情况
                    if (TakePhotoActivity.this.camera != null) {
                        try {
                            camera.startPreview();
                        } catch (Exception e) {
                            // preview失败了，可能此activity已经到了后台我们已经将camera释放了。
                        }
                    }
                }
            });
        } catch (Exception e) {
        }

    }


    public static Intent getTakePhotoActivity(boolean needCrop, boolean isMultiChoose) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(ImagePickActivity.EXTRA_NEED_CROP, needCrop);
        intent.putExtra(ImagePickActivity.EXTRA_CHOOSE_MODEL, isMultiChoose);
        return intent;
    }
}
