package com.unc0ded.shopdeliver.fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.adapters.VendorListAdapter;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerShopsListBinding;
import com.unc0ded.shopdeliver.models.Vendor;

import java.util.ArrayList;
import java.util.Objects;

public class customerShopsListFragment extends Fragment {

    FragmentCustomerShopsListBinding binding;
    Gson gsonInstance;

    ArrayList<Vendor> vendorList = new ArrayList<>();

    FirebaseAuth customerAuth = FirebaseAuth.getInstance();
    CollectionReference vendorFdb, customerFdb;

    //hardcode here for debugging
    String PIN_CODE = "411008";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vendorFdb = FirebaseFirestore.getInstance().collection("Vendors");
        customerFdb = FirebaseFirestore.getInstance().collection("Customers");
        gsonInstance = new GsonBuilder().setPrettyPrinting().create();
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

        binding.listRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (customerAuth.getCurrentUser() != null){
            customerFdb.document(Objects.requireNonNull(customerAuth.getCurrentUser()).getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                            PIN_CODE = gsonInstance.toJsonTree(documentSnapshot.getData()).getAsJsonObject().getAsJsonObject("Address").get("Pin code").getAsString();
                            fetchVendors();
                        }
                    });
        }
        else
            fetchVendors();
    }

    private void fetchVendors() {
        vendorFdb.document(PIN_CODE).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.getData() != null){
                JsonObject vendors_list = gsonInstance.toJsonTree(documentSnapshot.getData()).getAsJsonObject();
                populateVendorsList(vendors_list);
            }
            else
                binding.message.setText(getResources().getString(R.string.no_vendors_found_text));
        });
    }

    private void populateVendorsList(JsonObject vendors_list) {
        vendorList.clear();
        for (String key: vendors_list.keySet()) {
            if (!key.equals("New pin code available:")){
                vendorList.add(new Vendor(vendors_list.getAsJsonObject(key).get("Shop Name").getAsString(),
                        vendors_list.getAsJsonObject(key).get("Shop Type").getAsString(),
                        vendors_list.getAsJsonObject(key).get("Address").getAsJsonObject().get("Address Line 2").getAsString(),
                        "9999999999"));

                binding.listRv.setAdapter(new VendorListAdapter(getActivity(), vendorList));
            }
        }

        binding.message.setText("Shops in the area " + PIN_CODE);
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
}
