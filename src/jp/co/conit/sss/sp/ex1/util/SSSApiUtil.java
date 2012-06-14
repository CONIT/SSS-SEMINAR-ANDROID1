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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import jp.co.conit.sss.sp.ex1.entity.Book;
import jp.co.conit.sss.sp.ex1.entity.DownloadFile;
import jp.co.conit.sss.sp.ex1.entity.SPResult;
import jp.co.conit.sss.sp.ex1.entity.SSSProductListParam;
import jp.co.conit.sss.sp.ex1.entity.VerifiedProduct;
import jp.co.conit.sss.sp.ex1.io.BookListParser;
import jp.co.conit.sss.sp.ex1.io.DownloadFileListParser;
import jp.co.conit.sss.sp.ex1.io.NonceParser;
import jp.co.conit.sss.sp.ex1.io.VerifiedProductListParser;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;

/**
 * SamuraiPurchaseのAPIの実行するユーティリティクラスです。
 * 
 * @author conit
 */
public final class SSSApiUtil {

    private static final String DOMAIN = "https://ap344-cp1-d46b04eafe23b17925b1287449db25ca-apisrv.conit.jp/v2/";

    private SSSApiUtil() {

    }

    /**
     * 注文情報を検証します。
     * 
     * @param signedData
     * @param signature
     * @return
     */
    public static SPResult<List<VerifiedProduct>> orderVerify(Context context, String signedData,
            String signature) {

        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(DOMAIN);
        sbUrl.append("android/order_verify/");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("token", Util.getToken(Util.getSignature(context))));
        params.add(new BasicNameValuePair("token_method", "ndk"));
        params.add(new BasicNameValuePair("signed_data", signedData));
        params.add(new BasicNameValuePair("signature", signature));

        UrlEncodedFormEntity urlEncodedFormEntity = null;
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String str = null;
        try {
            str = HttpUtil.post(sbUrl.toString(), urlEncodedFormEntity);
        } catch (Exception e) {
            return SPResult.getSeverErrInstance(context);
        }
        return new VerifiedProductListParser(context).getResult(str);

    }

    /**
     * ダウンロードファルのリストを取得します。<br>
     * 有料プロダクトのファイルリストを取得する場合はreceiptは必須となります。<br>
     * 無料プロダクトのファイルリストを取得する場合はreceiptに{@code null}をして下さい。
     * 
     * @param context
     * @param productId
     * @param receipt
     * @return
     */
    public static SPResult<List<DownloadFile>> getFileList(Context context, String productId,
            String receipt) {

        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(DOMAIN);
        sbUrl.append("android/files/");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("token", Util.getToken(Util.getSignature(context))));
        params.add(new BasicNameValuePair("token_method", "ndk"));
        params.add(new BasicNameValuePair("product_id", productId));
        if (receipt != null) {
            params.add(new BasicNameValuePair("receipt", receipt));
        }
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String str = null;
        try {
            str = HttpUtil.post(sbUrl.toString(), entity);
        } catch (Exception e) {
            return SPResult.getSeverErrInstance(context);
        }
        return new DownloadFileListParser(context).getResult(str);
    }

    /**
     * nonceを取得します。
     * 
     * @param context
     * @return
     */
    public static SPResult<String> getNonce(Context context) {

        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(DOMAIN);
        sbUrl.append("android/nonce/");
        sbUrl.append(getTokenUrlParam(context));

        String str = null;
        try {
            str = HttpUtil.get(sbUrl.toString());
        } catch (Exception e) {
            return SPResult.getSeverErrInstance(context);
        }
        return new NonceParser(context).getResult(str);

    }

    /**
     * プロダクトリストを取得します。
     * 
     * @param context {@code null}禁止です。
     * @param param {@code null}禁止です。
     * @return
     */
    public static SPResult<List<Book>> getProductList(Context context, SSSProductListParam param) {

        if (context == null) {
            throw new IllegalArgumentException("'context' must not be null.");
        }

        if (param == null) {
            throw new IllegalArgumentException("'ProductListParameter' must not be null.");
        }

        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(DOMAIN);
        sbUrl.append("android/products/");

        sbUrl.append(getTokenUrlParam(context));

        if (!isEmpty(param.getFields())) {
            sbUrl.append("&fields=");
            sbUrl.append(param.getFields());
        }
        if (!isEmpty(param.getSorttype())) {
            sbUrl.append("&sorttype=");
            sbUrl.append(param.getSorttype());
        }
        if (!isEmpty(param.getSortfield())) {
            sbUrl.append("&sortfield=");
            sbUrl.append(param.getSortfield());
        }

        // ランキングの場合のURLの生成処理
        if (param.getSummaryType() != -1) {
            sbUrl.append("&summary_type=");
            sbUrl.append(param.getSummaryType());
        }
        if (param.getFreeType() != -1) {
            sbUrl.append("&fee_type=");
            sbUrl.append(param.getFreeType());
        }

        // オフセットの設定
        if (param.getOffset() != -1) {
            sbUrl.append("&offset=");
            sbUrl.append(param.getOffset());
        }

        // 取得上限の設定
        if (param.getLimit() != -1) {
            sbUrl.append("&limit=");
            sbUrl.append(param.getLimit());
        }

        String str = null;
        try {
            str = HttpUtil.get(sbUrl.toString());
        } catch (Exception e) {
            return SPResult.getSeverErrInstance(context);
        }
        return new BookListParser(context).getResult(str);
    }

    /**
     * SamuraiPurchaseAPIで使用するトークンパラメータ文字列を生成します。
     * 
     * @return
     */
    private static String getTokenUrlParam(Context context) {

        StringBuilder sb = new StringBuilder();
        sb.append("?token=");
        sb.append(getToken(context));
        sb.append("&token_method=ndk");

        return sb.toString();
    }

    /**
     * トークンを取得します。
     * 
     * @return
     */
    private static String getToken(Context context) {
        String token = null;
        try {
            token = URLEncoder.encode(Util.getToken(Util.getSignature(context)), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 文字列の空判定処理です。
     * 
     * @param value
     * @return
     */
    private static boolean isEmpty(String value) {
        if (value == null || "".equals(value)) {
            return true;
        } else {
            return false;
        }
    }

}
