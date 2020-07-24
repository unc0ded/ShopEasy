package com.unc0ded.shopdeliver.views.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.DialogAddToInventoryBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddItemDialog extends MaterialCardView {

    DialogAddToInventoryBinding binding;
    TextInputEditText name, quantity, price;
    MaterialAutoCompleteTextView type;
    Chip labelNew, labelPopular;

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

    public void save(FirebaseFirestore database, FirebaseAuth auth) {
        Map<String, Object> item = new HashMap<>();
        item.put("vendorId", Objects.requireNonNull(auth.getCurrentUser()).getUid());
        item.put("name", Objects.requireNonNull(Objects.requireNonNull(name.getText()).toString().trim()));
        item.put("type", type.getText().toString());
        item.put("quantity", Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(quantity.getText()).toString().trim())));
        item.put("price", Float.parseFloat((Objects.requireNonNull(Objects.requireNonNull(price.getText()).toString().trim()))));
        boolean newLabel = labelNew.isChecked();
        item.put("new", newLabel);
        boolean popLabel = labelPopular.isChecked();
        item.put("popular", popLabel);
        database.collection("Inventory").add(item)
                .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Item added to Inventory", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show());
    }
}
