package com.smiligenceTestenv.techUser.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.Filters;
import com.smiligenceTestenv.techUser.bean.Preferences;

import java.util.List;

public class FilterValuesAdapter extends RecyclerView.Adapter<FilterValuesAdapter.MyViewHolder> {

    private final Integer filterIndex;

    public FilterValuesAdapter(Integer filterIndex) {
        this.filterIndex = filterIndex;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_value_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {
        final Filters tmpFilter = Preferences.filters.get(filterIndex);
        myViewHolder.value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selected = tmpFilter.getSelected();
                if(myViewHolder.value.isChecked()) {
                    selected.add(tmpFilter.getValues().get(position));
                    tmpFilter.setSelected(selected);
                } else {
                    selected.remove(tmpFilter.getValues().get(position));
                    tmpFilter.setSelected(selected);
                }
                Preferences.filters.put(filterIndex, tmpFilter);
            }
        });
        myViewHolder.value.setText(tmpFilter.getValues().get(position));
        if(tmpFilter.getSelected().contains(tmpFilter.getValues().get(position))) {
            myViewHolder.value.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return Preferences.filters.get(filterIndex).getValues().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final CheckBox value;

        MyViewHolder(View view) {
            super(view);
            value = view.findViewById(R.id.value);
        }
    }

}