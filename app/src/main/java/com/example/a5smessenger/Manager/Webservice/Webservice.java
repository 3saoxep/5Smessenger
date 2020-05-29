package com.example.a5smessenger.Manager.Webservice;





import com.example.a5smessenger.Manager.P5sSecurity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import com.example.a5smessenger.Manager.Model.global;
public class Webservice {



    public static Webservice isInstance=null;
    private final String SOAP_ACTION = "http://tempuri.org/";
    private final int timeOut = 120000;// 2:30 second limit
    private final String mUrl="https://5smsgr.5stars.com.vn:7443/WebService/P5sWebService.asmx";

    public static Webservice getInstance(){

        if(isInstance==null){
            isInstance=new Webservice();
        }
        return isInstance;
    }

    public String saveTokeninDB(String userName,String token) {

        if (!this.setAccessHttps())
            return "-1";
        // hàm kết nối tới Webserive với các tham số ứng với webservice trên
        // Server
        String webServiceFunc = "P5sSaveTokenID"; // hàm webserivce
        SoapObject request = new SoapObject(this.SOAP_ACTION, webServiceFunc);

        // thiết lặp tham số và giá trị tương ứng
        PropertyInfo paramPI = new PropertyInfo();
        paramPI.setName("USER_CODE");
        paramPI.setValue(userName);
        paramPI.setType(String.class);
        request.addProperty(paramPI);

        // thiết lặp tham số và giá trị tương ứng
        PropertyInfo paramP2 = new PropertyInfo();
        paramP2.setName("TokenID");
        paramP2.setValue(token);
        paramP2.setType(String.class);
        request.addProperty(paramP2);

        // thiết lặp tham số và giá trị tương ứng
        PropertyInfo paramP3 = new PropertyInfo();
        paramP3.setName("serial");
        paramP3.setValue(global.getDeviceName());
        paramP3.setType(String.class);
        request.addProperty(paramP3);

        // thiết lặp tham số và giá trị tương ứng
        PropertyInfo paramP4 = new PropertyInfo();
        paramP4.setName("model");
        paramP4.setValue(global.getDeviceName());
        paramP4.setType(String.class);
        request.addProperty(paramP4);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        // kiểm tra User và Pass FSID trả về
        envelope.headerOut = this.getHeader();
        // dùng link FSID trả về
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                global.getUrlWebserviceToSynchronize(), timeOut);

        try {

            androidHttpTransport.call(SOAP_ACTION+ webServiceFunc, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            //global.lstLog.add("Download: getParam success " + CmnFns.getTimeOfPDA(global.getFormatDate()));
            return response.toString();

        } catch (Exception e) {
            //global.lstLog.add("Download: getParam failed " + e.getMessage() + CmnFns.getTimeOfPDA(global.getFormatDate()));
            //CmnFns.writeLogError("getParam:  " + e.getMessage());
            return "-1";
        }
    }


    private String decypt(String text) {

        String decy = null;
        try {
            decy = P5sSecurity.decrypt(text);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decy;
    }

    public Element[] getHeader() {
        // đoạn code crypt data
        try {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            bis = new BufferedInputStream(new FileInputStream("abc"));
            bos = new BufferedOutputStream(new FileOutputStream("abc", false));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
        }

        Element h = new Element().createElement(SOAP_ACTION, "Authentication");
        Element username = new Element().createElement(SOAP_ACTION, "UserName");
        username.addChild(Node.TEXT, "5stars.c33Gom*37237XZAAGF");
        h.addChild(Node.ELEMENT, username);
        Element pass = new Element().createElement(SOAP_ACTION, "Password");
        pass.addChild(Node.TEXT,
                "#*&!@((!*37327x4356*!@#&@#&@!6^@!@##@63827341232SS1@25423432");
        h.addChild(Node.ELEMENT, pass);

        return new Element[] { h };
    }

    // đoạn code xử dung cho HTTPs
    // Just add these two functions in your program
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

        javax.net.ssl.TrustManager tm = new miTM();

        trustAllCerts[0] = tm;

        javax.net.ssl.SSLContext sc =

                javax.net.ssl.SSLContext.getInstance("SSL");

        sc.init(null, trustAllCerts, null);

        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(

                sc.getSocketFactory());

    }

}
