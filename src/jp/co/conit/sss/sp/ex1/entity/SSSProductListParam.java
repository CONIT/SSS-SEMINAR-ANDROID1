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

/**
 * SamuraiPurchaseのWebAPIの実行する際にする各種パラーメターをまとめたモデルです。
 * 
 * @author conit
 */

public class SSSProductListParam {

    /** プロダクトID */
    private String mProductId;

    /** 言語 */
    private String mLang;

    /** 取得フィールド */
    private String mFields;

    /** 最大取得件数 */
    private int mLimit = -1;

    /** オフセット */
    private int mOffset = -1;

    /** ソートタイプ（昇順、降順） */
    private String mSorttype;

    /** ソート順 */
    private String mSortfield;

    /** カテゴリID */
    private String mCategory;

    /** サブカテゴリID */
    private String mSubCategory;

    /** タグ */
    private String mTag;

    /** サマリ種別（日次、週次、月次） */
    private int mSummaryType = -1;

    /** 料金種別 */
    private int mFreeType = -1;

    /** 集計基準日 */
    private String mSummaryDate;

    public SSSProductListParam() {

    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String productId) {
        mProductId = productId;
    }

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        mLang = lang;
    }

    public String getFields() {
        return mFields;
    }

    public void setFields(String fields) {
        mFields = fields;
    }

    public int getLimit() {
        return mLimit;
    }

    public void setLimit(int limit) {
        mLimit = limit;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(int offset) {
        mOffset = offset;
    }

    public String getSorttype() {
        return mSorttype;
    }

    public void setSorttype(String sorttype) {
        mSorttype = sorttype;
    }

    public String getSortfield() {
        return mSortfield;
    }

    public void setSortfield(String sortfield) {
        mSortfield = sortfield;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getSubCategory() {
        return mSubCategory;
    }

    public void setSubCategory(String subCategory) {
        mSubCategory = subCategory;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public int getSummaryType() {
        return mSummaryType;
    }

    public void setSummaryType(int summaryType) {
        mSummaryType = summaryType;
    }

    public String getSummaryDate() {
        return mSummaryDate;
    }

    public void setSummaryDate(String summaryDate) {
        mSummaryDate = summaryDate;
    }

    public int getFreeType() {
        return mFreeType;
    }

    public void setFreeType(int freeType) {
        mFreeType = freeType;
    }

}
