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

package jp.co.conit.sss.sp.ex1.fragment;

import java.io.File;

import jp.co.conit.sss.sp.ex1.R;
import jp.co.conit.sss.sp.ex1.billing.PurchaseDatabase;
import jp.co.conit.sss.sp.ex1.db.PurchasedBookDao;
import jp.co.conit.sss.sp.ex1.entity.Book;
import jp.co.conit.sss.sp.ex1.entity.Book.Builder;
import jp.co.conit.sss.sp.ex1.util.FileUtil;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ダウンロード済みの書籍一覧（マイブックス）を表示するListFragmentです。<br>
 * 
 * @author conit
 */
public class MybooksListFragment extends ListFragment {

    private static Activity mSelfActibity;

    private OnMypageItemSelectedListener mOnMypageItemSelectedListener;

    private MyBookAdapter mMyBookAdapter;

    private boolean mIsRemoveMode;

    private static PurchasedBookDao mPurchasedBookDao;

    /**
     * リストアイテムをタップした際のリスナーです。
     * 
     * @author
     */
    public interface OnMypageItemSelectedListener {

        public void onMypageItemSelected(Book book);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnMypageItemSelectedListener = (OnMypageItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMypageItemSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSelfActibity = getActivity();
        if (savedInstanceState != null) {
            mIsRemoveMode = savedInstanceState.getBoolean("remove_mode");
        }
        setEmptyText(getString(R.string.empty_purchase_book_data));
        mMyBookAdapter = new MyBookAdapter(mSelfActibity, null, true);
        setListAdapter(mMyBookAdapter);
        getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor c = (Cursor) parent.getItemAtPosition(position);
                String productId = c.getString(1);
                boolean isFree = (c.getInt(5) == 0) ? true : false;

                Builder builder = new Book.Builder(productId);
                Book book = builder.build();
                if (mIsRemoveMode) {

                    PurchasedBookDao purchasedBookDao = new PurchasedBookDao(mSelfActibity);
                    boolean isSuccessDeleteBookData = purchasedBookDao.deleteData(productId);

                    boolean isSuccessDeleteBookReceipt = false;
                    if (isFree) {
                        // 無料書籍は購入情報が登録されていないため
                        isSuccessDeleteBookReceipt = true;
                    } else {
                        PurchaseDatabase purchaseDatabase = new PurchaseDatabase(mSelfActibity);
                        isSuccessDeleteBookReceipt = purchaseDatabase.delete(productId);
                    }

                    File file = new File(FileUtil.generateBookFilePath(mSelfActibity, book));
                    if (file.exists()) {
                        file.delete();
                    }
                    if (isSuccessDeleteBookData && isSuccessDeleteBookReceipt) {
                        Toast.makeText(mSelfActibity,
                                getString(R.string.delete_purchase_book_data), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(mSelfActibity,
                                getString(R.string.delete_purchase_book_data_fail),
                                Toast.LENGTH_SHORT).show();
                    }
                    c.requery();

                } else {
                    mOnMypageItemSelectedListener.onMypageItemSelected(book);
                }

            }
        });
        mPurchasedBookDao = new PurchasedBookDao(mSelfActibity);
        getMybooksListAsync();

    }

    /**
     * Samurai Purchaseから取得したプロダクト一覧を表示するアダプターです。
     * 
     * @author conit
     */
    private class MyBookAdapter extends CursorAdapter {

        MyBookAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {

            TextView title = (TextView) view.findViewById(R.id.text_title);
            TextView author = (TextView) view.findViewById(R.id.text_author);

            String titleStr = cursor.getString(2);
            String authorStr = cursor.getString(3);

            title.setText(titleStr);
            author.setText(authorStr);

            ImageView removeImg = (ImageView) view.findViewById(R.id.img_remove);
            if (mIsRemoveMode) {
                removeImg.setVisibility(View.VISIBLE);
            } else {
                removeImg.setVisibility(View.GONE);
            }
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.list_item_mybook, parent, false);
            bindView(v, context, cursor);

            return v;
        }

    }

    /**
     * 非同期でマイブックス一覧（ダウンロード済み書籍一覧）を取得します。
     */
    private void getMybooksListAsync() {
        LoaderManager manager = getLoaderManager();
        manager.initLoader(0, null, mCallbacks);
    }

    /**
     * マイブックス一覧を取得するAsyncTaskLoaderです。
     * 
     * @author conit
     */
    private static class MybooksLoader extends AsyncTaskLoader<Cursor> {

        private Cursor mCursor;

        public MybooksLoader(Context context) {
            super(context);

        }

        @Override
        public Cursor loadInBackground() {
            return mPurchasedBookDao.getAllData();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            if (mCursor != null) {
                deliverResult(mCursor);
            }
            if (takeContentChanged() || mCursor == null) {
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            super.onStopLoading();
            cancelLoad();
        }

        @Override
        public void deliverResult(Cursor data) {

            if (isReset()) {
                if (mCursor != null) {
                    mCursor = null;
                }
                return;
            }

            mCursor = data;

            if (isStarted()) {
                super.deliverResult(data);
            }
        }

        @Override
        protected void onReset() {
            super.onReset();
            onStopLoading();

            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
            mCursor = null;
        }

        @Override
        public void onCanceled(Cursor cursor) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * マイブックス一覧データ取得処理 MybooksLoaderのコールバックです。
     */
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new MybooksLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor != null && !cursor.isClosed()) {
                mMyBookAdapter.swapCursor(cursor);
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            mMyBookAdapter.swapCursor(null);
        }
    };

    /**
     * 書籍削除モードの切替を行います。
     */
    public void setRemoveMode() {
        mIsRemoveMode = mIsRemoveMode ? false : true;
        if (mMyBookAdapter != null) {
            mMyBookAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("remove_mode", mIsRemoveMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelLoader();
        closeDb();
    }

    public void closeDb() {
        if (mPurchasedBookDao != null) {
            mPurchasedBookDao.close();
        }
    }

    /**
     * ダウンロー済み書籍の取得処理行うローダーをキャンセルします。
     */
    public void cancelLoader() {
        if (isAdded()) {
            LoaderManager manager = getLoaderManager();
            manager.destroyLoader(0);
        }
    }
}
