package com.relaxhub.frontend.ui.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.util.SpotMapHelper;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SpotMapHelper spotMapHelper;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    onLocationReady();
                } else {
                    Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_SHORT).show();
                    spotMapHelper.loadSpots();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_map);

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        ImageButton backButton = findViewById(R.id.mapBackButton);
        ChipGroup filterGroup = findViewById(R.id.spotFilterGroup);
        backButton.setOnClickListener(v -> finish());

        applyMapInsets(backButton, filterGroup);

        RelaxhubApi api = ApiClient.getApi();
        spotMapHelper = new SpotMapHelper(this, api);
        setupFilters();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void applyMapInsets(View backButton, ChipGroup filterGroup) {
        ViewCompat.setOnApplyWindowInsetsListener(backButton, (view, windowInsets) -> {
            Insets statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.topMargin = statusBars.top + getResources().getDimensionPixelSize(R.dimen.spacing_sm);
            view.setLayoutParams(params);
            return windowInsets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(filterGroup, (view, windowInsets) -> {
            Insets statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.topMargin = statusBars.top + getResources().getDimensionPixelSize(R.dimen.spacing_sm);
            view.setLayoutParams(params);
            return windowInsets;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        spotMapHelper.bindMap(map);
        requestLocationAccess();
    }

    private void setupFilters() {
        ChipGroup filterGroup = findViewById(R.id.spotFilterGroup);
        Chip allChip = findViewById(R.id.filterAllChip);

        filterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.filterRestaurantChip) {
                spotMapHelper.setTypeFilter("RESTAURANT");
            } else if (checkedId == R.id.filterRelaxationChip) {
                spotMapHelper.setTypeFilter("RELAXATION");
            } else {
                spotMapHelper.setTypeFilter(null);
            }
        });

        allChip.setChecked(true);
    }

    private void requestLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            onLocationReady();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void onLocationReady() {
        spotMapHelper.enableMyLocation();
        spotMapHelper.refreshWithUserLocation();
    }
}
