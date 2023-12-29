package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.smiligenceTestenv.techUser.Adapter.DiscountAdapter;
import com.smiligenceTestenv.techUser.Adapter.NotApplicableDiscountsAdapter;
import com.smiligenceTestenv.techUser.bean.Discount;
import com.smiligenceTestenv.techUser.bean.OrderDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.DateUtils;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_DISCOUNT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.DATE_FORMAT;
import static com.smiligenceTestenv.techUser.common.Constant.DISCOUNT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;

public class DiscountActivity extends AppCompatActivity   implements NetworkStateReceiver.NetworkStateReceiverListener
 {


    String resultstoreId, storeAddress, instructionString, paymentType, amount, deliverytType, receiptNumber, tipAmount, finalBill_tip, finalBill;
    double roundOff = 0.0, getStoreLatitude, getStoreLongtitude;
    ImageView backButton;
    DatabaseReference oneTimeDiscountRef, discountsDataRef;
    Discount discount;
    Discount discount1;
    ArrayList<Discount> discountArrayList = new ArrayList<>();
    final ArrayList<Discount> discountArrayListNew = new ArrayList<>();
    final ArrayList<Discount> notApplicableDiscountArrayList = new ArrayList<>();
    ListView discountListview;
    ListView notApplicableListview;
    DiscountAdapter discountAdapter;
    NotApplicableDiscountsAdapter notApplicableDiscountsAdapter;
    String getStoreName, getStoreId, getCategoryName, getCategoryId, getPincode, getAddressProof,
            getFullAddressFromMap, getShippingAddress, getcustomerName, getMobileNumber, getCustomerPincode;
    String deliveryFee;
    String pinCodeIntent;
    TextView availableTextView;
    LinearLayout linearLayout;
    DatabaseReference orderDetailsDataRef;
    String saved_id;
    ArrayList<String> discountNameArrayList = new ArrayList<>();
    String pattern = "hh:mm";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
     private NetworkStateReceiver networkStateReceiver;
     AlertDialog alertDialog;
     ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);


        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        backButton = findViewById(R.id.backButtomImageView);
        linearLayout = findViewById(R.id.textLayout);


        discountsDataRef = CommonMethods.fetchFirebaseDatabaseReference(DISCOUNT_DETAILS_FIREBASE_TABLE);
        oneTimeDiscountRef = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DISCOUNT_DETAILS_FIREBASE_TABLE);
        orderDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);

        discountListview = findViewById(R.id.discountsListview);
        notApplicableListview = findViewById(R.id.notapplicablediscountsListview);
        availableTextView = findViewById(R.id.textAvailoffers);


        getStoreName = getIntent().getStringExtra("StoreName");
        getStoreId = getIntent().getStringExtra("StoreId");
        getCategoryName = getIntent().getStringExtra("categoryName");
        getCategoryId = getIntent().getStringExtra("categoryId");
        getPincode = getIntent().getStringExtra("pinCode");
        getAddressProof = getIntent().getStringExtra("addressPref");
        getFullAddressFromMap = getIntent().getStringExtra("FullAddress");
        getShippingAddress = getIntent().getStringExtra("shippingAddress");
        getcustomerName = getIntent().getStringExtra("customerName");
        getMobileNumber = getIntent().getStringExtra("customerMobilenumber");
        getCustomerPincode = getIntent().getStringExtra("customerPinCode");
        instructionString = getIntent().getStringExtra("instructionString");
        deliverytType = getIntent().getStringExtra("deliveryType");
        tipAmount = getIntent().getStringExtra("tips");
        finalBill_tip = getIntent().getStringExtra("finalBillTip");
        finalBill = getIntent().getStringExtra("finalBillAmount");
        storeAddress = getIntent().getStringExtra("storeAddress");
        getStoreLatitude = getIntent().getDoubleExtra("storeatitude", 0.0);
        getStoreLongtitude = getIntent().getDoubleExtra("storeLongitude", 0.0);
        deliveryFee = getIntent().getStringExtra("deliveryFee");
        pinCodeIntent = getIntent().getStringExtra("pinCodeIntent");

        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();
        date = new SimpleDateFormat("HH:mm aa");
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        currentDateAndTime = dateFormat.format(new Date());
        currentTime = date.format(currentLocalTime);

        final SharedPreferences loginSharedPreferences1 = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences1.getString("customerId", "");

        final SharedPreferences loginSharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);
        resultstoreId = loginSharedPreferences.getString("STOREID", "");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra("pinCodeIntent", pinCodeIntent);
                intent.putExtra("StoreName", getStoreName);
                intent.putExtra("StoreId", getStoreId);
                intent.putExtra("categoryName", getCategoryName);
                intent.putExtra("categoryId", getCategoryId);
                intent.putExtra("pinCode", getPincode);
                intent.putExtra("addressPref", getAddressProof);
                intent.putExtra("FullAddress", getFullAddressFromMap);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.putExtra("shippingAddress", getShippingAddress);
                intent.putExtra("customerName", getcustomerName);
                intent.putExtra("customerMobilenumber", getMobileNumber);
                intent.putExtra("customerPinCode", getCustomerPincode);
                intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });



        discountsDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                discountArrayList.clear();
                notApplicableDiscountArrayList.clear();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot discountSnap : dataSnapshot.getChildren()) {
                        discount = discountSnap.getValue(Discount.class);
                        if (discount.getValidTillDate() != null) {
                            try {
                                if ((new SimpleDateFormat("dd-MM-yyyy").parse(DateUtils.fetchFormatedCurrentDate()).before((new SimpleDateFormat("dd-MM-yyyy").parse(discount.getValidTillDate()))))) {
                                    if (discount.getDiscountStatus().equals("Active")) {
                                        if (discount.getTypeOfDiscount() != null) {
                                            if (discount.getVisibility().equals("Visible")) {
                                                if (discount.getTypeOfDiscount().equals("Price")) {
                                                    discountPriceFunction(discount);
                                                } else if (discount.getTypeOfDiscount().equals("Percent")) {
                                                    discountPercentFunction(discount);
                                                }
                                            }
                                        }
                                    } else {
                                        notApplicableDiscountArrayList.add(discount);
                                    }
                                } else if (DateUtils.fetchFormatedCurrentDate().equals(discount.getValidTillDate())) {
                                    try {
                                        if ((sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == -1) ||
                                                (sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == 0)) {
                                            if (discount.getDiscountStatus().equals("Active")) {
                                                if (discount.getTypeOfDiscount() != null) {
                                                    if (discount.getVisibility().equals("Visible")) {
                                                        if (discount.getTypeOfDiscount().equals("Price")) {
                                                            discountPriceFunction(discount);
                                                        } else if (discount.getTypeOfDiscount().equals("Percent")) {
                                                            discountPercentFunction(discount);
                                                        }
                                                    }
                                                }
                                            } else {
                                                notApplicableDiscountArrayList.add(discount);
                                            }
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                oneTimeDiscountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {

                            for (DataSnapshot discountSnap : dataSnapshot.getChildren()) {
                                discount = discountSnap.getValue(Discount.class);
                                if (discount.getCustomerId().equals(saved_id)) {
                                    if (discount.getValidTillDate() != null) {
                                        try {
                                            if ((new SimpleDateFormat("dd-MM-yyyy").parse(DateUtils.fetchFormatedCurrentDate()).before((new SimpleDateFormat("dd-MM-yyyy").parse(discount.getValidTillDate()))))) {

                                                System.out.println("Comes 1");
                                                if (discount.getDiscountStatus().equals("Active")) {
                                                    if (discount.getTypeOfDiscount() != null) {
                                                        if (discount.getVisibility().equals("Visible")) {
                                                            if (discount.getTypeOfDiscount().equals("Price")) {
                                                                discountPriceFunction(discount);
                                                            } else if (discount.getTypeOfDiscount().equals("Percent")) {

                                                                discountPercentFunction(discount);
                                                            }
                                                        }

                                                    }
                                                }
                                            } else if (DateUtils.fetchFormatedCurrentDate().equals(discount.getValidTillDate())) {
                                                try {
                                                    System.out.println("Comes 2");
                                                    if ((sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == -1) ||
                                                            (sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == 0)) {
                                                        System.out.println("Comes 3");
                                                        if (discount.getDiscountStatus().equals("Active")) {
                                                            System.out.println("Comes 3");

                                                            if (discount.getTypeOfDiscount() != null) {

                                                                if (discount.getVisibility().equals("Visible")) {
                                                                    if (discount.getTypeOfDiscount().equals("Price")) {
                                                                        discountPriceFunction(discount);
                                                                        System.out.println("Comes 4");
                                                                    } else if (discount.getTypeOfDiscount().equals("Percent")) {
                                                                        discountPercentFunction(discount);
                                                                        System.out.println("Comes 5");
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            }


                        } else {

                        }
                        TextUtils.removeDuplicatesList(discountArrayList);
                        discountAdapter = new DiscountAdapter(DiscountActivity.this, discountArrayList);
                        discountAdapter.notifyDataSetChanged();
                        discountListview.setAdapter(discountAdapter);
                        if (discountAdapter != null) {
                            availableTextView.setVisibility(View.VISIBLE);
                            int totalHeight = 0;
                            for (int i = 0; i < discountAdapter.getCount(); i++) {
                                View listItem = discountAdapter.getView(i, null, discountListview);
                                listItem.measure(0, 0);
                                totalHeight += listItem.getMeasuredHeight();
                            }
                            ViewGroup.LayoutParams params = discountListview.getLayoutParams();
                            params.height = totalHeight + (discountListview.getDividerHeight() * (discountAdapter.getCount() - 1));
                            discountListview.setLayoutParams(params);
                            discountListview.requestLayout();
                            discountListview.setAdapter(discountAdapter);
                            discountAdapter.notifyDataSetChanged();
                        }
                        notApplicableDiscountsAdapter = new NotApplicableDiscountsAdapter(DiscountActivity.this, notApplicableDiscountArrayList);
                        notApplicableDiscountsAdapter.notifyDataSetChanged();
                        notApplicableListview.setAdapter(notApplicableDiscountsAdapter);

                        if (notApplicableDiscountsAdapter != null) {
                            availableTextView.setVisibility(View.VISIBLE);
                            int totalHeight = 0;
                            for (int i = 0; i < notApplicableDiscountsAdapter.getCount(); i++) {
                                View listItem = notApplicableDiscountsAdapter.getView(i, null, notApplicableListview);
                                listItem.measure(0, 0);
                                totalHeight += listItem.getMeasuredHeight();
                            }
                            ViewGroup.LayoutParams params = notApplicableListview.getLayoutParams();
                            params.height = totalHeight + (notApplicableListview.getDividerHeight() * (notApplicableDiscountsAdapter.getCount() - 1));
                            notApplicableListview.setLayoutParams(params);
                            notApplicableListview.requestLayout();
                            notApplicableListview.setAdapter(notApplicableDiscountsAdapter);
                            notApplicableDiscountsAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        discountListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                discount1 = discountArrayList.get(i);
                int price = 0;
                int percentage = 0;
                int maximumBillAmount = 0;
                if (discount1.getTypeOfDiscount().equals("Price")) {
                    if (!discount1.getDiscountPrice().equals("") && discount1.getDiscountPrice() != null) {
                        price = Integer.parseInt(discount1.getDiscountPrice());
                    } else {
                        price = 0;
                    }
                } else {
                    if (!discount1.getDiscountPercentageValue().equals("") && discount1.getDiscountPercentageValue() != null) {
                        percentage = Integer.parseInt(discount1.getDiscountPercentageValue());
                    } else {
                        percentage = 0;
                    }
                }
                if (!discount1.getMinmumBillAmount().equals("") && discount1.getMinmumBillAmount() != null) {
                    maximumBillAmount = Integer.parseInt(discount1.getMinmumBillAmount());
                } else {
                    maximumBillAmount = 0;
                }
                if (discount1.getTypeOfDiscount().equals("Price")) {
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra("pinCodeIntent", pinCodeIntent);
                    intent.putExtra("StoreName", getStoreName);
                    intent.putExtra("StoreId", getStoreId);
                    intent.putExtra("categoryName", getCategoryName);
                    intent.putExtra("categoryId", getCategoryId);
                    intent.putExtra("pinCode", getPincode);
                    intent.putExtra("addressPref", getAddressProof);
                    intent.putExtra("FullAddress", getFullAddressFromMap);
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("shippingAddress", getShippingAddress);
                    intent.putExtra("customerName", getcustomerName);
                    intent.putExtra("customerMobilenumber", getMobileNumber);
                    intent.putExtra("customerPinCode", getCustomerPincode);
                    intent.putExtra("DiscountName", discount1.getDiscountName());
                    intent.putExtra("Typeofdiscount", "Price");
                    intent.putExtra("DiscountPrice", price);
                    intent.putExtra("DiscountMaxAmount", maximumBillAmount);
                    intent.putExtra("BillDiscountMaxAmount", 0);
                    intent.putExtra("Preference", "2");
                    intent.putExtra("orderNumber", "0");
                    intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                    intent.putExtra("dicountGivenBy", discount1.getDiscountGivenBy());
                    startActivity(intent);
                } else if (discount1.getTypeOfDiscount().equals("Percent")) {

                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra("pinCodeIntent", pinCodeIntent);
                    intent.putExtra("StoreName", getStoreName);
                    intent.putExtra("StoreId", getStoreId);
                    intent.putExtra("categoryName", getCategoryName);
                    intent.putExtra("categoryId", getCategoryId);
                    intent.putExtra("pinCode", getPincode);
                    intent.putExtra("addressPref", getAddressProof);
                    intent.putExtra("FullAddress", getFullAddressFromMap);
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("shippingAddress", getShippingAddress);
                    intent.putExtra("customerName", getcustomerName);
                    intent.putExtra("customerMobilenumber", getMobileNumber);
                    intent.putExtra("customerPinCode", getCustomerPincode);
                    intent.putExtra("DiscountName", discount1.getDiscountName());
                    intent.putExtra("Typeofdiscount", "Percent");
                    intent.putExtra("DiscountPrice", percentage);
                    intent.putExtra("DiscountMaxAmount", maximumBillAmount);
                    intent.putExtra("BillDiscountMaxAmount", String.valueOf(discount1.getMaxAmountForDiscount()));
                    intent.putExtra("Preference", "3");
                    intent.putExtra("orderNumber", "0");
                    intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                    intent.putExtra("dicountGivenBy", discount1.getDiscountGivenBy());
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DiscountActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void discountPriceFunction(Discount discount) {
        try {
            if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                    ((Number) NumberFormat.getInstance().parse(discount.getMinmumBillAmount())).intValue()) {
                if (discount.getUsage().equals("One Time Discount")) {
                    if (discountNameArrayList == null || discountNameArrayList.size() == 0) {
                        discountArrayList.add(discount);
                    } else {
                        int count = 0;
                        for (int i = 0; i < discountNameArrayList.size(); i++) {
                            if (discountNameArrayList.get(i).equals(discount.getDiscountName())) {
                                count = count + 1;
                                break;
                            }
                        }
                        if (count == 0) {
                            discountArrayList.add(discount);
                        }
                    }
                } else {
                    discountArrayList.add(discount);
                }
            } else {
                if (!discount.getUsage().equals("One Time Discount")) {
                    notApplicableDiscountArrayList.add(discount);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void discountPercentFunction(Discount discount) {

        System.out.println("DiscountPercent" + discount + discount.getUsage());
        try {
            if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                    ((Number) NumberFormat.getInstance().parse(discount.getMinmumBillAmount())).intValue()) {

                if (discount.getUsage().equals("One Time Discount")) {
                    if (discountNameArrayList == null || discountNameArrayList.size() == 0) {
                        discountArrayList.add(discount);
                    } else {

                        int count = 0;
                        for (int i = 0; i < discountNameArrayList.size(); i++) {
                            if (discount.getDiscountName().equalsIgnoreCase(discountNameArrayList.get(i))) {
                                count = count + 1;
                                break;
                            }
                        }
                        if (count == 0) {
                            discountArrayList.add(discount);
                        }
                    }
                } else {
                    discountArrayList.add(discount);
                }
            } else {
                notApplicableDiscountArrayList.add(discount);
            }
        } catch (ParseException e) {
            e.printStackTrace();
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
         if (!((Activity) DiscountActivity.this).isFinishing()) {
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
