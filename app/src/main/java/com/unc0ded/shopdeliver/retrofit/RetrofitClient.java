package com.unc0ded.shopdeliver.retrofit;

import android.content.Context;
import android.content.SharedPreferences;

import com.unc0ded.shopdeliver.utils.SessionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitApiInterface apiInterface;
    private static final String BASE_URL = "https://pure-brook-58667.herokuapp.com/";

    public static RetrofitApiInterface getClient(SessionManager manager) {
        if (apiInterface == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getHttpClient(manager))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = retrofit.create(RetrofitApiInterface.class);
        }
        return apiInterface;
    }
    private static OkHttpClient getHttpClient(SessionManager manager) {
        String bearerToken = manager.fetchAuthToken();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(chain -> {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();

            if (bearerToken != null)
                builder.addHeader("Authorization", "bearer " + bearerToken);
            //for auth tokens
            //String authToken = App.getInstance().getSessionManager().getX_AUTH_TOKEN();
            //if (!StringUtils.isEmpty(authToken)) {
            //    builder.addHeader("X-AUTH-TOKEN", authToken);
            //}
            request = builder.build();
            return chain.proceed(request);
        });
        return httpClient.build();
    }
}

