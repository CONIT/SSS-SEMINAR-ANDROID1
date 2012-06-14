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

import java.util.ArrayList;
import java.util.List;

import jp.co.conit.sss.sp.ex1.R;
import jp.co.conit.sss.sp.ex1.entity.Book;
import jp.co.conit.sss.sp.ex1.entity.SPResult;
import jp.co.conit.sss.sp.ex1.entity.SSSProductListParam;
import jp.co.conit.sss.sp.ex1.fragment.ListFooterManager.Footer;
import jp.co.conit.sss.sp.ex1.util.HttpUtil;
import jp.co.conit.sss.sp.ex1.util.SSSApiUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/**
 * ストアの書籍一覧を表示する抽象ListFragmentです。<br>
 * <br>
 * 当クラスを継承する場合、generateAdapterメソッドとgenerateSSSParamメソッドを実装する必要があります。<br>
 * generateAdapterメソッド:ストアの書籍一覧を提供するアダプタの生成処理を実装します。＜br＞
 * generateSSSParamメソッド:SamuraiPurchaseのプロダクトリスト取得APIのパラメータを生成処理を実装します。
 * 
 * @author conit
 */
abstract public class AbstStoreListFragment extends ListFragment {

    /** 取得単位 */
    static final int DATA_UNIT = 5;

    static AbstStoreListFragment mSelf;

    StoreFragmentListener mStoreFragmentListener;

    List<Book> mStoreBookList = new ArrayList<Book>();

    ProductListGetTask mProductListGetTask;

    ArrayAdapter<Book> mStoreBookAdapter;

    int mOffset4BookList = 0;

    ListFooterManager mListFooterManager;

    boolean mIsFirst = true;

    boolean mIsComp = false;

    /**
     * 更新ボタンをタップした際のリスナーです。
     * 
     * @author
     */
    public interface StoreFragmentListener {

        public void onStoreItemSelected(Book book);

        public void onLoad();

        public void onLoadCallback();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mStoreFragmentListener = (StoreFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement StoreFragmentListener");
        }
        mListFooterManager = new ListFooterManager(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSelf = this;

        if (this instanceof StoreListDLFragment) {
            // 初回起動時に注意事項確認済みかを判定
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean isConfirmed = sp.getBoolean("confirmed", false);
            if (!isConfirmed) {
                FragmentManager fm = getFragmentManager();
                DialogFragment prev = (DialogFragment) getFragmentManager().findFragmentByTag(
                        "annotation");
                if (prev == null) {
                    AlertDialogFragment adf = AlertDialogFragment.newInstance();
                    adf.show(fm, "annotation");
                }
            }
        }

        initList();
    }

    /**
     * アダプターの生成処理です。
     * 
     * @return
     */
    abstract ArrayAdapter<Book> generateAdapter();

    /**
     * SamuraiPurchaseのプロダクトリスト取得APIに使用するパラメータを生成します。
     * 
     * @return
     */
    abstract SSSProductListParam generateSSSParam();

    /**
     * 非同期でプロダクトリストを取得します。
     */
    private void getProductListAsync() {
        mProductListGetTask = new ProductListGetTask(getActivity().getApplicationContext());
        mProductListGetTask.execute(generateSSSParam());
    }

    /**
     * プロダクトリストを取得するタスクです。
     * 
     * @author conit
     */
    class ProductListGetTask extends AsyncTask<SSSProductListParam, Void, SPResult<List<Book>>> {

        private Context mContext;

        private ProductListGetTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected SPResult<List<Book>> doInBackground(SSSProductListParam... params) {
            if (!HttpUtil.isConnected(mContext)) {
                return SPResult.getDisconectErrInstance(mContext);
            }
            return SSSApiUtil.getProductList(mContext, params[0]);
        }

        @Override
        protected void onPostExecute(SPResult<List<Book>> result) {
            if (isCancelled()) {
                return;
            }

            if (!result.isError()) {
                updateNewBook(result.getContent());
            } else {
                mListFooterManager.setFotterView(getListView(), Footer.RELOAD);
            }
            mStoreFragmentListener.onLoadCallback();
        }
    }

    /**
     * 書籍リストに取得したデータを追加し表示します。<br>
     * 取得データが０件の場合はトーストでユーザーに通知し、フッタービューは表示しません。<br>
     * １件以上の場合はリストにデータを追加し、もっと読むフッタービューを表示します。
     */
    private void updateNewBook(List<Book> bookList) {

        View root = getView();
        if (root == null) {
            return;
        }

        mOffset4BookList = mOffset4BookList + DATA_UNIT;

        for (Book sb : bookList) {
            mStoreBookAdapter.add(sb);
        }
        mStoreBookAdapter.notifyDataSetChanged();

        if (bookList.size() == 0) {
            Toast.makeText(getActivity().getApplicationContext(),
                    getText(R.string.get_all_product_list), Toast.LENGTH_SHORT).show();
            mListFooterManager.deleteFooterView(getListView());
            mIsComp = true;
        } else {
            mListFooterManager.setFotterView(getListView(), Footer.READMORE);
        }

    }

    /**
     * リストのデータを破棄し、オフセット０から書籍リストを再取得します。
     */
    public void reload() {

        if (mStoreBookAdapter != null) {
            mStoreBookAdapter.clear();
            mStoreBookAdapter.notifyDataSetChanged();
            mStoreBookList.clear();
            mListFooterManager.setFotterView(getListView(), Footer.LOADING);
            mOffset4BookList = 0;
            getProductListAsync();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelProductListGetTask();
    }

    /**
     * プロダクトリスト取得処理が実行中の場合、キャンセルします。
     */
    public void cancelProductListGetTask() {
        if (mProductListGetTask != null && !mProductListGetTask.isCancelled()) {
            mProductListGetTask.cancel(true);
        }
    }

    /**
     * 初回起動時に表示するアラートダイアログです。
     * 
     * @author conit
     */
    private static class AlertDialogFragment extends DialogFragment {

        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

            dlg.setTitle(getActivity().getString(R.string.app_about));
            dlg.setMessage(getActivity().getString(R.string.app_annotation));
            dlg.setPositiveButton(getString(R.string.dialog_btn_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mSelf.saveBooted();
                            mSelf.removeDialogFragment();
                        }
                    });
            dlg.setNegativeButton(getString(R.string.dialog_btn_cancel), new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mSelf.removeDialogFragment();
                    mSelf.getActivity().finish();
                }
            });
            dlg.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (KeyEvent.KEYCODE_BACK == keyCode) {
                        mSelf.removeDialogFragment();
                        mSelf.getActivity().finish();
                        return true;
                    }
                    return false;
                }
            });

            return dlg.create();
        }
    }

    /**
     * アダプターを生成し、リストを初期化します。
     */
    private void initList() {
        mStoreBookAdapter = generateAdapter();
        mListFooterManager.setFotterView(getListView(), Footer.LOADING);
        setListAdapter(mStoreBookAdapter);
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mListFooterManager.isReadMoreView(view)
                        || mListFooterManager.isReloadView(view)) {
                    mStoreFragmentListener.onLoad();
                    mListFooterManager.setFotterView(getListView(), Footer.LOADING);
                    getProductListAsync();
                    return;
                } else if (mListFooterManager.isLoadingView(view)) {
                    // 何もしない
                } else {
                    Book item = (Book) parent.getItemAtPosition(position);
                    mStoreFragmentListener.onStoreItemSelected(item);
                }
            }
        });

        if (mIsFirst) {
            getProductListAsync();
            mIsFirst = false;
        } else {
            if (mIsComp) {
                mListFooterManager.deleteFooterView(getListView());
            } else {
                mListFooterManager.setFotterView(getListView(), Footer.READMORE);
            }
        }

    }

    /**
     * ダイアログフラグメントが表示されている場合、消します。
     * 
     * @param type
     */
    private void removeDialogFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment prev = (DialogFragment) getFragmentManager().findFragmentByTag("annotation");
        if (prev != null) {
            prev.dismiss();
            ft.remove(prev);
            ft.commit();
        }
    }

    /**
     * SharedPreferencesに注意事項確認済の情報を保存します。
     */
    private void saveBooted() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.edit().putBoolean("confirmed", true).commit();
    }
}
