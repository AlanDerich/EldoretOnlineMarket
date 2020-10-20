package com.rayson.eldoretonlinemarket.ui.orders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rayson.eldoretonlinemarket.R;
import com.rayson.eldoretonlinemarket.resources.Orders;

import java.io.File;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AllOrdersAdapters extends RecyclerView.Adapter<AllOrdersAdapters.ViewHolder>{
    Context mContext;
    List<Orders> mItemInfo;
    private File localFile;
    private Bitmap bmp;
    private ViewHolder holder1;
    private int pos;

    public AllOrdersAdapters(List<Orders> mItemInfo){
        this.mItemInfo = mItemInfo;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_all_orders,parent,false);
        mContext = parent.getContext();
        return new AllOrdersAdapters.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder1 = holder;
//        try {
//          //  getImage(mItemInfo.get(position).getImage());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        new DownloadImageTask(holder.productImage).execute(mItemInfo.get(position).getProductImage());
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.tvTitle.setText(mItemInfo.get(position).getProductNames());
        holder.tvCash.setText("KSH"+ mItemInfo.get(position).getTotalAmount());
        holder.tvQuantity.setText("Amount: "+ mItemInfo.get(position).getProductAmount());
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);
    }

    @Override
    public int getItemCount() {
        return mItemInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvCash,tvTitle,tvQuantity;
        CardView cardView;
        ImageView productImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.image_view_orders);
            tvTitle=itemView.findViewById(R.id.title_view_orders);
            tvQuantity = itemView.findViewById(R.id.quantity_view_orders);
            tvCash = itemView.findViewById(R.id.amount_view_orders);
            cardView = itemView.findViewById(R.id.card_view_orders);
            itemView.setOnClickListener(this);
//        itemView.setOnCreateContextMenuListener(this);
        }

//    @Override
//    public boolean onMenuItemClick(MenuItem menuItem) {
//        String kkk=menuItem.getTitle().toString();
//        switch (menuItem.getTitle().toString()){
//            case "View":
//                Toast.makeText(mContext, "View", Toast.LENGTH_LONG).show();
//                break;
//            case "Update":
//                Toast.makeText(mContext, "Update", Toast.LENGTH_LONG).show();
//                break;
//            case "Delete":
//                Toast.makeText(mContext, "Delete", Toast.LENGTH_LONG).show();
//                break;
//        }
//        return true;
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//        contextMenu.setHeaderTitle("Select an action");
//        contextMenu.add(0, 0,0,"View");
//        contextMenu.add(0, 1,0,"Update");
//        contextMenu.add(0, 2,0,"Delete");
//    }

        @Override
        public void onClick(View view) {
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Bitmap result) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ordersn);
            // bmImage.setImageBitmap(result);
            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(result)
                    .into(bmImage);

        }
    }
    public interface OnItemsClickListener{
        void onItemsClick(int position);
    }
}
