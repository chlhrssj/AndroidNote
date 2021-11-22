package com.rssj.androidnote.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.rssj.androidnote.R;


/**
 * Create by rssj on 2019-09-03
 */
public class CustomDialog extends Dialog {

    private View view;
    private Context context;
    private int layout = Gravity.BOTTOM;
    private int laywidth = WindowManager.LayoutParams.MATCH_PARENT;
    private int layheigh = WindowManager.LayoutParams.WRAP_CONTENT;

    public static void dissDialog(CustomDialog dialogView) {
        if (dialogView != null && dialogView.isShowing()) {
            dialogView.dismiss();
        }
    }

    public CustomDialog(Context context, View view) {
        this(context, view, Gravity.BOTTOM);
    }

    public CustomDialog(Context context, View view, int type) {
        this(context, view, type, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public CustomDialog(Context context, View view, int type, int lw, int lh) {
        super(context, R.style.dialog);
        this.context = context;
        this.view = view;
        this.layout = type;
        this.laywidth = lw;
        this.layheigh = lh;
    }

    public CustomDialog(Context context, View view, int style, int type, int lw, int lh) {
        super(context, style);
        this.context = context;
        this.view = view;
        this.layout = type;
        this.laywidth = lw;
        this.layheigh = lh;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);//这行一定要写在前面
        Window window = this.getWindow();
        window.setGravity(layout);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = laywidth;
        params.height = layheigh;
        window.setAttributes(params);
    }

    @Override
    public void dismiss() {
        View view = getCurrentFocus();
        if (view instanceof TextView) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }

        super.dismiss();
    }

    /**
     * 设置动画
     */
    public void setWindowAnimation(int animRes){
        Window window = getWindow();
        window.setWindowAnimations(animRes);
    }

}