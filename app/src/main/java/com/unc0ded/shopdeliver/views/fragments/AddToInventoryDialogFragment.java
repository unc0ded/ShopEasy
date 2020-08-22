package com.unc0ded.shopdeliver.views.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.ShopEasy;
import com.unc0ded.shopdeliver.databinding.FragmentAddToInventoryBinding;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.utils.SessionManager;
import com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel.STATUS_FAILED;
import static com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel.STATUS_IS_UPLOADING;
import static com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel.STATUS_SUCCESS;

public class AddToInventoryDialogFragment extends DialogFragment {

    FragmentAddToInventoryBinding binding;
    VendorMainActivityViewModel vendorMainActivityViewModel;
    SessionManager sessionManager;

    private ActivityResultLauncher<String[]> cameraRequestPermissionLauncher;
    private ActivityResultLauncher<String[]> galleryRequestPermissionLauncher;
    private ActivityResultLauncher<String> pickImage;
    private ActivityResultLauncher<Uri> takePicture;
    private static Dialog chooseMethod;
    private Uri imageUri;

    private static final String[] REQUIRED_PERMISSIONS_GALLERY = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private static final String[] REQUIRED_PERMISSIONS_CAMERA = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    public AddToInventoryDialogFragment() {
        // Required empty public constructor
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

        cameraRequestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean allGranted = true;
            for (String permission: REQUIRED_PERMISSIONS_CAMERA) {
                if (!result.get(permission)) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted)
                openCamera();
            else Toast.makeText(requireContext(), "Camera permissions not granted by user", Toast.LENGTH_SHORT).show();
        });

        galleryRequestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean allGranted = true;
            for (String permission: REQUIRED_PERMISSIONS_GALLERY) {
                if (!result.get(permission)) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted)
                openGallery();
            else Toast.makeText(requireContext(), "Storage permissions not granted by user", Toast.LENGTH_SHORT).show();
        });

        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                imageUri = result;
                if (chooseMethod.isShowing()) chooseMethod.dismiss();
                Glide.with(requireContext()).load(result).into(binding.productImage);
            }
            else {
                if (chooseMethod.isShowing()) chooseMethod.dismiss();
                Toast.makeText(requireContext(), "No image picked.", Toast.LENGTH_SHORT).show();
            }
        });

        takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                Log.i("ImageCapture", "OK");
                if (chooseMethod.isShowing()) chooseMethod.dismiss();
                Glide.with(requireContext()).load(imageUri).into(binding.productImage);
            }
            else {
                if (chooseMethod.isShowing()) chooseMethod.dismiss();
                Toast.makeText(requireContext(), "Failed to save image.", Toast.LENGTH_SHORT).show();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddToInventoryBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vendorMainActivityViewModel = new ViewModelProvider(requireActivity()).get(VendorMainActivityViewModel.class);
        sessionManager = ((ShopEasy)requireActivity().getApplication()).getSessionManager();

        binding.itemTypeSpinner.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, new String[]{"Beverages", "Dairy", "Vegetables", "Fruits", "Grains", "Snacks", "Bathing"}));
        binding.itemTypeSpinner.setDropDownBackgroundResource(R.color.White);

        binding.productImage.setOnClickListener(v -> {
            chooseMethod = new Dialog(requireContext());
            chooseMethod.setContentView(R.layout.dialog_upload_image);
            Button openGallery = chooseMethod.findViewById(R.id.gallery_button), openCamera = chooseMethod.findViewById(R.id.camera_button);
            chooseMethod.setCancelable(true);

            openGallery.setOnClickListener(buttonGallery -> {
                if (storagePermissionsGranted()){
                    openGallery();
                }
                else {
                    galleryRequestPermissionLauncher.launch(REQUIRED_PERMISSIONS_GALLERY);
                }
            });

            openCamera.setOnClickListener(cameraButton -> {
                if(cameraPermissionGranted()) {
                    openCamera();
                }
                else {
                    cameraRequestPermissionLauncher.launch(REQUIRED_PERMISSIONS_CAMERA);
                }
            });
            chooseMethod.show();
        });
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

    private boolean cameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), REQUIRED_PERMISSIONS_CAMERA[0]) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean storagePermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS_GALLERY) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void openGallery() {
        Log.i("FUNCTION", "openGallery() started");
        pickImage.launch("image/*");
    }

    private void openCamera() {
        Log.i("FUNCTION", "openCamera() started");
        imageUri = getUriForImage();
        takePicture.launch(imageUri);
    }

    private Uri getUriForImage() {
        return Uri.withAppendedPath(Uri.fromFile(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")));
    }

    public void saveItem() {
        Product newProduct = new Product();
        newProduct.setName(Objects.requireNonNull(Objects.requireNonNull(binding.itemNameEt.getText()).toString().trim()));
        newProduct.setType(binding.itemTypeSpinner.getText().toString());
        newProduct.setQuantity(Integer.parseInt(Objects.requireNonNull(binding.quantityEt.getText()).toString().trim()));
        newProduct.setPrice(Double.valueOf(Objects.requireNonNull(binding.priceEt.getText()).toString().trim()));
        if (binding.newLabel.isChecked())
            newProduct.getTags().add("new");
        if (binding.popularLabel.isChecked())
            newProduct.getTags().add("popular");
        String userId = sessionManager.fetchUserId();
        if (userId != null)
            vendorMainActivityViewModel.addProduct(newProduct, userId, imageUri);
        else Toast.makeText(requireContext(), "Couldn't find user details", Toast.LENGTH_SHORT).show();
    }
}