package com.unc0ded.shopdeliver.signUpActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unc0ded.shopdeliver.R;

import java.util.Objects;

public class customerSignUpActivity extends AppCompatActivity {

    MaterialButton backBtn,signUp;
    TextInputEditText passwordE,reEnterPasswordE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);

        attachID();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    private void attachID() {
        passwordE = findViewById(R.id.customer_sign_up_password);
        reEnterPasswordE = findViewById(R.id.customer_sign_up_reenter_password);
        backBtn = findViewById(R.id.customer_sign_up_view_back_btn);
        signUp = findViewById(R.id.customer_sign_up_btn);
    }
}
