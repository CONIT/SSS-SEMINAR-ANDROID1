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

package jp.co.conit.sss.sp.ex1.io;

import java.util.HashMap;
import java.util.Map;

import jp.co.conit.sss.sp.ex1.entity.SPResult;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * JSONを解析するアブストラクトクラスです。<br>
 * SamuraiPurchaseから取得したJSONデータの解析には当クラスを継承したものを使用しています。 <br>
 * パース処理は各継承クラスの{@code parce} メソッドに実装します。<br>
 * <br>
 * パース処理を呼び出す場合、{@code getContent}メソッドを使用します。<br>
 * {@code getContent} メソッド内ではJSONデータのステータスコードを確認し、<br>
 * エラーの場合はエラーインスタンスを返却します。<br>
 * 正常の場合は、実装したパース処理で生成したオブジェクトが返却されます。
 * 
 * @author conit
 * @param <T>
 */
abstract class AbstSSSSPParser<T> {

    Context mContext;

    AbstSSSSPParser(Context context) {
        mContext = context;
    }

    public SPResult<T> getResult(String str) {

        // SamuraiPurchaseAPIの実行結果判定
        Map<String, String> statusMap = parseStatus(str);
        if (isSamuraiApiErr(statusMap)) {
            String statusCode = statusMap.get("code");
            String message = statusMap.get("message");
            return SPResult.getErrorInstance(statusCode, message);
        }

        return parse(str);
    }

    /**
     * SamuraiParchaceのWebAPIの実行結果がエラーかどうかを判定します。<br>
     * 正常ケースの場合ステータスコードは返却されないため、0件の場合は正常ケースとみなします。
     * 
     * @param statusMap
     * @return
     */
    private boolean isSamuraiApiErr(Map<String, String> statusMap) {
        return (statusMap.size() == 0) ? false : true;
    }

    /**
     * SamuraiParchaceのWebAPIの実行ステータス情報を取得します。<br>
     * エラー情報が存在しない場合、空のMapを返却します。
     * 
     * @param str
     * @return
     */
    private Map<String, String> parseStatus(String str) {

        Map<String, String> resultMap = new HashMap<String, String>();

        if (str == null) {
            return resultMap;
        }

        try {
            JSONObject jsonObject = new JSONObject(str);
            if (!jsonObject.has("error_info")) {
                return resultMap;
            }
            JSONObject databaseObject = jsonObject.getJSONObject("error_info");
            String statusCode = databaseObject.getString("code");
            resultMap.put("code", statusCode);
            String message = databaseObject.getString("message");
            resultMap.put("message", message);
        } catch (JSONException e) {
            resultMap.clear();
            return resultMap;
        }
        return resultMap;
    }

    /**
     * 各APIから取得したJSONをパースを実装してください。
     * 
     * @param str
     * @return
     */
    abstract SPResult<T> parse(String str);
}
