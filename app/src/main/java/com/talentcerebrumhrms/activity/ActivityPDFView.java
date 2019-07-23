package com.talentcerebrumhrms.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;
import com.talentcerebrumhrms.utils.EulaWebView;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by DVD on 17/11/2016.
 */
public class ActivityPDFView extends AppCompatActivity {




LinearLayout webviewlayout;
   String url = "";;
   String drive_url="http://docs.google.com/viewer?embedded=true&url=";
    final Activity activity = this;


    //WebView myWebView;
    EulaWebView myWebView;


    Boolean showProgressOnSplashScreen = false;

   // WebView mWebView;
    ProgressBar prgs;
    RelativeLayout splash, main_layout;
    TextView txtgotIt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web);
        ImageView btnDelete = (ImageView) findViewById(R.id.imageView4);
        btnDelete.setImageDrawable(getResources().getDrawable(R.drawable.close_24dp));
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ActivityPDFView.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                Intent i = new Intent(ActivityPDFView.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i, bndlanimation);
            }
        });
        txtgotIt = (TextView) findViewById(R.id.txtgotit);
        txtgotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ActivityPDFView.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                Intent i = new Intent(ActivityPDFView.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i, bndlanimation);
            }
        });
        txtgotIt.setVisibility(View.GONE);
        myWebView = (EulaWebView) findViewById(R.id.webView1);

        webviewlayout=(LinearLayout)findViewById(R.id.webViewlayout);
      /* Button myButton = new Button(this);
        myButton.setText("Push Me");
           myButton.setGravity(Gravity.BOTTOM);
        myWebView.addView(myButton);*/
       prgs = (ProgressBar) findViewById(R.id.progressBar);

            getPDFURL();
      // url= "http://docs.google.com/viewer?embedded=true&url=https://hpspl.talbrum.com/system/company_documents/documents/000/000/005/original/darpan110619.pdf";

   /* myWebView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
        @Override
        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
            /*int tek = (int) Math.floor(myWebView.getContentHeight() * myWebView.getScale());
            if(tek - myWebView.getScrollY() == myWebView.getHeight())
                Toast.makeText(ActivityPDFView.this, "End", Toast.LENGTH_SHORT).show();
                System.out.println("END WEBVIEW");
            System.out.println("END WEBVIEW : "+myWebView.getBottom());
            System.out.println("END WEBVIEW HEIGHT: "+myWebView.getHeight());
*/


     /*   }
    });*/
      /*  myWebView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback(){
            public void onScroll(int l, int t, int oldl, int oldt){
                if(t> oldt){
                    //Do stuff
                    System.out.println("Swipe UP");
                    //Do stuff
                }
                else if(t< oldt){
                    System.out.println("Swipe Down");
                }
               // Log.d(TAG,"We Scrolled etc...");
            }
        });*/
        myWebView.setVerticalScrollBarEnabled(true);
        myWebView.setOnBottomReachedListener(myWebView.mOnBottomReachedListener, 50);

        myWebView.setOnBottomReachedListener(new EulaWebView.OnBottomReachedListener() {
    @Override
    public void onBottomReached(View v) {
        txtgotIt.setVisibility(View.VISIBLE);
       // Toast.makeText(ActivityPDFView.this, "End", Toast.LENGTH_SHORT).show();
        System.out.println("END WEBVIEW");

    }
},50);
    }




    private void getPDFURL() {

        Call<JsonObject> call = ApiUtil.getServiceClass().getPDFurl();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null){
                    if(jObject.code()==200){

                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());

                             url = jsonObject.getString("data");
                            if (url!=null) {

                                String[] array = url.split("\\?");
                                String NewUrl = drive_url+ AppController.serverUrl+array[0];
                                System.out.println("data:  "+jsonObject.toString());

                                if (!showProgressOnSplashScreen)
                                    ((ProgressBar) findViewById(R.id.progressBarSplash)).setVisibility(View.GONE);
                                splash = (RelativeLayout) findViewById(R.id.splash);

                                myWebView.loadUrl(NewUrl);

                                // control javaScript and add html5 features
                                myWebView.setFocusable(true);
                                myWebView.setFocusableInTouchMode(true);
                                myWebView.getSettings().setJavaScriptEnabled(true);
                                myWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                                //myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                                //myWebView.getSettings().setDomStorageEnabled(true);
                                //myWebView.getSettings().setAppCacheEnabled(true);
                                myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                                //myWebView.getSettings().setDatabaseEnabled(true);
                                //myWebView.getSettings().setDatabasePath(
                                //        getFilesDir().getPath() + getPackageName()
                                //                + "/databases/");
                                WebSettings webSettings = myWebView.getSettings();
                                webSettings.setBuiltInZoomControls(true);
                                webSettings.setSupportZoom(true);

                                // this force use chromeWebClient
                                myWebView.getSettings().setSupportMultipleWindows(true);

                                myWebView.setWebViewClient(new WebViewClient() {

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        view.loadUrl(url);
                                        return false;
                                    }

                                    @Override
                                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                        super.onPageStarted(view, url, favicon);
                                        if (prgs.getVisibility() == View.GONE) {
                                            prgs.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onLoadResource(WebView view, String url) {
                                        super.onLoadResource(view, url);
                                    }

                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        super.onPageFinished(view, url);

                                        if (prgs.getVisibility() == View.VISIBLE)
                                            prgs.setVisibility(View.GONE);

                                        // check if splash is still there, get it away!
                                        if (splash.getVisibility() == View.VISIBLE)
                                            splash.setVisibility(View.GONE);
                                        // slideToBottom(splash);

                                    }
                                    @Override
                                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                        super.onReceivedError(view, errorCode, description, failingUrl);

                                        view.loadUrl("file:///android_asset/error.html");
                                    }
                                });


                                if(haveNetworkConnection()){
                                    System.out.println("url "+NewUrl);
                                    myWebView.loadUrl(NewUrl);
                                    // textviewlayout.setVisibility(View.GONE);


                                } else {

                                    //  webviewlayout.setVisibility(View.GONE);
                                    // textviewlayout.setVisibility(View.VISIBLE);


                                    // Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_LONG).show();
                                }



                            }

                            //attendanceFragment null when calling from mark attendance no need to call salary slip then.
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
                //  errorFunction(MainActivity.this, volleyError);
            }
        });

    }




    @Override
    public void onBackPressed() {
        //System.out.println("***back*");
        //ActivityPDFView.super.onBackPressed();
        if(myWebView.canGoBack())
            myWebView.goBack();
        else
            super.onBackPressed();
       // ani.onBPressed();

    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
