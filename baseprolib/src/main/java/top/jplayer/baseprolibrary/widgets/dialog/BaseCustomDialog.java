package top.jplayer.baseprolibrary.widgets.dialog;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import top.jplayer.baseprolibrary.R;
import top.jplayer.baseprolibrary.mvp.contract.IContract;
import top.jplayer.baseprolibrary.utils.ScreenUtils;
import top.jplayer.baseprolibrary.utils.StringUtils;


/**
 * Created by PEO on 2017/2/24.
 * d
 */

public abstract class BaseCustomDialog extends AlertDialog implements IContract.IView {

    public WeakReference<Context> mWeakReference;
    public View mContentView;


    public BaseCustomDialog(Context context) {
        this(context, R.style.dialog_custom);
        mWeakReference = new WeakReference<>(context);
    }

    public BaseCustomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContentView = View.inflate(context, initLayout(), null);
        initView(mContentView);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    protected abstract void initView(View view);


    public abstract int initLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        assert window != null;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = setAlpha();
        lp.width = setWidth(7);
        lp.height = setHeight();
        window.setGravity(setGravity());
        window.setWindowAnimations(setAnim());
        window.setAttributes(lp);
        window.setSoftInputMode(setSoftInputState());
        setCanceledOnTouchOutside(true);// 点击Dialog外部消失
        super.setContentView(mContentView);
    }

    public void bindText(String title, int id) {
        if (StringUtils.isEmpty(title)) {
            throw new RuntimeException("文本数据 不能为空");
        }
        TextView tv = mContentView.findViewById(id);
        tv.setText(title);
    }

    public float setAlpha() {
        return 0.5f;
    }

    public int setAnim() {
        return R.style.AnimFade;
    }

    public int setGravity() {
        return Gravity.CENTER;
    }

    public int setWidth(int i) {
        return ScreenUtils.getScreenWidth() / 10 * i;
    }

    public int setHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    public void show(@IdRes int ids) {
        show(ids, view -> cancel());
    }

    public void show(@IdRes int ids, SureListener listener) {
        show();
        mContentView.findViewById(ids).setOnClickListener(v -> {
            if (listener != null) {
                setSureListener(listener);
            }
        });
    }

    public void setSureListener(SureListener listener) {
        listener.onSure(mContentView);
    }

    public int setSoftInputState() {
        return WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
    }

    public interface SureListener {

        void onSure(View view);
    }

    @Override
    public void show() {
        super.show();
    }


    @Override
    public void showError() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showEmpty() {

    }
}
