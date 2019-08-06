package com.whx.practice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.whx.practice.R;

/**
 * Created by whx on 2017/9/8.
 */

public class FragmentActivity extends AppCompatActivity{

    public static final String[] DATA = new String[] {
            "this is a message",
            "I have a dream",
            "caution: a sb is over here"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);


    }

    private void showDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            ft.remove(prev);
        }
        MyDialogFragment dialog = MyDialogFragment.getInstance();

        //dialog.setTitle("test");
        dialog.show(ft, "dialog");
    }

}
