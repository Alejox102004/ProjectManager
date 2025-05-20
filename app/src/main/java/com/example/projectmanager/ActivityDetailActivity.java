package com.example.projectmanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityDetailActivity extends AppCompatActivity {
    private TextView tvName, tvDescription, tvStartDate, tvEndDate, tvStatus;
    private DatabaseHelper dbHelper;
    private int activityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);

        tvName = findViewById(R.id.tv_activity_name);
        tvDescription = findViewById(R.id.tv_activity_description);
        tvStartDate = findViewById(R.id.tv_activity_start_date);
        tvEndDate = findViewById(R.id.tv_activity_end_date);
        tvStatus = findViewById(R.id.tv_activity_status);
        dbHelper = new DatabaseHelper(this);
        activityId = getIntent().getIntExtra("activityId", -1);

        loadActivityDetails();
    }

    private void loadActivityDetails() {
        Cursor cursor = dbHelper.getActivities(getIntent().getIntExtra("projectId", -1));
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("activity_id")) == activityId) {
                tvName.setText(cursor.getString(cursor.getColumnIndex("name")));
                tvDescription.setText(cursor.getString(cursor.getColumnIndex("description")));
                tvStartDate.setText(cursor.getString(cursor.getColumnIndex("start_date")));
                tvEndDate.setText(cursor.getString(cursor.getColumnIndex("end_date")));
                tvStatus.setText(cursor.getString(cursor.getColumnIndex("status")));
                break;
            }
        }
        cursor.close();
    }
}