package com.unc0ded.shopdeliver.signUpActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unc0ded.shopdeliver.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class customerSignUpActivity extends AppCompatActivity {

    MaterialButton signUp,getOTP,validateOTP;
    TextInputEditText passwordE,reEnterPasswordE;
    TextInputLayout passField,reEnterPassField;
    Toolbar simpleBar;
    AutoCompleteTextView buildingSelect;
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference();

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

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(customerSignUpActivity.this,"OTP sent to mobile number",Toast.LENGTH_SHORT).show();
            }
        });



        validateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passField.setEnabled(true);
                reEnterPassField.setEnabled(true);
                signUp.setEnabled(true);
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
        signUp = findViewById(R.id.customer_sign_up_btn);
        getOTP = findViewById(R.id.otp_btn);
        validateOTP = findViewById(R.id.customer_validate_otp);
        simpleBar=findViewById(R.id.back_toolbar);
        passField=findViewById(R.id.textInputLayout3);
        reEnterPassField=findViewById(R.id.textInputLayout2);
        buildingSelect = findViewById(R.id.dropdown_base);
    }
}
