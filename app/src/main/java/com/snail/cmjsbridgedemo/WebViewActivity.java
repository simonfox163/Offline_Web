package com.snail.cmjsbridgedemo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Author: simon
 * Data: 16/7/20 下午7:15
 * Des: 用来测试jsbridge  && js 注入
 * version:
 */
public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private JsBridgeApi mJsBridgeApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("-offline---",System.currentTimeMillis()+"");
        webView = new WebView(this);
        Log.i("-offline---",System.currentTimeMillis()+"");
        setContentView(webView);


        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
//        mJsBridgeApi = new JsBridgeApi(webView, new IJsCallBack() {
//            @Override
//            public void onJsCall(JsMessageBean jsMessageBean) {
//                Log.v("onJsCall", JsonUtil.toJsonString(jsMessageBean)) ;
////                Toast.makeText(getApplicationContext(),"js call native "+JsonUtil.toJsonString(jsMessageBean),Toast.LENGTH_SHORT).show();
//                //mJsBridgeApi.notifyNativeTaskFinished("sf", jsMessageBean.id);
//
//
//
//
//                mJsBridgeApi.callH5FromNative(new NativeMessageBean() {
//                    {
//                        String s = "callFromNative('xxxx',1)";
//                        message = s.replace("xxxx",getJsContent());
//                        messageId = 1;
//                    }
//                }, new NativeJSCallBack() {
//                    @Override
//                    public void onResult(String result) {
////                        button.setText(result);
//                        Log.v("callH5FromNative", "h5 notify native callback");
////                        Toast.makeText(getApplicationContext(),"callH5FromNative  h5 notify native callback" ,Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            }
//        });
//        mJsBridgeApi.openJsBridgeChannel(webView);
        webView.setWebContentsDebuggingEnabled(true);


        //webView.loadUrl("file:///android_asset/index.html#goods");
        Log.i("-offline---",System.currentTimeMillis()+"");

        webView.loadUrl("https://static.yunjiglobal.com/qnUpload/frontend/testtime.html");
//        webView.loadUrl("https://static.yunjiglobal.com/qnUpload/frontend//performanceanalyse/performance.js");
//        webView.loadUrl("file:///android_asset/test.html");
//        webView.loadUrl("https://m.jd.com");

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("===offline==", "onPageStarted");
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(final WebView view, String url) {
                Log.i("===offline==", "onPageFinished" );
                super.onPageFinished(view, url);

               //整个文本load
                view.loadUrl("javascript:"+getJsContent());


                //具体调用某函数
                //view.loadUrl("javascript:"+"myFunction3()");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("should===offline==", url);
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }
                return true;
            }
        });
    }


    private String getJsContent() {
        return getJsfile("jsv6.js");
    }


    private String getJsfile(String file) {
        InputStream is = null;
        StringBuilder content = new StringBuilder();
        try {
            is = getAssets().open(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String temp;
            while ((temp = br.readLine()) != null) {
                content.append(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }



    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mJsBridgeApi.destroy();
        if (webView != null) {

            if (webView.getParent() instanceof ViewGroup) {
                ((ViewGroup) (webView.getParent())).removeView(webView);
            }
            webView.destroy();
        }

    }


}
