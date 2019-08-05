package com.hic.hicmall.base;

import android.util.ArrayMap;

import java.util.Map;

import top.jplayer.baseprolibrary.ui.fragment.SuperBaseFragment;
import top.jplayer.baseprolibrary.ui.fragment.TestFragment;

/**
 * Created by Obl on 2019/8/5.
 * com.hic.hicmall.base
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */
public class FragmentFactory {
    private static FragmentFactory mFactory;

    public static FragmentFactory instance() {
        if (mFactory == null) {
            mFactory = new FragmentFactory();
        }
        return mFactory;
    }

    private Map<Integer, SuperBaseFragment> map = new ArrayMap<>();

    public SuperBaseFragment getSingleFragment(int position) {
        SuperBaseFragment fragment;
        if (map.containsKey(position)) {
            return map.get(position);
        }
        switch (position) {
            case 0:
                fragment = new TestFragment();
                break;
            case 1:
                fragment = new TestFragment();
                break;
            case 2:
                fragment = new TestFragment();
                break;
            default:
                fragment = new TestFragment();
                break;

        }
        map.put(position, fragment);
        return fragment;
    }
}
