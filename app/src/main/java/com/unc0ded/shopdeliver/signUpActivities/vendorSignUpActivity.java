package com.unc0ded.shopdeliver.signUpActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unc0ded.shopdeliver.LoginActivity;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.model.Vendor;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class vendorSignUpActivity extends AppCompatActivity {

    Toolbar simpleBar;
    MaterialButton getOTP,validateOTP,signUp;
    TextInputEditText shopNameE,vendorNameE,vendorTypeE,passwordE,reEnterPasswordE,phoneE,otpE,emailE,addressE;
    private String sentOTP;

    //Firebase Objects
    FirebaseAuth vendorAuth;
    DatabaseReference userReference;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks vendorCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_sign_up);

        attachID();
        setSupportActionBar(simpleBar);

        //adding back button to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        emailE.setEnabled(false);
        passwordE.setEnabled(false);
        reEnterPasswordE.setEnabled(false);
        signUp.setEnabled(false);

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder otpAlertBuilder = new AlertDialog.Builder(vendorSignUpActivity.this);
                otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.")
                        .setCancelable(true)
                        .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if((Objects.requireNonNull(phoneE.getText()).toString().length()) != 10)
                                    {
                                        Toast.makeText(vendorSignUpActivity.this, "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
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
                passwordE.setEnabled(true);
                reEnterPasswordE.setEnabled(true);
                signUp.setEnabled(true);
                emailE.setEnabled(true);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("vendorCallbacks", "onVerificationFailed", e);
                Toast.makeText(vendorSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            }
        };

        validateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.requireNonNull(otpE.getText()).toString().isEmpty()){
                    Toast.makeText(vendorSignUpActivity.this, "Please enter OTP.", Toast.LENGTH_SHORT).show();
                }
                else{
                    validateOTPFunc();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!(passwordE.getText().toString().isEmpty()))&&(!(reEnterPasswordE.getText().toString().isEmpty()))
                        &&(!(shopNameE.getText().toString().isEmpty()))&&(!(vendorNameE.getText().toString().isEmpty()))
                        &&(!(vendorTypeE.getText().toString().isEmpty()))&&(!(addressE.getText().toString().isEmpty()))
                        &&(!(emailE.getText().toString().isEmpty()))&&(!(phoneE.getText().toString().isEmpty()))) 
                    {
                        if((Objects.requireNonNull(passwordE.getText()).toString().equals(Objects.requireNonNull(reEnterPasswordE.getText()).toString()))) 
                            {
                                addVendor(shopNameE.getText().toString(),vendorTypeE.getText().toString(), vendorNameE.getText().toString(), phoneE.getText().toString(),emailE.getText().toString(),addressE.getText().toString());
                                Intent signUp = new Intent(vendorSignUpActivity.this, LoginActivity.class);
                                startActivity(signUp);
                            }
                        else 
                            {    
                                Toast.makeText(vendorSignUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                            }
                    }
                else if(((passwordE.getText().toString().isEmpty()))&&((reEnterPasswordE.getText().toString().isEmpty()))
                        &&(!(shopNameE.getText().toString().isEmpty()))&&(!(vendorNameE.getText().toString().isEmpty()))
                        &&(!(vendorTypeE.getText().toString().isEmpty()))&&(!(addressE.getText().toString().isEmpty()))
                        &&((emailE.getText().toString().isEmpty()))&&(!(phoneE.getText().toString().isEmpty())))
                    {
                        addVendor(shopNameE.getText().toString(),vendorTypeE.getText().toString(), vendorNameE.getText().toString(), phoneE.getText().toString(),addressE.getText().toString());
                        Intent signUp = new Intent(vendorSignUpActivity.this, LoginActivity.class);
                        startActivity(signUp);
                    }
                else
                    {
                        Toast.makeText(vendorSignUpActivity.this, "Please fill necessary fields!", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    private void addVendor(String shopName, String shopType, String propName, String phone, String email, String address) {
        Vendor createVendor = new Vendor(shopName,shopType,propName,phone,email,address);
        userReference.child("Vendors").child(address).child(shopNameE.getText().toString()).setValue(createVendor);
        Toast.makeText(vendorSignUpActivity.this, "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

    private void addVendor(String shopName, String shopType, String propName, String phone, String address) {
        Vendor createVendor = new Vendor(shopName,shopType,propName,phone,address);
        userReference.child("Vendors").child(address).child(shopNameE.getText().toString()).setValue(createVendor);
        Toast.makeText(vendorSignUpActivity.this, "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

    private void validateOTPFunc() {
        PhoneAuthCredential vendorCredential = PhoneAuthProvider.getCredential(sentOTP, Objects.requireNonNull(otpE.getText()).toString());
        signInWithPhoneAuthCredentials(vendorCredential);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential vendorCredential) {
        vendorAuth.signInWithCredential(vendorCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d("SIGN UP Success", "signInWithCredential:success");

                            Toast.makeText(vendorSignUpActivity.this, "Phone number verified!", Toast.LENGTH_SHORT).show();

                            otpE.setEnabled(false);
                            validateOTP.setEnabled(false);

                            emailE.setEnabled(true);
                            passwordE.setEnabled(true);
                            reEnterPasswordE.setEnabled(true);
                            signUp.setEnabled(true);
                        }else{
                            Log.d("SIGN UP Failure", Objects.requireNonNull(task.getException()).toString());
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+ Objects.requireNonNull(phoneE.getText()).toString()
                ,60, TimeUnit.SECONDS
                ,vendorSignUpActivity.this
                ,vendorCallbacks);
    }

    private void attachID() {
        simpleBar=findViewById(R.id.back_toolbar);

        shopNameE = findViewById(R.id.shop_name);
        vendorNameE = findViewById(R.id.vendor_name);
        vendorTypeE = findViewById(R.id.vendor_type);
        addressE=findViewById(R.id.vendor_address);
        otpE = findViewById(R.id.vendor_sign_up_otp);
        phoneE = findViewById(R.id.vendor_sign_up_phone);
        passwordE = findViewById(R.id.vendor_sign_up_password);
        reEnterPasswordE = findViewById(R.id.vendor_sign_up_reenter_password);

        getOTP = findViewById(R.id.vendor_otp_btn);
        validateOTP = findViewById(R.id.vendor_validate_otp);
        signUp = findViewById(R.id.vendor_sign_up_btn);

        emailE=findViewById(R.id.vendor_sign_up_email);

        vendorAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }
}
