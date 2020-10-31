package com.rayson.eldoretonlinemarket.ui.categories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rayson.eldoretonlinemarket.R;
import com.rayson.eldoretonlinemarket.resources.Category;
import com.rayson.eldoretonlinemarket.ui.categoriesitems.AllItems;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    Context mContext;
    List <Category> mItemInfo;
    private File localFile;
    private Bitmap bmp;
    private ViewHolder holder1;
    RelativeLayout rootLayout_items_fragment;
    public CategoryAdapter(List<Category> mItemInfo, RelativeLayout rootLayout_items_fragment){
        this.mItemInfo = mItemInfo;
        this.rootLayout_items_fragment=rootLayout_items_fragment;
    }
    public CategoryAdapter(List<Category> mItemInfo){
        this.mItemInfo = mItemInfo;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_categories,parent,false);
        mContext = parent.getContext();
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);
        holder1 = holder;
//        try {
//          //  getImage(mItemInfo.get(position).getImage());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
       new DownloadImageTask(holder.imgCategory).execute(mItemInfo.get(position).getImage());
//        Glide.with(mContext)
//                .setDefaultRequestOptions(requestOptions)
//                .load(bmp)
//                .into(holder.imgCategory);
        holder.tvName.setText(mItemInfo.get(position).getName());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rootLayout_items_fragment!=null){
                    rootLayout_items_fragment.removeAllViews();
                }
                Bundle args = new Bundle();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragmentStaff = new AllItems();
                FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                transactionStaff.addToBackStack(null);
                args.putString("category",mItemInfo.get(position).getName());
                fragmentStaff.setArguments(args);
                transactionStaff.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView imgCategory;
        private CardView mainLayout;
        private RelativeLayout relative_layout_category;
        private ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.textView_category_name);
            imgCategory=itemView.findViewById(R.id.imageView_category_item);
            relative_layout_category=itemView.findViewById(R.id.relative_layout_category);
            mainLayout=itemView.findViewById(R.id.card_view_category);
            progressBar=itemView.findViewById(R.id.progressBarCategory);
            progressBar.setVisibility(View.GONE);
//            progressBar.setMax(mItemInfo.size());
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {
        ImageView bmImage;

        @Override
        protected void onPreExecute() {
//            holder1.progressBar.setVisibility(View.VISIBLE);
        }

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
//            holder1.progressBar.setProgress(values[0]);
        }

        protected void onPostExecute(Bitmap result) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.loader_icon);
           // bmImage.setImageBitmap(result);
//            holder1.progressBar.setVisibility(View.GONE);
            Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(result)
                .into(bmImage);

        }
    }
}
