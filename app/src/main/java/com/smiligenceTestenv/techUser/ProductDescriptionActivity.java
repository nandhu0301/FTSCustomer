package com.smiligenceTestenv.techUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.shockwave.pdfium.PdfDocument;
import com.smiligenceTestenv.techUser.Adapter.QuantityVarientAdapter;
import com.smiligenceTestenv.techUser.Adapter.ReviewAdapter;
import com.smiligenceTestenv.techUser.Adapter.ViewPageAdapterAdvertisements;
import com.smiligenceTestenv.techUser.bean.CustomerDetails;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.ItemReviewAndRatings;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.DateUtils;
import com.smiligenceTestenv.techUser.common.NetworkStateReceiver;
import com.smiligenceTestenv.techUser.common.TextUtils;
import com.smiligenceTestenv.techUser.common.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceTestenv.techUser.common.Constant.Available;
import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_ID;
import static com.smiligenceTestenv.techUser.common.Constant.INACTIVE_STATUS;
import static com.smiligenceTestenv.techUser.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.VIEW_CART_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.WISHLIST_FIREBASE_TABLE;

public class ProductDescriptionActivity extends AppCompatActivity implements OnPageChangeListener, NetworkStateReceiver.NetworkStateReceiverListener{
    ImageView productImage;
    DatabaseReference itemDataRef, viewCartdatabaseRef;
    String itemId;
    Integer increamentId = 1;
    ImageView addQty, decreaseQty;
    TextView qtyText;
    int qtycount = 1;
    ItemDetails itemDetails;
    RelativeLayout purchasedetailslayout;
    FirebaseAuth mAuth;
    String verificationId;
    int amount = 0;
    long items = 0;
    SweetAlertDialog errorDialog;
    TextView itemCount, totalAmount;
    Button okOTPTextButton;
    EditText otpEdittext;
    TextView resendOTPTimer;
    int itemLimitation;
    ReviewAdapter reviewAdapter;
    LinearLayout add_decreaseLayout;
    RelativeLayout addtoCartLayout;
    ImageView backbutton;
    TextView itemNameTextView, mrpPriceTextView, itemPriceTextView, textView;
    ImageView cartIcon;
    String categoryId;

    public static int PrefIndicator = 3;
    Button viewcartIntent;
    DatabaseReference reviewRatingItem;
    CustomerDetails sellerLoginDetails1 = new CustomerDetails();
    DatabaseReference customerDetailsDataRef;
    ArrayList<String> imageUri = new ArrayList<>();
    ArrayList<ItemDetails> imageUriObject = new ArrayList<>();
    ArrayList<String> numberOfOrders = new ArrayList<>();
    ArrayList<String> reviewList = new ArrayList<>();
    float tempNumberOfStars = 0;
    double ratingbarResult;
    TextView rating, review;
    RatingBar storeRating;
    ListView reviewListview;
    ItemReviewAndRatings itemReviewAndRatings;
    ArrayList<ItemReviewAndRatings> itemReviewAndRatingsArrayList = new ArrayList<>();
    ArrayList<ItemReviewAndRatings> ratingListWithReviewList = new ArrayList<>();
    String getstoreNameIntent;
    ViewPager viewPager;
    DotsIndicator dotsIndicator;
    String itemName;
    TextView itemNameText;
    String categoryName;
    ArrayList<ItemDetails> qtyVarientsList = new ArrayList<>();
    RecyclerView quantityVarientListView;

    LinearLayoutManager HorizontalLayout;
    ItemDetails mergedItemDetails;
    int counter;
    public static int indicator = 0;
    public static String itemidString;
    String ingredientIndicator;
    String ingredientName;
    String statusString;
    String adapterIntent;

    String saved_id;
    ImageView wishlist, removeWishlist;
    DatabaseReference wishListRef;
    EditText phoneNumberEditText;
    ItemDetails itemDetailsNew;
    boolean check = true;

    DatabaseReference databaseReference;
    ImageView wishListIcon;
    TextView itemQtyTextView;
    LinearLayout qtyvarientLayout;
    LinearLayout reviewLayout;
    public static final String SAMPLE_FILE = "android_tutorial.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    TextView brandName, modelName;
    WebView loadSpecifications;
    BottomSheetDialog bottomSheetDialog;

    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_product_description);
        // pdfView= (PDFView)findViewById(R.id.pdfView);
        //displayFromAsset(SAMPLE_FILE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");

        wishListIcon = findViewById(R.id.wishlisticon);
        review = findViewById(R.id.review);
        rating = findViewById(R.id.rating);
        productImage = findViewById(R.id.productimage);
        backbutton = findViewById(R.id.backbutton);
        addQty = findViewById(R.id.increaseqty);
        decreaseQty = findViewById(R.id.decreaseqty);
        qtyText = findViewById(R.id.qtyText);
        totalAmount = findViewById(R.id.totalamount);
        itemCount = findViewById(R.id.items);
        storeRating = findViewById(R.id.storeRating);
        purchasedetailslayout = findViewById(R.id.purchasesheet);
        add_decreaseLayout = findViewById(R.id.add_decrease);
        addtoCartLayout = findViewById(R.id.addcart);
        itemNameTextView = findViewById(R.id.itemName);
        mrpPriceTextView = findViewById(R.id.mrpPrice);
        itemPriceTextView = findViewById(R.id.itemPrice);
        textView = findViewById(R.id.cart_badge);
        cartIcon = findViewById(R.id.carctIcon);
        viewcartIntent = findViewById(R.id.viewcartIntent);
        reviewListview = findViewById(R.id.reviewListView);
        itemNameText = findViewById(R.id.itemNameText);
        brandName = findViewById(R.id.brandNameTextview);
        modelName = findViewById(R.id.modelNameTextview);
        loadSpecifications = findViewById(R.id.webView);
        quantityVarientListView = findViewById(R.id.listview);

        // purchasedetailslayout.setVisibility(View.VISIBLE);

        removeWishlist = findViewById(R.id.removeWishlist);
        wishlist = findViewById(R.id.wishlist);
        mAuth = FirebaseAuth.getInstance();
        itemQtyTextView = findViewById(R.id.itemQtyTextView);
        qtyvarientLayout = findViewById(R.id.qtyvarientLayout);
        reviewLayout = findViewById(R.id.reviewLayout);

        viewPager = findViewById(R.id.viewpager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        viewPager.setVisibility(View.VISIBLE);
        itemId = getIntent().getStringExtra("itemId");
        categoryId = getIntent().getStringExtra("categoryId");
        itemName = getIntent().getStringExtra("itemName");
        categoryName = getIntent().getStringExtra("categoryName");
        ingredientIndicator = getIntent().getStringExtra("ingredientIndicator");
        ingredientName = getIntent().getStringExtra("Ingredient");
        adapterIntent = getIntent().getStringExtra("adapterIntent");

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        itemNameText.setText(itemName);

        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);
        viewCartdatabaseRef = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE);
        customerDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DETAILS_FIREBASE_TABLE);
        wishListRef = CommonMethods.fetchFirebaseDatabaseReference(WISHLIST_FIREBASE_TABLE).child(saved_id);
        itemidString = itemId;


        //harcoded
        //  WebView webView=findViewById(R.id.webView);
        //   webView.loadUrl("https://firebasestorage.googleapis.com/v0/b/kidsdevelopmentnew.appspot.com/o/Yashi8%3A17%20PM.html?alt=media&token=f09dbc64-603d-48a2-b60e-d0f4b2b55c8f");

        wishListRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    removeWishlist.setVisibility(View.VISIBLE);
                    wishlist.setVisibility(View.INVISIBLE);
                } else {
                    removeWishlist.setVisibility(View.INVISIBLE);
                    wishlist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        itemDataRef.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    itemDetails = dataSnapshot.getValue(ItemDetails.class);
                    wishlist.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                    Settings.Secure.ANDROID_ID);

                            if (android_id.equals(saved_id)) {

                                final BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(ProductDescriptionActivity.this);
                                bottomSheetDialog1.setContentView(R.layout.phone_number_bottotm_sheet_dialog);
                                int width = (int) (ProductDescriptionActivity.this.getResources().getDisplayMetrics().widthPixels * 0.6);
                                int height = (int) (ProductDescriptionActivity.this.getResources().getDisplayMetrics().heightPixels * 0.4);
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
                                            bottomSheetDialog = new BottomSheetDialog(ProductDescriptionActivity.this);
                                            bottomSheetDialog.setContentView(R.layout.otp_bottom_sheet_layout);
                                            int width = (int) (ProductDescriptionActivity.this.getResources().getDisplayMetrics().widthPixels * 0.6);
                                            int height = (int) (ProductDescriptionActivity.this.getResources().getDisplayMetrics().heightPixels * 0.4);
                                            bottomSheetDialog.getWindow().setLayout(width, height);
                                            bottomSheetDialog.show();
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
                                wishListRef = CommonMethods.fetchFirebaseDatabaseReference(WISHLIST_FIREBASE_TABLE).child(saved_id);
                                wishListRef.child(itemId).setValue(itemDetails);
                                wishListRef.child(itemId).child("itemCounter").setValue(1);
                                wishListRef.child(itemId).child("itemBuyQuantity").setValue(1);
                                wishListRef.child(itemId).child("totalItemQtyPrice").setValue(itemDetails.getItemPrice());
                                wishListRef.child(itemId).child("totalTaxPrice").setValue(itemDetails.getTaxPrice());
                                removeWishlist.setVisibility(View.VISIBLE);
                                wishlist.setVisibility(View.INVISIBLE);


                                if (!((Activity) ProductDescriptionActivity.this).isFinishing()) {
                                    errorDialog = new SweetAlertDialog(ProductDescriptionActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    errorDialog.setCancelable(false);

                                    errorDialog
                                            .setContentText("Added to wishlist").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            errorDialog.dismiss();
                                        }
                                    }).show();
                                    Button btn = (Button) errorDialog.findViewById(R.id.confirm_button);
                                    btn.setBackgroundColor(ContextCompat.getColor(ProductDescriptionActivity.this, R.color.colorPrimary));
                                }
                            }

                        }
                    });
                    removeWishlist.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            wishListRef = CommonMethods.fetchFirebaseDatabaseReference(WISHLIST_FIREBASE_TABLE).child(saved_id);
                            wishListRef.child(itemId).removeValue();

                            removeWishlist.setVisibility(View.INVISIBLE);
                            wishlist.setVisibility(View.VISIBLE);
                            if (!((Activity) ProductDescriptionActivity.this).isFinishing()) {
                                errorDialog = new SweetAlertDialog(ProductDescriptionActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                errorDialog.setCancelable(false);
                                errorDialog
                                        .setContentText("Removed from wishlist").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        errorDialog.dismiss();
                                    }

                                });
                                errorDialog.show();
                                Button btn = (Button) errorDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(ProductDescriptionActivity.this, R.color.colorPrimary));
                            }

                        }
                    });


                    qtycount = Integer.parseInt(qtyText.getText().toString());
                    itemLimitation = itemDetails.getItemMaxLimitation();
                    statusString = itemDetails.getItemStatus();

                    if (itemDetails.getImageUriList() != null || !itemDetails.getImageUriList().isEmpty()) {
                        Iterator itemIterator = itemDetails.getImageUriList().iterator();
                        imageUri.add(itemDetails.getItemImage());
                        while (itemIterator.hasNext()) {
                            String key = (String) itemIterator.next();

                            imageUri.add(key);
                        }
                    }


                    ViewPageAdapterAdvertisements viewPageAdapter = new ViewPageAdapterAdvertisements(ProductDescriptionActivity.this, imageUri);
                    viewPager.setAdapter(viewPageAdapter);
                    dotsIndicator.setViewPager(viewPager);

                    if (qtycount == 0) {

                        addtoCartLayout.setVisibility(View.VISIBLE);
                        add_decreaseLayout.setVisibility(View.INVISIBLE);
                    } else if (qtycount > 0) {
                        addtoCartLayout.setVisibility(View.INVISIBLE);
                        add_decreaseLayout.setVisibility(View.VISIBLE);
                    }


                    if (imageUri.isEmpty()) {
                        productImage.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.INVISIBLE);
                        Glide.with(ProductDescriptionActivity.this).load(itemDetails.getItemImage()).into(productImage);
                    }
                    qtyText.setText(String.valueOf(itemDetails.getItemBuyQuantity()));
                    itemNameTextView.setText(itemDetails.getItemName());
                    itemPriceTextView.setText("₹ " + itemDetails.getItemPrice());
                    mrpPriceTextView.setText("₹ " + itemDetails.getMRP_Price());
                    mrpPriceTextView.setPaintFlags(mrpPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    itemQtyTextView.setText(itemDetails.getItemQuantity());
                    brandName.setText("Brand :" + itemDetails.getBrandName());
                    modelName.setText("Model :" + itemDetails.getModelName());

                    if (itemDetails.getDescriptionUrl() != null && !itemDetails.getDescriptionUrl().equals("")) {
                        loadSpecifications.loadUrl(itemDetails.getDescriptionUrl());
                    } else {
                        loadSpecifications.loadUrl("");
                    }

                    if (itemDetails.getSkuVariant() != null) {
                        int height = 0;
                        LinearLayout.LayoutParams layout_param = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                height * 2);
                        qtyvarientLayout.setLayoutParams(layout_param);
                        itemDataRef.orderByChild("skuVariant").equalTo(itemDetails.getSkuVariant()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                qtyVarientsList.clear();
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot mergeSnap : dataSnapshot.getChildren()) {
                                        ItemDetails mergeItemDetails = mergeSnap.getValue(ItemDetails.class);
                                        qtyVarientsList.add(mergeItemDetails);

                                    }
                                    QuantityVarientAdapter quantityVarientAdapter = new QuantityVarientAdapter(ProductDescriptionActivity.this
                                            , qtyVarientsList);
                                    HorizontalLayout
                                            = new LinearLayoutManager(
                                            ProductDescriptionActivity.this,
                                            LinearLayoutManager.HORIZONTAL,
                                            false);
                                    quantityVarientListView.setLayoutManager(HorizontalLayout);
                                    quantityVarientAdapter.notifyDataSetChanged();
                                    quantityVarientListView.setAdapter(quantityVarientAdapter);


                                    if (quantityVarientAdapter != null) {

                                        quantityVarientAdapter.setOnItemclickListener(new QuantityVarientAdapter.OnItemClicklistener() {
                                            @Override
                                            public void Onitemclick(int Position) {
                                                indicator = 10;
                                                ItemDetails itemDetails = qtyVarientsList.get(Position);

                                                if (!(itemId.equalsIgnoreCase(String.valueOf(itemDetails.getItemId())))) {
                                                    Intent intent = new Intent(ProductDescriptionActivity.this, ProductDescriptionActivity.class);
                                                    intent.putExtra("itemId", String.valueOf(itemDetails.getItemId()));
                                                    intent.putExtra("categoryId", itemDetails.getCategoryId());
                                                    intent.putExtra("itemName", itemDetails.getItemName());
                                                    intent.putExtra("categoryName", itemDetails.getCategoryName());
                                                    intent.putExtra(CUSTOMER_ID, saved_id);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    startActivity(intent);
                                                }


                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        itemQtyTextView.setText(itemDetails.getItemQuantity());
                    }

                    addtoCartLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            qtycount = 1;
                            qtyText.setText("1");
                            addtoCartLayout.setVisibility(View.INVISIBLE);
                            add_decreaseLayout.setVisibility(View.VISIBLE);
                            itemDetails.setItemBuyQuantity(1);
                            itemDetails.setTotalTaxPrice(itemDetails.getTaxPrice() * qtycount);
                            itemDetails.setTotalItemQtyPrice(itemDetails.getItemPrice());

                            viewCartdatabaseRef.
                                    child(saved_id).child(itemId).setValue(itemDetails);
                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        reviewRatingItem = CommonMethods.fetchFirebaseDatabaseReference("ItemRatingsAndReview");

        reviewRatingItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    numberOfOrders.clear();
                    reviewList.clear();
                    itemReviewAndRatingsArrayList.clear();
                    ratingListWithReviewList.clear();

                    for (DataSnapshot ratingSnap : dataSnapshot.getChildren()) {
                        itemReviewAndRatings = ratingSnap.getValue(ItemReviewAndRatings.class);

                        if (itemId.equalsIgnoreCase(itemReviewAndRatings.getItemId())) {

                            numberOfOrders.add(itemReviewAndRatings.getItemId());

                            if ("Approved".equalsIgnoreCase(itemReviewAndRatings.getItemRatingReviewStatus())) {
                                reviewList.add(itemReviewAndRatings.getReview());
                                itemReviewAndRatingsArrayList.add(itemReviewAndRatings);
                                if (itemReviewAndRatings.getReview() != null && !itemReviewAndRatings.getReview().equals("")) {
                                    ratingListWithReviewList.add(itemReviewAndRatings);
                                }
                            }
                            if ("Approved".equalsIgnoreCase(itemReviewAndRatings.getItemRatingReviewStatus())) {
                                tempNumberOfStars = tempNumberOfStars + itemReviewAndRatings.getStars();
                            }
                        }
                        if (!ratingListWithReviewList.isEmpty() && ratingListWithReviewList.size() > 0) {
                            reviewAdapter = new ReviewAdapter(ProductDescriptionActivity.this, ratingListWithReviewList);
                            reviewAdapter.notifyDataSetChanged();
                            reviewListview.setAdapter(reviewAdapter);
                            review.setVisibility(View.VISIBLE);
                        }

                        if (reviewAdapter != null) {

                            int totalHeight = 0;

                            for (int i = 0; i < reviewAdapter.getCount(); i++) {
                                View listItem = reviewAdapter.getView(i, null, reviewListview);
                                listItem.measure(0, 0);
                                totalHeight += listItem.getMeasuredHeight();
                            }

                            ViewGroup.LayoutParams params = reviewListview.getLayoutParams();
                            params.height = totalHeight + (reviewListview.getDividerHeight() * (reviewAdapter.getCount() - 1));
                            reviewListview.setLayoutParams(params);
                            reviewListview.requestLayout();
                            reviewListview.setAdapter(reviewAdapter);
                            reviewAdapter.notifyDataSetChanged();
                        }
                    }


                    review.setText("( " + ratingListWithReviewList.size() + " Reviews )");

                    if (Math.round(tempNumberOfStars / itemReviewAndRatingsArrayList.size()) != 0) {
                        reviewLayout.setVisibility(View.VISIBLE);
                        rating.setText("" + Math.round(tempNumberOfStars / itemReviewAndRatingsArrayList.size()));
                        storeRating.setRating(Math.round(tempNumberOfStars / itemReviewAndRatingsArrayList.size()));
                    } else {
                        int height = 0;
                        LinearLayout.LayoutParams layout_param = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                height * 2);
                        reviewLayout.setLayoutParams(layout_param);
                        reviewLayout.setVisibility(View.INVISIBLE);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProductDescriptionActivity.this, ViewCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Available.equalsIgnoreCase(statusString) && adapterIntent == null && categoryName != null && categoryId != null && !"".equalsIgnoreCase(categoryName)) {

                    Intent intent = new Intent(ProductDescriptionActivity.this, ProductsListingActivity.class);
                    intent.putExtra("itemId", itemId);
                    intent.putExtra("categoryId", categoryId);
                    intent.putExtra("itemName", itemName);
                    intent.putExtra("categoryName", categoryName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (INACTIVE_STATUS.equalsIgnoreCase(statusString) && adapterIntent == null) {

                    Intent intent = new Intent(ProductDescriptionActivity.this, HomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else if (adapterIntent != null) {

                    Intent intent = new Intent(ProductDescriptionActivity.this, HomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (ingredientIndicator == null && adapterIntent == null && categoryName == null || "".equalsIgnoreCase(categoryName)) {

                    Intent intent = new Intent(ProductDescriptionActivity.this, HomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });


        addQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (counter != 10) {


                    qtycount = Integer.parseInt(qtyText.getText().toString());
                    qtycount = qtycount + 1;
                    if (itemLimitation < qtycount) {
                        qtycount = itemLimitation;
                        if (!((Activity) ProductDescriptionActivity.this).isFinishing()) {
                            SweetAlertDialog sweetAlertDialog;
                            sweetAlertDialog=new SweetAlertDialog(ProductDescriptionActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Item Limitation")
                                    .setContentText("Item Quantity is limited to " + itemLimitation + " for this item")
                                    ;
                            sweetAlertDialog.show();
                            Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                            btn.setBackgroundColor(ContextCompat.getColor(ProductDescriptionActivity.this, R.color.colorPrimary));
                        }

                    }
                    if (qtycount == 0) {
                        qtycount = qtycount;
                        addtoCartLayout.setVisibility(View.VISIBLE);
                        add_decreaseLayout.setVisibility(View.INVISIBLE);

                        viewCartdatabaseRef.
                                child(saved_id).child(itemId).removeValue();
                    } else {
                        qtyText.setText(String.valueOf(qtycount));
                        itemDetails.setItemBuyQuantity(qtycount);
                        itemDetails.setTotalTaxPrice(itemDetails.getTaxPrice() * qtycount);
                        itemDetails.setTotalItemQtyPrice(qtycount * itemDetails.getItemPrice());
                        viewCartdatabaseRef.
                                child(saved_id).child(itemId).setValue(itemDetails);
                    }

                }

                if (counter == 10) {

                    qtycount = Integer.parseInt(qtyText.getText().toString());
                    qtycount = qtycount + 1;
                    if (itemLimitation < qtycount) {
                        qtycount = itemLimitation;
                        if (!((Activity) ProductDescriptionActivity.this).isFinishing()) {
                            SweetAlertDialog sweetAlertDialog;

                            sweetAlertDialog =new SweetAlertDialog(ProductDescriptionActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Item Limitation")
                                    .setContentText("Item Quantity is limited to " + itemLimitation + " for this item")
                                    ;
                            sweetAlertDialog.show();
                            Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                            btn.setBackgroundColor(ContextCompat.getColor(ProductDescriptionActivity.this, R.color.colorPrimary));
                        }

                    }
                    if (qtycount == 0) {
                        qtycount = qtycount;
                        addtoCartLayout.setVisibility(View.VISIBLE);
                        add_decreaseLayout.setVisibility(View.INVISIBLE);

                        viewCartdatabaseRef.
                                child(saved_id).child(itemId).removeValue();
                    } else {
                        qtyText.setText(String.valueOf(qtycount));
                        mergedItemDetails.setItemBuyQuantity(qtycount);
                        mergedItemDetails.setTotalTaxPrice(mergedItemDetails.getTaxPrice() * qtycount);
                        mergedItemDetails.setTotalItemQtyPrice(qtycount * mergedItemDetails.getItemPrice());
                        viewCartdatabaseRef.
                                child(saved_id).child(itemId).setValue(mergedItemDetails);
                    }

                }
            }
        });
        decreaseQty.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (counter != 10) {
                            qtycount = Integer.parseInt(qtyText.getText().toString());
                            qtyText.setText(String.valueOf(qtycount));
                            qtycount = qtycount - 1;

                            if (qtycount == 0) {
                                qtycount = qtycount;
                                addtoCartLayout.setVisibility(View.VISIBLE);
                                add_decreaseLayout.setVisibility(View.INVISIBLE);

                                viewCartdatabaseRef.
                                        child(saved_id).child(itemId).removeValue();
                            } else if (qtycount > 0) {
                                addtoCartLayout.setVisibility(View.INVISIBLE);
                                add_decreaseLayout.setVisibility(View.VISIBLE);
                                itemDetails.setItemBuyQuantity(qtycount);
                                itemDetails.setTotalTaxPrice(itemDetails.getTaxPrice() * qtycount);
                                itemDetails.setTotalItemQtyPrice(qtycount * itemDetails.getItemPrice());
                                viewCartdatabaseRef.
                                        child(saved_id).child(itemId).setValue(itemDetails);
                            }

                        }
                        if (counter == 10) {
                            qtycount = Integer.parseInt(qtyText.getText().toString());
                            qtyText.setText(String.valueOf(qtycount));
                            qtycount = qtycount - 1;

                            if (qtycount == 0) {
                                qtycount = qtycount;
                                addtoCartLayout.setVisibility(View.VISIBLE);
                                add_decreaseLayout.setVisibility(View.INVISIBLE);

                                viewCartdatabaseRef.
                                        child(saved_id).child(itemId).removeValue();
                            } else if (qtycount > 0) {
                                addtoCartLayout.setVisibility(View.INVISIBLE);
                                add_decreaseLayout.setVisibility(View.VISIBLE);
                                mergedItemDetails.setItemBuyQuantity(qtycount);
                                mergedItemDetails.setTotalTaxPrice(mergedItemDetails.getTaxPrice() * qtycount);
                                mergedItemDetails.setTotalItemQtyPrice(qtycount * mergedItemDetails.getItemPrice());
                                viewCartdatabaseRef.
                                        child(saved_id).child(itemId).setValue(mergedItemDetails);
                            }
                        }


                    }
                });


    }

    @Override
    protected void onStart() {

        super.onStart();

        viewCartdatabaseRef.child(saved_id).orderByChild("itemStatus").equalTo(Available).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    purchasedetailslayout.setVisibility(View.VISIBLE);
                    amount = 0;
                    for (DataSnapshot viewCartItemDetails : dataSnapshot.getChildren()) {

                        ItemDetails itemDetails = viewCartItemDetails.getValue(ItemDetails.class);

                        if (itemId.equalsIgnoreCase(String.valueOf(itemDetails.getItemId()))) {

                            qtyText.setText(String.valueOf(itemDetails.getItemBuyQuantity()));
                            itemLimitation = itemDetails.getItemMaxLimitation();
                            if ((Integer.parseInt(qtyText.getText().toString())) == 0) {
                                addtoCartLayout.setVisibility(View.VISIBLE);
                                add_decreaseLayout.setVisibility(View.INVISIBLE);

                            }
                            if ((Integer.parseInt(qtyText.getText().toString())) > 0) {
                                addtoCartLayout.setVisibility(View.INVISIBLE);
                                add_decreaseLayout.setVisibility(View.VISIBLE);
                            }
                        }


                        amount = amount + itemDetails.getTotalItemQtyPrice();
                        items = dataSnapshot.getChildrenCount();

                    }

                    textView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    if (items == 1) {
                        itemCount.setText((dataSnapshot.getChildrenCount()) + " item");
                    } else {
                        itemCount.setText((dataSnapshot.getChildrenCount()) + " items");
                    }
                    totalAmount.setText(" ₹" + (amount));

                } else if (dataSnapshot.getChildrenCount() == 0) {
                    textView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    purchasedetailslayout.setVisibility(View.INVISIBLE);

                    addtoCartLayout.setVisibility(View.VISIBLE);
                    add_decreaseLayout.setVisibility(View.INVISIBLE);


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        purchasedetailslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDescriptionActivity.this, ViewCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        viewcartIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDescriptionActivity.this, ViewCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad((OnLoadCompleteListener) ProductDescriptionActivity.this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            //  Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProductDescriptionActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (!((Activity) ProductDescriptionActivity.this).isFinishing()) {
                                bottomSheetDialog.dismiss();
                            }
                            Query findPhoneNumberQuery = customerDetailsDataRef.orderByChild("customerPhoneNumber").equalTo(phoneNumberEditText.getText().toString());

                            findPhoneNumberQuery.addValueEventListener(new ValueEventListener() {
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
                                        }
                                        wishListRef = CommonMethods.fetchFirebaseDatabaseReference(WISHLIST_FIREBASE_TABLE).child(saved_id);
                                        wishListRef.child(itemId).setValue(itemDetails);
                                        wishListRef.child(itemId).child("itemCounter").setValue(1);
                                        wishListRef.child(itemId).child("itemBuyQuantity").setValue(1);
                                        wishListRef.child(itemId).child("totalItemQtyPrice").setValue(itemDetails.getItemPrice());
                                        wishListRef.child(itemId).child("totalTaxPrice").setValue(itemDetails.getTaxPrice());
                                         if (!((Activity) ProductDescriptionActivity.this).isFinishing()) {
                                             errorDialog = new SweetAlertDialog(ProductDescriptionActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                             errorDialog.setCancelable(false);

                                             errorDialog
                                                     .setContentText("Added to wishlist").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                 @Override
                                                 public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                     errorDialog.dismiss();
                                                 }
                                             }).show();
                                             Button btn = (Button) errorDialog.findViewById(R.id.confirm_button);
                                             btn.setBackgroundColor(ContextCompat.getColor(ProductDescriptionActivity.this, R.color.colorPrimary));
                                             removeWishlist.setVisibility(View.VISIBLE);
                                             wishlist.setVisibility(View.INVISIBLE);
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
                            if (!((Activity) ProductDescriptionActivity.this).isFinishing()) {
                                errorDialog = new SweetAlertDialog(ProductDescriptionActivity.this, SweetAlertDialog.ERROR_TYPE);
                                errorDialog.setCancelable(false);
                                errorDialog
                                        .setContentText("Invalid OTP").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        errorDialog.dismiss();
                                    }

                                });

                                errorDialog.dismiss();

                                Button btn = (Button) errorDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(ProductDescriptionActivity.this, R.color.colorPrimary));
                            }
                        }
                    }
                });
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
            Toast.makeText(ProductDescriptionActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        if (!((Activity) ProductDescriptionActivity.this).isFinishing()) {
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