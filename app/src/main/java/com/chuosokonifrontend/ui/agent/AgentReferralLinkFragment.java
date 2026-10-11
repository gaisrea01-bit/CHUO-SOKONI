package com.chuosokonifrontend.ui.agent;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.chuosokonifrontend.R;
import com.chuosokonifrontend.data.api.AgentAPIService;
import com.chuosokonifrontend.data.network.ApiClient;
import com.chuosokonifrontend.data.network.TokenManager;
import com.google.android.material.button.MaterialButton;

public class AgentReferralLinkFragment extends Fragment {

    private AgentAPIService agentAPIService;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agent_referral_link, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenManager = new TokenManager(requireContext());
        agentAPIService = ApiClient.getInstance(requireContext()).getAgentService();

        MaterialButton btnCopy = view.findViewById(R.id.btnCopyLink);
        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Chuo Sokoni Referral Link", "https://chuosokoni.ac.tz/ref/CS-AGENT-9482");
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(requireContext(), "Referral link copied to clipboard!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
