package com.whx.practice;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.whx.practice.activity.PreferenceTestActivity;
import com.whx.practice.content.ContactTestActivity;
import com.whx.practice.countdown.TestCountdownActivity;
import com.whx.practice.fragment.FragmentActivity;
import com.whx.practice.intent.TestIntentActivity;
import com.whx.practice.leak.LeakActivity;
import com.whx.practice.loader.LoaderTestActivity;
import com.whx.practice.service.JobServiceActivity;
import com.whx.practice.service.TestServiceActivity;
import com.whx.practice.task.Activity_X;
import com.whx.practice.view.SimulateTouchActivity;
import com.whx.practice.view.BgFlashButton;
import com.whx.practice.view.pager.CycleViewpagerActivity;
import com.whx.practice.view.pager.ViewPagerActivity;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private Button toLeakBtn;
    private Button toIntentBtn;
    private Button toTaskBtn;
    private Button toFragment;
    private Button toLoader;
    private Button toView;
    private Button toAnother;
    private Button toTest;
    private Button toService;
    private Button toSchedule;
    private Button toContent;
    private Button toSetting;
    private Button toSimulate;
    private Button toCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            Log.e("--------- visibility = ", "" + visibility);
        });

        setContentView(R.layout.activity_main);

        initView();

        toLeakBtn.setOnClickListener(view -> startActivity(new Intent(this, LeakActivity.class)));
        toIntentBtn.setOnClickListener(view -> startActivity(new Intent(this, TestIntentActivity.class)));
        toTaskBtn.setOnClickListener(view -> startActivity(new Intent(this, Activity_X.class)));
        toFragment.setOnClickListener(view -> startActivity(new Intent(this, FragmentActivity.class)));
        toLoader.setOnClickListener(view -> startActivity(new Intent(this, LoaderTestActivity.class)));
        toView.setOnClickListener(view -> startActivity(new Intent(this, ViewPagerActivity.class)));
        toTest.setOnClickListener(view -> startActivity(new Intent(this, TestActivity.class)));

        toAnother.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("whx://test/hhh"));
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivityForResult(intent, 99);
        });

        toService.setOnClickListener(view -> startActivity(new Intent(this, TestServiceActivity.class)));
        toSchedule.setOnClickListener(view -> startActivity(new Intent(this, JobServiceActivity.class)));

        toContent.setOnClickListener(view -> startActivity(new Intent(this, ContactTestActivity.class)));
        toSetting.setOnClickListener(view -> startActivity(new Intent(this, PreferenceTestActivity.class)));

        toSimulate.setOnClickListener(view -> startActivity(new Intent(this, SimulateTouchActivity.class)));
        toCountdown.setOnClickListener(view -> startActivity(new Intent(this, TestCountdownActivity.class)));
    }

    private void initView() {
        toLeakBtn = findViewById(R.id.to_leak);
        toIntentBtn = findViewById(R.id.to_intent);
        toTaskBtn = findViewById(R.id.to_task);
        toFragment = findViewById(R.id.to_fragment);
        toLoader = findViewById(R.id.to_loader);
        toView = findViewById(R.id.to_view);
        toAnother = findViewById(R.id.to_another_app);
        toTest = findViewById(R.id.to_test);
        toService = findViewById(R.id.to_service);
        toSchedule = findViewById(R.id.to_schedule);
        toContent = findViewById(R.id.to_content);
        toSetting = findViewById(R.id.to_setting);
        toSimulate = findViewById(R.id.to_simulate);
        toCountdown = findViewById(R.id.to_countdown);

        ((BgFlashButton) findViewById(R.id.tmp_btn)).onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        Log.e("---------", requestCode + " " + resultCode);
    }

    @Override
    protected void onDestroy() {
//        Log.e("----------", "MainActivity is destroy");
        super.onDestroy();
    }

    private void setFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
