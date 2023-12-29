package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.Adapter.FilterAdapter;
import com.smiligenceTestenv.techUser.bean.Filters;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.Preferences;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.smiligenceTestenv.techUser.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.SELLER_ID;


public class FilterActivity extends AppCompatActivity  implements NetworkStateReceiver.NetworkStateReceiverListener
{
    DatabaseReference itemDatebaseRef;
    ArrayList<ItemDetails> itemList = new ArrayList<ItemDetails>();
    String getstoreIdIntent, getstoreNameIntent, categoryIdIntent, categoryNameIntent, pinCode,getfssaiNumber;

    String counterInd;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase ();
        setContentView(R.layout.activity_filter);
        checkGPSConnection(getApplicationContext());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getstoreIdIntent = getIntent().getStringExtra("StoreId");
        getstoreNameIntent = getIntent().getStringExtra("StoreName");
        categoryIdIntent = getIntent().getStringExtra("categoryId");
        categoryNameIntent = getIntent().getStringExtra("categoryName");
        pinCode = getIntent().getStringExtra("PinCode");
        getfssaiNumber=getIntent().getStringExtra("fssaiNumber");
        counterInd=getIntent().getStringExtra("counterInd");

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();




        itemDatebaseRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);
        Query fetchItemQuery = itemDatebaseRef.orderByChild(SELLER_ID).equalTo(getstoreIdIntent);

        fetchItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot itemObjectSnap : dataSnapshot.getChildren()) {
                    ItemDetails itemDetails = (ItemDetails) itemObjectSnap.getValue(ItemDetails.class);
                    itemList.add(itemDetails);
                }

                initControls();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initControls() {
        RecyclerView filterRV = findViewById(R.id.filterRV);
        RecyclerView filterValuesRV = findViewById(R.id.filterValuesRV);
        filterRV.setLayoutManager(new LinearLayoutManager(this));
        filterValuesRV.setLayoutManager(new LinearLayoutManager(this));

        List<String> prices = Arrays.asList("0-1000", "1001-5000", "5001-20000","20001-200000");

        if (!Preferences.filters.containsKey(Filters.INDEX_PRICE)) {
            Preferences.filters.put(Filters.INDEX_PRICE, new Filters("Price", prices, new ArrayList()));
        }

        FilterAdapter filterAdapter = new FilterAdapter(Preferences.filters, filterValuesRV);
        filterRV.setAdapter(filterAdapter);

        Button clearB = findViewById(R.id.clearB);

        clearB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());


                IntentActivity();
            }
        });

        Button applyB = findViewById(R.id.applyB);
        applyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.filters.size() == 1) {
                    Toast.makeText(FilterActivity.this,
                            "No Items Available in this Price range..select another Price range", Toast.LENGTH_SHORT).show();
                }
                IntentActivity();
            }
        });
    }

    public void IntentActivity() {
        Intent intent = new Intent(FilterActivity.this, ProductsListingActivity.class);
        intent.putExtra("StoreId", getstoreIdIntent);
        intent.putExtra("StoreName", getstoreNameIntent);
        intent.putExtra("categoryName", categoryNameIntent);
        intent.putExtra("categoryId", categoryIdIntent);
        intent.putExtra("pinCode", pinCode);
        intent.putExtra("fssaiNumber",getfssaiNumber);
        //
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        IntentActivity();
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
        if (!((Activity) FilterActivity.this).isFinishing()) {
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