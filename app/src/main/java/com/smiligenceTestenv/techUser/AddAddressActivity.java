package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.OrderDetails;
import com.smiligenceTestenv.techUser.bean.ShippingAddress;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.Constant;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.Utils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceTestenv.techUser.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.VIEW_CART_FIREBASE_TABLE;

public class AddAddressActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
    DatabaseReference shippindAddressDataRef, distanceFeeDataRef, storeTimingDataRef, metrozStoteTimingDataRef, Orderreference, viewcartRef;
    boolean checkIntent = false;
    int perKmDistanceFee, resultKiloMeterRoundOff = 0, totaldeliveryFee = 0;
    double roundOff = 0.0, getStoreLatitude, getStoreLongtitude;
    TextView addNewAddress, firstAddressName, secondAddresName, thirdAddressName, fourthAddressName, firstAddressPhoneNumber, secondAddressPhoneNumber, thirdAddressPhoneNumber, fourthAddressPhoneNumber;
    RadioButton firstAddressRb, secondAddressRb, thirdAddressRb, fourthAddressRb;
    ImageView back;

    RelativeLayout firstRelativeLayout, secondRelelativeLayout, thirdRelativeLayout, fourthRelativeLayout;

    RazorpayClient razorpay;
    Button paymentBtn;
    ShippingAddress shippingAddress;
    ArrayList<ShippingAddress> shippingAddressArrayList = new ArrayList<>();
    long maxid = 0;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<>();
    JSONObject jsonObject;
    String resultOrderId, storeAddress, instructionString, paymentType, amount, deliverytType, receiptNumber, tipAmount, finalBill_tip, finalBill;
    ItemDetails itemDetails;
    OrderDetails orderDetails = new OrderDetails();
    Payment payment;

    int getCheckValue, getDistance;

    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime;
    String metrozStopTime;
    String storeIdTxt;
    boolean checkAsync = true;
    double sharedPreferenceLatitude = 0.0, sharedPreferenceLongtitude = 0.0;
    String sharedPreferenceLat, sharedPreferenceLong;
    int counter;
    TextView addressTypetext, addressTypeTwoText, addressTypeThreetext, addressTypeFourText;
    String selectedChidValue, deliveryFee;
    String saved_id;
    String fullAddress;
    CardView firstCard, secondCard, thirdCard, fourthCard;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_add_address);
        final SharedPreferences loginSharedPreferences1 = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences1.getString("customerId", "");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        back = findViewById(R.id.back);
        addressTypetext = findViewById(R.id.addresstypetext);
        addressTypeTwoText = findViewById(R.id.addresstypetwotext);
        addressTypeThreetext = findViewById(R.id.addresstypethreetext);
        addressTypeFourText = findViewById(R.id.addresstypefourtext);

        instructionString = getIntent().getStringExtra("instructionString");
        deliverytType = getIntent().getStringExtra("deliveryType");
        tipAmount = getIntent().getStringExtra("tips");
        finalBill_tip = getIntent().getStringExtra("finalBillTip");
        finalBill = getIntent().getStringExtra("finalBillAmount");
        storeAddress = getIntent().getStringExtra("storeAddress");
        getStoreLatitude = getIntent().getDoubleExtra("storeLatitude", 0.0);
        getStoreLongtitude = getIntent().getDoubleExtra("storeLongitude", 0.0);
        getCheckValue = getIntent().getIntExtra("childCountValue", 0);
        deliveryFee = getIntent().getStringExtra("deliveryFee");
        fullAddress = getIntent().getStringExtra("FullAddress");


        firstCard = findViewById(R.id.firstAddressLayout);
        secondCard = findViewById(R.id.secondAddressLayout);
        thirdCard = findViewById(R.id.thirdAddresslayout);
        fourthCard = findViewById(R.id.fourthAddresslayout);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        final SharedPreferences loginSharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);

        if (loginSharedPreferences != null && !("".equalsIgnoreCase(String.valueOf(loginSharedPreferences)))) {
            sharedPreferenceLat = loginSharedPreferences.getString("STORELATITUDE", "");
            sharedPreferenceLong = loginSharedPreferences.getString("STORELONGTITUDE", "");
        }
        if (sharedPreferenceLat != null && !sharedPreferenceLat.equals("")) {

            sharedPreferenceLatitude = Double.parseDouble(sharedPreferenceLat);

        }
        if (sharedPreferenceLong != null && !sharedPreferenceLong.equals("")) {
            sharedPreferenceLongtitude = Double.parseDouble(sharedPreferenceLong);
        }


        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();

        date = new SimpleDateFormat("HH:mm aa");


        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
        currentDateAndTime = dateFormat.format(new Date());
        currentTime = date.format(currentLocalTime);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAddressActivity.this, ViewCartActivity.class);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.putExtra("storeLatitude", getStoreLatitude);
                intent.putExtra("storeLongitude", getStoreLongtitude);
                intent.putExtra("deliveryFee", String.valueOf(deliveryFee));


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });


        Orderreference = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        viewcartRef = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);
        retriveFunction();

        paymentBtn = findViewById(R.id.paymentBtn);


        firstAddressRb = findViewById(R.id.AddressOneRb);
        secondAddressRb = findViewById(R.id.AddresstwoRb);
        thirdAddressRb = findViewById(R.id.AddressthreeRb);
        fourthAddressRb = findViewById(R.id.AddressfourRb);

        firstRelativeLayout = findViewById(R.id.firstAddressLayoutRelative);
        secondRelelativeLayout = findViewById(R.id.SecondAddressLayoutRelative);
        thirdRelativeLayout = findViewById(R.id.thirdAddresslayoutRelative);
        fourthRelativeLayout = findViewById(R.id.fourthAddresslayoutRelative);
        distanceFeeDataRef = CommonMethods.fetchFirebaseDatabaseReference("DeliveryCharges");


        shippindAddressDataRef = CommonMethods.fetchFirebaseDatabaseReference("ShippingAddress").child(String.valueOf(saved_id));

        firstAddressName = findViewById(R.id.customerNameOneTxt);
        secondAddresName = findViewById(R.id.customerNametwoTxt);
        thirdAddressName = findViewById(R.id.customerNamethreeTxt);
        fourthAddressName = findViewById(R.id.customerNamefourTxt);

        firstAddressPhoneNumber = findViewById(R.id.phoneNumberOneTxt);
        secondAddressPhoneNumber = findViewById(R.id.phoneNumbertwoTxt);
        thirdAddressPhoneNumber = findViewById(R.id.phoneNumberthreeTxt);
        fourthAddressPhoneNumber = findViewById(R.id.phoneNumberfourTxt);


        firstRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentFunction(1);

            }
        });

        secondRelelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentFunction(2);

            }
        });

        thirdRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentFunction(3);

            }
        });

        fourthRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentFunction(4);

            }
        });

        shippindAddressDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot detailsSnap : dataSnapshot.getChildren()) {
                        shippingAddress = detailsSnap.getValue(ShippingAddress.class);
                        shippingAddressArrayList.add(shippingAddress);
                    }
                    if (dataSnapshot.getChildrenCount() == 1) {
                        firstCard.setVisibility(View.VISIBLE);
                        firstRelativeLayout.setVisibility(View.VISIBLE);
                        addressFunctionOne(shippingAddressArrayList.get(0).getFullName(),
                                shippingAddressArrayList.get(0).getPhoneNumber(),
                                shippingAddressArrayList.get(0).getHouseAddress(),
                                shippingAddressArrayList.get(0).getAreaAddress(),
                                shippingAddressArrayList.get(0).getCity(),
                                shippingAddressArrayList.get(0).getPincode(),
                                shippingAddressArrayList.get(0).getAddressType());
                    }

                    if (dataSnapshot.getChildrenCount() == 2) {
                        firstRelativeLayout.setVisibility(View.VISIBLE);
                        secondRelelativeLayout.setVisibility(View.VISIBLE);
                        addressFunctionOne(shippingAddressArrayList.get(0).getFullName(),
                                shippingAddressArrayList.get(0).getPhoneNumber(),
                                shippingAddressArrayList.get(0).getHouseAddress(),
                                shippingAddressArrayList.get(0).getAreaAddress(),
                                shippingAddressArrayList.get(0).getCity(),
                                shippingAddressArrayList.get(0).getPincode(),
                                shippingAddressArrayList.get(0).getAddressType());
                        addressFunctionTwo(shippingAddressArrayList.get(1).getFullName(),
                                shippingAddressArrayList.get(1).getPhoneNumber(),
                                shippingAddressArrayList.get(1).getHouseAddress(),
                                shippingAddressArrayList.get(1).getAreaAddress(),
                                shippingAddressArrayList.get(1).getCity(),
                                shippingAddressArrayList.get(1).getPincode(),
                                shippingAddressArrayList.get(1).getAddressType());
                    }
                    if (dataSnapshot.getChildrenCount() == 3) {
                        firstRelativeLayout.setVisibility(View.VISIBLE);
                        secondRelelativeLayout.setVisibility(View.VISIBLE);
                        thirdRelativeLayout.setVisibility(View.VISIBLE);
                        addressFunctionOne(shippingAddressArrayList.get(0).getFullName(),
                                shippingAddressArrayList.get(0).getPhoneNumber(),
                                shippingAddressArrayList.get(0).getHouseAddress(),
                                shippingAddressArrayList.get(0).getAreaAddress(),
                                shippingAddressArrayList.get(0).getCity(),
                                shippingAddressArrayList.get(0).getPincode(),
                                shippingAddressArrayList.get(0).getAddressType());
                        addressFunctionTwo(shippingAddressArrayList.get(1).getFullName(),
                                shippingAddressArrayList.get(1).getPhoneNumber(),
                                shippingAddressArrayList.get(1).getHouseAddress(),
                                shippingAddressArrayList.get(1).getAreaAddress(),
                                shippingAddressArrayList.get(1).getCity(),
                                shippingAddressArrayList.get(1).getPincode(),
                                shippingAddressArrayList.get(1).getAddressType());
                        addressFunctionThird(shippingAddressArrayList.get(2).getFullName(),
                                shippingAddressArrayList.get(2).getPhoneNumber(),
                                shippingAddressArrayList.get(2).getHouseAddress(),
                                shippingAddressArrayList.get(2).getAreaAddress(),
                                shippingAddressArrayList.get(2).getCity(),
                                shippingAddressArrayList.get(2).getPincode(),
                                shippingAddressArrayList.get(2).getAddressType());

                    }
                    if (dataSnapshot.getChildrenCount() == 4) {
                        firstRelativeLayout.setVisibility(View.VISIBLE);
                        secondRelelativeLayout.setVisibility(View.VISIBLE);
                        thirdRelativeLayout.setVisibility(View.VISIBLE);
                        fourthRelativeLayout.setVisibility(View.VISIBLE);
                        addressFunctionOne(
                                shippingAddressArrayList.get(0).getFullName(),
                                shippingAddressArrayList.get(0).getPhoneNumber(),
                                shippingAddressArrayList.get(0).getHouseAddress(),
                                shippingAddressArrayList.get(0).getAreaAddress(),
                                shippingAddressArrayList.get(0).getCity(),
                                shippingAddressArrayList.get(0).getPincode(),
                                shippingAddressArrayList.get(0).getAddressType());

                        addressFunctionTwo(
                                shippingAddressArrayList.get(1).getFullName(),
                                shippingAddressArrayList.get(1).getPhoneNumber(),
                                shippingAddressArrayList.get(1).getHouseAddress(),
                                shippingAddressArrayList.get(1).getAreaAddress(),
                                shippingAddressArrayList.get(1).getCity(),
                                shippingAddressArrayList.get(1).getPincode(),
                                shippingAddressArrayList.get(1).getAddressType());
                        addressFunctionThird(
                                shippingAddressArrayList.get(2).getFullName(),
                                shippingAddressArrayList.get(2).getPhoneNumber(),
                                shippingAddressArrayList.get(2).getHouseAddress(),
                                shippingAddressArrayList.get(2).getAreaAddress(),
                                shippingAddressArrayList.get(2).getCity(),
                                shippingAddressArrayList.get(2).getPincode(),
                                shippingAddressArrayList.get(2).getAddressType());
                        addressFunctionFourth(
                                shippingAddressArrayList.get(3).getFullName(),
                                shippingAddressArrayList.get(3).getPhoneNumber(),
                                shippingAddressArrayList.get(3).getHouseAddress(),
                                shippingAddressArrayList.get(3).getAreaAddress(),
                                shippingAddressArrayList.get(3).getCity(),
                                shippingAddressArrayList.get(3).getPincode(),
                                shippingAddressArrayList.get(3).getAddressType());
                    }
                    if (dataSnapshot.getChildrenCount() == 1) {
                        firstCard.setVisibility(View.VISIBLE);
                    }
                    if (dataSnapshot.getChildrenCount() == 2) {
                        firstCard.setVisibility(View.VISIBLE);
                        secondCard.setVisibility(View.VISIBLE);
                    }
                    if (dataSnapshot.getChildrenCount() == 3) {
                        firstCard.setVisibility(View.VISIBLE);
                        secondCard.setVisibility(View.VISIBLE);
                        thirdCard.setVisibility(View.VISIBLE);

                    }
                    if (dataSnapshot.getChildrenCount() == 4) {
                        firstCard.setVisibility(View.VISIBLE);
                        secondCard.setVisibility(View.VISIBLE);
                        thirdCard.setVisibility(View.VISIBLE);
                        fourthCard.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addNewAddress = findViewById(R.id.addnewAddress);


        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shippindAddressDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.getChildrenCount() < 4) {


                            Intent intent = new Intent(AddAddressActivity.this, AddNewAddressActivity.class);
                            int childCountValue = (int) (snapshot.getChildrenCount() + 1);

                            intent.putExtra("childCountValue", childCountValue);
                            intent.putExtra("deliveryType", deliverytType);
                            intent.putExtra("tips", String.valueOf(tipAmount));
                            intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                            intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                            intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                            intent.putExtra("instructionString", instructionString);
                            intent.putExtra("storeAddress", storeAddress);
                            intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                            intent.putExtra("EditAddressIntent", "Add new Address");
                            startActivity(intent);
                        } else {
                            Toast.makeText(AddAddressActivity.this, "Please edit address", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        if (getCheckValue == 1) {
            secondAddressRb.setChecked(false);
            thirdAddressRb.setChecked(false);
            fourthAddressRb.setChecked(false);
            firstAddressRb.setChecked(true);
        }
        if (getCheckValue == 2) {
            secondAddressRb.setChecked(true);
            thirdAddressRb.setChecked(false);
            fourthAddressRb.setChecked(false);
            firstAddressRb.setChecked(false);
        }
        if (getCheckValue == 3) {
            secondAddressRb.setChecked(false);
            thirdAddressRb.setChecked(true);
            fourthAddressRb.setChecked(false);
            firstAddressRb.setChecked(false);
        }
        if (getCheckValue == 4) {

            secondAddressRb.setChecked(false);
            thirdAddressRb.setChecked(false);
            fourthAddressRb.setChecked(true);
            firstAddressRb.setChecked(false);
        }


        firstAddressRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter = 0;
                    selectedChidValue = "1";
                    secondAddressRb.setChecked(false);
                    thirdAddressRb.setChecked(false);
                    fourthAddressRb.setChecked(false);
                }
            }
        });

        secondAddressRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter = 1;
                    selectedChidValue = "2";
                    firstAddressRb.setChecked(false);
                    thirdAddressRb.setChecked(false);
                    fourthAddressRb.setChecked(false);
                }
            }
        });

        thirdAddressRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter = 2;
                    selectedChidValue = "3";
                    firstAddressRb.setChecked(false);
                    secondAddressRb.setChecked(false);
                    fourthAddressRb.setChecked(false);

                }
            }
        });

        fourthAddressRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter = 3;
                    selectedChidValue = "4";
                    firstAddressRb.setChecked(false);
                    secondAddressRb.setChecked(false);
                    thirdAddressRb.setChecked(false);

                }
            }
        });


        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFunction();

            }
        });
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void bottomSheetDialog() {
        if (!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available")) {
            if (saved_id != null && !"".equals(saved_id)) {

                if (!((Activity) AddAddressActivity.this).isFinishing()) {
                    if (itemDetailList.size() == 0) {
                        Toast.makeText(AddAddressActivity.this, "" +
                                "No items available in the cart ", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent intent = new Intent(AddAddressActivity.this, PaymentActivity.class);


                        intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                        intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                        intent.putExtra("deliveryType", deliverytType);
                        intent.putExtra("tips", String.valueOf(tipAmount));
                        intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                        intent.putExtra("instructionString", instructionString);
                        intent.putExtra("storeAddress", storeAddress);
                        intent.putExtra("storeLatitude", getStoreLatitude);
                        intent.putExtra("storeLongitude", getStoreLongtitude);
                        intent.putExtra("addressPref", "2");
                        intent.putExtra("StoreId", storeIdTxt);
                        intent.putExtra("FromNewAddress", "FromNewAddress");
                        String customerName = shippingAddressArrayList.get(counter).getFullName().toUpperCase();
                        String customerMobilenumber = shippingAddressArrayList.get(counter).getPhoneNumber();
                        String customerPinCode = shippingAddressArrayList.get(counter).getPincode();


                        String address = (shippingAddressArrayList.get(counter).getHouseAddress() + "," + shippingAddressArrayList.get(counter).getAreaAddress() + "," +
                                shippingAddressArrayList.get(counter).getCity() + "," + shippingAddressArrayList.get(counter).getState() + "," + shippingAddressArrayList.get(counter).getPincode());

                        intent.putExtra("shippingAddress", address);
                        intent.putExtra("pinCodeIntent", shippingAddressArrayList.get(counter).getPincode());
                        intent.putExtra("customerName", customerName);
                        intent.putExtra("customerMobilenumber", customerMobilenumber);
                        intent.putExtra("customerPinCode", customerPinCode);
                        intent.putExtra("AddressId", selectedChidValue);
                        intent.putExtra("deliveryFee", String.valueOf(deliveryFee));

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }
            } else {
                Toast.makeText(AddAddressActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                paymentBtn.setEnabled(true);
            }
        } else {
            SweetAlertDialog sweetAlertDialog;
           sweetAlertDialog= new SweetAlertDialog(AddAddressActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("No Network Connection");
           sweetAlertDialog.show();
            Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
            btn.setBackgroundColor(ContextCompat.getColor(AddAddressActivity.this, R.color.colorPrimary));


            paymentBtn.setEnabled(true);
        }
    }


    public void intentFunction(int i) {
        Intent intent = new Intent(getApplicationContext(), AddNewAddressActivity.class);


        intent.putExtra("childCountValue", i);
        intent.putExtra("deliveryType", deliverytType);
        intent.putExtra("tips", String.valueOf(tipAmount));
        intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
        intent.putExtra("finalBillAmount", String.valueOf(finalBill));
        intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
        intent.putExtra("instructionString", instructionString);
        intent.putExtra("storeAddress", storeAddress);
        intent.putExtra("storeLatitude", getStoreLatitude);
        intent.putExtra("storeLongitude", getStoreLongtitude);
        intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
        intent.putExtra("EditAddressIntent", "Edit Address");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();


        Orderreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void retriveFunction() {

        viewcartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount()>0)
                {
                    itemDetailList.clear();

                    for (DataSnapshot viewCartsnap : dataSnapshot.getChildren()) {
                        itemDetails = viewCartsnap.getValue(ItemDetails.class);
                        itemDetailList.add(itemDetails);
                    }
                    if (itemDetailList != null && itemDetailList.size() > 0) {
                        storeIdTxt = itemDetailList.get(0).getSellerId();
                    }

                    orderDetails.setItemDetailList(itemDetailList);
                    orderDetails.setStoreName(itemDetails.getStoreName());
                    orderDetails.setStorePincode(itemDetails.getStorePincode());
                    orderDetails.setStoreAddress(itemDetails.getStoreAdress());
                    orderDetails.setDeliveryType(deliverytType);
                    orderDetails.setTotalAmount(Integer.parseInt(finalBill));
                    checkIntent = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(AddAddressActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public void addressFunctionOne(String customerName, String phoneNumber, String homeAddress, String areaAddress, String cityAddress, String pincode, String addressType) {

        String upperString = customerName;
        String newString = upperString.substring(0, 1).toUpperCase() + upperString.substring(1).toLowerCase();
        firstAddressName.setText(newString);
        firstAddressPhoneNumber.setText(phoneNumber);

        if (addressType.equals("Work/Office Address")) {
            addressTypetext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_work_24, 0, 0, 0);
            addressTypetext.setText(addressType);
        } else {
            addressTypetext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_home_24, 0, 0, 0);
            addressTypetext.setText(addressType);
        }
        firstAddressRb.setText(homeAddress + "," + "\n" + areaAddress + "," + "\n" + cityAddress + "-" + pincode);
    }

    public void addressFunctionTwo(String customerName, String phoneNumber, String homeAddress, String areaAddress, String cityAddress, String pincode, String addressType) {

        String upperString = customerName;
        String newString = upperString.substring(0, 1).toUpperCase() + upperString.substring(1).toLowerCase();
        secondAddresName.setText(newString);
        secondAddressPhoneNumber.setText(phoneNumber);
        if (addressType.equals("Work/Office Address")) {
            addressTypeTwoText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_work_24, 0, 0, 0);
            addressTypeTwoText.setText(addressType);
        } else {
            addressTypeTwoText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_home_24, 0, 0, 0);
            addressTypeTwoText.setText(addressType);
        }

        secondAddressRb.setText(homeAddress + "," + "\n" + areaAddress + "," + "\n" + cityAddress + "-" + pincode);
    }

    public void addressFunctionThird(String customerName, String phoneNumber, String homeAddress, String areaAddress, String cityAddress, String pincode, String addressType) {
        String upperString = customerName;
        String newString = upperString.substring(0, 1).toUpperCase() + upperString.substring(1).toLowerCase();
        thirdAddressName.setText(newString);
        thirdAddressPhoneNumber.setText(phoneNumber);


        if (addressType.equals("Work/Office Address")) {
            addressTypeThreetext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_work_24, 0, 0, 0);
            addressTypeThreetext.setText(addressType);
        } else {
            addressTypeThreetext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_home_24, 0, 0, 0);
            addressTypeThreetext.setText(addressType);
        }

        thirdAddressRb.setText(homeAddress + "," + "\n" + areaAddress + "," + "\n" + cityAddress + "-" + pincode);
    }

    public void addressFunctionFourth(String customerName, String phoneNumber, String homeAddress, String areaAddress, String cityAddress, String pincode, String addressType) {
        String upperString = customerName;
        String newString = upperString.substring(0, 1).toUpperCase() + upperString.substring(1).toLowerCase();
        fourthAddressName.setText(newString);
        fourthAddressPhoneNumber.setText(phoneNumber);

        if (addressType.equals("Work/Office Address")) {
            addressTypeFourText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_work_24, 0, 0, 0);
            addressTypeFourText.setText(addressType);
        } else {
            addressTypeFourText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_home_24, 0, 0, 0);
            addressTypeFourText.setText(addressType);
        }

        fourthAddressRb.setText(homeAddress + "," + "\n" + areaAddress + "," + "\n" + cityAddress + "-" + pincode);
    }


    public void loadFunction() {
        if (firstAddressRb.isChecked()) {

            bottomSheetDialog();
            orderDetails.setFullName(shippingAddressArrayList.get(0).getFullName().toUpperCase());
            orderDetails.setShippingaddress(shippingAddressArrayList.get(0).getHouseAddress() + "," + shippingAddressArrayList.get(0).getAreaAddress() + "," +
                    shippingAddressArrayList.get(0).getCity() + "," + shippingAddressArrayList.get(0).getState() + "," + shippingAddressArrayList.get(0).getPincode());
            orderDetails.setShippingPincode(shippingAddressArrayList.get(0).getPincode());
            orderDetails.setPhoneNumber(shippingAddressArrayList.get(0).getPhoneNumber());

        } else if (secondAddressRb.isChecked()) {

            bottomSheetDialog();
            orderDetails.setFullName(shippingAddressArrayList.get(1).getFullName().toUpperCase());
            orderDetails.setShippingaddress(shippingAddressArrayList.get(1).getHouseAddress() + "," + shippingAddressArrayList.get(1).getAreaAddress() + "," +
                    shippingAddressArrayList.get(1).getCity() + "," + shippingAddressArrayList.get(1).getState() + "," + shippingAddressArrayList.get(1).getPincode());
            orderDetails.setShippingPincode(shippingAddressArrayList.get(1).getPincode());
            orderDetails.setPhoneNumber(shippingAddressArrayList.get(1).getPhoneNumber());


        } else if (thirdAddressRb.isChecked()) {

            bottomSheetDialog();
            orderDetails.setFullName(shippingAddressArrayList.get(2).getFullName().toUpperCase());
            orderDetails.setShippingaddress(shippingAddressArrayList.get(2).getHouseAddress() + "," + shippingAddressArrayList.get(2).getAreaAddress() + "," +
                    shippingAddressArrayList.get(2).getCity() + "," + shippingAddressArrayList.get(2).getState() + "," + shippingAddressArrayList.get(2).getPincode());
            orderDetails.setShippingPincode(shippingAddressArrayList.get(2).getPincode());
            orderDetails.setPhoneNumber(shippingAddressArrayList.get(2).getPhoneNumber());

        } else if (fourthAddressRb.isChecked()) {

            bottomSheetDialog();
            orderDetails.setFullName(shippingAddressArrayList.get(3).getFullName().toUpperCase());
            orderDetails.setShippingaddress(shippingAddressArrayList.get(3).getHouseAddress() + "," + shippingAddressArrayList.get(3).getAreaAddress() + "," +
                    shippingAddressArrayList.get(3).getCity() + "," + shippingAddressArrayList.get(3).getState() + "," + shippingAddressArrayList.get(3).getPincode());
            orderDetails.setShippingPincode(shippingAddressArrayList.get(3).getPincode());
            orderDetails.setPhoneNumber(shippingAddressArrayList.get(3).getPhoneNumber());

        } else if (!firstAddressRb.isChecked() && !secondAddressRb.isChecked() &&
                !thirdAddressRb.isChecked() && !fourthAddressRb.isChecked()) {
            Toast.makeText(AddAddressActivity.this, "Please Select address", Toast.LENGTH_SHORT).show();
            paymentBtn.setEnabled(true);
        } else {
            Toast.makeText(AddAddressActivity.this, "Please add address", Toast.LENGTH_SHORT).show();
            paymentBtn.setEnabled(true);
        }
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
        if (!((Activity) AddAddressActivity.this).isFinishing()) {
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





