package com.smiligenceTestenv.techUser.Adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.ItemDetails;

import java.util.List;

import static com.smiligenceTestenv.techUser.ProductDescriptionActivity.itemidString;


public class QuantityVarientAdapter extends RecyclerView.Adapter<QuantityVarientAdapter.ImageViewHolder> {
    private Context mcontext;
    private List<ItemDetails> categoryDetailsList;
    private OnItemClicklistener mlistener;
    Activity activity;
    int priority;
    int Position;

    ImageViewHolder holder;


    ImageViewHolder imageViewHolder;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public QuantityVarientAdapter(Context context, List<ItemDetails> catagories) {
        mcontext = context;
        categoryDetailsList = catagories;
        this.priority = priority;

    }

    @NonNull

    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mcontext).inflate(R.layout.qty_varient_layout, parent, false);
        imageViewHolder = new ImageViewHolder(v, mlistener);


        return imageViewHolder;
    }

    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {


        ItemDetails categoryDetails = categoryDetailsList.get(position);


        holder.qtyVarient.setText(categoryDetails.getItemQuantity() );
        holder.qtyVarient.setSelected(true);


        if (itemidString != null) {

            if (itemidString.equalsIgnoreCase(String.valueOf(categoryDetails.getItemId()))) {
                holder.qtyVarient.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.cyanbase));
                holder.qtyVarient.setTextColor(ContextCompat.getColor(mcontext, R.color.white));
            }
        }



    }


    public int getItemCount() {
        return categoryDetailsList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView qtyVarient;
        ImageView itemImages;
        RelativeLayout secondaryGrid;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);
            qtyVarient = itemView.findViewById(R.id.qtyvarients);
            qtyVarient.setSelected(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClicklistener != null) {
                        Position = getAdapterPosition();
                        notifyDataSetChanged();
                        if (Position != RecyclerView.NO_POSITION) {
                            itemClicklistener.Onitemclick(Position);
                        }


                    }
                }
            });
        }
    }
}


