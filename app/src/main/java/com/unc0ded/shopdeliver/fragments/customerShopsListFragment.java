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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unc0ded.shopdeliver.adapters.VendorListAdapter;

import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.models.Vendor;

import java.util.ArrayList;

public class customerShopsListFragment extends Fragment {

    RecyclerView vendorRV;
    ArrayList<Vendor> vendorList = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_shops_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vendorRV = view.findViewById(R.id.vendor_list_rv);

        vendorRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        reference.child("Vendors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vendorList.clear();
                for(DataSnapshot localityShot : dataSnapshot.getChildren())
                {
                    for(DataSnapshot vendorShot : localityShot.getChildren()){
                        vendorList.add(vendorShot.getValue(Vendor.class));
                    }
                }
                vendorRV.setAdapter(new VendorListAdapter(getActivity(), vendorList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Data fetch error", databaseError.getMessage());
            }
        });
//        vendorList.add(new Vendor("Dmart","Supermarket","Baner","1234567890"));
//        vendorList.add(new Vendor("Medplus","Medical","Aundh", "2422567890"));
//        vendorList.add(new Vendor("Joshi","Confectionary","Aundh", "2345678912"));
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
}
