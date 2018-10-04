package com.mTapWiki.shaktis.wikipedia.Article;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.mTapWiki.shaktis.wikipedia.R;

public class WikiDetail extends AppCompatActivity {
    private WebView browser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_detail);
//        getActionBar().setTitle("Wikipedia");
        getSupportActionBar().setTitle("Wikipedia");
        Intent intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra("title"));
        browser = new WebView(this);
        browser.getSettings().setJavaScriptEnabled(true); // enable javascript
        browser.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
        final Activity activity = this;
        browser.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                browser.loadUrl("javascript:(function() { " +"document.getElementsByClassName"+
                "('header-container header-chrome')[0].style.display='none';"
                        +"})()");

            }
        });
        browser.loadUrl(intent.getStringExtra("url"));
        setContentView(browser );


    }

}
