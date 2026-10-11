package com.chuosokonifrontend.ui.investor;

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

public class AcquireStockFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_acquire_stock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton btnConfirm = view.findViewById(R.id.btnConfirmAcquire);
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> Toast.makeText(requireContext(), "Proceeding to Acquisition Payment", Toast.LENGTH_SHORT).show());
        }
    }
}
