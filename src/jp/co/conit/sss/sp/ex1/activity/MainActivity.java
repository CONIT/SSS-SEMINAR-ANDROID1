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

import java.util.HashMap;

import jp.co.conit.sss.sp.ex1.R;
import jp.co.conit.sss.sp.ex1.entity.Book;
import jp.co.conit.sss.sp.ex1.fragment.AbstStoreListFragment.StoreFragmentListener;
import jp.co.conit.sss.sp.ex1.fragment.MybooksListFragment.OnMypageItemSelectedListener;
import jp.co.conit.sss.sp.ex1.fragment.ViewPagerFragment;
import jp.co.conit.sss.sp.ex1.util.FileUtil;
import jp.co.conit.sss.sp.ex1.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;

/**
 * アプリ起動時に呼び出されます。<br>
 * ストア情報（SamuraiPurchaseから取得した書籍一覧）とマイブック情報（ダウンロードした書籍一覧）をタブで切り替え表示します。
 * 
 * @author conit
 */
public class MainActivity extends FragmentActivity implements StoreFragmentListener,
        OnMypageItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final boolean SHOW_SIGUNATURE = false;

    private static final String TAG_STORE = "store";

    private static final String TAG_MY_BOOK = "my_book";

    private Activity mSelf;

    private TabHost mTabHost;

    private TabManager mTabManager;

    private ImageButton mReloadImgBtn;

    private ImageButton mRemoveImgBtn;

    private ImageButton mSettingImgBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSelf = this;

        // シグネチャー表示モードの場合、アプリの操作は一切できないようにする
        if (SHOW_SIGUNATURE) {
            AlertDialogFragment adf = AlertDialogFragment.newInstance();
            adf.show(getSupportFragmentManager(), "signature");
            return;
        }

        mReloadImgBtn = (ImageButton) findViewById(R.id.btn_reload);
        mReloadImgBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mReloadImgBtn.setEnabled(false);

                FragmentManager fm = getSupportFragmentManager();
                ViewPagerFragment f = (ViewPagerFragment) fm.findFragmentByTag(TAG_STORE);
                f.reload();
            }
        });

        mRemoveImgBtn = (ImageButton) findViewById(R.id.btn_remove);
        mRemoveImgBtn.setTag("close");
        mRemoveImgBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String str = (String) mRemoveImgBtn.getTag();
                if (str.equals("open")) {
                    mRemoveImgBtn.setImageResource(R.drawable.btn_remove_close_selector);
                    mRemoveImgBtn.setTag("close");
                } else {
                    mRemoveImgBtn.setImageResource(R.drawable.btn_remove_open_selector);
                    mRemoveImgBtn.setTag("open");
                }

                FragmentManager fm = getSupportFragmentManager();
                ViewPagerFragment f = (ViewPagerFragment) fm.findFragmentByTag(TAG_MY_BOOK);
                f.setRemoveMode();

            }
        });

        mSettingImgBtn = (ImageButton) findViewById(R.id.btn_settings);
        mSettingImgBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mSelf, SettingsActivity.class);
                startActivity(intent);
            }
        });

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
        CustomTabContent customTabContent = new CustomTabContent();
        Bundle bundle = new Bundle();
        bundle.putInt("tab_type", 1);
        mTabManager.addTab(
                mTabHost.newTabSpec(TAG_STORE).setIndicator(
                        customTabContent.generateCustomTabView(CustomTabContent.STORE)),
                ViewPagerFragment.class, bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("tab_type", 2);
        mTabManager.addTab(
                mTabHost.newTabSpec(TAG_MY_BOOK).setIndicator(
                        customTabContent.generateCustomTabView(CustomTabContent.SHELF)),
                ViewPagerFragment.class, bundle2);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTabHost != null) {
            outState.putString("tab", mTabHost.getCurrentTabTag());
        }
    }

    public static class TabManager implements TabHost.OnTabChangeListener {

        private final FragmentActivity mActivity;

        private final TabHost mTabHost;

        private final int mContainerId;

        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();

        /** 前回表示していたタブ */
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;

            private final Class<?> clss;

            private final Bundle args;

            private Fragment fragment;

            public TabInfo(String tag, Class<?> clss, Bundle args) {
                super();
                this.tag = tag;
                this.clss = clss;
                this.args = args;
            }

        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {

            // ダミーを作成し、ダミースペック（タグのつまみ）のタグとフラグメントを関連付けして格納する
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();
            TabInfo info = new TabInfo(tag, clss, args);

            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }
            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

        @Override
        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();

            if (mLastTab != newTab) {
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
            }

            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(mActivity, newTab.clss.getName(),
                            newTab.args);
                    ft.add(mContainerId, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            mActivity.getSupportFragmentManager().executePendingTransactions();

            ((ViewPagerFragment) newTab.fragment).clearFragments();
            // タイトルバーのアイコン制御
            if (tabId.equals(TAG_STORE)) {
                mActivity.findViewById(R.id.title_mybook).setVisibility(View.GONE);
                mActivity.findViewById(R.id.title_store).setVisibility(View.VISIBLE);
                ImageButton imgBtn = (ImageButton) mActivity.findViewById(R.id.btn_remove);
                String str = (String) imgBtn.getTag();
                if (str.equals("open")) {
                    imgBtn.setImageResource(R.drawable.btn_remove_close_selector);
                    imgBtn.setTag("close");
                }

                FragmentManager fm = mActivity.getSupportFragmentManager();
                ViewPagerFragment f = (ViewPagerFragment) fm.findFragmentByTag(TAG_MY_BOOK);

                if (f != null) {
                    f.clearFragments();
                }

            } else {
                mActivity.findViewById(R.id.title_mybook).setVisibility(View.VISIBLE);
                mActivity.findViewById(R.id.title_store).setVisibility(View.GONE);

                FragmentManager fm = mActivity.getSupportFragmentManager();
                ViewPagerFragment f = (ViewPagerFragment) fm.findFragmentByTag(TAG_STORE);
                if (f != null) {
                    f.clearFragments();
                }

            }
        }
    }

    /**
     * タブのつまみ箇所のカスタムビューです。
     * 
     * @author conit
     */
    private class CustomTabContent {

        private LayoutInflater mInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        private final static int STORE = 0;

        private final static int SHELF = 1;

        View generateCustomTabView(int position) {

            View view = mInflater.inflate(R.layout.custom_tab_widget, null);
            ImageView img = (ImageView) view.findViewById(R.id_costom_tab_widget.img);

            switch (position) {
                case STORE:
                    img.setBackgroundResource(R.drawable.tab_store_selector);
                    break;
                case SHELF:
                    img.setBackgroundResource(R.drawable.tab_shelf_selector);
                    break;
                default:
                    break;
            }
            return view;
        }
    }

    @Override
    public void onStoreItemSelected(Book book) {
        Intent intent = new Intent(this, BookPurchaseActivity.class);
        intent.putExtra("store_book", book);
        startActivity(intent);
    }

    @Override
    public void onLoadCallback() {
        mReloadImgBtn.setEnabled(true);
    }

    @Override
    public void onLoad() {
        mReloadImgBtn.setEnabled(false);
    }

    @Override
    public void onMypageItemSelected(Book book) {
        String generateBookFilePath = FileUtil.generateBookFilePath(mSelf, book);
        Intent intent = new Intent(mSelf, BookViewerActivity.class);
        intent.putExtra("book_path", generateBookFilePath);
        startActivity(intent);
    }

    /**
     * signatureを表示するアラートダイアログです。
     * 
     * @author conit
     */
    private static class AlertDialogFragment extends DialogFragment {

        public static AlertDialogFragment newInstance() {
            AlertDialogFragment frag = new AlertDialogFragment();
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String signature = Util.getSignature(getActivity().getApplicationContext());
            Log.d(TAG, "signature:" + signature);
            Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.signature));
            builder.setMessage(signature);
            builder.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (KeyEvent.KEYCODE_BACK == keyCode) {
                        dialog.dismiss();
                        getActivity().finish();
                        return true;
                    }
                    return false;
                }
            });
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.dialog_btn_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    });
            return builder.create();
        }
    }

}
