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

import jp.co.conit.sss.sp.ex1.billing.Consts.PurchaseState;

/**
 * SamuraiPurchaseの注文情報検証APIで検証に成功した購入情報を格納するモデルです。
 * 
 * @author conit
 */
public class VerifiedProduct {

    private PurchaseState mPurchaseState;

    /** プロダクトID */
    private String mProductId;

    /** レシート（SamuraiPurchaseからファイルダウンロードURLを取得するのに使用） */
    private String mReceipt;

    /** ノーティフィケーションID（COMFIRMを送信するのに使用） */
    private String mNotificationId;

    /** 時間 */
    private long mPurchaseTime;

    /** オーダーID */
    private String mOrderId;

    public VerifiedProduct(PurchaseState purchaseState, String productId, String orderId,
            String receipt, String notificationId, long purchaseTime) {
        mPurchaseState = purchaseState;
        mProductId = productId;
        mOrderId = orderId;
        mReceipt = receipt;
        mNotificationId = notificationId;
        mPurchaseTime = purchaseTime;
    }

    public String getProductId() {
        return mProductId;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public String getReceipt() {
        return mReceipt;
    }

    public String getNotificationId() {
        return mNotificationId;
    }

    public long getPurchaseTime() {
        return mPurchaseTime;
    }

    public PurchaseState getPurchaseState() {
        return mPurchaseState;
    }

    @Override
    public String toString() {
        return "VerifiedProduct [mPurchaseState=" + mPurchaseState + ", mProductId=" + mProductId
                + ", mReceipt=" + mReceipt + ", mNotificationId=" + mNotificationId
                + ", mPurchaseTime=" + mPurchaseTime + ", mOrderId=" + mOrderId + "]";
    }

}
