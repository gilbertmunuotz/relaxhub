package com.relaxhub.frontend.ui.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.local.SessionManager;
import com.relaxhub.frontend.util.NotificationHelper;
import com.relaxhub.frontend.util.SpotMapHelper;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

public class DashboardActivity extends AppCompatActivity {

    public static final String EXTRA_NAV_TAB = "nav_tab";
    public static final String EXTRA_FOCUS_SPOT_LAT = "focus_spot_lat";
    public static final String EXTRA_FOCUS_SPOT_LNG = "focus_spot_lng";
    public static final String EXTRA_FOCUS_SPOT_NAME = "focus_spot_name";

    private NavController navController;
    private Double pendingFocusLat;
    private Double pendingFocusLng;
    private String pendingFocusName;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    showWelcomeNotification();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SessionManager sessionManager = SessionManager.getInstance(this);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (view, windowInsets) -> {
            Insets statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            view.setPadding(
                    view.getPaddingLeft(),
                    statusBars.top,
                    view.getPaddingRight(),
                    view.getPaddingBottom()
            );
            return windowInsets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigation, (view, windowInsets) -> {
            Insets navBars = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    navBars.bottom
            );
            return windowInsets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigation, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destinationId = destination.getId();
                boolean immersiveTab = destinationId == R.id.homeFragment
                        || destinationId == R.id.mapsFragment;

                toolbar.setVisibility(immersiveTab ? View.GONE : View.VISIBLE);

                if (!immersiveTab) {
                    toolbar.setTitle(destination.getLabel());
                    if (destinationId == R.id.accountFragment && sessionManager.isLoggedIn()) {
                        toolbar.setSubtitle(sessionManager.getEmail());
                    } else {
                        toolbar.setSubtitle(null);
                    }
                }

                boolean lightStatusBarIcons = destinationId == R.id.mapsFragment;
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                        .setAppearanceLightStatusBars(lightStatusBarIcons);
            });
        }

        handleNavigationIntent(getIntent());
        requestNotificationPermissionIfNeeded();
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNavigationIntent(intent);
    }

    public void consumePendingSpotFocus(SpotMapHelper spotMapHelper) {
        if (pendingFocusLat == null || pendingFocusLng == null || spotMapHelper == null) {
            return;
        }
        spotMapHelper.focusOnSpot(pendingFocusLat, pendingFocusLng, pendingFocusName);
        pendingFocusLat = null;
        pendingFocusLng = null;
        pendingFocusName = null;
    }

    public boolean hasPendingSpotFocus() {
        return pendingFocusLat != null && pendingFocusLng != null;
    }

    private void handleNavigationIntent(android.content.Intent intent) {
        if (intent == null || navController == null) {
            return;
        }

        int tabId = intent.getIntExtra(EXTRA_NAV_TAB, -1);
        if (tabId != -1) {
            navController.navigate(tabId);
        }

        if (intent.hasExtra(EXTRA_FOCUS_SPOT_LAT) && intent.hasExtra(EXTRA_FOCUS_SPOT_LNG)) {
            pendingFocusLat = intent.getDoubleExtra(EXTRA_FOCUS_SPOT_LAT, 0);
            pendingFocusLng = intent.getDoubleExtra(EXTRA_FOCUS_SPOT_LNG, 0);
            pendingFocusName = intent.getStringExtra(EXTRA_FOCUS_SPOT_NAME);
        }

        intent.removeExtra(EXTRA_NAV_TAB);
        intent.removeExtra(EXTRA_FOCUS_SPOT_LAT);
        intent.removeExtra(EXTRA_FOCUS_SPOT_LNG);
        intent.removeExtra(EXTRA_FOCUS_SPOT_NAME);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            showWelcomeNotification();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            showWelcomeNotification();
        } else {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void showWelcomeNotification() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        NotificationHelper.showWelcomeIfNeeded(this, sessionManager.getFullName());
    }
}
