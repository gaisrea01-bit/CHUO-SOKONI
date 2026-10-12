package com.chuosokonifrontend.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.chuosokonifrontend.R;
import com.chuosokonifrontend.ui.adapters.OnboardingAdapter;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class OnboardingFragment extends Fragment {

    private RecyclerView recyclerView;
    private View dot1, dot2, dot3;
    private MaterialButton btnNext;
    private int currentPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        recyclerView = view.findViewById(R.id.recyclerViewOnboarding);
        btnNext = view.findViewById(R.id.btnOnboardingNext);

        View layoutDots = view.findViewById(R.id.layoutDots);
        if (layoutDots instanceof ViewGroup) {
            ViewGroup dotsGroup = (ViewGroup) layoutDots;
            if (dotsGroup.getChildCount() >= 3) {
                dot1 = dotsGroup.getChildAt(0);
                dot2 = dotsGroup.getChildAt(1);
                dot3 = dotsGroup.getChildAt(2);
            }
        }

        List<OnboardingAdapter.OnboardingSlide> slides = new ArrayList<>();
        slides.add(new OnboardingAdapter.OnboardingSlide(
                R.drawable.image_one,
                "Welcome to Chuo Sokoni",
                "Connect • Invest • Grow your student enterprise and campus business effortlessly."
        ));
        slides.add(new OnboardingAdapter.OnboardingSlide(
                R.drawable.image_two,
                "Student Entrepreneurship",
                "List products, fulfill orders, and receive proceeds directly from campus buyers."
        ));
        slides.add(new OnboardingAdapter.OnboardingSlide(
                R.drawable.image_three,
                "Invest & Earn Returns",
                "Fund approved wholesale stock and earn verified commercial margins safely."
        ));

        if (recyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new OnboardingAdapter(slides));

            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int pos = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        currentPosition = pos;
                        updateDots(pos);
                    }
                }
            });
        }

        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                if (currentPosition < slides.size() - 1) {
                    currentPosition++;
                    if (recyclerView != null) {
                        recyclerView.smoothScrollToPosition(currentPosition);
                    }
                } else {
                    Toast.makeText(requireContext(), "Entering Chuo Sokoni Marketplace", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_accountStatus_to_onboarding);
                }
            });
        }
    }

    private void updateDots(int activePosition) {
        if (dot1 != null) dot1.setBackgroundResource(activePosition == 0 ? R.drawable.shape_dot_active : R.drawable.shape_dot_inactive);
        if (dot2 != null) dot2.setBackgroundResource(activePosition == 1 ? R.drawable.shape_dot_active : R.drawable.shape_dot_inactive);
        if (dot3 != null) dot3.setBackgroundResource(activePosition == 2 ? R.drawable.shape_dot_active : R.drawable.shape_dot_inactive);
    }
}
