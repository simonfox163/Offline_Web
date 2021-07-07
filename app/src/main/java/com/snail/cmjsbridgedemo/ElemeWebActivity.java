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
public class ElemeWebActivity extends AppCompatActivity {

    private static final String TAG = "OFFline_eleme";
    private WebView webView;
    private JsBridgeApi mJsBridgeApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        mJsBridgeApi = new JsBridgeApi(webView, new IJsCallBack() {
            @Override
            public void onJsCall(JsMessageBean jsMessageBean) {
                Log.v("onJsCall", JsonUtil.toJsonString(jsMessageBean)) ;
//                Toast.makeText(getApplicationContext(),"js call native "+JsonUtil.toJsonString(jsMessageBean),Toast.LENGTH_SHORT).show();
                //mJsBridgeApi.notifyNativeTaskFinished("sf", jsMessageBean.id);


                mJsBridgeApi.callH5FromNative(new NativeMessageBean() {
                    {
                        String s = "callFromNative('xxxx',1)";
                        message = s.replace("xxxx",getJsContent());
                        messageId = 1;
                    }
                }, new NativeJSCallBack() {
                    @Override
                    public void onResult(String result) {
                        Log.v(TAG, "h5 notify native callback");
//                        Toast.makeText(getApplicationContext(),"callH5FromNative  h5 notify native callback" ,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        mJsBridgeApi.openJsBridgeChannel(webView);
        webView.setWebContentsDebuggingEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        // 需要在index.html文件里引入core.js
        // web项目 npm run build 然后把 /static  index.html拷贝到assert 下面
        webView.loadUrl("file:///android_asset/index.html#goods");
    }



    // 获取离线商品列表数据
    private String getJsContent() {
        InputStream is = null;
        StringBuilder content = new StringBuilder();
        try {
            is = getAssets().open("menu.json");
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
