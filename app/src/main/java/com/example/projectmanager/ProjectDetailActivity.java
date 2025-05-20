package com.example.projectmanager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProjectDetailActivity extends AppCompatActivity {
    private TextView tvProjectName, tvProgress;
    private RecyclerView rvActivities;
    private MaterialButton btnAddActivity;
    private DatabaseHelper dbHelper;
    private ActivityAdapter activityAdapter;
    private List<Activity> activityList; // asegurarse de inicializarla
    private int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        tvProjectName = findViewById(R.id.tv_project_name);
        tvProgress = findViewById(R.id.tv_progress);
        rvActivities = findViewById(R.id.rv_activities);
        btnAddActivity = findViewById(R.id.btn_add_activity);
        dbHelper = new DatabaseHelper(this);
        projectId = getIntent().getIntExtra("projectId", -1);

        // ✅ INICIALIZAR LA LISTA
        activityList = new ArrayList<>();

        activityAdapter = new ActivityAdapter(activityList,
                activity -> showActivityDetailModal(activity),
                activity -> showEditActivityDialog(activity),
                activity -> deleteActivity(activity.getId())
        );
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        rvActivities.setAdapter(activityAdapter);

        loadProjectDetails();
        loadActivities();

        btnAddActivity.setOnClickListener(v -> showAddActivityDialog());
    }

    @SuppressLint("Range")
    private void loadProjectDetails() {
        Cursor cursor = dbHelper.getProjects(-1);
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("project_id")) == projectId) {
                tvProjectName.setText(cursor.getString(cursor.getColumnIndex("name")));
                break;
            }
        }
        cursor.close();
        float progress = dbHelper.getProjectProgress(projectId);
        tvProgress.setText(String.format(Locale.getDefault(), "Progreso: %.2f%%", progress));
    }

    private void loadActivities() {
        activityList.clear();
        Cursor cursor = dbHelper.getActivities(projectId);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("activity_id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
            @SuppressLint("Range") String startDate = cursor.getString(cursor.getColumnIndex("start_date"));
            @SuppressLint("Range") String endDate = cursor.getString(cursor.getColumnIndex("end_date"));
            @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
            activityList.add(new Activity(id, name, description, startDate, endDate, status, projectId));
        }
        cursor.close();
        activityAdapter.notifyDataSetChanged();
        loadProjectDetails();
    }

    private void showAddActivityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_activity, null);
        builder.setView(view);

        TextInputEditText etName = view.findViewById(R.id.et_activity_name);
        TextInputEditText etDescription = view.findViewById(R.id.et_activity_description);
        TextInputEditText etStartDate = view.findViewById(R.id.et_activity_start_date);
        TextInputEditText etEndDate = view.findViewById(R.id.et_activity_end_date);
        AutoCompleteTextView spStatus = view.findViewById(R.id.dropdown_activity_status);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_activity);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"Planificado", "En ejecución", "Realizado"});
        spStatus.setAdapter(statusAdapter);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        etStartDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                etStartDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etEndDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view12, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                etEndDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();
            String status = spStatus.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) return;

            if (dbHelper.addActivity(name, description, startDate, endDate, status, projectId)) {
                loadActivities();
                dialog.dismiss();
            }
        });
    }

    private void showEditActivityDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_activity, null);
        builder.setView(view);

        TextInputEditText etName = view.findViewById(R.id.et_activity_name);
        TextInputEditText etDescription = view.findViewById(R.id.et_activity_description);
        TextInputEditText etStartDate = view.findViewById(R.id.et_activity_start_date);
        TextInputEditText etEndDate = view.findViewById(R.id.et_activity_end_date);
        AutoCompleteTextView spStatus = view.findViewById(R.id.dropdown_activity_status);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_activity);

        etName.setText(activity.getName());
        etDescription.setText(activity.getDescription());
        etStartDate.setText(activity.getStartDate());
        etEndDate.setText(activity.getEndDate());

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"Planificado", "En ejecución", "Realizado"});
        spStatus.setAdapter(statusAdapter);
        spStatus.setText(activity.getStatus(), false);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        etStartDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                etStartDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etEndDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view12, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                etEndDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setText("Guardar Cambios");
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();
            String status = spStatus.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) return;

            if (dbHelper.updateActivity(activity.getId(), name, description, startDate, endDate, status)) {
                loadActivities();
                dialog.dismiss();
            }
        });
    }

    private void deleteActivity(int activityId) {
        if (dbHelper.deleteActivity(activityId)) {
            loadActivities();
        }
    }

    private void showActivityDetailModal(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_activity_detail, null);
        builder.setView(view);

        TextView tvName = view.findViewById(R.id.tv_activity_name);
        TextView tvDescription = view.findViewById(R.id.tv_activity_description);
        TextView tvStart = view.findViewById(R.id.tv_activity_start_date);
        TextView tvEnd = view.findViewById(R.id.tv_activity_end_date);
        TextView tvStatus = view.findViewById(R.id.tv_activity_status);

        tvName.setText(activity.getName());
        tvDescription.setText(activity.getDescription());
        tvStart.setText("Inicio: " + activity.getStartDate());
        tvEnd.setText("Fin: " + activity.getEndDate());
        tvStatus.setText("Estado: " + activity.getStatus());

        switch (activity.getStatus()) {
            case "Realizado":
                tvStatus.setTextColor(getResources().getColor(R.color.success_green, null));
                break;
            case "En ejecución":
                tvStatus.setTextColor(getResources().getColor(R.color.primary_color, null));
                break;
            case "Planificado":
                tvStatus.setTextColor(getResources().getColor(R.color.warning_yellow, null));
                break;
        }

        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }
}
