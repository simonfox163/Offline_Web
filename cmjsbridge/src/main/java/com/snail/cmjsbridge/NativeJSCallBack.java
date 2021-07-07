package com.snail.cmjsbridge;

public interface NativeJSCallBack {

    /**
     * js 回调原生，通知调用结果
     * @param result
     */
    void onResult(String result);
}
