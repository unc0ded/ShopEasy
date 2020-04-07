package com.unc0ded.shopdeliver.mainActivities.ui.customerSettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.unc0ded.shopdeliver.R;

public class customerSettingsFragment extends Fragment {
    Toolbar toolbar;

    private customerSettingsViewModel customerSettingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        customerSettingsViewModel =
                ViewModelProviders.of(this).get(customerSettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_customer_settings, container, false);
//        final TextView textView = root.findViewById(R.id.settings_tv);
//        customerSettingsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.customer_navigation_shops_list, R.id.customer_navigation_view_orders, R.id.customer_navigation_settings)
                .build();
        toolbar = view.findViewById(R.id.customer_settings_toolbar);
        if(savedInstanceState==null)
            toolbar.inflateMenu(R.menu.customer_settings_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.customer_logout_button : Toast.makeText(getContext(),"Log Out Button",Toast.LENGTH_SHORT).show();
                        return true;
                    default: return false;
                }
            }
        });
        NavigationUI.setupWithNavController(toolbar,navController,appBarConfiguration);
    }
}
