package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by Ghanshyam patidar on 23/9/15.
 */
public class PaypalTestActivity extends Activity {

    // set to PaymentActivity.ENVIRONMENT_PRODUCTION to move real money.
    // set to PaymentActivity.ENVIRONMENT_SANDBOX to use your test credentials from https://developer.paypal.com
    // set to PaymentActivity.ENVIRONMENT_NO_NETWORK to kick the tires without communicating to PayPal's servers.
    private static final String CONFIG_ENVIRONMENT = PaymentActivity.ENVIRONMENT_SANDBOX;

//    // note that these credentials will differ between live & sandbox environments.
//    private static final String CONFIG_CLIENT_ID = "AXJVRhC7DvWS3Br2NNUbF7twDnB-20ggOOx2huKOb2FwH9IG3oiFqtoafze4";
    // when testing in sandbox, this is likely the -facilitator email address.
//    private static final String CONFIG_RECEIVER_EMAIL = "priyankakaushal88-facilitator@gmail.com";


//    private static final String CONFIG_CLIENT_ID = "AdyrQuE3tNCcArZpVnTQjvPUt5SwufW0CZZ3Hh-MdnS221h29MmxItkBk_my6nimQHmGxggqgCUPnG4z";
//    private static final String CONFIG_RECEIVER_EMAIL = "ghanshyam.ypsilon-facilitator@gmail.com";

    private static final String CONFIG_CLIENT_ID = "AcoiH9I9TL8dLQC6YJePmu5rWE2h0dLYeYuaRoAtvHdd1pH4XUHbT-GZRg4FIBy9IkjMGCGbPUxBLBZo";
    private static final String CONFIG_RECEIVER_EMAIL = "ghanshyam.patidar34mca-facilitator@gmail.com";

    Context mContext;

    paymentDetail paydetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mContext = this;


        Intent intent = new Intent(PaypalTestActivity.this,
                PayPalService.class);


        intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT,
                CONFIG_ENVIRONMENT);
        intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
        intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL,
                CONFIG_RECEIVER_EMAIL);

        startService(intent);


        Intent payIntent = getIntent();

        try{

//            if(payIntent != null){
//
//                paydetail = new paymentDetail();
//                paydetail.student_id = payIntent.getExtras().getString("student_id");
//                paydetail.amount = payIntent.getExtras().getString("amount");
//                paydetail.course_id = payIntent.getExtras().getString("course_id");
//                paydetail.admission_id = payIntent.getExtras().getString("admission_id");
//
//                onBuyPressed(paydetail.amount, "Exam Fees", "USD");
//
//            }

            onBuyPressed("1.75", "Exam Fees", "USD");

        }catch(Exception e){

            e.printStackTrace();

            finish();
//            TextMessagePopup.showTextMessage(mContext,"Failed to open Paypal page");
            Toast.makeText(mContext,"Failed to open Paypal page",Toast.LENGTH_LONG).show();

        }

    }


    public void onBuyPressed(String amount,String nameOfPayment,String currencycode) {

//        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal("1.75"), "USD", "Admission fees");

        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(amount), currencycode, nameOfPayment);

        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, CONFIG_ENVIRONMENT);
        intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
        intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL, CONFIG_RECEIVER_EMAIL);

        // It's important to repeat the clientId here so that the SDK has it if Android restarts your
        // app midway through the payment UI flow.
//        intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, "AXJVRhC7DvWS3Br2NNUbF7twDnB-20ggOOx2huKOb2FwH9IG3oiFqtoafze4");

        intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
        intent.putExtra(PaymentActivity.EXTRA_PAYER_ID, "your-customer-id-in-your-system");
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, 0);

    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {

                try {

                    Log.i("paymentExample", confirm.toJSONObject().toString());

                    JSONObject jsonObj = new JSONObject(confirm.toJSONObject().toString());
//                    String paymentId=jsonObj.getJSONObject("response").getString("id");
//                    System.out.println("payment id:-=="+paymentId);
//                    Toast.makeText(getApplicationContext(), paymentId, Toast.LENGTH_LONG).show();

                    JSONObject clientJsonObj =  jsonObj.getJSONObject("client");
                    JSONObject paymentJsonObj =  jsonObj.getJSONObject("payment");
                    JSONObject proofofpaymentJsonObj =  jsonObj.getJSONObject("proof_of_payment");
                    JSONObject adaptive_payment = proofofpaymentJsonObj.getJSONObject("adaptive_payment");


//                    MultipartEntity multipart = new MultipartEntity();

//
//                    multipart.addPart("user_id", new StringBody(paydetail.student_id));
//                    multipart.addPart("admission_id", new StringBody(paydetail.admission_id));
//                    multipart.addPart("course_id", new StringBody(paydetail.course_id));
//
//                    multipart.addPart("transaction_id",
//                            new StringBody(adaptive_payment.getString("pay_key")));
//
//                    multipart.addPart("amount", new StringBody(paymentJsonObj.getString("amount")));
//
//                    multipart.addPart("payment_status", new StringBody("1"));
//
//
//                    //pass multipart to send payment info on server by webservice
////                    sendAdmissionPaymentInfoToServer(multipart);
//
//
                } catch (JSONException e) {

                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);

//                    TextMessagePopup.showTextMessage(mContext,"an extremely unlikely failure occurred");
                    finish();

                } catch (Exception e) {

                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
//                    TextMessagePopup.showTextMessage(mContext,"an extremely unlikely failure occurred");
                    finish();
//
                }
            }

        }
        else if (resultCode == Activity.RESULT_CANCELED) {

            Log.i("paymentExample", "The user canceled.");
//            TextMessagePopup.showTextMessage(mContext,"The user canceled.");
            finish();

        }
        else if (resultCode == PaymentActivity.RESULT_PAYMENT_INVALID) {

            Log.i("paymentExample", "An invalid payment was submitted. Please see the docs.");
//            TextMessagePopup.showTextMessage(mContext,"An invalid payment was submitted");
            finish();

        }

    }



	/*
	 * this method called when payment is submitted through paypal successfully
	 * for store payment information on server for specific course for specific
	 * student
	 */
//
//    public void sendAdmissionPaymentInfoToServer(MultipartEntity multipart){
//
//
//        if (ValidationConstant.isNetworkAvailable(mContext)) {
//
//            try {
//
//                new AdmissionPaymentAsyTask(mContext,new AdmissionPaymentListener(),multipart).execute();
//
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//
//            }
//
//        } else {
//
//            ValidationConstant.showNetworkError(mContext);
//
//        }
//
//    }


//    public class AdmissionPaymentListener {
//
//        public void onSuccess() {
//
//
//            TextMessagePopup.showTextMessage(mContext,"Transaction Success");
//            Intent intent=new Intent();
//            setResult(Activity.RESULT_OK,intent);
//            finish();
//
//        }
//
//        public void onError(String error) {
//
//            finish();
//            TextMessagePopup.showTextMessage(mContext,"Transaction Failure");
////			RefundTransaction
////			MassPay
//
//        }
//    }


    ProgressDialog progress;
    public void showProgress() {
        try {
            if (progress == null)
                progress = new ProgressDialog(this);
            progress.setMessage("Please Wait..");
            progress.setCancelable(false);
            progress.show();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                progress = new ProgressDialog(this);
                progress.setMessage("Please Wait..");
                progress.setCancelable(false);
                progress.show();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }
    public void hideProgress() {
        if (progress != null) {
            progress.dismiss();
        }
    }


    public class paymentDetail{

        public String student_id;
        public String amount;
        public String admission_id;
        public String course_id;

    }

    @Override
    public void onDestroy() {

        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();

    }
}