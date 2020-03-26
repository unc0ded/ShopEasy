package com.unc0ded.shopdeliver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.unc0ded.shopdeliver.signUpActivities.customerSignUpActivity;
import com.unc0ded.shopdeliver.signUpActivities.vendorSignUpActivity;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText emailOrPhoneE,passwordE;
    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MaterialLight);
        setContentView(R.layout.activity_login);

        attachID();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog signUpPopUp = new Dialog(LoginActivity.this);
                signUpPopUp.setContentView(R.layout.sign_up_popup_dialogbox);
                Button customer = signUpPopUp.findViewById(R.id.popup_customer_sign_up_btn),vendor = signUpPopUp.findViewById(R.id.popup_vendor_sign_up_btn);

                //Customer and Vendor Buttons
                {
                    customer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent customerSignUp = new Intent(LoginActivity.this, customerSignUpActivity.class);
                            startActivity(customerSignUp);
                            signUpPopUp.dismiss();
                        }
                    });

                    vendor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent vendorSignUp = new Intent(LoginActivity.this, vendorSignUpActivity.class);
                            startActivity(vendorSignUp);
                            signUpPopUp.dismiss();
                        }
                    });
                }
                signUpPopUp.show();
            }
        });

        Toolbar myToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void attachID() {
        emailOrPhoneE = findViewById(R.id.email_phone_tf);
        passwordE = findViewById(R.id.password_tf);
        signUp = findViewById(R.id.sign_up_textView);
    }
}
