package com.rayson.eldoretonlinemarket;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rayson.eldoretonlinemarket.resources.Products1;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MainRecyclerViewAd";

    //vars
    private List<Products1> mProducts = new ArrayList<>();
    private Context mContext;

    public MainRecyclerViewAdapter(Context context, List<Products1> mProducts) {
        mContext = context;
        this.mProducts = mProducts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_feed_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvName.setText(mProducts.get(position).getName());
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(mProducts.get(position).getPrice()));
        holder.tvPrice.setText(fmt.format(price));
        holder.tvDescription.setText(mProducts.get(position).getDescription());
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ordersn);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(mProducts.get(position).getImage())
                .into(holder.image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewProductActivity.class);
//                Products1 mProd=new Products1(mProducts.get(position).getName(),mProducts.get(position).getImage(),mProducts.get(position).getDescription(),mProducts.get(position).getPrice(),mProducts.get(position).getMenuId());
                intent.putExtra("product_name", mProducts.get(position).getName());
                intent.putExtra("product_description", mProducts.get(position).getDescription());
                intent.putExtra("product_price", mProducts.get(position).getPrice());
                intent.putExtra("product_category", mProducts.get(position).getMenuId());
                intent.putExtra("product_image", mProducts.get(position).getImage());
                intent.putExtra("owner_name", mProducts.get(position).getUsername());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mProducts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        CardView cardView;
        TextView tvName,tvPrice,tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tvName = itemView.findViewById(R.id.titleMain);
            tvPrice = itemView.findViewById(R.id.priceMain);
            tvDescription = itemView.findViewById(R.id.descriptionMain);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}

