package com.hic.hicmall;


import android.annotation.SuppressLint;
import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import top.jplayer.baseprolibrary.BaseInitApplication;
import top.jplayer.baseprolibrary.listener.SampleApplicationLifecycleCallbacks;

/**
 * Created by Obl on 2019/8/2.
 * com.hic.hicmall
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */
public class HicApplication extends MultiDexApplication {

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);

    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate() {
        super.onCreate();
        BaseInitApplication.with(this)
                .swipeBack()
                .lifecycle(new SampleApplicationLifecycleCallbacks())
                .retrofit();
        Observable.just(0)
                .subscribeOn(Schedulers.io())
                .map(integer -> {
                    BaseInitApplication.with(this)
                            .zxing()
                            .crash()
                            .aRouter();
                    return integer;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
