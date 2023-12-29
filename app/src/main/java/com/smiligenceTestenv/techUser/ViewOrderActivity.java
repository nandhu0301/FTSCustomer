package com.smiligenceTestenv.techUser;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.smiligenceTestenv.techUser.Adapter.ItemOrderDetails;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.OrderDetails;
import com.smiligenceTestenv.techUser.bean.ReviewAndRatings;
import com.smiligenceTestenv.techUser.bean.StoreDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.Constant;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceTestenv.techUser.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.SELLER_DETAILS;

public class ViewOrderActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
    TextView order_Date, order_Id, order_Total, type_Of_Payment, fullName, shipping_Address, amount, giftPrice, offerName, deductionAmount, wholeCharge, order_status,
            customerPhoneNumber, orderTimeTxt, clearCart;
    BottomNavigationView bottomNavigation;
    ConstraintLayout orderDetailsLayout, paymentDetailsLayout, shippingAddressLayout, orderSummaryLayout, trackingDetailsLayout;
    RelativeLayout itemDetailsLayout, taxAmountCalculationLayout;
    DatabaseReference databaseReference, viewcartRef;
    private ArrayList<ItemDetails> openTicketItemList = new ArrayList<>();
    String orderIdForItemRatings, orderStatusText;
    ItemOrderDetails itemOrderDetails;
    ListView listView;
    public static String getOrderIdValue, getType;
    boolean checkNotification = true;
    boolean notify = false;
    OrderDetails orderDetailsNotification;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    DatabaseReference storeDetailsDataRef;
    ReviewAndRatings reviewAndRatings = new ReviewAndRatings();
    Animation animation;
    Ringtone r;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsList1 = new ArrayList<>();
    ArrayList<String> itemIdList = new ArrayList<>();
    ArrayList<ItemDetails> ItemDetailsNew = new ArrayList<>();
    private static final long START_TIME_IN_MILLIS = 61000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private long remainingTimeInMillis;
    public static String saved_id;
    boolean checking = true;
    ItemDetails itemDetails;
    String reasonStr;
    DatabaseReference reviewRatingStore;
    TextView deliveryfeeText;
    //float taxPrice;
    public static int maxid;
    TextView courierPartnerName, trackingId, trackingImage;
    Button generatePdf;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    String storeName, storePhone, storeAddress, storePincode,storeGstNumber;
    int gstAmount;


    String pathToMyAttachedFile;
    View cgst_sgst_calculation_layout, igst_calculation_layout;
    TextView cgst_Textview, sgst_Textview, totalTaxAmount_cgst_sgst;
    TextView igst_Textview, totalTaxAmount_igst;
    float cgst_sgst_calculation = 0;
    boolean insideOrOutsideTag;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_view_order);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");

        databaseReference = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        storeDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS);
        viewcartRef = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(saved_id);
        loadFunction();

        itemDetailsLayout = findViewById(R.id.itemdetailslayout);

        bottomNavigation = findViewById(R.id.drawerLayout);
        bottomNavigation.setSelectedItemId(R.id.order);

        generatePdf = findViewById(R.id.generatePdf);

        animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        checkGPSConnection(getApplicationContext());
        trackingDetailsLayout = findViewById(R.id.tracking_details_layout);

        courierPartnerName = trackingDetailsLayout.findViewById(R.id.courierPartnerNameEdt);
        trackingId = trackingDetailsLayout.findViewById(R.id.trackingIdEdt);
        trackingImage = trackingDetailsLayout.findViewById(R.id.status);
        //Order details value
        orderDetailsLayout = findViewById(R.id.order_details_layout);
        order_Date = orderDetailsLayout.findViewById(R.id.orderdate);
        order_Id = orderDetailsLayout.findViewById(R.id.bill_num);
        order_Total = orderDetailsLayout.findViewById(R.id.order_total);
        order_status = orderDetailsLayout.findViewById(R.id.order_status);
        orderTimeTxt = orderDetailsLayout.findViewById(R.id.ordertimetxt);
        // taxAmount = orderDetailsLayout.findViewById(R.id.totaltaxValue);


        taxAmountCalculationLayout = findViewById(R.id.taxAmountCalculationLayout);

        cgst_sgst_calculation_layout = getLayoutInflater()
                .inflate(R.layout.cgst_sgst_order_details, taxAmountCalculationLayout, false);

        //cgst-sgst
        cgst_Textview = cgst_sgst_calculation_layout.findViewById(R.id.cgstTextview);
        sgst_Textview = cgst_sgst_calculation_layout.findViewById(R.id.sgstTextview);
        totalTaxAmount_cgst_sgst = cgst_sgst_calculation_layout.findViewById(R.id.totaltaxAmount_cgst_sgst);


        igst_calculation_layout = getLayoutInflater()
                .inflate(R.layout.igst_order_details, taxAmountCalculationLayout, false);
        igst_Textview = igst_calculation_layout.findViewById(R.id.igstTextview);
        totalTaxAmount_igst = igst_calculation_layout.findViewById(R.id.totalTaxAmount_igst);


        //igst

        //Payment details
        paymentDetailsLayout = findViewById(R.id.payment_details);

        type_Of_Payment = paymentDetailsLayout.findViewById(R.id.type_of_payment);

        //Shipping Address Details
        shippingAddressLayout = findViewById(R.id.shipping_details_layout);
        fullName = shippingAddressLayout.findViewById(R.id.full_name);
        shipping_Address = shippingAddressLayout.findViewById(R.id.address);
        customerPhoneNumber = shippingAddressLayout.findViewById(R.id.phoneNumber);

        //Order summary
        orderSummaryLayout = findViewById(R.id.cart_total_amount_layout);
        amount = orderSummaryLayout.findViewById(R.id.tips_price1);

        wholeCharge = orderSummaryLayout.findViewById(R.id.total_price);

        offerName = orderSummaryLayout.findViewById(R.id.offerNameTextViewResult);
        deductionAmount = orderSummaryLayout.findViewById(R.id.deductionAmountTextViewResult);
        deliveryfeeText = orderSummaryLayout.findViewById(R.id.deliveryfeeText);

//        final StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
//        }

        //Special instruction layout

        //ItemDetails
        listView = itemDetailsLayout.findViewById(R.id.itemDetailslist);


        getOrderIdValue = getIntent().getStringExtra("OrderidDetails");
        getType = getIntent().getStringExtra("type");

//        File docsFolder = null;
//
//        docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "Download");
//        pdfFile = new File(docsFolder.getAbsolutePath(), "Sample.pdf");
//
        generatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdfWrapper();

                    SweetAlertDialog errorDialog = new SweetAlertDialog(ViewOrderActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    errorDialog.setCancelable(false);
                    errorDialog
                            .setContentText("Downloaded").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            errorDialog.dismiss();
                            File docsFolder = null;
                            docsFolder = new File(getApplicationContext().getExternalFilesDir(null) + "/" + "Download");


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                docsFolder = new File(getApplicationContext().getExternalFilesDir(null) + "/" + "Download");
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "Download");
                            } else {
                                docsFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Download");

                            }

                            if (!docsFolder.exists()) {
                                docsFolder.mkdir();
                            }
                            pdfFile = new File(docsFolder.getAbsolutePath(), "Invoice" + order_Id.getText().toString() + ".pdf");


                            previewPdf(pdfFile);

                        }

                    });
                    errorDialog.show();
                    Button btn = (Button) errorDialog.findViewById(R.id.confirm_button);
                    btn.setBackgroundColor(ContextCompat.getColor(ViewOrderActivity.this, R.color.colorPrimary));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order: {
                        Intent intent = new Intent(getApplicationContext(), CustomersOrdersActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.home: {
                        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.cus_profile: {
                        Intent intent = new Intent(getApplicationContext(), CustomerProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });


        final Query getOrderDetails = databaseReference.orderByChild("orderId").equalTo(getOrderIdValue);


        getOrderDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        orderDetails = dataSnapshot1.getValue(OrderDetails.class);
                        itemDetailsList1 = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();
                        order_Date.setText(orderDetails.getPaymentDate());
                        order_Id.setText(orderDetails.getOrderNumberForFinancialYearCalculation());
                        order_Total.setText(" ₹" + (orderDetails.getPaymentamount()));
                        order_status.setText(orderDetails.getOrderStatus());
                        if (orderDetails.getDeliveryFee() == 0) {
                            deliveryfeeText.setText("Free");
                        } else {
                            deliveryfeeText.setText("₹ " + orderDetails.getDeliveryFee());
                        }

                        if (orderDetails.getCourierPartnerName() != null) {
                            courierPartnerName.setText(orderDetails.getCourierPartnerName());
                        }
                        if (orderDetails.getTrackingId() != null) {
                            trackingId.setText(orderDetails.getTrackingId());
                        }


                        if (orderDetails.getDocType() != null && !orderDetails.getDocType().equals("")) {

                            trackingImage.setVisibility(View.VISIBLE);
                            trackingImage.startAnimation(animation);
                            trackingImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (orderDetails.getDocType().equals("pdf")) {
                                        openPDFViewer(orderDetails.getTrackingImage());
                                    } else {

                                        if (orderDetails.getTrackingImage() != null) {


                                            if (!orderDetails.getTrackingImage().equals("")) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrderActivity.this);
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                });

                                                final AlertDialog dialog = builder.create();
                                                LayoutInflater inflater = getLayoutInflater();
                                                View dialogLayout = inflater.inflate(R.layout.tracking_image_layout, null);
                                                dialog.setView(dialogLayout);

                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.show();
                                                ImageView trackImage = (ImageView) dialog.findViewById(R.id.goProDialogImage);
                                                RequestOptions requestOptions = new RequestOptions();
                                                requestOptions.placeholder(R.mipmap.ic_launcher);
                                                requestOptions.error(R.mipmap.ic_launcher);
                                                Glide.with(ViewOrderActivity.this)
                                                        .setDefaultRequestOptions(requestOptions)
                                                        .load(orderDetails.getTrackingImage()).fitCenter().into(trackImage);
                                            }
                                        }
                                    }
                                }
                            });


                        } else {
                            trackingImage.setVisibility(View.INVISIBLE);
                        }


                        orderTimeTxt.setText(orderDetails.getOrderTime());
                        type_Of_Payment.setText(orderDetails.getPaymentType());

                        fullName.setText(" " + orderDetails.getFullName());
                        shipping_Address.setText(" " + orderDetails.getShippingaddress());
                        customerPhoneNumber.setText(" " + orderDetails.getCustomerPhoneNumber());
                        fullName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cus, 0, 0, 0);
                        shipping_Address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_01, 0, 0, 0);
                        customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phonenumicon_01, 0, 0, 0);
                        amount.setText(" ₹" + (orderDetails.getTotalAmount()));
                        wholeCharge.setText(" ₹" + (orderDetails.getPaymentamount()));
                        offerName.setText(orderDetails.getDiscountName());
                        deductionAmount.setText(" - ₹" + (orderDetails.getDiscountAmount()));


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        storeDetailsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    StoreDetails storeDetails = dataSnapshot.getValue(StoreDetails.class);
                    storeName = storeDetails.getStoreName();
                    storePhone = storeDetails.getStoreContactNumber();
                    storeAddress = storeDetails.getStoreAddress();
                    storePincode = storeDetails.getZipCode();
                    gstAmount = storeDetails.getGst();
                    storeGstNumber=storeDetails.getGstNumber();
                }



                final Query getitemDetailsQuery = databaseReference.orderByChild("orderId").equalTo(String.valueOf(getOrderIdValue));


                getitemDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {

                            OrderDetails openTickets = postdataSnapshot.getValue(OrderDetails.class);
                            openTicketItemList = (ArrayList<ItemDetails>) openTickets.getItemDetailList();
                            orderIdForItemRatings = openTickets.getOrderId();
                            orderStatusText = openTickets.getOrderStatus();


                            //taxPrice = orderDetails.getPaymentamount() - (orderDetails.getPaymentamount() * 100) / (100 + gstAmount);
                           // Log.d("Result",taxPrice+"jj"+orderDetails.getPaymentamount() + "cc" + gstAmount);

                            //System.out.print("Result" + (orderDetails.getPaymentamount() + "cc" + gstAmount));
                          //  cgst_sgst_calculation = taxPrice / 2;


                            if (orderDetails.getInsideOrOutside() != null) {
                                removeView();

                                if (orderDetails.getInsideOrOutside().equals(Constant.TamilNadu)) {
                                    insideOrOutsideTag = true;
                                    taxAmountCalculationLayout.addView(cgst_sgst_calculation_layout);
                                    cgst_Textview.setText("₹ " + orderDetails.getCgstValue());
                                    sgst_Textview.setText("₹ " + orderDetails.getSgstValue());
                                    totalTaxAmount_cgst_sgst.setText("₹ " + orderDetails.getTotalTaxValue());
                                }
                                else {
                                    insideOrOutsideTag = false;
                                    taxAmountCalculationLayout.addView(igst_calculation_layout);
                                    igst_Textview.setText("₹ " + orderDetails.getTotalTaxValue());
                                    totalTaxAmount_igst.setText("₹ " + orderDetails.getTotalTaxValue());
                                }
                            }
                            //taxAmount.setText("₹ " + (taxPrice));

                            itemOrderDetails = new
                                    ItemOrderDetails(ViewOrderActivity.this, openTicketItemList, orderIdForItemRatings, orderStatusText);
                            itemOrderDetails.notifyDataSetChanged();
                            listView.setAdapter(itemOrderDetails);

                            if (listView != null) {
                                int totalHeight = 0;
                                for (int i = 0; i < itemOrderDetails.getCount(); i++) {
                                    View listItem = itemOrderDetails.getView(i, null, listView);
                                    listItem.measure(0, 0);
                                    totalHeight += listItem.getMeasuredHeight();
                                }
                                ViewGroup.LayoutParams params = listView.getLayoutParams();
                                params.height = totalHeight + (listView.getDividerHeight() * (listView.getCount() - 1));
                                listView.setLayoutParams(params);
                                listView.requestLayout();
                                listView.setAdapter(itemOrderDetails);
                                itemOrderDetails.notifyDataSetChanged();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });


    }

    private void previewPdf(File pdfFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            intent = Intent.createChooser(intent, "Open File");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);



    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ViewOrderActivity.this, HomePageActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void loadFunction() {
        getOrderIdValue = getIntent().getStringExtra("OrderidDetails");
        databaseReference = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);

        Query placedOrderQuery = databaseReference.orderByChild("orderId").equalTo(String.valueOf(getOrderIdValue));

        placedOrderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notify = true;
                if (checkNotification) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot detailsSnap : dataSnapshot.getChildren()) {
                            orderDetailsNotification = detailsSnap.getValue(OrderDetails.class);
                        }
                        if (orderDetailsNotification.getOrderStatus().equals("Order Placed")) {


                            if ("false".equalsIgnoreCase(orderDetailsNotification.getNotificationStatusForCustomer())) {
                                if (notify) {
                                    if (!((Activity) ViewOrderActivity.this).isFinishing()) {
                                        DatabaseReference orderDetailsRef = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails").child(orderDetailsNotification.getOrderId());
                                        orderDetailsRef.child("notificationStatusForCustomer").setValue("true");
                                    }
                                    notify = false;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new weatherTask().execute(orderDetailsNotification.getOrderId());
                                        }
                                    }, 3000);
                                }
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class weatherTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String orderId = strings[0];

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotification("Order Id #" + orderDetailsNotification.getOrderId() + " is Placed Successfully", orderDetailsNotification.getOrderId());
                notify = false;
            }
            return null;
        }
    }

    public void createNotification(String res, String orderId) {
        int count = 0;


        if (count == 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ViewOrderActivity.this, default_notification_channel_id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("New Order")
                    .setContentText(res)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));


            Intent secondActivityIntent = new Intent(this, ViewOrderActivity.class);
            secondActivityIntent.putExtra("OrderidDetails", getOrderIdValue);
            secondActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            PendingIntent secondActivityPendingIntent = PendingIntent.getActivity(this, 0, secondActivityIntent, PendingIntent.FLAG_ONE_SHOT);
            getOrderIdValue = getIntent().getStringExtra("OrderidDetails");
            mBuilder.addAction(R.mipmap.ic_launcher, "View", secondActivityPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Integer.parseInt(orderId), notification);
            count = count + 1;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadFunction();
        DatabaseReference itemRatingsAndReviewsRef = CommonMethods.fetchFirebaseDatabaseReference("ItemRatingsAndReview");
        itemRatingsAndReviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openPDFViewer(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (Exception e) {
        }
    }


    private void createPdfWrapper() throws FileNotFoundException, IOException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            createPdf();
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf() throws FileNotFoundException, IOException {

        File docsFolder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            docsFolder = new File(getApplicationContext().getExternalFilesDir(null) + "/" + "Download");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "Download");

        } else {
            docsFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Download");

        }


        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }
        pdfFile = new File(docsFolder.getAbsolutePath(), "Invoice" + order_Id.getText().toString() + ".pdf");


        PdfWriter pdfWriter = new PdfWriter(pdfFile);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        DeviceRgb invoiceGreen = new DeviceRgb(21,2,76);
        DeviceRgb invoiceGray = new DeviceRgb(220, 220, 220);


        float columnWidth[] = {140, 140, 140, 180};


        Table table1 = new Table(columnWidth);

        float columnWidthSellerBuyer[] = {180, 100, 180, 100};
        Table sellerBuyerTable = new Table(columnWidthSellerBuyer);
        Drawable d1 = getDrawable(R.drawable.new_fts_logo);
        Bitmap bitmap1 = ((BitmapDrawable) d1).getBitmap();

        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1);
        byte[] bitmapData1 = stream1.toByteArray();
        ImageData imageData10 = ImageDataFactory.create(bitmapData1);
        Image imag10 = new Image(imageData10);
        imag10.setWidth(100f);
        imag10.setHeight(100f);

        SolidLine line = new SolidLine(1f);
        line.setColor(invoiceGreen);

        LineSeparator ls = new LineSeparator(line);
        ls.setHeight(10f);


        // table1-----01
        table1.addCell(new Cell(3, 1).add(imag10).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell(2, 3).add(new Paragraph("Invoice: " + order_Id.getText().toString()).setFontSize(16f).setFontColor(invoiceGreen)).setBorder(Border.NO_BORDER));


        // table1-----02

        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        if (storeGstNumber != null){
            table1.addCell(new Cell(2, 3).add(new Paragraph("Date: " + order_Date.getText().toString() +"\n\n"+"GSTIN: " + storeGstNumber.toString()).setFontSize(15f)).setBorder(Border.NO_BORDER));
        }else {
            table1.addCell(new Cell(2, 3).add(new Paragraph("Date: " + order_Date.getText().toString() ).setFontSize(15f)).setBorder(Border.NO_BORDER));
        }

        // table1-----05
        table1.addCell(new Cell().add(new Paragraph("\n")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        // table1-----06
        sellerBuyerTable.addCell(new Cell().add(new Paragraph("Seller").setFontSize(18f).setBold()).setBorder(Border.NO_BORDER));
        sellerBuyerTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));


        sellerBuyerTable.addCell(new Cell().add(new Paragraph("Buyer").setFontSize(18f).setBold()).setBorder(Border.NO_BORDER));
        sellerBuyerTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));


        // table1-----07
        sellerBuyerTable.addCell(new Cell().add(new Paragraph("Fresh Spices").setFontSize(15f)).setBorder(Border.NO_BORDER));
        sellerBuyerTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        sellerBuyerTable.addCell(new Cell().add(new Paragraph(fullName.getText().toString()).setFontSize(15f)).setBorder(Border.NO_BORDER));
        sellerBuyerTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        // table1----08
        sellerBuyerTable.addCell(new Cell(1, 2).add(new Paragraph("Sapani koil Street, Gokhale Rd, Chinna Chokikulam, Tamil Nadu 625002")).setBorder(Border.NO_BORDER));
        sellerBuyerTable.addCell(new Cell(1, 2).add(new Paragraph(shipping_Address.getText().toString())).setBorder(Border.NO_BORDER));
        // table1-----09
        sellerBuyerTable.addCell(new Cell(1, 2).add(new Paragraph("Email: " + "favtechshop@gmail.com" + "\n" + "Phone: " + storePhone)).setBorder(Border.NO_BORDER));
        //sellerBuyerTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        sellerBuyerTable.addCell(new Cell(1, 2).add(new Paragraph("Phone: " + "0452 450 0081")).setBorder(Border.NO_BORDER));
        float columWith2[] = {62, 162, 70, 84, 102, 82};
        Table table2 = new Table(columWith2);
        //table2-01
        table2.addCell(new Cell().add(new Paragraph("S.No").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoiceGreen));
        table2.addCell(new Cell().add(new Paragraph("Item Name").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoiceGreen));
        table2.addCell(new Cell().add(new Paragraph("GST(%)").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoiceGreen));
        table2.addCell(new Cell().add(new Paragraph("Price").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoiceGreen));
        table2.addCell(new Cell().add(new Paragraph("Qty").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoiceGreen));
        table2.addCell(new Cell().add(new Paragraph("Total").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoiceGreen));
        //table2-02

        for (int i = 0; i < openTicketItemList.size(); i++) {
            table2.addCell(new Cell().add(new Paragraph("" + (i + 1))));
            table2.addCell(new Cell().add(new Paragraph(openTicketItemList.get(i).getItemName())));
            table2.addCell(new Cell().add(new Paragraph(String.valueOf(openTicketItemList.get(i).getTax()))));
            table2.addCell(new Cell().add(new Paragraph(String.valueOf(openTicketItemList.get(i).getItemPrice()))));
            table2.addCell(new Cell().add(new Paragraph(String.valueOf(openTicketItemList.get(i).getItemBuyQuantity()))));
            table2.addCell(new Cell().add(new Paragraph(String.valueOf(openTicketItemList.get(i).getTotalItemQtyPrice()))));
        }


        //table2-06
        tableFunction(table2);
        table2.addCell(new Cell().add(new Paragraph("Item Total").setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table2.addCell(new Cell().add(new Paragraph("₹ " + amount.getText().toString()).setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        //table2-06
        tableFunction(table2);
        table2.addCell(new Cell().add(new Paragraph("Discount Name").setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table2.addCell(new Cell().add(new Paragraph(offerName.getText().toString()).setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        //table2-07
        tableFunction(table2);
        table2.addCell(new Cell().add(new Paragraph("Discount Amount").setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table2.addCell(new Cell().add(new Paragraph("₹ " + deductionAmount.getText().toString()).setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        //table2-08
        tableFunction(table2);
        table2.addCell(new Cell().add(new Paragraph("Shipping Fare").setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table2.addCell(new Cell().add(new Paragraph("₹ " + deliveryfeeText.getText().toString()).setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        tableFunction(table2);

        //cgst sgst igst

        if (insideOrOutsideTag) {
            table2.addCell(new Cell().add(new Paragraph("CGST").setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table2.addCell(new Cell().add(new Paragraph("₹ " + cgst_Textview.getText().toString()).setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tableFunction(table2);

            table2.addCell(new Cell().add(new Paragraph("SGST").setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table2.addCell(new Cell().add(new Paragraph("₹ " + sgst_Textview.getText().toString()).setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tableFunction(table2);
        } else {
            table2.addCell(new Cell().add(new Paragraph("IGST").setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table2.addCell(new Cell().add(new Paragraph("₹ " + igst_Textview.getText().toString()).setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tableFunction(table2);
        }

        table2.addCell(new Cell().add(new Paragraph("Total Tax Amount").setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table2.addCell(new Cell().add(new Paragraph(String.valueOf("₹ " + orderDetails.getTotalTaxValue())).setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        tableFunction(table2);


        table2.addCell(new Cell().add(new Paragraph("Billed Amount").setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table2.addCell(new Cell().add(new Paragraph("₹ " + wholeCharge.getText().toString()).setFontColor(ColorConstants.BLACK)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        document.add(table1);
        document.add(ls);
        document.add(new Paragraph("\n"));
        document.add(sellerBuyerTable);
        document.add(new Paragraph("\n"));
        document.add(table2);
        document.close();


    }

    public void tableFunction(Table table2) {
        for (int i = 0; i < 4; i++) {
            table2.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        }
    }

    public void removeView() {
        if (cgst_sgst_calculation_layout.getParent() != null) {
            ((ViewGroup) cgst_sgst_calculation_layout.getParent()).removeView(cgst_sgst_calculation_layout); // <- fix
        }
        if (igst_calculation_layout.getParent() != null) {
            ((ViewGroup) igst_calculation_layout.getParent()).removeView(igst_calculation_layout); // <- fix
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
        if (!((Activity) ViewOrderActivity.this).isFinishing()) {
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