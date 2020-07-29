package com.unc0ded.shopdeliver.views.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.unc0ded.shopdeliver.databinding.FragmentVendorSignUpMainBinding;
import com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class vendorSignUpMain extends Fragment {

    FragmentVendorSignUpMainBinding binding;
    LoginActivityViewModel loginActivityVM = new LoginActivityViewModel();

    private static final String STATUS_VERIFIED = "verified";
    private static final String STATUS_FAILED = "failed";
    private static final String STATUS_WRONG_OTP = "WrongOTP";
    private static final String STATUS_PROCESSING = "processing";

    private String sentOTP;

    private View rootView;

    //Firebase Objects
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks vendorCallbacks;

    //empty constructor
    public vendorSignUpMain() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginActivityVM.getAuthStatus().observe(this, status -> {
            switch(status){
                case STATUS_VERIFIED:
                    Toast.makeText(requireContext(), "Phone number verified!", Toast.LENGTH_SHORT).show();
                    vendorSignUpMainDirections.ActionVendorSignUpMainToVendorSignUpDetails action = vendorSignUpMainDirections.actionVendorSignUpMainToVendorSignUpDetails("+91 "+binding.phoneNumber.getText().toString().trim());
                    Navigation.findNavController(rootView).navigate(action);
                    binding.progressbar.setVisibility(View.GONE);
                    break;
                case STATUS_WRONG_OTP:
                    binding.otp.setEnabled(true);
                    binding.validateOtpBtn.setEnabled(true);
                    binding.progressbar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Invalid OTP!", Toast.LENGTH_SHORT).show();
                    break;
                case STATUS_FAILED:
                    binding.otp.setEnabled(false);
                    binding.validateOtpBtn.setEnabled(false);
                    binding.phoneNumber.setEnabled(true);
                    binding.otpBtn.setEnabled(true);
                    Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    binding.progressbar.setVisibility(View.GONE);
                    break;
                case STATUS_PROCESSING:
                    binding.progressbar.setVisibility(View.VISIBLE);
                    break;
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendorSignUpMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;

        binding.otp.setEnabled(false);
        binding.validateOtpBtn.setEnabled(false);

        binding.otpBtn.setOnClickListener(v -> {
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected){
                AlertDialog.Builder otpAlertBuilder = new AlertDialog.Builder(requireContext());
                otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.")
                        .setCancelable(true)
                        .setPositiveButton("Ok",
                                (dialog, which) -> {
                                    if((Objects.requireNonNull(binding.phoneNumber.getText()).toString().trim().length()) != 10)
                                    {
                                        Toast.makeText(getContext(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+ Objects.requireNonNull(binding.phoneNumber.getText()).toString()
                                                ,60, TimeUnit.SECONDS
                                                ,requireActivity()
                                                ,vendorCallbacks);
                                        binding.progressbar.setVisibility(View.VISIBLE);
                                    }
                                    dialog.cancel();
                                }).setNegativeButton("Cancel",
                        (dialog, which) -> dialog.cancel()).create().show();
            }else
                Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_LONG).show();
        });

        vendorCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                binding.progressbar.setVisibility(View.GONE);
                Log.d("customerCallbacks", "onVerificationCompleted:" + phoneAuthCredential);
                loginActivityVM.signUpWithPhone(phoneAuthCredential);
//                signInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                binding.progressbar.setVisibility(View.GONE);
                Log.d("vendorCallbacks", "onVerificationFailed", e);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                binding.phoneNumber.setEnabled(true);
                binding.otpBtn.setEnabled(true);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.d("vendorCallbacks","onVerificationFailed: Invalid Number");
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.d("vendorCallbacks","onVerificationFailed: SMS quota exceeded");
                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                binding.progressbar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "OTP has been sent", Toast.LENGTH_SHORT).show();
                sentOTP = s;
                binding.validateOtpBtn.setEnabled(true);
                binding.otp.setEnabled(true);
            }
        };

        binding.validateOtpBtn.setOnClickListener(v -> {
            if(Objects.requireNonNull(binding.otp.getText()).toString().isEmpty()) {
                Toast.makeText(getContext(), "Please enter OTP.", Toast.LENGTH_SHORT).show();
            }
            else {
                PhoneAuthCredential vendorCredential = PhoneAuthProvider.getCredential(sentOTP, Objects.requireNonNull(binding.otp.getText()).toString());
                loginActivityVM.signUpWithPhone(vendorCredential);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.phoneNumber.setEnabled(true);
        binding.otp.setEnabled(true);
        binding.otpBtn.setEnabled(true);
        binding.validateOtpBtn.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }
}
