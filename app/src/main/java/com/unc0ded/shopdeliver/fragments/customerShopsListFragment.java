package com.unc0ded.shopdeliver.fragments;

import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.adapters.VendorListAdapter;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerShopsListBinding;
import com.unc0ded.shopdeliver.models.Vendor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class customerShopsListFragment extends Fragment {

    FragmentCustomerShopsListBinding binding;

    ArrayList<Vendor> vendorList = new ArrayList<>();

    FirebaseAuth customerAuth = FirebaseAuth.getInstance();
    CollectionReference vendorFdb = FirebaseFirestore.getInstance().collection("Vendors");

    String PIN_CODE = "411008";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerShopsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.listRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (customerAuth.getUid() != null){
            vendorFdb.document(customerAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                        try {
                            Log.i("pin code fetched", new JSONObject(documentSnapshot.getData()).getJSONObject("Address").getString("Pin code"));
                            PIN_CODE = new JSONObject(documentSnapshot.getData()).getJSONObject("Address").getString("Pin code");
                            fetchVendors();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }else {
            fetchVendors();
        }
    }

    private void fetchVendors() {
        vendorFdb.document(PIN_CODE).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.getData() != null){
                    JSONObject vendors_list = new JSONObject(documentSnapshot.getData());
                    populateVendorsList(vendors_list);
                }else
                    binding.message.setText("No vendors found\nin your area :(");
            }
        });
    }

    private void populateVendorsList(JSONObject vendors_list) {
        Iterator<String> keys = vendors_list.keys();

        vendorList.clear();
        while(keys.hasNext()){
            try {
                String key = keys.next();
                if (!key.equals("New pin code available:")){
                    vendorList.add(new Vendor(vendors_list.getJSONObject(key).getString("Shop Name")
                            , vendors_list.getJSONObject(key).getString("Shop Type")
                            , vendors_list.getJSONObject(key).getJSONObject("Address").getString("Address Line 2")
                            , "9999999999"));

                    binding.listRv.setAdapter(new VendorListAdapter(getActivity(), vendorList));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        binding.message.setText("Shops in the area " + PIN_CODE);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.customer_browse_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
