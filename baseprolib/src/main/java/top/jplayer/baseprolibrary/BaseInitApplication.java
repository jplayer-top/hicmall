package top.jplayer.baseprolibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.ArrayMap;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import skin.support.SkinCompatManager;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;
import top.jplayer.baseprolibrary.listener.SampleApplicationLifecycleCallbacks;
import top.jplayer.baseprolibrary.listener.observer.CustomObserver;
import top.jplayer.baseprolibrary.utils.LogUtil;
import top.jplayer.baseprolibrary.utils.ScreenUtils;
import top.jplayer.networklibrary.NetworkApplication;

/**
 * Created by Obl on 2018/1/9.
 * top.jplayer.baseprolibrary
 */

public class BaseInitApplication {


    private static BaseInitApplication mInit;
    public static WeakReference<Application> mWeakReference;
    public static Map<String, String> mUrlMap;
    public static Map<String, String> mHeardMap;
    public static final String urlHeardHost = "url_header_host";
    public static final Long TIME_OUT = 30L;
    public static List<Activity> sActivityList;
    public static Map<String, CustomObserver> mObserverMap;

    private BaseInitApplication(Application application) {
        mWeakReference = new WeakReference<>(application);
        mUrlMap = new ArrayMap<>();
        mHeardMap = NetworkApplication.mHeaderMap;
        sActivityList = new LinkedList<>();
        mObserverMap = new ArrayMap<>();
    }

    public synchronized static BaseInitApplication with(Application application) {
        if (mInit == null) {

            synchronized (BaseInitApplication.class) {
                if (mInit == null) {
                    mInit = new BaseInitApplication(application);
                }
            }
        }
        return mInit;
    }

    public BaseInitApplication crash(boolean debug) {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                .enabled(true) //default: true
                .showErrorDetails(debug) //default: true
                .showRestartButton(true) //default: true
                .logErrorOnRestart(false) //default: true
                .trackActivities(true) //default: false
                .errorActivity(DefaultErrorActivity.class)
                .minTimeBetweenCrashesMs(2000) //default: 3000
                .apply();
        return this;
    }

    public BaseInitApplication lifecycle(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
        return this;
    }

    public BaseInitApplication swipeBack() {
        BGASwipeBackHelper.init(mWeakReference.get(), null);
        return this;
    }

    public BaseInitApplication fixFileProvide() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        return this;
    }

    public static Context getContext() {
        return mWeakReference.get().getApplicationContext();
    }

    public Application getApplication() {
        return mWeakReference.get();
    }

    /**
     * 初始化retrofit
     */
    public BaseInitApplication retrofit() {
        NetworkApplication.with(mWeakReference.get())
                .retrofit();
        return this;
    }

    /**
     * 初始化zxing
     */
    public BaseInitApplication zxing() {
        ZXingLibrary.initDisplayOpinion(getContext());
        return this;
    }

    public BaseInitApplication skin() {
        SkinCompatManager.withoutActivity(this.getApplication())                         // 基础控件换肤初始化
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
                .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
                .setSkinStatusBarColorEnable(true)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(false)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
        return this;
    }

}
