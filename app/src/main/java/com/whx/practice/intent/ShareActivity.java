package com.whx.practice.intent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.whx.practice.R;

/**
 * Created by whx on 2017/9/5.
 */

public class ShareActivity extends AppCompatActivity{

    private TextView shareText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        shareText = (TextView) findViewById(R.id.share_text);

        if (getIntent() != null) {
            String content = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            shareText.setText(content);
        }
    }
}
