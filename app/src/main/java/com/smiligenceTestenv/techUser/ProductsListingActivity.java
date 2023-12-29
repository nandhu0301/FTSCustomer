package com.smiligenceTestenv.techUser;

import android.annotation.SuppressLint;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.Adapter.ItemsAdapter;
import com.smiligenceTestenv.techUser.Adapter.ViewFlipperAdapter;
import com.smiligenceTestenv.techUser.bean.AdvertisementDetails;
import com.smiligenceTestenv.techUser.bean.CategoryDetails;
import com.smiligenceTestenv.techUser.bean.Filters;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.ItemReviewAndRatings;
import com.smiligenceTestenv.techUser.bean.Preferences;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;
import com.smiligenceTestenv.techUser.common.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.smiligenceTestenv.techUser.common.Constant.ADVERTISEMENT_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.Available;
import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_ID;
import static com.smiligenceTestenv.techUser.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.REVIEW_AND_RATING;
import static com.smiligenceTestenv.techUser.common.Constant.VIEW_CART_FIREBASE_TABLE;

public class ProductsListingActivity extends AppCompatActivity  implements NetworkStateReceiver.NetworkStateReceiverListener{
    ItemsAdapter itemsAdapter;
    DatabaseReference itemDataRef, advertisementref;
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
    RelativeLayout filter, sort;
    public static int counter = 0;
    public static String temp = "";
    String selected_sortValue;
    ArrayList<ItemDetails> refinedItemList = new ArrayList<ItemDetails>();
    ArrayList<String> itemIDList = new ArrayList<>();
    ArrayList<CategoryDetails> categoryDetailsArrayList = new ArrayList<>();
    String saved_id;
    String counterInd;
    ImageView wishListIcon;
    public static ArrayList<ItemReviewAndRatings> reviewAndRatingsList = new ArrayList<>();
    RadioGroup sortGroup;
    RadioButton sortbyname, sortprice_low_to_high, sortprice_high_to_low, sort_latest;
    BottomSheetDialog bottomSheetDialog;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_products_listing);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");
        itemRecyclerView = findViewById(R.id.categoryRecyclerView);
        adapterViewFlipper = findViewById(R.id.advflippers);
        advCardView = findViewById(R.id.adcard);
        backbutton = findViewById(R.id.backbutton);
        cartIcon = findViewById(R.id.carctIcon);
        cartBadge = findViewById(R.id.cart_badge);
        categoryNameText = findViewById(R.id.categoryname);
        sort = findViewById(R.id.sort);
        filter = findViewById(R.id.filter);

        wishListIcon = findViewById(R.id.wishlisticon);
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);
        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");
        categoryNameText.setText(categoryName);
        counterInd = getIntent().getStringExtra("counterInd");

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        if (!TextUtils.isNumeric((saved_id))) {
            wishListIcon.setVisibility(View.INVISIBLE);
        }
        wishListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WishListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        if (counterInd != null) {
            counter = Integer.parseInt(counterInd);
        }

        itemRecyclerView.setLayoutManager(new GridLayoutManager(ProductsListingActivity.this, 1));
        itemRecyclerView.setHasFixedSize(true);
        itemRecyclerView.setNestedScrollingEnabled(false);

        sortFunction();


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = "";
                counter = 0;


                if (Preferences.filters.size() != 0) {
                    Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());
                }
                Intent intent = new Intent(ProductsListingActivity.this, HomePageActivity.class);
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("categoryName", categoryName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                temp = "";
                counter = 0;


                if (Preferences.filters.size() != 0) {
                    Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());

                }

                Intent intent = new Intent(ProductsListingActivity.this, ViewCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        DatabaseReference databaseReference = CommonMethods.
                fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);

        databaseReference.orderByChild("itemStatus").equalTo(Available).addListenerForSingleValueEvent(new ValueEventListener() {
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

        itemDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (counter != 10) {
                    refinedItemList.clear();
                }

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final ItemDetails itemDetails = postSnapshot.getValue(ItemDetails.class);
                    categoryDetailsArrayList = itemDetails.getCategoryDetailsArrayList();

                    Iterator itemIterator = categoryDetailsArrayList.iterator();

                    while (itemIterator.hasNext()) {

                        CategoryDetails categoryDetails = (CategoryDetails) itemIterator.next();
                        if (categoryId.equalsIgnoreCase(categoryDetails.getCategoryid())) {
                            itemIDList.add(categoryDetails.getItemId());

                        }
                    }
                }

                refinedItemList.clear();
                for (int i = 0; i < itemIDList.size(); i++) {
                    itemDataRef.child(String.valueOf(itemIDList.get(i))).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                ItemDetails itemObjDetails = dataSnapshot.getValue(ItemDetails.class);
                                if (Available.equalsIgnoreCase(itemObjDetails.getItemStatus())) {
                                    refinedItemList.add(itemObjDetails);
                                }

                            }
                            if (refinedItemList.size() > 0) {


                                if (counter == 10) {

                                    initControls();
                                }

                                if (counter != 10) {
                                    itemsAdapter = new ItemsAdapter(ProductsListingActivity.this, refinedItemList);
                                    itemsAdapter.notifyDataSetChanged();
                                    itemRecyclerView.setAdapter(itemsAdapter);
                                }
                            }


                            if (itemsAdapter != null) {
                                itemsAdapter.setOnItemclickListener(new ItemsAdapter.OnItemClicklistener() {
                                    @Override
                                    public void Onitemclick(int Position) {
                                        temp = "";
                                        counter = 0;


                                        if (Preferences.filters.size() != 0) {
                                            Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());

                                        }
                                        ItemDetails itemDetails = refinedItemList.get(Position);
                                        Intent intent = new Intent(ProductsListingActivity.this, ProductDescriptionActivity.class);
                                        intent.putExtra("itemId", String.valueOf(itemDetails.getItemId()));
                                        intent.putExtra("categoryId", categoryId);
                                        intent.putExtra("itemName", itemDetails.getItemName());
                                        intent.putExtra("categoryName", categoryName);
                                        intent.putExtra(CUSTOMER_ID, saved_id);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
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

        advertisementref = CommonMethods.fetchFirebaseDatabaseReference(ADVERTISEMENT_TABLE);

        advertisementBannerQuery = advertisementref.orderByChild("advertisementpriority").equalTo("2");
        advertisementBannerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot adSnap : dataSnapshot.getChildren()) {
                    advertisementDetails = adSnap.getValue(AdvertisementDetails.class);
                    if (categoryId.equalsIgnoreCase(advertisementDetails.getCategoryId())) {
                        if (Available.equalsIgnoreCase(advertisementDetails.getAdvertisementstatus())) {
                            advertisementDetailsObjectList.add(advertisementDetails);
                        }
                    }
                }

                if (advertisementDetailsObjectList.size() > 0) {
                    advCardView.setVisibility(View.VISIBLE);
                    adapterViewFlipper.setAdapter(new ViewFlipperAdapter(ProductsListingActivity.this, advertisementDetailsObjectList));
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
        bottomSheetDialog = new BottomSheetDialog(ProductsListingActivity.this);
        bottomSheetDialog.setContentView(R.layout.sort_dialog);

        sortGroup = bottomSheetDialog.findViewById(R.id.sort_radiogroup);
        sortbyname = bottomSheetDialog.findViewById(R.id.sort_name);
        sortprice_low_to_high = bottomSheetDialog.findViewById(R.id.sort_lowtohigh);
        sortprice_high_to_low = bottomSheetDialog.findViewById(R.id.sort_hightolow);
        sort_latest = bottomSheetDialog.findViewById(R.id.sort_latest);
        if (counter == 0) {
            sort_latest.setChecked(false);
            sortbyname.setChecked(false);
            sortprice_high_to_low.setChecked(false);
            sortprice_low_to_high.setChecked(false);
        }

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProductsListingActivity.this);
                bottomSheetDialog.setContentView(R.layout.sort_dialog);

                sortGroup = bottomSheetDialog.findViewById(R.id.sort_radiogroup);
                sortbyname = bottomSheetDialog.findViewById(R.id.sort_name);
                sortprice_low_to_high = bottomSheetDialog.findViewById(R.id.sort_lowtohigh);
                sortprice_high_to_low = bottomSheetDialog.findViewById(R.id.sort_hightolow);
                sort_latest = bottomSheetDialog.findViewById(R.id.sort_latest);


                if (temp.equals("Sort by Name")) {
                    sortbyname.setChecked(true);
                } else if (temp.equals("Price-Low to high")) {
                    sortprice_low_to_high.setChecked(true);
                } else if (temp.equals("Price-High to Low")) {
                    sortprice_high_to_low.setChecked(true);
                } else if (temp.equals("Latest on Top")) {
                    sort_latest.setChecked(true);
                }

                sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        checkedId = sortGroup.getCheckedRadioButtonId();


                        RadioButton radioButton = (RadioButton) bottomSheetDialog.findViewById(checkedId);
                        selected_sortValue = radioButton.getText().toString();


                        if (selected_sortValue.equals("Sort by Name")) {

                            temp = "Sort by Name";
                            counter = 1;

                            sortFunction();
                            radioButton.setChecked(true);
                            sortbyname.setChecked(true);
                            if (itemsAdapter != null) {

                                itemsAdapter.notifyDataSetChanged();
                            }
                            bottomSheetDialog.dismiss();

                        } else if (selected_sortValue.equals("Price-Low to high")) {
                            temp = "Price-Low to high";
                            counter = 2;
                            sortFunction();
                            if (itemsAdapter != null) {

                                itemsAdapter.notifyDataSetChanged();
                            }

                            bottomSheetDialog.dismiss();


                        } else if (selected_sortValue.equals("Price-High to Low")) {
                            temp = "Price-High to Low";

                            counter = 3;
                            sortFunction();

                            if (itemsAdapter != null) {

                                itemsAdapter.notifyDataSetChanged();
                            }

                            bottomSheetDialog.dismiss();
                        } else if (selected_sortValue.equals("Latest on Top")) {
                            temp = "Latest on Top";

                            counter = 4;
                            sortFunction();

                            sortGroup.getCheckedRadioButtonId();
                            radioButton.setChecked(true);
                            bottomSheetDialog.dismiss();
                        }
                        if (itemsAdapter != null) {

                            itemsAdapter.notifyDataSetChanged();
                        }


                    }

                });

                int width = (int) (ProductsListingActivity.this.getResources().getDisplayMetrics().widthPixels * 0.6);
                int height = (int) (ProductsListingActivity.this.getResources().getDisplayMetrics().heightPixels * 0.5);
                bottomSheetDialog.getWindow().setLayout(width, height);
                bottomSheetDialog.show();
                bottomSheetDialog.setCancelable(true);

            }
        });


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 10;
                initControls();
                Intent intent = new Intent(ProductsListingActivity.this, FilterActivity.class);
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("categoryName", categoryName);
                intent.putExtra("counterInd", counter);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
    }

    private void sortFunction() {


        if (counter == 1) {

            Collections.sort(refinedItemList, new Comparator<ItemDetails>() {
                @Override
                public int compare(ItemDetails o1, ItemDetails o2) {
                    return o1.getItemName().compareTo(o2.getItemName());

                }

            });
            if (itemsAdapter != null) {

                itemsAdapter.notifyDataSetChanged();
            }
            temp = "Sort by Name";
            counter = 1;
        } else if (counter == 2) {

            Collections.sort(refinedItemList, new Comparator<ItemDetails>() {
                @Override
                public int compare(ItemDetails o1, ItemDetails o2) {
                    return o1.getItemPrice() - o2.getItemPrice();
                }
            });
            if (itemsAdapter != null) {


                itemsAdapter.notifyDataSetChanged();

            }
            counter = 2;
            temp = "Price-Low to high";
        } else if (counter == 3) {

            Collections.sort(refinedItemList, new Comparator<ItemDetails>() {
                @Override
                public int compare(ItemDetails o1, ItemDetails o2) {
                    return o2.getItemPrice() - o1.getItemPrice();

                }
            });
            if (itemsAdapter != null) {


                itemsAdapter.notifyDataSetChanged();

            }
            counter = 3;
            temp = "Price-High to Low";
        } else if (counter == 4) {
            Collections.sort(refinedItemList, new Comparator<ItemDetails>() {
                @Override
                public int compare(ItemDetails o1, ItemDetails o2) {
                    return o2.getItemId() - o1.getItemId();
                }
            });
            if (itemsAdapter != null) {

                itemsAdapter.notifyDataSetChanged();

            }
            counter = 4;
            temp = "Latest on Top";
        }
    }

    private void initControls() {
        if (counter == 10) {

            if (refinedItemList != null || !refinedItemList.isEmpty()) {

                ArrayList<ItemDetails> filteredItems = new ArrayList<>();

                if (!Preferences.filters.isEmpty()) {

                    List<String> prices = Preferences.filters.get(Filters.INDEX_PRICE).getSelected();

                    for (ItemDetails item : refinedItemList) {

                        boolean priceMatched = true;

                        if (prices.size() > 0 && !priceContains(prices, item.getItemPrice())) {
                            priceMatched = false;
                        }
                        if (priceMatched) {
                            filteredItems.add(item);
                        }
                    }
                    refinedItemList = filteredItems;

                    itemsAdapter = new ItemsAdapter(ProductsListingActivity.this, refinedItemList);
                    itemsAdapter.notifyDataSetChanged();
                    itemRecyclerView.setAdapter(itemsAdapter);
                }


            }

        }


    }


    @Override
    public void onBackPressed() {
        temp = "";
        counter = 0;
        if (Preferences.filters.size() != 0) {
            Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());

        }
        Intent intent = new Intent(ProductsListingActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private boolean priceContains(List<String> prices, Integer price) {
        boolean flag = false;
        for (String p : prices) {
            String[] tmpPrices = p.split("-");
            if (price >= Integer.parseInt(tmpPrices[0]) && price <= Integer.parseInt(tmpPrices[1])) {


                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference reviewRatingItem;

        reviewRatingItem = CommonMethods.fetchFirebaseDatabaseReference(REVIEW_AND_RATING);

        reviewRatingItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    reviewAndRatingsList.clear();
                    for (DataSnapshot reviewSnap : dataSnapshot.getChildren()) {
                        ItemReviewAndRatings itemReviewAndRatings = reviewSnap.getValue(ItemReviewAndRatings.class);
                        if ("Approved".equalsIgnoreCase(itemReviewAndRatings.getItemRatingReviewStatus())) {
                            reviewAndRatingsList.add(itemReviewAndRatings);
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
        if (!((Activity) ProductsListingActivity.this).isFinishing()) {
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