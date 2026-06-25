package com.relaxhub.frontend.ui.privacy;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.ContentToolbarActivity;

public class PrivacyPolicyActivity extends ContentToolbarActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_privacy;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_privacy;
    }

    @Override
    protected void bindContent() {
        bindSubtitle(R.string.privacy_subtitle);

        String[] titles = getResources().getStringArray(R.array.privacy_section_titles);
        String[] bodies = getResources().getStringArray(R.array.privacy_section_bodies);
        LinearLayout container = findViewById(R.id.privacySectionsContainer);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < titles.length; i++) {
            if (i > 0) {
                View divider = new View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelSize(R.dimen.divider_height)
                ));
                divider.setBackgroundColor(getColor(R.color.divider));
                container.addView(divider);
            }

            View item = inflater.inflate(R.layout.item_privacy_section, container, false);
            TextView title = item.findViewById(R.id.privacySectionTitle);
            TextView body = item.findViewById(R.id.privacySectionBody);
            title.setText(titles[i]);
            body.setText(i < bodies.length ? bodies[i] : "");
            container.addView(item);
        }
    }
}
