package com.rssj.pluggable;

/**
 * Create by rssj on 2021/12/2
 */
public interface LoadCallback {

    void onSuccess();
    void onFail(String errMsg);

}
