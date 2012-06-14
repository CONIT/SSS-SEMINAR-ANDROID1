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

import java.util.ArrayList;
import java.util.List;

import jp.co.conit.sss.sp.ex1.entity.SPResult;
import jp.co.conit.sss.sp.ex1.entity.DownloadFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * ファイル一覧取得APIの取得データをパースします。
 * 
 * @author conit
 */
public class DownloadFileListParser extends AbstSSSSPParser<List<DownloadFile>> {

    public DownloadFileListParser(Context context) {
        super(context);
    }

    @Override
    public SPResult<List<DownloadFile>> parse(String str) {

        if (str == null) {
            return SPResult.getSystemErrInstance(mContext);
        }

        List<DownloadFile> fileList = new ArrayList<DownloadFile>();
        try {
            JSONObject jsonObject = new JSONObject(str);
            if (!jsonObject.has("files")) {
                return SPResult.getSystemErrInstance(mContext);
            }
            JSONArray fileArray = jsonObject.getJSONArray("files");
            int length = fileArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject fileObj = fileArray.getJSONObject(i);
                String filename = fileObj.getString("file_name");
                String dowmloadUrl = fileObj.getString("download_url");
                String hash = fileObj.getString("hash");
                fileList.add(new DownloadFile(filename, dowmloadUrl, hash));
            }
        } catch (JSONException e) {
            return SPResult.getSystemErrInstance(mContext);
        }
        return SPResult.getSuccessInstance(fileList);
    }
}
