package com.hic.hicmall;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by Obl on 2019/8/2.
 * com.hic.hicmall
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */
public class HicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.openLog();
        ARouter.openDebug();
        ARouter.init(this);
    }
}
