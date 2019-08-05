package top.jplayer.baseprolibrary.listener;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import top.jplayer.baseprolibrary.utils.LogUtil;

import static top.jplayer.baseprolibrary.BaseInitApplication.sActivityList;

/**
 * Created by Obl on 2018/3/14.
 * top.jplayer.baseprolibrary.listener
 */

public class SampleApplicationLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        sActivityList.add(activity);
        LogUtil.e(activity.getLocalClassName(), "onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        sActivityList.remove(activity);
        LogUtil.e(activity.getLocalClassName(), "onActivityDestroyed");
    }
}
