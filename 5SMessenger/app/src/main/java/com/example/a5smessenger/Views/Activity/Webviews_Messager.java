package com.example.a5smessenger.Views.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.a5smessenger.Manager.Database.DatabaseHelper;
import com.example.a5smessenger.Manager.Model.CParam;
import com.example.a5smessenger.Manager.Webservice.Webservice;
import com.example.a5smessenger.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import com.example.a5smessenger.Manager.Model.global;
public class Webviews_Messager extends Activity {

    String urlCurrent;
    RelativeLayout relativeLayout;
    String urlMess = "";
    WebView webViewMess;
    DatabaseHelper db;
    private final static int FCR = 1;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;


    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviews_messager);
        db=new DatabaseHelper(Webviews_Messager.this);
        addControl();
        loadWebViewMess();
    }

    private void addControl() {
        relativeLayout = findViewById(R.id.Relative_webview);
        webViewMess = findViewById(R.id.webview_mess);

        //webViewMess.setWebViewClient(new MyBrowser());
//        swipeRefreshLayout=findViewById(R.id.swiperefresh_webview_mess);
//        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh(SwipyRefreshLayoutDirection direction) {
//                Log.d("MainActivity", "Refresh triggered at "
//                        + (direction == SwipyRefreshLayoutDirection.BOTTOM ? "top" : "bottom"));
//                webViewMess.reload();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

    }

    private void loadWebViewMess() {
        CParam keyUrl = db.getParamByKey("URL_Messenge");
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webViewMess, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        //urlMess = urlMess + "?USER_CODE=" + CSales.getSaleCode();
        webViewMess.getSettings().setJavaScriptEnabled(true);
        webViewMess.getSettings().setAppCacheEnabled(true); //cho phép sử dụng cache của webview
        webViewMess.getSettings().setDomStorageEnabled(true);//cho phép dùng bộ nhớ
        //webViewMess.canGoBack();
        webViewMess.canGoForward();
        webViewMess.getSettings().setGeolocationEnabled(true);
        webViewMess.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewMess.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Webviews_Messager.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
        assert webViewMess != null;

        final WebSettings webSettings = webViewMess.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webViewMess.getSettings().setSupportZoom(false);
        webViewMess.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(0);
            webViewMess.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            webViewMess.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            webViewMess.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webViewMess.setWebViewClient(new Callback());

        webViewMess.loadUrl(keyUrl.getValue());
        //mới
        webViewMess.setWebChromeClient(new ChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String urlCurrent=webViewMess.getUrl();
                try {
                    String session=getCookieFromAppCookieManager(urlCurrent);
                    Log.d("NameUser",session);
                    if(session.length() > 60) {
                        String[] spiitSession = session.split(global.SPLIT_KEY_1);
                        for(int i=0;i < spiitSession.length;i++){
                            String[] spiituserName=spiitSession[0].split(global.SPLIT_KEY_2);
                            if(spiituserName[0].equals("txtUsername")){
                                Log.d("NameUser","User: "+spiituserName[1]);
                                sendToken(spiituserName[1]);
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onLoadResource( WebView view, String url ){
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(url);
                Log.d("ONloadResource","cookie is"+cookie);
            }
            //For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                Webviews_Messager.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
            }

            // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {

                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                Webviews_Messager.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FCR);
            }

            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                Webviews_Messager.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), Webviews_Messager.FCR);
            }

            //mở file hệ thống để upload file lên webview
            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {

                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }

                mUMA = filePathCallback;
                // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
//                Intent[] intentArray;
//
//                if (takePictureIntent != null) {
//                    intentArray = new Intent[]{takePictureIntent};
//                } else {
//                    intentArray = new Intent[0];
//                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                // chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                // chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);

                return true;
            }
        });



        // hàm download file khi click vào
        webViewMess.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                String[] filename = url.split("/");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename[4]);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        });

        //hàm LongClick để download file

//        webViewMess.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                webViewMess.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        DownloadManager.Request request = new DownloadManager.Request(
//                                Uri.parse(url));
//                        String[] filename=url.split("/");
//                        request.allowScanningByMediaScanner();
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename[4]);
//                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                        dm.enqueue(request);
//                        return false;
//                    }
//                });
//            }
//        });

    }


    private abstract class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {
        }


        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        }

        public abstract void onLoadResource(WebView view, String url);
        ///
    }

    public class Callback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }
            message += "\"SSL Certificate Error\" Do you want to continue anyway?.. YES";

            handler.proceed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FCR) {
            if (mUMA == null) return;
            mUMA.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            mUMA = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        webViewMess.clearCache(true);
        webViewMess.clearFormData();
        webViewMess.clearHistory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webViewMess.clearCache(true);
        webViewMess.clearFormData();
        webViewMess.clearHistory();
    }

    public static void sendToken(String userName){
        if(userName != null){
            CParam cParam=DatabaseHelper.getInstance().getParamByKey(global.TOKEN_KEY);
            if(cParam != null){
               int sendToken=Integer.parseInt(Webservice.getInstance().saveTokeninDB(userName,cParam.getValue()));
               Log.d("Send Token","Success: "+sendToken);
            }
        }
    }

    public static String getCookieFromAppCookieManager(String url) throws MalformedURLException {

        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager == null)
            return null;
        String rawCookieHeader = null;
        URL parsedURL = new URL(url);
        rawCookieHeader = cookieManager.getCookie(parsedURL.getHost());
        if (rawCookieHeader == null)
            return null;
        return rawCookieHeader;
    }



}
