package com.hic.hicmall;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.hic.common.ARouterService;

/**
 * Created by Obl on 2019/8/2.
 * com.hic.hicmall
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */
@Route(path = "/provider/Test")
public class Test extends ARouterService {
    @Override
    public void init(Context context) {

    }


    @Override
    public void testService() {
        Log.e("TAG", "testService: Hello Provider!");
    }
}
