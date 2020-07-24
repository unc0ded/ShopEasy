package com.unc0ded.shopdeliver.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.unc0ded.shopdeliver.OnCompleteFetchListener;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.viewmodels.CustomerMainActivityViewModel;
import com.unc0ded.shopdeliver.views.adapters.VendorListAdapter;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerShopsListBinding;
import com.unc0ded.shopdeliver.models.Vendor;

import java.util.ArrayList;
import java.util.Objects;

public class customerShopsListFragment extends Fragment {

    private CustomerMainActivityViewModel mCustomerMainActivityViewModel;
    private VendorListAdapter adapter = new VendorListAdapter();

    FragmentCustomerShopsListBinding binding;
    Gson gsonInstance;

    FirebaseAuth customerAuth = FirebaseAuth.getInstance();
    CollectionReference customerFdb;

    //hardcode here for debugging
    String PIN_CODE = "411007";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customerFdb = FirebaseFirestore.getInstance().collection("Customers");
        gsonInstance = new GsonBuilder().setPrettyPrinting().create();
        mCustomerMainActivityViewModel = new ViewModelProvider(this).get(CustomerMainActivityViewModel.class);

        mCustomerMainActivityViewModel.getVendors().observe(this, vendors -> {
            adapter.notifyDataSetChanged();
            populateVendorsList();
        });

        mCustomerMainActivityViewModel.getIsFetching().observe(this, isLoading -> {
            if (isLoading)
                binding.swipeRefresh.setRefreshing(true);
            else
                binding.swipeRefresh.setRefreshing(false);
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
            customerFdb.document(Objects.requireNonNull(customerAuth.getCurrentUser()).getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                            PIN_CODE = gsonInstance.toJsonTree(documentSnapshot.getData()).getAsJsonObject().getAsJsonObject("Address").get("Pin code").getAsString();
                            mCustomerMainActivityViewModel.initializeVendorList(PIN_CODE);
                        }
                    });
        }
        else
            mCustomerMainActivityViewModel.initializeVendorList(PIN_CODE);

        binding.swipeRefresh.setOnRefreshListener(() -> mCustomerMainActivityViewModel.initializeVendorList(PIN_CODE));
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

        adapter = new VendorListAdapter(requireContext(), mCustomerMainActivityViewModel.getVendors().getValue());
        binding.listRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.listRv.setAdapter(adapter);

        if (adapter.getItemCount() == 0)
            binding.message.setText(R.string.no_vendors_found_text);
        else
            binding.message.setText("Shops in the area " + PIN_CODE);
    }
}
