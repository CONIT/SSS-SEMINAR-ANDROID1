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

import static jp.co.conit.sss.sp.ex1.db.PurchasedBookDao.COLUMN_AUTHOR;
import static jp.co.conit.sss.sp.ex1.db.PurchasedBookDao.COLUMN_ID;
import static jp.co.conit.sss.sp.ex1.db.PurchasedBookDao.COLUMN_PAYMENT;
import static jp.co.conit.sss.sp.ex1.db.PurchasedBookDao.COLUMN_PRODUCT_ID;
import static jp.co.conit.sss.sp.ex1.db.PurchasedBookDao.COLUMN_PURCHASED_DATE;
import static jp.co.conit.sss.sp.ex1.db.PurchasedBookDao.COLUMN_TITLE;
import static jp.co.conit.sss.sp.ex1.db.PurchasedBookDao.TABLE_NAME;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class SSSDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "sss_sp.db";

    SSSDbHelper(Context c) {
        super(c, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        StringBuilder createSql = new StringBuilder();
        createSql.append("create table " + TABLE_NAME + " ( ");
        createSql.append(COLUMN_ID + " integer primary key autoincrement not null,");
        createSql.append(COLUMN_PRODUCT_ID + " text unique not null,");
        createSql.append(COLUMN_TITLE + " text not null,");
        createSql.append(COLUMN_AUTHOR + " text not null,");
        createSql.append(COLUMN_PURCHASED_DATE + " text not null, ");
        createSql.append(COLUMN_PAYMENT + " integer not null ");
        createSql.append(")");

        db.execSQL(createSql.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }

}
