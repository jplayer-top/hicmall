package com.hic.hicmall.base;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import top.jplayer.networklibrary.contract.BasePresenter;
import top.jplayer.networklibrary.net.tip.INetTip;
import top.jplayer.networklibrary.net.tip.NullTip;
import top.jplayer.networklibrary.utils.LogUtil;

/**
 * Created by Obl on 2019/8/5.
 * com.hic.hicmall.base
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */
public abstract class DefaultCallBackObserver <T extends BaseBean> implements Observer<T> {
    private INetTip mProgress;
    private Disposable mDisposable;
    private BasePresenter presenter;

    public Disposable getDisposable() {
        return mDisposable;
    }

    public DefaultCallBackObserver(INetTip progress) {
        this.mProgress = progress;
        if (mProgress == null) {
            mProgress = new NullTip();
        }
    }

    public DefaultCallBackObserver() {
        if (mProgress == null) {
            mProgress = new NullTip();
        }

    }

    public DefaultCallBackObserver(BasePresenter presenter) {
        if (mProgress == null) {
            mProgress = new NullTip();
        }
        this.presenter = presenter;
    }

    public DefaultCallBackObserver(INetTip progress, BasePresenter presenter) {
        this.mProgress = progress;
        if (mProgress == null) {
            mProgress = new NullTip();
        }
        this.presenter = presenter;
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.mDisposable = d;
        if (presenter != null) {
            presenter.addSubscription(d);
        }
        if (!d.isDisposed()) {
            mProgress.tipStart();
        }
    }

    @Override
    public void onNext(T t) {
        onRequestEnd(t);
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.e(e.getMessage());
        mProgress.tipEnd();
        if (e.getMessage() != null && e.getMessage().contains("401")) {
            errorLogin();
        } else {
            mProgress.tipError(e);
        }
//        try {
//            if (e instanceof ConnectException
//                    || e instanceof TimeoutException
//                    || e instanceof NetworkErrorException
//                    || e instanceof UnknownHostException) {
//                //网络错误
//            } else {
//                //未知错误
//            }
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
    }

    @Override
    public void onComplete() {
        mProgress.tipComplete();
    }

    protected void onRequestEnd(T t) {
        mProgress.tipEnd();
        if (t.isSuccess()) {
            mProgress.tipSuccess(t.message);
            responseSuccess(t);
        } else {
            mProgress.tipFail(t.result, t.message);
            responseFail(t);
        }
    }

    public abstract void responseSuccess(T t);

    public abstract void responseFail(T t);


    /**
     * 无登录状况，默认实现
     */
    public void errorLogin() {
        mProgress.tipFail("401", "当前账号未登录，请前往登录");
    }

}
