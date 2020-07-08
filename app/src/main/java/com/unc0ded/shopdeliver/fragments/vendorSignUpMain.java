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

/**
 * A simple {@link Fragment} subclass.
 */
public class vendorSignUpMain extends Fragment {

    private MaterialButton getOTP,validateOTP;
    private TextInputEditText phoneE,otpE;
    private String sentOTP;

    private View rootView;

    //Firebase Objects
    private FirebaseAuth vendorAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks vendorCallbacks;

    public vendorSignUpMain() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vendor_sign_up_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        otpE = view.findViewById(R.id.vendor_sign_up_otp);
        phoneE = view.findViewById(R.id.vendor_sign_up_phone);

        getOTP = view.findViewById(R.id.vendor_otp_btn);
        validateOTP = view.findViewById(R.id.vendor_validate_otp);

        vendorAuth = FirebaseAuth.getInstance();

        otpE.setEnabled(false);
        validateOTP.setEnabled(false);

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder otpAlertBuilder = new AlertDialog.Builder(getContext());
                otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.")
                        .setCancelable(true)
                        .setPositiveButton("Ok",
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

        vendorCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d("customerCallbacks", "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("vendorCallbacks", "onVerificationFailed", e);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                phoneE.setEnabled(true);
                getOTP.setEnabled(true);
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
                sentOTP = s;
                otpE.setEnabled(true);
                validateOTP.setEnabled(true);
            }
        };

        validateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.requireNonNull(otpE.getText()).toString().isEmpty()){
                    Toast.makeText(getContext(), "Please enter OTP.", Toast.LENGTH_SHORT).show();
                }
                else{
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
        PhoneAuthCredential vendorCredential = PhoneAuthProvider.getCredential(sentOTP, Objects.requireNonNull(otpE.getText()).toString());
        signInWithPhoneAuthCredentials(vendorCredential);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential vendorCredential) {
        vendorAuth.signInWithCredential(vendorCredential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d("SIGN UP Success", "signInWithCredential:success");

                            Toast.makeText(getContext(), "Phone number verified!", Toast.LENGTH_SHORT).show();

                            otpE.setEnabled(false);
                            validateOTP.setEnabled(false);
                            getOTP.setEnabled(false);
                            validateOTP.setEnabled(false);

                            vendorSignUpMainDirections.ActionVendorSignUpMainToVendorSignUpDetails action = vendorSignUpMainDirections.actionVendorSignUpMainToVendorSignUpDetails("+91"+phoneE.getText().toString().trim());
                            Navigation.findNavController(rootView).navigate(action);
                        }
                        else{
                            Log.d("SIGN UP Failure", Objects.requireNonNull(task.getException()).toString());

                            phoneE.setEnabled(true);
                            otpE.setEnabled(true);
                            getOTP.setEnabled(true);
                            validateOTP.setEnabled(true);
                        }
                    }
                });
    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+ Objects.requireNonNull(phoneE.getText()).toString()
                ,60, TimeUnit.SECONDS
                ,getActivity()
                ,vendorCallbacks);
    }
}
