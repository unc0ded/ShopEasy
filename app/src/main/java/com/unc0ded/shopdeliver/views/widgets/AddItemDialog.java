package com.unc0ded.shopdeliver.views.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.DialogAddToInventoryBinding;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddItemDialog extends MaterialCardView {

    DialogAddToInventoryBinding binding;
    TextInputEditText name, quantity, price;
    MaterialAutoCompleteTextView type;
    Chip labelNew, labelPopular;
    VendorMainActivityViewModel vendorMainActivityViewModel = new VendorMainActivityViewModel();

    public AddItemDialog(Context context) {
        super(context);
        binding = DialogAddToInventoryBinding.inflate(LayoutInflater.from(context), this, true);

        name = binding.itemNameEt;
        type = binding.itemTypeSpinner;
        quantity = binding.quantityEt;
        price = binding.priceEt;
        labelNew = binding.newLabel;
        labelPopular = binding.popularLabel;

        type.setAdapter(new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, new String[]{"Beverages", "Dairy", "Vegetables", "Fruits", "Grains", "Snacks", "Bathing"}));
    }

    public void save(DialogInterface dialogInterface, FirebaseAuth auth) {
        Product newProduct = new Product();

        Map<String, Object> item = new HashMap<>();
        newProduct.setVendorId(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        newProduct.setName(Objects.requireNonNull(Objects.requireNonNull(name.getText()).toString().trim()));
        newProduct.setType(type.getText().toString());
        newProduct.setQuantity(Long.valueOf(Objects.requireNonNull(quantity.getText()).toString().trim()));
        newProduct.setPrice(Double.valueOf(Objects.requireNonNull(price.getText()).toString().trim()));
        newProduct.setNewLabel(labelNew.isChecked());
        newProduct.setPopularLabel(labelPopular.isChecked());

        vendorMainActivityViewModel.addProduct(newProduct);
        dialogInterface.dismiss();
    }
}
