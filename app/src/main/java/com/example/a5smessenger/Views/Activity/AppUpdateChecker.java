package com.example.a5smessenger.Views.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.a5smessenger.Manager.Database.DatabaseHelper;
import com.example.a5smessenger.Manager.Model.CParam;
import com.example.a5smessenger.Manager.Model.global;
import com.example.a5smessenger.R;

import org.jsoup.Jsoup;

public class AppUpdateChecker {
    private Activity activity;
    private String latestVersion;

    public AppUpdateChecker(Activity activity) {
        this.activity = activity;
    }
    //current version of app installed in the device
    private String getCurrentVersion(){
        PackageManager pm = activity.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(activity.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return pInfo.versionName;
    }
    private class GetLatestVersion extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        boolean manualCheck;
        GetLatestVersion(boolean manualCheck) {
            this.manualCheck = manualCheck;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (manualCheck)
            {
                if (progressDialog!=null)
                {
                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                }
            }
            String currentVersion = getCurrentVersion();
            //If the versions are not the same
            if(!currentVersion.equals(latestVersion)&&latestVersion!=null){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Thông báo");
                builder.setIcon(R.drawable.ic_logo_mess);
                builder.setMessage("Đã có phiên bản mới vui lòng cập nhật !!");
                builder.setCancelable(false);
                builder.setNegativeButton("Cập nhật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+activity.getPackageName())));
//                        dialog.dismiss();
                        System.exit(0);
                    }
                });
                manualCheck=false;
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else {
                if (manualCheck) {
                    Toast.makeText(activity, "No Update Available", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (manualCheck) {
                progressDialog=new ProgressDialog(activity);
                progressDialog.setMessage("Checking For Update.....");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + activity.getPackageName() + "&hl=it")
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
    public void checkForUpdate(boolean manualCheck)
    {
        new GetLatestVersion(manualCheck).execute();

    }

}
