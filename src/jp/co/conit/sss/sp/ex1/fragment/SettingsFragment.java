/* * Copyright (C) 2012 CONIT Co., Ltd. * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *      http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package jp.co.conit.sss.sp.ex1.fragment;import jp.co.conit.sss.sp.ex1.R;import android.app.Activity;import android.os.Bundle;import android.support.v4.app.Fragment;import android.view.LayoutInflater;import android.view.View;import android.view.View.OnClickListener;import android.view.ViewGroup;/** * 設定機能（リストア）を提供するFragmentです。 *  * @author conit */public class SettingsFragment extends Fragment {    private OnRestoreLisntener mOnRestoreLisntener;    /**     * リストア処理のリスナーです。     *      * @author conit     */    public interface OnRestoreLisntener {        void onRestore();    }    @Override    public void onAttach(Activity activity) {        super.onAttach(activity);        try {            mOnRestoreLisntener = (OnRestoreLisntener) activity;        } catch (ClassCastException e) {            throw new ClassCastException(activity.toString() + " must implement OnRestoreLisntener");        }    };    public static SettingsFragment newInstance() {        SettingsFragment fragment = new SettingsFragment();        return fragment;    }    @Override    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {        View view = inflater.inflate(R.layout.fragment_settings, null);        view.findViewById(R.id.btn_restore).setOnClickListener(new OnClickListener() {            @Override            public void onClick(View v) {                mOnRestoreLisntener.onRestore();            }        });        return view;    }}