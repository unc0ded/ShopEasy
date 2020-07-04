package com.unc0ded.shopdeliver.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unc0ded.shopdeliver.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class customerMainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    Toolbar custToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        bottomNav = findViewById(R.id.customer_bottom_nav);
        custToolbar = findViewById(R.id.customer_toolbar);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.customer_navigation_shops_list, R.id.customer_navigation_view_orders, R.id.customer_navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.customer_nav_host_fragment);
        NavigationUI.setupWithNavController(custToolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

}
