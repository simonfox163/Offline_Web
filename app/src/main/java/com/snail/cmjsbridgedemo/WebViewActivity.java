package com.snail.cmjsbridgedemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

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


/**
 * Author: simon
 * Data: 16/7/20 下午7:15
 * Des: 用来测试jsbridge
 * version:
 */
public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private JsBridgeApi mJsBridgeApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);
        final Button button = new Button(this);
        button.setText("Native Call h5 need callback-s");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJsBridgeApi.callH5FromNative(new NativeMessageBean() {
                    {
                        message = "callFromNative('arg1',1)";
                        messageId = 1;
                    }
                }, new NativeJSCallBack() {
                    @Override
                    public void onResult(String result) {
                        button.setText(result);
                        Log.v("callH5FromNative", "h5 notify native callback");
//                        Toast.makeText(getApplicationContext(),"callH5FromNative  h5 notify native callback" ,Toast.LENGTH_SHORT).show();

                    }
                });
               //startActivity(new Intent(WebViewActivity.this,WebViewActivity.class));
            }
        });

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.topMargin = 200;
        FrameLayout viewGroup = (FrameLayout) findViewById(android.R.id.content);
        //viewGroup.addView(button, layoutParams);


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
                        button.setText(result);
                        Log.v("callH5FromNative", "h5 notify native callback");
//                        Toast.makeText(getApplicationContext(),"callH5FromNative  h5 notify native callback" ,Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        mJsBridgeApi.openJsBridgeChannel(webView);
        webView.setWebContentsDebuggingEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

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
