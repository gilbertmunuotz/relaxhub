package com.relaxhub.frontend.ui.spots;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.SpotResponse;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.ui.ContentToolbarActivity;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotsListActivity extends ContentToolbarActivity {

    private RelaxhubApi api;
    private LinearLayout spotsListContainer;
    private TextView spotsEmptyText;
    private MaterialCardView spotsListCard;
    private String typeFilter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_spots_list;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_spots;
    }

    @Override
    protected void bindContent() {
        bindSubtitle(R.string.spots_list_subtitle);
        api = ApiClient.getApi();
        spotsListContainer = findViewById(R.id.spotsListContainer);
        spotsEmptyText = findViewById(R.id.spotsEmptyText);
        spotsListCard = findViewById(R.id.spotsListCard);

        ChipGroup filterGroup = findViewById(R.id.spotFilterGroup);
        Chip allChip = findViewById(R.id.filterAllChip);
        filterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.filterRestaurantChip) {
                typeFilter = "RESTAURANT";
            } else if (checkedId == R.id.filterRelaxationChip) {
                typeFilter = "RELAXATION";
            } else {
                typeFilter = null;
            }
            loadSpots();
        });
        allChip.setChecked(true);

        loadSpots();
    }

    private void loadSpots() {
        spotsListContainer.removeAllViews();
        spotsEmptyText.setVisibility(View.GONE);
        spotsListCard.setVisibility(View.VISIBLE);

        api.getSpots(typeFilter).enqueue(new Callback<ApiResponse<List<SpotResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<SpotResponse>>> call,
                    Response<ApiResponse<List<SpotResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    Toast.makeText(SpotsListActivity.this, R.string.spots_load_failed, Toast.LENGTH_SHORT).show();
                    showEmpty();
                    return;
                }
                renderSpots(response.body().getData());
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SpotResponse>>> call, Throwable t) {
                Toast.makeText(SpotsListActivity.this, R.string.spots_load_failed, Toast.LENGTH_SHORT).show();
                showEmpty();
            }
        });
    }

    private void renderSpots(List<SpotResponse> spots) {
        spotsListContainer.removeAllViews();

        if (spots == null || spots.isEmpty()) {
            showEmpty();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < spots.size(); i++) {
            if (i > 0) {
                View divider = new View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelSize(R.dimen.divider_height)
                ));
                divider.setBackgroundColor(getColor(R.color.divider));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) divider.getLayoutParams();
                params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.settings_divider_inset));
                divider.setLayoutParams(params);
                spotsListContainer.addView(divider);
            }

            SpotResponse spot = spots.get(i);
            View item = inflater.inflate(R.layout.item_spot, spotsListContainer, false);
            bindSpotItem(item, spot);
            item.setOnClickListener(v -> openSpotDetail(spot.getId()));
            spotsListContainer.addView(item);
        }
    }

    private void bindSpotItem(View item, SpotResponse spot) {
        ImageView typeIcon = item.findViewById(R.id.spotTypeIcon);
        TextView name = item.findViewById(R.id.spotName);
        TextView meta = item.findViewById(R.id.spotMeta);
        TextView description = item.findViewById(R.id.spotDescriptionPreview);

        boolean restaurant = "RESTAURANT".equals(spot.getType());
        typeIcon.setImageResource(restaurant ? R.drawable.ic_action_map : R.drawable.ic_action_browse);
        name.setText(spot.getName());
        meta.setText(buildMeta(spot));
        description.setText(spot.getDescription() == null ? "" : spot.getDescription());
    }

    private String buildMeta(SpotResponse spot) {
        StringBuilder builder = new StringBuilder();
        if (spot.getType() != null) {
            builder.append(formatType(spot.getType()));
        }
        if (spot.getAddress() != null && !spot.getAddress().isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" · ");
            }
            builder.append(spot.getAddress());
        }
        return builder.toString();
    }

    private String formatType(String type) {
        return type.substring(0, 1).toUpperCase(Locale.getDefault())
                + type.substring(1).toLowerCase(Locale.getDefault());
    }

    private void showEmpty() {
        spotsListCard.setVisibility(View.GONE);
        spotsEmptyText.setVisibility(View.VISIBLE);
    }

    private void openSpotDetail(long spotId) {
        Intent intent = new Intent(this, SpotDetailActivity.class);
        intent.putExtra(SpotDetailActivity.EXTRA_SPOT_ID, spotId);
        startActivity(intent);
    }
}
