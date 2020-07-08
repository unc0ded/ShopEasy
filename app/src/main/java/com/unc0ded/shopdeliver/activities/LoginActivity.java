package com.unc0ded.shopdeliver.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.unc0ded.shopdeliver.R;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText emailOrPhoneE,passwordE;
    MaterialTextView signUp;
    MaterialButton login;
    Toolbar loginBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        attachID();
        setSupportActionBar(loginBar);
    }

    private void attachID() {
        loginBar = findViewById(R.id.login_toolbar);
    }
}
