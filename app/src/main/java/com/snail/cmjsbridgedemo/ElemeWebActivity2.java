package com.snail.cmjsbridgedemo;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.snail.cmjsbridge.IJsCallBack;
import com.snail.cmjsbridge.JsBridgeApi;
import com.snail.cmjsbridge.JsMessageBean;
import com.snail.cmjsbridge.JsonUtil;
import com.snail.cmjsbridge.NativeJSCallBack;
import com.snail.cmjsbridge.NativeMessageBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Author: simon
 * Data: 16/7/20 下午7:15
 * Des: 模拟 js调用原生网络请求，然后原生异步返回数据给js
 * version:
 */
public class ElemeWebActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.finish();
    }
}
