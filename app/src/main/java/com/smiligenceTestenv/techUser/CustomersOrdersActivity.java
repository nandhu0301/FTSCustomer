package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.Adapter.CustomerOrdersAdapter;
import com.smiligenceTestenv.techUser.bean.OrderDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.Constant;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;
import com.smiligenceTestenv.techUser.common.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomersOrdersActivity extends AppCompatActivity   implements NetworkStateReceiver.NetworkStateReceiverListener
 {
    DatabaseReference orderDetailsRef, logindataRef;

    ExpandableListView orderList;
    DatabaseReference billDetailref, storeNameRef;
    ArrayList<OrderDetails> billArrayList = new ArrayList<OrderDetails>();
    List<String> header_list = new ArrayList<String>();

    List<String> phone_list = new ArrayList<String>();
    List<String> billDateList = new ArrayList<String>();
    public  static  int maxid=0;

    CustomerOrdersAdapter receiptAdapter;

    int counter = 0;
    String billedDate;
    BottomNavigationView bottomNavigation;
    ImageView back_Button;

    List<OrderDetails> datewiseBillDetails = new ArrayList<>();

    String saved_id;
    Map<String, List<OrderDetails>> expandableBillDetail = new HashMap<>();

     private NetworkStateReceiver networkStateReceiver;
     AlertDialog alertDialog;
     ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_orders);
        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utils.getDatabase();
        checkGPSConnection(getApplicationContext());


        bottomNavigation = findViewById(R.id.bottomsheetlayout);

        bottomNavigation.setSelectedItemId(R.id.order);

        orderList = findViewById(R.id.orderList);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

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

        orderDetailsRef = CommonMethods.fetchFirebaseDatabaseReference(Constant.ORDER_DETAILS_FIREBASE_TABLE);

        Query storebasedQuery = orderDetailsRef.orderByChild("customerId").equalTo(saved_id);

        storebasedQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot dateSnap : dataSnapshot.getChildren()) {
                        OrderDetails billDetails = dateSnap.getValue(OrderDetails.class);
                        phone_list.add(billDetails.getCustomerPhoneNumber());
                        billDateList.add(billDetails.getCustomerId());
                    }
                    TextUtils.removeDuplicatesList(phone_list);
                    TextUtils.removeDuplicatesList(billDateList);

                    for (int pincodeIterator = 0; pincodeIterator < billDateList.size(); pincodeIterator++) {
                        if (billDateList.get(pincodeIterator).equals(saved_id)) {
                            Query pincodeBaseQuery = orderDetailsRef.orderByChild("customerId").equalTo
                                    (billDateList.get(pincodeIterator));
                            pincodeBaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        header_list.clear();
                                        billArrayList.clear();
                                        counter = 0;

                                        for (DataSnapshot dateSnap : dataSnapshot.getChildren()) {

                                            OrderDetails billDetails = dateSnap.getValue(OrderDetails.class);
                                            billArrayList.add(billDetails);
                                            header_list.add(billDetails.getPaymentDate());
                                        }

                                        TextUtils.removeDuplicatesList(header_list);

                                        if (header_list.size() > 0) {

                                            Collections.reverse(header_list);

                                            for (int i = 0; i < header_list.size(); i++) {
                                                billedDate = header_list.get(i);
                                                for (int j = 0; j < billArrayList.size(); j++) {

                                                    OrderDetails billDetailsData = billArrayList.get(j);
                                                    if (billedDate.equalsIgnoreCase(billDetailsData.getPaymentDate())) {

                                                        datewiseBillDetails.add(billDetailsData);

                                                    }
                                                }


                                                if (datewiseBillDetails != null) {

                                                    Collections.reverse(datewiseBillDetails);
                                                    expandableBillDetail.put(header_list.get(counter), datewiseBillDetails);
                                                    counter++;
                                                    datewiseBillDetails = new ArrayList<>();
                                                }
                                            }
                                        }

                                        receiptAdapter = new CustomerOrdersAdapter(CustomersOrdersActivity.this,
                                                header_list, (HashMap<String, List<OrderDetails>>) expandableBillDetail);

                                        orderList.setAdapter(receiptAdapter);

                                        for (int i = 0; i < receiptAdapter.getGroupCount(); i++) {
                                            orderList.expandGroup(i);
                                        }

                                        orderList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                            @Override
                                            public boolean onChildClick(ExpandableListView parent, View v,
                                                                        int groupPosition, int childPosition, long id) {

                                                OrderDetails orderDetails = expandableBillDetail.get(header_list.get(groupPosition))
                                                        .get(childPosition);


                                                Intent intent = new Intent(CustomersOrdersActivity.this, ViewOrderActivity.class);
                                                intent.putExtra("OrderidDetails", String.valueOf(orderDetails.getOrderId()));
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);


                                                return false;
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CustomersOrdersActivity.this, HomePageActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);



    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference  itemRatingsAndReviewsRef = CommonMethods.fetchFirebaseDatabaseReference("ItemRatingsAndReview");
        itemRatingsAndReviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid= (int) dataSnapshot.getChildrenCount();
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
         if (!((Activity) CustomersOrdersActivity.this).isFinishing()) {
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