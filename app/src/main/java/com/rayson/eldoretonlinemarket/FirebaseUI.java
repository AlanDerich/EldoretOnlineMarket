package com.rayson.eldoretonlinemarket;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;


public class FirebaseUI extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);
        getConnectivityStatus(this);
        if (getConnectivityStatus(this)){
        themeAndLogo();
            }
        else {
            Toast.makeText(this, "No network connection!", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        Toast.makeText(context, "No network connection!", Toast.LENGTH_SHORT).show();
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                loginUser();
            }
            if(resultCode == RESULT_CANCELED){
                displayMessage(("Sign in failed"));
                loginUser();
            }
            return;
        }
        displayMessage("Unknown response");
    }
    private void loginUser(){
        Intent loginIntent = new Intent(FirebaseUI.this, MainActivity.class);
        startActivity(loginIntent);
    }
    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // [END auth_fui_result]
    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
               // new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)      // Set logo drawable
                        .setTosAndPrivacyPolicyUrls(
                        "https://example.com/terms.html",
                        "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_theme_logo]
    }
}