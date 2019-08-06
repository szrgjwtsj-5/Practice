package com.whx.practice.intent;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.whx.practice.R;


/**
 * Created by whx on 2017/9/4.
 */

public class TestIntentActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    //static final Uri mLocationForPhotos;

    private Button openText;
    private Button alarmBtn;
    private Button captureBtn;
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_intent);

        initView();

        addListener();
    }

    private void addListener() {
        //测试隐式Intent
        openText.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "hhhhh");
            sendIntent.setType("text/plain");

            String title = "分享应用选择";
            Intent chooser = Intent.createChooser(sendIntent, title);

            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
        });

        alarmBtn.setOnClickListener(view -> {
            TimePickerDialog pickerDialog = new TimePickerDialog(this, (timePicker, hour, minutes) -> {
                Toast.makeText(TestIntentActivity.this, "i = " + hour + " i1 = " + minutes, Toast.LENGTH_SHORT).show();
                createAlarm("闹钟", hour, minutes);
            }, 24, 60, true);
            pickerDialog.show();
        });

        captureBtn.setOnClickListener(view -> startActivity(new Intent(this, PhotoIntentActivity.class)));
    }
    private void initView() {
        openText = (Button) findViewById(R.id.open_text);
        alarmBtn = (Button) findViewById(R.id.alarm);
        captureBtn = (Button) findViewById(R.id.capture);
        mImageView = (ImageView) findViewById(R.id.image);
    }

    /**
     * 测试使用Intent创建闹钟
     *
     * @param message 用于标识闹钟的自定义消息
     * @param hour    闹钟的小时
     * @param minutes 闹钟的分钟
     */
    private void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                .putExtra(AlarmClock.EXTRA_RINGTONE, AlarmClock.VALUE_RINGTONE_SILENT)
                .putExtra(AlarmClock.EXTRA_VIBRATE, true);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "没有匹配的Activity", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            mImageView.setImageBitmap(bitmap);

        }
    }
}
