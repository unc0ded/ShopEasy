package com.unc0ded.shopdeliver.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.unc0ded.shopdeliver.retrofit.RetrofitClient;
import com.unc0ded.shopdeliver.utils.SessionManager;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationRepository {

    private static AuthenticationRepository instance;

    public static AuthenticationRepository getInstance(){
        if (instance == null)
            instance = new AuthenticationRepository();
        return instance;
    }

    public MutableLiveData<JsonObject> requestOtpForSignUp(SessionManager sessionManager, String type, Map<String, Object> body) {
        MutableLiveData<JsonObject> requestStatus = new MutableLiveData<>();
        RetrofitClient.getClient(sessionManager).getOtp(type, body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    requestStatus.postValue(response.body());
                }
                else {
                    Log.e("OtpRequest", response.message());
                    requestStatus.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("OtpRequest", Objects.requireNonNull(t.getMessage()));
                requestStatus.postValue(null);
            }
        });
        return requestStatus;
    }

    public MutableLiveData<JsonObject> verifyOtpForSignUp(SessionManager sessionManager, Map<String, Object> body) {
        MutableLiveData<JsonObject> verificationResult = new MutableLiveData<>();
        RetrofitClient.getClient(sessionManager).verifyOtp(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    verificationResult.postValue(response.body());
                }
                else {
                    Log.e("VerificationResult", response.message());
                    verificationResult.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("VerificationResult", Objects.requireNonNull(t.getMessage()));
                verificationResult.postValue(null);
            }
        });
        return verificationResult;
    }

    public MutableLiveData<JsonObject> requestOtpForLogin(SessionManager sessionManager, Map<String, Object> body) {
        MutableLiveData<JsonObject> requestStatus = new MutableLiveData<>();
        RetrofitClient.getClient(sessionManager).getLoginOtp(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                    requestStatus.postValue(response.body());
                else {
                    Log.e("LoginOtpRequest", response.message());
                    requestStatus.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("LoginOtpRequest", Objects.requireNonNull(t.getMessage()));
                requestStatus.postValue(null);
            }
        });
        return requestStatus;
    }

    public MutableLiveData<JsonObject> verifyOtpForLogin(SessionManager sessionManager, Map<String, Object> body) {
        MutableLiveData<JsonObject> verificationResult = new MutableLiveData<>();
        RetrofitClient.getClient(sessionManager).verifyLoginOtp(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                    verificationResult.postValue(response.body());
                else {
                    Log.e("LoginVerificationResult", response.message());
                    verificationResult.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("LoginVerificationResult", Objects.requireNonNull(t.getMessage()));
                verificationResult.postValue(null);
            }
        });
        return verificationResult;
    }
}
