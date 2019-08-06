package com.whx.practice.loader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.whx.practice.R;

/**
 * Created by whx on 2017/9/21.
 */

public class LoaderTestActivity extends AppCompatActivity implements AlbumSelectFragment.OnImageBucketSelectedListener{

    private View toolBar;
    private String curAlbumId, curAlbumName;
    private LoadImageFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common_fragment);

        initToolBar();

        FragmentManager fm = getSupportFragmentManager();
        fragment = LoadImageFragment.getInstance(null);

        try {
            fm.beginTransaction().add(R.id.pic_container, fragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolBar() {
        toolBar = findViewById(R.id.tool_bar);
        ImageView menu = toolBar.findViewById(R.id.menu);
        menu.setImageDrawable(
                getResources().getDrawable(R.drawable.ic_add_white_24dp));
        menu.setOnClickListener(view -> {
            AlbumSelectFragment fragment = AlbumSelectFragment.getInstance(curAlbumId);
            getSupportFragmentManager().beginTransaction().add(R.id.pic_files_container, fragment).commitAllowingStateLoss();
        });
        ImageView back = toolBar.findViewById(R.id.back);
        back.setImageDrawable(
                getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        back.setOnClickListener(view -> finish());
    }

    @Override
    public void onItemSelected(String id, String name) {
        curAlbumId = id;
        curAlbumName = name;

        fragment.update(curAlbumId, curAlbumName);
    }
}
