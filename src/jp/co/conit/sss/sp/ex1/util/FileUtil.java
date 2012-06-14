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

package jp.co.conit.sss.sp.ex1.util;

import java.io.File;

import jp.co.conit.sss.sp.ex1.entity.Book;
import android.content.Context;

/**
 * ファイル関連のユーティリティクラスです。
 * 
 * @author conit
 */
public final class FileUtil {

    private FileUtil() {
    };

    /**
     * データ保存先PATH（SDカードのPATH＋アプリのパッケージ名）を返却します。
     * 
     * @param context
     * @return
     */
    private static String getExternalPackagePath(Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        return externalFilesDir.getPath();

    }

    /**
     * 指定したファイルorディレクトリを削除します。<br>
     * ディレクトリの場合、子要素もすべて削除します。
     * 
     * @param file 指定ファイル
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isFile()) {
            file.delete();
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
            file.delete();
        }
    }

    /**
     * 書籍データのディレクトリパスを生成します。<br>
     * 書籍データはSDカード内に保存するものとします。
     */
    public static String generateBookDirPath(Context context) {
        return FileUtil.getExternalPackagePath(context) + "/bookData/";
    }

    /**
     * 書籍データのファイルパスを生成します。<br>
     * 書籍データはSDカード内に保存するものとします。
     */
    public static String generateBookFilePath(Context context, Book book) {
        return new File(generateBookDirPath(context), book.getProductId() + ".html").getPath();
    }

}
