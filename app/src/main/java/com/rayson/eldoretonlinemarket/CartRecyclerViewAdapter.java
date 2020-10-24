package com.rayson.eldoretonlinemarket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rayson.eldoretonlinemarket.resources.CartDetails;
import com.rayson.eldoretonlinemarket.resources.OrderIds;
import com.rayson.eldoretonlinemarket.resources.Orders;
import com.rayson.eldoretonlinemarket.touchhelpers.ItemTouchHelperAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.rayson.eldoretonlinemarket.ViewCartActivity.encode;


public class CartRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter ,
        GestureDetector.OnGestureListener
{
    private TextView tvAmount;
    private Spinner spLocations;
    List<UserDestinationInfo> mLocations;
    List<String> cats = new ArrayList<>();
    private static final String TAG = "CartRecyclerViewAd";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
    List<String> coordsLat = new ArrayList<>();
    List<String> coordsLong = new ArrayList<>();
    private static final int PRODUCT_TYPE = 1;
    private static final int HEADER_TYPE = 2;

    //vars
    private ArrayList<CartDetails> mProducts = new ArrayList<>();
    private Context mContext;
    private ItemTouchHelper mTouchHelper;
    private GestureDetector mGestureDetector;
    private ViewHolder mSelectedHolder;
    TextView tvTotal;
    private int n;
    private int amnt;


    public CartRecyclerViewAdapter(Context context, ArrayList<CartDetails> products,TextView tvTotal) {
        mContext = context;
        this.tvTotal=tvTotal;
        mProducts = products;
        mGestureDetector = new GestureDetector(mContext, this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_cart_list_item, parent, false);
                return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        int itemViewType = getItemViewType(position);
//        if (mProducts.size()-1==position){
//            ((ViewHolder)holder).btnCheckOut.setVisibility(View.VISIBLE);
//            ((ViewHolder)holder).btnCheckOut.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showDialog();
//                }
//            });
//        }
        if (itemViewType == PRODUCT_TYPE) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);

            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(mProducts.get(position).getImage())
                    .into(((ViewHolder)holder).image);

            ((ViewHolder)holder).title.setText(mProducts.get(position).getName());
            Integer amnt =Integer.valueOf(mProducts.get(position).getAmount());
            Integer price = Integer.valueOf(mProducts.get(position).getPrice());
            Integer totals = amnt*price;
            BigDecimal sPrice = new BigDecimal(totals);
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("KSH"));
            ((ViewHolder)holder).price.setText(format.format(sPrice ));

            ((ViewHolder)holder).parentView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    ((ViewCartActivity)mContext).setIsScrolling(false);

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mSelectedHolder = ((ViewHolder)holder);
                        mGestureDetector.onTouchEvent(event);
                    }

                    return true;
                }
            });
        }
        else{
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
            headerViewHolder.sectionTitle.setText(mProducts.get(position).getName());
        }


    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    @Override
    public int getItemViewType(int position) {
            return PRODUCT_TYPE;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
//        CartDetails fromProduct = mProducts.get(fromPosition);
//        CartDetails product = new CartDetails(fromProduct);
//        mProducts.remove(fromPosition);
//        mProducts.add(toPosition, product);
//        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        removeItemFromCart(mProducts.get(position));

        mProducts.remove(mProducts.get(position));
        notifyItemRemoved(position);
    }

    private void removeItemFromCart(CartDetails cartDetails) {
        db.collection(mUser.getEmail()).document("all_cart_products").collection("Cart").document(cartDetails.getName())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(mContext, "successfully deleted!", Toast.LENGTH_LONG).show();
                        Locale locale = new Locale("en","KE");
                        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                        tvTotal.setText("Total amount: " + fmt.format(getTotalCartAmount()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private int getTotalCartAmount() {
        amnt=0;
        int position;
        int size=mProducts.size();
        for (position=0;position<size;position++){
            int kkk=Integer.valueOf(mProducts.get(position).getAmount()) * Integer.valueOf(mProducts.get(position).getPrice());
            amnt = amnt + kkk;
        }
        return amnt;
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {

        mTouchHelper = touchHelper;
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Place order");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View add_order_layout = inflater.inflate(R.layout.place_order_layout,null);

        tvAmount = add_order_layout.findViewById(R.id.tvOrderAmount);
        tvAmount.setText("Total amount: " + getTotalCartAmount());
        spLocations = add_order_layout.findViewById(R.id.spLocations);
        alertDialog.setView(add_order_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        populateSpinner();
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                String location =spLocations.getSelectedItem().toString();
                if (!location.isEmpty()){
                    String lat=coordsLat.get(i);
                    String longtd=coordsLong.get(i);
                    addOrderInOrderId(lat,longtd);
                }


            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                mLocations.clear();
            }
        });
        alertDialog.show();

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
                            }
                            ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(
                                    mContext, android.R.layout.simple_spinner_item, cats);
                            spLocations.setAdapter(usersAdapter);
                        } else {
                            Toast.makeText(mContext, "No locations found. Please select the map to add a new location", Toast.LENGTH_LONG).show();
                            // Intent intentAddLocation = new Intent(ViewCartActivity.this,);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG," "+ e);
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void addOrderInOrderId(String latitude,String longitude) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.US);
        String formattedDate = df.format(c);
        String formattedDateAndTime = sdf.format(c);
        String[] dts= formattedDateAndTime.split(" ");
        String fdddd = dts[0];
        final int min=10;
        final int max = 1000000;
        final int random = new Random().nextInt((max-min) + 1) + min;
        final String randomOrder=mUser.getEmail() + random;
        OrderIds orderIds=new OrderIds("Orders of "+ encode(formattedDate),mUser.getEmail(),formattedDateAndTime,latitude,longitude,fdddd,getTotalCartAmount(),0);
        db.collection("AllOrders").document(encode(fdddd)).collection("allOrderIds").document(randomOrder)
                .set(orderIds)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                        //   Toast.makeText(ViewCartActivity.this, "Order has been placed.", Toast.LENGTH_SHORT).show();
                        //getCartProducts(randomOrder);
                        addOrder(randomOrder);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext,"Not ordered. Try again later.",Toast.LENGTH_LONG).show();
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

                        } else {
                            Toast.makeText(mContext, "No products found in your cart.", Toast.LENGTH_LONG).show();
                            Intent intentMain= new Intent(mContext,MainActivity.class);
                            mContext.startActivity(intentMain);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
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
            Orders orders=new Orders(product_name,product_amount,orderId,mUser.getEmail(),String.valueOf(cash),mProducts.get(position).getImage(),mProducts.get(position).getOwner_name(),0);
            removeItemFromCart();
            db.collection("Orders").document("placed orders").collection(orderId).document(product_name)
                    .set(orders)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext,"Not ordered. Try again later.",Toast.LENGTH_LONG).show();
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
                                Toast.makeText(mContext, "Order has been placed.", Toast.LENGTH_SHORT).show();
                                Intent intentMain= new Intent(mContext,MainActivity.class);
                                mContext.startActivity(intentMain);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext,"Not ordered. Try again later.",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        if(!((ViewCartActivity)mContext).isScrolling()){
            mTouchHelper.startDrag(mSelectedHolder);
        }
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        Button btnCheckOut;
        TextView title, price;
        RelativeLayout parentView;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            btnCheckOut=itemView.findViewById(R.id.btn_checkout_in_adapter);
            parentView = itemView.findViewById(R.id.parent);
        }
    }

    public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {

        TextView sectionTitle;

        public SectionHeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.cart_section_header);
        }
    }
}



