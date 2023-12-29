package com.smiligenceTestenv.techUser.Adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.ItemDetails;
import com.smiligenceTestenv.techUser.bean.ItemReviewAndRatings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.smiligenceTestenv.techUser.ProductsListingActivity.reviewAndRatingsList;


public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ImageViewHolder> {
    private Context mcontext;
    private List<ItemDetails> categoryDetailsList;
    private OnItemClicklistener mlistener;
    Activity activity;
    int priority;
    int Position;
    ArrayList<ItemReviewAndRatings> reviewList = new ArrayList<>();
    ImageViewHolder holder;
    int tempNumberOfStars;
    float ratingbarResult;
    ArrayList<String> numberOfOrders = new ArrayList<>();
    ItemReviewAndRatings itemReviewAndRatings;
    ImageViewHolder imageViewHolder;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public ItemsAdapter(Context context, List<ItemDetails> catagories) {
        mcontext = context;
        categoryDetailsList = catagories;
        this.priority = priority;
        //preloadImages();

    }

    @NonNull

    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mcontext).inflate(R.layout.item_list_layout, parent, false);
        imageViewHolder = new ImageViewHolder(v, mlistener);


        return imageViewHolder;
    }

    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {


        final ItemDetails categoryDetails = categoryDetailsList.get(position);
        if (!((Activity) mcontext).isFinishing()) {
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.mipmap.ic_launcher);
//            requestOptions.error(R.mipmap.ic_launcher);
//            Glide.with(mcontext)
//                    .setDefaultRequestOptions(requestOptions)
//                    .load(categoryDetails.getItemImage()).into(holder.itemImages);

            Glide.with(mcontext)
                    .load(categoryDetails.getItemImage())
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


        holder.itemName.setText(categoryDetails.getItemName());
        holder.itemPrice.setText("₹" + categoryDetails.getItemPrice());
        holder.itemMRP.setText("₹" + categoryDetails.getMRP_Price());
        holder.itemMRP.setPaintFlags(holder.itemMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Iterator iterator = reviewAndRatingsList.iterator();
        float stars = 0;
        ArrayList<ItemReviewAndRatings> list = new ArrayList<>();
        while (iterator.hasNext()) {
            ItemReviewAndRatings reviewAndRatings = (ItemReviewAndRatings) iterator.next();
            if (reviewAndRatings.getItemId().equalsIgnoreCase(String.valueOf(categoryDetailsList.get(position).getItemId())) &&
                    "Approved".equalsIgnoreCase(reviewAndRatings.getItemRatingReviewStatus())) {


                list.add(reviewAndRatings);

                    stars = stars + reviewAndRatings.getStars();


                    holder.rating.setText("" + Math.round(stars / list.size()));
                    holder.ratingBar.setRating(Math.round(stars / list.size()));
               if( Integer.parseInt(holder.rating.getText().toString().trim())!=0){
                   holder.rating.setVisibility(View.VISIBLE);
                   holder.ratingBar.setVisibility(View.VISIBLE);
               }




            }
        }

    }


    public int getItemCount() {
        return categoryDetailsList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemPrice, itemMRP, rating;
        ImageView itemImages;
        RatingBar ratingBar;
        RelativeLayout relativeLayout;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);

            itemImages = itemView.findViewById(R.id.productImage);
            itemName = itemView.findViewById(R.id.itemname);
            itemPrice = itemView.findViewById(R.id.itemprice);
            itemMRP = itemView.findViewById(R.id.mrp);
            ratingBar = itemView.findViewById(R.id.storeRating);
            rating = itemView.findViewById(R.id.rating);
            relativeLayout = itemView.findViewById(R.id.reltive);


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
        for(ItemDetails url: categoryDetailsList) {
            if (!((Activity) mcontext).isFinishing()) {
                Glide.with(mcontext)
                        .load(url.getItemImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .preload();
            }
        }
    }
}


