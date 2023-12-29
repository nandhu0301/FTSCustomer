package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.make.dots.dotsindicator.DotsIndicator;
import com.smiligenceTestenv.techUser.Adapter.ViewCartAdapter;
import com.smiligenceTestenv.techUser.Adapter.ViewPageAdapter;
import com.smiligenceTestenv.techUser.Adapter.WishListAdapter;
import com.smiligenceTestenv.techUser.bean.AdvertisementDetails;
import com.smiligenceTestenv.techUser.bean.CustomerDetails;
import com.smiligenceTestenv.techUser.bean.DeliveryFareDetails;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.OrderDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.Constant;
import com.smiligenceTestenv.techUser.common.DateUtils;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;
import com.smiligenceTestenv.techUser.common.Utils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceTestenv.techUser.common.Constant.ADVERTISEMENT_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.Available;
import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.DELIVERY_FARE_DETAILS;
import static com.smiligenceTestenv.techUser.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.VIEW_CART_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.WISHLIST_FIREBASE_TABLE;

public class ViewCartActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    ListView viewCartListView;
    DatabaseReference viewcartRef, itemDataRef, userCurrentLocationDetails, storeTimingDataRef, metrozStoteTimingDataRef, deliveryFareRef, Orderreference;
    boolean checkIntent = false;
    Chip chip;
    SweetAlertDialog errorDialog;
    double getStoreLatitude = 0.0, getStoreLongtitude = 0.0, roundOff = 0.0;
    int resultKiloMeterRoundOff = 0, totaldeliveryFee = 0, tipsAmountInt = 0, temp = 0, perKmDistanceFee, finalBillAmount, finalBillAmountWithShippingFee;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<>();
    TextView storeNameText, finalBillTextView, addmoreText;
    Button addAddressButton;
    ItemDetails itemDetails;
    ArrayList<ItemDetails> refinedItemList = new ArrayList<>();
    ArrayList<ItemDetails> refinedItemList1 = new ArrayList<>();
    WishListAdapter itemsAdapter;
    ImageView back_button, clearCart;
    boolean check = true;
    FirebaseAuth mAuth;
    SweetAlertDialog pDialog;
    OrderDetails orderDetails = new OrderDetails();
    long maxid = 0;
    String verificationId;
    JSONObject jsonObject;
    Integer increamentId = 1;
    String resultOrderId, paymentId, paymentType, amount, storeAddress, deliveryType,
            receiptNumber, storeNameFromAdapter, storeId, storeIdTxt, categoryName, categoryId, pinCode;
    CustomerDetails userCurrentLocation;
    boolean customerCheck = true;
    double sharedPreferenceLatitude = 0.0, sharedPreferenceLongtitude = 0.0;
    String sharedPreferenceLat, sharedPreferenceLong;
    String saved_id;
    int getDistance;
    CustomerDetails sellerLoginDetails1 = new CustomerDetails();
    int storeIdFromList;
    ViewPager adapterViewFlipper;
    DatabaseReference databaseReference;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    Button okOTPTextButton;
    EditText otpEdittext;
    TextView resendOTPTimer;
    DatabaseReference advertisementref, wishListRef;
    Query advertisementBannerQuery;
    AdvertisementDetails advertisementDetails;
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList = new ArrayList<>();
    CardView advCardView;
    TextView shippingfareTextView;
    int deliveryFee;
    ImageView info;
    TextView alertTextView;
    EditText phoneNumberEditText;
    DatabaseReference customerDetailsDataRef;
    ItemDetails itemDetailsNew;
    ArrayList<ItemDetails> itemDetailsListNew = new ArrayList<>();
    DotsIndicator dotsIndicator;
    ListView wishListCart;
    boolean AsyncCheck = true;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_view_cart);
        checkGPSConnection(getApplicationContext());

        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        viewCartListView = findViewById(R.id.viewcart);
        addAddressButton = findViewById(R.id.adddresButton);

        finalBillTextView = findViewById(R.id.itemTotaltxt);
        wishListCart = findViewById(R.id.wishListCart);

        back_button = findViewById(R.id.back_buttontohome);
        clearCart = findViewById(R.id.clearcart);

        adapterViewFlipper = findViewById(R.id.advflippers);
        advCardView = findViewById(R.id.adcard);
        shippingfareTextView = findViewById(R.id.shippingfare);
        alertTextView = findViewById(R.id.alertTextView);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        info = findViewById(R.id.info);
        ProductDescriptionActivity.PrefIndicator = 6;
        storeNameFromAdapter = getIntent().getStringExtra("StoreName");
        storeId = getIntent().getStringExtra("StoreId");
        categoryName = getIntent().getStringExtra("categoryName");
        categoryId = getIntent().getStringExtra("categoryId");
        pinCode = getIntent().getStringExtra("pinCode");
        mAuth = FirebaseAuth.getInstance();


        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        date = new SimpleDateFormat("HH:mm aa");


        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
        currentDateAndTime = dateFormat.format(new Date());
        currentTime = date.format(currentLocalTime);
        advertisementref = CommonMethods.fetchFirebaseDatabaseReference(ADVERTISEMENT_TABLE);
        wishListRef = CommonMethods.fetchFirebaseDatabaseReference(WISHLIST_FIREBASE_TABLE).child(saved_id);
        customerDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DETAILS_FIREBASE_TABLE);
        advertisementBannerQuery = advertisementref.orderByChild("advertisementpriority").equalTo("1");
        advertisementBannerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot adSnap : dataSnapshot.getChildren()) {
                    advertisementDetails = adSnap.getValue(AdvertisementDetails.class);
                    if (Available.equalsIgnoreCase(advertisementDetails.getAdvertisementstatus())) {
                        advertisementDetailsObjectList.add(advertisementDetails);
                    }
                }
                if (advertisementDetailsObjectList.size() > 0) {
                    advCardView.setVisibility(View.VISIBLE);
                    ViewPageAdapter viewPageAdapter = new ViewPageAdapter(ViewCartActivity.this, advertisementDetailsObjectList);
                    adapterViewFlipper.setAdapter(viewPageAdapter);
                    dotsIndicator.setViewPager(adapterViewFlipper);
                } else {
                    advCardView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Orderreference = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        userCurrentLocationDetails = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails").child(String.valueOf(saved_id));
        viewcartRef = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(saved_id);
        storeTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance");
        metrozStoteTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference("MetrozstoreTiming");
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);


        //loadFunction();
        onStart();


        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemDetailList != null && !itemDetailList.isEmpty()) {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewCartActivity.this);
                    bottomSheetDialog.setContentView(R.layout.clearcart_confirmation_dialog_viewcart);
                    Button clearCart = bottomSheetDialog.findViewById(R.id.clear_cart_buttondialog);
                    Button cancel = bottomSheetDialog.findViewById(R.id.cancel_buttondialog);

                    bottomSheetDialog.show();
                    bottomSheetDialog.setCancelable(false);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                        }
                    });


                    clearCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!((Activity) ViewCartActivity.this).isFinishing()) {
                                viewcartRef.removeValue();
                                Intent intent = new Intent(ViewCartActivity.this, ViewCartActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });

                } else {

                    if (!((Activity) ViewCartActivity.this).isFinishing()) {

                       SweetAlertDialog sweetAlertDialog;
                       sweetAlertDialog=new SweetAlertDialog(ViewCartActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Items Available in Cart to Clear")
                                .setContentText("");

                        sweetAlertDialog.show();
                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(ViewCartActivity.this, R.color.colorPrimary));

                    }

                }
            }
        });


        wishListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    refinedItemList1.clear();
                    for (DataSnapshot wishSnap : dataSnapshot.getChildren()) {
                        ItemDetails itemDetails = wishSnap.getValue(ItemDetails.class);

                        itemDataRef.child(String.valueOf(itemDetails.getItemId())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getChildrenCount() > 0) {

                                    ItemDetails itemDetails2 = dataSnapshot.getValue(ItemDetails.class);

                                    if (Available.equalsIgnoreCase(itemDetails2.getItemStatus())) {
                                        refinedItemList1.add(itemDetails);


                                        itemsAdapter = new WishListAdapter(ViewCartActivity.this, refinedItemList1);
                                        if (itemsAdapter != null && itemsAdapter.getCount() > 0 && refinedItemList1.size() == itemsAdapter.getCount()) {

                                            LinearLayout textLayout = findViewById(R.id.textLayout);
                                            textLayout.setVisibility(View.VISIBLE);
                                            int totalHeight = 0;

                                            for (int i = 0; i < itemsAdapter.getCount(); i++) {
                                                View listItem = itemsAdapter.getView(i, null, wishListCart);
                                                listItem.measure(0, 0);
                                                totalHeight += listItem.getMeasuredHeight();
                                            }

                                            ViewGroup.LayoutParams params = wishListCart.getLayoutParams();
                                            params.height = totalHeight + (wishListCart.getDividerHeight() * (itemsAdapter.getCount() - 1));
                                            wishListCart.setLayoutParams(params);
                                            wishListCart.setAdapter(itemsAdapter);
                                            itemsAdapter.notifyDataSetChanged();
                                            wishListCart.requestLayout();
                                        }
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCartActivity.this, HomePageActivity.class);
                intent.putExtra("StoreName", storeNameFromAdapter);
                intent.putExtra("StoreId", storeId);
                intent.putExtra("categoryName", categoryName);
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("storeLatitude", getStoreLatitude);
                intent.putExtra("storeLongitude", getStoreLongtitude);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available")) {
                    if (itemDetailList.size() == 0) {
                        SweetAlertDialog sweetAlertDialog ;
                        sweetAlertDialog= new SweetAlertDialog(ViewCartActivity.this, SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setCancelable(false);
                        sweetAlertDialog.setTitleText("No items available in the cart");
                        sweetAlertDialog.show();
                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(ViewCartActivity.this, R.color.colorPrimary));
                    } else {
                        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID);

                        if (android_id.equals(saved_id)) {

                            final BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(ViewCartActivity.this);
                            bottomSheetDialog1.setContentView(R.layout.phone_number_bottotm_sheet_dialog);
                            int width = (int) (ViewCartActivity.this.getResources().getDisplayMetrics().widthPixels * 0.6);
                            int height = (int) (ViewCartActivity.this.getResources().getDisplayMetrics().heightPixels * 0.4);
                            bottomSheetDialog1.getWindow().setLayout(width, height);
                            bottomSheetDialog1.show();
                            bottomSheetDialog1.setCancelable(true);
                            Button phoneNumberOkButton = bottomSheetDialog1.findViewById(R.id.phoneButton);
                            phoneNumberEditText = bottomSheetDialog1.findViewById(R.id.phoneNumberEditText);

                            phoneNumberOkButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (phoneNumberEditText.getText().toString().equals("")) {
                                        phoneNumberEditText.setError("Required");
                                        return;
                                    } else if (phoneNumberEditText.getText().toString().trim() != null && !TextUtils.validatePhoneNumber(phoneNumberEditText.getText().toString().trim())) {
                                        phoneNumberEditText.setError("Enter Valid Phone Number");
                                        return;
                                    } else {
                                        bottomSheetDialog1.dismiss();
                                        sendVerificationCode("+91" + phoneNumberEditText.getText().toString());
                                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewCartActivity.this);
                                        bottomSheetDialog.setContentView(R.layout.otp_bottom_sheet_layout);
                                        int width = (int) (ViewCartActivity.this.getResources().getDisplayMetrics().widthPixels * 0.6);
                                        int height = (int) (ViewCartActivity.this.getResources().getDisplayMetrics().heightPixels * 0.4);
                                        bottomSheetDialog.getWindow().setLayout(width, height);
                                        if (!((Activity) ViewCartActivity.this).isFinishing()) {
                                            bottomSheetDialog.show();
                                        }

                                        bottomSheetDialog.setCancelable(true);
                                        okOTPTextButton = bottomSheetDialog.findViewById(R.id.otpButton);
                                        otpEdittext = bottomSheetDialog.findViewById(R.id.otpEditext);
                                        resendOTPTimer = bottomSheetDialog.findViewById(R.id.resendOTPTimer);


                                        okOTPTextButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String code = otpEdittext.getText().toString().trim();

                                                if (code.isEmpty() || code.length() < 6) {
                                                    otpEdittext.setError("Enter valid code...");
                                                    otpEdittext.requestFocus();
                                                    return;
                                                }

                                                verifyCode(code);
                                            }
                                        });

                                    }
                                }
                            });
                        } else {
                            Intent intent = new Intent(ViewCartActivity.this, AddAddressActivity.class);
                            intent.putExtra("finalBillAmount", String.valueOf(finalBillAmount));
                            intent.putExtra("deliveryType", deliveryType);
                            intent.putExtra("tips", String.valueOf(temp));
                            intent.putExtra("finalBillTip", String.valueOf(tipsAmountInt));
                            intent.putExtra("storeAddress", storeAddress);
                            intent.putExtra("storeLatitude", getStoreLatitude);
                            intent.putExtra("storeLongitude", getStoreLongtitude);
                            intent.putExtra("StoreId", storeId);
                            intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                    }
                } else {
                    new SweetAlertDialog(ViewCartActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No Network Connection")
                            .show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewCartActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    protected void onStart() {
        super.onStart();

        Orderreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        viewcartRef = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).
                child(saved_id);

        viewcartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (check) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        itemDetailList.clear();
                        finalBillAmount = 0;
                        tipsAmountInt = 0;
                        for (DataSnapshot viewCartsnap : dataSnapshot.getChildren()) {
                            itemDetails = viewCartsnap.getValue(ItemDetails.class);
                            if (itemDetails.getItemStatus().equalsIgnoreCase(Available)) {
                                finalBillAmount = finalBillAmount + (itemDetails.getTotalItemQtyPrice());
                                tipsAmountInt = finalBillAmount;
                                itemDetailList.add(itemDetails);
                            }
                        }
                        orderDetails.setItemDetailList(itemDetailList);
                        orderDetails.setStoreName(itemDetails.getStoreName());
                        orderDetails.setStorePincode(itemDetails.getStorePincode());
                        orderDetails.setStoreAddress(itemDetails.getStoreAdress());

                        orderDetails.setTotalAmount(Integer.parseInt(String.valueOf(finalBillAmount)));
                        if (itemDetailList != null && itemDetailList.size() > 0) {
                            storeIdTxt = itemDetailList.get(0).getSellerId();
                        }

                        tipsAmountInt = finalBillAmount + temp;
                    } else {
                        finalBillAmount = 0;
                        finalBillTextView.setText("₹ " + 0);
                        shippingfareTextView.setText("₹ " + 0);
                    }
                }

                ViewCartAdapter viewCartAdapter = new ViewCartAdapter(ViewCartActivity.this, itemDetailList);
                viewCartListView.setAdapter(viewCartAdapter);
                viewCartAdapter.notifyDataSetChanged();

                if (viewCartAdapter != null) {
                    int totalHeight = 0;

                    for (int i = 0; i < viewCartAdapter.getCount(); i++) {
                        View listItem = viewCartAdapter.getView(i, null, viewCartListView);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = viewCartListView.getLayoutParams();
                    params.height = totalHeight + (viewCartListView.getDividerHeight() * (viewCartAdapter.getCount() - 1));
                    viewCartListView.setLayoutParams(params);
                    viewCartListView.requestLayout();
                    viewCartListView.setAdapter(viewCartAdapter);
                    viewCartAdapter.notifyDataSetChanged();
                    finalBillTextView.setText(" ₹" + (finalBillAmount));


                    deliveryFareRef = CommonMethods.fetchFirebaseDatabaseReference(DELIVERY_FARE_DETAILS);
                    deliveryFareRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                final DeliveryFareDetails deliveryFareDetails = dataSnapshot.getValue(DeliveryFareDetails.class);
                                if (deliveryFareDetails.getCartValue() >= finalBillAmount) {

                                    finalBillAmountWithShippingFee = finalBillAmount + deliveryFareDetails.getDeliveryFare();
                                    shippingfareTextView.setText("₹" + deliveryFareDetails.getDeliveryFare());
                                    finalBillTextView.setText("₹ " + (finalBillAmountWithShippingFee));
                                    deliveryFee = deliveryFareDetails.getDeliveryFare();
                                    int val = deliveryFareDetails.getCartValue() - finalBillAmount + 1;
                                    alertTextView.setText("Please, Add Products worth ₹" + val + " more to Avail Free Shipping");
                                    alertTextView.setTextSize(14);
                                } else if (deliveryFareDetails.getCartValue() < finalBillAmount) {
                                    finalBillAmountWithShippingFee = finalBillAmount;
                                    shippingfareTextView.setText("Free");
                                    deliveryFee = 0;
                                    alertTextView.setText("");
                                    alertTextView.setTextSize(0);
                                    finalBillTextView.setText("₹ " + (finalBillAmountWithShippingFee));
                                }
                                if (itemDetailList.isEmpty() || itemDetailList.size() == 0) {
                                    finalBillAmount = 0;
                                    finalBillTextView.setText("₹ " + 0);
                                    shippingfareTextView.setText("₹ " + 0);
                                    alertTextView.setText("");
                                    alertTextView.setTextSize(0);
                                }

                                info.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ViewCartActivity.this);
                                        LayoutInflater inflater = ((Activity) ViewCartActivity.this).getLayoutInflater();
                                        final View dialogView = inflater.inflate(R.layout.shipping_info_layout, null);
                                        Button close = dialogView.findViewById(R.id.close);
                                        TextView textInfo = dialogView.findViewById(R.id.textInfo);
                                        dialogBuilder.setView(dialogView);
                                        final AlertDialog b = dialogBuilder.create();
                                        b.show();
                                        close.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                b.cancel();
                                            }
                                        });

                                        textInfo.setText("Orders Above ₹" + deliveryFareDetails.getCartValue() + " will get Free Shipping and ₹" +
                                                deliveryFareDetails.getDeliveryFare() + " for all Other Orders");

                                    }
                                });


                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);


    }


    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Query findPhoneNumberQuery = customerDetailsDataRef.orderByChild("customerPhoneNumber").equalTo(phoneNumberEditText.getText().toString());

                            findPhoneNumberQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        for (DataSnapshot custSnap : dataSnapshot.getChildren()) {
                                            CustomerDetails customerDetails = custSnap.getValue(CustomerDetails.class);
                                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.clear();
                                            editor.commit();
                                            SharedPreferences sharedPreferences1 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                            editor1.putString("customerId", String.valueOf(customerDetails.getCustomerId()));
                                            editor1.commit();
                                            final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                            saved_id = loginSharedPreferences.getString("customerId", "");
                                            final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                                    Settings.Secure.ANDROID_ID);
                                            Query guestLoginDetails = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(android_id);

                                            guestLoginDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getChildrenCount() > 0) {
                                                        for (DataSnapshot deliveryBoySnap : dataSnapshot.getChildren()) {
                                                            itemDetailsNew = deliveryBoySnap.getValue(ItemDetails.class);
                                                            databaseReference = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);
                                                            databaseReference.child(String.valueOf(itemDetailsNew.getItemId())).setValue(itemDetailsNew);
                                                        }
                                                        Intent intent = new Intent(ViewCartActivity.this, AddAddressActivity.class);
                                                        intent.putExtra("finalBillAmount", String.valueOf(finalBillAmount));
                                                        intent.putExtra("deliveryType", deliveryType);
                                                        intent.putExtra("tips", String.valueOf(temp));
                                                        intent.putExtra("finalBillTip", String.valueOf(tipsAmountInt));
                                                        intent.putExtra("storeAddress", storeAddress);
                                                        intent.putExtra("storeLatitude", getStoreLatitude);
                                                        intent.putExtra("storeLongitude", getStoreLongtitude);
                                                        intent.putExtra("StoreId", storeId);
                                                        intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }
                                    } else {
                                        sellerLoginDetails1.setCustomerPhoneNumber(phoneNumberEditText.getText().toString());
                                        customerDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DETAILS_FIREBASE_TABLE);
                                        onTransaction(customerDetailsDataRef);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            if (!((Activity) ViewCartActivity.this).isFinishing()) {
                                errorDialog = new SweetAlertDialog(ViewCartActivity.this, SweetAlertDialog.ERROR_TYPE);
                                errorDialog.setCancelable(false);
                                errorDialog
                                        .setContentText("Invalid OTP").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        errorDialog.dismiss();
                                    }

                                }).show();
                            }
                        }
                    }
                });
    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Verification Code is wrong, try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void sendVerificationCode(String number) {


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

        Toast.makeText(this, "OTP Sent Successfully"
                , Toast.LENGTH_SHORT).show();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otpEdittext.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ViewCartActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void onTransaction(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                increamentId = Math.toIntExact(mutableData.getChildrenCount());
                if (increamentId == null) {
                    increamentId = 1;
                    return Transaction.success(mutableData);
                } else {
                    increamentId = increamentId + 1;
                    return Transaction.success(mutableData);

                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                sellerLoginDetails1.setSignIn(true);
                sellerLoginDetails1.setOtpVerified(true);
                sellerLoginDetails1.setEmailVerified(false);
                String createdDate = DateUtils.fetchCurrentDateAndTime();
                sellerLoginDetails1.setCreationDate(createdDate);
                if (increamentId == 0) {
                    sellerLoginDetails1.setCustomerId(String.valueOf(1));
                    customerDetailsDataRef.child(String.valueOf(1)).setValue(sellerLoginDetails1);

                } else {
                    sellerLoginDetails1.setCustomerId(String.valueOf(increamentId));
                    customerDetailsDataRef.child(String.valueOf(increamentId)).setValue(sellerLoginDetails1);
                }


                SharedPreferences sharedPreferences1 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                if (increamentId == 0) {
                    editor1.putString("customerId", String.valueOf(1));
                } else {
                    editor1.putString("customerId", String.valueOf(increamentId));
                }

                editor1.commit();
                final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                saved_id = loginSharedPreferences.getString("customerId", "");
                final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Query guestLoginDetails = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(android_id);

                guestLoginDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            for (DataSnapshot deliveryBoySnap : dataSnapshot.getChildren()) {
                                itemDetailsNew = deliveryBoySnap.getValue(ItemDetails.class);
                                databaseReference = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);
                                databaseReference.child(String.valueOf(itemDetailsNew.getItemId())).setValue(itemDetailsNew);
                            }
                        }
                        Intent intent = getIntent();
                        startActivity(intent);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
        if (!((Activity) ViewCartActivity.this).isFinishing()) {
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