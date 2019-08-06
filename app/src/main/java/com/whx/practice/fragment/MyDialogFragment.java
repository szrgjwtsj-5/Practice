package com.whx.practice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.whx.practice.R;

/**
 * Created by whx on 2017/9/8.
 */

public class MyDialogFragment extends DialogFragment{

    private TextView mTitle;
    private EditText mEdit;
    private Button mOk;

    public static MyDialogFragment getInstance() {
        MyDialogFragment fragment = new MyDialogFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        mTitle = v.findViewById(R.id.title);
        mEdit = v.findViewById(R.id.edit_text);
        mOk = v.findViewById(R.id.ok);

        return v;
    }

    public void setTitle(String title) {
        if (title != null) {
            mTitle.setText(title);
        }
    }

}
