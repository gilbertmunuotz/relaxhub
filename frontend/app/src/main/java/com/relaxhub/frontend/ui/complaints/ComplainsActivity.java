package com.relaxhub.frontend.ui.complaints;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.model.CreateComplaintRequest;
import com.relaxhub.frontend.ui.FormActivity;

public class ComplainsActivity extends FormActivity {

    private TextInputEditText subjectInput;
    private TextInputEditText descriptionInput;

    @Override
    protected int getTitleRes() {
        return R.string.title_complaints;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_complaints;
    }

    @Override
    protected void bindViews() {
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

            if (subject.isEmpty() || description.isEmpty()) {
                return;
            }

            handleSubmit(
                    api.submitComplaint(new CreateComplaintRequest(subject, description)),
                    this::finish
            );
        });
    }
}
