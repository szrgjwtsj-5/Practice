package com.whx.practice.threadpool.frag;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.whx.practice.R;


public class AbsoluteDialogFragment extends DialogFragment {

    public static final String ARG_TAG_POPUP = "popup";
    public static final String ARG_ANIMATION = "animation";
    public static final String ARG_GRAVITY = "gravity";
    public static final String ARG_HEIGHT = "height";
    public static final String ARG_WIDTH = "width";
    protected String popupName;

    public interface OnDialogDismissListener {
        void onDialogDismiss();
    }

    private OnDialogDismissListener dialogDismissListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getParentFragment() instanceof OnDialogDismissListener) {
            dialogDismissListener = (OnDialogDismissListener) getParentFragment();
        } else if (getTargetFragment() instanceof  OnDialogDismissListener) {
            dialogDismissListener = (OnDialogDismissListener) getTargetFragment();
        } else if (activity instanceof OnDialogDismissListener) {
            dialogDismissListener = (OnDialogDismissListener) activity;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dialogDismissListener!= null) {
            dialogDismissListener.onDialogDismiss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NO_TITLE, R.style.App_NoTitleBar);
        if (getArguments() != null) {
            popupName = getArguments().getString(ARG_TAG_POPUP);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        windowDeploy(dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getShowsDialog() && showWithPadding()) {
            int padding = (int) (8 * getResources().getDisplayMetrics().density);
            view.setPadding(padding, 0, padding, 0);
        }
    }

    protected boolean showWithPadding() {
        return false;
    }

    public void removeSelf() {
        if (getFragmentManager() == null) {
            return;
        }

        if (dialogDismissListener != null) {
            dialogDismissListener.onDialogDismiss();
        }

        if (getDialog() != null) {
            dismissAllowingStateLoss();
        } else {
            if (!TextUtils.isEmpty(popupName)) {
                getFragmentManager().popBackStack();
            }

            getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();

            if (getParentFragment() instanceof AbsoluteDialogFragment) {
                ((AbsoluteDialogFragment) getParentFragment()).removeSelf();
            }
        }
    }

    /**
     * 设置窗口显示
     */
    public void windowDeploy(Dialog dialog) {
        Window window = dialog.getWindow(); // 得到对话框
        window.setWindowAnimations(getArguments().containsKey(ARG_ANIMATION) ? getArguments().getInt(ARG_ANIMATION) : R.style.image_picker_folder_push_top); // 设置窗口弹出动画
        window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = getArguments() == null ? 0 : getArguments().getInt("x");
        wl.y = getArguments() == null ? 0 : getArguments().getInt("y");
        int activityHeight = getActivity().getWindow().getDecorView().getHeight();
        wl.width = getArguments() != null && getArguments().containsKey(ARG_WIDTH) ? getArguments().getInt(ARG_WIDTH) : ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = getArguments() != null && getArguments().containsKey(ARG_HEIGHT) ? getArguments().getInt(ARG_HEIGHT) : Math.min(activityHeight - wl.y, (int) (0.6 * activityHeight));
        wl.gravity = getArguments() != null && getArguments().containsKey(ARG_GRAVITY) ? getArguments().getInt(ARG_GRAVITY) : Gravity.START | Gravity.TOP;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        wl.dimAmount = 0.6f;
        window.setAttributes(wl);
    }
}

