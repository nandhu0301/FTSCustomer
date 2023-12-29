package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smiligenceTestenv.techUser.bean.CustomerDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.DateUtils;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_DETAILS_FIREBASE_TABLE;

public class OtpLogin extends AppCompatActivity   implements NetworkStateReceiver.NetworkStateReceiverListener
{
    EditText otpverificationEdt;
    Button proceddButton;

    TextView numberText;
    TextView resendOtpTimer;
    long sellermaxid = 0;
    String verificationId;
    FirebaseAuth mAuth;
    DatabaseReference sellerLoginDetailsRef;
    SweetAlertDialog errorDialog;
    String getPhoneNumberIntent;
    String sellerId;
    String sellerPhoneNumber;
    CustomerDetails sellerDetails;
    int count = 0;
    String userToken;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_otp_login );


        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            userToken = Objects.requireNonNull(task.getResult());
                            Log.d("tooooo", "token:" + userToken);
                            return;
                        }
                    }
                });

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        sellerLoginDetailsRef =  CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DETAILS_FIREBASE_TABLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        otpverificationEdt = findViewById ( R.id.inputCode );
        proceddButton = findViewById ( R.id.smsVerificationButton );
        numberText = findViewById ( R.id.numberText );
        resendOtpTimer = findViewById ( R.id.resend_timer );


        mAuth = FirebaseAuth.getInstance ();


        getPhoneNumberIntent = getIntent ().getStringExtra ( "customerPhoneNumber" );

           sendVerificationCode("+91" + getPhoneNumberIntent);
        //sendVerificationCode("+91" + "98431 83986");
            numberText.setText("+91 " + getPhoneNumberIntent);
            resendOtpTimer ();


        proceddButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String code = otpverificationEdt.getText ().toString ().trim ();

                if (code.isEmpty () || code.length () < 6) {
                    otpverificationEdt.setError ( "Enter valid code..." );
                    otpverificationEdt.requestFocus ();
                    return;
                }
                verifyCode ( code );
              //  verifyCode ( "123456" );
            }
        } );


        sellerLoginDetailsRef.addListenerForSingleValueEvent ( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sellermaxid = dataSnapshot.getChildrenCount ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        resendOtpTimer.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                sendVerificationCode ( "+91" + getPhoneNumberIntent );
                resendOtpTimer();
                Toast.makeText ( OtpLogin.this, "Sent Successfully", Toast.LENGTH_SHORT ).show ();
            }
        } );
    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential ( verificationId, code );
            signInWithCredential ( credential );
        } catch (Exception e) {
            Toast toast = Toast.makeText ( getApplicationContext (), "Verification Code is wrong, try again", Toast.LENGTH_SHORT );
            toast.setGravity ( Gravity.CENTER, 0, 0 );
            toast.show ();
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential ( credential )
                .addOnCompleteListener ( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ()) {

                            FirebaseUser user = task.getResult().getUser();
                            Query query = sellerLoginDetailsRef.orderByChild ( "customerPhoneNumber" ).equalTo ( getPhoneNumberIntent );
                            query.addListenerForSingleValueEvent ( new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (count == 0) {
                                        if (dataSnapshot.getChildrenCount () > 0) {

                                            for ( DataSnapshot detailsSnap : dataSnapshot.getChildren () ) {
                                                sellerDetails = detailsSnap.getValue ( CustomerDetails.class );
                                                sellerId = sellerDetails.getCustomerId ();
                                                sellerPhoneNumber = sellerDetails.getCustomerPhoneNumber ();
                                            }

                                            if (sellerDetails.isSignIn ())
                                            {

                                                SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                                                SharedPreferences.Editor editor = sharedPreferences.edit ();
                                                editor.putString ( "customerPhoneNumber", sellerPhoneNumber );
                                                editor.putString ( "customerId", sellerId );
                                                editor.commit ();

                                                sellerLoginDetailsRef.child(sellerId).child("deviceId").setValue(userToken);

                                                Intent HomeActivity = new Intent( OtpLogin.this, OnBoardScreenActivity.class );
                                                setResult ( RESULT_OK, null );
                                                HomeActivity.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                startActivity ( HomeActivity );
                                            }
                                        } else {
                                            CustomerDetails sellerLoginDetails = new CustomerDetails ();
                                            String createdDate = DateUtils.fetchCurrentDateAndTime ();
                                            sellerLoginDetails.setCustomerId ( String.valueOf ( sellermaxid + 1 ) );
                                            sellerLoginDetails.setCustomerPhoneNumber ( getPhoneNumberIntent );
                                            sellerLoginDetails.setCreationDate ( createdDate );
                                            sellerLoginDetails.setSignIn ( true );
                                            sellerLoginDetails.setDeviceId(userToken);


                                            count = count + 1;
                                            sellerLoginDetailsRef.child ( String.valueOf ( sellermaxid + 1 ) ).setValue ( sellerLoginDetails );
                                            SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                                            SharedPreferences.Editor editor = sharedPreferences.edit ();
                                            editor.putString ( "customerPhoneNumber", getPhoneNumberIntent );
                                            editor.putString ( "customerId", String.valueOf ( sellermaxid + 1 ) );
                                            editor.commit ();

                                            Intent HomeActivity = new Intent( OtpLogin.this, OnBoardScreenActivity.class );
                                            setResult ( RESULT_OK, null );
                                            HomeActivity.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                            startActivity ( HomeActivity );
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );
                        } else {
                            if (!((Activity) OtpLogin.this).isFinishing()) {
                                errorDialog = new SweetAlertDialog(OtpLogin.this, SweetAlertDialog.ERROR_TYPE);
                                errorDialog.setCancelable(false);
                                errorDialog
                                        .setContentText("Invalid OTP").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        errorDialog.dismiss();
                                    }

                                });
                                errorDialog.show();
                                Button btn = (Button) errorDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(OtpLogin.this, R.color.colorPrimary));
                            }
                        }
                    }
                } );
    }

    private void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance ().verifyPhoneNumber (
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

        Toast.makeText ( this, "OTP Sent Successfully"
                , Toast.LENGTH_SHORT ).show ();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent ( s, forceResendingToken );
            verificationId = s;
          //  mResendToken = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithCredential(phoneAuthCredential);

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText ( OtpLogin.this, e.getMessage (), Toast.LENGTH_LONG ).show ();
        }
    };


    private void resendOtpTimer() {
        resendOtpTimer.setClickable ( false );
        resendOtpTimer.setEnabled ( false );
        resendOtpTimer.setTextColor ( ContextCompat.getColor ( OtpLogin.this, R.color.cyanbase ) );
        new CountDownTimer( 60000, 1000 ) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round ( (float) ms / 1000.0f ) != secondsLeft) {
                    secondsLeft = Math.round ( (float) ms / 1000.0f );
                    resendOtpTimer.setText ( "Resend OTP ( " + secondsLeft + " )" );
                }
            }

            public void onFinish() {
                resendOtpTimer.setClickable ( true );
                resendOtpTimer.setEnabled ( true );
                resendOtpTimer.setText ( "Resend OTP" );
                resendOtpTimer.setTextColor ( ContextCompat.getColor ( OtpLogin.this, R.color.cyanbase ) );

            }
        }.start ();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OtpLogin.this, OtpRegister.class);
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
        if (!((Activity) OtpLogin.this).isFinishing()) {
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
