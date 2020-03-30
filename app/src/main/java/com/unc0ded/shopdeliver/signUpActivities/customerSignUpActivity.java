package com.unc0ded.shopdeliver.signUpActivities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.unc0ded.shopdeliver.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class customerSignUpActivity extends AppCompatActivity {

    MaterialButton signUp,getOTP,validateOTP;
    TextInputEditText passwordE,reEnterPasswordE,phoneE, otpE;
    TextInputLayout passField,reEnterPassField;
    Toolbar simpleBar;
    AutoCompleteTextView buildingSelect;
    private String sentOTP;

    //Firebase
    //DatabaseReference userReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth customerAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks customerCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);

        attachID();
        setSupportActionBar(simpleBar);

        //adding back button to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //ArrayAdapter for building selection
        String[] BUILDINGS = new String[] {"A", "B", "C", "D"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(customerSignUpActivity.this, R.layout.dropdown_item, BUILDINGS);
        buildingSelect.setAdapter(adapter);

        customerAuth = FirebaseAuth.getInstance();

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder otpAlertBuilder = new AlertDialog.Builder(customerSignUpActivity.this);
                otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.");
                otpAlertBuilder.setCancelable(true);

                otpAlertBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendOTP();
                                Toast.makeText(customerSignUpActivity.this, "OTP sent to mobile number", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog otpAlert = otpAlertBuilder.create();
                otpAlert.show();

            }
        });

        customerCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d("customerCallbacks", "onVerificationCompleted:" + phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("customerCallbacks", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.d("customerCallbacks","onVerificationFailed: Invalid Number");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.d("customerCallbacks","onVerificationFailed: SMS quota exceeded");
                    // The SMS quota for the project has been exceeded
                    // ...
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
            public void onClick(View view) {
                validateOTPFunc();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Objects.requireNonNull(passwordE.getText()).toString().equals(Objects.requireNonNull(reEnterPasswordE.getText()).toString())) {
                    onBackPressed();
                }
            }
        });

    }

    private void validateOTPFunc() {
        PhoneAuthCredential customerCredential = PhoneAuthProvider.getCredential(sentOTP, Objects.requireNonNull(otpE.getText()).toString());
        signInWithPhoneAuthCredentials(customerCredential);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential customerCredential) {
        customerAuth.signInWithCredential(customerCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("SIGN UP Success", "signInWithCredential:success");

                            Toast.makeText(customerSignUpActivity.this, "Phone number verified!", Toast.LENGTH_SHORT).show();

                            passField.setVisibility(View.VISIBLE);
                            reEnterPassField.setVisibility(View.VISIBLE);
                            signUp.setVisibility(View.VISIBLE);
                        }else{
                            Log.d("SIGN UP Failure", Objects.requireNonNull(task.getException()).toString());
                        }
                    }
                });
    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(Objects.requireNonNull(phoneE.getText()).toString()
                ,60, TimeUnit.SECONDS
                ,customerSignUpActivity.this
                ,customerCallbacks);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void attachID() {
        passwordE = findViewById(R.id.customer_sign_up_password);
        reEnterPasswordE = findViewById(R.id.customer_sign_up_reenter_password);
        phoneE = findViewById(R.id.customer_sign_up_phone);
        otpE = findViewById(R.id.customer_sign_up_otp);
        signUp = findViewById(R.id.customer_sign_up_btn);
        getOTP = findViewById(R.id.otp_btn);
        validateOTP = findViewById(R.id.customer_validate_otp);
        simpleBar=findViewById(R.id.back_toolbar);
        passField=findViewById(R.id.textInputLayout3);
        reEnterPassField=findViewById(R.id.textInputLayout2);
        buildingSelect = findViewById(R.id.dropdown_base);
    }
}
