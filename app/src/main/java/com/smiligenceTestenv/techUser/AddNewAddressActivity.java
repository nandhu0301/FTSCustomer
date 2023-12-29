package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.bean.PincodeDetails;
import com.smiligenceTestenv.techUser.bean.ShippingAddress;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;
import com.smiligenceTestenv.techUser.common.Utils;

import java.util.ArrayList;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceTestenv.techUser.common.Constant.BOOLEAN_FALSE;
import static com.smiligenceTestenv.techUser.common.Constant.BOOLEAN_TRUE;
import static com.smiligenceTestenv.techUser.common.Constant.RESTRICTED_PINCODE;

@SuppressWarnings("PointlessBooleanExpression")
public class AddNewAddressActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
    DatabaseReference shippingDataBaseRef, pincodeRef;
    Button saveShippingAddress;
    EditText customerNameShipping, customerPhoneNumberShipping, alternatePhoneNumberShipping, houseNoShipping, areaShipping,
            pincodeShipping, landmarkShipping;

    EditText cityShipping, stateShiping;
    int shippindAddressMaxId = 0;
    RadioButton homeAddressRadioButton, officeAddressRadioButton;
    RadioGroup radioGroup;
    String addressType;
    ShippingAddress shippingAddressOne = new ShippingAddress();
    TextInputLayout customerNameShippingTxt, customerPhoneNumberShippingTxt, alternatePhoneNumberShippingTxt, houseNoShippingTxt, areaShippingTxt,
            pincodeShippingTxt, landmarkShippingtTxt, cityShippingTxt, stateShipingTxt;
    boolean isAddressSelected, isAutoLoadFunction = false;
    int receivedChildCountValue;
    ImageView backtoaddress;
    String tipAmount, finalBill_tip, finalBill, deliverytType;
    String instructionString, storeAddress;
    boolean intentCheck = false;


    String getFullAdreesFromMap, shippingAddressPinCode, shippingCity, shippingState;

    String deliveryFee;
    PincodeDetails pincodeDetails;
    ArrayList<PincodeDetails> pincodeDetailsArrayList = new ArrayList<>();
    int counter;
    boolean isValidPinCode = BOOLEAN_FALSE;
    String saved_id;
    String EditAddressIntent;
    TextView textTitle;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getDatabase();
        setContentView(R.layout.activity_add_new_address);
        textTitle = findViewById(R.id.title);
        //disableAutofill();
        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");

     /*   if (Build.VERSION.SDK_INT > 5 ) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
            }
        }*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        deliverytType = getIntent().getStringExtra("deliveryType");
        tipAmount = getIntent().getStringExtra("tips");
        finalBill_tip = getIntent().getStringExtra("finalBillTip");
        finalBill = getIntent().getStringExtra("finalBillAmount");
        instructionString = getIntent().getStringExtra("instructionString");
        storeAddress = getIntent().getStringExtra("storeAddress");
        getFullAdreesFromMap = getIntent().getStringExtra("FullAddress");
        shippingAddressPinCode = getIntent().getStringExtra("shippingAddressPinCode");
        shippingCity = getIntent().getStringExtra("shippingAddressCity");
        shippingState = getIntent().getStringExtra("shippingAddressState");
        deliveryFee = getIntent().getStringExtra("deliveryFee");
        EditAddressIntent = getIntent().getStringExtra("EditAddressIntent");

        if (EditAddressIntent != null) {
            textTitle.setText(EditAddressIntent);
        }


        backtoaddress = findViewById(R.id.backtoaddress);
        radioGroup = findViewById(R.id.groupradio);


        backtoaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewAddressActivity.this, AddAddressActivity.class);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        receivedChildCountValue = getIntent().getIntExtra("childCountValue", 0);


        shippingDataBaseRef = CommonMethods.fetchFirebaseDatabaseReference("ShippingAddress").child(String.valueOf(saved_id));
        pincodeRef = CommonMethods.fetchFirebaseDatabaseReference(RESTRICTED_PINCODE);
        saveShippingAddress = findViewById(R.id.SaveShippingAddress);
        customerNameShipping = findViewById(R.id.shippingName);
        customerPhoneNumberShipping = findViewById(R.id.shippingMobileNumber);
        alternatePhoneNumberShipping = findViewById(R.id.shippingAlternatePhoneNumber);
        houseNoShipping = findViewById(R.id.shippingHousenumber);
        areaShipping = findViewById(R.id.ShippingArea);
        cityShipping = findViewById(R.id.ShippingCity);
        stateShiping = findViewById(R.id.ShippingState);
        pincodeShipping = findViewById(R.id.shippingpincode);
        landmarkShipping = findViewById(R.id.shippinglandmark);
        homeAddressRadioButton = findViewById(R.id.shippingHomeAddress);
        officeAddressRadioButton = findViewById(R.id.shippingOfficeAddress);
        customerNameShippingTxt = findViewById(R.id.shippingnameTxt);
        customerPhoneNumberShippingTxt = findViewById(R.id.shippingMobileNumberTxt);
        alternatePhoneNumberShippingTxt = findViewById(R.id.shippingAlternatePhoneNumberTxt);
        houseNoShippingTxt = findViewById(R.id.shippingHousenumberTxt);
        areaShippingTxt = findViewById(R.id.ShippingAreaTxt);
        pincodeShippingTxt = findViewById(R.id.shippingpincodeTxt);
        landmarkShippingtTxt = findViewById(R.id.shippinglandmarkTxt);
        cityShippingTxt = findViewById(R.id.ShippingCityTxt);
        stateShipingTxt = findViewById(R.id.ShippingStateTxt);


        if (receivedChildCountValue != 0) {
            final Query shippindAddressDetails = shippingDataBaseRef.orderByChild("shippingId")
                    .equalTo(String.valueOf(receivedChildCountValue));

            shippindAddressDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() > 0) {

                        for (DataSnapshot shippindAddressSnap : dataSnapshot.getChildren()) {

                            ShippingAddress shippingAddress = shippindAddressSnap.getValue(ShippingAddress.class);
                            isAutoLoadFunction = true;
                            customerNameShipping.setText(shippingAddress.getFullName());
                            customerPhoneNumberShipping.setText(shippingAddress.getPhoneNumber());
                            alternatePhoneNumberShipping.setText(shippingAddress.getAlternatePhoneNumber());
                            houseNoShipping.setText(shippingAddress.getHouseAddress());
                            areaShipping.setText(shippingAddress.getAreaAddress());

                            if (stateShiping.getText().toString() == null || stateShiping.getText().toString().trim().isEmpty()) {
                                stateShiping.setText(shippingAddress.getState());
                            }
                            if (cityShipping.getText().toString() == null || cityShipping.getText().toString().trim().isEmpty()) {
                                cityShipping.setText(shippingAddress.getCity());
                            }

                            if (pincodeShipping.getText().toString() == null || pincodeShipping.getText().toString().trim().isEmpty()) {
                                pincodeShipping.setText(shippingAddress.getPincode());
                            }

                            if (landmarkShipping.getText().toString() == null || landmarkShipping.getText().toString().trim().isEmpty()) {
                                landmarkShipping.setText(shippingAddress.getLandMark());
                            }


                            if (shippingAddress.getAddressType().equals("Home Address")) {
                                homeAddressRadioButton.setChecked(true);
                                isAddressSelected = true;
                                addressType = "Home Address";
                            } else {
                                officeAddressRadioButton.setChecked(true);
                                isAddressSelected = true;
                                addressType = "Work/Office Address";
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        homeAddressRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressType = "Home Address";
                isAddressSelected = true;

            }
        });
        officeAddressRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressType = "Work/Office Address";
                isAddressSelected = true;
            }
        });


        saveShippingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("".equals(customerNameShipping.getText().toString().trim()) || customerNameShipping.getText().toString().trim() == null || " ".equalsIgnoreCase(customerNameShipping.getText().toString().trim())) {
                    customerNameShipping.setError("Required");
                    return;
                } else if ("".equals(customerPhoneNumberShipping.getText().toString())) {
                    customerPhoneNumberShipping.setError("Required");

                    return;
                } else if (!TextUtils.validatePhoneNumber(customerPhoneNumberShipping.getText().toString())) {
                    customerPhoneNumberShipping.setError("Invalid Phone Number");

                    return;
                } else if (!"".equals(alternatePhoneNumberShipping.getText().toString())
                        && (!TextUtils.validatePhoneNumber(alternatePhoneNumberShipping.getText().toString()))) {
                    alternatePhoneNumberShipping.setError("Invalid Phone Number");

                    return;
                } else if (alternatePhoneNumberShipping.getText().toString().equalsIgnoreCase(customerPhoneNumberShipping.getText().toString())) {
                    alternatePhoneNumberShipping.setError("Alternate Phone Number cannot be same as Primary Phone Number");

                    return;
                } else if ("".equals(houseNoShipping.getText().toString())) {
                    houseNoShipping.setError("Required");

                    return;
                } else if (TextUtils.isNumeric(houseNoShipping.getText().toString().trim())) {
                    houseNoShipping.setError("Invalid Building Name");
                    return;
                } else if ("".equals(areaShipping.getText().toString())) {
                    areaShipping.setError("Required");

                    return;
                } else if (TextUtils.isNumeric(areaShipping.getText().toString().trim())) {
                    areaShipping.setError("Invalid Road Name, Area, Colony ");
                    return;
                } else if ("".equals(cityShipping.getText().toString())) {
                    cityShipping.setError("Required");

                    return;
                } else if ("".equals(stateShiping.getText().toString())) {
                    cityShipping.setError(null);
                    stateShiping.setError("Required");
                    return;
                } else if ("".equals(pincodeShipping.getText().toString())) {
                    stateShiping.setError(null);
                    pincodeShipping.setError("Required");
                    return;
                } else if (!TextUtils.validPinCode(pincodeShipping.getText().toString())) {
                    Toast.makeText(AddNewAddressActivity.this, "Delivery is not available in this location", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!landmarkShipping.getText().toString().trim().isEmpty() && TextUtils.isNumeric(landmarkShipping.getText().toString().trim())) {
                    landmarkShipping.setError("Invalid Landmark");
                    return;
                } else if (pincodeDetailsArrayList != null) {
                    Iterator itemIterator = pincodeDetailsArrayList.iterator();

                    while (itemIterator.hasNext()) {

                        PincodeDetails pincodeDetails = (PincodeDetails) itemIterator.next();
                        Log.d("pincodeDetails", pincodeDetails.getPincodeStatus() + pincodeDetails.getPinCode());

                        if (pincodeShipping.getText().toString().trim().equalsIgnoreCase(pincodeDetails.getPinCode())
                                && "Available".equalsIgnoreCase(pincodeDetails.getPincodeStatus())) {

                            isValidPinCode = BOOLEAN_TRUE;

                            break;
                        } else {

                            isValidPinCode = BOOLEAN_FALSE;
                        }
                    }
                }
                if (isValidPinCode) {
                    SweetAlertDialog sweetAlertDialog;

                   sweetAlertDialog= new SweetAlertDialog(AddNewAddressActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Delivery is restricted in the chosen Pincode Location due to Logistics concern. Kindly select another suitable delivery location")
                           ;
                   sweetAlertDialog.show();;
                    Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                    btn.setBackgroundColor(ContextCompat.getColor(AddNewAddressActivity.this, R.color.colorPrimary));
                    return;
                } else if (isAddressSelected == false) {
                    Toast.makeText(getApplicationContext(), "Please select address Type", Toast.LENGTH_SHORT).show();
                    return;
                } else if ((isAddressSelected) && (!isValidPinCode)) {

                    insertFunction();

                    if (isAutoLoadFunction) {

                        if (intentCheck == true) {
                            shippingAddressOne.setShippingId(String.valueOf(receivedChildCountValue));
                            shippingDataBaseRef.child(String.valueOf(receivedChildCountValue)).setValue(shippingAddressOne);
                            intentCheck = false;
                        }

                        Intent intent = new Intent(AddNewAddressActivity.this, AddAddressActivity.class);
                        intent.putExtra("deliveryType", deliverytType);
                        intent.putExtra("tips", String.valueOf(tipAmount));
                        intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                        intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                        intent.putExtra("instructionString", instructionString);
                        intent.putExtra("storeAddress", storeAddress);
                        intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    } else {

                        if (intentCheck == true) {
                            shippingAddressOne.setShippingId(String.valueOf(receivedChildCountValue));
                            shippingDataBaseRef.child(String.valueOf(receivedChildCountValue)).setValue(shippingAddressOne);
                            intentCheck = false;
                        }
                        Intent intent = new Intent(AddNewAddressActivity.this, AddAddressActivity.class);
                        intent.putExtra("deliveryType", deliverytType);
                        intent.putExtra("tips", String.valueOf(tipAmount));
                        intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                        intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                        intent.putExtra("instructionString", instructionString);
                        intent.putExtra("storeAddress", storeAddress);
                        intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                    }
                }


            }
        });

        shippingDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shippindAddressMaxId = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void insertFunction() {
        if (!((Activity) AddNewAddressActivity.this).isFinishing()) {
            shippingAddressOne.setFullName(customerNameShipping.getText().toString().replace(" ", ""));
            shippingAddressOne.setPhoneNumber(customerPhoneNumberShipping.getText().toString().replace(" ", ""));
            shippingAddressOne.setAlternatePhoneNumber(alternatePhoneNumberShipping.getText().toString().replace(" ", ""));
            shippingAddressOne.setHouseAddress(houseNoShipping.getText().toString().replace(" ", ""));
            shippingAddressOne.setAreaAddress(areaShipping.getText().toString().replace(" ", ""));
            shippingAddressOne.setCity(cityShipping.getText().toString().replace(" ", ""));
            shippingAddressOne.setState(stateShiping.getText().toString().replace(" ", ""));
            shippingAddressOne.setPincode(pincodeShipping.getText().toString().replace(" ", ""));
            shippingAddressOne.setLandMark(landmarkShipping.getText().toString().replace(" ", ""));
            shippingAddressOne.setAddressType(addressType);
            intentCheck = true;
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddNewAddressActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        pincodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot pincodeSnap : dataSnapshot.getChildren()) {
                        pincodeDetails = pincodeSnap.getValue(PincodeDetails.class);
                        pincodeDetailsArrayList.add(pincodeDetails);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        if (!((Activity) AddNewAddressActivity.this).isFinishing()) {
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

