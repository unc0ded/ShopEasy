package com.unc0ded.shopdeliver.views.fragments;

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
import androidx.lifecycle.Observer;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerSignUpDetailsBinding;
import com.unc0ded.shopdeliver.models.Address;
import com.unc0ded.shopdeliver.models.Credentials;
import com.unc0ded.shopdeliver.models.Customer;
import com.unc0ded.shopdeliver.models.Name;
import com.unc0ded.shopdeliver.viewmodels.CustomerAuthenticationViewModel;
import com.unc0ded.shopdeliver.views.activities.customerMainActivity;

import java.util.HashMap;
import java.util.Objects;

public class customerSignUpDetails extends Fragment {

    FragmentCustomerSignUpDetailsBinding binding;
    CustomerAuthenticationViewModel mCustomerAuthenticationVM = new CustomerAuthenticationViewModel();

    private String phone;

    private FirebaseAuth customerAuth;

    //empty constructor
    public customerSignUpDetails(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCustomerAuthenticationVM.getIsUploading().observe(this, isUploading -> {
            switch (isUploading){
                case "failed":
                    Toast.makeText(requireContext(), "Authentication failed! Please try again", Toast.LENGTH_LONG).show();
                    binding.progressbar.setVisibility(View.GONE);
                    break;
                case "success":
                    Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
                    binding.progressbar.setVisibility(View.GONE);
                    startActivity(new Intent(requireContext(), customerMainActivity.class));
                    requireActivity().finish();
                    break;
                case "start":
                    binding.progressbar.setVisibility(View.VISIBLE);
                    break;
                default:
                    binding.progressbar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerSignUpDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phone = customerSignUpDetailsArgs.fromBundle(requireArguments()).getPhone();

        customerAuth = FirebaseAuth.getInstance();

        /*BUILDINGS.new_user("A");
        BUILDINGS.new_user("B");
            private ArrayList<String> BUILDINGS = new ArrayList<>();*/
        final String[] STATES = new String[]{"MAHARASHTRA", "GOA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, STATES);
        binding.state.setAdapter(adapter);

        /*binding.state.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                try{
                    userReference.child("Customers").child(Objects.requireNonNull(binding.locality.getText().toString().trim())).child(Objects.requireNonNull(binding.societyName.getText().toString().trim())).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            BUILDINGS.clear();
                            for(DataSnapshot shot : dataSnapshot.getChildren()){
                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
                                BUILDINGS.new_user(flatSplit[0]);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
                            binding.state.setAdapter(adapter);
                        }
                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            BUILDINGS.clear();
                            for(DataSnapshot shot : dataSnapshot.getChildren()){
                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
                                BUILDINGS.new_user(flatSplit[0]);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
                            binding.state.setAdapter(adapter);
                        }
                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            BUILDINGS.clear();
                            for(DataSnapshot shot : dataSnapshot.getChildren()){
                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
                                BUILDINGS.new_user(flatSplit[0]);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
                            binding.state.setAdapter(adapter);
                        }
                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            BUILDINGS.clear();
                            for(DataSnapshot shot : dataSnapshot.getChildren()){
                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
                                BUILDINGS.new_user(flatSplit[0]);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
                            binding.state.setAdapter(adapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Data fetch error", databaseError.getMessage());
                        }
                    });
                }
                catch (Exception e){
                    Toast.makeText(getContext(),"No previous entries for this society",Toast.LENGTH_SHORT).show();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
                    binding.state.setAdapter(adapter);
                }

                return true;
            }
        })*/

        binding.signUpBtn.setOnClickListener(v -> {
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected){
                String first_name = binding.firstName.getText().toString().trim();
                String last_name = binding.lastName.getText().toString().trim();
                String address_first_line = binding.firstLine.getText().toString().trim();
                String address_second_line = binding.secondLine.getText().toString().trim();
                String pin_code = binding.pinCode.getText().toString().trim();
                String city = binding.city.getText().toString().trim();
                String state = binding.state.getText().toString().trim();
                final String email_id = binding.emailId.getText().toString().trim();
                String password = binding.password.getText().toString().trim();
                final String re_enter_password = binding.reEnterPassword.getText().toString().trim();

                if (first_name.isEmpty() || last_name.isEmpty() || address_first_line.isEmpty() || address_second_line.isEmpty() || city.isEmpty() || state.isEmpty() || pin_code.isEmpty())
                    Toast.makeText(getContext(), "Please fill all the compulsory fields", Toast.LENGTH_LONG).show();
                else if (email_id.isEmpty()) {
                    if (validatePinCode(pin_code))
                        addCustomerWithoutEmail(first_name, last_name, address_first_line, address_second_line, pin_code, city, state);
                    else
                        Toast.makeText(getContext(), "Please enter a valid pin code", Toast.LENGTH_SHORT).show();
                }
                else if ((password.isEmpty() || re_enter_password.isEmpty()))
                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                else if (password.equals(re_enter_password)) {
                    AuthCredential emailCredential = EmailAuthProvider.getCredential(email_id, re_enter_password);
                    Objects.requireNonNull(customerAuth.getCurrentUser()).linkWithCredential(emailCredential)
                            .addOnCompleteListener(requireActivity(), task -> {
                                if(task.isSuccessful())
                                    Toast.makeText(getContext(), "Email Linked Successfully", Toast.LENGTH_SHORT).show();
                                else {
                                    Toast.makeText(getContext(), "Email Link Failed try again later", Toast.LENGTH_LONG).show();
                                    Log.d("Email Linking", String.valueOf(task.getException()));
                                }
                            });
                    addCustomerWithEmail(first_name, last_name, address_first_line, address_second_line, pin_code, city, state, email_id);
                }
                else
                    Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_LONG).show();
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

    private void addCustomerWithoutEmail(String first_name, String last_name, String line1, String line2, final String pin_code, String city
            , String state) {
        Customer newCustomer = new Customer();

        newCustomer.setName(new Name());
        newCustomer.setAddress(new Address());
        newCustomer.setCredentials(new Credentials());

        newCustomer.getName().setFirstName(first_name);
        newCustomer.getName().setLastName(last_name);
        newCustomer.getAddress().setAddressLine1(line1);
        newCustomer.getAddress().setAddressLine2(line2);
        newCustomer.getAddress().setPinCode(pin_code);
        newCustomer.getAddress().setCity(city);
        newCustomer.getAddress().setState(state);
        newCustomer.getCredentials().setPhone(phone);

        mCustomerAuthenticationVM.registerUser(newCustomer, customerAuth.getUid());
    }

    private void addCustomerWithEmail(String first_name, String last_name, String line1, String line2, final String pin_code, String city
            , String state, String email_id) {
        Customer newCustomer = new Customer();

        newCustomer.setName(new Name());
        newCustomer.setAddress(new Address());
        newCustomer.setCredentials(new Credentials());

        newCustomer.getName().setFirstName(first_name);
        newCustomer.getName().setLastName(last_name);
        newCustomer.getAddress().setAddressLine1(line1);
        newCustomer.getAddress().setAddressLine2(line2);
        newCustomer.getAddress().setPinCode(pin_code);
        newCustomer.getAddress().setCity(city);
        newCustomer.getAddress().setState(state);
        newCustomer.getCredentials().setPhone(phone);
        newCustomer.getCredentials().setEmailId(email_id);

        mCustomerAuthenticationVM.registerUser(newCustomer, customerAuth.getUid());
    }
}
