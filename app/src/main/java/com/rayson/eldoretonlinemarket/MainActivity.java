package com.rayson.eldoretonlinemarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser mUser;
    private View mHeaderView;
    private TextView tvUsername,tvEmail;
    private ImageView imgUser;
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        checkLogIn();
        mHeaderView=navigationView.getHeaderView(0);
        tvUsername=mHeaderView.findViewById(R.id.drawer_main_username);
        tvEmail=mHeaderView.findViewById(R.id.drawer_main_email);
        imgUser=mHeaderView.findViewById(R.id.imageView_main_drawer);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_maps_fragment,R.id.mpesaPaymentFragment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        tvEmail.setText(mUser.getEmail());
        tvUsername.setText(mUser.getDisplayName());
        //imgUser.setImageURI(mUser.getPhotoUrl());
        Picasso.with(this).load(mUser.getPhotoUrl()).into(imgUser);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_logout:
                if (mUser!=null){
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_logout));
                    signOut();
                }
                else {
                    Intent intent= new Intent(MainActivity.this,FirebaseUI.class);
                    startActivity(intent);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent= new Intent(MainActivity.this, FirebaseUI.class);
                        startActivity(intent);
                    }
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private void checkLogIn() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {

        } else {
            Intent intentLogin= new Intent(MainActivity.this,FirebaseUI.class);
            startActivity(intentLogin);
        }
    }

}