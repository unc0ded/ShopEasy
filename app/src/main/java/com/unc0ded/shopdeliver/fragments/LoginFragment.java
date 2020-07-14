package com.unc0ded.shopdeliver.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.activities.customerMainActivity;
import com.unc0ded.shopdeliver.activities.vendorMainActivity;
import com.unc0ded.shopdeliver.databinding.FragmentLoginBinding;
import com.unc0ded.shopdeliver.widgets.OtpWidget;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;
    View root;
    MaterialButton switchLoginMethod;
    TextInputLayout usernameLayout, passwordLayout;
    TextInputEditText emailOrPhone, password;
    private static final int METHOD_EMAIL = 0;
    private static final int METHOD_PHONE = 1;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationId;
    FirebaseAuth auth;
    FirebaseFirestore db;

    //TODO: the loginBar was initialized as loginBar = root.findViewById(R.id.login_tool_bar) but was never used.
    // Have to figure out what it is used for and how to implement it.
//    Toolbar loginBar;

    //empty constructor
    public LoginFragment() {
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
        usernameLayout = binding.usernameLayout;
        passwordLayout = binding.passwordLayout;
        emailOrPhone = binding.emailOrPhone;
        password = binding.password;
        switchLoginMethod = binding.switchMethod;

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        switchLoginMethod.setTag(METHOD_PHONE);

        switchLoginMethod.setOnClickListener(v -> {
            switch (Integer.parseInt(switchLoginMethod.getTag().toString())) {
                case METHOD_PHONE: switchLoginMethod.setTag(METHOD_EMAIL);
                switchLoginMethod.setText(getResources().getString(R.string.use_phone_number_text));
                usernameLayout.setHint(getResources().getString(R.string.email_text));
                emailOrPhone.setInputType(InputType.TYPE_CLASS_TEXT);
                passwordLayout.setVisibility(View.VISIBLE);
                break;
                case METHOD_EMAIL: switchLoginMethod.setTag(METHOD_PHONE);
                switchLoginMethod.setText(getResources().getString(R.string.use_email_text));
                usernameLayout.setHint(getResources().getString(R.string.phone_number_text));
                emailOrPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                passwordLayout.setVisibility(View.GONE);
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
            if (emailOrPhone.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
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
                switch (Integer.parseInt(switchLoginMethod.getTag().toString())) {
                    case METHOD_PHONE: {
                        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                        if (isConnected) {
                            MaterialAlertDialogBuilder otpAlertBuilder = new MaterialAlertDialogBuilder(requireContext());
                            otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.")
                                    .setCancelable(true)
                                    .setPositiveButton("OK",
                                            (dialog, which) -> {
                                                if((Objects.requireNonNull(Objects.requireNonNull(emailOrPhone.getText()).toString().trim()).length()) != 10) {
                                                    Toast.makeText(getContext(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    sendOTP();
                                                    emailOrPhone.setEnabled(false);
                                                }
                                                dialog.cancel();
                                            })
                                    .show();
                        }
                        else
                            Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_LONG).show();
                    }
                    break;
                    case METHOD_EMAIL: auth.signInWithEmailAndPassword(Objects.requireNonNull(Objects.requireNonNull(emailOrPhone.getText()).toString().trim()), Objects.requireNonNull(password.getText()).toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    db.collection("Customers").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                                                    startActivity(new Intent(requireContext(), customerMainActivity.class));
                                                    requireActivity().finish();
                                                }
                                                else {
                                                    startActivity(new Intent(requireContext(), vendorMainActivity.class));
                                                    requireActivity().finish();
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("Cred Search", "Failed: " + e.getMessage()));
                                }
                            });
                    break;
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d("customerCallbacks", "onVerificationCompleted:" + phoneAuthCredential);
                //signInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("customerCallbacks", "onVerificationFailed", e);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                emailOrPhone.setEnabled(true);
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
                verificationId = s;
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + Objects.requireNonNull(Objects.requireNonNull(emailOrPhone.getText()).toString().trim()),
                60, TimeUnit.SECONDS, requireActivity(), mCallbacks);

        OtpWidget otpView = new OtpWidget(requireContext());
        otpView.getVerifyBtn().setOnClickListener(view -> {
            if (Objects.requireNonNull(otpView.getOtpView().getText()).toString().length() == 6) {
                PhoneAuthCredential customerCredential = PhoneAuthProvider.getCredential(verificationId, otpView.getOtpView().getText().toString());
                signInWithPhoneAuthCredentials(customerCredential);
            }
            else
                Toast.makeText(requireContext(), "Please enter complete OTP", Toast.LENGTH_SHORT).show();
        });
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Enter OTP")
                .setCancelable(false)
                .setView(otpView)
                .show();
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential customerCredential) {
        auth.signInWithCredential(customerCredential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d("signInWithCredential:", "success");

                        Toast.makeText(getContext(), "Phone number verified!", Toast.LENGTH_LONG).show();

                        db.collection("Customers").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                                        startActivity(new Intent(requireContext(), customerMainActivity.class));
                                        requireActivity().finish();
                                    }
                                    else {
                                        startActivity(new Intent(requireContext(), vendorMainActivity.class));
                                        requireActivity().finish();
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Cred Search", "Failed: " + e.getMessage()));
                    }
                    else {
                        Log.d("SIGN UP Failure", Objects.requireNonNull(task.getException()).toString());
                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
