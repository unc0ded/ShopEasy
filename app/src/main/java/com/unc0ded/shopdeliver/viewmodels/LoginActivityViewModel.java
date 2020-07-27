package com.unc0ded.shopdeliver.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.PhoneAuthCredential;
import com.unc0ded.shopdeliver.OnAuthenticationListener;
import com.unc0ded.shopdeliver.repositories.AuthenticationRepository;

public class LoginActivityViewModel extends ViewModel {

    private AuthenticationRepository authenticationRepo = AuthenticationRepository.getInstance();

    private MutableLiveData<String> authStatus = new MutableLiveData<>();
    public LiveData<String> getAuthStatus(){
        return authStatus;
    }

    public void signInWithEmail(String email, String password){
        authenticationRepo.authenticate(email, password, new OnAuthenticationListener() {
            @Override
            public void onStart() {
                authStatus.setValue("processing");
            }

            @Override
            public void onSuccess(Throwable t) {
                Log.i("AuthSuccess", "" + t.getMessage());
                authStatus.setValue("Success." + t.getMessage());
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("AuthenticationException", "" + e.getMessage());
                authStatus.setValue("failed");
            }
        });
    }

    public void signInWithPhone(PhoneAuthCredential credential){
        authenticationRepo.authenticate(credential, new OnAuthenticationListener() {
            @Override
            public void onStart() {
                authStatus.setValue("processing");
            }

            @Override
            public void onSuccess(Throwable t) {
                Log.i("AuthSuccess", "" + t.getMessage());
                authStatus.setValue("Success." + t.getMessage());
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("AuthenticationException", "" + e.getMessage());
                if (("" + e.getMessage()).equals("WrongOTP"))
                    authStatus.setValue("WrongOTP");
                else
                    authStatus.setValue("failed");
            }
        });
    }

}
