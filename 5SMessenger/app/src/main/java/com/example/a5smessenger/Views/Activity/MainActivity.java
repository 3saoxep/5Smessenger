package com.example.a5smessenger.Views.Activity;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.example.a5smessenger.Manager.Database.DatabaseHelper;
import com.example.a5smessenger.Manager.Model.CParam;
import com.example.a5smessenger.Manager.Model.global;
import com.example.a5smessenger.Manager.Webservice.Webservice;
import com.example.a5smessenger.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class MainActivity extends Activity {
    Button btnOK;
    TextView txtVersion;
    EditText edtURL;
    boolean connection;
    int StatusCheck;
    DatabaseHelper db;
    //NextMessengerPage nextPage;
    LinearLayout linearLayout;
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
        addControl();
        checkConnectPage();
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



    private String checkConnectPage() {


        if(!getNetwork(this)){
            showAlertDialog();
        }

        try {
            if(!isConnected()) {
                CustomToast.makeText(this,"Kết nối không ổn định",CustomToast.LENGTH_SHORT,CustomToast.WARNING,true).show();
            }
            if (!this.setAccessHttps()) {
                return "-1";
            }
            if(!DatabaseHelper.getInstance().checkExistsParam(global.URL_Messenger)){
                CParam cParam=new CParam();
                cParam.setKey(global.URL_Messenger);
                cParam.setValue(LinkPageWebview);
                DatabaseHelper.getInstance().createParam(cParam);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CParam cParamcheck =DatabaseHelper.getInstance().getParamByKey(global.URL_Messenger);
        Log.d("Quang",cParamcheck.getValue());
        boolean isConnect_URL=CheckURLConnect(this,cParamcheck.getValue());
        Log.d("Quang",isConnect_URL+"");
        if(isConnect_URL){
           new NextMessengerPage().execute();
        }else {
            if(getNetwork(this)){
                linearLayout.setVisibility(View.VISIBLE);
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
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
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

    public boolean isConnected() throws InterruptedException, IOException {
        final String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }
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
}
