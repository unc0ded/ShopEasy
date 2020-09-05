package com.unc0ded.shopeasy.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.unc0ded.shopeasy.models.Customer;
import com.unc0ded.shopeasy.models.Vendor;
import com.unc0ded.shopeasy.repositories.AuthenticationRepository;
import com.unc0ded.shopeasy.repositories.CustomerRepository;
import com.unc0ded.shopeasy.repositories.VendorRepository;
import com.unc0ded.shopeasy.utils.SessionManager;

import java.util.Map;

public class LoginActivityViewModel extends AndroidViewModel {

    private AuthenticationRepository authenticationRepo = AuthenticationRepository.getInstance();
    private CustomerRepository customerRepo = CustomerRepository.getInstance();
    private VendorRepository vendorRepo = VendorRepository.getInstance();

    private MutableLiveData<Integer> loginMethod = new MutableLiveData<>();
    private MutableLiveData<JsonObject> otpRequestStatus = new MutableLiveData<>();
    private MutableLiveData<JsonObject> verificationStatus = new MutableLiveData<>();
    private MutableLiveData<Customer> newCustomerCreated = new MutableLiveData<>();
    private MutableLiveData<Vendor> newVendorCreated = new MutableLiveData<>();
    private MutableLiveData<JsonObject> loginOtpRequestStatus = new MutableLiveData<>();
    private MutableLiveData<JsonObject> loginVerificationStatus = new MutableLiveData<>();

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
    }

    // Switching between email and phone login in login fragment
    public LiveData<Integer> getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(Integer method) {
        loginMethod.postValue(method);
    }

    // Phone verification for sign up, string parameter 'type' differentiates between vendor and customer registration
    public void requestOtp(SessionManager sessionManager, String type, Map<String, Object> body) {
        otpRequestStatus = authenticationRepo.requestOtpForSignUp(sessionManager, type, body);
    }

    public LiveData<JsonObject> getOtpRequestStatus() {
        return otpRequestStatus;
    }

    public void verifyOtp(SessionManager sessionManager, Map<String, Object> bodyMap) {
        verificationStatus = authenticationRepo.verifyOtpForSignUp(sessionManager, bodyMap);
    }

    public LiveData<JsonObject> getVerificationResult() {
        return verificationStatus;
    }

    // Register new customer in customerSignUpDetails fragment
    public LiveData<Customer> registerCustomer(SessionManager sessionManager, Customer customer) {
        newCustomerCreated = customerRepo.registerCustomer(sessionManager, customer);
        return newCustomerCreated;
    }

    // Register new vendor in vendorSignUpDetails fragment
    public LiveData<Vendor> registerVendor(SessionManager sessionManager, Vendor vendor) {
        newVendorCreated = vendorRepo.registerVendor(sessionManager, vendor);
        return newVendorCreated;
    }

    // Login (Common for Customer & Vendor)
    public void loginOtpRequest(SessionManager sessionManager, Map<String, Object> body) {
        loginOtpRequestStatus = authenticationRepo.requestOtpForLogin(sessionManager, body);
    }

    public LiveData<JsonObject> getLoginOtpRequestStatus() {
        return loginOtpRequestStatus;
    }

    public void verifyLoginOtp(SessionManager sessionManager, Map<String, Object> body) {
        loginVerificationStatus = authenticationRepo.verifyOtpForLogin(sessionManager, body);
    }

    public LiveData<JsonObject> getLoginVerificationResult() {
        return loginVerificationStatus;
    }
}