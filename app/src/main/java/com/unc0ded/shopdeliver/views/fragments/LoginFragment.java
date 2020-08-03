package com.unc0ded.shopdeliver.views.fragments;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.FragmentLoginBinding;
import com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel;
import com.unc0ded.shopdeliver.views.activities.customerMainActivity;
import com.unc0ded.shopdeliver.views.activities.vendorMainActivity;
import com.unc0ded.shopdeliver.views.widgets.OtpWidget;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel.STATUS_FAILED;
import static com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel.STATUS_PROCESSING;
import static com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel.STATUS_SUCCESS_CUSTOMER;
import static com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel.STATUS_SUCCESS_VENDOR;
import static com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel.STATUS_WRONG_OTP;


public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;
    View root;
    private static final int METHOD_EMAIL = 0;
    private static final int METHOD_PHONE = 1;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationId;

    private LoginActivityViewModel loginActivityVM = new LoginActivityViewModel();

    androidx.appcompat.app.AlertDialog enter_otp;

    //TODO: the loginBar was initialized as loginBar = root.findViewById(R.id.login_tool_bar) but was never used.
    // Have to figure out what it is used for and how to implement it.
//    Toolbar loginBar;

    //empty constructor
    public LoginFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginActivityVM.getAuthStatus().observe(this, status -> {
            switch (status){
                case STATUS_SUCCESS_CUSTOMER:
                    Toast.makeText(requireContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                    if (enter_otp != null && enter_otp.isShowing()){
                        enter_otp.dismiss();
                    }
                    startActivity(new Intent(requireContext(), customerMainActivity.class));
                    requireActivity().finish();
                    break;
                case STATUS_SUCCESS_VENDOR:
                    Toast.makeText(requireContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                    if (enter_otp != null && enter_otp.isShowing()){
                        enter_otp.dismiss();
                    }
                    startActivity(new Intent(requireContext(), vendorMainActivity.class));
                    requireActivity().finish();
                    break;
                case STATUS_WRONG_OTP:
                    Toast.makeText(requireContext(), "Invalid OTP!", Toast.LENGTH_LONG).show();
                    binding.progressBar.setVisibility(View.GONE);
                    //This is done because the dialog box is hidden but not dismissed
                    if (enter_otp != null && enter_otp.isShowing())
                        enter_otp.show();
                    break;
                case STATUS_FAILED:
                default:
                    Toast.makeText(requireContext(), "Something went wrong! Check if phone number or email and password are correct", Toast.LENGTH_LONG).show();
                    binding.progressBar.setVisibility(View.GONE);
                    //This is done because the dialog box is hidden but not dismissed
                    if (enter_otp != null && enter_otp.isShowing())
                        enter_otp.show();
                    break;
                case STATUS_PROCESSING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });
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
        root = view;

        binding.switchMethod.setTag(METHOD_PHONE);

        binding.switchMethod.setOnClickListener(v -> {
            switch (Integer.parseInt(binding.switchMethod.getTag().toString())) {
                case METHOD_PHONE:
                    binding.switchMethod.setTag(METHOD_EMAIL);
                    binding.switchMethod.setText(getResources().getString(R.string.use_phone_number_text));
                    binding.usernameLayout.setHint(getResources().getString(R.string.email_text));
                    binding.emailOrPhone.setInputType(InputType.TYPE_CLASS_TEXT);
                    binding.passwordLayout.setVisibility(View.VISIBLE);
                    break;
                case METHOD_EMAIL:
                    binding.switchMethod.setTag(METHOD_PHONE);
                    binding.switchMethod.setText(getResources().getString(R.string.use_email_text));
                    binding.usernameLayout.setHint(getResources().getString(R.string.phone_number_text));
                    binding.emailOrPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                    binding.passwordLayout.setVisibility(View.GONE);
                    break;
            }
        });

        binding.signUp.setOnClickListener(v -> {

            final Dialog signUpPopUp = new Dialog(requireContext());
            signUpPopUp.setContentView(R.layout.sign_up_dialogbox);
            Button customer = signUpPopUp.findViewById(R.id.popup_customer_sign_up_btn),vendor = signUpPopUp.findViewById(R.id.popup_vendor_sign_up_btn);

            //Customer and Vendor Buttons
            {
                customer.setOnClickListener(v1 -> {
                    Navigation.findNavController(root).navigate(R.id.action_login_to_customer_sign_up_main);
                    signUpPopUp.dismiss();
                });

                vendor.setOnClickListener(v1 -> {
                    Navigation.findNavController(root).navigate(R.id.action_login_to_vendorSignUpMain);
                    signUpPopUp.dismiss();
                });
            }
            signUpPopUp.show();
        });

        binding.signInBtn.setOnClickListener(v -> {
            if (binding.emailOrPhone.getText().toString().isEmpty() && binding.password.getText().toString().isEmpty()) {
                AlertDialog.Builder choose = new AlertDialog.Builder(requireContext());
                choose.setCancelable(true);
                choose.setTitle("Debug");
                choose.setMessage("Which activity do you want to debug?");
                choose.setPositiveButton("Customer", (dialog, which) -> {
                    startActivity(new Intent(getContext(), customerMainActivity.class));
                    requireActivity().finish();
                });

                choose.setNegativeButton("Vendor", (dialog, which) -> {
                    startActivity(new Intent(getContext(), vendorMainActivity.class));
                    requireActivity().finish();
                });
                choose.show();
            }
            else {
                ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                switch (Integer.parseInt(binding.switchMethod.getTag().toString())) {
                    case METHOD_PHONE: {
                        if (isConnected) {
                            MaterialAlertDialogBuilder otpAlertBuilder = new MaterialAlertDialogBuilder(requireContext());
                            otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.")
                                    .setCancelable(true)
                                    .setPositiveButton("OK",
                                            (dialog, which) -> {
                                                if((Objects.requireNonNull(Objects.requireNonNull(binding.emailOrPhone.getText()).toString().trim()).length()) != 10) {
                                                    Toast.makeText(getContext(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + Objects.requireNonNull(Objects.requireNonNull(binding.emailOrPhone.getText()).toString().trim()),
                                                            60, TimeUnit.SECONDS, requireActivity(), mCallbacks);
                                                    binding.progressBar.setVisibility(View.VISIBLE);
                                                }
                                                dialog.cancel();
                                            })
                                    .show();
                        }
                        else
                            Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_LONG).show();
                    }
                        break;
                    case METHOD_EMAIL: {if (isConnected)
                            loginActivityVM.signInWithEmail(binding.emailOrPhone.getText().toString().trim(), binding.password.getText().toString().trim());
                        else
                            Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d("customerCallbacks", "onVerificationCompleted:" + phoneAuthCredential);
                binding.progressBar.setVisibility(View.GONE);
                //signInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                binding.progressBar.setVisibility(View.GONE);
                Log.d("customerCallbacks", "onVerificationFailed", e);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                binding.emailOrPhone.setEnabled(true);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.d("customerCallbacks","onVerificationFailed: Invalid Number");
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.d("customerCallbacks","onVerificationFailed: SMS quota exceeded");
                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                binding.progressBar.setVisibility(View.GONE);
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;

                Toast.makeText(requireContext(), "OTP has been sent", Toast.LENGTH_SHORT).show();

                OtpWidget otpView = new OtpWidget(requireContext());
                otpView.getVerifyBtn().setOnClickListener(view -> {
                    if (Objects.requireNonNull(otpView.getOtpView().getText()).toString().length() == 6) {
                        PhoneAuthCredential customerCredential = PhoneAuthProvider.getCredential(verificationId, otpView.getOtpView().getText().toString());
                        loginActivityVM.signInWithPhone(customerCredential);
                        enter_otp.hide();
                    }
                    else
                        Toast.makeText(requireContext(), "Please enter complete OTP", Toast.LENGTH_SHORT).show();
                });
                enter_otp = new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Enter OTP")
                        .setCancelable(true)
                        .setView(otpView)
                        .show();
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }

}
