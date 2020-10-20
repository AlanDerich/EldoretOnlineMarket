package com.rayson.eldoretonlinemarket.ui.orders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rayson.eldoretonlinemarket.R;
import com.rayson.eldoretonlinemarket.ViewCartActivity;
import com.rayson.eldoretonlinemarket.resources.Category;
import com.rayson.eldoretonlinemarket.resources.OrderIds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdersFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    OrdersAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Category> mCategory;
    List<OrderIds> mOrderIds;
    List<String> cats = new ArrayList<>();
    Context mContext;
    RelativeLayout mainL;
    private FirebaseUser mUser;
    //widgets
    private RecyclerView mRecyclerView;
    private RelativeLayout mCart;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar pbOrders;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_orders, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_viewViewOrders);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mCart = root.findViewById(R.id.cartViewOrders);
        pbOrders = root.findViewById(R.id.progressBarOrders);
        mainL = root.findViewById(R.id.mainLayout_fragment_orders);
        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layoutViewOrders);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mContext= getActivity();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        getOrders();
        mCart.setOnClickListener(this);
        return root;
    }

    private void getOrders(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("allOrderIds").whereEqualTo("username",mUser.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOrderIds = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mOrderIds.add(snapshot.toObject(OrderIds.class));
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
        mAdapter = new OrdersAdapter(getContext(), mOrderIds,mainL,this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        pbOrders.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
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

    @Override
    public void onRefresh() {
        Collections.shuffle(mOrderIds);
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        (mRecyclerView.getAdapter()).notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}