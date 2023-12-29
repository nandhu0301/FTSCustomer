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
import android.widget.AdapterViewFlipper;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.Adapter.ItemsAdapter;
import com.smiligenceTestenv.techUser.Adapter.ViewFlipperAdapter;
import com.smiligenceTestenv.techUser.bean.AdvertisementDetails;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;

import java.util.ArrayList;

import static com.smiligenceTestenv.techUser.common.Constant.ADVERTISEMENT_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.Available;
import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_ID;
import static com.smiligenceTestenv.techUser.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.VIEW_CART_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.WISHLIST_FIREBASE_TABLE;


public class WishListActivity extends AppCompatActivity  implements NetworkStateReceiver.NetworkStateReceiverListener
{
    ItemsAdapter itemsAdapter;
    DatabaseReference itemDataRef, advertisementref,wishListRef;

    ArrayList<AdvertisementDetails> advertisementDetailsObjectList = new ArrayList<>();
    RecyclerView itemRecyclerView;
    Query advertisementBannerQuery;
    AdvertisementDetails advertisementDetails;
    AdapterViewFlipper adapterViewFlipper;
    CardView advCardView;
    String categoryId;
    ImageView backbutton;
    ImageView cartIcon;
    TextView cartBadge;
    String categoryName;
    TextView categoryNameText;
    ItemDetails itemDetails;
    RelativeLayout filter, sort;
    ArrayList<ItemDetails> refinedItemList = new ArrayList<>();
    String saved_id;
    ImageView wishListIcon;

    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();



        setContentView(R.layout.activity_wish_list);
        wishListIcon=findViewById(R.id.wishlisticon);
        wishListIcon.setVisibility(View.INVISIBLE);
        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");
        itemRecyclerView = findViewById(R.id.categoryRecyclerView);
        adapterViewFlipper = findViewById(R.id.advflippers);
        advCardView = findViewById(R.id.adcard);
        backbutton = findViewById(R.id.backbutton);
        cartIcon = findViewById(R.id.carctIcon);
        cartBadge = findViewById(R.id.cart_badge);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        wishListRef = CommonMethods.fetchFirebaseDatabaseReference(WISHLIST_FIREBASE_TABLE).child(saved_id);
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);
        itemRecyclerView.setLayoutManager(new GridLayoutManager(WishListActivity.this, 1));
        itemRecyclerView.setHasFixedSize(true);
        itemRecyclerView.setNestedScrollingEnabled(false);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WishListActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WishListActivity.this, ViewCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        DatabaseReference databaseReference = CommonMethods.
                fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);

        databaseReference.orderByChild("itemStatus").equalTo(Available).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    cartBadge.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        wishListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0)
                {
                    refinedItemList.clear();
                    for (DataSnapshot wishSnap:dataSnapshot.getChildren())
                    {
                        itemDetails = wishSnap.getValue(ItemDetails.class);
                        itemDataRef.child(String.valueOf(itemDetails.getItemId())).addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getChildrenCount()>0)
                                {

                                    itemDetails = dataSnapshot.getValue(ItemDetails.class);
                                        if(Available.equalsIgnoreCase(itemDetails.getItemStatus()))
                                        {
                                            refinedItemList.add(itemDetails);
                                        }

                                    itemsAdapter = new ItemsAdapter(WishListActivity.this, refinedItemList);
                                    itemsAdapter.notifyDataSetChanged();
                                    itemRecyclerView.setAdapter(itemsAdapter);
                                }

                                if (itemsAdapter != null) {
                                    itemsAdapter.setOnItemclickListener(new ItemsAdapter.OnItemClicklistener() {
                                        @Override
                                        public void Onitemclick(int Position) {
                                            ItemDetails itemDetails = refinedItemList.get(Position);
                                            Intent intent = new Intent(WishListActivity.this, ProductDescriptionActivity.class);
                                            intent.putExtra("itemId", String.valueOf(itemDetails.getItemId()));
                                            intent.putExtra("categoryId", itemDetails.getCategoryId());
                                            intent.putExtra("itemName", itemDetails.getItemName());
                                            intent.putExtra("categoryName", itemDetails.getCategoryName());
                                            intent.putExtra(CUSTOMER_ID, saved_id);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    });
                                }

                                backbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(WishListActivity.this, HomePageActivity.class);
                                        intent.putExtra("categoryId", itemDetails.getCategoryId());
                                        intent.putExtra("categoryName", itemDetails.getCategoryName());
                                        startActivity(intent);
                                    }
                                });

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




        advertisementref = CommonMethods.fetchFirebaseDatabaseReference(ADVERTISEMENT_TABLE);
        advertisementBannerQuery = advertisementref.orderByChild("advertisementpriority").equalTo("1");

        advertisementBannerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot adSnap : dataSnapshot.getChildren()) {
                    advertisementDetails = adSnap.getValue(AdvertisementDetails.class);

                        if(Available.equalsIgnoreCase(advertisementDetails.getAdvertisementstatus())){
                            advertisementDetailsObjectList.add(advertisementDetails);
                        }

                }
                if (advertisementDetailsObjectList.size() > 0) {
                    advCardView.setVisibility(View.VISIBLE);
                    adapterViewFlipper.setAdapter(new ViewFlipperAdapter(WishListActivity.this, advertisementDetailsObjectList));

                    adapterViewFlipper.setFlipInterval(4000);
                    adapterViewFlipper.startFlipping();
                    adapterViewFlipper.setAutoStart(true);
                    adapterViewFlipper.setAnimateFirstView(false);
                } else {
                    advCardView.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WishListActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
        if (!((Activity) WishListActivity.this).isFinishing()) {
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