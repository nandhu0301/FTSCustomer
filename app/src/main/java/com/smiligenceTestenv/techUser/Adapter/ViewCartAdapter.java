package com.smiligenceTestenv.techUser.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.ViewCartActivity;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;
import com.smiligenceTestenv.techUser.common.TextUtils;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


import static android.content.Context.MODE_PRIVATE;
import static com.smiligenceTestenv.techUser.ProductDescriptionActivity.PrefIndicator;
import static com.smiligenceTestenv.techUser.common.Constant.VIEW_CART_FIREBASE_TABLE;
import static com.smiligenceTestenv.techUser.common.Constant.WISHLIST_FIREBASE_TABLE;


public class ViewCartAdapter extends BaseAdapter {
    private Activity context;
    List<ItemDetails> itemDetailsList;
    ItemDetails itemDetails;
    private static LayoutInflater inflater = null;
    DatabaseReference databaseReference;
    int counter = 1;
    long childCount;
    int incrementIndicator = 1;
    int decrementIndicator = 0;
    String saved_id;
    public static int countIndicator = 3;
    DatabaseReference databaseReferenceAdd;
    DatabaseReference databaseReferenceRemove;


    public ViewCartAdapter(Activity context, List<ItemDetails> list) {
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
        ImageView itemImage;
        TextView itemName, itemqtyPrice, itemPrice, itemMrp;
        RelativeLayout addItem, addQuantity;
        ImageView increaseQty, decreaseQty;
        TextView itemQtyText;
        ImageView itemImageView;
        RelativeLayout moveToWishList;

    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {


        final ViewHolder holder = new ViewHolder();
        View listViewItem = convertView;
        listViewItem = (listViewItem == null) ? inflater.inflate(R.layout.view_cart_list_layout, null) : listViewItem;
        holder.itemName = (TextView) listViewItem.findViewById
                (R.id.itemname_viewcart);
        holder.itemqtyPrice = (TextView) listViewItem.findViewById
                (R.id.itemqty_viewcart);
        holder.itemPrice = (TextView) listViewItem.findViewById
                (R.id.itemprice_viewcart);
        holder.itemMrp = listViewItem.findViewById(R.id.itemmrp);
        holder.itemImageView = listViewItem.findViewById(R.id.itemimage);
        holder.moveToWishList = listViewItem.findViewById(R.id.moveToWishlist);

        RelativeLayout deleteItem = listViewItem.findViewById(R.id.deleteItem);

        holder.addQuantity = listViewItem.findViewById(R.id.addquantity);
        holder.increaseQty = listViewItem.findViewById(R.id.incqty);
        holder.decreaseQty = listViewItem.findViewById(R.id.decqty);
        holder.itemQtyText = listViewItem.findViewById(R.id.textqty);
        final SharedPreferences loginSharedPreferences = context.getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");
        databaseReference = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(saved_id);
        databaseReferenceAdd = CommonMethods.fetchFirebaseDatabaseReference(WISHLIST_FIREBASE_TABLE).child(saved_id);
        databaseReferenceRemove = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);
        itemDetails = itemDetailsList.get(position);
        holder.itemName.setText(itemDetails.getItemName());
        holder.itemqtyPrice.setText(" ₹" + (itemDetails.getTotalItemQtyPrice()));
        holder.itemPrice.setText((" ₹" + itemDetails.getItemPrice()));
        holder.itemMrp.setText(" ₹" + (itemDetails.getMRP_Price()));
        holder.itemMrp.setPaintFlags(holder.itemMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.itemQtyText.setText(String.valueOf(itemDetails.getItemBuyQuantity()));
        if (!context.isFinishing()) {
            Glide.with(context).load(itemDetails.getItemImage()).into(holder.itemImageView);
        }

        counter = itemDetails.getItemBuyQuantity();

        holder.increaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertData(position, holder, incrementIndicator);
            }
        });

        if(!TextUtils.isNumeric((saved_id))){
            holder.moveToWishList.setVisibility(View.INVISIBLE);
        }

        holder.moveToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.move_to_wishlist_dialog, null);

                Button move = dialogView.findViewById(R.id.moveItem);
                Button cancel = dialogView.findViewById(R.id.cancelitem);
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

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.delete_cart_dialog, null);

                Button delete = dialogView.findViewById(R.id.deleteItem);
                Button cancel = dialogView.findViewById(R.id.cancel_item);
                dialogBuilder.setView(dialogView);
                final AlertDialog b = dialogBuilder.create();
                b.show();


                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        databaseReference.child(String.valueOf(itemDetailsList.get(position).getItemId())).removeValue();

                        PrefIndicator = 10;

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                childCount = dataSnapshot.getChildrenCount();


                                if (childCount == 0) {
                                    if (PrefIndicator == 10) {

                                        Intent intent = new Intent(context, ViewCartActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(intent);
                                        PrefIndicator = 10;
                                    }
                                    if (PrefIndicator == 3) {

                                        Intent intent = new Intent(context, ViewCartActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(intent);
                                        PrefIndicator = 3;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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


        holder.decreaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                itemDetails = itemDetailsList.get(position);
                counter = itemDetails.getItemBuyQuantity();

                if (counter > 0) {

                    counter = counter - 1;

                    holder.itemQtyText.setText(String.valueOf(counter));

                    itemDetailsList.get(position).setItemCounter(counter);

                    itemDetails.setItemCounter(counter);
                    itemDetails.setItemBuyQuantity(counter);
                    itemDetails.setTotalItemQtyPrice(counter * itemDetails.getItemPrice());
                    itemDetails.setTotalTaxPrice(itemDetails.getTaxPrice()*counter);
                    holder.itemqtyPrice.setText(" ₹" + (itemDetails.getTotalItemQtyPrice()));

                    databaseReference.child(String.valueOf(itemDetailsList.get(position).getItemId())).setValue(itemDetails);

                }

                if (counter == 0) {

                    holder.itemQtyText.setText(String.valueOf(counter));
                    itemDetailsList.get(position).setItemCounter(counter);

                    itemDetails = itemDetailsList.get(position);

                    DatabaseReference query = databaseReference.child(String.valueOf(itemDetailsList.get(position).getItemId()));
                    query.child("itemBuyQuantity").equalTo("0").getRef().getParent().removeValue();

                }


                if (itemDetailsList.size() == 1) {
                    if (itemDetailsList.get(position).getItemBuyQuantity() == 0) {


                        Intent intent = new Intent(context, ViewCartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);

                    }

                }

            }
        });


        return listViewItem;
    }


    private void insertData(int position, ViewHolder holder, int incDecIndicator) {

        itemDetails = itemDetailsList.get(position);
        counter = itemDetails.getItemBuyQuantity();
        if (incDecIndicator == incrementIndicator) {


            if (counter == itemDetailsList.get(position).getItemMaxLimitation()) {

                if (!((Activity) context).isFinishing()) {
                    SweetAlertDialog sweetAlertDialog;
                    sweetAlertDialog=new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Item Limitation")
                            .setContentText("Item Quantity is limited to " + itemDetailsList.get(position).getItemMaxLimitation() + " for this item")
                            ;
                    sweetAlertDialog.show();
                    Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                    btn.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                counter = counter;
            } else if (counter > itemDetailsList.get(position).getItemMaxLimitation()) {
                if (!((Activity) context).isFinishing()) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Item Limitation")
                            .setContentText("Item Quantity is limited to " + itemDetailsList.get(position).getItemMaxLimitation() + " for this item")
                            .show();
                }
                counter = counter;

            } else {
                counter = counter + 1;
            }
        } else if (incDecIndicator == decrementIndicator) {
            counter = counter - 1;
        }


        holder.itemQtyText.setText(String.valueOf(counter));
        itemDetailsList.get(position).setItemCounter(counter);

        itemDetails.setItemCounter(counter);
        itemDetails.setItemBuyQuantity(counter);
        itemDetails.setTotalItemQtyPrice(counter * itemDetails.getItemPrice());
        itemDetails.setTotalTaxPrice(itemDetails.getTaxPrice()*counter);
        holder.itemqtyPrice.setText("₹" + (itemDetails.getTotalItemQtyPrice()));

        databaseReference.child(String.valueOf(itemDetailsList.get(position).getItemId())).setValue(itemDetails);
    }


}
