package com.rayson.eldoretonlinemarket.ui.orders;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rayson.eldoretonlinemarket.R;
import com.rayson.eldoretonlinemarket.resources.OrderIds;
import com.rayson.eldoretonlinemarket.resources.Orders;
import com.rayson.eldoretonlinemarket.ui.slideshow.SlideshowFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private static final String TAG = "MainRecyclerViewAd";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //vars
    private List<OrderIds> mOrders;
    private Context mContext;
    private RelativeLayout mainL;
    private TextView tvLocation;
    private TextView tvDate;
    private TextView tvStatus;
    private RecyclerView rvViewOrder;
    OrdersFragment ordersFragment;
    AllOrdersAdapters mAdapter;
    List<Orders> mAllOrders;
    public OrdersAdapter(Context context, List<OrderIds> orders, RelativeLayout mainL, OrdersFragment ordersFragment) {
        mContext = context;
        this.ordersFragment=ordersFragment;
        mOrders = orders;
        this.mainL=mainL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_orders, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.tvName.setText(mOrders.get(position).getOrderId());
        holder.tvDate.setText(mOrders.get(position).getDateAndTime());
        holder.tvAmount.setText(fmt.format(mOrders.get(position).getTotalAmount()));
        if (mOrders.get(position).getStatus()==0){
            holder.tvStatus.setText("Pending");
        }
        else {
            holder.tvStatus.setText("Approved");
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                holder.rv11.removeAllViews();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragmentStaff = new SlideshowFragment();
                FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                transactionStaff.addToBackStack(null);
                args.putString("orderId",mOrders.get(position).getOrderId());
                args.putString("location",mOrders.get(position).getLatitude() + " , " + mOrders.get(position).getLongitude());
                args.putInt("status",mOrders.get(position).getStatus());
                args.putString("date",mOrders.get(position).getDateAndTime());
                fragmentStaff.setArguments(args);
                transactionStaff.commit();
            }
        });
    }
    private void initRecyclerView(){
        mAdapter = new AllOrdersAdapters(mAllOrders);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(mContext);
        rvViewOrder.setLayoutManager(linearLayoutManager);
        rvViewOrder.setAdapter(mAdapter);
//        pbOrders.setVisibility(View.GONE);
        rvViewOrder.setVisibility(View.VISIBLE);
    }
    private void viewOrder(int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(mOrders.get(position).getOrderId());
//        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = ordersFragment.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.view_order_layout,null);
        tvDate = add_menu_layout.findViewById(R.id.textView_view_order_date);
        tvDate.setText(mOrders.get(position).getDateAndTime());
        tvLocation = add_menu_layout.findViewById(R.id.textView_view_order_location);
        tvLocation.setText(mOrders.get(position).getLatitude() + " , " + mOrders.get(position).getLongitude());
        tvStatus = add_menu_layout.findViewById(R.id.textView_view_order_status);
        if (mOrders.get(position).getStatus()==0){
            tvStatus.setText("Pending");
        }
        else {
            tvStatus.setText("Approved");
        }
        rvViewOrder = add_menu_layout.findViewById(R.id.rv_view_orders_list);
        initRecyclerView();
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        alertDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static String encode(String date){
        return date.replace("/",",");
    }


    @Override
    public int getItemCount() {
        return mOrders.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,tvDate,tvAmount,tvStatus;
        CardView cardView;
        RelativeLayout rv11;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_order_order_id);
            tvDate=itemView.findViewById(R.id.textView_order_date);
            tvAmount=itemView.findViewById(R.id.textView_order_amount);
            tvStatus = itemView.findViewById(R.id.textView_order_status);
            cardView = itemView.findViewById(R.id.card_view_orders);
            rv11 = itemView.findViewById(R.id.rv1111);
        }
    }
}