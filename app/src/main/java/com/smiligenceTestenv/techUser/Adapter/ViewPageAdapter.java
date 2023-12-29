package com.smiligenceTestenv.techUser.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.smiligenceTestenv.techUser.ProductDescriptionActivity;
import com.smiligenceTestenv.techUser.ProductsListingActivity;
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.AdvertisementDetails;


import java.util.ArrayList;

public class ViewPageAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    ArrayList<AdvertisementDetails> stringList=new ArrayList<>();

    public ViewPageAdapter(Context context, ArrayList<AdvertisementDetails> stringList) {
        this.context = context;
        this.stringList=stringList;
        //preloadImages();

    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.advertisementimages, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.advImage);

        AdvertisementDetails categoryDetails = stringList.get(position);
        if (!((Activity) context).isFinishing()) {
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.mipmap.ic_launcher);
//            requestOptions.error(R.mipmap.ic_launcher);
//
//            Glide.with(context)
//                    .setDefaultRequestOptions(requestOptions)
//                    .load(categoryDetails.getImage()).into(imageView);


            Glide.with(context)
                    .load(categoryDetails.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    }).into(imageView);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (categoryDetails.getItemName().equals("Select Category")){

                        Intent intent = new Intent(view.getContext(), ProductsListingActivity.class);
                        intent.putExtra("categoryName", categoryDetails.getAdvertisingBrandName());
                        intent.putExtra("categoryId", categoryDetails.getCategoryId());
                        view.getContext().startActivity(intent);
                    }
                    else {

                        Intent intent = new Intent(view.getContext(), ProductDescriptionActivity.class);
                        intent.putExtra("adapterIntent", "adapterIntent");
                        intent.putExtra("categoryName", categoryDetails.getAdvertisingBrandName());
                        intent.putExtra("categoryId", categoryDetails.getCategoryId());
                        intent.putExtra("itemName", categoryDetails.getItemName());
                        intent.putExtra("itemId",String.valueOf(categoryDetails.getItemId()));
                        view.getContext().startActivity(intent);
                    }





                }
            });
        }
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);




        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
    private void preloadImages() {
        for(AdvertisementDetails url: stringList) {
            Glide.with(context)
                    .load(url.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload();
        }
    }
}



