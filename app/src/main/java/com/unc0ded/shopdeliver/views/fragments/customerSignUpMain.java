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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unc0ded.shopdeliver.ShopEasy;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerSignUpMainBinding;
import com.unc0ded.shopdeliver.utils.SessionManager;
import com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel;
import com.unc0ded.shopdeliver.views.widgets.OtpWidget;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class customerSignUpMain extends Fragment {

    FragmentCustomerSignUpMainBinding binding;
    LoginActivityViewModel loginActivityVM;
    SessionManager sessionManager;

    private String phoneNumber;
    AlertDialog otpDialog;

    //empty constructor
    public customerSignUpMain() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerSignUpMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginActivityVM = new ViewModelProvider(requireActivity()).get(LoginActivityViewModel.class);
        sessionManager = ((ShopEasy)requireActivity().getApplication()).getSessionManager();

        binding.otpBtn.setOnClickListener(v -> {
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected){
                MaterialAlertDialogBuilder otpAlertBuilder = new MaterialAlertDialogBuilder(requireContext());
                otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.")
                        .setCancelable(true)
                        .setPositiveButton("Accept",
                                (dialog, which) -> {
                                    if((Objects.requireNonNull(binding.phoneNumber.getText()).toString().trim().length()) != 10) {
                                        Toast.makeText(getContext(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        phoneNumber = "+91" + Objects.requireNonNull(binding.phoneNumber.getText()).toString();
                                        Map<String, Object> bodyMap = new HashMap<>();
                                        bodyMap.put("phone", phoneNumber);
                                        loginActivityVM.requestOtp(sessionManager, "customer", bodyMap);
                                        binding.progressbar.setVisibility(View.VISIBLE);
                                    }
                                    dialog.dismiss();
                                })
                        .setNegativeButton("Cancel",
                                (dialog, which) -> dialog.cancel()).show();
            }
            else
                Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_LONG).show();
        });

        loginActivityVM.getOtpRequestStatus().observe(getViewLifecycleOwner(), jsonObject -> {
            if (jsonObject != null && jsonObject.get("error").isJsonNull()) {
                binding.progressbar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "OTP has been sent", Toast.LENGTH_SHORT).show();

                OtpWidget otpView = new OtpWidget(requireContext());
                otpView.getVerifyBtn().setOnClickListener(v -> {
                    if (!otpView.getOtpView().getText().toString().isEmpty() && otpView.getOtpView().getText().toString().length() == 6) {
                        Map<String, Object> bodyMap = new HashMap<>();
                        bodyMap.put("phone", phoneNumber);
                        bodyMap.put("code", otpView.getOtpView().getText().toString());
                        loginActivityVM.verifyOtp(sessionManager, bodyMap);
                        loginActivityVM.clearOtpRequestStatus();
                    }
                    else
                        Toast.makeText(requireContext(), "Please enter complete OTP", Toast.LENGTH_SHORT).show();
                });
                otpDialog = new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Enter OTP")
                        .setCancelable(false)
                        .setView(otpView)
                        .show();
            }
            else if (jsonObject != null && !jsonObject.get("error").isJsonNull()) {
                binding.progressbar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), jsonObject.get("error").getAsString(), Toast.LENGTH_SHORT).show();
                loginActivityVM.clearOtpRequestStatus();
            }
            else Log.e("RequestStatus", "Empty");
        });

        loginActivityVM.getVerificationResult().observe(getViewLifecycleOwner(), jsonObject -> {
            if (jsonObject != null && jsonObject.get("success").getAsBoolean()) {
                sessionManager.saveAuthToken(jsonObject.get("token").getAsString());
                Toast.makeText(requireContext(), "Verified!", Toast.LENGTH_SHORT).show();
                if (otpDialog.isShowing()) otpDialog.dismiss();
                customerSignUpMainDirections.ActionCustomerSignUpMainToCustomerSignUpDetails action = customerSignUpMainDirections.actionCustomerSignUpMainToCustomerSignUpDetails("+91 "+binding.phoneNumber.getText().toString().trim());
                Navigation.findNavController(view).navigate(action);
                loginActivityVM.emptyVerificationResult();
            }
            else if (jsonObject != null && !jsonObject.get("success").getAsBoolean()) {
                Toast.makeText(requireContext(), jsonObject.get("status").getAsString(), Toast.LENGTH_SHORT).show();
                loginActivityVM.emptyVerificationResult();
            }
            else Log.e("VerificationResult", "Empty");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }
}
