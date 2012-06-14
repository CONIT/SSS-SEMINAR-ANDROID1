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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

/**
 * SamuraiPurchaseのアクセストークンを取得するユーティリティクラスです。
 * 
 * @author conit
 */
public final class Util {

    private Util() {
    }

    static {
        System.loadLibrary("token");
    }

    public static native String getToken(String key);

    public static String getSignature(Context context) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }

        PackageManager pm = context.getPackageManager();
        try {
            Signature[] signatures = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES).signatures;
            messageDigest.update(signatures[0].toByteArray());
            return digestToString(messageDigest.digest());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String digestToString(byte[] data) {
        String result = "";
        for (int i = 0; i < data.length; i++) {
            int d = data[i];
            if (d < 0) {
                d += 256;
            }
            if (d < 16) {
                result += "0";
            }
            result += Integer.toString(d, 16);
        }
        return result;
    }
}
