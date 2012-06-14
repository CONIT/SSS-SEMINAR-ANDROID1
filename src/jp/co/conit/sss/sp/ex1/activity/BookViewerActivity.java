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

package jp.co.conit.sss.sp.ex1.activity;

import jp.co.conit.sss.sp.ex1.R;
import jp.co.conit.sss.sp.ex1.fragment.BookViewerFragment.OnCancelLoadLisntener;
import jp.co.conit.sss.sp.ex1.fragment.BookViewerFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * 書籍を閲覧するアクティビティです。
 * 
 * @author conit
 */
public class BookViewerActivity extends FragmentActivity implements OnCancelLoadLisntener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookviewer);

        Intent intent = getIntent();
        String path = intent.getStringExtra("book_path");

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        BookViewerFragment fragment = BookViewerFragment.newInstance("file://" + path);
        transaction.replace(R.id.frame_web, fragment);
        transaction.commit();

    }

    @Override
    public void cancelLoad() {
        finish();
    }
}
