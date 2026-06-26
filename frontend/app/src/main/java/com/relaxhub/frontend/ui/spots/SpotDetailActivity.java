package com.relaxhub.frontend.ui.spots;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.SpotResponse;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.ui.ContentToolbarActivity;
import com.relaxhub.frontend.ui.dashboard.DashboardActivity;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotDetailActivity extends ContentToolbarActivity {

    public static final String EXTRA_SPOT_ID = "spot_id";

    private RelaxhubApi api;
    private SpotResponse spot;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_spot_detail;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_spot_detail;
    }

    @Override
    protected void bindContent() {
        api = ApiClient.getApi();
        long spotId = getIntent().getLongExtra(EXTRA_SPOT_ID, -1L);
        if (spotId < 0) {
            finish();
            return;
        }

        MaterialButton showOnMapButton = findViewById(R.id.showOnMapButton);
        MaterialButton callSpotButton = findViewById(R.id.callSpotButton);
        showOnMapButton.setEnabled(false);
        callSpotButton.setEnabled(false);

        showOnMapButton.setOnClickListener(v -> openOnMap());
        callSpotButton.setOnClickListener(v -> dialSpot());

        api.getSpot(spotId).enqueue(new Callback<ApiResponse<SpotResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SpotResponse>> call, Response<ApiResponse<SpotResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()
                        || response.body().getData() == null) {
                    Toast.makeText(SpotDetailActivity.this, R.string.spot_load_failed, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                spot = response.body().getData();
                bindSpot(spot);
                showOnMapButton.setEnabled(true);
                if (spot.getPhone() != null && !spot.getPhone().isEmpty()) {
                    callSpotButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SpotResponse>> call, Throwable t) {
                Toast.makeText(SpotDetailActivity.this, R.string.spot_load_failed, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void bindSpot(SpotResponse spot) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(spot.getName());
        }

        TextView typeBadge = findViewById(R.id.spotTypeBadge);
        TextView name = findViewById(R.id.spotDetailName);
        TextView address = findViewById(R.id.spotDetailAddress);
        TextView description = findViewById(R.id.spotDetailDescription);
        TextView phoneLabel = findViewById(R.id.spotDetailPhoneLabel);
        TextView phone = findViewById(R.id.spotDetailPhone);
        MaterialButton callSpotButton = findViewById(R.id.callSpotButton);

        typeBadge.setText(formatType(spot.getType()));
        name.setText(spot.getName());
        address.setText(spot.getAddress() == null ? "" : spot.getAddress());
        description.setText(spot.getDescription() == null ? "" : spot.getDescription());

        if (spot.getPhone() == null || spot.getPhone().isEmpty()) {
            phoneLabel.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            callSpotButton.setVisibility(View.GONE);
        } else {
            phone.setText(spot.getPhone());
            phone.setOnClickListener(v -> dialSpot());
        }
    }

    private void openOnMap() {
        if (spot == null) {
            return;
        }
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(DashboardActivity.EXTRA_NAV_TAB, R.id.mapsFragment);
        intent.putExtra(DashboardActivity.EXTRA_FOCUS_SPOT_LAT, spot.getLatitude());
        intent.putExtra(DashboardActivity.EXTRA_FOCUS_SPOT_LNG, spot.getLongitude());
        intent.putExtra(DashboardActivity.EXTRA_FOCUS_SPOT_NAME, spot.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void dialSpot() {
        if (spot == null || spot.getPhone() == null || spot.getPhone().isEmpty()) {
            return;
        }
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + spot.getPhone())));
    }

    private String formatType(String type) {
        if (type == null || type.isEmpty()) {
            return "";
        }
        return type.substring(0, 1).toUpperCase(Locale.getDefault())
                + type.substring(1).toLowerCase(Locale.getDefault());
    }
}
