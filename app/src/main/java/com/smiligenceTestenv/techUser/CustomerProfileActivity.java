package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.bean.ContactDetails;
import com.smiligenceTestenv.techUser.bean.CustomerDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceTestenv.techUser.common.Constant.CONTACT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.INVALID_EMAIL;
import static com.smiligenceTestenv.techUser.common.Constant.INVALID_USERNAME;

public class CustomerProfileActivity extends AppCompatActivity  implements NetworkStateReceiver.NetworkStateReceiverListener
{
    ImageView backToHome;
    DatabaseReference databaseReference;
    TextView customerName, customerPhoneNumber, customerEmail;
    CustomerDetails customerDetails;
    String customerAddressIntent;
    ImageView logout;
    TextView whatsapptext;
    Button updateProfileButton;
    String saved_id;
    TextView login;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        whatsapptext = findViewById(R.id.whatsapptext);
        updateProfileButton = findViewById(R.id.updateProfile);
        login = findViewById(R.id.login);
        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        checkGPSConnection(getApplicationContext());

        backToHome = findViewById(R.id.backto_home);
        customerName = findViewById(R.id.customername);
        customerPhoneNumber = findViewById(R.id.customernumber);
        customerPhoneNumber.setEnabled(false);
        customerPhoneNumber.setFocusable(false);
        customerEmail = findViewById(R.id.customeremail);
        logout = findViewById(R.id.logout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        databaseReference = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DETAILS_FIREBASE_TABLE);

        customerAddressIntent = getIntent().getStringExtra("userAddress");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                if (android_id.equals(saved_id)) {
                    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(CustomerProfileActivity.this, OtpRegister.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    new SweetAlertDialog(CustomerProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("You want to logout from current login and continue new login!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.commit();
                                    Intent intent = new Intent(CustomerProfileActivity.this, OtpRegister.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                if (android_id.equals(saved_id)) {
                    new SweetAlertDialog(CustomerProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Please login and then update profile")
                            .show();
                } else {
                    String customerNameString = customerName.getText().toString().trim();
                    String customerMobileNumber = customerPhoneNumber.getText().toString().trim();
                    String customerEmailString = customerEmail.getText().toString().trim();
                    if (customerNameString != null && (!TextUtils.isValidUserName(customerNameString))) {
                        customerName.setError(INVALID_USERNAME);
                        return;
                    }
                    if (customerEmailString != null && (!TextUtils.isValidEmail(customerEmailString))) {
                        customerEmail.setError(INVALID_EMAIL);
                        return;
                    } else {
                        CustomerDetails customerDetails = new CustomerDetails();
                        customerDetails.setCustomerPhoneNumber(customerMobileNumber);
                        customerDetails.setFullName(customerNameString);
                        customerDetails.setEmailId(customerEmailString);
                        customerDetails.setCustomerId(saved_id);
                        customerDetails.setCreationDate(customerDetails.getCreationDate());
                        databaseReference.child(saved_id).setValue(customerDetails);

                        Toast.makeText(CustomerProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        databaseReference.orderByChild("customerId").equalTo(saved_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.exists()) {


                        for (DataSnapshot customerSnap : dataSnapshot.getChildren()) {
                            customerDetails = customerSnap.getValue(CustomerDetails.class);
                            customerPhoneNumber.setText("" + customerDetails.getCustomerPhoneNumber());

                            if (customerDetails.getCustomerPhoneNumber() != null) {
                                login.setVisibility(View.INVISIBLE);
                                logout.setVisibility(View.VISIBLE);
                            }

                            if (customerDetails.getFullName() == null) {
                                customerName.setText("");
                            } else {
                                customerName.setText("" + customerDetails.getFullName());
                            }
                            if (customerDetails.getEmailId() == null) {
                                customerEmail.setText("");
                            } else {
                                customerEmail.setText("" + customerDetails.getEmailId());
                            }
                        }


                        customerName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cus, 0, 0, 0);
                        customerEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mailicon_01, 0, 0, 0);
                        customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phonenumicon_01, 0, 0, 0);

                    } else {


                        login.setVisibility(View.VISIBLE);
                        logout.setVisibility(View.INVISIBLE);

                    }
                } else {


                    login.setVisibility(View.VISIBLE);
                    logout.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference contactRef = CommonMethods.fetchFirebaseDatabaseReference(CONTACT_DETAILS_FIREBASE_TABLE);


        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    ContactDetails contactDetails = dataSnapshot.getValue(ContactDetails.class);
                    String whatsAppUrl = "+91" + contactDetails.getWhatsAppContact();
                    whatsapptext.setText(whatsAppUrl);
                    SpannableString content = new SpannableString(whatsAppUrl);
                    content.setSpan(new UnderlineSpan(), 0, whatsAppUrl.length(), 0);
                    whatsapptext.setText(content);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        whatsapptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=" + whatsapptext.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CustomerProfileActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CustomerProfileActivity.this);
                bottomSheetDialog.setContentView(R.layout.logout_confirmation);
                Button logout = bottomSheetDialog.findViewById(R.id.logout);
                Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

                bottomSheetDialog.show();
                bottomSheetDialog.setCancelable(false);

                logout.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!((Activity) CustomerProfileActivity.this).isFinishing()) {

                                    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.commit();

                                    Intent intent = new Intent(CustomerProfileActivity.this, LoginChoiceScreenActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                bottomSheetDialog.dismiss();
                            }
                        });
                stayinapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CustomerProfileActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void checkGPSConnection(Context context) {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);



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
        if (!((Activity) CustomerProfileActivity.this).isFinishing()) {
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