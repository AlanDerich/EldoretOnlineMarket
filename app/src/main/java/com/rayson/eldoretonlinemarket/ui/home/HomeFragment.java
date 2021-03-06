package com.rayson.eldoretonlinemarket.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rayson.eldoretonlinemarket.MainRecyclerViewAdapter;
import com.rayson.eldoretonlinemarket.R;
import com.rayson.eldoretonlinemarket.ViewCartActivity;
import com.rayson.eldoretonlinemarket.resources.Category;
import com.rayson.eldoretonlinemarket.resources.Products1;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    MainRecyclerViewAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Category> mCategory;
    List<Products1> mProducts;
    List<String> cats = new ArrayList<>();
    Context mContext;
    //widgets
    private RecyclerView mRecyclerView;
    private Spinner spCategories;
    private RelativeLayout mCart;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar pbLoading;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mCart = root.findViewById(R.id.cart);
        pbLoading = root.findViewById(R.id.progressBarHome);
        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        spCategories=root.findViewById(R.id.spinnerCategories);
        mContext= getActivity();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        populateSpinner();
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String catName=spCategories.getSelectedItem().toString().trim();
                if (catName.equals("All Products")){
                    getProducts();
                }
                else {
                    getProducts(catName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mCart.setOnClickListener(this);
        return root;
    }

    private void populateSpinner() {
        db.collection("AllCategories").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mCategory = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mCategory.add(snapshot.toObject(Category.class));
                            int size = mCategory.size();
                            int position;
                            cats.add("All Products");
                            for (position=0;position<size;position++){
                                Category uDetails= mCategory.get(position);
                                cats.add(uDetails.getName());
                            }
                            ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(
                                    getContext(), android.R.layout.simple_spinner_item, cats);
                            spCategories.setAdapter(usersAdapter);
                        } else {
                            Toast.makeText(mContext, "No products found. Please contact admin to add a new product", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getProducts(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllItems").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mProducts = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mProducts.add(snapshot.toObject(Products1.class));
                        } else {
                            Toast.makeText(mContext, "No products found. Please add a new product", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void getProducts(String categoryName){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        mProducts=new ArrayList<>();
        initRecyclerView();
        db.collectionGroup("AllItems").whereEqualTo("menuId",categoryName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mProducts = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mProducts.add(snapshot.toObject(Products1.class));
                        } else {
                            Toast.makeText(mContext, "No products found. Please add a new product", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initRecyclerView(){
        mAdapter = new MainRecyclerViewAdapter(getContext(), mProducts);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
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
        Collections.shuffle(mProducts);
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        (mRecyclerView.getAdapter()).notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}