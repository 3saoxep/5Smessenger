package com.example.a5smessenger.Views.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a5smessenger.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import org.jsoup.Jsoup;

public class AppUpdate extends Activity {
    private AppUpdateManager appUpdateManager;
    private int MY_REQUEST_CODE = 101;
    private Button btn;
    InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            Log.d("verison1", installState.toString());
            if(installState.installStatus() == InstallStatus.DOWNLOADING){
                popupSnackbarForCompleteUpdate();
            }
        }
    };

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Log.d("verison" , "trang update");
        btn = findViewById(R.id.btn_update);
        appUpdateManager = AppUpdateManagerFactory.create(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                            try {
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, AppUpdate.this, MY_REQUEST_CODE);

                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getApplicationContext().getPackageName())));
                System.exit(0);

                Log.d("verison" , "click  buton");
                // Before starting an update, register a listener for updates.
                appUpdateManager.registerListener(listener);
            }
        });

    }
    /* Displays the snackbar notification and call to action. */
    private void popupSnackbarForCompleteUpdate() {
        @SuppressLint("ResourceType")
        Snackbar snackbar = Snackbar.make(findViewById(R.layout.activity_update),"An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", (View.OnClickListener) appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_REQUEST_CODE){
            if(resultCode != RESULT_OK){
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if(appUpdateInfo.installStatus()== InstallStatus.DOWNLOADED){
                    popupSnackbarForCompleteUpdate();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Start an update.
        // When status updates are no longer needed, unregister the listener.
        appUpdateManager.unregisterListener(listener);

    }
    //current version of app installed in the device
    private String getCurrentVersion(){
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(this.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return pInfo.versionName;
    }
}
