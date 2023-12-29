package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.make.dots.dotsindicator.DotsIndicator;
import com.smiligenceTestenv.techUser.Adapter.CategoryExpandableAdapter;
import com.smiligenceTestenv.techUser.Adapter.CategoryViewAdapter;
import com.smiligenceTestenv.techUser.Adapter.ReviewAdapter;
import com.smiligenceTestenv.techUser.Adapter.ViewPageAdapter;
import com.smiligenceTestenv.techUser.bean.AdvertisementDetails;
import com.smiligenceTestenv.techUser.bean.CategoryDetails;
import com.smiligenceTestenv.techUser.bean.ContactDetails;
import com.smiligenceTestenv.techUser.bean.DeliveryFareDetails;
import com.smiligenceTestenv.techUser.bean.Ingredients;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.ItemReviewAndRatings;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;
import com.smiligenceTestenv.techUser.common.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.smiligenceTestenv.techUser.common.Constant.ABOUT_US;
import static com.smiligenceTestenv.techUser.common.Constant.ADVERTISEMENT_PRIOROTY;
import static com.smiligenceTestenv.techUser.common.Constant.ADVERTISEMENT_PRIOROTY_1;
import static com.smiligenceTestenv.techUser.common.Constant.ADVERTISEMENT_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.Available;
import static com.smiligenceTestenv.techUser.common.Constant.CATEGORY_DETAILS_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.CONTACT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.DELIVERY_FARE_DETAILS;
import static com.smiligenceTestenv.techUser.common.Constant.PRIVACY_POLICY;
import static com.smiligenceTestenv.techUser.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.REVIEW_AND_RATING;
import static com.smiligenceTestenv.techUser.common.Constant.TERMS_AND_CONDITIONS;
import static com.smiligenceTestenv.techUser.common.Constant.VIEW_CART_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.TextUtils.removeDuplicatesList;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener, NetworkStateReceiver.NetworkStateReceiverListener{
    DatabaseReference categoryRef, advertisementref, itemDataRef;
    Query advertisementBannerQuery;
    AdvertisementDetails advertisementDetails;
    ItemDetails itemDetails;
    ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
    ArrayList<String> itemCategoryHeader = new ArrayList<>();
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList = new ArrayList<>();
    private ArrayList<ItemDetails> brands = new ArrayList<>();
    CardView advCardView;
    ViewPager viewpager;
    String categoryString;
    ArrayList<ItemDetails> refinedItemList = new ArrayList<>();
    Map<String, List<ItemDetails>> expandableBillDetail = new HashMap<>();
    ArrayList<ItemDetails> item = new ArrayList<>();
    int countervariable, counter = 1;
    ExpandableListView expandableListView;
    CategoryExpandableAdapter mCategoryExpandableAdapter;
    RecyclerView categoryRecyclerView;
    ArrayList<CategoryDetails> priority1catagoryList = new ArrayList<>();
    ArrayList<Ingredients> ingredientsArrayList = new ArrayList<>();
    CategoryDetails CategoryDetails;
    CategoryViewAdapter categoryAdapter;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;
    DotsIndicator dotsIndicator;
    String saved_id;
    public static String saved_userName;
    public static String saved_customerPhonenumber;
    TextView cartText;
    ImageView cartIcon;
    BottomNavigationView bottomNavigationView;
    RelativeLayout whatsappIcon;
    DatabaseReference contactRef, deliveryFareRef;
    CardView search;
    EditText searchText;
    DatabaseReference reviewRatingRef;
    ArrayList<ItemReviewAndRatings> reviewRatingArrayList = new ArrayList<>();
    ArrayList<ItemReviewAndRatings> top5ratingList = new ArrayList<>();
    ArrayList<String> stringKeyList = new ArrayList<>();
    ItemReviewAndRatings reviewAndRatings;
    ItemReviewAndRatings refinedReviewAndRatings;
    ReviewAdapter reviewAdapter;
    ListView reviewListView;
    TextView review;
    ImageView instaIntent, fbIntent, youTubeIntent;
    public static String whatsAppUrl, instaUrl, facebookUrl, youTubeUrl;
    TextView termsAndConditions, privacyPolicy;

    ArrayList<CategoryDetails> itemDetailsArrayList = new ArrayList<>();
    ArrayList<CategoryDetails> categoryList = new ArrayList<>();
    LinearLayout cardView;
    CardView reviewView;
    TextView aboutUsText;
    String yCeyonUri, ourPromiseUri;
    TextView freeshippingText, emailText, contactNumber;

    ImageView wishListIcon;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private AppUpdateManager appUpdateManager;
    private static final int RCQ = 100;
    private FirebaseAnalytics firebaseAnalytics;

    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Utils.getDatabase();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, HomePageActivity.this, RCQ);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

//        Bundle bundle = new Bundle();
//        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID,1);
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Vannila");
//        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        wishListIcon = findViewById(R.id.wishlisticon);
        cartIcon = findViewById(R.id.carctIcon);
        cartText = findViewById(R.id.cart_badge);
        bottomNavigationView = findViewById(R.id.drawerLayout);
        advCardView = findViewById(R.id.adcard);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        expandableListView = findViewById(R.id.list_brands);
        whatsappIcon = findViewById(R.id.whatsappIcon);
        viewpager = findViewById(R.id.viewpager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        search = findViewById(R.id.searchview);
        searchText = findViewById(R.id.searchtext);
        reviewListView = findViewById(R.id.reviewListView);
        review = findViewById(R.id.review);
        instaIntent = findViewById(R.id.insta);
        fbIntent = findViewById(R.id.fb);
        youTubeIntent = findViewById(R.id.utube);
        termsAndConditions = findViewById(R.id.termsandcondition);
        privacyPolicy = findViewById(R.id.privacypolicy);
        cardView = findViewById(R.id.cardview);
        reviewView = findViewById(R.id.reviewView);
        aboutUsText = findViewById(R.id.aboutustext);
        freeshippingText = findViewById(R.id.freeshippingText);
        emailText = findViewById(R.id.txtemail);
        contactNumber = findViewById(R.id.contactnumber);

        //  click = findViewById(R.id.click);

        if (Build.VERSION.SDK_INT > 27) {

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_customerPhonenumber = loginSharedPreferences.getString("customerPhoneNumber", "");
        saved_id = loginSharedPreferences.getString("customerId", "");

        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);
        advertisementref = CommonMethods.fetchFirebaseDatabaseReference(ADVERTISEMENT_TABLE);
        categoryRef = CommonMethods.fetchFirebaseDatabaseReference(CATEGORY_DETAILS_TABLE);
        reviewRatingRef = CommonMethods.fetchFirebaseDatabaseReference(REVIEW_AND_RATING);
        contactRef = CommonMethods.fetchFirebaseDatabaseReference(CONTACT_DETAILS_FIREBASE_TABLE);
        deliveryFareRef = CommonMethods.fetchFirebaseDatabaseReference(DELIVERY_FARE_DETAILS);

        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        categoryRecyclerView.setLayoutManager(RecyclerViewLayoutManager);

        advertisementBannerQuery = advertisementref.orderByChild(ADVERTISEMENT_PRIOROTY).equalTo(ADVERTISEMENT_PRIOROTY_1);

        advertisementref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot adSnap : dataSnapshot.getChildren()) {
                    advertisementDetails = adSnap.getValue(AdvertisementDetails.class);
                    if (Available.equalsIgnoreCase(advertisementDetails.getAdvertisementstatus())) {
                        advertisementDetailsObjectList.add(advertisementDetails);
                    }

                    if (advertisementDetailsObjectList.size() > 0) {
                        advCardView.setVisibility(View.VISIBLE);
                        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(HomePageActivity.this, advertisementDetailsObjectList);
                        viewpager.setAdapter(viewPageAdapter);
                        dotsIndicator.setViewPager(viewpager);
                    } else {
                        advCardView.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                priority1catagoryList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryDetails categoryDetails = postSnapshot.getValue(CategoryDetails.class);
                    priority1catagoryList.add(categoryDetails);

                }

                if (priority1catagoryList.size() > 0) {
                    categoryAdapter = new CategoryViewAdapter(HomePageActivity.this, priority1catagoryList);
                    HorizontalLayout
                            = new LinearLayoutManager(
                            HomePageActivity.this,
                            LinearLayoutManager.HORIZONTAL,
                            false);
                    categoryRecyclerView.setLayoutManager(HorizontalLayout);
                    categoryAdapter.notifyDataSetChanged();
                    categoryRecyclerView.setRecycledViewPool(viewPool);
                    categoryRecyclerView.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ///  click.setOnClickListener(HomePageActivity.this);
        //click.performClick();

        review.setVisibility(View.INVISIBLE);

        if (!TextUtils.isNumeric((saved_id))) {
            wishListIcon.setVisibility(View.INVISIBLE);
        }

        wishListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomePageActivity.this, WishListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });


        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    ContactDetails contactDetails = dataSnapshot.getValue(ContactDetails.class);
                    whatsAppUrl = "https://api.whatsapp.com/send?phone=" + "+91" + contactDetails.getWhatsAppContact();
                    instaUrl = contactDetails.getInstagramUrl();
                    facebookUrl = contactDetails.getFacebookUrl();
                    youTubeUrl = contactDetails.getYouTubeUrl();
                    String emailString = contactDetails.getEmail();
                    contactNumber.setText("+91-" + contactDetails.getWhatsAppContact());
                    emailText.setText(emailString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        deliveryFareRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DeliveryFareDetails deliveryFareDetails = dataSnapshot.getValue(DeliveryFareDetails.class);
                    freeshippingText.setText("Above ₹" + (deliveryFareDetails.getCartValue()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, DisclaimerActivity.class);
                intent.putExtra("IndicatorVariable", PRIVACY_POLICY);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomePageActivity.this, DisclaimerActivity.class);
                intent.putExtra("IndicatorVariable", TERMS_AND_CONDITIONS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });


        aboutUsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, DisclaimerActivity.class);
                intent.putExtra("IndicatorVariable", ABOUT_US);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        whatsappIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (whatsAppUrl != null) {

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(whatsAppUrl));
                    startActivity(i);
                }

            }
        });

        instaIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instaUrl != null) {
                    Intent intent = new Intent(HomePageActivity.this, webViewActivity.class);
                    intent.putExtra("linkedPath", instaUrl);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }


            }
        });

        fbIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (facebookUrl != null) {
                    Intent intent = new Intent(HomePageActivity.this, webViewActivity.class);
                    intent.putExtra("linkedPath", facebookUrl);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        youTubeIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (youTubeUrl != null) {
                    Intent intent = new Intent(HomePageActivity.this, webViewActivity.class);
                    intent.putExtra("linkedPath", youTubeUrl);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }

            }
        });
        searchText.clearFocus();

        searchText.setTextIsSelectable(false);
        searchText.setFocusable(false);

        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString("Test", "TEST");
                firebaseAnalytics.logEvent("LogEventTest", bundle);

                // First
                for (int i = 0; i < 3; i++) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Testloop1" + i, "TESTLOOP" + i);
                    firebaseAnalytics.logEvent("LogEventTestLoop" + i, bundle1);
                }

                // First
                for (int i = 0; i < 3; i++) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Secondloop1" + i, "SECONDLOOP");
                    firebaseAnalytics.logEvent("SecondEventTestLoop" + i, bundle1);
                }

                for (int i = 0; i < 3; i++) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Thirdloop1", "THIRDLOOP" + i);
                    firebaseAnalytics.logEvent("ThirdEventTestLoop" + i, bundle1);
                }

                for (int i = 0; i < 3; i++) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Fourthloop1", "FOURTHLOOP");
                    firebaseAnalytics.logEvent("FourthEventTestLoop" + i, bundle1);
                }

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("Test", "TEST");
                firebaseAnalytics.logEvent("LogEventTest", bundle);

                // First
                for (int i = 0; i < 3; i++) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Testloop1" + i, "TESTLOOP" + i);
                    firebaseAnalytics.logEvent("LogEventTestLoop" + i, bundle1);
                }

                // First
                for (int i = 0; i < 3; i++) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Secondloop1" + i, "SECONDLOOP");
                    firebaseAnalytics.logEvent("SecondEventTestLoop" + i, bundle1);
                }

                for (int i = 0; i < 3; i++) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Thirdloop1", "THIRDLOOP" + i);
                    firebaseAnalytics.logEvent("ThirdEventTestLoop" + i, bundle1);
                }

                for (int i = 0; i < 3; i++) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Fourthloop1", "FOURTHLOOP");
                    firebaseAnalytics.logEvent("FourthEventTestLoop" + i, bundle1);
                }
                Intent intent = new Intent(HomePageActivity.this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        reviewRatingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewRatingArrayList.clear();
                stringKeyList.clear();
                top5ratingList.clear();
                if (dataSnapshot.exists()) {
                    reviewRatingArrayList.clear();
                    for (DataSnapshot reviewSnap : dataSnapshot.getChildren()) {
                        ItemReviewAndRatings itemReviewAndRatings = reviewSnap.getValue(ItemReviewAndRatings.class);
                        if ("Approved".equalsIgnoreCase(itemReviewAndRatings.getItemRatingReviewStatus())) {
                            if (5 == itemReviewAndRatings.getStars() && itemReviewAndRatings.getReview() != null) {
                                reviewRatingArrayList.add(itemReviewAndRatings);

                            }
                        }

                    }

                    if (reviewRatingArrayList.isEmpty()) {
                        review.setTextSize(0);
                        review.setVisibility(View.INVISIBLE);
                        int height = 0;
                        LinearLayout.LayoutParams layout_param = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                height * 2);
                        reviewView.setLayoutParams(layout_param);


                    }
                    if (reviewRatingArrayList.size() < 5) {

                        reviewView.setVisibility(View.VISIBLE);
                        cardView.setVisibility(View.VISIBLE);


                        reviewAdapter = new ReviewAdapter(HomePageActivity.this, reviewRatingArrayList);
                        reviewAdapter.notifyDataSetChanged();
                        reviewListView.setAdapter(reviewAdapter);
                        review.setVisibility(View.VISIBLE);

                    } else if (reviewRatingArrayList.size() > 5) {
                        reviewView.setVisibility(View.VISIBLE);
                        cardView.setVisibility(View.VISIBLE);
                        Collections.reverse(reviewRatingArrayList);

                        top5ratingList.add(reviewRatingArrayList.get(0));
                        top5ratingList.add(reviewRatingArrayList.get(1));
                        top5ratingList.add(reviewRatingArrayList.get(2));
                        top5ratingList.add(reviewRatingArrayList.get(3));
                        top5ratingList.add(reviewRatingArrayList.get(4));

                        if (!top5ratingList.isEmpty() && top5ratingList.size() > 0 && top5ratingList.size() == 5) {
                            reviewAdapter = new ReviewAdapter(HomePageActivity.this, top5ratingList);
                            reviewAdapter.notifyDataSetChanged();
                            reviewListView.setAdapter(reviewAdapter);
                            review.setVisibility(View.VISIBLE);
                        }
                        top5ratingList = new ArrayList<>();
                    }


                    if (reviewAdapter != null) {
                        int totalHeight = 0;

                        for (int i = 0; i < reviewAdapter.getCount(); i++) {
                            View listItem = reviewAdapter.getView(i, null, reviewListView);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }

                        ViewGroup.LayoutParams params = reviewListView.getLayoutParams();
                        params.height = totalHeight + (reviewListView.getDividerHeight() * (reviewAdapter.getCount() - 1));
                        reviewListView.setLayoutParams(params);
                        reviewListView.requestLayout();
                        reviewListView.setAdapter(reviewAdapter);
                        reviewAdapter.notifyDataSetChanged();

                    }


                } else {
                    review.setTextSize(0);
                    review.setVisibility(View.INVISIBLE);
                    int height = 0;
                    LinearLayout.LayoutParams layout_param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            height * 2);
                    reviewView.setLayoutParams(layout_param);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        itemDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnap : dataSnapshot.getChildren()) {
                    itemDetails = itemSnap.getValue(ItemDetails.class);
                    itemDetailsList.add(itemDetails);
                    itemDetailsArrayList = itemDetails.getCategoryDetailsArrayList();
                    Iterator itemIterator = itemDetailsArrayList.iterator();

                    while (itemIterator.hasNext()) {

                        CategoryDetails categoryDetails = (CategoryDetails) itemIterator.next();
                        itemCategoryHeader.add(categoryDetails.getCategoryName());
                    }

                }

                removeDuplicatesList(itemCategoryHeader);
                if (itemCategoryHeader.size() > 0) {


                    for (int i = 0; i < itemCategoryHeader.size(); i++) {
                        categoryString = itemCategoryHeader.get(i);

                        for (int j = 0; j < itemDetailsList.size(); j++) {

                            ItemDetails userDetails = itemDetailsList.get(j);

                            categoryList = itemDetailsList.get(j).getCategoryDetailsArrayList();

                            Iterator itemIterator = categoryList.iterator();

                            while (itemIterator.hasNext()) {

                                CategoryDetails categoryDetails = (CategoryDetails) itemIterator.next();
                                if (categoryString.equalsIgnoreCase(categoryDetails.getCategoryName())) {
                                    if (Available.equalsIgnoreCase(userDetails.getItemStatus())) {
                                        refinedItemList.add(userDetails);
                                    }
                                }


                            }
                        }

                        if (itemDetailsList != null || !itemDetailsList.isEmpty()) {

                            // Collections.sort(refinedItemList);
                            expandableBillDetail.put(itemCategoryHeader.get(countervariable), refinedItemList);

                            ItemDetails itemDetails = new ItemDetails(itemCategoryHeader.get(countervariable), refinedItemList);
                            countervariable++;

                            brands.add(itemDetails);
                            refinedItemList = new ArrayList<>();


                        }


                        mCategoryExpandableAdapter = new CategoryExpandableAdapter(HomePageActivity.this, brands);
                        expandableListView.setAdapter(mCategoryExpandableAdapter);


                        if (counter == 1) {
                            expandableListView.performItemClick(null, 0, 0);
                        }


                        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                                i = 0;
                                setListViewHeightSales(expandableListView, i);
                                counter = 2;


                                return false;
                            }
                        });


                        for (int j = 0; j < mCategoryExpandableAdapter.getGroupCount(); j++) {
                            expandableListView.expandGroup(j);
                        }
                        expandableListView.performItemClick(null, 0, 0);
                        expandableListView.performItemClick(null, 0, 0);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomePageActivity.this, ViewCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

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

        DatabaseReference databaseReference = CommonMethods.
                fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);

        databaseReference.orderByChild("itemStatus").equalTo(Available).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    cartText.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View view) {

        expandFuc();
    }


    @Override
    public void onBackPressed() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomePageActivity.this);
        bottomSheetDialog.setContentView(R.layout.application_exiting_dialog);
        Button quit = bottomSheetDialog.findViewById(R.id.quit_dialog);
        Button cancel = bottomSheetDialog.findViewById(R.id.cancel_dialog);

        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                bottomSheetDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

    }

    private void setListViewHeightSales(ExpandableListView listView,
                                        int group) {
        if (mCategoryExpandableAdapter != null) {
            mCategoryExpandableAdapter = (CategoryExpandableAdapter) expandableListView.getExpandableListAdapter();
            int totalHeight = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                    View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mCategoryExpandableAdapter.getGroupCount(); i++) {
                View groupItem = mCategoryExpandableAdapter.getGroupView(i, false, null, listView);
                groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                totalHeight += groupItem.getMeasuredHeight();

                if (((listView.isGroupExpanded(i)) && (i != group))
                        || ((!listView.isGroupExpanded(i)) && (i == group))) {
                    for (int j = 0; j < mCategoryExpandableAdapter.getChildrenCount(i); j++) {
                        View listItem = mCategoryExpandableAdapter.getChildView(i, j, false, null,
                                listView);
                        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                        totalHeight += listItem.getMeasuredHeight();

                    }
                }
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            int height = totalHeight
                    + (listView.getDividerHeight() * (mCategoryExpandableAdapter.getGroupCount()));
            if (height < 10)
                height = 250;
            params.height = height;
            listView.setLayoutParams(params);
            listView.requestLayout();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void expandFuc() {

        expandableListView.performItemClick(null, 0, 0);
        expandableListView.performItemClick(null, 0, 0);
        counter = 2;


    }

    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState state) {

            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showCompletedUpdate();
            }
        }
    };


    private void showCompletedUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New app is ready", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, HomePageActivity.this, RCQ
                        );
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RCQ && resultCode != RESULT_OK) {
            Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        if (!((Activity) HomePageActivity.this).isFinishing()) {
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