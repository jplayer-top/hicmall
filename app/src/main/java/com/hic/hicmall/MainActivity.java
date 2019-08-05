package com.hic.hicmall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import top.jplayer.codelib.AutoMP;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hic.meinfo.MeActivity;

@Route(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {
    @Autowired
    public String name;
    @Autowired(name = "age")
    int age;

    @AutoMP
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ARouter.getInstance().inject(this);//添加在onCreate（）
        Log.e("param", name + age + age);
        findViewById(R.id.btn).setOnClickListener(view -> ARouter.getInstance().build("/me/MeActivity").navigation());
    }
}
