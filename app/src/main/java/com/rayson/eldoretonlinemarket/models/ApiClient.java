package com.rayson.eldoretonlinemarket.models;

import com.rayson.eldoretonlinemarket.ui.payment.Interceptor.AccessTokenInterceptor;
import com.rayson.eldoretonlinemarket.ui.payment.Interceptor.AuthInterceptor;
import com.rayson.eldoretonlinemarket.ui.payment.Services.STKPushService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.rayson.eldoretonlinemarket.ui.payment.Interceptor.Constants.BASE_URL;
import static com.rayson.eldoretonlinemarket.ui.payment.Interceptor.Constants.CONNECT_TIMEOUT;
import static com.rayson.eldoretonlinemarket.ui.payment.Interceptor.Constants.READ_TIMEOUT;
import static com.rayson.eldoretonlinemarket.ui.payment.Interceptor.Constants.WRITE_TIMEOUT;

public class ApiClient {
    private Retrofit retrofit;
    private boolean isDebug;
    private boolean isGetAccessToken;
    private String mAuthToken;
    private HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

    public ApiClient setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    //Set authentication token
    public ApiClient setAuthToken(String authToken) {
        mAuthToken = authToken;
        return this;
    }

    // Determine of token endpoint has been called only from the accessToken.
    public ApiClient setGetAccessToken(boolean getAccessToken) {
        isGetAccessToken = getAccessToken;
        return this;
    }


    //OKHttp Configuration
    private OkHttpClient.Builder okHttpClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor);

        return okHttpClient;
    }


    private Retrofit getRestAdapter() {

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());

        if (isDebug) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        OkHttpClient.Builder okhttpBuilder = okHttpClient();

        if (isGetAccessToken) {
            okhttpBuilder.addInterceptor(new AccessTokenInterceptor());
        }

        if (mAuthToken != null && !mAuthToken.isEmpty()) {
            okhttpBuilder.addInterceptor(new AuthInterceptor(mAuthToken));
        }

        builder.client(okhttpBuilder.build());

        retrofit = builder.build();

        return retrofit;
    }

    // Create an instance of STKPushService
    public STKPushService mpesaService() {
        return getRestAdapter().create(STKPushService.class);
    }
}
