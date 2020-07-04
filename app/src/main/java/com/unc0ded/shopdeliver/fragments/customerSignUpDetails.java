package com.unc0ded.shopdeliver.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unc0ded.shopdeliver.activities.LoginActivity;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.activities.customerMainActivity;
import com.unc0ded.shopdeliver.models.Customer;

import java.util.ArrayList;
import java.util.Objects;

public class customerSignUpDetails extends Fragment {

    private MaterialButton signUp;
    private TextInputEditText nameE,societyNameE,flatNumberE,passwordE,reEnterPasswordE, localityE, emailE;
    private AutoCompleteTextView buildingSelect;

//    private ArrayList<String> BUILDINGS = new ArrayList<>();
    private String[] BUILDINGS;

    private String phone;

    private FirebaseAuth customerAuth;
    private DatabaseReference userReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_sign_up_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameE = view.findViewById(R.id.customer_sign_up_name);
        societyNameE = view.findViewById(R.id.customer_sign_up_society_name);
        flatNumberE = view.findViewById(R.id.customer_sign_up_flat_number);
        localityE=view.findViewById(R.id.customer_sign_up_locality);
        buildingSelect = view.findViewById(R.id.customer_sign_up_building_name);

        phone = customerSignUpDetailsArgs.fromBundle(getArguments()).getPhone();

        passwordE = view.findViewById(R.id.customer_sign_up_password);
        reEnterPasswordE = view.findViewById(R.id.customer_sign_up_reenter_password);

        signUp = view.findViewById(R.id.customer_sign_up_btn);

        emailE= view.findViewById(R.id.customer_sign_up_email);

        customerAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

//        BUILDINGS.add("A");
//        BUILDINGS.add("B");
        BUILDINGS = new String[]{"A", "B"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
        buildingSelect.setAdapter(adapter);

//        buildingSelect.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                try{
//                    userReference.child("Customers").child(Objects.requireNonNull(localityE.getText().toString().trim())).child(Objects.requireNonNull(societyNameE.getText().toString().trim())).addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                            BUILDINGS.clear();
//                            for(DataSnapshot shot : dataSnapshot.getChildren()){
//                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
//                                BUILDINGS.add(flatSplit[0]);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
//                            buildingSelect.setAdapter(adapter);
//                        }
//                        @Override
//                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                            BUILDINGS.clear();
//                            for(DataSnapshot shot : dataSnapshot.getChildren()){
//                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
//                                BUILDINGS.add(flatSplit[0]);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
//                            buildingSelect.setAdapter(adapter);
//                        }
//                        @Override
//                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                            BUILDINGS.clear();
//                            for(DataSnapshot shot : dataSnapshot.getChildren()){
//                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
//                                BUILDINGS.add(flatSplit[0]);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
//                            buildingSelect.setAdapter(adapter);
//                        }
//                        @Override
//                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                            BUILDINGS.clear();
//                            for(DataSnapshot shot : dataSnapshot.getChildren()){
//                                String[] flatSplit = shot.getValue(Customer.class).getFlat().split("-");
//                                BUILDINGS.add(flatSplit[0]);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, BUILDINGS);
//                            buildingSelect.setAdapter(adapter);
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
//                    buildingSelect.setAdapter(adapter);
//                }
//
//                return true;
//            }
//        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((!(passwordE.getText()).toString().isEmpty())&&(!(reEnterPasswordE.getText()).toString().isEmpty())
                        &&(!(nameE.getText()).toString().trim().isEmpty())&&(!(societyNameE.getText()).toString().trim().isEmpty())
                        &&(!(flatNumberE.getText()).toString().trim().isEmpty())&&(!(buildingSelect.getText().toString().trim().equals("Building Number")))
                        &&(!(emailE.getText().toString().isEmpty()))&&(!(localityE.getText().toString().isEmpty())))
                {
                    if((Objects.requireNonNull(passwordE.getText().toString()).equals(Objects.requireNonNull(reEnterPasswordE.getText().toString()))))
                    {
                        AuthCredential emailCredential = EmailAuthProvider.getCredential(emailE.getText().toString().trim(), reEnterPasswordE.getText().toString());
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
                        addCustomer(nameE.getText().toString().trim(),societyNameE.getText().toString().trim(),(buildingSelect.getText().toString().trim()+"-"+flatNumberE.getText().toString().trim()), localityE.getText().toString().trim(), phone, emailE.getText().toString().trim());
                        Intent signUp = new Intent(getActivity(), customerMainActivity.class);
                        startActivity(signUp);
                        getActivity().finish();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(((passwordE.getText()).toString().isEmpty())&&((reEnterPasswordE.getText()).toString().isEmpty())
                        &&(!(nameE.getText()).toString().trim().isEmpty())&&(!(societyNameE.getText()).toString().trim().isEmpty())
                        &&(!(flatNumberE.getText()).toString().trim().isEmpty())&&(!(buildingSelect.getText().toString().trim().equals("Building Number")))
                        &&((emailE.getText().toString().trim().isEmpty()))&&(!(localityE.getText().toString().trim().isEmpty())))
                {
                    addCustomer(nameE.getText().toString().trim(),societyNameE.getText().toString().trim(),(buildingSelect.getText().toString().trim()+"-"+flatNumberE.getText().toString().trim()), localityE.getText().toString().trim(), phone);
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

    private void addCustomer(String custName, String socName, String flat, String loc,  String phone, String email) {
        Customer createCustomer = new Customer(custName,socName,flat,loc,phone,email);
        userReference.child("Customers").child(loc).child(socName).setValue(createCustomer);
        Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

    private void addCustomer(String custName, String socName, String flat, String loc, String phone) {
        Customer createCustomer = new Customer(custName, socName, flat, loc, phone);
        userReference.child("Customers").child(loc).child(socName).setValue(createCustomer);
        Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

}
