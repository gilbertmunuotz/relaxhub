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
import com.relaxhub.frontend.data.local.SessionManager;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.AuthResponse;
import com.relaxhub.frontend.data.model.RegisterRequest;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.ui.dashboard.DashboardActivity;
import com.relaxhub.frontend.util.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sign_up_Activity extends AppCompatActivity {

    private RelaxhubApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        api = ApiClient.getApi();

        TextInputEditText fullNameInput = findViewById(R.id.fullNameInput);
        TextInputEditText emailInput = findViewById(R.id.emailInput);
        TextInputEditText phoneInput = findViewById(R.id.phoneInput);
        TextInputEditText passwordInput = findViewById(R.id.passwordInput);
        TextInputEditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        MaterialButton registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.status_no_network, Toast.LENGTH_SHORT).show();
                return;
            }

            String fullName = fullNameInput.getText() != null
                    ? fullNameInput.getText().toString().trim() : "";
            String email = emailInput.getText() != null
                    ? emailInput.getText().toString().trim() : "";
            String phone = phoneInput.getText() != null
                    ? phoneInput.getText().toString().trim() : "";
            String password = passwordInput.getText() != null
                    ? passwordInput.getText().toString() : "";
            String confirmPassword = confirmPasswordInput.getText() != null
                    ? confirmPasswordInput.getText().toString() : "";

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, R.string.error_password_mismatch, Toast.LENGTH_SHORT).show();
                return;
            }

            registerButton.setEnabled(false);

            RegisterRequest request = new RegisterRequest(fullName, email, password, phone);
            api.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
                @Override
                public void onResponse(
                        Call<ApiResponse<AuthResponse>> call,
                        Response<ApiResponse<AuthResponse>> response
                ) {
                    registerButton.setEnabled(true);

                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().isSuccess()
                            && response.body().getData() != null) {
                        SessionManager.getInstance(Sign_up_Activity.this)
                                .saveSession(response.body().getData());
                        goToDashboard();
                        return;
                    }

                    String message = response.body() != null && response.body().getMessage() != null
                            ? response.body().getMessage()
                            : getString(R.string.error_register_failed);
                    Toast.makeText(Sign_up_Activity.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                    registerButton.setEnabled(true);
                    Toast.makeText(Sign_up_Activity.this, R.string.error_register_failed, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
