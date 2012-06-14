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
import java.util.Arrays;
import java.util.List;

import jp.co.conit.sss.sp.ex1.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * ストアタブ、マイブックスタブ内で使用するFragmentです。<br>
 * ViewPagerを保持し、表示するタブの内容でViewPagerの表示内容を決定、生成します。
 * 
 * @author conit
 */
public class ViewPagerFragment extends Fragment {

    private static final int STORE = 1;

    private static final int MY_PAGE = 2;

    private List<TextView> mIndicatorTextViewList;

    private List<String> mIndicatorStrList;

    private ViewPager mViewPager;

    private SSSFragmentPagerAdapter mAdapter;

    private int mTabType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pager_main, null, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);

        mIndicatorTextViewList = new ArrayList<TextView>();
        mIndicatorTextViewList.add((TextView) view.findViewById(R.id.indicator_left));
        mIndicatorTextViewList.add((TextView) view.findViewById(R.id.indicator_center));
        mIndicatorTextViewList.add((TextView) view.findViewById(R.id.indicator_right));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] stringArray;
        Bundle arg = getArguments();

        // 表示するタブの内容を取得する
        mTabType = arg.getInt("tab_type");

        if (mTabType == STORE) {
            stringArray = getActivity().getResources()
                    .getStringArray(R.array.indicator_array_store);
        } else {
            stringArray = getActivity().getResources().getStringArray(
                    R.array.indicator_array_mypage);
        }

        mIndicatorStrList = Arrays.asList(stringArray);
        createPagerAdapter();
        new setAdapterTask().execute();
    }

    /**
     * ViewPagerに対して書籍一覧を提供するアダプタです。<br>
     * <br>
     * タブのタイプでストアの書籍一覧のViewPagerかダウンロード済みの書籍一覧のViewPagerのどちらを表示するかを決めます。<br>
     * 
     * @author conit
     */
    private class SSSFragmentPagerAdapter extends FragmentPagerAdapter implements
            OnPageChangeListener {

        private int mNumViews = 0;

        private final IndicatorManager mIndicatorManager;

        private List<Fragment> mFragments;

        private int mLastPosition;

        public SSSFragmentPagerAdapter(FragmentManager fm, List<TextView> indicatorTextList,
                List<String> indicatorPageTitleList, int lastPosition) {
            super(fm);
            mIndicatorManager = new IndicatorManager(indicatorTextList, indicatorPageTitleList,
                    lastPosition);
            mFragments = new ArrayList<Fragment>();
            if (mTabType == STORE) {
                mNumViews = 3;
                mFragments.add(new StoreListDLFragment());
                mFragments.add(new StoreListNewFragment());
                mFragments.add(new StoreListTitleFragment());
            } else {
                mNumViews = 1;
                mFragments.add(new MybooksListFragment());
            }
        }

        @Override
        public Fragment getItem(int position) {

            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mNumViews;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            mIndicatorManager.update(position);
            mLastPosition = position;
        }

    }

    private class setAdapterTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            initViewPager();
        }
    }

    private void createPagerAdapter() {

        int lastPosition = 0;
        if (mAdapter != null) {
            lastPosition = mAdapter.mLastPosition;
        }
        mAdapter = new SSSFragmentPagerAdapter(getFragmentManager(), mIndicatorTextViewList,
                mIndicatorStrList, lastPosition);
    }

    private void initViewPager() {
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mAdapter);
    }

    /**
     * ViewPager内の子Fragmentを全てremoveします。
     */
    public void clearFragments() {

        if (mAdapter == null) {
            return;
        }

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Fragment fragment = mAdapter.getItem(i);
            if (fragment instanceof AbstStoreListFragment) {
                ((AbstStoreListFragment) fragment).cancelProductListGetTask();
            } else if (fragment instanceof MybooksListFragment) {
                ((MybooksListFragment) fragment).closeDb();
                ((MybooksListFragment) fragment).cancelLoader();

            }
            if (fragment != null) {
                ft.remove(fragment);
            }
        }
        ft.commit();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 表示しているリストのデータを再読み込みします。
     */
    public void reload() {
        AbstStoreListFragment f = (AbstStoreListFragment) mAdapter.getItem(mAdapter.mLastPosition);
        f.reload();
    }

    /**
     * マイブックスの表示を削除モードに変更します。
     */
    public void setRemoveMode() {
        MybooksListFragment f = (MybooksListFragment) mAdapter.getItem(mAdapter.mLastPosition);
        f.setRemoveMode();
    }
}
