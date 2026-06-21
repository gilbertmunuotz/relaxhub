package com.relaxhub.frontend.ui.feedback;

import android.widget.RatingBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.model.CreateFeedbackRequest;
import com.relaxhub.frontend.ui.FormActivity;

public class UserFeedbackActivity extends FormActivity {

    private RatingBar ratingBar;
    private TextInputEditText messageInput;

    @Override
    protected int getTitleRes() {
        return R.string.title_feedback;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void bindViews() {
        ratingBar = findViewById(R.id.ratingBar);
        messageInput = findViewById(R.id.messageInput);
        MaterialButton submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            if (!ensureNetwork()) {
                return;
            }

            String message = messageInput.getText() != null ? messageInput.getText().toString().trim() : "";
            if (message.isEmpty()) {
                messageInput.setError(getString(R.string.error_required_fields));
                return;
            }

            int rating = Math.round(ratingBar.getRating());
            handleSubmit(
                    api.submitFeedback(new CreateFeedbackRequest(rating, message)),
                    this::finish
            );
        });
    }
}
