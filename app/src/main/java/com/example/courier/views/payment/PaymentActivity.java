package com.example.courier.views.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.courier.R;
import com.example.courier.constants.GlobalConstants;
import com.example.courier.utils.BaseActivity;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class PaymentActivity extends BaseActivity {

    private TextView mTxvProductPrice, mTxvBuy;
    String amount, currency;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_payment;

    }

    protected void initViews() {
        //  setContentView(R.layout.activity_payment);
        mTxvProductPrice = findViewById(R.id.txv_product_price);
        mTxvBuy = findViewById(R.id.txt_buy_product);
        amount = getIntent().getStringExtra("amount");
        currency = getIntent().getStringExtra("currency");
        String totalItems = getIntent().getStringExtra("totalItems");
        // Toolbar commonToolBar = findViewById(R.id.common_tool_bar);
        TextView tvTotalItems = findViewById(R.id.tvTotalItems);
        TextView tvOfferPrice = findViewById(R.id.tv_offer_price);
        TextView img_toolbar_text = findViewById(R.id.img_toolbar_text);
        ImageView toolbar = findViewById(R.id.toolbar);

        img_toolbar_text.setText("Payment Screen");
        tvOfferPrice.setText(currency + " " + amount);
        tvTotalItems.setText(totalItems);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //launchPaymentFlow(amount, currency);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        String priceNo = amount;//getString(R.string.txt_product_price);
        String price = currency + " " + priceNo/*getResources().getString(R.string.Rupees)*/;
        mTxvProductPrice.setText(price);

        mTxvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxvBuy.setEnabled(false);
                launchPaymentFlow(amount, currency);
                // launchPaymentFlow();
            }
        });
    }

    private void launchPaymentFlow(String amount, String currency) {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
        payUmoneyConfig.setPayUmoneyActivityTitle(getResources().getString(R.string.book) + " " + getResources().getString(R.string.service));
        payUmoneyConfig.setDoneButtonText("Pay " + currency + " " + amount/*getResources().getString(R.string.Rupees) + getResources().getString(R.string.txt_product_price)*/);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(String.valueOf(convertStringToDouble(amount)))
                .setTxnId(System.currentTimeMillis() + "")
                .setPhone(GlobalConstants.getMOBILE())
                .setProductName("Order Information"/*getResources().getString(R.string.nike_power_run)*/)
                .setFirstName(GlobalConstants.getFIRST_NAME())
                .setEmail(GlobalConstants.getEMAIL())
                .setsUrl(GlobalConstants.getSURL())
                .setfUrl(GlobalConstants.getFURL())
                .setUdf1("as")
                .setUdf2("sad")
                .setUdf3("ud3")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(GlobalConstants.getDEBUG())
                .setKey(GlobalConstants.getMERCHANT_KEY())
                .setMerchantId(GlobalConstants.getMERCHANT_ID());

        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, String> params = mPaymentParams.getParams();
            stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
            stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");
            //calculateHashInServer(mPaymentParams);

            stringBuilder.append(GlobalConstants.getSALT());

            String hash = hashCal(stringBuilder.toString());
            mPaymentParams.setMerchantHash(hash);
            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PaymentActivity.this, R.style.PayUMoney, true);
            // calculateHashInServer(mPaymentParams);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            mTxvBuy.setEnabled(true);
        }
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTxvBuy.setEnabled(true);
        Intent intent = new Intent();
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {

            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    // showAlert("Payment Successful");
                    String res = transactionResponse.getPayuResponse();
                    try {
                        JSONObject jsonObject = new JSONObject(res);

                        String result = jsonObject.getString("result");
                        Log.d("id------", result);
                        jsonObject = new JSONObject(result);
                        String id = jsonObject.getString("paymentId");
                        Log.d("id------", id);
                        intent.putExtra("status", "success");
                        intent.putExtra("id", id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // merchantHash = jsonObject.getString("result");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    //  showAlert("Payment Cancelled");
                    intent.putExtra("status", "Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    //showAlert("Payment Failed");
                    intent.putExtra("status", "Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
                intent.putExtra("status", "SomethingWent wrong");
            } else {
                Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
                intent.putExtra("status", "SomethingWent wrong");
            }
            setResult(RESULT_OK, intent);
            finish();
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
            //   showAlert("Payment Cancelled");
            intent.putExtra("status", "Payment Cancelled");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private Double convertStringToDouble(String str) {
        return Double.parseDouble(str);
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


}
