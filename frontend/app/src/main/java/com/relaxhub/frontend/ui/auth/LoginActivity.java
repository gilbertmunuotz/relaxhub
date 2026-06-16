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
import com.relaxhub.frontend.data.model.DbHealthResponse;
import com.relaxhub.frontend.data.model.HealthResponse;
import com.relaxhub.frontend.data.model.LoginRequest;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.ui.dashboard.DashboardActivity;
import com.relaxhub.frontend.util.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private RelaxhubApi api;
    private android.widget.TextView statusText;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        api = ApiClient.getApi();
        statusText = findViewById(R.id.statusText);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        MaterialButton signUpLink = findViewById(R.id.signUpLink);

        loginButton.setOnClickListener(v -> attemptLogin());
        signUpLink.setOnClickListener(v ->
                startActivity(new Intent(this, Sign_up_Activity.class)));

        checkConnectivityAndHealth();
    }

    private void checkConnectivityAndHealth() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            statusText.setText(R.string.status_no_network);
            loginButton.setEnabled(false);
            return;
        }

        statusText.setText(R.string.status_checking_connection);

        api.getHealth().enqueue(new Callback<HealthResponse>() {
            @Override
            public void onResponse(Call<HealthResponse> call, Response<HealthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statusText.setText(R.string.status_api_up);
                    checkDatabaseHealth();
                } else {
                    statusText.setText(R.string.status_api_down);
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<HealthResponse> call, Throwable t) {
                statusText.setText(R.string.status_api_down);
                loginButton.setEnabled(false);
            }
        });
    }

    private void checkDatabaseHealth() {
        api.getDatabaseHealth().enqueue(new Callback<DbHealthResponse>() {
            @Override
            public void onResponse(Call<DbHealthResponse> call, Response<DbHealthResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && "UP".equalsIgnoreCase(response.body().getDatabase())) {
                    statusText.setText(R.string.status_db_up);
                    loginButton.setEnabled(true);
                } else {
                    statusText.setText(R.string.status_db_down);
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<DbHealthResponse> call, Throwable t) {
                statusText.setText(R.string.status_db_down);
                loginButton.setEnabled(false);
            }
        });
    }

    private void attemptLogin() {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);

        api.login(new LoginRequest(email, password)).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<AuthResponse>> call,
                    Response<ApiResponse<AuthResponse>> response
            ) {
                loginButton.setEnabled(true);

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getData() != null) {
                    SessionManager.getInstance(LoginActivity.this)
                            .saveSession(response.body().getData());
                    goToDashboard();
                    return;
                }

                String message = response.body() != null && response.body().getMessage() != null
                        ? response.body().getMessage()
                        : getString(R.string.error_login_failed);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                loginButton.setEnabled(true);
                Toast.makeText(LoginActivity.this, R.string.error_login_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
