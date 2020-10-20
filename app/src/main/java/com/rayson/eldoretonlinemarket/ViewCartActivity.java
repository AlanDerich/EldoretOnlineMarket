package com.rayson.eldoretonlinemarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rayson.eldoretonlinemarket.resources.CartDetails;
import com.rayson.eldoretonlinemarket.resources.OrderIds;
import com.rayson.eldoretonlinemarket.resources.Orders;
import com.rayson.eldoretonlinemarket.touchhelpers.CartItemTouchHelperCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ViewCartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewCartActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
    //widgets
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private Button checkout;
    //vars
    CartRecyclerViewAdapter mAdapter;
    private ArrayList<CartDetails> mProducts = new ArrayList<>();
    private boolean mIsScrolling;
    private TextView tvAmount;
    private Spinner spLocations;
    List<UserDestinationInfo> mLocations;
    List<String> cats = new ArrayList<>();
    List<String> coordsLat = new ArrayList<>();
    List<String> coordsLong = new ArrayList<>();
    private int n;
    private int postn;
    private ProgressBar pbCart;
    private TextView tvTotal;
    private int amnt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setVisibility(View.INVISIBLE);
        tvTotal = findViewById(R.id.textView4_total_cash);
        pbCart = findViewById(R.id.progressBarCart);
        amnt = 0;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFab = findViewById(R.id.fabCart);
        checkout=findViewById(R.id.button2_checkout);
        mFab.setOnClickListener(this);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        getProducts();
    }

    private void getProducts(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("Cart").whereEqualTo("username",mUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mProducts = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mProducts.add(snapshot.toObject(CartDetails.class));
                            int position;
                            int size=mProducts.size();
                            for (position=0;position<size;position++){
                                int kkk=Integer.valueOf(mProducts.get(position).getAmount()) * Integer.valueOf(mProducts.get(position).getPrice());
                                amnt= amnt+ kkk;
                            }
                            tvTotal.setVisibility(View.VISIBLE);
                            tvTotal.setText("Total amount: " + amnt);
                        } else {
                            Toast.makeText(ViewCartActivity.this, "No products found in your cart.", Toast.LENGTH_LONG).show();
                            Intent intentMain= new Intent(ViewCartActivity.this,MainActivity.class);
                            startActivity(intentMain);
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewCartActivity.this, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "error "+ e);
                    }
                });
    }
    private void getCartProducts(final String orderId){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("Cart").whereEqualTo("username",mUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mProducts = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mProducts.add(snapshot.toObject(CartDetails.class));
                            addOrder(orderId);
                        } else {
                            Toast.makeText(ViewCartActivity.this, "No products found in your cart.", Toast.LENGTH_LONG).show();
                            Intent intentMain= new Intent(ViewCartActivity.this,MainActivity.class);
                            startActivity(intentMain);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewCartActivity.this, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "error "+ e);
                    }
                });
    }
    private void addOrder(String orderId){
        int size = mProducts.size();
        int position;
        for (position=0;position<size;position++){
            String product_name=mProducts.get(position).getName();
            n = position;
            String product_amount=mProducts.get(position).getAmount();
            int cash = Integer.parseInt(product_amount) * Integer.parseInt(mProducts.get(position).getPrice());

            Orders orders=new Orders(product_name,product_amount,orderId,mUser.getEmail(),String.valueOf(cash),mProducts.get(position).getImage(),0);
            db.collection("Orders").document("placed orders").collection(orderId).document(product_name)
                    .set(orders)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                            removeItemFromCart();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ViewCartActivity.this,"Not ordered. Try again later.",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
    private void removeItemFromCart() {
        int size = mProducts.size();
        int position;
        for (position =size-1; position >=0; position--){
            n = position;
            db.collection(mUser.getEmail()).document("all_cart_products").collection("Cart").document(mProducts.get(position).getName())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                            if (n==0){
                                Toast.makeText(ViewCartActivity.this, "Order has been placed.", Toast.LENGTH_SHORT).show();
                                Intent intentMain= new Intent(ViewCartActivity.this,MainActivity.class);
                                startActivity(intentMain);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ViewCartActivity.this,"Not ordered. Try again later.",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
    public static String encode(String date){
        return date.replace("/",",");
    }
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewCartActivity.this);
        alertDialog.setTitle("Place order");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_order_layout = inflater.inflate(R.layout.place_order_layout,null);

        tvAmount = add_order_layout.findViewById(R.id.tvOrderAmount);
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        tvAmount.setText("Total amount: " + fmt.format(getTotalCartAmount()));
        spLocations = add_order_layout.findViewById(R.id.spLocations);
        alertDialog.setView(add_order_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        populateSpinner();
        spLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                postn = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                String location =spLocations.getSelectedItem().toString();
                if (!location.isEmpty()){
                    String lat=coordsLat.get(postn);
                    String longtd=coordsLong.get(postn);
                    addOrderInOrderId(lat,longtd);
                }


            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }
    private int getTotalCartAmount() {
        amnt=0;
        int position;
        int size=mProducts.size();
        for (position=0;position<size;position++){
            int kkk=Integer.parseInt(mProducts.get(position).getAmount()) * Integer.parseInt(mProducts.get(position).getPrice());
            amnt = amnt + kkk;
        }
        return amnt;
    }
    private void populateSpinner() {
        db.collectionGroup("allUserLocations").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mLocations = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mLocations.add(snapshot.toObject(UserDestinationInfo.class));
                            int size = mLocations.size();
                            int position;
                            for (position=0;position<size;position++){
                                UserDestinationInfo uDetails= mLocations.get(position);
                                cats.add(uDetails.getDestinationNickName());
                                coordsLat.add(uDetails.getLatitude());
                                coordsLong.add(uDetails.getLongitude());
                            }
                            ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(
                                    ViewCartActivity.this, android.R.layout.simple_spinner_item, cats);
                            spLocations.setAdapter(usersAdapter);
                        } else {
                            Toast.makeText(ViewCartActivity.this, "No locations found. Please select the map to add a new location", Toast.LENGTH_LONG).show();
                           // Intent intentAddLocation = new Intent(ViewCartActivity.this,);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG," "+ e);
                        Toast.makeText(ViewCartActivity.this, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void addOrderInOrderId(String latitude,String longitude) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale.US);
        String formattedDate = df.format(c);
        String formattedDateAndTime = sdf.format(c);
        String[] dts= formattedDateAndTime.split(" ");
        String ttttt = dts[0];
        final int min=10;
        final int max = 1000000;
        final int random = new Random().nextInt((max-min) + 1) + min;
        final String randomOrder=mUser.getEmail() + random;
        OrderIds orderIds=new OrderIds(randomOrder,mUser.getEmail(),formattedDateAndTime,latitude,longitude,ttttt,getTotalCartAmount(),0);
        db.collection("AllOrders").document(encode(ttttt)).collection("allOrderIds").document(randomOrder)
                .set(orderIds)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                     //   Toast.makeText(ViewCartActivity.this, "Order has been placed.", Toast.LENGTH_SHORT).show();
                        getCartProducts(randomOrder);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewCartActivity.this,"Not ordered. Try again later.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initRecyclerView(){
        mAdapter = new CartRecyclerViewAdapter(this, mProducts,tvTotal);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new CartItemTouchHelperCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mAdapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        pbCart.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        //wait for the recyclerview to finish loading the views
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                    mRecyclerView.setOnScrollListener(new CartScrollListener());
                }
                else{
                    mRecyclerView.addOnScrollListener(new CartScrollListener());
                }
            }
        });
    }


    private void setFABVisibility(boolean isVisible){
        Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        if(isVisible){
            mFab.setAnimation(animFadeIn);
            mFab.setVisibility(View.VISIBLE);
        }
        else{
            mFab.setAnimation(animFadeOut);
            mFab.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isRecyclerScrollable() {
        return mRecyclerView.computeVerticalScrollRange() > mRecyclerView.getHeight();
    }

    public void setIsScrolling(boolean isScrolling){
        mIsScrolling = isScrolling;
    }

    public boolean isScrolling(){
        return mIsScrolling;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.fabCart){
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    class CartScrollListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                Log.d(TAG, "onScrollStateChanged: stopped...");
            }
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                Log.d(TAG, "onScrollStateChanged: fling.");
            }
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                Log.d(TAG, "onScrollStateChanged: touched.");
            }
            setIsScrolling(true);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(isRecyclerScrollable()){
                if(!recyclerView.canScrollVertically(1)){
                    setFABVisibility(true);
                }
                else{
                    setFABVisibility(false);
                }
            }
            setIsScrolling(true);
        }
    }

}