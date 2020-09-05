package com.unc0ded.shopeasy.views.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.unc0ded.shopeasy.databinding.ActivityLoginBinding;
import com.unc0ded.shopeasy.viewmodels.LoginActivityViewModel;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    LoginActivityViewModel loginActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.loginActivityToolbar);

        loginActivityViewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);
    }
}
