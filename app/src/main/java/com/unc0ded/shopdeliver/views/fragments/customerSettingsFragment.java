package com.unc0ded.shopdeliver.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.views.activities.LoginActivity;
import com.unc0ded.shopdeliver.databinding.FragmentCustomerSettingsBinding;

public class customerSettingsFragment extends Fragment {

    FragmentCustomerSettingsBinding binding;
    FirebaseAuth userAuth;

    public customerSettingsFragment() {
        //Empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerSettingsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout_button) {
            if (userAuth.getCurrentUser() != null) {
                Toast.makeText(getContext(), userAuth.getCurrentUser().getDisplayName() + "Logged Out", Toast.LENGTH_SHORT).show();
                userAuth.signOut();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            }
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }
}
