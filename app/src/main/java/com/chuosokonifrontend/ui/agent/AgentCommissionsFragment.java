package com.chuosokonifrontend.ui.agent;

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

public class AgentCommissionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agent_commissions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton btnWithdraw = view.findViewById(R.id.btnRequestWithdrawal);
        if (btnWithdraw != null) {
            btnWithdraw.setOnClickListener(v -> Toast.makeText(requireContext(), "Withdrawal request submitted successfully!", Toast.LENGTH_SHORT).show());
        }
    }
}
