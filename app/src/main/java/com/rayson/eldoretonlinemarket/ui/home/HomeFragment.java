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
import com.rayson.eldoretonlinemarket.ui.categories.CategoryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    MainRecyclerViewAdapter mAdapter;
    CategoryAdapter mCatsAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Category> mCategory;
    List<Products1> mProducts;
    Context mContext;
    //widgets
    private RecyclerView mRecyclerView,mRecyclerViewCategories;
    private RelativeLayout mCart;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar pbLoading;
    private RelativeLayout relative_layout_home;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view);
        relative_layout_home = root.findViewById(R.id.relative_layout_home);
        mRecyclerViewCategories = root.findViewById(R.id.recycler_categories);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mRecyclerViewCategories.setVisibility(View.INVISIBLE);
        mCart = root.findViewById(R.id.cart);
        pbLoading = root.findViewById(R.id.progressBarHome);
        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        mContext= getActivity();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        populateSpinner();
        getProducts();
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

        mCatsAdapter = new CategoryAdapter(mCategory,relative_layout_home);
        GridLayoutManager layoutManagerCats = new GridLayoutManager(getContext(), 3);
        mRecyclerViewCategories.setLayoutManager(layoutManagerCats);
        mRecyclerViewCategories.setAdapter(mCatsAdapter);
        mRecyclerViewCategories.setVisibility(View.VISIBLE);
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