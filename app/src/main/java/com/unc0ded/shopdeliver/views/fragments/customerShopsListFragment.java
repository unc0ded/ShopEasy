package com.unc0ded.shopdeliver.views.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerShopsListBinding;
import com.unc0ded.shopdeliver.viewmodels.CustomerMainActivityViewModel;
import com.unc0ded.shopdeliver.views.adapters.VendorListAdapter;

public class customerShopsListFragment extends Fragment {

    private CustomerMainActivityViewModel mCustomerMainActivityVM;
    private VendorListAdapter adapter = new VendorListAdapter();

    FragmentCustomerShopsListBinding binding;

    FirebaseAuth customerAuth = FirebaseAuth.getInstance();

    //hardcode here for debugging
    String PIN_CODE = "411008";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCustomerMainActivityVM = new ViewModelProvider(this).get(CustomerMainActivityViewModel.class);

        mCustomerMainActivityVM.getIsFetching().observe(this, isLoading -> {
            if (isLoading){
                binding.swipeRefresh.setRefreshing(true);
            }
            else{
                new Handler().postDelayed(() -> {
                    binding.swipeRefresh.setRefreshing(false);
                }, 1000);
            }
        });

        mCustomerMainActivityVM.getVendors().observe(this, vendors -> {
            adapter.notifyDataSetChanged();
            populateVendorsList();
        });

        mCustomerMainActivityVM.getCustomerPinCode().observe(this, pinCode -> {
            PIN_CODE = pinCode;
            mCustomerMainActivityVM.fetchVendorList(pinCode);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerShopsListBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (customerAuth.getCurrentUser() != null){
            mCustomerMainActivityVM.fetchCustomerPinCode(customerAuth.getUid());
        } else {
            mCustomerMainActivityVM.fetchVendorList(PIN_CODE);
        }

        binding.swipeRefresh.setOnRefreshListener(() -> {
            mCustomerMainActivityVM.fetchVendorList(PIN_CODE);
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.customer_shops_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_btn : Toast.makeText(getContext(), "Search", Toast.LENGTH_SHORT).show();
            return true;
            case R.id.cart_button : Toast.makeText(getContext(), "Cart", Toast.LENGTH_SHORT).show();
            return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateVendorsList() {

        if (mCustomerMainActivityVM.getVendors().getValue() != null){
            adapter = new VendorListAdapter(requireContext(), mCustomerMainActivityVM.getVendors().getValue());
            binding.listRv.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.listRv.setAdapter(adapter);

            binding.message.setText(R.string.shops_in_the_area_text);
            binding.message.append(PIN_CODE);
        }else{
            binding.message.setText(R.string.no_vendors_found_text);
        }

    }
}
