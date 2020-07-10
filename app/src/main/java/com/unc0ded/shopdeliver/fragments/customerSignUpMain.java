package com.unc0ded.shopdeliver.fragments;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerSignUpMainBinding;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class customerSignUpMain extends Fragment {

    FragmentCustomerSignUpMainBinding binding;

    private String sentOTP;
    private View rootView;

    //Firebase
    private FirebaseAuth customerAuth = FirebaseAuth.getInstance();;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks customerCallbacks;

    //empty constructor
    public customerSignUpMain() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerSignUpMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;

        binding.otp.setEnabled(false);
        binding.validateOtpBtn.setEnabled(false);

        binding.otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder otpAlertBuilder = new AlertDialog.Builder(requireContext());
                otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.")
                        .setCancelable(true)
                        .setPositiveButton("Accept",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if((Objects.requireNonNull(binding.phoneNumber.getText()).toString().trim().length()) != 10)
                                        {
                                            Toast.makeText(getContext(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            sendOTP();
                                            binding.phoneNumber.setEnabled(false);
                                            binding.otpBtn.setEnabled(false);
                                        }
                                        dialog.cancel();
                                    }
                                }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();

            }
        });

        customerCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d("customerCallbacks", "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("customerCallbacks", "onVerificationFailed", e);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                binding.phoneNumber.setEnabled(true);
                binding.otpBtn.setEnabled(true);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.d("customerCallbacks","onVerificationFailed: Invalid Number");
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.d("customerCallbacks","onVerificationFailed: SMS quota exceeded");
                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                sentOTP = s;
                binding.otp.setEnabled(true);
                binding.validateOtpBtn.setEnabled(true);
            }
        };

        binding.validateOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.requireNonNull(binding.otp.getText()).toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter OTP.", Toast.LENGTH_SHORT).show();
                } else {
                    validateOTPFunc();
                }
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

    private void validateOTPFunc() {
        PhoneAuthCredential customerCredential = PhoneAuthProvider.getCredential(sentOTP, Objects.requireNonNull(binding.otp.getText()).toString());
        signInWithPhoneAuthCredentials(customerCredential);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential customerCredential) {
        customerAuth.signInWithCredential(customerCredential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d("signInWithCredential:", "success");

                            Toast.makeText(getContext(), "Phone number verified!", Toast.LENGTH_SHORT).show();

                            binding.otp.setEnabled(false);
                            binding.validateOtpBtn.setEnabled(false);
                            binding.otpBtn.setEnabled(false);
                            binding.validateOtpBtn.setEnabled(false);

                            customerSignUpMainDirections.ActionCustomerSignUpMainToCustomerSignUpDetails action = customerSignUpMainDirections.actionCustomerSignUpMainToCustomerSignUpDetails("+91"+binding.phoneNumber.getText().toString().trim());
                            Navigation.findNavController(rootView).navigate(action);
                        } else {
                            Log.d("SIGN UP Failure", Objects.requireNonNull(task.getException()).toString());
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();

                                binding.phoneNumber.setEnabled(true);
                                binding.otp.setEnabled(true);
                                binding.otpBtn.setEnabled(true);
                                binding.validateOtpBtn.setEnabled(true);
                            }
                        }
                    }
                });
    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+Objects.requireNonNull(binding.phoneNumber.getText()).toString()
                ,60, TimeUnit.SECONDS
                , requireActivity()
                ,customerCallbacks);
    }

}
