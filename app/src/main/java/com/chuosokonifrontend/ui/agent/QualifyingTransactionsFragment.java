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

public class QualifyingTransactionsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private OrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qualifying_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipeQualifyingTrans);
        RecyclerView recyclerView = view.findViewById(R.id.rvQualifyingTrans);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));
        }
    }
}
