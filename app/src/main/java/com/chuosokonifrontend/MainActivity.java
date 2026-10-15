package com.chuosokonifrontend;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
            if (bottomNav != null) {
                NavigationUI.setupWithNavController(bottomNav, navController);

                // Role-based Bottom Navigation menu switcher & visibility listener
                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    int id = destination.getId();
                    if (id == R.id.splashFragment 
                            || id == R.id.loginFragment 
                            || id == R.id.registerFragment 
                            || id == R.id.onboardingFragment 
                            || id == R.id.referralCodeFragment 
                            || id == R.id.passwordRecoveryFragment 
                            || id == R.id.accountStatusFragment
                            || id == R.id.resetPasswordFragment
                            || id == R.id.verifyOtpFragment) {
                        bottomNav.setVisibility(View.GONE);
                    } else {
                        bottomNav.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}
