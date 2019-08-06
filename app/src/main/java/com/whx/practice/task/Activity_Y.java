package com.whx.practice.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.whx.practice.R;

/**
 * Created by whx on 2017/9/6.
 */

public class Activity_Y extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common);

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(this.getClass().getSimpleName());

        Button jumpBtn = (Button) findViewById(R.id.jump);
        jumpBtn.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction("com.whx.testsupport.hhh");
            startActivity(intent);
        });
    }
}
