package com.relaxhub.frontend.ui.help;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.ContentToolbarActivity;

public class HelpActivity extends ContentToolbarActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_help;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_help;
    }

    @Override
    protected void bindContent() {
        bindSubtitle(R.string.help_subtitle);

        String[] questions = getResources().getStringArray(R.array.help_questions);
        String[] answers = getResources().getStringArray(R.array.help_answers);
        LinearLayout faqContainer = findViewById(R.id.faqContainer);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < questions.length; i++) {
            if (i > 0) {
                View divider = new View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelSize(R.dimen.divider_height)
                ));
                divider.setBackgroundColor(getColor(R.color.divider));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) divider.getLayoutParams();
                params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.settings_divider_inset));
                divider.setLayoutParams(params);
                faqContainer.addView(divider);
            }

            View item = inflater.inflate(R.layout.item_faq, faqContainer, false);
            TextView question = item.findViewById(R.id.faqQuestion);
            TextView answer = item.findViewById(R.id.faqAnswer);
            question.setText(questions[i]);
            answer.setText(i < answers.length ? answers[i] : "");
            faqContainer.addView(item);
        }
    }
}
