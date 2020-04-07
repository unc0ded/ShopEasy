package com.unc0ded.shopdeliver.mainActivities.ui.customerShopsList;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unc0ded.shopdeliver.adapter.VendorListAdapter;
import com.unc0ded.shopdeliver.mainActivities.customerMainActivity;

import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.model.Vendor;

import java.util.ArrayList;

public class customerShopsListFragment extends Fragment {

    Toolbar toolbar;
    RecyclerView vendorRV;
    ArrayList<Vendor> vendorList = new ArrayList<>();

    private customerShopsListViewModel customerShopsListViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        customerShopsListViewModel =
                ViewModelProviders.of(this).get(customerShopsListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_customer_shops_list, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        customerShopsListViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        vendorRV=root.findViewById(R.id.vendor_list_rv);
        vendorList.add(new Vendor("Dmart","Supermarket","Baner"));
        vendorList.add(new Vendor("Medplus","Medical","Aundh"));
        vendorList.add(new Vendor("Joshi","Confectionary","Aundh"));
        VendorListAdapter adapter=new VendorListAdapter(getActivity(),vendorList);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        vendorRV.setAdapter(adapter);
        vendorRV.setLayoutManager(linearLayoutManager);
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.customer_navigation_shops_list, R.id.customer_navigation_view_orders, R.id.customer_navigation_settings)
                .build();
        toolbar = view.findViewById(R.id.customer_catalog_toolbar);
        if(savedInstanceState==null)
            toolbar.inflateMenu(R.menu.customer_browse_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.cart_button : Toast.makeText(getContext(),"Cart Button",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.search_btn : Toast.makeText(getContext(),"Search Clicked",Toast.LENGTH_SHORT).show();
                        return true;
                    default: return false;
                }
            }
        });
        NavigationUI.setupWithNavController(toolbar,navController,appBarConfiguration);
    }
}
