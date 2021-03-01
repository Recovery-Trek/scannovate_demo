package com.example.scanovatedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends Activity {

    private static final String TAG = "WebViewActivity";
    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webviewactivity);

        WebView webView = findViewById(R.id.mWebView);

        webView.setWebViewClient(new WebViewClient() {

            // Here we stop the process on your server finish scheme
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.toLowerCase().startsWith("recoverytrek://".toLowerCase())) {

                Map<String, String> params=getQueryMap(url);

                // Get token from url
                String token= params.get("token");

                Log.d("Token",token);
                    SharedPreferences sharedpreferences= getSharedPreferences("pref", Context.MODE_PRIVATE);;
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("token", token);
                    editor.apply();
                // Return token
                Intent returnIntent = new Intent();
                returnIntent.putExtra("token",token);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
                return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view,handler,error);
            Log.i(TAG,"onReceivedSslError handler: "+handler.toString());
            Log.i(TAG,"onReceivedSslError error: "+error.toString());
            handler.proceed();
        }

    });

        webView.setWebChromeClient(new WebChromeClient() {
        @Override
        public void onPermissionRequest(PermissionRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request.grant(request.getResources());
            }
        }

    });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

    // Add for debug in inspact
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        WebView.setWebContentsDebuggingEnabled(true);
    }

        webView.loadUrl("data link create from 61 or 69 flow");
}

    // Helper func
    private static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params)
        {  String [] p=param.split("=");
            String name = p[0];
            if(p.length>1)  {String value = p[1];
                map.put(name, value);
            }
        }
        return map;
    }
}
