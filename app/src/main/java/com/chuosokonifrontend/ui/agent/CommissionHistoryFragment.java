package com.chuosokonifrontend.ui.agent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.chuosokonifrontend.R;
import com.chuosokonifrontend.ui.adapters.OrderAdapter;
import java.util.ArrayList;

public class CommissionHistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_commission_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwipeRefreshLayout swipe = view.findViewById(R.id.swipeCommHistory);
        RecyclerView recyclerView = view.findViewById(R.id.rvCommHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new OrderAdapter(new ArrayList<>()));

        if (swipe != null) {
            swipe.setOnRefreshListener(() -> swipe.setRefreshing(false));
        }
    }
}
