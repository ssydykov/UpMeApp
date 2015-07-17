package com.example.saken.upmeapp;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ServiceHandler {
    static String response = null;
    public final static int SIGNIN = 0;
    public final static int SIGNUP = 1;
    public final static int GET = 2;
    public final static int POST = 3;

    public String[] makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null, null, null);
    }

    public ServiceHandler() {

    }

    public String[] makeServiceCall(String url, int method,
                                    List<NameValuePair> params, String csrftoken, String sessionid) {

        String[] arrayListResponse = new String[3];

        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == SIGNIN){

                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                CookieStore cookieStore = new BasicCookieStore();
                HttpContext httpContext = new BasicHttpContext();
                httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                httpResponse = httpClient.execute(httpPost, httpContext);
                List<Cookie> cookies = cookieStore.getCookies();
                Log.d("Cookie store", cookieStore.toString());

                arrayListResponse[0] = cookies.get(0).getValue();
                arrayListResponse[1] = cookies.get(1).getValue();

            } else if (method == SIGNUP) {

                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                CookieStore cookieStore = new BasicCookieStore();
                HttpContext httpContext = new BasicHttpContext();
                httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                httpResponse = httpClient.execute(httpPost, httpContext);
                List<Cookie> cookies = cookieStore.getCookies();
                Log.d("Cookie store", cookieStore.toString());

                arrayListResponse[0] = cookies.get(0).getValue();
                arrayListResponse[1] = cookies.get(1).getValue();

            } else if (method == POST) {

                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                String header = "csrftoken=" + csrftoken + "; sessionid=" + sessionid + ";";
                httpPost.addHeader("cookie", header);

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {

                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                String header = "csrftoken=" + csrftoken + "; sessionid=" + sessionid + ";";
                httpGet.addHeader("cookie", header);

                httpResponse = httpClient.execute(httpGet);
            }

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            arrayListResponse[2] = response;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayListResponse;
    }
}

