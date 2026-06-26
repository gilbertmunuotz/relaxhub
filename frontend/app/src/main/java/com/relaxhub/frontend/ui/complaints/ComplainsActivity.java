package com.relaxhub.frontend.ui.complaints;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.model.CreateComplaintRequest;
import com.relaxhub.frontend.ui.FormActivity;

public class ComplainsActivity extends FormActivity {

    private TextInputEditText subjectInput;
    private TextInputEditText descriptionInput;
    private TextInputLayout subjectInputLayout;
    private TextInputLayout descriptionInputLayout;

    @Override
    protected int getTitleRes() {
        return R.string.title_complaints;
    }

    @Override
    protected int getSubtitleRes() {
        return R.string.complaints_form_subtitle;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_complaints;
    }

    @Override
    protected void bindViews() {
        bindSubtitle();
        subjectInputLayout = findViewById(R.id.subjectInputLayout);
        descriptionInputLayout = findViewById(R.id.descriptionInputLayout);
        subjectInput = findViewById(R.id.subjectInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        MaterialButton submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            if (!ensureNetwork()) {
                return;
            }

            String subject = subjectInput.getText() != null ? subjectInput.getText().toString().trim() : "";
            String description = descriptionInput.getText() != null
                    ? descriptionInput.getText().toString().trim() : "";

            boolean valid = true;
            if (subject.isEmpty()) {
                subjectInputLayout.setError(getString(R.string.error_required_fields));
                valid = false;
            } else {
                subjectInputLayout.setError(null);
            }

            if (description.isEmpty()) {
                descriptionInputLayout.setError(getString(R.string.error_required_fields));
                valid = false;
            } else {
                descriptionInputLayout.setError(null);
            }

            if (!valid) {
                return;
            }

            handleSubmit(
                    api.submitComplaint(new CreateComplaintRequest(subject, description)),
                    this::finish
            );
        });
    }
}
