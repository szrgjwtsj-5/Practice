package com.whx.practice.threadpool.act;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.whx.practice.R;

/**
 *
 * FragmentActvity 基类
 */
public abstract class BaseFragmentActivity extends AppCompatActivity {
    public final static String TAG = "BaseActivity";
    private ProgressDialog mProgressDialog;

    private String mLoadingMessage;
    private String mCurrentIntentAction;//当前activity的action

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoadingMessage = getString(R.string.data_loading_wait_please);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(mLoadingMessage);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 显示加载对话框
     */
    public void showProgressDialog() {
        mProgressDialog.show();
    }

    /**
     * 显示加载对话框
     */
    public void showProgressDialog(String msg) {
        if (!TextUtils.isEmpty(msg))
            mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    public ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {//松手时判断
            View v = getCurrentFocus();//当前拥有焦点的view
            if (isShouldHideKeyboard(v, ev)) {//是否要隐藏键盘
                hideKeyboard(v.getWindowToken());
            }
        }

        try {//忽略异常
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
        }
        return false;
    }

    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            v.getLocationOnScreen(l);//获取editText的坐标(相对屏幕的绝对坐标)
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getRawX() > left && event.getRawX() < right
                    && event.getRawY() > top && event.getRawY() < bottom);//是否点击其本身(RawXY也为相对于屏幕的绝对坐标,要与view的坐标类型对应)
        }
        return false;
    }

    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null)
                im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 打开activity
     *
     * @param action Activity action
     */
    public void startActivityByAction(String action, Bundle extras) {
        if (TextUtils.isEmpty(action))
            return;
        Intent intent = new Intent(action);
        if (extras == null)
            extras = new Bundle();
        if (!TextUtils.isEmpty(mCurrentIntentAction))
            extras.putString("KEY_INTENT_DATA_INTENT_TYPE", mCurrentIntentAction);
        intent.putExtras(extras);
        startActivity(intent);
    }

}
