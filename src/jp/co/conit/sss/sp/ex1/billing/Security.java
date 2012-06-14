// Copyright 2010 Google Inc. All Rights Reserved.

package jp.co.conit.sss.sp.ex1.billing;

import java.util.List;

import jp.co.conit.sss.sp.ex1.billing.Consts.PurchaseState;
import jp.co.conit.sss.sp.ex1.entity.SPResult;
import jp.co.conit.sss.sp.ex1.entity.VerifiedProduct;
import jp.co.conit.sss.sp.ex1.util.SSSApiUtil;
import android.content.Context;

/**
 * Security-related methods. For a secure implementation, all of this code
 * should be implemented on a server that communicates with the application on
 * the device. For the sake of simplicity and clarity of this example, this code
 * is included here and is executed on the device. If you must verify the
 * purchases on the phone, you should obfuscate this code to make it harder for
 * an attacker to replace the code with stubs that treat all purchases as
 * verified.
 */
public class Security {

    /**
     * A class to hold the verified purchase information.
     */
    public static class VerifiedPurchase {

        public PurchaseState purchaseState;

        public String notificationId;

        public String productId;

        public String orderId;

        public long purchaseTime;

        public String developerPayload;

        public VerifiedPurchase(PurchaseState purchaseState, String notificationId,
                String productId, String orderId, long purchaseTime, String developerPayload) {
            this.purchaseState = purchaseState;
            this.notificationId = notificationId;
            this.productId = productId;
            this.orderId = orderId;
            this.purchaseTime = purchaseTime;
            this.developerPayload = developerPayload;
        }
    }

    /**
     * Verifies that the data was signed with the given signature, and returns
     * the list of verified purchases. The data is in JSON format and contains a
     * nonce (number used once) that we generated and that was signed (as part
     * of the whole data string) with a private key. The data also contains the
     * {@link PurchaseState} and product ID of the purchase. In the general
     * case, there can be an array of purchase transactions because there may be
     * delays in processing the purchase on the backend and then several
     * purchases can be batched together.
     * 
     * @param signedData the signed JSON string (signed, not encrypted)
     * @param signature the signature for the data, signed with the private key
     */
    public static SPResult<List<VerifiedProduct>> verifyPurchase(Context context,
            String signedData, String signature) {
        
        // SamuraiPurchaseで注文情報の検証（nonce、signature）
        return SSSApiUtil.orderVerify(context, signedData, signature);
    }
}
