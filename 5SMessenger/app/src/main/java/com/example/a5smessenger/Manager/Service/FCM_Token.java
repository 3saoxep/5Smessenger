package com.example.a5smessenger.Manager.Service;

import android.util.Log;


import com.example.a5smessenger.Manager.Database.DatabaseHelper;
import com.example.a5smessenger.Manager.Model.CParam;
import com.example.a5smessenger.Manager.Webservice.Webservice;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.example.a5smessenger.Manager.Model.global;
public class FCM_Token extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        CParam cParam=new CParam();
        String token= FirebaseInstanceId.getInstance().getToken();
        Log.d("Token_Firebase",token);
        cParam.setKey(global.TOKEN_KEY);
        cParam.setValue(token);
        DatabaseHelper.getInstance().createParam(cParam);
    }
}
