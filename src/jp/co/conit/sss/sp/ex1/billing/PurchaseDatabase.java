/*
 * Copyright (C) 2010 The Android Open Source Project
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

package jp.co.conit.sss.sp.ex1.billing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * An example database that records the state of each purchase. You should use
 * an obfuscator before storing any information to persistent storage. The
 * obfuscator should use a key that is specific to the device and/or user.
 * Otherwise an attacker could copy a database full of valid purchases and
 * distribute it to others.
 */
public class PurchaseDatabase {
    private static final String TAG = "PurchaseDatabase";

    private static final String DATABASE_NAME = "purchase.db";

    private static final int DATABASE_VERSION = 1;

    private static final String PURCHASE_HISTORY_TABLE_NAME = "history";

    // These are the column names for the purchase history table. We need a
    // column named "_id" if we want to use a CursorAdapter. The primary key is
    // the orderId so that we can be robust against getting multiple messages
    // from the server for the same purchase.
    static final String HISTORY_PRODUCT_ID_COL = "_id";

    static final String HISTORY_RECEIPT_COL = "receipt";

    private SQLiteDatabase mDb;

    private DatabaseHelper mDatabaseHelper;

    public PurchaseDatabase(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mDb = mDatabaseHelper.getWritableDatabase();
    }

    public void close() {
        mDatabaseHelper.close();
    }

    /**
     * Adds the given purchase information to the database and returns the total
     * number of times that the given product has been purchased.
     * 
     * @param orderId a string identifying the order
     * @param productId the product ID (sku)
     * @param purchaseState the purchase state of the product
     * @param purchaseTime the time the product was purchased, in milliseconds
     *            since the epoch (Jan 1, 1970)
     * @param developerPayload the developer provided "payload" associated with
     *            the order
     * @return the number of times the given product has been purchased.
     */
    public synchronized void updatePurchase(String productId, String receipt, long purchaseTime) {
        insertOrder(productId, receipt, purchaseTime);
        return;
    }

    /**
     * Inserts a purchased product into the database. There may be multiple rows
     * in the table for the same product if it was purchased multiple times or
     * if it was refunded.
     * 
     * @param orderId the order ID (matches the value in the product list)
     * @param productId the product ID (sku)
     * @param state the state of the purchase
     * @param purchaseTime the purchase time (in milliseconds since the epoch)
     * @param developerPayload the developer provided "payload" associated with
     *            the order.
     */
    private void insertOrder(String productId, String receipt, long purchaseTime) {

        ContentValues values = new ContentValues();
        values.put(HISTORY_PRODUCT_ID_COL, productId);
        values.put(HISTORY_RECEIPT_COL, receipt);
        mDb.replace(PURCHASE_HISTORY_TABLE_NAME, null /* nullColumnHack */, values);

    }

    public boolean deleteAll() {
        mDb.delete(PURCHASE_HISTORY_TABLE_NAME, null, null);
        mDb.close();
        mDatabaseHelper.close();
        return true;
    }

    public boolean delete(String productId) {

        int delete = mDb.delete(PURCHASE_HISTORY_TABLE_NAME, HISTORY_PRODUCT_ID_COL + " = ?",
                new String[] {
                    "" + productId
                });

        mDb.close();
        mDatabaseHelper.close();
        return (delete == 1) ? true : false;

    }

    /**
     * プロダクトに該当するレシート情報を取得します。<br>
     * 該当データが存在しない場合は{@code null}を、キャンセルなどでレシートが空白となっている場合は空文字を返却します。
     * 
     * @param productId
     * @return
     */
    public String readReceipt(String productId) {

        SQLiteDatabase readableDatabase = mDatabaseHelper.getReadableDatabase();

        StringBuilder sb = new StringBuilder();
        sb.append(HISTORY_PRODUCT_ID_COL);
        sb.append(" = ");
        sb.append("\"");
        sb.append(productId);
        sb.append("\"");

        Cursor cursor = readableDatabase.query(PURCHASE_HISTORY_TABLE_NAME, new String[] {
            HISTORY_RECEIPT_COL
        }, sb.toString(), null, null, null, null);

        cursor.moveToFirst();

        int count = cursor.getCount();
        if (count == 0) {
            cursor.close();
            cursor = null;
            readableDatabase.close();
            return null;
        }

        int receiptColumnIndex = cursor.getColumnIndex(HISTORY_RECEIPT_COL);
        String receipt = cursor.getString(receiptColumnIndex);
        cursor.close();
        cursor = null;
        readableDatabase.close();
        mDatabaseHelper.close();
        return receipt;
    }

    /**
     * This is a standard helper class for constructing the database.
     */
    private class DatabaseHelper extends SQLiteOpenHelper {
        // private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createPurchaseTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Production-quality upgrade code should modify the tables when
            // the database version changes instead of dropping the tables and
            // re-creating them.
            if (newVersion != DATABASE_VERSION) {
                Log.w(TAG, "Database upgrade from old: " + oldVersion + " to: " + newVersion);
                db.execSQL("DROP TABLE IF EXISTS " + PURCHASE_HISTORY_TABLE_NAME);
                createPurchaseTable(db);
                return;
            }
        }

        private void createPurchaseTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + PURCHASE_HISTORY_TABLE_NAME + "(" + HISTORY_PRODUCT_ID_COL
                    + " TEXT PRIMARY KEY, " + HISTORY_RECEIPT_COL + " TEXT)");
        }
    }
}
