package com.smiligenceTestenv.techUser.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.common.CommonMethods;

import java.util.List;


import static android.content.Context.MODE_PRIVATE;

public class PaymentAdapter extends BaseAdapter {
    private Activity context;
    List<ItemDetails> itemDetailsList;
    ItemDetails itemDetails;
    private static LayoutInflater inflater = null;
    DatabaseReference databaseReference;
    String  saved_id;

    public PaymentAdapter(Activity context, List<ItemDetails> list) {
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
        TextView itemName, itemqtyPrice, itemPrice, taxPercent,taxamount;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        final ViewHolder holder = new ViewHolder();

        listViewItem = (listViewItem == null) ? inflater.inflate(R.layout.item_detail_layout, null) : listViewItem;
        holder.itemName = (TextView) listViewItem.findViewById
                (R.id.itemnamepuchase);
        holder.itemqtyPrice = (TextView) listViewItem.findViewById
                (R.id.total);
        holder.itemPrice = (TextView) listViewItem.findViewById
                (R.id.itempricepurchase);
        holder.itemImage = listViewItem.findViewById(R.id.itemimagePayment);

        final SharedPreferences loginSharedPreferences = context.getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");
        databaseReference = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(saved_id);

        itemDetails = itemDetailsList.get(position);
        holder.itemName.setText(itemDetails.getItemName());
        holder.itemqtyPrice.setText(" ₹" + (itemDetails.getTotalItemQtyPrice()));
        holder.itemPrice.setText((itemDetails.getItemPrice()) + "*" + itemDetails.getItemBuyQuantity());



        Double percentageValue = (double) itemDetails.getTax();
        Double discountPercentageValue = (percentageValue / 100) * itemDetails.getItemPrice();



        if (!((Activity) context).isFinishing() && context != null && !context.isDestroyed()) {
            Glide.with(context).load(itemDetails.getItemImage()).into(holder.itemImage);

        }

        return listViewItem;
    }


}
