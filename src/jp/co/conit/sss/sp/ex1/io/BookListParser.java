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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.conit.sss.sp.ex1.entity.Book;
import jp.co.conit.sss.sp.ex1.entity.SPResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * プロダクトリストAPIの取得データをパースします。<br>
 * 異常系のプロダクトデータは破棄します。
 * 
 * @author conit
 */
public class BookListParser extends AbstSSSSPParser<List<Book>> {

    public BookListParser(Context context) {
        super(context);
    }

    @Override
    public SPResult<List<Book>> parse(String str) {

        if (str == null) {
            return SPResult.getSystemErrInstance(mContext);
        }

        List<Book> saleBookList = new ArrayList<Book>();
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray productArray = jsonObject.getJSONArray("products");
            int length = productArray.length();
            for (int i = 0; i < length; i++) {

                try {
                    JSONObject productObj = productArray.getJSONObject(i);

                    String productId = productObj.getString("product_id");
                    String title = productObj.getString("title");
                    String outline = productObj.getString("outline");

                    String publishDateStr = "";
                    Date date = null;
                    if (productObj.has("publish_date")) {
                        String pub = productObj.getString("publish_date");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            date = sdf.parse(pub);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        publishDateStr = sdf2.format(date);
                    }
                    // 有料無料判定
                    int free = productObj.getInt("is_free");
                    boolean isFree = false;
                    if (free == 1) {
                        isFree = true;
                    } else {
                        isFree = false;
                    }

                    int dlCount = 0;
                    if (productObj.has("summary")) {
                        dlCount = productObj.getInt("summary");
                    }

                    Book sb = new Book.Builder(productId).title(title).isFree(isFree)
                            .publishDateStr(publishDateStr).outline(outline).dlCount(dlCount)
                            .build();
                    saleBookList.add(sb);

                } catch (JSONException e) {
                    // 異常系のデータは破棄します
                }
            }
        } catch (JSONException e) {
            return SPResult.getSystemErrInstance(mContext);
        }
        return SPResult.getSuccessInstance(saleBookList);
    }
}
