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

import jp.co.conit.sss.sp.ex1.entity.SPResult;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * nonce取得APIの取得データをパースします。
 * 
 * @author conit
 */
public class NonceParser extends AbstSSSSPParser<String> {

    public NonceParser(Context context) {
        super(context);
    }

    @Override
    public SPResult<String> parse(String str) {
        if (str == null) {
            return SPResult.getSystemErrInstance(mContext);
        }

        String downloadUrl = null;
        try {
            JSONObject jsonObject = new JSONObject(str);
            downloadUrl = jsonObject.getString("nonce");
        } catch (JSONException e) {
            return SPResult.getSystemErrInstance(mContext);
        }
        return SPResult.getSuccessInstance(downloadUrl);
    }
}
