package com.hic.hicmall.base;

/**
 * Created by Obl on 2019/8/5.
 * com.hic.hicmall.base
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */
public class BaseBean {

    /**
     * result : ok
     * data : {}
     * message : 操作成功
     * curson : null
     * erros : null
     */

    public String result;
    public String message;

    public boolean isSuccess() {
        return "ok".equals(result);
    }
}
