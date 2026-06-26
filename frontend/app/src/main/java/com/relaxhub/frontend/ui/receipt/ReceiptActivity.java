package com.relaxhub.frontend.ui.receipt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.CreateReceiptRequest;
import com.relaxhub.frontend.data.model.ReceiptResponse;
import com.relaxhub.frontend.ui.FormActivity;
import com.relaxhub.frontend.util.DateTimeUtils;
import com.relaxhub.frontend.util.NotificationHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptActivity extends FormActivity {

    private static final DateTimeFormatter API_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter API_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LocalDate visitDate;
    private LocalTime visitTime;
    private TextView visitDateTimeText;
    private LinearLayout receiptHistoryContainer;
    private TextView receiptHistoryEmpty;
    private TextInputLayout placeNameInputLayout;

    @Override
    protected int getTitleRes() {
        return R.string.title_receipt;
    }

    @Override
    protected int getSubtitleRes() {
        return R.string.receipt_form_subtitle;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_receipt;
    }

    @Override
    protected void bindViews() {
        bindSubtitle();
        visitDateTimeText = findViewById(R.id.visitDateTimeText);
        receiptHistoryContainer = findViewById(R.id.receiptHistoryContainer);
        receiptHistoryEmpty = findViewById(R.id.receiptHistoryEmpty);
        placeNameInputLayout = findViewById(R.id.placeNameInputLayout);
        TextInputEditText placeNameInput = findViewById(R.id.placeNameInput);
        TextInputEditText amountInput = findViewById(R.id.amountInput);
        TextInputEditText notesInput = findViewById(R.id.notesInput);
        MaterialButton pickDateButton = findViewById(R.id.pickDateButton);
        MaterialButton pickTimeButton = findViewById(R.id.pickTimeButton);
        MaterialButton saveButton = findViewById(R.id.saveButton);

        visitDate = LocalDate.now();
        visitTime = LocalTime.now();
        updateDateTimeText();

        pickDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        visitDate = LocalDate.of(year, month + 1, dayOfMonth);
                        updateDateTimeText();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        pickTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        visitTime = LocalTime.of(hourOfDay, minute);
                        updateDateTimeText();
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            ).show();
        });

        saveButton.setOnClickListener(v -> {
            if (!ensureNetwork()) {
                return;
            }

            String placeName = placeNameInput.getText() != null
                    ? placeNameInput.getText().toString().trim() : "";
            if (placeName.isEmpty()) {
                placeNameInputLayout.setError(getString(R.string.error_required_fields));
                return;
            }
            placeNameInputLayout.setError(null);

            Double amount = null;
            String amountText = amountInput.getText() != null ? amountInput.getText().toString().trim() : "";
            if (!amountText.isEmpty()) {
                amount = Double.parseDouble(amountText);
            }

            String notes = notesInput.getText() != null ? notesInput.getText().toString().trim() : "";

            CreateReceiptRequest request = new CreateReceiptRequest(
                    placeName,
                    visitDate.format(API_DATE),
                    visitTime.format(API_TIME),
                    amount,
                    notes.isEmpty() ? null : notes
            );

            api.createReceipt(request).enqueue(new Callback<ApiResponse<ReceiptResponse>>() {
                @Override
                public void onResponse(
                        Call<ApiResponse<ReceiptResponse>> call,
                        Response<ApiResponse<ReceiptResponse>> response
                ) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        NotificationHelper.showReceiptSaved(ReceiptActivity.this, placeName);
                        placeNameInput.setText("");
                        amountInput.setText("");
                        notesInput.setText("");
                        loadReceipts();
                        return;
                    }
                    android.widget.Toast.makeText(ReceiptActivity.this, R.string.submit_failed, android.widget.Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ApiResponse<ReceiptResponse>> call, Throwable t) {
                    android.widget.Toast.makeText(ReceiptActivity.this, R.string.submit_failed, android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });

        loadReceipts();
    }

    private void updateDateTimeText() {
        visitDateTimeText.setText(getString(
                R.string.receipt_selected_datetime,
                DateTimeUtils.formatDate(visitDate),
                DateTimeUtils.formatTime(visitTime)
        ));
    }

    private void loadReceipts() {
        if (!ensureNetwork()) {
            return;
        }

        api.getReceipts().enqueue(new Callback<ApiResponse<List<ReceiptResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<ReceiptResponse>>> call,
                    Response<ApiResponse<List<ReceiptResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    return;
                }
                renderReceiptHistory(response.body().getData());
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReceiptResponse>>> call, Throwable t) {
                // Keep existing history
            }
        });
    }

    private void renderReceiptHistory(List<ReceiptResponse> receipts) {
        receiptHistoryContainer.removeAllViews();

        if (receipts == null || receipts.isEmpty()) {
            receiptHistoryEmpty.setVisibility(View.VISIBLE);
            receiptHistoryContainer.setVisibility(View.GONE);
            return;
        }

        receiptHistoryEmpty.setVisibility(View.GONE);
        receiptHistoryContainer.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (ReceiptResponse receipt : receipts) {
            View item = inflater.inflate(R.layout.item_recent_visit, receiptHistoryContainer, false);
            TextView placeName = item.findViewById(R.id.visitPlaceName);
            TextView dateTime = item.findViewById(R.id.visitDateTime);
            placeName.setText(receipt.getPlaceName());
            dateTime.setText(formatVisitDateTime(receipt));
            receiptHistoryContainer.addView(item);
        }
    }

    private String formatVisitDateTime(ReceiptResponse receipt) {
        try {
            LocalDate date = LocalDate.parse(receipt.getVisitDate());
            LocalTime time = LocalTime.parse(receipt.getVisitTime());
            return DateTimeUtils.formatDate(date) + " · " + DateTimeUtils.formatTime(time);
        } catch (Exception exception) {
            return receipt.getVisitDate() + " · " + receipt.getVisitTime();
        }
    }
}
