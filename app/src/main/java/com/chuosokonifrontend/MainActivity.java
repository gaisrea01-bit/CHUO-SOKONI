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
                    if (id == R.id.splashFragment || id == R.id.loginFragment || id == R.id.registerFragment || id == R.id.onboardingFragment || id == R.id.referralCodeFragment) {
                        bottomNav.setVisibility(View.GONE);
                    } else {
                        bottomNav.setVisibility(View.VISIBLE);

                        // Switch menu dynamically based on active destination portal
                        if (id == R.id.sellerDashboardFragment || id == R.id.sellerProductListFragment || id == R.id.sellerOrdersFragment || id == R.id.sellerSalesProceedsFragment || id == R.id.sellerProfileFragment) {
                            if (bottomNav.getMenu().findItem(R.id.sellerDashboardFragment) == null) {
                                bottomNav.getMenu().clear();
                                bottomNav.inflateMenu(R.menu.menu_bottom_nav_seller);
                                NavigationUI.setupWithNavController(bottomNav, navController);
                            }
                        } else if (id == R.id.investorDashboardFragment || id == R.id.wholesaleOpportunitiesFragment || id == R.id.managedInventoryFragment || id == R.id.resellerDashboardFragment || id == R.id.investorDisputesFragment) {
                            if (bottomNav.getMenu().findItem(R.id.investorDashboardFragment) == null) {
                                bottomNav.getMenu().clear();
                                bottomNav.inflateMenu(R.menu.menu_bottom_nav_investor);
                                NavigationUI.setupWithNavController(bottomNav, navController);
                            }
                        } else if (id == R.id.agentDashboardFragment || id == R.id.agentReferralLinkFragment || id == R.id.referredUsersFragment || id == R.id.agentCommissionsFragment || id == R.id.agentSupportFragment) {
                            if (bottomNav.getMenu().findItem(R.id.agentDashboardFragment) == null) {
                                bottomNav.getMenu().clear();
                                bottomNav.inflateMenu(R.menu.menu_bottom_nav_agent);
                                NavigationUI.setupWithNavController(bottomNav, navController);
                            }
                        } else if (id == R.id.adminDashboardFragment || id == R.id.adminVerificationQueuesFragment || id == R.id.userRoleManagementFragment || id == R.id.transactionMonitoringFragment || id == R.id.reportsAnalyticsFragment) {
                            if (bottomNav.getMenu().findItem(R.id.adminDashboardFragment) == null) {
                                bottomNav.getMenu().clear();
                                bottomNav.inflateMenu(R.menu.menu_bottom_nav_admin);
                                NavigationUI.setupWithNavController(bottomNav, navController);
                            }
                        } else {
                            if (bottomNav.getMenu().findItem(R.id.customerDashboardFragment) == null) {
                                bottomNav.getMenu().clear();
                                bottomNav.inflateMenu(R.menu.menu_bottom_nav_customer);
                                NavigationUI.setupWithNavController(bottomNav, navController);
                            }
                        }
                    }
                });
            }
        }
    }
}
