package com.relaxhub.frontend.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.ResetPasswordRequest;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.util.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reset_password_Activity extends AppCompatActivity {

    private RelaxhubApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        api = ApiClient.getApi();

        TextInputEditText emailInput = findViewById(R.id.emailInput);
        TextInputEditText newPasswordInput = findViewById(R.id.newPasswordInput);
        MaterialButton resetButton = findViewById(R.id.resetButton);

        resetButton.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.status_no_network, Toast.LENGTH_SHORT).show();
                return;
            }

            String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            String newPassword = newPasswordInput.getText() != null ? newPasswordInput.getText().toString() : "";

            if (email.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            resetButton.setEnabled(false);
            api.resetPassword(new ResetPasswordRequest(email, newPassword))
                    .enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            resetButton.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(Reset_password_Activity.this,
                                        R.string.reset_password_success, Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                            Toast.makeText(Reset_password_Activity.this,
                                    R.string.reset_password_failed, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            resetButton.setEnabled(true);
                            Toast.makeText(Reset_password_Activity.this,
                                    R.string.reset_password_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
