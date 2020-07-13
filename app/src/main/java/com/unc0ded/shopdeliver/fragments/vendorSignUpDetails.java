package com.unc0ded.shopdeliver.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.activities.vendorMainActivity;
import com.unc0ded.shopdeliver.databinding.FragmentVendorSignUpDetailsBinding;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class vendorSignUpDetails extends Fragment {

    FragmentVendorSignUpDetailsBinding binding;

    String phone;

    private FirebaseFirestore newUser;
    FirebaseAuth vendorAuth;

    //empty constructor
    public vendorSignUpDetails() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendorSignUpDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phone = vendorSignUpDetailsArgs.fromBundle(requireArguments()).getPhone();

        vendorAuth = FirebaseAuth.getInstance();
        newUser = FirebaseFirestore.getInstance();

        final String[] STATES = new String[]{"MAHARASHTRA", "GOA"};
        ArrayAdapter<String> adapter_state = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, STATES);
        binding.state.setAdapter(adapter_state);
        final String[] SHOP_TYPES = new String[]{"Grocery", "Medical Store", "Supermarket"};
        ArrayAdapter<String> adapter_shop_type = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, SHOP_TYPES);
        binding.shopType.setAdapter(adapter_shop_type);

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (isConnected){
                    String first_name = binding.firstName.getText().toString().trim();
                    String last_name = binding.lastName.getText().toString().trim();
                    String shop_name = binding.shopName.getText().toString().trim();
                    String shop_type = binding.shopType.getText().toString().trim();
                    String address_first_line = binding.firstLine.getText().toString().trim();
                    String address_second_line = binding.secondLine.getText().toString().trim();
                    String pin_code = binding.pinCode.getText().toString().trim();
                    String city = binding.city.getText().toString().trim();
                    String state = binding.state.getText().toString().trim();
                    final String email_id = binding.emailId.getText().toString().trim();
                    String password = binding.password.getText().toString().trim();
                    final String re_enter_password = binding.reEnterPassword.getText().toString().trim();

                    if (first_name.isEmpty() || last_name.isEmpty() || shop_name.isEmpty() || shop_type.isEmpty() || address_first_line.isEmpty() || address_second_line.isEmpty() || city.isEmpty() || state.isEmpty() || pin_code.isEmpty())
                        Toast.makeText(getContext(), "Please fill all the compulsory fields", Toast.LENGTH_LONG).show();
                    else if (email_id.isEmpty()){
                        if (validatePinCode(pin_code))
                            addVendorWithoutEmail(first_name, last_name, shop_name, shop_type, address_first_line, address_second_line, pin_code, city, state);
                        else
                            Toast.makeText(getContext(), "Please enter a valid pin code", Toast.LENGTH_SHORT).show();
                    }else if ((password.isEmpty() || re_enter_password.isEmpty()))
                        Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                    else if (password.equals(re_enter_password)) {
                        AuthCredential emailCredential = EmailAuthProvider.getCredential(email_id, re_enter_password);
                        Objects.requireNonNull(vendorAuth.getCurrentUser()).linkWithCredential(emailCredential)
                                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                            Toast.makeText(getContext(), "Email Linked Successfully", Toast.LENGTH_SHORT).show();
                                        else{
                                            Toast.makeText(getContext(), "Email Link Failed try again later", Toast.LENGTH_LONG).show();
                                            Log.d("Email Linking", String.valueOf(task.getException()));
                                        }
                                    }
                                });
                        addVendorWithEmail(first_name, last_name, shop_name, shop_type, address_first_line, address_second_line, pin_code, city, state, email_id, re_enter_password);
                    }else
                        Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }

    private boolean validatePinCode(String pin_code) {
        boolean valid = false;
        if (pin_code.length() == 6){ valid = pin_code.charAt(0) != '0'; }
        return valid;
    }

    private void addVendorWithoutEmail(String first_name, String last_name, String shop_name, String shop_type, String line1, String line2, final String pin_code, String city
            , String state) {
        final HashMap<String, Object> new_user = new HashMap<>();
        final HashMap<String, String> proprietor_name = new HashMap<>()
                , address = new HashMap<>()
                , credentials = new HashMap<>();

        proprietor_name.put("First Name", first_name);
        proprietor_name.put("Last Name", last_name);

        address.put("Address Line 1", line1);
        address.put("Address Line 2", line2);
        address.put("Pin code", pin_code);
        address.put("City", city);
        address.put("State", state);

        credentials.put("Phone", phone);

        new_user.put("Name", proprietor_name);
        new_user.put("Address", address);
        new_user.put("Credentials", credentials);
        new_user.put("Shop Name", shop_name);
        new_user.put("Shop Type", shop_type);

        newUser.collection("Vendors").document(pin_code).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    HashMap<String, Date> timeStamp = new HashMap<>();
                    timeStamp.put("New pin code available:", Calendar.getInstance().getTime());
                    newUser.collection("Vendors").document(pin_code).set(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            newUser.collection("Vendors").document(pin_code).update(Objects.requireNonNull(vendorAuth.getUid()), new_user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(requireContext(), vendorMainActivity.class));
                                    requireActivity().finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "There was some problem registering you\nPlease try again later", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }else{
                    newUser.collection("Vendors").document(pin_code).update(Objects.requireNonNull(vendorAuth.getUid()), new_user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(requireContext(), vendorMainActivity.class));
                            requireActivity().finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "There was some problem registering you\nPlease try again later", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    private void addVendorWithEmail(String first_name, String last_name, String shop_name, String shop_type, String line1, String line2, final String pin_code, String city
            , String state, String email_id, String password) {
        final HashMap<String, Object> new_user = new HashMap<>();
        final HashMap<String, String> proprietor_name = new HashMap<>()
                , address = new HashMap<>()
                , credentials = new HashMap<>();

        proprietor_name.put("First Name", first_name);
        proprietor_name.put("Last Name", last_name);

        address.put("Address Line 1", line1);
        address.put("Address Line 2", line2);
        address.put("Pin code", pin_code);
        address.put("City", city);
        address.put("State", state);

        credentials.put("Phone", phone);
        credentials.put("Email ID", email_id);
        credentials.put("Password", password);

        new_user.put("Name", proprietor_name);
        new_user.put("Address", address);
        new_user.put("Credentials", credentials);
        new_user.put("Shop Name", shop_name);
        new_user.put("Shop Type", shop_type);

        newUser.collection("Vendors").document(pin_code).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    HashMap<String, Date> timeStamp = new HashMap<>();
                    timeStamp.put("New pin code available:", Calendar.getInstance().getTime());
                    newUser.collection("Vendors").document(pin_code).set(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            newUser.collection("Vendors").document(pin_code).update(Objects.requireNonNull(vendorAuth.getUid()), new_user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(requireContext(), vendorMainActivity.class));
                                    requireActivity().finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "There was some problem registering you\nPlease try again later", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }else{
                    newUser.collection("Vendors").document(pin_code).update(Objects.requireNonNull(vendorAuth.getUid()), new_user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(requireContext(), vendorMainActivity.class));
                            requireActivity().finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "There was some problem registering you\nPlease try again later", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }
}
