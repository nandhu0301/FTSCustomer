package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.smiligenceTestenv.techUser.Adapter.PaymentAdapter;
import com.smiligenceTestenv.techUser.bean.CustomerDetails;
import com.smiligenceTestenv.techUser.bean.DeliveryFareDetails;
import com.smiligenceTestenv.techUser.bean.Discount;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.OrderDetails;
import com.smiligenceTestenv.techUser.bean.PincodeDetails;
import com.smiligenceTestenv.techUser.bean.StoreDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.Constant;
import com.smiligenceTestenv.techUser.common.DateUtils;
import com.smiligenceTestenv.techUser.common.FcmNotificationsSender;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;
import com.smiligenceTestenv.techUser.common.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceTestenv.techUser.common.Constant.Available;
import static com.smiligenceTestenv.techUser.common.Constant.BOOLEAN_FALSE;
import static com.smiligenceTestenv.techUser.common.Constant.BOOLEAN_TRUE;
import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_DISCOUNT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.DATE_FORMAT;
import static com.smiligenceTestenv.techUser.common.Constant.DATE_FORMAT_YYYYMD;
import static com.smiligenceTestenv.techUser.common.Constant.DATE_TIME_FORMAT;
import static com.smiligenceTestenv.techUser.common.Constant.DATE_TIME_FORMAT_NEW;
import static com.smiligenceTestenv.techUser.common.Constant.DELIVERY_FARE_DETAILS;
import static com.smiligenceTestenv.techUser.common.Constant.DISCOUNT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.OutsideTamilnadu;
import static com.smiligenceTestenv.techUser.common.Constant.RAZORPAY_KEY_ID;
import static com.smiligenceTestenv.techUser.common.Constant.RAZORPAY_SECRAT_KEY;
import static com.smiligenceTestenv.techUser.common.Constant.RESTRICTED_PINCODE;
import static com.smiligenceTestenv.techUser.common.Constant.SELLER_DETAILS;
import static com.smiligenceTestenv.techUser.common.Constant.TamilNadu;
import static com.smiligenceTestenv.techUser.common.Constant.VIEW_CART_FIREBASE_TABLE;

public class PaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener, NetworkStateReceiver.NetworkStateReceiverListener {

    DatabaseReference deliveryFareRef, oneTimeDiscountRef, viewCartRef, oneTimeDisDataRef, userCurrentLocationDetails, Orderreference;
    OrderDetails orderDetails = new OrderDetails();
    ItemDetails itemDetails = new ItemDetails();
    ArrayList<ItemDetails> giftWrapItemList = new ArrayList<ItemDetails>();
    Animation animation;
    int finalBillAmount, tipsAmountInt, temp;
    ListView purchaseListView;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails>();
    TextView totalPurchaseAmount;
    RelativeLayout purchaseLayout;
    String pattern = "hh:mm";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime;
    String metrozStopTime;
    String amount;
    boolean checkAsync = true;
    Payment payment;
    String paymentType;
    CustomerDetails userCurrentLocation;
    String storeNameFromAdapter, storeId, categoryName, categoryId, pinCode;
    int getDistance;
    RazorpayClient razorpay;
    JSONObject jsonObject;
    long maxid = 0;
    String resultOrderId, receiptNumber;
    boolean checkIntent = false;
    TextView addCurrentAddressEditText;
    String deliverytType, tipAmount, finalBill_tip, finalBill, instructionString, storeAddress, getFullAdreesFromMap;

    int resultKiloMeterRoundOff = 0;
    ImageView backToScreen;
    String customerID, addressPref, shippingAddress, customerName, customerMobilenumber, customerPinCode;
    TextView viewAddress;
    TextView totalItemValue, toPayValue;
    TextView giftText, giftAmount;
    List<String> name;
    List<Integer> id;
    TextView offerName, viewOffers;
    String testPay = "";
    int getDiscountAmount = 0, getMaximumBillAmount = 0;
    String ifPercentMaxAmountForDiscount;
    String discountName, typeOfDiscount;
    TextView deductionAmountTextView;
    String discountAppliedOrNot = "No";
    int discountAmountCalculation = 0;
    String getDiscountGivenBy, discountUsage;
    String getPreference, oneTimeOrderNumber;
    int totalFeeFordeliveryBoy;
    Integer increamentId;
    SweetAlertDialog pDialog;
    String deliveryFee;
    String TIME_SERVER;
    String financialYear;

    PincodeDetails pincodeDetails;
    DatabaseReference pincodeRef;
    ArrayList<PincodeDetails> pincodeDetailsArrayList = new ArrayList<>();
    public static boolean isValidPinCode = BOOLEAN_FALSE;
    public static String pinCodeIntent;
    String saved_id;
    EditText couponEditText;
    TextView applyCouponTextview;
    DatabaseReference discountDataRef;
    ArrayList<String> discountNameArrayList = new ArrayList<>();
    Discount discount;
    Discount Customerdiscount;
    TextView shippingAddressInfo;
    String insideOrOutsideTamilNaduTag;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;
    DatabaseReference storeDataRef;
    StoreDetails storeDetails;
    int taxPrice;
    float cgst_sgst_calculation = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_payment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");

        purchaseListView = findViewById(R.id.purchaselist);
        viewAddress = findViewById(R.id.addresstext);
        totalPurchaseAmount = findViewById(R.id.totalpurchaseamountText);
        purchaseLayout = findViewById(R.id.placeorderlayout);
        totalItemValue = findViewById(R.id.itemtotalvalue);
        offerName = findViewById(R.id.discountTextview);
        viewOffers = findViewById(R.id.viewOffers);
        couponEditText = findViewById(R.id.couponCodeEdittext);
        applyCouponTextview = findViewById(R.id.applyCouponCode);
        shippingAddressInfo = findViewById(R.id.giftText);

        toPayValue = findViewById(R.id.topayvalue);
        backToScreen = findViewById(R.id.backtoScreen);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        //  animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        giftText = findViewById(R.id.giftText);
        giftAmount = findViewById(R.id.giftTextAmount);
        deductionAmountTextView = findViewById(R.id.deductionAmount);

        pincodeRef = CommonMethods.fetchFirebaseDatabaseReference(RESTRICTED_PINCODE);
        oneTimeDiscountRef = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DISCOUNT_DETAILS_FIREBASE_TABLE);
        pinCodeIntent = getIntent().getStringExtra("pinCodeIntent");

        storeNameFromAdapter = getIntent().getStringExtra("StoreName");
        storeId = getIntent().getStringExtra("StoreId");
        categoryName = getIntent().getStringExtra("categoryName");
        categoryId = getIntent().getStringExtra("categoryId");
        pinCode = getIntent().getStringExtra("pinCode");
        addressPref = getIntent().getStringExtra("addressPref");
        shippingAddress = getIntent().getStringExtra("shippingAddress");
        customerName = getIntent().getStringExtra("customerName");
        customerMobilenumber = getIntent().getStringExtra("customerMobilenumber");


        customerPinCode = getIntent().getStringExtra("customerPinCode");
        deliverytType = getIntent().getStringExtra("deliveryType");
        tipAmount = getIntent().getStringExtra("tips");
        finalBill_tip = getIntent().getStringExtra("finalBillTip");
        finalBill = getIntent().getStringExtra("finalBillAmount");
        instructionString = getIntent().getStringExtra("instructionString");
        storeAddress = getIntent().getStringExtra("storeAddress");
        getFullAdreesFromMap = getIntent().getStringExtra("FullAddress");
        getDiscountAmount = getIntent().getIntExtra("DiscountPrice", 0);
        getMaximumBillAmount = getIntent().getIntExtra("DiscountMaxAmount", 0);
        discountName = getIntent().getStringExtra("DiscountName");
        typeOfDiscount = getIntent().getStringExtra("Typeofdiscount");

        ifPercentMaxAmountForDiscount = getIntent().getStringExtra("BillDiscountMaxAmount");
        customerID = saved_id;
        getPreference = getIntent().getStringExtra("Preference");
        oneTimeOrderNumber = getIntent().getStringExtra("orderNumber");
        deliveryFee = getIntent().getStringExtra("deliveryFee");
        getDiscountGivenBy = getIntent().getStringExtra("dicountGivenBy");
        discountUsage = getIntent().getStringExtra("discountUsage");

        giftAmount.setText("₹ " + deliveryFee);

        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();

        date = new SimpleDateFormat("HH:mm aa");
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        currentDateAndTime = dateFormat.format(new Date());
        currentTime = date.format(currentLocalTime);
        discountDataRef = CommonMethods.fetchFirebaseDatabaseReference(DISCOUNT_DETAILS_FIREBASE_TABLE);
        userCurrentLocationDetails = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails").child(String.valueOf(saved_id));
        Orderreference = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        storeDataRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS);


        viewCartRef = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).
                child(saved_id);

        deliveryFareRef = CommonMethods.fetchFirebaseDatabaseReference(DELIVERY_FARE_DETAILS);

        autoLoadFunction();
        loadFunction();
        couponCodeFunction();
        deliveryFareFunction();
        viewCartFunction();


        storeDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    storeDetails = dataSnapshot.getValue(StoreDetails.class);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
        viewOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewOffers.getText().toString().equals("View Offers")) {
                    Intent intent = new Intent(PaymentActivity.this, DiscountActivity.class);
                    intent.putExtra("pinCodeIntent", pinCodeIntent);
                    intent.putExtra("StoreName", storeNameFromAdapter);
                    intent.putExtra("StoreId", storeId);
                    intent.putExtra("categoryName", categoryName);
                    intent.putExtra("categoryId", categoryId);
                    intent.putExtra("pinCode", pinCode);
                    intent.putExtra("addressPref", addressPref);
                    intent.putExtra("FullAddress", getFullAdreesFromMap);
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("shippingAddress", shippingAddress);
                    intent.putExtra("customerName", customerName);
                    intent.putExtra("customerMobilenumber", customerMobilenumber);
                    intent.putExtra("customerPinCode", customerPinCode);
                    intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (viewOffers.getText().toString().equals("Remove")) {

                    typeOfDiscount = null;
                    getDiscountAmount = 0;
                    getMaximumBillAmount = 0;
                    deliveryFee = String.valueOf(0);
                    discountName = "-";
                    getPreference = "";
                    offerName.setText("Select Offers");
                    viewOffers.setText("View Offers");
                    discountAppliedOrNot = "No";
                    couponEditText.setText("");
                    deliveryFareFunction();

                    // giftAmount.setText("₹ " + deliveryFee);
                    autoLoadFunction();
                    couponCodeFunction();

                }
            }
        });

        backToScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this, ViewCartActivity.class);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.putExtra("shippingAddress", shippingAddress);
                intent.putExtra("customerName", customerName);
                intent.putExtra("customerMobilenumber", customerMobilenumber);
                intent.putExtra("customerPinCode", customerPinCode);
                intent.putExtra("deliveryFee", String.valueOf(deliveryFee));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        purchaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                purchaseLayout.setVisibility(View.INVISIBLE);
                if (!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available")) {
                    if (itemDetailList.size() == 0) {

                        Toast.makeText(PaymentActivity.this, "k", Toast.LENGTH_SHORT).show();
                        new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("No items available in the cart.")
                        ;
                        //  sweetAlertDialog.show();
                        //  Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        // btn.setBackgroundColor(ContextCompat.getColor(PaymentActivity.this, R.color.colorPrimary));
                        return;
                    } else {
                        if (pincodeDetailsArrayList != null && pinCodeIntent != null) {
                            Iterator itemIterator = pincodeDetailsArrayList.iterator();

                            while (itemIterator.hasNext()) {

                                PincodeDetails pincodeDetails = (PincodeDetails) itemIterator.next();
                                if (pinCodeIntent.equalsIgnoreCase(pincodeDetails.getPinCode())) {

                                    isValidPinCode = BOOLEAN_TRUE;


                                    break;
                                } else {
                                    isValidPinCode = BOOLEAN_FALSE;
                                }
                            }
                        }
                        if (isValidPinCode) {
                            purchaseLayout.setVisibility(View.VISIBLE);

                            new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText("Delivery is restricted in the chosen Pincode Location due to Logistics concern. Kindly select another suitable delivery location")
                                    .show();
                            return;
                        } else {
                            resultChecking();
                        }

                    }
                } else {
                    new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No Network Connection")
                            .show();
                    purchaseLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        Orderreference.orderByChild("customerId").equalTo(saved_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                discountNameArrayList.clear();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot orderSnap : dataSnapshot.getChildren()) {
                        OrderDetails orderDetails = orderSnap.getValue(OrderDetails.class);
                        discountNameArrayList.add(orderDetails.getDiscountName());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });

    }


    private void startpayment() throws JSONException, RazorpayException {

        razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRAT_KEY);
        JSONObject options = new JSONObject();
        options.put("amount", ((tipsAmountInt - getDiscountAmount + Integer.parseInt(deliveryFee)) * 100));
        options.put("currency", "INR");
        options.put("receipt", generateString(10));
        Order order = razorpay.Orders.create(options);
        jsonObject = new JSONObject(String.valueOf(order));
        resultOrderId = jsonObject.getString("id");
        receiptNumber = jsonObject.getString("receipt");
        checkOutFunction(resultOrderId);
    }

    private void checkOutFunction(String orderId) throws JSONException {

        final Activity activity = this;
        Checkout checkout = new Checkout();
        checkout.setKeyID(RAZORPAY_KEY_ID);
        checkout.setImage(R.mipmap.ic_launcher);
        JSONObject options = new JSONObject();
        options.put("payment_capture", true);
        options.put("order_id", orderId);
        JSONObject preFill = new JSONObject();
        preFill.put("email", "");
        preFill.put("contact", customerMobilenumber);
        options.put("prefill", preFill);

        checkout.open(activity, options);

    }

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

        pincodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot pincodeSnap : dataSnapshot.getChildren()) {
                        pincodeDetails = pincodeSnap.getValue(PincodeDetails.class);
                        if ("Available".equalsIgnoreCase(pincodeDetails.getPincodeStatus())) {
                            pincodeDetailsArrayList.add(pincodeDetails);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewCartFunction();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        pDialog = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#5C9535"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        if (checkIntent) {
            orderDetails.setPaymentId(paymentData.getPaymentId());
            try {

                razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRAT_KEY);
                payment = razorpay.Payments.fetch(paymentData.getPaymentId());
                jsonObject = new JSONObject(String.valueOf(payment));

                paymentType = jsonObject.getString("method");
                amount = jsonObject.getString("amount");


                Calendar calendar = Calendar.getInstance();
                TimeZone tz = TimeZone.getDefault();
                calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                java.util.Date currenTimeZone = new java.util.Date((long) Long.parseLong(jsonObject.getString("created_at")) * 1000);
                orderDetails.setFormattedDate(new SimpleDateFormat(DATE_FORMAT_YYYYMD).format(currenTimeZone));
                orderDetails.setOrderTime(new SimpleDateFormat(DATE_TIME_FORMAT_NEW).format(currenTimeZone));
                orderDetails.setOrderCreateDate(new SimpleDateFormat(DATE_TIME_FORMAT).format(currenTimeZone));
                orderDetails.setPaymentDate(new SimpleDateFormat(DATE_FORMAT).format(currenTimeZone));


                int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(currenTimeZone));
                int month = Integer.parseInt(new SimpleDateFormat("M").format(currenTimeZone));


                if (month <= 3) {
                    financialYear = ((year - 1) + "-" + year);
                } else {
                    financialYear = (year + "-" + (year + 1));
                }

                Log.d("Financial year", financialYear);

            } catch (RazorpayException | JSONException e) {
            }
            orderDetails.setPaymentType(paymentType);
            orderDetails.setOrderIdfromPaymentGateway(resultOrderId);
            orderDetails.setOrderStatus("Order Placed");
            orderDetails.setTotalAmount(finalBillAmount);
            if (discountAppliedOrNot.equals("Yes")) {
                orderDetails.setDiscountName(String.valueOf(discountName));
                orderDetails.setDiscountAmount(getDiscountAmount);
            } else {
                orderDetails.setDiscountName("");
                orderDetails.setDiscountAmount(0);
            }
            orderDetails.setAssignedTo("");
            orderDetails.setTipAmount(temp);
            orderDetails.setCategoryTypeId("1");
            orderDetails.setPaymentamount(tipsAmountInt - getDiscountAmount + Integer.parseInt(deliveryFee));


            orderDetails.setCustomerName(customerName);
            orderDetails.setCustomerId(saved_id);
            orderDetails.setTotalDistanceTraveled(resultKiloMeterRoundOff);
            orderDetails.setDeliveryFee(Integer.parseInt(deliveryFee));
            orderDetails.setDeliveryType(deliverytType);
            orderDetails.setStoreAddress(storeAddress);
            orderDetails.setNotificationStatus("false");
            orderDetails.setNotificationStatusForCustomer("false");
            orderDetails.setNotificationStatusForSeller("false");
            orderDetails.setInstructionsToDeliveryBoy(instructionString);
            orderDetails.setDiscountGivenBy(getDiscountGivenBy);
            orderDetails.setOrderType("Online");

            //int val = tipsAmountInt - getDiscountAmount + Integer.parseInt(deliveryFee);
            //val -> Total order amount

            Iterator itemIterator = itemDetailList.iterator();
            //taxPrice=0;
            while (itemIterator.hasNext()) {
                ItemDetails itemDetails = (ItemDetails) itemIterator.next();
                taxPrice = taxPrice + itemDetails.getTotalTaxPrice();
            }


            //taxPrice = val - (val * 100) / (100 + storeDetails.getGst());
            cgst_sgst_calculation = taxPrice / 2;

            if (!TextUtils.validTamilNaduPinCode(pinCodeIntent)) {
                insideOrOutsideTamilNaduTag = OutsideTamilnadu;
                orderDetails.setIgstValue(taxPrice);
            } else {
                insideOrOutsideTamilNaduTag = TamilNadu;
                orderDetails.setCgstValue(cgst_sgst_calculation);
                orderDetails.setSgstValue(cgst_sgst_calculation);
            }

            orderDetails.setTotalTaxValue(taxPrice);
            orderDetails.setInsideOrOutside(insideOrOutsideTamilNaduTag);
            onTransaction(Orderreference);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        testPay = "Failed";
        if (discountAppliedOrNot.equals("Yes")) {
            toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt - getDiscountAmount + Integer.parseInt(deliveryFee)));
            totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt - getDiscountAmount + Integer.parseInt(deliveryFee)));
        } else {
            toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + Integer.parseInt(deliveryFee)));
            totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + Integer.parseInt(deliveryFee)));
        }

        if (!((Activity) PaymentActivity.this).isFinishing()) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Payment cancelled").setContentText("Please try again!");
            sweetAlertDialog.show();

            Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
            btn.setBackgroundColor(ContextCompat.getColor(PaymentActivity.this, R.color.colorPrimary));

            purchaseLayout.setVisibility(View.VISIBLE);
        }
    }

    public void resultChecking() {
        if (!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available")) {

            if ((saved_id != null && !"".equals(saved_id))) {
                if (!((Activity) PaymentActivity.this).isFinishing()) {
                    Checkout.preload(getApplicationContext());
                    new backGroundClass().execute();
                }
            } else {
                Toast.makeText(PaymentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PaymentActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else {
            new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("No Network Connection")
                    .show();
        }
    }


    private class backGroundClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                startpayment();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (RazorpayException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void loadFunction() {
        userCurrentLocationDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {


                    userCurrentLocation = dataSnapshot.getValue(CustomerDetails.class);

                    if ("2".equalsIgnoreCase(addressPref)) {
                        viewAddress.setText(shippingAddress);
                        orderDetails.setShippingaddress(shippingAddress);
                        orderDetails.setFullName(customerName);
                        orderDetails.setCustomerName(customerName);
                        orderDetails.setShippingPincode(customerPinCode);
                        orderDetails.setCustomerPhoneNumber(customerMobilenumber);

                    }
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
        Intent intent = new Intent(PaymentActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public void autoLoadFunction() {
        if (typeOfDiscount != null) {
            viewOffers.setText("Remove");
            if (typeOfDiscount.equals("Price")) {

                if ((Integer.parseInt(finalBill) >= getMaximumBillAmount)) {
                    deductionAmountTextView.setText("- ₹" + String.valueOf(getDiscountAmount));
                    offerName.setText(discountName);
                    discountAppliedOrNot = "Yes";

                } else {
                    offerName.setText("Offer is not applicable");
                    discountAppliedOrNot = "No";
                    offerName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_crossline_01, 0, 0, 0);
                }
            } else if (typeOfDiscount.equals("Percent")) {

                if ((Integer.parseInt(finalBill) >= getMaximumBillAmount)) {

                    discountAmountCalculation = (getDiscountAmount * Integer.parseInt(finalBill)) / 100;

                    if (ifPercentMaxAmountForDiscount != null) {

                        if (Integer.parseInt(ifPercentMaxAmountForDiscount) < discountAmountCalculation) {

                            getDiscountAmount = Integer.parseInt(ifPercentMaxAmountForDiscount);
                            deductionAmountTextView.setText("- ₹" + String.valueOf(getDiscountAmount));
                        } else if (Integer.parseInt(ifPercentMaxAmountForDiscount) >= discountAmountCalculation) {

                            getDiscountAmount = (discountAmountCalculation);
                            deductionAmountTextView.setText("- ₹" + String.valueOf(getDiscountAmount));
                        }
                        offerName.setText(discountName);
                        discountAppliedOrNot = "Yes";
                    }
                    offerName.setText(discountName);
                    discountAppliedOrNot = "Yes";


                } else {


                    offerName.setText("Offer is not applicable");
                    discountAppliedOrNot = "No";
                    offerName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_crossline_01, 0, 0, 0);
                }
            }
        } else {


            deductionAmountTextView.setText("- ₹" + String.valueOf(getDiscountAmount));
            onStart();
        }

    }

    private void onTransaction(DatabaseReference postRef) {


        postRef.runTransaction(new Transaction.Handler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                increamentId = Math.toIntExact(mutableData.getChildrenCount());
                if (increamentId == null) {
                    orderDetails.setOrderNumberForFinancialYearCalculation(Constant.Order_format_start + financialYear + Constant.Order_format_end + "_" + "#1");

                    orderDetails.setOrderId(String.valueOf(1));
                    mutableData.child(String.valueOf(1)).setValue(orderDetails);
                    return Transaction.success(mutableData);
                } else {
                    increamentId = increamentId + 1;
                    orderDetails.setOrderNumberForFinancialYearCalculation(Constant.Order_format_start + financialYear + Constant.Order_format_end + "_" + "#" + increamentId);
                    orderDetails.setOrderId(String.valueOf(increamentId));
                    mutableData.child(String.valueOf(increamentId)).setValue(orderDetails);
                    return Transaction.success(mutableData);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                viewCartRef.removeValue();
                final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                DatabaseReference guestLoginRef = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).
                        child(android_id);
                guestLoginRef.removeValue();

                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(userCurrentLocation.getDeviceId(),
                        "Order Notification",
                        "New Order Placed", getApplicationContext(), PaymentActivity.this);
                notificationsSender.SendNotifications();
                if (!((Activity) PaymentActivity.this).isFinishing()) {
                    pDialog.dismiss();
                }
                Intent intent = new Intent(PaymentActivity.this, ViewOrderActivity.class);
                intent.putExtra("OrderidDetails", String.valueOf(increamentId));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    public static String generateString(int length) {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789!@#$%^&*".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public void couponCodeFunction() {
        if (viewOffers.getText().toString().equals("View Offers")) {
            couponEditText.setEnabled(true);
            applyCouponTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (couponEditText.getText().toString().equals("")) {
                        couponEditText.setError(Constant.REQUIRED_MSG);
                    } else {
                        discountDataRef.orderByChild("discountName").equalTo(couponEditText.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    for (DataSnapshot disocuntSnap : dataSnapshot.getChildren()) {
                                        try {
                                            discount = disocuntSnap.getValue(Discount.class);

                                            if ((new SimpleDateFormat("dd-MM-yyyy").parse(DateUtils.fetchFormatedCurrentDate()).before((new SimpleDateFormat("dd-MM-yyyy").parse(discount.getValidTillDate()))))) {
                                                if (discount.getDiscountStatus().equals("Active")) {
                                                    discountName = discount.getDiscountName();
                                                    typeOfDiscount = discount.getTypeOfDiscount();
                                                    if (discount.getTypeOfDiscount().equals("Price")) {
                                                        discountPrice(discount);

                                                        break;
                                                    } else if (discount.getTypeOfDiscount().equals("Percent")) {
                                                        discountPercent(discount);
                                                        break;
                                                    }
                                                } else {
                                                    discountExistDialog();

                                                    break;
                                                }
                                            } else if (DateUtils.fetchFormatedCurrentDate().equals(discount.getValidTillDate())) {
                                                try {
                                                    if ((sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == -1) ||
                                                            (sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == 0)) {
                                                        if (discount.getDiscountStatus().equals("Active")) {
                                                            discountName = discount.getDiscountName();

                                                            typeOfDiscount = discount.getTypeOfDiscount();
                                                            if (discount.getTypeOfDiscount().equals("Price")) {
                                                                discountPrice(discount);

                                                                break;
                                                            } else if (discount.getTypeOfDiscount().equals("Percent")) {
                                                                discountPercent(discount);

                                                                break;
                                                            }
                                                        } else {
                                                            discountExistDialog();

                                                            break;
                                                        }
                                                    } else {
                                                        discountExistDialog();
                                                        break;
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                discountExistDialog();

                                                break;
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    oneTimeDisFunction();

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                            }
                        });


                    }


                }
            });
        } else {
            couponEditText.setEnabled(false);
        }
    }

    public void oneTimeDisFunction() {
        oneTimeDiscountRef.orderByChild("discountName").equalTo(couponEditText.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot discountSnap : dataSnapshot.getChildren()) {
                        Customerdiscount = discountSnap.getValue(Discount.class);
                        if (Customerdiscount.getCustomerId().equals(saved_id)) {
                            if (Customerdiscount.getValidTillDate() != null) {
                                try {
                                    if ((new SimpleDateFormat("dd-MM-yyyy").parse(DateUtils.fetchFormatedCurrentDate()).before((new SimpleDateFormat("dd-MM-yyyy").parse(Customerdiscount.getValidTillDate()))))) {
                                        if (Customerdiscount.getDiscountStatus().equals("Active")) {
                                            if (Customerdiscount.getTypeOfDiscount() != null) {
                                                if (Customerdiscount.getTypeOfDiscount().equals("Price")) {
                                                    discountPrice(Customerdiscount);
                                                    break;
                                                } else if (Customerdiscount.getTypeOfDiscount().equals("Percent")) {
                                                    discountPercent(Customerdiscount);
                                                    break;
                                                }
                                            }
                                        } else {
                                            discountExistDialog();
                                            break;
                                        }
                                    } else if (DateUtils.fetchFormatedCurrentDate().equals(Customerdiscount.getValidTillDate())) {
                                        try {
                                            if ((sdf.parse(currentTime).compareTo(sdf.parse(Customerdiscount.getValidTillTime())) == -1) ||
                                                    (sdf.parse(currentTime).compareTo(sdf.parse(Customerdiscount.getValidTillTime())) == 0)) {

                                                if (Customerdiscount.getDiscountStatus().equals("Active")) {
                                                    if (Customerdiscount.getTypeOfDiscount() != null) {
                                                        if (Customerdiscount.getTypeOfDiscount().equals("Price")) {
                                                            discountPrice(Customerdiscount);

                                                            break;
                                                        } else if (Customerdiscount.getTypeOfDiscount().equals("Percent")) {
                                                            discountPercent(Customerdiscount);

                                                            break;
                                                        }
                                                    }
                                                } else {
                                                    discountExistDialog();
                                                    break;
                                                }
                                            } else {
                                                discountExistDialog();

                                                break;
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        discountExistDialog();

                                        break;
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            discountExistDialog();
                        }
                    }
                } else {
                    discountExistDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void viewCartFunction() {

        viewCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    giftWrapItemList.clear();
                    itemDetailList.clear();
                    finalBillAmount = 0;

                    tipsAmountInt = 0;
                    name = new ArrayList<>();
                    id = new ArrayList<>();
                    name.clear();
                    id.clear();
                    for (DataSnapshot viewCartsnap : dataSnapshot.getChildren()) {

                        itemDetails = viewCartsnap.getValue(ItemDetails.class);
                        if (itemDetails.getItemStatus().equals(Available)) {
                            finalBillAmount = finalBillAmount + (itemDetails.getTotalItemQtyPrice());
                            tipsAmountInt = finalBillAmount;
                            itemDetailList.add(itemDetails);
                        }
                    }
                    finalBill = String.valueOf(finalBillAmount);
                    orderDetails.setItemDetailList(itemDetailList);
                    orderDetails.setStoreName(itemDetails.getStoreName());
                    orderDetails.setStoreAddress(itemDetails.getStoreAdress());
                    orderDetails.setTotalAmount(Integer.parseInt(String.valueOf(finalBillAmount)));
                    orderDetails.setDeliveryFee(Integer.parseInt(deliveryFee));

                    if (itemDetailList != null && itemDetailList.size() > 0) {
                        storeId = itemDetailList.get(0).getSellerId();
                    }
                    if ("".equalsIgnoreCase(tipAmount) || tipAmount == null) {
                        temp = 0;
                    } else {
                        temp = Integer.parseInt(tipAmount);
                    }
                    tipsAmountInt = finalBillAmount + temp;
                }
                PaymentAdapter viewCartAdapter = new PaymentAdapter(PaymentActivity.this, itemDetailList);
                purchaseListView.setAdapter(viewCartAdapter);
                viewCartAdapter.notifyDataSetChanged();
                if (viewCartAdapter != null) {
                    int totalHeight = 0;

                    for (int i = 0; i < viewCartAdapter.getCount(); i++) {
                        View listItem = viewCartAdapter.getView(i, null, purchaseListView);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = purchaseListView.getLayoutParams();
                    params.height = totalHeight + (purchaseListView.getDividerHeight() * (viewCartAdapter.getCount() - 1));
                    purchaseListView.setLayoutParams(params);
                    purchaseListView.requestLayout();
                    purchaseListView.setAdapter(viewCartAdapter);
                    viewCartAdapter.notifyDataSetChanged();
                    totalItemValue.setText(" ₹" + String.valueOf(finalBill));
                    if (discountAppliedOrNot.equals("Yes")) {
                        totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt - getDiscountAmount + Integer.parseInt(deliveryFee)));
                        toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt - getDiscountAmount + Integer.parseInt(deliveryFee)));
                    } else {
                        totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + Integer.parseInt(deliveryFee)));
                        toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + Integer.parseInt(deliveryFee)));
                    }
                    giftAmount.setText("₹ " + deliveryFee);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deliveryFareFunction() {


        deliveryFareRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final DeliveryFareDetails deliveryFareDetails = dataSnapshot.getValue(DeliveryFareDetails.class);

                    deliveryFee = String.valueOf(0);

                    if (Integer.parseInt(deliveryFee) == 0) {
                        //need to check shipping fare after applied discounts
                        Log.d("First", finalBill + "D" + getDiscountAmount);
                        if (deliveryFareDetails.getCartValue() >= Math.abs(Integer.parseInt(finalBill) - getDiscountAmount)) {
                            Log.d("First1", finalBill + "D" + getDiscountAmount);
                            deliveryFee = String.valueOf(deliveryFareDetails.getDeliveryFare());
                            giftAmount.setText("₹ " + deliveryFee);


                            toPayValue.setText(" ₹" + Math.abs(Integer.parseInt(finalBill) - getDiscountAmount + Integer.parseInt(deliveryFee)));

                        } else if (deliveryFareDetails.getCartValue() < Math.abs(Integer.parseInt(finalBill) - getDiscountAmount)) {
                            Log.d("First2", finalBill + "D" + getDiscountAmount);
                            deliveryFee = String.valueOf(0);

                            giftAmount.setText("₹ " + deliveryFee);
                            toPayValue.setText(" ₹" + Math.abs(Integer.parseInt(finalBill) - getDiscountAmount + Integer.parseInt(deliveryFee)));
                        }
                    }
                    shippingAddressInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
                            LayoutInflater inflater = ((Activity) PaymentActivity.this).getLayoutInflater();
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


    public void discountExistDialog() {
        discountAppliedOrNot = "No";
        deliveryFareFunction();

        couponEditText.setText("");
        couponEditText.setEnabled(true);
        new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("Coupon Code Not Exist")
                .show();

    }

    public void discountPrice(Discount discount) {

        try {
            if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                    ((Number) NumberFormat.getInstance().parse(discount.getMinmumBillAmount())).intValue()) {
                if (discount.getUsage().equals("One Time Discount")) {
                    if (discountNameArrayList == null || discountNameArrayList.size() == 0) {

                        if (!discount.getDiscountPrice().equals("") && discount.getDiscountPrice() != null) {
                            getDiscountAmount = Integer.parseInt(discount.getDiscountPrice());
                        } else {
                            getDiscountAmount = 0;
                        }
                        getMaximumBillAmount = 0;
                        typeOfDiscount = discount.getTypeOfDiscount();
                        discountName = discount.getDiscountName();
                        autoLoadFunction();
                        viewCartFunction();
                        discountName = discount.getDiscountName();
                        couponEditText.setEnabled(false);
                        discountAppliedOrNot = "Yes";
                        deliveryFareFunction();

                    } else {

                        int count = 0;
                        for (int i = 0; i < discountNameArrayList.size(); i++) {
                            if (discountNameArrayList.get(i).equals(discount.getDiscountName())) {
                                count = count + 1;
                                break;
                            }
                        }

                        if (count == 0) {

                            if (!discount.getDiscountPrice().equals("") && discount.getDiscountPrice() != null) {
                                getDiscountAmount = Integer.parseInt(discount.getDiscountPrice());
                            } else {
                                getDiscountAmount = 0;
                            }
                            getMaximumBillAmount = 0;
                            typeOfDiscount = discount.getTypeOfDiscount();
                            discountName = discount.getDiscountName();
                            autoLoadFunction();
                            viewCartFunction();
                            discountName = discount.getDiscountName();
                            couponEditText.setEnabled(false);
                            discountAppliedOrNot = "Yes";
                            deliveryFareFunction();

                        } else {

                            discountExistDialog();
                        }
                    }

                } else {
                    if (!discount.getDiscountPrice().equals("") && discount.getDiscountPrice() != null) {
                        getDiscountAmount = Integer.parseInt(discount.getDiscountPrice());
                    } else {
                        getDiscountAmount = 0;
                    }


                    getMaximumBillAmount = 0;
                    typeOfDiscount = discount.getTypeOfDiscount();
                    discountName = discount.getDiscountName();
                    autoLoadFunction();
                    viewCartFunction();

                    couponEditText.setEnabled(false);
                    discountAppliedOrNot = "Yes";
                    deliveryFareFunction();

                }
            } else {
                discountExistDialog();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void discountPercent(Discount discount) {
        try {
            if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                    ((Number) NumberFormat.getInstance().parse(discount.getMinmumBillAmount())).intValue()) {

                if (discount.getUsage().equals("One Time Discount")) {

                    if (discountNameArrayList == null || discountNameArrayList.size() == 0) {
                        if (!discount.getDiscountPercentageValue().equals("") && discount.getDiscountPercentageValue() != null) {
                            getDiscountAmount = Integer.parseInt(discount.getDiscountPercentageValue());
                        } else {
                            getDiscountAmount = 0;
                        }
                        if (!discount.getMinmumBillAmount().equals("") && discount.getMinmumBillAmount() != null) {
                            getMaximumBillAmount = Integer.parseInt(discount.getMinmumBillAmount());
                        } else {
                            getMaximumBillAmount = 0;
                        }
                        ifPercentMaxAmountForDiscount = discount.getMaxAmountForDiscount();
                        typeOfDiscount = discount.getTypeOfDiscount();
                        discountName = discount.getDiscountName();
                        autoLoadFunction();
                        viewCartFunction();
                        couponEditText.setEnabled(false);
                        discountAppliedOrNot = "Yes";
                        deliveryFareFunction();

                    } else {
                        int count = 0;
                        for (int i = 0; i < discountNameArrayList.size(); i++) {
                            if (discountNameArrayList.get(i).equals(discount.getDiscountName())) {
                                count = count + 1;
                            }
                        }
                        if (count == 0) {
                            if (!discount.getDiscountPercentageValue().equals("") && discount.getDiscountPercentageValue() != null) {
                                getDiscountAmount = Integer.parseInt(discount.getDiscountPercentageValue());
                            } else {
                                getDiscountAmount = 0;
                            }

                            if (!discount.getMinmumBillAmount().equals("") && discount.getMinmumBillAmount() != null) {
                                getMaximumBillAmount = Integer.parseInt(discount.getMinmumBillAmount());
                            } else {
                                getMaximumBillAmount = 0;
                            }

                            ifPercentMaxAmountForDiscount = discount.getMaxAmountForDiscount();
                            typeOfDiscount = discount.getTypeOfDiscount();
                            discountName = discount.getDiscountName();
                            autoLoadFunction();
                            viewCartFunction();
                            couponEditText.setEnabled(false);
                            discountAppliedOrNot = "Yes";
                            deliveryFareFunction();

                        } else {
                            discountExistDialog();

                        }
                    }
                } else {
                    if (!discount.getDiscountPercentageValue().equals("") && discount.getDiscountPercentageValue() != null) {
                        getDiscountAmount = Integer.parseInt(discount.getDiscountPercentageValue());
                    } else {
                        getDiscountAmount = 0;
                    }
                    if (!discount.getMinmumBillAmount().equals("") && discount.getMinmumBillAmount() != null) {
                        getMaximumBillAmount = Integer.parseInt(discount.getMinmumBillAmount());
                    } else {
                        getMaximumBillAmount = 0;
                    }
                    ifPercentMaxAmountForDiscount = discount.getMaxAmountForDiscount();
                    typeOfDiscount = discount.getTypeOfDiscount();
                    discountName = discount.getDiscountName();
                    autoLoadFunction();
                    viewCartFunction();
                    couponEditText.setEnabled(false);
                    discountAppliedOrNot = "Yes";
                    deliveryFareFunction();

                }
            } else {
                discountExistDialog();
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
        if (!((Activity) PaymentActivity.this).isFinishing()) {
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