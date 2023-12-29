package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;

import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_DETAILS_FIREBASE_TABLE;

public class OtpRegister extends AppCompatActivity  implements NetworkStateReceiver.NetworkStateReceiverListener
{
    EditText otpEditText;
    Button sendOtpText;
    DatabaseReference loginDetails;
    int count = 0;
    boolean check = false;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_register);

        otpEditText = findViewById(R.id.phoneNumber);
        sendOtpText = findViewById(R.id.smsVerificationButton);
        loginDetails = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DETAILS_FIREBASE_TABLE);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();




        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sendOtpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otpEditText.getText().toString().equals("")) {
                    otpEditText.setError("Required");
                    return;
                } else if (otpEditText.getText().toString().trim() != null && !TextUtils.validatePhoneNumber(otpEditText.getText().toString().trim())) {
                    otpEditText.setError("Enter Valid Phone Number");
                    return;
                } else {
                    Intent intent = new Intent(OtpRegister.this, OtpLogin.class);
                    intent.putExtra("customerPhoneNumber", otpEditText.getText().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    count = count + 1;
                    otpEditText.setText("");
                    return;
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OtpRegister.this, LoginChoiceScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        startNetworkBroadcastReceiver(this);
        unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        startNetworkBroadcastReceiver(this);
        registerNetworkBroadcastReceiver(this);
        super.onResume();
    }

    @Override
    public void networkAvailable() {
        alertDialog.dismiss();
    }

    @Override
    public void networkUnavailable() {
        if (!((Activity) OtpRegister.this).isFinishing()) {
            showCustomDialog();
        }
    }

    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }


    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkStateReceiver);
    }

    private void showCustomDialog() {

        alertDialog.setCancelable(false);
        alertDialog.show();

    }
}