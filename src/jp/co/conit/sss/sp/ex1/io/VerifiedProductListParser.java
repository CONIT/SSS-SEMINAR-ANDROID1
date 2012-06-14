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

import jp.co.conit.sss.sp.ex1.billing.Consts.PurchaseState;
import jp.co.conit.sss.sp.ex1.entity.SPResult;
import jp.co.conit.sss.sp.ex1.entity.VerifiedProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * 注文情報検証APIの取得データをパースします。<br>
 * 異常系の注文情報は破棄します。
 * 
 * @author conit
 */
public class VerifiedProductListParser extends AbstSSSSPParser<List<VerifiedProduct>> {

    public VerifiedProductListParser(Context context) {
        super(context);
    }

    @Override
    public SPResult<List<VerifiedProduct>> parse(String str) {

        if (str == null) {
            return SPResult.getSystemErrInstance(mContext);
        }

        List<VerifiedProduct> verifiedProductList = new ArrayList<VerifiedProduct>();
        try {
            JSONObject jsonObject = new JSONObject(str);
            if (!jsonObject.has("orders")) {
                return SPResult.getSystemErrInstance(mContext);
            }
            JSONArray productArray = jsonObject.getJSONArray("orders");
            int length = productArray.length();
            for (int i = 0; i < length; i++) {

                try {
                    JSONObject productObj = productArray.getJSONObject(i);

                    int response = productObj.getInt("purchaseState");
                    PurchaseState purchaseState = PurchaseState.valueOf(response);
                    String notificationId = null;
                    if (productObj.has("notificationId")) {
                        notificationId = productObj.getString("notificationId");
                    }
                    String orderId = productObj.getString("orderId");
                    String productId = productObj.getString("productId");
                    long purchaseTime = productObj.getLong("purchaseTime");
                    String receipt = productObj.getString("receipt");
                    if (isEmpty(receipt)) {
                        receipt = "";
                    }

                    VerifiedProduct verifiedProduct = new VerifiedProduct(purchaseState, productId,
                            orderId, receipt, notificationId, purchaseTime);

                    verifiedProductList.add(verifiedProduct);
                } catch (JSONException e) {
                    // 異常系の注文情報は破棄します
                }
            }
        } catch (JSONException e) {
            return SPResult.getSystemErrInstance(mContext);
        }
        return SPResult.getSuccessInstance(verifiedProductList);
    }

    private boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        if (str.equals("NULL") || str.equals("null") || str.equals("")) {
            return true;
        }
        return false;
    }
}
