package com.unc0ded.shopdeliver.views.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.FragmentAddToInventoryBinding;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel.STATUS_FAILED;
import static com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel.STATUS_IS_UPLOADING;
import static com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel.STATUS_SUCCESS;

public class AddToInventoryDialogFragment extends DialogFragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FragmentAddToInventoryBinding binding;

    VendorMainActivityViewModel vendorMainActivityViewModel = new VendorMainActivityViewModel();

    private static Dialog chooseMethod;

    private static final int PERMISSION_REQUEST_CODE = 777;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 778;
    private static final int GALLERY_REQUEST_CODE = 107;
    private static final int CAMERA_REQUEST= 108;

    private Uri uploadUri = null;

    public AddToInventoryDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddToInventoryBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.itemTypeSpinner.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, new String[]{"Beverages", "Dairy", "Vegetables", "Fruits", "Grains", "Snacks", "Bathing"}));
        binding.itemTypeSpinner.setDropDownBackgroundResource(R.color.White);

        binding.productImage.setOnClickListener(v -> {
            chooseMethod = new Dialog(requireContext());
            chooseMethod.setContentView(R.layout.dialog_upload_image);
            Button openGallery = chooseMethod.findViewById(R.id.gallery_button), openCamera = chooseMethod.findViewById(R.id.camera_button);
            chooseMethod.setCancelable(true);

            openGallery.setOnClickListener(buttonGallery -> {
                if(EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    openGallery();
                }else{
                    EasyPermissions.requestPermissions(requireActivity(), "Allow ShopEasy to access your Gallery?", PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            });

            openCamera.setOnClickListener(cameraButton -> {
                if(EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)){
                    openCamera();
                }else{
                    EasyPermissions.requestPermissions(requireActivity(), "Allow this app to open your camera?", CAMERA_PERMISSION_REQUEST_CODE, Manifest.permission.CAMERA);
                }
            });

            chooseMethod.show();
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vendorMainActivityViewModel.getIsUploading().observe(this, s -> {
            switch (s){
                case STATUS_IS_UPLOADING:
                    binding.progressbar.setVisibility(View.VISIBLE);
                    break;
                case STATUS_SUCCESS:
                default:
                    Toast.makeText(requireContext(), "Product added to inventory successfully!", Toast.LENGTH_SHORT).show();
                    binding.progressbar.setVisibility(View.GONE);
                    dismiss();
                    break;
                case STATUS_FAILED:
                    binding.progressbar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Something went wrong! Could not add product", Toast.LENGTH_LONG).show();
                    binding.productImage.setImageResource(R.drawable.ic_shopping_cart_24px);
                    break;
            }
        });
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
                return true;
            case R.id.cancel_add:
                dismiss();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (chooseMethod.isShowing()){ chooseMethod.dismiss();}

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Glide.with(requireContext()).load(data.getData()).into(binding.productImage);
            try {
                photo = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null && data.getExtras() != null){
            photo = (Bitmap) data.getExtras().get("data");
            binding.productImage.setImageBitmap(photo);
        }

        if (photo != null) {
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), photo, "Title", null);
            uploadUri = Uri.parse(path);
        }
    }

    private void openGallery() {
        Log.i("FUNCTION", "openGallery() started");
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void openCamera() {
        Log.i("FUNCTION", "openCamera() started");
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
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

        vendorMainActivityViewModel.addProduct(newProduct, auth, uploadUri);
    }
}