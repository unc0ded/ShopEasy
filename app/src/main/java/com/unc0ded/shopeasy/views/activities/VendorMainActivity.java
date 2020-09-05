package com.unc0ded.shopeasy.views.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unc0ded.shopeasy.R;
import com.unc0ded.shopeasy.databinding.ActivityVendorMainBinding;
import com.unc0ded.shopeasy.viewmodels.VendorMainActivityViewModel;

public class VendorMainActivity extends AppCompatActivity {

    ActivityVendorMainBinding binding;
    BottomNavigationView bottomNav;
    Toolbar toolbar;

    VendorMainActivityViewModel vendorMainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVendorMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        bottomNav = binding.bottomNav;
        toolbar = binding.toolbar;

        setSupportActionBar(toolbar);

        vendorMainActivityViewModel = new ViewModelProvider(this).get(VendorMainActivityViewModel.class);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.vendor_inventory, R.id.vendor_orders, R.id.vendor_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.vendor_nav_host_fragment);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}
