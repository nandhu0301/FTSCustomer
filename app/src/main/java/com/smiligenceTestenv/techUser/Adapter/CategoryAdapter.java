package com.smiligenceTestenv.techUser.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.smiligenceTestenv.techUser.ProductDescriptionActivity;
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.ItemDetails;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<ItemDetails> mobiles = new ArrayList<>();
    AdapterView.OnItemClickListener onItemClickListener;
    View.OnClickListener mOnClickListener;

    public CategoryAdapter(Context context, List<ItemDetails> mobiles) {
        this.context = context;
        this.mobiles = mobiles;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //preloadImages();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView = inflater.inflate(R.layout.item_child, null, false);
        ViewHolder viewHolder = new ViewHolder(cardView);
        viewHolder.mobileImage = (ImageView) cardView.findViewById(R.id.image_mobile);
        viewHolder.modelName = (TextView) cardView.findViewById(R.id.text_mobile_model);
        viewHolder.price = (TextView) cardView.findViewById(R.id.text_mobile_price);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        ImageView mobileImageView = (ImageView) holder.mobileImage;
        if (mobiles.get(position).getItemImage() != null && mobiles.get(position).getItemPrice() != 0 && mobiles.get(position).getItemName() != null) {

            if (!((Activity) context).isFinishing()) {
                Glide.with(context)
                        .load(mobiles.get(position).getItemImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                }).into(mobileImageView);
            }
            TextView modelTextView = (TextView) holder.modelName;
            modelTextView.setText(mobiles.get(position).getItemName());

            TextView priceTextView = (TextView) holder.price;
            priceTextView.setText("₹ " + mobiles.get(position).getItemPrice());

        }


        holder.modelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobiles.get(position).getItemImage() != null && mobiles.get(position).getItemPrice() != 0 && mobiles.get(position).getItemName() != null) {
                    Intent intent = new Intent(context, ProductDescriptionActivity.class);
                    intent.putExtra("adapterIntent", "adapterIntent");
                    intent.putExtra("itemId", String.valueOf(mobiles.get(position).getItemId()));
                    intent.putExtra("categoryId", String.valueOf(mobiles.get(position).getCategoryId()));
                    intent.putExtra("itemName", String.valueOf(mobiles.get(position).getItemName()));
                    intent.putExtra("categoryName", String.valueOf(mobiles.get(position).getCategoryName()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }

            }
        });


        holder.mobileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mobiles.get(position).getItemImage() != null && mobiles.get(position).getItemPrice() != 0 && mobiles.get(position).getItemName() != null) {
                    Intent intent = new Intent(context, ProductDescriptionActivity.class);
                    intent.putExtra("adapterIntent", "adapterIntent");
                    intent.putExtra("itemId", String.valueOf(mobiles.get(position).getItemId()));
                    intent.putExtra("categoryId", String.valueOf(mobiles.get(position).getCategoryId()));
                    intent.putExtra("itemName", String.valueOf(mobiles.get(position).getItemName()));
                    intent.putExtra("categoryName", String.valueOf(mobiles.get(position).getCategoryName()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mobiles.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mobileImage;
        TextView modelName;
        TextView price;

        public ViewHolder(View itemView) {
            super(itemView);
            mobileImage = (ImageView) itemView.findViewById(R.id.image_mobile);
            modelName = (TextView) itemView.findViewById(R.id.text_mobile_model);
            price = (TextView) itemView.findViewById(R.id.text_mobile_price);
        }


    }

    private void preloadImages() {
        for (ItemDetails url : mobiles) {
            Glide.with(context)
                    .load(url.getItemImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload();
        }
    }

}
