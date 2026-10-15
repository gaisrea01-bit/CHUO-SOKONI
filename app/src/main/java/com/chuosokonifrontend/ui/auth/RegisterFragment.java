package com.chuosokonifrontend.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.chuosokonifrontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterFragment extends Fragment {

    private TextInputEditText etFullName, etPhone, etPassword;
    private ChipGroup chipGroupRole;
    private MaterialButton btnRegisterSubmit;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etFullName = view.findViewById(R.id.etFullName);
        etPhone = view.findViewById(R.id.etPhone);
        etPassword = view.findViewById(R.id.etPassword);
        chipGroupRole = view.findViewById(R.id.chipGroupRole);
        btnRegisterSubmit = view.findViewById(R.id.btnRegisterSubmit);
        TextView tvLoginPrompt = view.findViewById(R.id.tvLoginPrompt);

        if (tvLoginPrompt != null) {
            tvLoginPrompt.setOnClickListener(v -> navController.navigate(R.id.action_register_to_login));
        }

        authViewModel.getRegisterSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), "Registration Successful! Please login.", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_register_to_login);
            }
        });

        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        btnRegisterSubmit.setOnClickListener(v -> {
            String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            String selectedRole = "CUSTOMER";
            if (chipGroupRole != null) {
                int checkedChipId = chipGroupRole.getCheckedChipId();
                if (checkedChipId == R.id.chipSeller) {
                    selectedRole = "SELLER";
                } else if (checkedChipId == R.id.chipInvestor) {
                    selectedRole = "INVESTOR";
                } else if (checkedChipId == R.id.chipAgent) {
                    selectedRole = "AGENT";
                }
            }

            authViewModel.register(fullName, phone, password, selectedRole);
        });
    }
}
