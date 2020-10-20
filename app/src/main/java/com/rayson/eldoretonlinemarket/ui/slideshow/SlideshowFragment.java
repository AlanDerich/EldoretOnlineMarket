package com.rayson.eldoretonlinemarket.ui.slideshow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rayson.eldoretonlinemarket.R;
import com.rayson.eldoretonlinemarket.ViewCartActivity;
import com.rayson.eldoretonlinemarket.resources.OrderIds;
import com.rayson.eldoretonlinemarket.resources.Orders;
import com.rayson.eldoretonlinemarket.ui.orders.AllOrdersAdapters;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SlideshowFragment extends Fragment implements View.OnClickListener{

private static final int NUM_COLUMNS = 2;

        //vars
        AllOrdersAdapters mAdapter;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<OrderIds> mOrderIds;
        List<String> cats = new ArrayList<>();
        Context mContext;
        RelativeLayout mainL;
private FirebaseUser mUser;
//widgets
    List<Orders> mAllOrders;
private RelativeLayout mCart;
private SwipeRefreshLayout mSwipeRefreshLayout;
private ProgressBar pbOrders;
    private TextView tvLocation;
    private TextView tvDate;
    private TextView tvStatus,tvOrderId;
    private RecyclerView rvViewOrder;
    private String orderID,location,date;
    private int status;

    public View onCreateView(@NonNull LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.view_order_layout, container, false);
    tvDate = root.findViewById(R.id.textView_view_order_date);
//    tvDate.setText(mOrders.get(position).getDateAndTime());
    tvLocation = root.findViewById(R.id.textView_view_order_location);
//    tvLocation.setText(mOrders.get(position).getLatitude() + " , " + mOrders.get(position).getLongitude());
    tvStatus = root.findViewById(R.id.textView_view_order_status);
        tvOrderId = root.findViewById(R.id.textView4_order_id);
//    if (mOrders.get(position).getStatus()==0){
//        tvStatus.setText("Pending");
//    }
//    else {
//        tvStatus.setText("Approved");
//    }
    rvViewOrder = root.findViewById(R.id.rv_view_orders_list);
//        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layoutViewOrders);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mContext= getActivity();
    if (getArguments()!=null) {
        orderID = getArguments().getString("orderId");
        tvOrderId.setText(orderID);
        location = getArguments().getString("location");
        tvLocation.setText(location);
        status = getArguments().getInt("status");
        if (status==0){
       tvStatus.setText("Pending");
    }
    else {
        tvStatus.setText("Approved");
    }
        date = getArguments().getString("date");
    tvDate.setText(date);

    }
        getOrders();
        return root;
        }

    private void getOrders(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup(orderID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mAllOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mAllOrders.add(snapshot.toObject(Orders.class));
                        } else {
                            Toast.makeText(mContext, "No orders found. Orders you place will appear here", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("OrdersFragment", "error " + e);
                    }
                });
    }

private void initRecyclerView(){
        mAdapter = new AllOrdersAdapters(mAllOrders);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        rvViewOrder.setLayoutManager(linearLayoutManager);
        rvViewOrder.setAdapter(mAdapter);
//        pbOrders.setVisibility(View.GONE);
        rvViewOrder.setVisibility(View.VISIBLE);
        }

@Override
public void onClick(View view) {
        switch(view.getId()){
        case R.id.cart:{
        //open Cart Activity
        Intent intent = new Intent(view.getContext(), ViewCartActivity.class);
        startActivity(intent);
        break;
        }
        }
        }


        }