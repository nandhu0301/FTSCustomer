package com.smiligenceTestenv.techUser.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.Filters;

import java.util.HashMap;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {

    private final HashMap<Integer, Filters> filters;
    private final RecyclerView filterValuesRV;
    private int selectedPosition = 0;

    public FilterAdapter(HashMap<Integer, Filters> filters, RecyclerView filterValuesRV) {
        this.filters = filters;
        this.filterValuesRV = filterValuesRV;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {
        myViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterValuesRV.setAdapter(new FilterValuesAdapter(position));
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });

        filterValuesRV.setAdapter(new FilterValuesAdapter(selectedPosition));
        myViewHolder.container.setBackgroundColor(selectedPosition == position ? Color.WHITE : Color.TRANSPARENT);
        myViewHolder.title.setText(filters.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final View container;
        final TextView title;

        MyViewHolder(View view) {
            super(view);
            container = view;
            title = view.findViewById(R.id.title);
        }
    }

}
