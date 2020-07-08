package com.unc0ded.shopdeliver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unc0ded.shopdeliver.R;

public class vendorMainActivity extends AppCompatActivity {

    Toolbar vendorToolbar;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_main);

        vendorToolbar = findViewById(R.id.vendor_toolbar);
        bottomNav = findViewById(R.id.vendor_bottom_nav);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.vendor_inventory, R.id.vendor_orders, R.id.vendor_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.vendor_nav_host_fragment);
        NavigationUI.setupWithNavController(vendorToolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}
