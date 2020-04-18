package com.unc0ded.shopdeliver.signUpActivities;

import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
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
import com.unc0ded.shopdeliver.model.Customer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class customerSignUpActivity extends AppCompatActivity {

    MaterialButton signUp,getOTP,validateOTP;
    MaterialTextView optionalTV;
    TextInputEditText nameE,societyNameE,flatNumberE,passwordE,reEnterPasswordE,phoneE, otpE, emailE, localityE;
    Toolbar simpleBar;
    NestedScrollView scrollView;
    AutoCompleteTextView buildingSelect;
    private String sentOTP;

    //Firebase
    DatabaseReference userReference;
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

        emailE.setEnabled(false);
        passwordE.setEnabled(false);
        reEnterPasswordE.setEnabled(false);
        signUp.setEnabled(false);

        customerAuth = FirebaseAuth.getInstance();

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder otpAlertBuilder = new AlertDialog.Builder(customerSignUpActivity.this);
                otpAlertBuilder.setMessage("You will receive an OTP and standard SMS charges may apply.")
                        .setCancelable(true)
                        .setPositiveButton("Accept",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if((Objects.requireNonNull(phoneE.getText()).toString().length()) != 10)
                                    {
                                        Toast.makeText(customerSignUpActivity.this, "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
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
                passwordE.setEnabled(true);
                reEnterPasswordE.setEnabled(true);
                emailE.setEnabled(true);
                signUp.setEnabled(true);
                autoScroll();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("customerCallbacks", "onVerificationFailed", e);
                Toast.makeText(customerSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            }
        };

        validateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.requireNonNull(otpE.getText()).toString().isEmpty())
                    {
                        Toast.makeText(customerSignUpActivity.this, "Please enter OTP.", Toast.LENGTH_SHORT).show();
                    }
                else
                    {
                        validateOTPFunc();
                    }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((!(passwordE.getText()).toString().isEmpty())&&(!(reEnterPasswordE.getText()).toString().isEmpty())
                        &&(!(nameE.getText()).toString().isEmpty())&&(!(societyNameE.getText()).toString().isEmpty())
                        &&(!(flatNumberE.getText()).toString().isEmpty())&&(!(buildingSelect.getText().toString().equals("Building Number")))
                        &&(!(emailE.getText().toString().isEmpty()))&&(!(phoneE.getText().toString().isEmpty()))&&(!(localityE.getText().toString().isEmpty())))
                    {
                        if((Objects.requireNonNull(passwordE.getText().toString()).equals(Objects.requireNonNull(reEnterPasswordE.getText().toString()))))
                            {
                                addCustomer(nameE.getText().toString(),societyNameE.getText().toString(),(buildingSelect.getText().toString()+flatNumberE.getText().toString()), localityE.getText().toString(), phoneE.getText().toString(), emailE.getText().toString());
                                Intent signUp = new Intent(customerSignUpActivity.this, LoginActivity.class);
                                startActivity(signUp);
                            }
                        else
                            {
                                Toast.makeText(customerSignUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                            }
                    }
                else if(((passwordE.getText()).toString().isEmpty())&&((reEnterPasswordE.getText()).toString().isEmpty())
                        &&(!(nameE.getText()).toString().isEmpty())&&(!(societyNameE.getText()).toString().isEmpty())
                        &&(!(flatNumberE.getText()).toString().isEmpty())&&(!(buildingSelect.getText().toString().equals("Building Number")))
                        &&((emailE.getText().toString().isEmpty()))&&(!(phoneE.getText().toString().isEmpty()))&&(!(localityE.getText().toString().isEmpty())))
                    {
                        addCustomer(nameE.getText().toString(),societyNameE.getText().toString(),(buildingSelect.getText().toString()+flatNumberE.getText().toString()), localityE.getText().toString(), phoneE.getText().toString());
                        Intent signUp = new Intent(customerSignUpActivity.this, LoginActivity.class);
                        startActivity(signUp);
                    }
                else
                    {
                        Toast.makeText(customerSignUpActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }

    private void addCustomer(String custName, String socName, String flat, String loc,  String phone, String email) {
        Customer createCustomer = new Customer(custName,socName,flat,loc,phone,email);
        userReference.child("Customers").child(loc).child(socName).child(flat).setValue(createCustomer);
        Toast.makeText(customerSignUpActivity.this, "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

    private void addCustomer(String custName, String socName, String flat, String loc, String phone) {
        Customer createCustomer = new Customer(custName, socName, flat, loc, phone);
        userReference.child("Customers").child(loc).child(socName).child(flat).setValue(createCustomer);
        Toast.makeText(customerSignUpActivity.this, "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

    private void validateOTPFunc() {
        PhoneAuthCredential customerCredential = PhoneAuthProvider.getCredential(sentOTP, Objects.requireNonNull(otpE.getText()).toString());
        signInWithPhoneAuthCredentials(customerCredential);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential customerCredential) {
        customerAuth.signInWithCredential(customerCredential)
                .addOnCompleteListener(customerSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                            {
                                Log.d("SIGN UP Success", "signInWithCredential:success");

                                Toast.makeText(customerSignUpActivity.this, "Phone number verified!", Toast.LENGTH_SHORT).show();

                                otpE.setEnabled(false);
                                validateOTP.setEnabled(false);

                                emailE.setEnabled(true);
                                passwordE.setEnabled(true);
                                reEnterPasswordE.setEnabled(true);
                                signUp.setEnabled(true);
                                autoScroll();
                            }
                        else
                            {
                                Log.d("SIGN UP Failure", Objects.requireNonNull(task.getException()).toString());
                                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                    Toast.makeText(customerSignUpActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+Objects.requireNonNull(phoneE.getText()).toString()
                ,60, TimeUnit.SECONDS
                ,customerSignUpActivity.this
                ,customerCallbacks);
    }

    private void autoScroll(){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0,optionalTV.getBottom());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void attachID() {
        simpleBar=findViewById(R.id.back_toolbar);
        scrollView=findViewById(R.id.customer_sign_up_nested_scrollview);

        optionalTV=findViewById(R.id.customer_sign_up_optional_tv);
        nameE = findViewById(R.id.customer_sign_up_name);
        societyNameE = findViewById(R.id.customer_sign_up_society_name);
        flatNumberE = findViewById(R.id.customer_sign_up_flat_number);
        localityE=findViewById(R.id.customer_sign_up_locality);
        passwordE = findViewById(R.id.customer_sign_up_password);
        reEnterPasswordE = findViewById(R.id.customer_sign_up_reenter_password);
        phoneE = findViewById(R.id.customer_sign_up_phone);
        otpE = findViewById(R.id.customer_sign_up_otp);
        buildingSelect = findViewById(R.id.customer_sign_up_building_name);

        signUp = findViewById(R.id.customer_sign_up_btn);
        getOTP = findViewById(R.id.customer_otp_btn);
        validateOTP = findViewById(R.id.customer_validate_otp);

        emailE=findViewById(R.id.customer_sign_up_email);

        customerAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

    }
}
