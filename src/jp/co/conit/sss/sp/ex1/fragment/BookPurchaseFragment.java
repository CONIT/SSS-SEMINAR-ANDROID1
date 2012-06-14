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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.co.conit.sss.sp.ex1.R;
import jp.co.conit.sss.sp.ex1.activity.BookViewerActivity;
import jp.co.conit.sss.sp.ex1.billing.PurchaseDatabase;
import jp.co.conit.sss.sp.ex1.db.PurchasedBookDao;
import jp.co.conit.sss.sp.ex1.entity.Book;
import jp.co.conit.sss.sp.ex1.entity.SPResult;
import jp.co.conit.sss.sp.ex1.entity.DownloadFile;
import jp.co.conit.sss.sp.ex1.util.FileUtil;
import jp.co.conit.sss.sp.ex1.util.HttpUtil;
import jp.co.conit.sss.sp.ex1.util.SSSApiUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 書籍の購入、ダウンロード機能を提供するFragmentです。
 * 
 * @author conit
 */
public class BookPurchaseFragment extends Fragment {

    private static final int BUFFER_SIZE = 1024;

    private Book mBook;

    private FileInfoTask mFileInfoTask;

    private FileDownloadTask mFileDownloadTask;

    private BillingListener mBillingListener;

    private Button mPurchaceBtn;

    private String mBookPath;

    private static BookPurchaseFragment mSelf;

    /**
     * プロダクト購入処理を行う際に呼び出されるリスナーです。
     * 
     * @author conit
     */
    public interface BillingListener {
        /**
         * 指定したプロダクトIDの商品を購入します。
         * 
         * @param productId プロダクトID
         */
        void onBuyProduct(String productId);
    }

    public static BookPurchaseFragment newInstance(Book book) {

        Bundle args = new Bundle();
        args.putSerializable("book", book);
        BookPurchaseFragment fragment = new BookPurchaseFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mBillingListener = (BillingListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement BillingListener");
        }
        mSelf = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_purchase, container, false);

        Bundle arguments = getArguments();
        mBook = (Book) arguments.get("book");
        String title = mBook.getTitle();
        String outline = mBook.getOutline();

        TextView detailTitle = (TextView) v.findViewById(R.id.detail_title);
        detailTitle.setText(title);
        TextView detailAuthor = (TextView) v.findViewById(R.id.detail_author);
        detailAuthor.setText(outline);

        mPurchaceBtn = (Button) v.findViewById(R.id.btn_purchace);
        mPurchaceBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (hasBookFile()) {
                    mBookPath = FileUtil.generateBookFilePath(getActivity(), mBook);
                    intentToBookViewer();
                } else {
                    if (isDownloadable()) {
                        downloadBookDataAsync();
                    } else {
                        purchaseBook();
                    }
                }
            }
        });

        if (hasBookFile()) {
            mPurchaceBtn.setText(getString(R.string.read));
        } else {
            if (isDownloadable()) {
                mPurchaceBtn.setText(getString(R.string.download));
            } else {
                if (mBook.isFree()) {
                    mPurchaceBtn.setText(getString(R.string.free));
                } else {
                    mPurchaceBtn.setText(getString(R.string.pay));
                }
            }
        }

        return v;
    }

    /**
     * 書籍ファイルのダウンロードが可能かどうかを判定します。<br>
     * 無料の場合は可、有料の場合はレシート情報が存在する場合が可となります。<br>
     * 
     * @return {@code true}ダウンロード可{@code false}ダウンロード不可
     */
    private boolean isDownloadable() {
        boolean downloadable = false;
        PurchaseDatabase purchaseDatabase = new PurchaseDatabase(getActivity());
        String receipt = purchaseDatabase.readReceipt(mBook.getProductId());
        if (!mBook.isFree() && (receipt == null || receipt.equals(""))) {
            downloadable = false;
        } else {
            downloadable = true;
        }
        return downloadable;
    }

    /**
     * 書籍の購入します。
     */
    private void purchaseBook() {
        mBillingListener.onBuyProduct(mBook.getProductId());
    }

    /**
     * 書籍ファイルがSDカード内に存在するか調べます。
     * 
     * @return {@code true}存在する。{@code false}存在しない。
     */
    private boolean hasBookFile() {
        File bookDataFile = new File(FileUtil.generateBookFilePath(getActivity(), mBook));
        return bookDataFile.exists() ? true : false;
    }

    /**
     * 非同期で書籍ファイルをダウンロードします。
     */
    private void downloadBookDataAsync() {

        mFileInfoTask = new FileInfoTask(getActivity());
        mFileInfoTask.execute();

    }

    /**
     * ダウンロードファイル情報を取得するタスクです。<br>
     * 取得に成功した場合は継続してファイルのダウンロード処理を実行します。
     * 
     * @author conit
     */
    private class FileInfoTask extends AsyncTask<Void, Void, SPResult<List<DownloadFile>>> {

        private Context mContext;

        private FileInfoTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialogFragment(ProgresssDialogFragment.STYLE_SPINNER);
        }

        @Override
        protected SPResult<List<DownloadFile>> doInBackground(Void... params) {
            if (!HttpUtil.isConnected(mContext)) {
                return SPResult.getDisconectErrInstance(mContext);
            }
            String receipt = null;

            // 有料コンテンツの場合はレシート情報が必須
            if (!mBook.isFree()) {
                PurchaseDatabase pd = new PurchaseDatabase(getActivity());
                receipt = pd.readReceipt(mBook.getProductId());
            }

            // ダウンロードファイルリスト取得
            return SSSApiUtil.getFileList(getActivity().getApplicationContext(),
                    mBook.getProductId(), receipt);
        }

        @Override
        protected void onPostExecute(SPResult<List<DownloadFile>> result) {
            removeDialogFragment(ProgresssDialogFragment.STYLE_SPINNER);
            if (isCancelled()) {
                return;
            }
            if (!result.isError()) {
                List<DownloadFile> downLoadFileList = result.getContent();
                // 当アプリではファイルは１つ固定として実装
                DownloadFile downLoadFile = downLoadFileList.get(0);
                // ファイルダウンロード処理を実行
                mFileDownloadTask = new FileDownloadTask(downLoadFile);
                mFileDownloadTask.execute();
            } else {
                showAlertDialogFragment(AlertDialogFragment.DOWNLOAD_FAIL);
            }
        }
    }

    /**
     * 書籍ファイルをダウンロードするタスクです。
     */
    private class FileDownloadTask extends AsyncTask<Void, Integer, Integer> {

        private DownloadFile mDownLoadFile;

        public FileDownloadTask(DownloadFile downLoadFile) {
            mDownLoadFile = downLoadFile;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialogFragment(ProgresssDialogFragment.STYLE_HORIZONTAL);
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {

                int fileSize = 0;
                URL url = new URL(mDownLoadFile.getDowmloadUrl());
                URLConnection conn = url.openConnection();
                fileSize = conn.getContentLength();

                DialogFragment df = (DialogFragment) getFragmentManager().findFragmentByTag(
                        Integer.toString(ProgresssDialogFragment.STYLE_HORIZONTAL));
                ProgressDialog pd = (ProgressDialog) df.getDialog();
                pd.setMax(fileSize);

                File downloadDir = new File(FileUtil.generateBookDirPath(getActivity()));
                if (!downloadDir.exists()) {
                    downloadDir.mkdirs();
                }
                File downloadFile = new File(downloadDir.getPath(), mDownLoadFile.getName());
                mBookPath = downloadFile.getPath();

                String hash = null;
                // 有料書籍ならばハッシュを取得
                if (!mBook.isFree()) {
                    hash = mDownLoadFile.getHash();
                }

                BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
                        downloadFile.getPath(), false));

                byte[] buf = new byte[BUFFER_SIZE];
                int len;
                int downloadSize = 0;
                MessageDigest digest = MessageDigest.getInstance("SHA-1");

                while ((len = in.read(buf)) != -1 && !isCancelled()) {
                    digest.update(buf, 0, len);
                    out.write(buf, 0, len);
                    downloadSize += buf.length;
                    publishProgress(downloadSize);
                }
                if (hash != null) {
                    String downloadHash = hashByte2MD5(digest.digest());
                    if (!downloadHash.equals(hash)) {
                        throw new HashInvalidException("not correct hash");
                    }
                }
                out.close();
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return 0;
            } catch (HashInvalidException e) {
                e.printStackTrace();
                return 0;
            }
            return 1;
        }

        private String hashByte2MD5(byte[] hash) {
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }

            return hexString.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            DialogFragment df = (DialogFragment) getFragmentManager().findFragmentByTag(
                    Integer.toString(ProgresssDialogFragment.STYLE_HORIZONTAL));
            ProgressDialog pd = (ProgressDialog) df.getDialog();
            pd.incrementProgressBy(values[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            removeDialogFragment(ProgresssDialogFragment.STYLE_HORIZONTAL);

            if (!isCancelled()) {
                if (result == 0) { // 失敗
                    deleteBookFile();
                    showAlertDialogFragment(AlertDialogFragment.DOWNLOAD_FAIL);
                } else if (result == 1) { // 成功
                    // 購入書籍情報（無料分も）情報をDBへinsert
                    Date time = Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    mBook.setPurchasedDateStr(simpleDateFormat.format(time));
                    PurchasedBookDao dbAccessor = new PurchasedBookDao(getActivity());
                    // DB未登録ならば登録
                    if (!dbAccessor.isExistBook(mBook)) {
                        dbAccessor.addData(mBook);
                    }
                    mPurchaceBtn.setText(getString(R.string.read));
                    showAlertDialogFragment(AlertDialogFragment.INTENT_VIEWER);
                }
            } else {
                // キャンセルならディレクトリ削除
                deleteBookFile();
                showAlertDialogFragment(AlertDialogFragment.DOWNLOAD_CANCEL);
            }

        }
    }

    /**
     * アラートダイアログフラグメントを表示します。
     * 
     * @param type
     */
    private void showAlertDialogFragment(int type) {
        FragmentManager fm = getFragmentManager();
        AlertDialogFragment adf = AlertDialogFragment.newInstance(type);
        adf.show(fm, Integer.toString(type));
    }

    /**
     * プログレスダイアログフラグメントを表示します。
     * 
     * @param type
     */
    private void showProgressDialogFragment(int type) {
        FragmentManager fm = getFragmentManager();
        ProgresssDialogFragment pdf = ProgresssDialogFragment.newInstance(type);
        pdf.show(fm, Integer.toString(type));
    }

    /**
     * 書籍ビューワー画面に遷移します。
     */
    private void intentToBookViewer() {
        Intent intent = new Intent(getActivity(), BookViewerActivity.class);
        intent.putExtra("book_path", mBookPath);
        startActivity(intent);
    }

    /**
     * 書籍ファイルを削除します。<br>
     * ファイルが存在しない場合は何もしません。
     */
    private void deleteBookFile() {
        File file = new File(mBookPath);
        FileUtil.deleteFile(file);
    }

    /**
     * hash不一致例外です。
     */
    private class HashInvalidException extends Exception {
        private static final long serialVersionUID = 3957048623463577915L;

        public HashInvalidException(String msg) {
            super(msg);
        }
    }

    /**
     * 書籍情報取得の失敗、キャンセル時、ファイルダウンロード完了時、失敗時、キャンセル時に表示するアラートダイアログです。
     * 
     * @author conit
     */
    private static class AlertDialogFragment extends DialogFragment {

        public static final int INTENT_VIEWER = 1;

        public static final int DOWNLOAD_FAIL = 2;

        public static final int DOWNLOAD_CANCEL = 3;

        public static AlertDialogFragment newInstance(int type) {
            AlertDialogFragment frag = new AlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("dialog_type", type);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Bundle arg = getArguments();
            int type = arg.getInt("dialog_type");
            AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
            switch (type) {
                case INTENT_VIEWER:
                    dlg.setTitle(getActivity().getString(R.string.dialog_title_download_complete));
                    dlg.setMessage(getActivity().getString(R.string.dialog_download_complete));
                    dlg.setCancelable(false);
                    dlg.setPositiveButton(getString(R.string.dialog_btn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mSelf.intentToBookViewer();
                                }
                            });
                    break;
                case DOWNLOAD_FAIL:
                    dlg.setMessage(getActivity().getString(R.string.dialog_error_download));
                    dlg.setCancelable(false);
                    dlg.setPositiveButton(getString(R.string.dialog_btn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mSelf.removeDialogFragment(DOWNLOAD_FAIL);
                                }
                            });
                    dlg.show();
                    break;
                case DOWNLOAD_CANCEL:
                    dlg.setMessage(getActivity().getString(R.string.dialog_cancel_download));
                    dlg.setCancelable(false);
                    dlg.setPositiveButton(getString(R.string.dialog_btn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mSelf.removeDialogFragment(DOWNLOAD_CANCEL);
                                }
                            });
                    break;
                default:
                    break;
            }
            return dlg.create();
        }
    }

    /**
     * 書籍ファイル情報取得中、書籍ファイルダウンロード中に表示するプログレスフラグメントダイアログです。
     * 
     * @author conit
     */
    private static class ProgresssDialogFragment extends DialogFragment {

        public static final int STYLE_SPINNER = 10;

        public static final int STYLE_HORIZONTAL = 20;

        public static ProgresssDialogFragment newInstance(int type) {
            ProgresssDialogFragment frag = new ProgresssDialogFragment();
            Bundle args = new Bundle();
            args.putInt("dialog_type", type);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            Bundle arg = getArguments();
            int type = arg.getInt("dialog_type");

            switch (type) {
                case STYLE_SPINNER:
                    progressDialog.setMessage(getActivity().getString(
                            R.string.dialog_getting_book_info));
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (KeyEvent.KEYCODE_BACK == keyCode) {
                                mSelf.cancelDownloadFileInfoTask();
                                mSelf.removeDialogFragment(STYLE_SPINNER);
                                return true;
                            }
                            return false;
                        }
                    });
                    break;
                case STYLE_HORIZONTAL:
                    progressDialog
                            .setMessage(getActivity().getString(R.string.dialog_getting_book));
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setCancelable(false);
                    progressDialog.setMax(1);
                    progressDialog.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (KeyEvent.KEYCODE_BACK == keyCode) {
                                return true;
                            }
                            return false;
                        }
                    });
                    break;
                default:
                    break;
            }
            return progressDialog;
        }

    }

    /**
     * 書籍ファイル情報の取得処理が実行中の場合、キャンセルします。
     */
    private void cancelDownloadFileInfoTask() {
        if (mFileInfoTask != null && !mFileInfoTask.isCancelled()) {
            mFileInfoTask.cancel(true);
        }
    }

    /**
     * 書籍ファイルのダウンロード処理が実行中の場合、キャンセルします。
     */
    private void cancelDownloadFileTask() {
        if (mFileDownloadTask != null && !mFileDownloadTask.isCancelled()) {
            mFileDownloadTask.cancel(true);
        }
    }

    /**
     * ダイアログフラグメントが表示されている場合、消します。
     * 
     * @param type
     */
    private void removeDialogFragment(int type) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment prev = (DialogFragment) getFragmentManager().findFragmentByTag(
                Integer.toString(type));
        if (prev != null) {
            prev.dismiss();
            ft.remove(prev);
            ft.commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelDownloadFileInfoTask();
        cancelDownloadFileTask();
    }

    /**
     * 購入ボタンをロックまたはロックの解除を行います。
     * 
     * @param enable
     */
    public void setPurchaceBtnEnable(boolean enable) {
        mPurchaceBtn.setEnabled(enable);
    }

    /**
     * 購入ボタンの表示名を変更します。
     * 
     * @param title 表示する文字列
     */
    public void setPurchaceBtnTitle(String title) {
        mPurchaceBtn.setText(title);
    }

}
