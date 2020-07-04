package com.unc0ded.shopdeliver.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.unc0ded.shopdeliver.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class customerSignUpMain extends Fragment {


    private MaterialButton getOTP,validateOTP;
    private TextInputEditText phoneE, otpE;
    private String sentOTP;
    private View rootView;

    //Firebase
    private FirebaseAuth customerAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks customerCallbacks;

    public customerSignUpMain() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_sign_up_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        phoneE = view.findViewById(R.id.customer_sign_up_phone);
        otpE = view.findViewById(R.id.customer_sign_up_otp);

        getOTP = view.findViewById(R.id.customer_otp_btn);
        validateOTP = view.findViewById(R.id.customer_validate_otp);

        customerAuth = FirebaseAuth.getInstance();

        otpE.setEnabled(false);
        validateOTP.setEnabled(false);

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder otpAlertBuilder = new AlertDialog.Builder(getContext());
                otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.")
                        .setCancelable(true)
                        .setPositiveButton("Accept",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if((Objects.requireNonNull(phoneE.getText()).toString().trim().length()) != 10)
                                        {
                                            Toast.makeText(getContext(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            sendOTP();
                                            phoneE.setEnabled(false);
                                            getOTP.setEnabled(false);
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
                phoneE.setEnabled(true);
                getOTP.setEnabled(true);
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
                otpE.setEnabled(true);
                validateOTP.setEnabled(true);
            }
        };

        validateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.requireNonNull(otpE.getText()).toString().isEmpty())
                {
                    Toast.makeText(getContext(), "Please enter OTP.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    validateOTPFunc();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        phoneE.setEnabled(true);
        otpE.setEnabled(true);
        getOTP.setEnabled(true);
        validateOTP.setEnabled(true);
    }

    private void validateOTPFunc() {
        PhoneAuthCredential customerCredential = PhoneAuthProvider.getCredential(sentOTP, Objects.requireNonNull(otpE.getText()).toString());
        signInWithPhoneAuthCredentials(customerCredential);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential customerCredential) {
        customerAuth.signInWithCredential(customerCredential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d("SIGN UP Success", "signInWithCredential:success");

                            Toast.makeText(getContext(), "Phone number verified!", Toast.LENGTH_SHORT).show();

                            otpE.setEnabled(false);
                            validateOTP.setEnabled(false);
                            getOTP.setEnabled(false);
                            validateOTP.setEnabled(false);

                            customerSignUpMainDirections.ActionCustomerSignUpMainToCustomerSignUpDetails action = customerSignUpMainDirections.actionCustomerSignUpMainToCustomerSignUpDetails("+91"+phoneE.getText().toString().trim());
                            Navigation.findNavController(rootView).navigate(action);
                        }
                        else
                        {
                            Log.d("SIGN UP Failure", Objects.requireNonNull(task.getException()).toString());
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();

                                phoneE.setEnabled(true);
                                otpE.setEnabled(true);
                                getOTP.setEnabled(true);
                                validateOTP.setEnabled(true);
                            }
                        }
                    }
                });
    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+Objects.requireNonNull(phoneE.getText()).toString()
                ,60, TimeUnit.SECONDS
                ,getActivity()
                ,customerCallbacks);
    }

}
