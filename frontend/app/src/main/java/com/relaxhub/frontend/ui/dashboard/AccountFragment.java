package com.relaxhub.frontend.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.local.SessionManager;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.UserResponse;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private RelaxhubApi api;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = ApiClient.getApi();

        TextView accountName = view.findViewById(R.id.accountName);
        TextView accountEmail = view.findViewById(R.id.accountEmail);
        TextView accountStatus = view.findViewById(R.id.accountStatus);

        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        accountName.setText(sessionManager.getFullName());
        accountEmail.setText(sessionManager.getEmail());
        accountStatus.setText(R.string.profile_loading);

        api.getProfile().enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()
                        && response.body().getData() != null) {
                    UserResponse user = response.body().getData();
                    accountName.setText(user.getFullName());
                    accountEmail.setText(user.getEmail());
                    accountStatus.setText(R.string.profile_loaded);
                    return;
                }
                accountStatus.setText(R.string.profile_failed);
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                accountStatus.setText(R.string.profile_failed);
            }
        });
    }
}
