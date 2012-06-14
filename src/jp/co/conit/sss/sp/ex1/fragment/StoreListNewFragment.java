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

import java.util.List;

import jp.co.conit.sss.sp.ex1.R;
import jp.co.conit.sss.sp.ex1.entity.Book;
import jp.co.conit.sss.sp.ex1.entity.SSSProductListParam;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StoreListNewFragment extends AbstStoreListFragment {

    /**
     * Samurai Purchaseから取得したプロダクト一覧を表示するアダプターです。
     * 
     * @author
     */
    class StoreBookNewAdapter extends ArrayAdapter<Book> {

        private LayoutInflater mInflater;

        public StoreBookNewAdapter(Context context, int textViewResourceId, List<Book> objects) {
            super(context, textViewResourceId, objects);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            ViewHolder viewHolder;

            if (v == null) {
                v = mInflater.inflate(R.layout.list_item_store, parent, false);

                TextView title = (TextView) v.findViewById(R.id.text_title);
                TextView author = (TextView) v.findViewById(R.id.text_author);
                TextView price = (TextView) v.findViewById(R.id.text_price);
                TextView dateRegistered = (TextView) v.findViewById(R.id.text_date_registered);
                viewHolder = new ViewHolder();
                viewHolder.titleText = title;
                viewHolder.authorText = author;
                viewHolder.priceText = price;
                viewHolder.dateRegisteredText = dateRegistered;
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            Book item = getItem(position);
            viewHolder.titleText.setText(item.getTitle());
            viewHolder.authorText.setText(item.getOutline());
            if (item.isFree()) {
                viewHolder.priceText.setText(getString(R.string.free));
            } else {
                viewHolder.priceText.setText(getString(R.string.pay));

            }
            viewHolder.dateRegisteredText.setText(item.getPublishDateStr());

            return v;
        }

    }

    private static class ViewHolder {
        TextView titleText;

        TextView authorText;

        TextView priceText;

        TextView dateRegisteredText;

    }

    @Override
    ArrayAdapter<Book> generateAdapter() {
        return new StoreBookNewAdapter(getActivity(), R.layout.list_item_store, mStoreBookList);
    }

    @Override
    SSSProductListParam generateSSSParam() {
        SSSProductListParam param = new SSSProductListParam();
        param.setFields("title,outline,publish_date");
        param.setSorttype("DESC");
        param.setSortfield("publish_date");
        param.setOffset(mOffset4BookList);
        param.setLimit(DATA_UNIT);
        return param;
    }

}
