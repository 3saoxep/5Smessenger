package com.example.a5smessenger.Views.Activity;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.a5smessenger.Manager.Database.DatabaseHelper;
import com.example.a5smessenger.Manager.Model.CParam;
import com.example.a5smessenger.Manager.Model.global;
import com.example.a5smessenger.Manager.Webservice.Webservice;
import com.example.a5smessenger.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class MainActivity extends Activity {
    Button btnOK;
    TextView txtVersion;
    private int MY_REQUEST_CODE = 101;
    EditText edtURL;
    boolean connection;
    int StatusCheck;
    DatabaseHelper db;
    Context context;
    //NextMessengerPage nextPage;
    LinearLayout linearLayout;
    Webviews_Messager webviews_messager;
    private final String LinkPageWebview="https://5smsgr.5stars.com.vn:7443/Login/L5SLogin.aspx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //khai báo stricmode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //khởi tạo database                             //Khởi tạo hàm asys next page
                                                        //nextPage=new NextMessengerPage();
        global.setAppContext(getApplicationContext());// lấy vị trí Activity dùng cho APP

        // get Version name
//        new GetLastVersion().execute();
        AppUpdateChecker appUpdateChecker = new AppUpdateChecker(this);
        appUpdateChecker.checkForUpdate(true);
        if(appUpdateChecker.checkUpdate == true){
            checkConnectPage();
        }
        addControl();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edt_URL=edtURL.getText().toString();
                if(edt_URL.equals("")){
                    edtURL.setError("Chưa nhập URL !!");
                }else {
                    CParam cParam=new CParam();
                    cParam.setKey(global.URL_Messenger);
                    cParam.setValue(edt_URL);
                    if(DatabaseHelper.getInstance().updateParam(cParam)==1){
                        linearLayout.setVisibility(View.GONE);
                    }
                }
                linearLayout.setVisibility(View.GONE);
                checkConnectPage();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
                // If the update is cancelled or fails,
                // you can request to start the update again.
            checkConnectPage();
        }
    }

    public String checkConnectPage() {


        if(!getNetwork(this)){
            showAlertDialog();
        }

        //            if(!isConnected()) {
//                CustomToast.makeText(this,"Kết nối không ổn định",CustomToast.LENGTH_SHORT,CustomToast.WARNING,true).show();
//            }
        if (!this.setAccessHttps()) {
            return "-1";
        }
        if(!DatabaseHelper.getInstance().checkExistsParam(global.URL_Messenger)){
            CParam cParam=new CParam();
            cParam.setKey(global.URL_Messenger);
            cParam.setValue(LinkPageWebview);
            DatabaseHelper.getInstance().createParam(cParam);
        }


        final CParam cParamcheck =DatabaseHelper.getInstance().getParamByKey(global.URL_Messenger);
        Log.d("Quang",cParamcheck.getValue());
        //kiem tra link url
        boolean isConnect_URL=CheckURLConnect(this,cParamcheck.getValue());

        Log.d("Quang",isConnect_URL+"");
        if(isConnect_URL){
           new NextMessengerPage().execute();
        }else {
            if(getNetwork(this)){
                //add 29/05/2020

                showAlertDialog11();
                CustomToast.makeText(this,"Kết nối không ổn định",CustomToast.LENGTH_SHORT,CustomToast.WARNING,true).show();

                // end add 29/05/2020
                //ẩn linearLayout
           //     linearLayout.setVisibility(View.VISIBLE);
            }
        }

        return null;
    }

    public boolean getNetwork(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            return true;
        }
        return false;
    }

    private void addControl() {
        linearLayout=findViewById(R.id.layout_error);
        btnOK=findViewById(R.id.btnOK);
        edtURL=findViewById(R.id.edit_URL);
        txtVersion=findViewById(R.id.txtVersion);
        txtVersion.setText("5S Messenger: "+ getVersionName(this));
        Log.d("verison" , getVersionName(this));
    }

//    public static boolean isConnected(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        if (activeNetwork != null && activeNetwork.isConnected()) {
//            try {
//                URL url = new URL("http://www.google.com/");
//                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
//                urlc.setRequestProperty("User-Agent", "test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1000); // mTimeout is in seconds
//                urlc.connect();
//                if (urlc.getResponseCode() == 200) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } catch (IOException e) {
//                Log.i("warning", "Error checking internet connection", e);
//                return false;
//            }
//        }
//        return false;
//    }

    public static boolean CheckURLConnect(Context context,String URL_Mess) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL(URL_Mess);
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.setReadTimeout(1000);
                Log.d("PcsAA", "1");
                urlc.connect();
                Log.d("PcsAA","2");

                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("PcsAA", "Error checking internet connection", e);
                return false;
            }
        }
        return false;
    }

    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lỗi kết nối");
        builder.setMessage("Vui lòng kiểm tra lại kết nối và nhấn Xác nhận !! ");
        builder.setCancelable(false);
        builder.setNegativeButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(getNetwork(MainActivity.this)){
                    Toast.makeText(MainActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }else {
                    Toast.makeText(MainActivity.this, "Không thành công", Toast.LENGTH_SHORT).show();
                    checkConnectPage();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    class NextMessengerPage extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent it = new Intent(MainActivity.this, Webviews_Messager.class);
            startActivity(it);
            finish();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //add

    public static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

    // hàm đăng ký Https
    private Boolean setAccessHttps() {
        try {
            trustAllHttpsCertificates();
            // Now you are telling the JRE to ignore the hostname
            HostnameVerifier hv = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    // TODO Auto-generated method stub
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            return true;

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }

    }

    // đoạn code xử dung cho HTTPs
    private static void trustAllHttpsCertificates() throws Exception {

        // Create a trust manager that does not validate certificate chains:

        javax.net.ssl.TrustManager[] trustAllCerts =

                new javax.net.ssl.TrustManager[1];

        javax.net.ssl.TrustManager tm = new Webservice.miTM();

        trustAllCerts[0] = tm;

        javax.net.ssl.SSLContext sc =

                javax.net.ssl.SSLContext.getInstance("SSL");

        sc.init(null, trustAllCerts, null);

        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(

                sc.getSocketFactory());

    }
    // không cần ping google
//    public boolean isConnected() throws InterruptedException, IOException {
//        final String command = "ping -c 1 google.com";
//        return Runtime.getRuntime().exec(command).waitFor() == 0;
//    }
    public static String getVersionName(Context context){
        String versionName="";
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
    // start add 29/05/2020
    public void showAlertDialog11(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setIcon(R.drawable.ic_logo_mess);
        builder.setMessage("không có kết nối internet !!");
        builder.setCancelable(false);
        builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    // end add 29/05/2020
    private class GetLastVersion extends AsyncTask<String, String, String>{
        private String latestVersion;
        private Context context;
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("verison" , latestVersion);
            if(!latestVersion.equals(getVersionName(getApplicationContext()))){
                Intent intent = new Intent(getApplicationContext(), AppUpdate.class);
                startActivity(intent);
                Log.d("verison" , "inetn");
            }else {
                checkConnectPage();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "&hl=it")
                        .timeout(3000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return latestVersion;
            } catch (Exception e) {
                return latestVersion;
            }
        }
    }
}
