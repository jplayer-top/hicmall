package com.hic.hicmall.base;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.PartMap;
import top.jplayer.codelib.AutoMP;

/**
 * Created by Obl on 2019/8/7.
 * com.hic.hicmall.base
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */
public interface HicServer {
    @AutoMP
    Observable<BaseBean> test(@PartMap Map<String, String> map);
}
