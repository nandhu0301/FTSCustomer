package com.smiligenceTestenv.techUser.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.ViewOrderActivity;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.ItemReviewAndRatings;
import com.smiligenceTestenv.techUser.common.CommonMethods;

import java.util.ArrayList;
import java.util.List;

import static com.smiligenceTestenv.techUser.ViewOrderActivity.getOrderIdValue;
import static com.smiligenceTestenv.techUser.ViewOrderActivity.maxid;

public class ItemOrderDetails extends BaseAdapter {

    private Context mcontext;
    private List<ItemDetails> itemList;
    LayoutInflater inflater;
    private List<String> giftWrappingDetails;
    ArrayList<String> giftWrappingOption = new ArrayList<>();
    DatabaseReference itemRatingsAndReviewsRef;
    String orderIdForItemRatings, orderStatusText;
    ItemReviewAndRatings itemReviewAndRatings = new ItemReviewAndRatings();
    int counter = 0;
    int maxId;

    public ItemOrderDetails(Context context, List<ItemDetails> itemListŇew, String orderIdForItemRatings1, String orderStatusText1) {
        mcontext = context;
        itemList = itemListŇew;
        inflater = (LayoutInflater.from(context));
        orderIdForItemRatings = orderIdForItemRatings1;
        orderStatusText = orderStatusText1;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView images;
        TextView t_name, t_price_percent, t_total_amount, rate_us_items, taxText;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        giftWrappingOption.clear();
        itemRatingsAndReviewsRef = CommonMethods.fetchFirebaseDatabaseReference("ItemRatingsAndReview");

        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_details_layout, parent, false);

            holder.t_name = row.findViewById(R.id.itemName);
            holder.t_price_percent = row.findViewById(R.id.item_qty);
            holder.t_total_amount = row.findViewById(R.id.itemTotal);
            holder.rate_us_items = row.findViewById(R.id.rate_us_items);
            holder.taxText = row.findViewById(R.id.taxText);

            holder.images = (ImageView) row.findViewById(R.id.itemImage);
            row.setTag(holder);
        } else {

            holder = (ViewHolder) row.getTag();
        }


        final ItemDetails itemDetailsObj = itemList.get(position);

        final ViewHolder finalHolder = holder;

        if (orderStatusText.equals("Shipped")) {
            itemRatingsAndReviewsRef.orderByChild(String.valueOf(getOrderIdValue)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot ratingSnap : dataSnapshot.getChildren()) {
                            ItemReviewAndRatings itemDetails = ratingSnap.getValue(ItemReviewAndRatings.class);
                            if ("Starred".equalsIgnoreCase(itemDetails.getStarred())) {
                                if (itemDetails.getItemId().equalsIgnoreCase(String.valueOf(itemDetailsObj.getItemId()))) {
                                    if (itemDetails.getOrderId().equalsIgnoreCase(getOrderIdValue)) {
                                        finalHolder.rate_us_items.setVisibility(View.INVISIBLE);
                                    }
                                }

                            }
                        }
                    } else {
                        finalHolder.rate_us_items.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            finalHolder.rate_us_items.setVisibility(View.VISIBLE);
        }


        holder.rate_us_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mcontext);
                LayoutInflater inflater = ((Activity) mcontext).getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_rate, null);

                final RatingBar ratingBar = dialogView.findViewById(R.id.rb_stars);
                Button addRatings = dialogView.findViewById(R.id.bt_send);
                LinearLayout mayBeLaterRatings = dialogView.findViewById(R.id.bt_no);
                dialogBuilder.setView(dialogView);
                final AlertDialog b = dialogBuilder.create();
                b.show();
                b.setCancelable(false);
                addRatings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int rating = (int) ratingBar.getRating();
                        if (!((Activity) mcontext).isFinishing()) {
                            counter = 2;
                            if (counter == 2) {

                                maxId = maxid + 1;

                            }

                            itemReviewAndRatings.setStars(rating);
                            itemReviewAndRatings.setItemRatingReviewStatus("Waiting For Approval");
                            itemReviewAndRatings.setStarred("Starred");
                            itemReviewAndRatings.setItemId(String.valueOf(itemDetailsObj.getItemId()));
                            itemReviewAndRatings.setOrderId(getOrderIdValue);
                            itemReviewAndRatings.setItemName(itemDetailsObj.getItemName());

                            itemReviewAndRatings.setRatingReviewId(String.valueOf(maxId));
                            itemRatingsAndReviewsRef.child(String.valueOf(maxId)).setValue(itemReviewAndRatings);

                        }
                        b.dismiss();

                        AlertDialog.Builder dialog_feedbackdialogBuilder = new AlertDialog.Builder(mcontext);
                        LayoutInflater dialog_feedbackinflater = ((Activity) mcontext).getLayoutInflater();
                        final View dialog_feedbackdialogView = dialog_feedbackinflater.inflate(R.layout.dialog_feedback, null);
                        dialog_feedbackdialogBuilder.setView(dialog_feedbackdialogView);
                        final AlertDialog feedALert = dialog_feedbackdialogBuilder.create();
                        feedALert.show();
                        feedALert.setCancelable(false);
                        final EditText feedBackEdt = dialog_feedbackdialogView.findViewById(R.id.et_feedback);
                        Button sendFeedback = dialog_feedbackdialogView.findViewById(R.id.bt_send);
                        LinearLayout bt_no = dialog_feedbackdialogView.findViewById(R.id.bt_no);
                        bt_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                feedALert.dismiss();
                                Intent intent = new Intent(mcontext, ViewOrderActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("OrderidDetails", getOrderIdValue);
                                mcontext.startActivity(intent);

                            }
                        });
                        sendFeedback.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String feedback = feedBackEdt.getText().toString();
                                if (view.getId() == R.id.bt_send && feedback.length() > 0) {
                                    feedALert.dismiss();
                                    if (!((Activity) mcontext).isFinishing()) {
                                        if (counter == 2) {
                                            itemReviewAndRatings.setReview(feedback);
                                            itemRatingsAndReviewsRef.child(String.valueOf(maxId)).setValue(itemReviewAndRatings);
                                            counter = 0;
                                            Intent intent = new Intent(mcontext, ViewOrderActivity.class);
                                            intent.putExtra("OrderidDetails", getOrderIdValue);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            mcontext.startActivity(intent);

                                        }

                                    }
                                } else if (view.getId() == R.id.bt_send) {
                                    return;
                                }
                            }
                        });
                    }
                });
                mayBeLaterRatings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        b.dismiss();


                    }
                });
            }
        });
        holder.t_name.setText(itemDetailsObj.getItemName());
        holder.t_price_percent.setText((itemDetailsObj.getItemPrice()) + " * " + itemDetailsObj.getItemBuyQuantity());
        holder.t_total_amount.setText(""+itemDetailsObj.getTax());
      //  holder.t_total_amount.setText("₹" + (itemDetailsObj.getTotalItemQtyPrice()) + " ( inclusive of " + (itemDetailsObj.getTax()) + "% Tax)");
        holder.taxText.setText("");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);
        if (!((Activity) mcontext).isFinishing()) {
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(itemDetailsObj.getItemImage()).fitCenter().into(holder.images);
        }
        return row;
    }

    public void setMaxId() {

    }

}
