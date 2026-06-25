package com.relaxhub.frontend.ui.contact;

import android.content.Intent;
import android.net.Uri;

import com.google.android.material.button.MaterialButton;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.ContentToolbarActivity;

public class ContactActivity extends ContentToolbarActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_contact;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_contact;
    }

    @Override
    protected void bindContent() {
        bindSubtitle(R.string.contact_intro);

        String phone = getString(R.string.support_phone);
        MaterialButton callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone))));
    }
}
