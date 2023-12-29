package com.smiligenceTestenv.techUser.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.smiligenceTestenv.techUser.ProductsListingActivity;
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.CategoryDetails;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.smiligenceTestenv.techUser.common.Constant.CUSTOMER_ID;


public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.ImageViewHolder> {

    private Context mcontext;
    private List<CategoryDetails> categoryDetailsList;
    private OnItemClicklistener mlistener;
    Activity activity;

    int Position;
    String  saved_id;
    ImageViewHolder holder;
    ImageViewHolder imageViewHolder;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public CategoryViewAdapter(Context context, List<CategoryDetails> catagories) {
        mcontext = context;
        categoryDetailsList = catagories;
        //preloadImages();


    }

    @NonNull

    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(mcontext).inflate(R.layout.primary_category_grid, parent, false);
            imageViewHolder = new ImageViewHolder(v, mlistener);
            return imageViewHolder;
    }

    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {


        final SharedPreferences loginSharedPreferences = mcontext.getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");
        final CategoryDetails categoryDetails = categoryDetailsList.get(position);

        String upperString =categoryDetails.getCategoryName();
        String newString=  upperString.substring(0, 1).toUpperCase() + upperString.substring(1).toLowerCase();
      holder.catagoryName.setText(newString);
       // holder.catagoryName.setText(categoryDetails.getCategoryName());
        holder.catagoryName.setSelected(true);

        if (!((Activity) mcontext).isFinishing()) {

//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.mipmap.ic_launcher);
//            requestOptions.error(R.mipmap.ic_launcher);
//            Glide.with(mcontext)
//                    .setDefaultRequestOptions(requestOptions)
//                    .load(categoryDetails.getCategoryImage()).into(holder.itemImages);

            Glide.with(mcontext)
                    .load(categoryDetails.getCategoryImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    return false;
                }
            }).into(holder.itemImages);

        }

        holder.catagoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, ProductsListingActivity.class);
                intent.putExtra("categoryName", categoryDetails.getCategoryName());
                intent.putExtra("categoryId", categoryDetails.getCategoryid());

                intent.putExtra(CUSTOMER_ID, saved_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mcontext.startActivity(intent);
            }
        });



        holder.itemImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, ProductsListingActivity.class);
                intent.putExtra("categoryName", categoryDetails.getCategoryName());
                intent.putExtra("categoryId", categoryDetails.getCategoryid());

                intent.putExtra(CUSTOMER_ID, saved_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mcontext.startActivity(intent);
            }
        });

    }


    public int getItemCount() {
        return categoryDetailsList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView catagoryName;
        ImageView itemImages;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);
            catagoryName = itemView.findViewById(R.id.catagoryName_adapter);
            itemImages = itemView.findViewById(R.id.catagory_imageAdapter);
            catagoryName.setSelected(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClicklistener != null) {
                         Position = getAdapterPosition();

                        if (Position != RecyclerView.NO_POSITION) {
                            itemClicklistener.Onitemclick(Position);
                        }
                    }
                }
            });
        }
    }
    private void preloadImages() {
        for(CategoryDetails url: categoryDetailsList) {
            Glide.with(mcontext)
                    .load(url.getCategoryImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload();
        }
    }
}


