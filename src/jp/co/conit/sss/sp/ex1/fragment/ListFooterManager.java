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

import jp.co.conit.sss.sp.ex1.R;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;

/**
 * ListViewのFotterViewを管理するクラスです。
 * 
 * @author conit
 */
class ListFooterManager {

    /**
     * フッタービューの種別です。<br>
     * <br>
     * RELOAD :再読み込み…<br>
     * READMORE :もっと読む…<br>
     * LOADING :読み込み中…<br>
     * <br>
     * 
     * @author conit
     */
    static enum Footer {
        RELOAD, READMORE, LOADING
    }

    private Activity mActivity;

    /** 表示しているフッターの種別 */
    private Footer mSate;

    /** 再取得フッター */
    private View mRegetView = null;

    /** もっと見るフッター */
    private View mLookMoreView = null;

    /** 読み込み中フッター */
    private View mLoadingView = null;

    ListFooterManager(Activity activity) {
        mActivity = activity;
    }

    /**
     * フッタービューを設定します。<br>
     * 何らかのフッタービューが設定されている場合は、一旦削除してから設定し直します。
     * 
     * @param listView
     * @param type
     */
    void setFotterView(ListView listView, Footer type) {
        deleteFooterView(listView);
        listView.addFooterView(getFotterView(type));
    }

    /**
     * フッタービューを全て消します。
     * 
     * @param listView
     */
    void deleteFooterView(ListView listView) {
        if (listView.getFooterViewsCount() != 0) {
            listView.removeFooterView(getFotterView(mSate));
        }
    }

    /**
     * フッタービューを取得します。
     * 
     * @param type フッタービューの形式
     * @return
     */
    private View getFotterView(Footer type) {
        switch (type) {
            case LOADING:
                mSate = Footer.LOADING;
                return getLoadingView();
            case READMORE:
                mSate = Footer.READMORE;
                return getReadMoreView();
            case RELOAD:
                mSate = Footer.RELOAD;
                return getReloadView();
            default:
                return null;
        }
    }

    /**
     * ビューが生成されていない場合は生成し、再取得のフッタービューを取得します。
     */
    private View getReloadView() {
        if (mRegetView == null) {
            mRegetView = mActivity.getLayoutInflater().inflate(R.layout.list_item_footer_reload,
                    null);
        }
        return mRegetView;
    }

    /**
     * ビューが生成されていない場合は生成し、もっと見るフッタービューを取得します。
     */
    private View getReadMoreView() {
        if (mLookMoreView == null) {
            mLookMoreView = mActivity.getLayoutInflater().inflate(
                    R.layout.list_item_footer_read_more, null);
        }
        return mLookMoreView;
    }

    /**
     * ビューが生成されていない場合は生成し、読み込み中フッタービューを取得します。
     */
    private View getLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = mActivity.getLayoutInflater().inflate(R.layout.list_item_footer_loading,
                    null);
        }
        return mLoadingView;
    }

    /**
     * 表示しているFooterViewがもっと見るであるかを判定します。
     * 
     * @param view
     * @return
     */
    public boolean isReadMoreView(View view) {
        if (view == null) {
            throw new IllegalArgumentException("'view' must not be null.");
        }
        return view.equals(getReadMoreView());
    }

    /**
     * 表示しているFooterViewが再読み込みであるかを判定します。
     * 
     * @param view
     * @return
     */
    public boolean isReloadView(View view) {
        if (view == null) {
            throw new IllegalArgumentException("'view' must not be null.");
        }
        return view.equals(getReloadView());
    }

    /**
     * 表示しているFooterViewが読み込み中であるかを判定します。
     * 
     * @param view
     * @return
     */
    public boolean isLoadingView(View view) {
        if (view == null) {
            throw new IllegalArgumentException("'view' must not be null.");
        }
        return view.equals(getLoadingView());
    }

}
