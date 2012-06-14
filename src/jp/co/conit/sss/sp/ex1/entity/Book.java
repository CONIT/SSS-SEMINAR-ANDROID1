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

import java.io.Serializable;

/**
 * 書籍のモデルクラスです。
 * 
 * @author conit
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 9166022254081671997L;

    /** プロダクトID（マーケットのアプリ内サービス IDと同値） */
    private String mProductId;

    /** タイトル */
    private String mTitle;

    /** 概要(当アプリでは著者名として使用する) */
    private String mOutline;

    /** 無料かどうか */
    private boolean mIsFree;

    /** 金額 */
    private int mPrice;

    /** メタ情報ファイルのハッシュ値 */
    private String mFileHash;

    /** 公開日 */
    private String mPublishDateStr;

    /** 購入日 */
    private String mPurchasedDateStr;

    /** DL数 */
    private int mDlCount;

    private Book(Builder builder) {

        mProductId = builder.productId;
        mOutline = builder.outline;
        mTitle = builder.title;
        mIsFree = builder.isFree;
        mPrice = builder.price;
        mFileHash = builder.fileHash;
        mPublishDateStr = builder.publishDateStr;
        mDlCount = builder.dlCount;
    }

    public static class Builder {

        private String productId;

        private String outline;

        private String title;

        private boolean isFree;

        private int price;

        private String fileHash;

        private String publishDateStr;

        private int dlCount;

        public Builder(String broductId) {
            this.productId = broductId;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder outline(String outline) {
            this.outline = outline;
            return this;
        }

        public Builder isFree(boolean isFree) {
            this.isFree = isFree;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder fileHash(String fileHash) {
            this.fileHash = fileHash;
            return this;
        }

        public Builder publishDateStr(String publishDateStr) {
            this.publishDateStr = publishDateStr;
            return this;
        }

        public Builder dlCount(int dlCount) {
            this.dlCount = dlCount;
            return this;
        }

        public Book build() {
            return new Book(this);
        }

    }

    public String getProductId() {
        return mProductId;
    }

    public String getOutline() {
        return mOutline;
    }

    public int getPrice() {
        return mPrice;
    }

    public String getFileHash() {
        return mFileHash;
    }

    public String getPublishDateStr() {
        return mPublishDateStr;
    }

    public String getPurchasedDateStr() {
        return mPurchasedDateStr;
    }

    public void setPurchasedDateStr(String purchasedDate) {
        mPurchasedDateStr = purchasedDate;
    }

    public boolean isFree() {
        return mIsFree;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getDlCount() {
        return mDlCount;
    }

}
