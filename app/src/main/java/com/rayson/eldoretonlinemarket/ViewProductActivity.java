package com.rayson.eldoretonlinemarket;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rayson.eldoretonlinemarket.resources.CartDetails;
import com.rayson.eldoretonlinemarket.resources.Products1;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Currency;


/**
 * Created by User on 3/3/2018.
 */

public class ViewProductActivity extends AppCompatActivity implements View.OnTouchListener, GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,View.OnClickListener, View.OnDragListener {
    private static final String TAG = "ViewProductActivity";

    //widgets
    private FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
    private RelativeLayout mAddToCart, mCart;
    private ImageView mCartIcon, mPlusIcon,productImage;
    private ImageButton btnAdd,btnRemove;
    private TextView tvPrice,selectedItems,tvName,tvProductDescription;
    //vars
    private String product_name,product_description,product_price,product_category,product_image,owner_name;
    private GestureDetector mGestureDetector;
    private Rect mCartPositionRectangle;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        mAddToCart = findViewById(R.id.add_to_cart);
        productImage=findViewById(R.id.imageViewViewImageImage);
        btnAdd=findViewById(R.id.imageButtonAddItem);
        btnRemove=findViewById(R.id.imageButtonSubtractItem);
        tvPrice=findViewById(R.id.tv_view_product_price);
        tvName=findViewById(R.id.tv_view_product_name);
        tvProductDescription=findViewById(R.id.product_description);
        selectedItems=findViewById(R.id.textViewCurrentProductsSelected);
        mCart = findViewById(R.id.cart);
        mPlusIcon = findViewById(R.id.plus_image);
        mCartIcon = findViewById(R.id.cart_image);

        productImage.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this, this);
        mCart.setOnClickListener(this);
        mAddToCart.setOnClickListener(this);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(selectedItems.getText().toString().trim());
                int next= current+1;
                selectedItems.setText(String.valueOf(next));
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(selectedItems.getText().toString().trim());
                if (current>0){
                    int next= current-1;
                    selectedItems.setText(String.valueOf(next));
                }
            }
        });

        getIncomingIntent();
        initViews();
    }

    private void getIncomingIntent(){
        Intent intent = getIntent();
        if(intent.hasExtra("product_name")){
            product_name = intent.getStringExtra("product_name");
            product_description = intent.getStringExtra("product_description");
            product_price = intent.getStringExtra("product_price");
            product_category = intent.getStringExtra("product_category");
            product_image = intent.getStringExtra("product_image");
            owner_name =intent.getStringExtra("owner_name");
        }
    }

    private void initViews(){
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("KSH"));
      tvName.setText(product_name);
      tvProductDescription.setText(product_description);
      tvPrice.setText(format.format(Integer.valueOf(product_price)));
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.loader_icon);
        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(product_image)
                .into(productImage);

    }

    private void getCartPosition(){
        mCartPositionRectangle = new Rect();
        mCart.getGlobalVisibleRect(mCartPositionRectangle);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        mCartPositionRectangle.left = mCartPositionRectangle.left - Math.round((int)(width * 0.18));
        mCartPositionRectangle.top = 0;
        mCartPositionRectangle.right = width;
        mCartPositionRectangle.bottom = mCartPositionRectangle.bottom - Math.round((int)(width * 0.03));
    }

    private void setDragMode(boolean isDragging){
        if(isDragging){
            mCartIcon.setVisibility(View.INVISIBLE);
            mPlusIcon.setVisibility(View.VISIBLE);
        }
        else{
            mCartIcon.setVisibility(View.VISIBLE);
            mPlusIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void addCurrentItemToCart(){
        CartDetails selectedProduct=new CartDetails(product_name,product_image,selectedItems.getText().toString(),product_price,product_category,mUser.getEmail(),owner_name);
        db.collection(mUser.getEmail()).document("all_cart_products").collection("Cart").document(product_name)
                .set(selectedProduct)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                        Toast.makeText(ViewProductActivity.this, "added to cart", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewProductActivity.this,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                    }
                });
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cart:{
                //open Cart Activity
                Intent intent = new Intent(this, ViewCartActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.add_to_cart:{
                addCurrentItemToCart();
                break;
            }
        }
    }


    /*
        OnTouch
     */

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        getCartPosition();

        if(view.getId() == R.id.imageViewViewImageImage){
            mGestureDetector.onTouchEvent(motionEvent);
        }

//        int action = motionEvent.getAction();
//
//        switch(action) {
//            case (MotionEvent.ACTION_DOWN):
//                Log.d(TAG, "Action was DOWN");
//                return false;
//            case (MotionEvent.ACTION_MOVE):
//                Log.d(TAG, "Action was MOVE");
//                return false;
//            case (MotionEvent.ACTION_UP):
//                Log.d(TAG, "Action was UP");
//                return false;
//            case (MotionEvent.ACTION_CANCEL):
//                Log.d(TAG, "Action was CANCEL");
//                return false;
//            case (MotionEvent.ACTION_OUTSIDE):
//                Log.d(TAG, "Movement occurred outside bounds " +
//                        "of current screen element");
//                return false;
//        }

        return false;
    }

    /*
        GestureDetector
     */

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.d(TAG, "onDown: called");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Log.d(TAG, "onShowPress: called.");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Log.d(TAG, "onSingleTapUp: called.");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent,
                            MotionEvent motionEvent1,
                            float v, float v1) {
        Log.d(TAG, "onScroll: called.");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.d(TAG, "onLongPress: called.");

//        Products1 selectedProduct=new Products1(product_name,product_image,product_description,product_price,product_category);
//        // Instantiates the drag shadow builder.
//        View.DragShadowBuilder myShadow = new MyDragShadowBuilder(
//                productImage, product_image);
//
//        // Starts the drag
//        productImage.startDrag(null,  // the data to be dragged
//                myShadow,  // the drag shadow builder
//                null,      // no need to use local data
//                0          // flags (not currently used, set to 0)
//        );
//
//        myShadow.getView().setOnDragListener(this);
    }

    @Override
    public boolean onFling(MotionEvent motionEvent,
                           MotionEvent motionEvent1,
                           float v, float v1) {
        Log.d(TAG, "onFling: called.");
        return false;
    }

    /*
        DoubleTap
     */

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        Log.d(TAG, "onSingleTapConfirmed: called.");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        Log.d(TAG, "onDoubleTap: called.");
//        inflateFullScreenProductFragment();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        Log.d(TAG, "onDoubleTapEvent: called.");
        return false;
    }

    /*
        OnDragListener
     */
    @Override
    public boolean onDrag(View view, DragEvent event) {

        switch(event.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                Log.d(TAG, "onDrag: drag started.");

                setDragMode(true);

                return true;

            case DragEvent.ACTION_DRAG_ENTERED:

                return true;

            case DragEvent.ACTION_DRAG_LOCATION:

                Point currentPoint = new Point(Math.round(event.getX()), Math.round(event.getY()));
//                Log.d(TAG, "onDrag: x: " + currentPoint.x + ", y: " + currentPoint.y );

                if(mCartPositionRectangle.contains(currentPoint.x, currentPoint.y)){
                    mCart.setBackgroundColor(this.getResources().getColor(R.color.blue2));
                }
                else{
                    mCart.setBackgroundColor(this.getResources().getColor(R.color.blue1));
                }

                return true;

            case DragEvent.ACTION_DRAG_EXITED:

                return true;

            case DragEvent.ACTION_DROP:

                Log.d(TAG, "onDrag: dropped.");

                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                Log.d(TAG, "onDrag: ended.");

                Drawable background = mCart.getBackground();
                if (background instanceof ColorDrawable) {
                    if (((ColorDrawable) background).getColor() == getResources().getColor(R.color.blue2)) {
                        addCurrentItemToCart();
                    }
                }
                mCart.setBackground(this.getResources().getDrawable(R.drawable.blue_onclick_dark));
                setDragMode(false);
                return true;

            // An unknown action type was received.
            default:
                Log.e(TAG,"Unknown action type received by OnStartDragListener.");
                break;

        }
        return false;
    }
}
