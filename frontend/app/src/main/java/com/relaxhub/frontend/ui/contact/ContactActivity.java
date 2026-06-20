package com.relaxhub.frontend.ui.contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.relaxhub.frontend.R;

public class ContactActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> callPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    dialSupport();
                }
            });

    private final ActivityResultLauncher<String> smsPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    smsSupport();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_contact);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        MaterialButton callButton = findViewById(R.id.callButton);
        MaterialButton smsButton = findViewById(R.id.smsButton);
        MaterialButton emailButton = findViewById(R.id.emailButton);

        callButton.setOnClickListener(v -> requestCallPermission());
        smsButton.setOnClickListener(v -> requestSmsPermission());
        emailButton.setOnClickListener(v -> emailSupport());
    }

    private void requestCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            dialSupport();
        } else {
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            smsSupport();
        } else {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    private void dialSupport() {
        String phone = getString(R.string.support_phone);
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
    }

    private void smsSupport() {
        String phone = getString(R.string.support_phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
        intent.putExtra("sms_body", "Hello Relaxhub support, ");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.submit_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void emailSupport() {
        String email = getString(R.string.support_email);
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Relaxhub support");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.submit_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
