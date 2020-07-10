package com.unc0ded.shopdeliver.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.ActivityVendorMainBinding;

public class vendorMainActivity extends AppCompatActivity {

    ActivityVendorMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVendorMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.vendor_inventory, R.id.vendor_orders, R.id.vendor_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.vendor_nav_host_fragment);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
    }
}
