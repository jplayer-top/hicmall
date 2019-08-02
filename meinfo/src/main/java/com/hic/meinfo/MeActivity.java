package com.hic.meinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hic.common.ARouterService;

@Route(path = "/me/MeActivity")
public class MeActivity extends AppCompatActivity {
    @Autowired(name = "/provider/Test")
    ARouterService mARouterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        ARouter.getInstance().inject(this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mARouterService.testService();
                ARouter.getInstance().build("/app/MainActivity")
                        .withString("name", "888")
                        .withInt("age", 11)
                        .navigation();
            }
        });
    }
}
