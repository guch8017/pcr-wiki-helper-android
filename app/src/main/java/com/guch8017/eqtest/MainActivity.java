package com.guch8017.eqtest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String libraryUrl = "https://pcredivewiki.tw/Armory";
    private boolean removeBlank = false;
    private boolean forward = false;
    private SharedPreferences mSP;
    ProgressWebView mWebView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case  R.id.refresh:
                if(mWebView != null){
                    mWebView.loadUrl(libraryUrl);
                }
                break;

            case R.id.imp:
                if(mWebView != null){
                    final EditText et = new EditText(this);
                    new AlertDialog.Builder(this).setTitle(R.string.imp_hint).
                            setView(et).
                            setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String url = et.getText().toString();

                                    if(url.startsWith("https://pcredivewiki.tw/Armory?s=")){
                                        mWebView.loadUrl(url);
                                    }
                                    else if(url.length() < 10){
                                        mWebView.loadUrl("https://pcredivewiki.tw/Armory?s=" + url);
                                    }else{
                                        Toast.makeText(MainActivity.this, R.string.url_err, Toast.LENGTH_LONG).show();
                                    }

                                }
                            }).setNegativeButton(R.string.cancel, null).show();
                }
                break;
            default:
                Log.w(TAG, "onOptionsItemSelected: Unknown ID: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(mWebView != null){
            if(mWebView.canGoBack()){
                mWebView.goBack();
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        removeBlank = mSP.getBoolean("hide_blank", false);
        forward = mSP.getBoolean("forward", false);
        Log.i(TAG, "onResume: Preference loaded");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSP = getSharedPreferences("settings", Context.MODE_PRIVATE);
        mWebView = findViewById(R.id.library);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        WebView.setWebContentsDebuggingEnabled(true);
        //mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageCommitVisible(WebView view, String url){
                Log.i(TAG, "onPageFinished: URL: " + url);
                if(url.contains("pcredivewiki.tw/Armory")) {
                    view.evaluateJavascript(readAsset(R.raw.jquery), null);
                    view.evaluateJavascript(readAsset(R.raw.solver), null);
                    view.evaluateJavascript(readAsset(R.raw.main), null);

                    Log.i(TAG, "onPageFinished: JS LOADED");
                    Toast.makeText(MainActivity.this, "Javascript Injected", Toast.LENGTH_LONG).show();
                }
                super.onPageCommitVisible(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url){
                if(removeBlank) {
                    view.evaluateJavascript(readAsset(R.raw.remove), null);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        });

        mWebView.loadUrl(libraryUrl);
    }

    private String readAsset(int resourceId){
        String str = "";
        try{
            InputStream is = getResources().openRawResource(resourceId);
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String temp;
            while ((temp = bufferedReader.readLine()) != null){
                builder.append(temp);
                builder.append('\n');
            }
            str = builder.toString();
            Log.i(TAG, "readAsset: Asset loaded. ID: " + resourceId + " Length: " + str.length());
        }catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }
}
