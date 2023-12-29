package com.smiligenceTestenv.techUser.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DatabaseReference;
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.ViewCartActivity;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.Constant;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.smiligenceTestenv.techUser.common.Constant.VIEW_CART_FIREBASE_TABLE;


public class WishListAdapter extends BaseAdapter {
    private Activity context;
    List<ItemDetails> itemDetailsList;
    ItemDetails itemDetails;
    private static LayoutInflater inflater = null;
    DatabaseReference databaseReference;
    int counter = 1;
    DatabaseReference databaseReferenceAdd;
    DatabaseReference databaseReferenceRemove;
    String saved_id;
    public static int countIndicator = 3;


    public WishListAdapter(Activity context, List<ItemDetails> list) {
        this.context = context;
        this.itemDetailsList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return itemDetailsList.size();
    }


    @Override
    public Object getItem(int position) {
        return itemDetailsList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    private class ViewHolder {
        TextView itemName, itemPrice, itemMrp;
        ImageView itemImageView;
        RelativeLayout wishListCart, WishlistRemove;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        //preloadImages();
        final WishListAdapter.ViewHolder holder = new WishListAdapter.ViewHolder();
        View listViewItem = convertView;
        listViewItem = (listViewItem == null) ? inflater.inflate(R.layout.wishlist_layout, null) : listViewItem;
        holder.itemName = (TextView) listViewItem.findViewById
                (R.id.wishitemname_viewcart);

        holder.itemPrice = (TextView) listViewItem.findViewById
                (R.id.wishitemprice_viewcart);
        holder.itemMrp = listViewItem.findViewById(R.id.wishitemmrp);
        holder.itemImageView = listViewItem.findViewById(R.id.wishitemimage);
        holder.wishListCart = listViewItem.findViewById(R.id.wishMoveToCart);
        holder.WishlistRemove = listViewItem.findViewById(R.id.wishReomove);

        final SharedPreferences loginSharedPreferences = context.getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");
        databaseReference = CommonMethods.fetchFirebaseDatabaseReference(Constant.WISHLIST_FIREBASE_TABLE).child(saved_id);
        databaseReferenceAdd = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);
        databaseReferenceRemove = CommonMethods.fetchFirebaseDatabaseReference(Constant.WISHLIST_FIREBASE_TABLE).child(saved_id);
        itemDetails = itemDetailsList.get(position);
        holder.itemName.setText(itemDetails.getItemName());
        holder.itemPrice.setText((" ₹" + itemDetails.getItemPrice()));
        holder.itemMrp.setText(" ₹" + (itemDetails.getMRP_Price()));
        holder.itemMrp.setPaintFlags(holder.itemMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Log.d("RESSSS", "" + itemDetailsList.get(position).getItemBuyQuantity());

        if (!context.isFinishing()) {
            Glide.with(context)
                    .load(itemDetails.getItemImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    return false;
                }
            }).into(holder.itemImageView);
        }


        View finalListViewItem = listViewItem;
        holder.wishListCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.move_to_cart_dialog, null);

                Button move = dialogView.findViewById(R.id.moveItemtocart);
                Button cancel = dialogView.findViewById(R.id.cancelItemcart);
                dialogBuilder.setView(dialogView);
                final AlertDialog b = dialogBuilder.create();
                b.show();


                move.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!((Activity) context).isFinishing()) {
                            databaseReferenceAdd.child(String.valueOf(itemDetailsList.get(position).getItemId())).setValue(itemDetailsList.get(position));
                            databaseReferenceRemove.child(String.valueOf(itemDetailsList.get(position).getItemId())).removeValue();
                        }



                        Intent intent = new Intent(context, ViewCartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        b.dismiss();

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        b.dismiss();
                    }
                });

            }
        });

        holder.WishlistRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.delete_wishlist_dialog, null);

                Button delete = dialogView.findViewById(R.id.deleteItem);
                Button cancel = dialogView.findViewById(R.id.cancel_item);
                dialogBuilder.setView(dialogView);
                final AlertDialog b = dialogBuilder.create();
                b.show();

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReference.child(String.valueOf(itemDetailsList.get(position).getItemId())).removeValue();
                        Intent intent = new Intent(context, ViewCartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        b.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        b.dismiss();
                    }
                });

            }
        });

        return listViewItem;
    }



}
