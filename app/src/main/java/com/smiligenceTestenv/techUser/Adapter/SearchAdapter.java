package com.smiligenceTestenv.techUser.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligenceTestenv.techUser.R;
import com.smiligenceTestenv.techUser.bean.ItemDetails;

import java.util.List;

import static com.smiligenceTestenv.techUser.common.Constant.BOOLEAN_FALSE;
import static com.smiligenceTestenv.techUser.common.Constant.BOOLEAN_TRUE;
import static org.apache.commons.text.WordUtils.capitalize;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ImageViewHolder> {

    private Context mcontext;
    private List<ItemDetails> itemDetailsList;
    private OnItemClicklistener mlistener;
    ItemDetails currentItemDetails;
    int preference;




    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public SearchAdapter(Context context, List<ItemDetails> itemDetails ) {
        mcontext = context;
        itemDetailsList = itemDetails;


    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.store_listsearch, parent, BOOLEAN_FALSE);
        ImageViewHolder imageViewHolder = new ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
         currentItemDetails = itemDetailsList.get(position);




            holder.itemname.setText(capitalize(currentItemDetails.getItemName().toLowerCase()));
            holder.itemprice.setText(("₹ " + currentItemDetails.getItemPrice()));
        if (!((Activity) mcontext).isFinishing()) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher);
            requestOptions.error(R.mipmap.ic_launcher);
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(currentItemDetails.getItemImage()).fitCenter().into(holder.itemImages);

        }
    }

    @Override
    public int getItemCount() {

        if (itemDetailsList == null) {
            return 0;
        } else {
            return itemDetailsList.size();
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView itemname, itemprice,storeName;
        ImageView itemImages;



        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);
            storeName = itemView.findViewById(R.id.storenamesearch);
            itemprice = itemView.findViewById(R.id.pricesearch);
            itemname=itemView.findViewById(R.id.itemNamesearch);
            itemImages = itemView.findViewById(R.id.storeimagesearch);

            storeName.setSelected(BOOLEAN_TRUE);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClicklistener != null) {
                        int Position = getAdapterPosition();
                        if (Position != RecyclerView.NO_POSITION) {
                            itemClicklistener.Onitemclick(Position);
                        }
                    }
                }
            });


        }
    }
}