package com.tickera.tickeraapp;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by aber on 18/05/15.
 */
public class ApiCall {

    private String url="";
    private ApiCallListener callback;

    private boolean success = false;
    private String result = "";

    private int id;

    public ApiCall(String url, ApiCallListener callback, int id){
        this.callback = callback;
        this.url = url;
        this.id = id;
        SendData sd = new SendData();
        sd.execute();
    }

    private class SendData extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            if (url.equals("") || url==null) return null;


            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

            DefaultHttpClient client = new DefaultHttpClient();

            SchemeRegistry registry = new SchemeRegistry();
            org.apache.http.conn.ssl.SSLSocketFactory socketFactory = org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);

            DefaultHttpClient httpClient;
            if (url.toUpperCase().indexOf("HTTPS://")!=-1)
                httpClient = new DefaultHttpClient(mgr, client.getParams());
            else
                httpClient = new DefaultHttpClient();

            try {
                HttpGet httpGet = new HttpGet(ApiCall.this.url);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                HttpEntity httpEntity = httpResponse.getEntity();
                if(httpEntity != null) {
                    result = EntityUtils.toString(httpEntity);
                    success = true;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            } catch (IllegalStateException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callback.call(result, success, id);
        }
    }

}
