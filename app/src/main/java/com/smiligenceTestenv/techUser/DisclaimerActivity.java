package com.smiligenceTestenv.techUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;

import static com.smiligenceTestenv.techUser.common.Constant.ABOUT_US;
import static com.smiligenceTestenv.techUser.common.Constant.DISCLAIMER;
import static com.smiligenceTestenv.techUser.common.Constant.LEGAL_DETAILS;
import static com.smiligenceTestenv.techUser.common.Constant.PRIVACY_POLICY;
import static com.smiligenceTestenv.techUser.common.Constant.TERMS_AND_CONDITIONS;

public class DisclaimerActivity extends AppCompatActivity  implements NetworkStateReceiver.NetworkStateReceiverListener
{

    ImageView backButton;
    TextView disclaimerText,textHeader;
    DatabaseReference legalRef;
    String indicatorString;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        backButton=findViewById(R.id.backbutton);
        textHeader=findViewById(R.id.textHeader);
        disclaimerText=findViewById(R.id.disclaimerText);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        indicatorString=getIntent().getStringExtra("IndicatorVariable");
        if(indicatorString.equalsIgnoreCase(PRIVACY_POLICY)){
            textHeader.setText("Privacy Policy");
        }

        if(indicatorString.equalsIgnoreCase(TERMS_AND_CONDITIONS)){
            textHeader.setText("Terms and Conditions");
        }

        if(indicatorString.equalsIgnoreCase(DISCLAIMER)){
            textHeader.setText(DISCLAIMER);
        }
        if(indicatorString.equalsIgnoreCase(ABOUT_US)){
            textHeader.setText("About Us");
        }


        legalRef = CommonMethods.fetchFirebaseDatabaseReference(LEGAL_DETAILS);


        legalRef.child(indicatorString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String data = (String) dataSnapshot.getValue();
                   // disclaimerText.setText(data);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        disclaimerText.setText(Html.fromHtml(""+data, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        disclaimerText.setText(Html.fromHtml(""+data));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });




        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisclaimerActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DisclaimerActivity.this, HomePageActivity.class);
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
        if (!((Activity) DisclaimerActivity.this).isFinishing()) {
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