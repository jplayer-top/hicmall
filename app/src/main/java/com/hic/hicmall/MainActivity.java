package com.hic.hicmall;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hic.hicmall.base.FragmentFactory;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import top.jplayer.baseprolibrary.ui.activity.SuperBaseActivity;

public class MainActivity extends SuperBaseActivity {
    FrameLayout mFlFragment;
    ArrayList<LinearLayout> mViewList;

    @Override
    protected int initRootLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initRootData(View view) {
        super.initRootData(view);
        mFlFragment = view.findViewById(R.id.flFragment);
        AndPermission.with(this)
                .permission(Permission.CAMERA,
                        Permission.RECORD_AUDIO,
                        Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.ACCESS_COARSE_LOCATION)
                .onGranted(permissions -> {
                })
                .onDenied(permissions -> AndPermission.hasAlwaysDeniedPermission(mActivity, permissions))
                .start();
        bottomBar();
    }

    private void bottomBar() {
        mViewList = new ArrayList<>();

        LinearLayout llHome = superRootView.findViewById(R.id.llHome);
        mViewList.add(llHome);
        llHome.setOnClickListener(v -> onTabClick((LinearLayout) v, 0));


        LinearLayout llServer = superRootView.findViewById(R.id.llServer);
        mViewList.add(llServer);
        llServer.setOnClickListener(v -> onTabClick((LinearLayout) v, 2));

        LinearLayout llMsg = superRootView.findViewById(R.id.llMsg);
        mViewList.add(llMsg);
        llMsg.setOnClickListener(v -> {
                onTabClick((LinearLayout) v, 3);
        });

        LinearLayout llMe = superRootView.findViewById(R.id.llMe);
        mViewList.add(llMe);
        llMe.setOnClickListener(v -> {
                onTabClick((LinearLayout) v, 4);
        });

        onTabClick(mViewList.get(0), 0);
    }

    public int curIndex = -1;

    public void onTabClick(LinearLayout view, int index) {

        if (curIndex == index) {
            return;
        }
        for (LinearLayout linearLayout : mViewList) {
            linearLayout.getChildAt(0).setSelected(false);
            linearLayout.getChildAt(1).setSelected(false);
        }
        view.getChildAt(0).setSelected(true);
        view.getChildAt(1).setSelected(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(index + "");
        if (fragment == null) {
            fragment = FragmentFactory.instance().getSingleFragment(index);
            transaction.add(R.id.flFragment, fragment, index + "");
        }
        for (Fragment fragmentItem : getSupportFragmentManager().getFragments()) {
            if (!fragmentItem.isHidden()) {
                transaction.hide(fragmentItem);
            }
        }
        if (fragment.isHidden()) {
            transaction.show(fragment);
        }
        curIndex = index;
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean isOpenDoubleBack() {
        return true;
    }
}
