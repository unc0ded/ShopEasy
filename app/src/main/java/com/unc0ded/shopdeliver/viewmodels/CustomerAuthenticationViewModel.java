package com.unc0ded.shopdeliver.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unc0ded.shopdeliver.OnCompletePostListener;
import com.unc0ded.shopdeliver.models.Customer;
import com.unc0ded.shopdeliver.repositories.CustomerRepository;

public class CustomerAuthenticationViewModel extends ViewModel {

        private CustomerRepository customerRepo = new CustomerRepository();

        private MutableLiveData<String> isUploading = new MutableLiveData<>();
        public LiveData<String> getIsUploading(){
            return isUploading;
        }

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
}
