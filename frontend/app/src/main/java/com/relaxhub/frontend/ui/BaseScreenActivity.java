package com.relaxhub.frontend.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.relaxhub.frontend.R;

public abstract class BaseScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getScreenTitleRes());
        }

        android.widget.TextView messageView = findViewById(R.id.screenMessage);
        messageView.setText(getPlaceholderMessageRes());
    }

    @StringRes
    protected abstract int getScreenTitleRes();

    @StringRes
    protected abstract int getPlaceholderMessageRes();
}
