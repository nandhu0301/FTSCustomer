package com.smiligenceTestenv.techUser.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.Discount;

import java.util.List;

public class CustomerDiscountAdapter extends BaseAdapter {

    private Context mcontext;
    private List<Discount> discountList;
    LayoutInflater inflater;

    public CustomerDiscountAdapter(Context context, List<Discount> listDiscount) {
        mcontext = context;
        discountList = listDiscount;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return discountList.size();
    }

    @Override
    public Object getItem(int position) {
        return discountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView t_name, t_price_percent,t_minimumamount;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.discount_adapter_grid, parent, false);
            holder.t_name = row.findViewById(R.id.discountName);
            holder.t_price_percent = row.findViewById(R.id.discountType);
            holder.t_minimumamount=row.findViewById(R.id.discountValid);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Discount newDiscount = discountList.get(position);
        holder.t_name.setText(newDiscount.getDiscountName());
        holder.t_minimumamount.setText("Valid on orders above ₹"+newDiscount.getMinmumBillAmount());

        if ("Price".equalsIgnoreCase(newDiscount.getTypeOfDiscount()))
        {
            holder.t_price_percent.setText("Flat ₹"+newDiscount.getDiscountPrice()+" OFFER");
        }
        else
        {
            holder.t_price_percent.setText(newDiscount.getDiscountPercentageValue()+"%"+" OFFER");

        }
        return row;
    }
}