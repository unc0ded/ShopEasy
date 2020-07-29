package com.unc0ded.shopdeliver.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.PhoneAuthCredential;
import com.unc0ded.shopdeliver.listenerinterfaces.OnAuthenticationListener;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompletePostListener;
import com.unc0ded.shopdeliver.models.Customer;
import com.unc0ded.shopdeliver.models.Vendor;
import com.unc0ded.shopdeliver.repositories.AuthenticationRepository;
import com.unc0ded.shopdeliver.repositories.CustomerRepository;
import com.unc0ded.shopdeliver.repositories.VendorRepository;

public class LoginActivityViewModel extends ViewModel {

    private AuthenticationRepository authenticationRepo = AuthenticationRepository.getInstance();
    private CustomerRepository customerRepo = CustomerRepository.getInstance();
    private VendorRepository vendorRepo = VendorRepository.getInstance();

    private MutableLiveData<String> authStatus = new MutableLiveData<>();
    public LiveData<String> getAuthStatus(){
        return authStatus;
    }

    private MutableLiveData<String> isUploading = new MutableLiveData<>();
    public LiveData<String> getIsUploading(){
        return isUploading;
    }

    //LoginFragment
    public void signInWithEmail(String email, String password){
        authenticationRepo.authenticateForSignIn(email, password, new OnAuthenticationListener() {
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
        authenticationRepo.authenticateForSignIn(credential, new OnAuthenticationListener() {
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

    //customerSignUpMain & vendorSignUpMain
    public  void signUpWithPhone(PhoneAuthCredential credential){
        authenticationRepo.authenticateForSignUp(credential, new OnAuthenticationListener() {
            @Override
            public void onStart() {
                authStatus.setValue("processing");
            }

            @Override
            public void onSuccess(Throwable t) {
                Log.i("AuthSuccess", "" + t.getMessage());
                authStatus.setValue("verified");
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("AuthenticationException", "" + e.getMessage());
                if (("" + e.getMessage()).equals("WrongOTP"))
                    authStatus.setValue(e.getMessage());
                else
                    authStatus.setValue("failed");
            }
        });
    }

    //customerSignUpDetails & vendorSignUpDetails
    public void linkEmail(String email, String password){
        authenticationRepo.linkEmail(email, password, new OnAuthenticationListener() {
            @Override
            public void onStart() {
                authStatus.setValue("start");
            }

            @Override
            public void onSuccess(Throwable t) {
                authStatus.setValue("success");
                Log.i("EmailLinkSuccess", "" + t.getMessage());
            }

            @Override
            public void onFailure(Exception e) {
                authStatus.setValue("failed");
                Log.i("LinkEmailException", "" + e.getMessage());
            }
        });
    }

    //customerSignUpDetails
    public void registerUser(Customer newCustomer, String uid){
        customerRepo.registerCustomer(newCustomer, uid, new OnCompletePostListener() {
            @Override
            public void onStart() {
                isUploading.setValue("start");
            }

            @Override
            public void onSuccess(Throwable t) {
                isUploading.setValue("success");
                Log.i("RegisterUserThrowable", "" + t.getMessage());
            }

            @Override
            public void onFailure(Exception e) {
                isUploading.setValue("failed");
                Log.i("RegisterUserException", "" + e.getMessage());
            }
        });
    }

    //vendorSignUpDetails
    public void registerUser(Vendor newVendor, String uid){
        vendorRepo.registerVendor(newVendor, uid, new OnCompletePostListener() {
            @Override
            public void onStart() {
                isUploading.setValue("start");
            }

            @Override
            public void onSuccess(Throwable t) {
                isUploading.setValue("success");
                Log.i("RegisterUserThrowable", "" + t.getMessage());
            }

            @Override
            public void onFailure(Exception e) {
                isUploading.setValue("failed");
                Log.i("RegisterUserException", "" + e.getMessage());
            }
        });
    }
}
