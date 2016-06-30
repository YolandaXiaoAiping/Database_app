package com.example.aipingxiao.assignment_4.UI;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.aipingxiao.assignment_4.R;

/**
 * Created by Aiping Xiao on 2016-02-10.
 */
public class Webview_Activity extends Activity {
    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_search);
        browser = (WebView)findViewById(R.id.webkit);

        browser.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });
        browser.loadUrl("http://google.ca");
    }
}
