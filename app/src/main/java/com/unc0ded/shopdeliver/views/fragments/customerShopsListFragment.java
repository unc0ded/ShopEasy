package com.unc0ded.shopdeliver.views.fragments;

import android.os.Bundle;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.viewmodels.CustomerShopsListFragmentViewModel;
import com.unc0ded.shopdeliver.views.adapters.VendorListAdapter;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerShopsListBinding;

public class customerShopsListFragment extends Fragment {

    private CustomerShopsListFragmentViewModel customerShopsListFragmentVM;
    private VendorListAdapter adapter = new VendorListAdapter();

    FragmentCustomerShopsListBinding binding;
    Gson gsonInstance =  new GsonBuilder().setPrettyPrinting().create();

    FirebaseAuth customerAuth = FirebaseAuth.getInstance();
    CollectionReference customerFdb = FirebaseFirestore.getInstance().collection("Customers");

    //hardcode here for debugging
    String PIN_CODE = "411008";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customerShopsListFragmentVM = new ViewModelProvider(this).get(CustomerShopsListFragmentViewModel.class);

        customerShopsListFragmentVM.getIsFetching().observe(this, isLoading -> {
            if (isLoading)
                binding.swipeRefresh.setRefreshing(true);
            else
                binding.swipeRefresh.setRefreshing(false);
        });

        customerShopsListFragmentVM.getVendors().observe(this, vendors -> {
            adapter.notifyDataSetChanged();
            populateVendorsList();
        });

        customerShopsListFragmentVM.getCustomerPinCode().observe(this, pinCode -> {
            PIN_CODE = pinCode;
            customerShopsListFragmentVM.fetchVendorList(pinCode);
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
            customerShopsListFragmentVM.fetchCustomerPinCode(customerAuth.getUid());
        } else {
            customerShopsListFragmentVM.fetchVendorList(PIN_CODE);
        }

        binding.swipeRefresh.setOnRefreshListener(() -> {
            customerShopsListFragmentVM.fetchVendorList(PIN_CODE);
            binding.message.setText(R.string.no_vendors_found_text);
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

        adapter = new VendorListAdapter(requireContext(), customerShopsListFragmentVM.getVendors().getValue());
        binding.listRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.listRv.setAdapter(adapter);

        if (adapter.getItemCount() == 0)
            binding.message.setText(R.string.no_vendors_found_text);
        else
            binding.message.setText("Shops in the area " + PIN_CODE);
    }
}
