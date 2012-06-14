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

package jp.co.conit.sss.sp.ex1.entity;

import jp.co.conit.sss.sp.ex1.R;
import android.content.Context;

/**
 * SamuraiPurchase APIからの取得し、JSONをパースした結果を格納するモデルクラスです。
 * 
 * @author conit
 * @param <T>
 */
public final class SPResult<T> {

    /** コンテンツ */
    private T mContent;

    /** エラーメッセージ */
    private String mMessage;

    /** エラーステータスコード */
    private String mStatusCode;

    /** エラー状態 */
    private boolean mIsError;

    private SPResult(T content, String statusCode, String message, boolean isErr) {
        mContent = content;
        mMessage = message;
        mStatusCode = statusCode;
        mIsError = isErr;
    }

    /**
     * データを格納したインスタンスを生成します。<br>
     * エラーメッセージ、ステータスコードは保持しません。
     * 
     * @param <T>
     * @param content
     * @return
     */
    public static <T> SPResult<T> getSuccessInstance(T content) {
        return new SPResult<T>(content, null, null, false);
    }

    /**
     * エラー情報を格納したインスタンスを生成します。<br>
     * エラーメッセージ、ステータスコードのみを保持します。
     * 
     * @param <T>
     * @param statusCode
     * @param message
     * @return
     */
    public static <T> SPResult<T> getErrorInstance(String statusCode, String message) {
        return new SPResult<T>(null, statusCode, message, true);
    }

    public T getContent() {
        return mContent;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getStatusCode() {
        return mStatusCode;
    }

    public boolean isError() {
        return mIsError;
    }

    /**
     * ネットワークがオフになっている場合に表示するエラー情報のインスタンスを取得します。
     * 
     * @param <T>
     * @param context
     * @return
     */
    public static <T> SPResult<T> getDisconectErrInstance(Context context) {
        return SPResult
                .getErrorInstance("", context.getString(R.string.dialog_error_not_connected));
    }

    /**
     * サーバー側でなんらかのエラーが発生した場合に表示するエラー情報のインスタンスを取得します。
     * 
     * @param <T>
     * @param context
     * @return
     */
    public static <T> SPResult<T> getSeverErrInstance(Context context) {
        return SPResult.getErrorInstance("",
                context.getString(R.string.dialog_title_error_connection_down));
    }

    /**
     * サーバー側から取得したデータが空の場合に表示するエラーインスタンスを取得します。<br>
     * 
     * @param <T>
     * @param context
     * @return
     */
    public static <T> SPResult<T> getSystemErrInstance(Context context) {
        return SPResult.getErrorInstance("", context.getString(R.string.dialog_title_error_system));
    }

}
