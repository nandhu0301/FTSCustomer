package com.smiligenceTestenv.techUser.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.AdvertisementDetails;

import java.util.ArrayList;

public class ViewFlipperAdapter extends BaseAdapter {

    private ArrayList<AdvertisementDetails> androidVersions;
    private Context mContext;

    public ViewFlipperAdapter(Context context, ArrayList<AdvertisementDetails> androidVersions) {
        this.mContext = context;
        this.androidVersions = androidVersions;
    }

    @Override
    public int getCount() {
        return androidVersions.size();
    }

    @Override
    public Object getItem(int position) {
        return androidVersions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.advertisementimages, parent, false);
        }

        AdvertisementDetails version = androidVersions.get(position);
        ImageView imageView = convertView.findViewById(R.id.advImage);

        if (!((Activity) mContext).isFinishing()) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher);
            requestOptions.error(R.mipmap.ic_launcher);
            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(version.getImage()).fitCenter().into(imageView);
        }

        return convertView;
    }
}