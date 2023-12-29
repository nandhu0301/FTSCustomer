package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.Adapter.SearchAdapter;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.UserDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import static com.smiligenceTestenv.techUser.common.Constant.Available;
import static com.smiligenceTestenv.techUser.common.Constant.CATEGORY_DETAILS_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.CATEGORY_NAME;
import static com.smiligenceTestenv.techUser.common.Constant.PINCODE;
import static com.smiligenceTestenv.techUser.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.SELLER_DETAILS;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, NetworkStateReceiver.NetworkStateReceiverListener {
    SearchView searchView;
    ArrayList<ItemDetails> search_itemDetailsList = new ArrayList<>();
    ArrayList<ItemDetails> search_storeDetailsList = new ArrayList<>();
    DatabaseReference itemDataRef, categoryDataRef, sellerDetailRef, storeTimingDataRef, metrozStoteTimingDataRef;
    ItemDetails itemDetails;
    RecyclerView itemListView, storeNameListView;
    List<UserDetails> sellerList = new ArrayList<>();
    ArrayList<ItemDetails> search_items = new ArrayList<>();
    ArrayList<ItemDetails> search_Items = new ArrayList<>();
    BottomNavigationView bottomNavigationView;
    SearchAdapter itemDetailsAdapter, storeDetailAdapter;
    String pincodeCodeIntent;

    ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
    ArrayList<String> closedStoreIdList = new ArrayList<>();
    HashMap<String, ItemDetails> storeList;
    HashMap<String, ItemDetails> itemFilteredList;
    int innerLoopsize;
    int outerLoopSize;
    boolean sizeCheckForOpenedStore = false;
    List<UserDetails> openStoresSellerList = new ArrayList<>();
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime;
    String metrozStopTime;
    public static String DATE_FORMAT = "MMMM dd, YYYY";
    int getDistance;
    double getUserLatitude, getUserLongtitude, roundOff = 0.0;
    boolean check = true;
    String searchIndicator, categoryNameIntent;
    SearchAdapter searchitemDetailsAdapter;
    RecyclerView recyclerView;

    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();

        setContentView(R.layout.activity_search);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        searchView = findViewById(R.id.searchbar);
        itemListView = findViewById(R.id.itemListView);
        bottomNavigationView = findViewById(R.id.drawerLayout);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        checkGPSConnection(getApplicationContext());


        pincodeCodeIntent = getIntent().getStringExtra(PINCODE);
        searchIndicator = getIntent().getStringExtra("SearchIndicator");

        categoryNameIntent = getIntent().getStringExtra(CATEGORY_NAME);

        categoryDataRef = CommonMethods.fetchFirebaseDatabaseReference(CATEGORY_DETAILS_TABLE);
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);
        sellerDetailRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        searchView.setOnCloseListener((SearchView.OnCloseListener) this);
        searchView.requestFocus();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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


        itemListView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 1));
        itemListView.setHasFixedSize(true);
        itemDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    itemDetailsList.clear();
                    for (DataSnapshot searchSnap : dataSnapshot.getChildren()) {
                        ItemDetails itemDetails = searchSnap.getValue(ItemDetails.class);
                        if(Available.equalsIgnoreCase(itemDetails.getItemStatus()))
                        {
                            itemDetailsList.add(itemDetails);
                        }
                    }
                    itemDetailsAdapter = new SearchAdapter(SearchActivity.this, itemDetailsList);
                    itemListView.setAdapter(itemDetailsAdapter);
                    itemDetailsAdapter.notifyDataSetChanged();
                    itemDetailsAdapter.setOnItemclickListener(new SearchAdapter.OnItemClicklistener() {
                        @Override
                        public void Onitemclick(int Position) {
                            ItemDetails itemDetails = itemDetailsList.get(Position);
                            navigateProductDiscription(itemDetails);
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

    @Override
    public boolean onClose() {
        filterData("");
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        filterData(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        filterData(s);
        return false;
    }

    public void filterData(String query) {
        query = query.toLowerCase();
        search_items.clear();

        if (query.isEmpty()) {
            search_items.addAll(itemDetailsList);
        } else {
            for (ItemDetails itemDetails : itemDetailsList) {
                if (itemDetails.getItemName().toLowerCase().contains(query)) {
                    search_items.add(itemDetails);

                }
            }
            if (search_items.size() > 0) {
                searchitemDetailsAdapter = new SearchAdapter(SearchActivity.this, search_items);
                itemListView.setAdapter(searchitemDetailsAdapter);

                searchitemDetailsAdapter.setOnItemclickListener(new SearchAdapter.OnItemClicklistener() {
                    @Override
                    public void Onitemclick(int Position) {
                        ItemDetails itemDetails = search_items.get(Position);
                        navigateProductDiscription(itemDetails);

                    }
                });
            }
        }

        if (searchitemDetailsAdapter != null) {
            searchitemDetailsAdapter.notifyDataSetChanged();
        }




    }
    private  void  navigateProductDiscription(ItemDetails itemDetails){
        Intent intent=new Intent(SearchActivity.this,ProductDescriptionActivity.class);
        intent.putExtra("itemId", String.valueOf(itemDetails.getItemId()));
        intent.putExtra("categoryId", String.valueOf(itemDetails.getCategoryId()));
        intent.putExtra("itemName", itemDetails.getItemName());
        intent.putExtra("categoryName", itemDetails.getCategoryName());
        startActivity(intent);

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent( SearchActivity.this, HomePageActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    @Override
    protected void onStart() {
        super.onStart();

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
        if (!((Activity) SearchActivity.this).isFinishing()) {
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