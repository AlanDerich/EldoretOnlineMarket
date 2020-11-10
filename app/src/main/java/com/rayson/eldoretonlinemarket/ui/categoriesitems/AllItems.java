package com.rayson.eldoretonlinemarket.ui.categoriesitems;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rayson.eldoretonlinemarket.R;
import com.rayson.eldoretonlinemarket.resources.Products1;
import com.rayson.eldoretonlinemarket.ui.categories.ItemsFragment;
import com.rayson.eldoretonlinemarket.ui.home.HomeFragment;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class AllItems extends Fragment implements AllItemsAdapter.OnItemsClickListener{
    String categoryId="";
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    ProgressBar progressBar;
    List<Products1> mCategory;
    private Context mContext;
    StorageReference storageReference;
    FirebaseStorage storage;
    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    Button btnUpload, btnSelect;
    Products1 newProduct;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Uri saveUri;
    public static final int PICK_IMAGE_REQUEST = 71;
    private RecyclerView rvCategories;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    private String isIherited;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (getArguments()!=null) {
            isIherited = getArguments().getString("category");
        }
        else {
            Fragment fragmentStaff = new ItemsFragment();
            FragmentTransaction transactionStaff = getParentFragmentManager().beginTransaction();
            transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
            transactionStaff.addToBackStack(null);
            transactionStaff.commit();
        }
        View root = inflater.inflate(R.layout.fragment_all_items, container, false);
        mContext = getActivity();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        rvCategories = root.findViewById(R.id.recycler_all_items);
        progressBar=root.findViewById(R.id.progress_bar_all_items);
        rvCategories.setHasFixedSize(true);
        init();
        getCategoryList();
        return root;
    }


    private void init(){
        gridLayoutManager=new GridLayoutManager(mContext,2);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvCategories.setLayoutManager(linearLayoutManager);
    }
    private void getCategoryList(){
        db.collectionGroup("AllItems").whereEqualTo("menuId",isIherited).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mCategory = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mCategory.add(snapshot.toObject(Products1.class));
                            populate();
                        } else {
                            Toast.makeText(mContext, "No Products found in this category", Toast.LENGTH_LONG).show();
                            AppCompatActivity activity = (AppCompatActivity) mContext;
                            Fragment fragmentStaff = new HomeFragment();
                            FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                            transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                            transactionStaff.addToBackStack(null);
                            transactionStaff.commit();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.e(getActivity().toString(),"Error" +e);
                    }
                });
    }
    private void populate(){
        AllItemsAdapter itemsAdapter = new AllItemsAdapter(mCategory,this);
        itemsAdapter.setHasStableIds(true);
        itemsAdapter.notifyDataSetChanged();
        rvCategories.setAdapter(itemsAdapter);
        progressBar.setVisibility(View.GONE);

    }

    private void uploadImage() {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("image/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(mContext,"Image Uploaded!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newProduct = new Products1();
                                    newProduct.setName(edtName.getText().toString());
                                    newProduct.setDescription(edtDescription.getText().toString());
                                    newProduct.setPrice(edtPrice.getText().toString());
                                    newProduct.setUsername(mUser.getEmail());
                                    if (!(isIherited.isEmpty())){
                                        newProduct.setMenuId(isIherited);
                                    }
                                    newProduct.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(mContext,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded"+progress+"%");
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data!= null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select an action");
        menu.add(0, 0,0,"View");
        menu.add(0, 1,0,"Update");
        menu.add(0, 2,0,"Delete");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()){
            case "View":
                Toast.makeText(mContext, "View", Toast.LENGTH_LONG).show();
                break;
            case "Update":
                Toast.makeText(mContext, "Update", Toast.LENGTH_LONG).show();
                break;
            case "Delete":
                Toast.makeText(mContext, "Delete", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemsClick(int position) {

        Toast.makeText(mContext, "Clicked", Toast.LENGTH_LONG).show();
    }
}