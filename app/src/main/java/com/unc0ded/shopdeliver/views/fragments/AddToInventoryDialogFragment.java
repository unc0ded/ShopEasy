package com.unc0ded.shopdeliver.views.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.FragmentAddToInventoryBinding;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel;

import java.util.Objects;

public class AddToInventoryDialogFragment extends DialogFragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FragmentAddToInventoryBinding binding;

    VendorMainActivityViewModel vendorMainActivityViewModel = new VendorMainActivityViewModel();

//    FragmentManager fragmentManager = requireParentFragment().getParentFragmentManager();

    public AddToInventoryDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.itemTypeSpinner.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, new String[]{"Beverages", "Dairy", "Vegetables", "Fruits", "Grains", "Snacks", "Bathing"}));
        binding.itemTypeSpinner.setDropDownBackgroundResource(R.color.White);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddToInventoryBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.vendor_inventory_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_button:
                saveItem();
                dismiss();
                return true;
            case R.id.cancel_add:
                dismiss();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void saveItem() {
        Product newProduct = new Product();

        newProduct.setVendorId(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        newProduct.setName(Objects.requireNonNull(Objects.requireNonNull(binding.itemNameEt.getText()).toString().trim()));
        newProduct.setType(binding.itemTypeSpinner.getText().toString());
        newProduct.setQuantity(Long.valueOf(Objects.requireNonNull(binding.quantityEt.getText()).toString().trim()));
        newProduct.setPrice(Double.valueOf(Objects.requireNonNull(binding.priceEt.getText()).toString().trim()));
        newProduct.setNewLabel(binding.newLabel.isChecked());
        newProduct.setPopularLabel(binding.popularLabel.isChecked());

        vendorMainActivityViewModel.addProduct(newProduct);
    }
}