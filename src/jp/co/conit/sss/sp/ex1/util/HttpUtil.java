/*
 * Copyright (C) 2012 CONIT Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.conit.sss.sp.ex1.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * HTTP通信を扱うユーティリティクラスです。
 * 
 * @author conit
 */
public final class HttpUtil {

    /**
     * コネクションのタイムアウト時間（ms）.
     */
    private static final int TIMEOUT_CONNECTION = 1000 * 60;

    /**
     * ソケットのタイムアウト時間（ms）.
     */
    private static final int TIMEOUT_SOCKET = 1000 * 60;

    private HttpUtil() {
    }

    /**
     * ネットワークが利用可能かを判定します。
     * 
     * @param context {@code null}禁止です。
     * @return {@code true}:利用可、{@code false}:利用不可
     */
    public static boolean isConnected(Context context) {

        if (context == null) {
            throw new IllegalArgumentException("'context' must not be null.");
        }

        boolean isConnectied = false;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            isConnectied = true;
        }
        return isConnectied;
    }

    /**
     * GETしてデータを取得します。
     * 
     * @param url {@code null}禁止です。
     * @return
     */
    public static String get(String url) throws Exception {

        if (url == null) {
            throw new IllegalArgumentException("'url' must not be null.");
        }

        String result = null;

        DefaultHttpClient httpclient = new DefaultHttpClient(getHttpParam());
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpclient.execute(httpGet);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            response.getEntity().writeTo(byteArrayOutputStream);
            result = byteArrayOutputStream.toString();
        } catch (ClientProtocolException e) {
            throw new Exception();
        } catch (IllegalStateException e) {
            throw new Exception();
        } catch (IOException e) {
            throw new Exception();
        }

        return result;
    }

    /**
     * POSTしてデータを取得します。
     * 
     * @param url APIのURL
     * @param httpEntity 各種パラメータ
     * @return
     */
    public static String post(String url, UrlEncodedFormEntity httpEntity) throws Exception {

        if (url == null) {
            throw new IllegalArgumentException("'url' must not be null.");
        }
        if (httpEntity == null) {
            throw new IllegalArgumentException("'httpEntity' must not be null.");
        }

        String result = null;

        DefaultHttpClient httpclient = new DefaultHttpClient(getHttpParam());
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(httpEntity);
            HttpResponse response = httpclient.execute(httpPost);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            response.getEntity().writeTo(byteArrayOutputStream);
            result = byteArrayOutputStream.toString();
        } catch (ClientProtocolException e) {
            throw new Exception();
        } catch (IllegalStateException e) {
            throw new Exception();
        } catch (IOException e) {
            throw new Exception();
        }

        return result;
    }

    /**
     * HTTP通信のパラメータを生成します。
     * 
     * @return
     */
    private static HttpParams getHttpParam() {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION);
        HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
        return httpParameters;
    }

}
