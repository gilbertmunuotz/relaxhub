package com.relaxhub.frontend.ui.receipt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.CreateReceiptRequest;
import com.relaxhub.frontend.data.model.ReceiptResponse;
import com.relaxhub.frontend.ui.FormActivity;
import com.relaxhub.frontend.util.DateTimeUtils;

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
    private TextView receiptHistoryText;

    @Override
    protected int getTitleRes() {
        return R.string.title_receipt;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_receipt;
    }

    @Override
    protected void bindViews() {
        visitDateTimeText = findViewById(R.id.visitDateTimeText);
        receiptHistoryText = findViewById(R.id.receiptHistoryText);
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
                return;
            }

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

            handleSubmit(api.createReceipt(request), this::loadReceipts);
        });

        loadReceipts();
    }

    private void updateDateTimeText() {
        visitDateTimeText.setText(getString(
                R.string.receipt_item_format,
                "Visit",
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

                List<ReceiptResponse> receipts = response.body().getData();
                if (receipts == null || receipts.isEmpty()) {
                    receiptHistoryText.setText(R.string.receipt_history_empty);
                    return;
                }

                StringBuilder builder = new StringBuilder();
                for (ReceiptResponse receipt : receipts) {
                    if (builder.length() > 0) {
                        builder.append("\n\n");
                    }
                    builder.append(getString(
                            R.string.receipt_item_format,
                            receipt.getPlaceName(),
                            receipt.getVisitDate(),
                            receipt.getVisitTime()
                    ));
                }
                receiptHistoryText.setText(builder.toString());
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReceiptResponse>>> call, Throwable t) {
                // Keep existing history text
            }
        });
    }
}
