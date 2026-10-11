package com.chuosokonifrontend.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.chuosokonifrontend.R;
import com.google.android.material.button.MaterialButton;

public class AccountStatusFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton btnContinue = view.findViewById(R.id.btnStatusContinue);
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> Toast.makeText(requireContext(), "Continuing to Dashboard", Toast.LENGTH_SHORT).show());
        }
    }
}
