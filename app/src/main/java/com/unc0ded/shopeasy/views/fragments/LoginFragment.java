package com.unc0ded.shopeasy.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unc0ded.shopeasy.R;
import com.unc0ded.shopeasy.ShopEasy;
import com.unc0ded.shopeasy.databinding.FragmentLoginBinding;
import com.unc0ded.shopeasy.utils.SessionManager;
import com.unc0ded.shopeasy.viewmodels.LoginActivityViewModel;
import com.unc0ded.shopeasy.views.activities.CustomerMainActivity;
import com.unc0ded.shopeasy.views.activities.VendorMainActivity;
import com.unc0ded.shopeasy.views.widgets.OtpWidget;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;
    private LoginActivityViewModel loginActivityVM;
    private SessionManager sessionManager;

    private String phoneNumber;
    private String userId;

    AlertDialog otpDialog;

    private static final int METHOD_EMAIL = 0;
    private static final int METHOD_PHONE = 1;

    //empty constructor
    public LoginFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginActivityVM = new ViewModelProvider(requireActivity()).get(LoginActivityViewModel.class);
        loginActivityVM.setLoginMethod(METHOD_PHONE);
        sessionManager = ((ShopEasy)requireActivity().getApplication()).getSessionManager();

        loginActivityVM.getLoginMethod().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                switch (integer) {
                    case METHOD_PHONE:
                        binding.switchMethod.setText(getResources().getString(R.string.use_email_text));
                        binding.usernameLayout.setHint(getResources().getString(R.string.phone_number_text));
                        binding.emailOrPhone.setText("");
                        binding.emailOrPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                        binding.passwordLayout.setVisibility(View.GONE);
                        binding.switchMethod.setOnClickListener(v -> loginActivityVM.setLoginMethod(METHOD_EMAIL));
                        binding.signInBtn.setOnClickListener(v -> {
                            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
                            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                            if (isConnected) {
                                new MaterialAlertDialogBuilder(requireContext())
                                        .setMessage("You will receive an OTP and standard SMS charges may apply.")
                                        .setCancelable(true)
                                        .setPositiveButton("Accept", (dialogInterface, i) -> {
                                            if (binding.emailOrPhone.getText().toString().isEmpty() || binding.emailOrPhone.getText().toString().trim().length() != 10) {
                                                Toast.makeText(getContext(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                phoneNumber = "+91 " + binding.emailOrPhone.getText().toString();
                                                Map<String, Object> bodyMap = new HashMap<>();
                                                bodyMap.put("phone", phoneNumber);
                                                loginActivityVM.loginOtpRequest(sessionManager, bodyMap);
                                                binding.progressBar.setVisibility(View.VISIBLE);
                                                startObservingForOtpRequest();
                                            }
                                            dialogInterface.dismiss();
                                        })
                                        .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                                        .show();
                            }
                            else
                                Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_LONG).show();
                        });
                        break;
                    case METHOD_EMAIL:
                        binding.switchMethod.setText(getResources().getString(R.string.use_phone_number_text));
                        binding.usernameLayout.setHint(getResources().getString(R.string.email_text));
                        binding.emailOrPhone.setText("");
                        binding.emailOrPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                        binding.passwordLayout.setVisibility(View.VISIBLE);
                        binding.switchMethod.setOnClickListener(v -> loginActivityVM.setLoginMethod(METHOD_PHONE));
                        binding.signInBtn.setOnClickListener(v -> {
                            //TODO Email Login
                        });
                        break;
                }
            }
        });

        binding.signUp.setOnClickListener(v -> {
            final Dialog signUpPopUp = new Dialog(requireContext());
            signUpPopUp.setContentView(R.layout.dialog_register);
            MaterialButton customer = signUpPopUp.findViewById(R.id.popup_customer_sign_up_btn),
                    vendor = signUpPopUp.findViewById(R.id.popup_vendor_sign_up_btn);

            customer.setOnClickListener(v1 -> {
                Navigation.findNavController(view).navigate(R.id.action_login_to_customer_sign_up_main);
                signUpPopUp.dismiss();
            });

            vendor.setOnClickListener(v1 -> {
                Navigation.findNavController(view).navigate(R.id.action_login_to_vendorSignUpMain);
                signUpPopUp.dismiss();
            });
            signUpPopUp.show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }

    private void startObservingForOtpRequest() {
        loginActivityVM.getLoginOtpRequestStatus().observe(getViewLifecycleOwner(), jsonObject -> {
            if (jsonObject != null && jsonObject.get("error").isJsonNull()) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "OTP has been sent", Toast.LENGTH_SHORT).show();

                OtpWidget otpView = new OtpWidget(requireContext());
                otpView.getVerifyBtn().setOnClickListener(v -> {
                    if (!otpView.getOtpView().getText().toString().isEmpty() && otpView.getOtpView().getText().toString().length() == 6) {
                        Map<String, Object> bodyMap = new HashMap<>();
                        bodyMap.put("phone", phoneNumber);
                        bodyMap.put("code", otpView.getOtpView().getText().toString());
                        userId = jsonObject.get("user").getAsString();
                        bodyMap.put("user", userId);
                        loginActivityVM.verifyLoginOtp(sessionManager, bodyMap);
                        startObservingForVerificationResult();
                        //loginActivityVM.clearLoginOtpRequest();
                        loginActivityVM.getLoginOtpRequestStatus().removeObservers(getViewLifecycleOwner());
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
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), jsonObject.get("error").getAsString(), Toast.LENGTH_SHORT).show();
                //loginActivityVM.clearLoginOtpRequest();
                loginActivityVM.getLoginOtpRequestStatus().removeObservers(getViewLifecycleOwner());
            }
            else Log.e("LoginRequest", "Empty");
        });
    }

    private void startObservingForVerificationResult() {
        loginActivityVM.getLoginVerificationResult().observe(getViewLifecycleOwner(), jsonObject -> {
            if (jsonObject != null && !jsonObject.get("token").isJsonNull()) {
                sessionManager.saveAuthToken(jsonObject.get("token").getAsString());
                if (userId != null)
                    sessionManager.saveUserId(userId);
                if (jsonObject.get("customer").getAsBoolean() && jsonObject.get("vendor").getAsBoolean()) {
                    otpDialog.dismiss();
                    Dialog loginOption = new Dialog(requireContext());
                    loginOption.setContentView(R.layout.dialog_register);
                    loginOption.setCancelable(false);
                    loginOption.findViewById(R.id.popup_customer_sign_up_btn).setOnClickListener(v -> {
                        loginOption.dismiss();
                        startActivity(new Intent(requireContext(), CustomerMainActivity.class));
                        requireActivity().finish();
                        loginActivityVM.getLoginVerificationResult().removeObservers(getViewLifecycleOwner());
                        //loginActivityVM.clearLoginVerificationResult();
                    });
                    loginOption.findViewById(R.id.popup_vendor_sign_up_btn).setOnClickListener(v -> {
                        loginOption.dismiss();
                        startActivity(new Intent(requireContext(), VendorMainActivity.class));
                        requireActivity().finish();
                        loginActivityVM.getLoginVerificationResult().removeObservers(getViewLifecycleOwner());
                        //loginActivityVM.clearLoginVerificationResult();
                    });
                }
                else if (jsonObject.get("customer").getAsBoolean()) {
                    startActivity(new Intent(requireContext(), CustomerMainActivity.class));
                    requireActivity().finish();
                    loginActivityVM.getLoginVerificationResult().removeObservers(getViewLifecycleOwner());
                    //loginActivityVM.clearLoginVerificationResult();
                }
                else {
                    startActivity(new Intent(requireContext(), VendorMainActivity.class));
                    requireActivity().finish();
                    loginActivityVM.getLoginVerificationResult().removeObservers(getViewLifecycleOwner());
                    //loginActivityVM.clearLoginVerificationResult();
                }
            }
            else if (jsonObject != null && !jsonObject.get("customer").getAsBoolean() && !jsonObject.get("vendor").getAsBoolean() && jsonObject.get("token").isJsonNull()) {
                Toast.makeText(requireContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();
                loginActivityVM.getLoginVerificationResult().removeObservers(getViewLifecycleOwner());
                // loginActivityVM.clearLoginVerificationResult();
            }
            else Log.e("LoginVerificationStatus", "Empty");
        });
    }
}
