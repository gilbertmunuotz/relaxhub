package com.relaxhub.frontend.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.util.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class FormActivity extends AppCompatActivity {

    protected RelaxhubApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(getLayoutRes());

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getTitleRes());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIconTint(getColor(R.color.white));
        toolbar.setNavigationOnClickListener(v -> finish());

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

        api = ApiClient.getApi();
        bindViews();
    }

    @StringRes
    protected abstract int getTitleRes();

    @StringRes
    protected int getSubtitleRes() {
        return 0;
    }

    protected abstract int getLayoutRes();

    protected abstract void bindViews();

    protected void bindSubtitle() {
        if (getSubtitleRes() == 0) {
            return;
        }
        android.widget.TextView subtitleView = findViewById(R.id.formSubtitle);
        if (subtitleView != null) {
            subtitleView.setText(getSubtitleRes());
        }
    }

    protected boolean ensureNetwork() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.status_no_network, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    protected <T> void handleSubmit(Call<com.relaxhub.frontend.data.model.ApiResponse<T>> call, Runnable onSuccess) {
        call.enqueue(new Callback<com.relaxhub.frontend.data.model.ApiResponse<T>>() {
            @Override
            public void onResponse(
                    Call<com.relaxhub.frontend.data.model.ApiResponse<T>> call,
                    Response<com.relaxhub.frontend.data.model.ApiResponse<T>> response
            ) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(FormActivity.this, R.string.submit_success, Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                    return;
                }
                Toast.makeText(FormActivity.this, R.string.submit_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<com.relaxhub.frontend.data.model.ApiResponse<T>> call, Throwable t) {
                Toast.makeText(FormActivity.this, R.string.submit_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
