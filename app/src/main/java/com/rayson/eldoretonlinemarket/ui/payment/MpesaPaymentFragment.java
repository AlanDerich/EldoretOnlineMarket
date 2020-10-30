package com.rayson.eldoretonlinemarket.ui.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rayson.eldoretonlinemarket.R;
import com.rayson.eldoretonlinemarket.models.AccessToken;
import com.rayson.eldoretonlinemarket.models.ApiClient;
import com.rayson.eldoretonlinemarket.models.STKPush;
import com.rayson.eldoretonlinemarket.ui.payment.Services.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.rayson.eldoretonlinemarket.ui.payment.Interceptor.Constants.BUSINESS_SHORT_CODE;
import static com.rayson.eldoretonlinemarket.ui.payment.Interceptor.Constants.CALLBACKURL;
import static com.rayson.eldoretonlinemarket.ui.payment.Interceptor.Constants.PARTYB;
import static com.rayson.eldoretonlinemarket.ui.payment.Interceptor.Constants.PASSKEY;
import static com.rayson.eldoretonlinemarket.ui.payment.Interceptor.Constants.TRANSACTION_TYPE;


public class MpesaPaymentFragment extends Fragment implements View.OnClickListener{
    Context mContext;
    private ApiClient mApiClient;
    private ProgressDialog mProgressDialog;
    EditText mAmount;
    EditText mPhone;
    Button mPay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mpesa_payment, container, false);
        mAmount = root.findViewById(R.id.etAmount);
        mPhone=root.findViewById(R.id.etPhone);
        mContext= getActivity();
        mProgressDialog = new ProgressDialog(mContext);
        mApiClient = new ApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.

        mPay.setOnClickListener(this);

        getAccessToken();
        return root;
    }
    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view== mPay){
            String phone_number = mPhone.getText().toString();
            String amount = mAmount.getText().toString();
            performSTKPush(phone_number,amount);
        }
    }


    public void performSTKPush(String phone_number,String amount) {
        mProgressDialog.setMessage("Processing your request");
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL,
                "MPESA Android Test", //Account reference
                "Testing"  //Transaction description
        );

        mApiClient.setGetAccessToken(false);

        //Sending the data to the Mpesa API, remember to remove the logging when in production.
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());
                    } else {
                        Timber.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Timber.e(t);
            }
        });
    }
}