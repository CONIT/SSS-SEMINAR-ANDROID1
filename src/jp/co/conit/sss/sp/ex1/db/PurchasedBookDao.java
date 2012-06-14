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

package jp.co.conit.sss.sp.ex1.db;

import jp.co.conit.sss.sp.ex1.entity.Book;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PurchasedBookDao {

    static final String TABLE_NAME = "purchased_book_table";

    static final String COLUMN_ID = "_id";

    static final String COLUMN_PRODUCT_ID = "product_id";

    static final String COLUMN_TITLE = "title";

    static final String COLUMN_AUTHOR = "author";

    static final String COLUMN_PURCHASED_DATE = "purchased_date";

    // 0:無料、1:有料
    static final String COLUMN_PAYMENT = "payment";

    private static final String[] PURCHASED_BOOK_COLUMNS = {
            COLUMN_ID, COLUMN_PRODUCT_ID, COLUMN_TITLE, COLUMN_AUTHOR, COLUMN_PURCHASED_DATE,
            COLUMN_PAYMENT
    };

    private SSSDbHelper mHelper;

    private SQLiteDatabase mSdb;

    private Cursor mCursor;

    public PurchasedBookDao(Activity activity) {
        mHelper = new SSSDbHelper(activity);
    }

    /**
     * 書籍データを登録します。
     * 
     * @param
     * @return
     */
    public boolean addData(Book book) {
        boolean result = true;

        mSdb = mHelper.getWritableDatabase();
        mSdb.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_PRODUCT_ID, book.getProductId());
            cv.put(COLUMN_TITLE, book.getTitle());
            cv.put(COLUMN_AUTHOR, book.getOutline());
            cv.put(COLUMN_PURCHASED_DATE, book.getPurchasedDateStr());
            int payment = (book.isFree()) ? 0 : 1;
            cv.put(COLUMN_PAYMENT, payment);

            long insertCount = mSdb.insert(TABLE_NAME, null, cv);
            result = (insertCount == -1) ? false : true;
            if (result) {
                mSdb.setTransactionSuccessful();
            }
        } finally {
            mSdb.endTransaction();
            close();
        }

        return result;
    }

    /**
     * プロダクトIDに該当する書籍データを削除します。
     * 
     * @param prodctId
     * @return
     */
    public boolean deleteData(String prodctId) {

        mSdb = mHelper.getWritableDatabase();
        int deleteCount = mSdb.delete(TABLE_NAME, COLUMN_PRODUCT_ID + " = ?", new String[] {
            "" + prodctId
        });
        close();
        return (deleteCount == 1) ? true : false;
    }

    /**
     * 全書籍情報を取得します。
     * 
     * @return
     */
    public Cursor getAllData() {

        mSdb = mHelper.getReadableDatabase();
        mCursor = mSdb.query(TABLE_NAME, PURCHASED_BOOK_COLUMNS, null, null, null, null,
                COLUMN_PURCHASED_DATE + " desc");
        return mCursor;
    }

    /**
     * 書籍が登録済みかをチェックします。
     * 
     * @param bookId
     * @return true:登録済み false:未登録
     */
    public boolean isExistBook(Book book) {

        mSdb = mHelper.getReadableDatabase();
        Cursor c = mSdb.query(TABLE_NAME, PURCHASED_BOOK_COLUMNS, COLUMN_PRODUCT_ID + " == ? ",
                new String[] {
                    (book.getProductId())
                }, null, null, null);

        int count = c.getCount();

        c.close();
        close();
        return (count == 1) ? true : false;
    }

    public void close() {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mHelper.close();
    }

}
