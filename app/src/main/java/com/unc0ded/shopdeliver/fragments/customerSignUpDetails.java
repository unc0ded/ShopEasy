package com.unc0ded.shopdeliver.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.activities.customerMainActivity;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerSignUpDetailsBinding;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerSignUpMainBinding;
import com.unc0ded.shopdeliver.models.Customer;

import java.util.Objects;

public class customerSignUpDetails extends Fragment {

    FragmentCustomerSignUpDetailsBinding binding;

//    private ArrayList<String> BUILDINGS = new ArrayList<>();
    private String[] BUILDINGS;

    private String phone;

    private FirebaseAuth customerAuth;
    private DatabaseReference userReference;

    //empty constructor
    public customerSignUpDetails(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerSignUpDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phone = customerSignUpDetailsArgs.fromBundle(getArguments()).getPhone();

        customerAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

//        BUILDINGS.add("A");
//        BUILDINGS.add("B");
        BUILDINGS = new String[]{"A", "B"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
        binding.buildingName.setAdapter(adapter);

//        binding.buildingName.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                try{
//                    userReference.child("Customers").child(Objects.requireNonNull(binding.locality.getText().toString().trim())).child(Objects.requireNonNull(binding.societyName.getText().toString().trim())).addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                            BUILDINGS.clear();
//                            for(DataSnapshot shot : dataSnapshot.getChildren()){
//                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
//                                BUILDINGS.add(flatSplit[0]);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
//                            binding.buildingName.setAdapter(adapter);
//                        }
//                        @Override
//                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                            BUILDINGS.clear();
//                            for(DataSnapshot shot : dataSnapshot.getChildren()){
//                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
//                                BUILDINGS.add(flatSplit[0]);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
//                            binding.buildingName.setAdapter(adapter);
//                        }
//                        @Override
//                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                            BUILDINGS.clear();
//                            for(DataSnapshot shot : dataSnapshot.getChildren()){
//                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
//                                BUILDINGS.add(flatSplit[0]);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
//                            binding.buildingName.setAdapter(adapter);
//                        }
//                        @Override
//                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                            BUILDINGS.clear();
//                            for(DataSnapshot shot : dataSnapshot.getChildren()){
//                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
//                                BUILDINGS.add(flatSplit[0]);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
//                            binding.buildingName.setAdapter(adapter);
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Log.e("Data fetch error", databaseError.getMessage());
//                        }
//                    });
//                }
//                catch (Exception e){
//                    Toast.makeText(getContext(),"No previous entries for this society",Toast.LENGTH_SHORT).show();
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
//                    binding.buildingName.setAdapter(adapter);
//                }
//
//                return true;
//            }
//        });


        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((!(binding.password.getText()).toString().isEmpty())&&(!(binding.reEnterPassword.getText()).toString().isEmpty())
                        &&(!(binding.name.getText()).toString().trim().isEmpty())&&(!(binding.societyName.getText()).toString().trim().isEmpty())
                        &&(!(binding.flatNumber.getText()).toString().trim().isEmpty())&&(!(binding.buildingName.getText().toString().trim().equals("Building Number")))
                        &&(!(binding.emailId.getText().toString().isEmpty()))&&(!(binding.locality.getText().toString().isEmpty())))
                {
                    if((Objects.requireNonNull(binding.password.getText().toString()).equals(Objects.requireNonNull(binding.reEnterPassword.getText().toString()))))
                    {
                        AuthCredential emailCredential = EmailAuthProvider.getCredential(binding.emailId.getText().toString().trim(), binding.reEnterPassword.getText().toString());
                        customerAuth.getCurrentUser().linkWithCredential(emailCredential)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                            Toast.makeText(getContext(), "Email Linked Successfully", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(getContext(), "Email Link Failed: "+ task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        addCustomer(binding.name.getText().toString().trim(),binding.societyName.getText().toString().trim(),(binding.buildingName.getText().toString().trim()+"-"+binding.flatNumber.getText().toString().trim()), binding.locality.getText().toString().trim(), phone, binding.emailId.getText().toString().trim());
                        Intent signUp = new Intent(getActivity(), customerMainActivity.class);
                        startActivity(signUp);
                        getActivity().finish();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(((binding.password.getText()).toString().isEmpty())&&((binding.reEnterPassword.getText()).toString().isEmpty())
                        &&(!(binding.name.getText()).toString().trim().isEmpty())&&(!(binding.societyName.getText()).toString().trim().isEmpty())
                        &&(!(binding.flatNumber.getText()).toString().trim().isEmpty())&&(!(binding.buildingName.getText().toString().trim().equals("Building Number")))
                        &&((binding.emailId.getText().toString().trim().isEmpty()))&&(!(binding.locality.getText().toString().trim().isEmpty())))
                {
                    addCustomer(binding.name.getText().toString().trim(),binding.societyName.getText().toString().trim(),(binding.buildingName.getText().toString().trim()+"-"+binding.flatNumber.getText().toString().trim()), binding.locality.getText().toString().trim(), phone);
                    Intent signUp = new Intent(getActivity(), customerMainActivity.class);
                    startActivity(signUp);
                    getActivity().finish();
                }
                else
                {
                    Toast.makeText(getActivity(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }

    private void addCustomer(String custName, String socName, String flat, String loc, String phone, String emailId) {
        Customer createCustomer = new Customer(custName,socName,flat,loc,phone,emailId);
        userReference.child("Customers").child(loc).child(socName).setValue(createCustomer);
        Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

    private void addCustomer(String custName, String socName, String flat, String loc, String phone) {
        Customer createCustomer = new Customer(custName, socName, flat, loc, phone);
        userReference.child("Customers").child(loc).child(socName).setValue(createCustomer);
        Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

}
