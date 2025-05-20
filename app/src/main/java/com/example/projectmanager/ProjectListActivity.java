package com.example.projectmanager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectListActivity extends AppCompatActivity {
    private RecyclerView rvProjects;
    private Button btnAddProject;
    private DatabaseHelper dbHelper;
    private ProjectAdapter projectAdapter;
    private List<Project> projectList;
    private int userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        rvProjects = findViewById(R.id.rv_projects);
        btnAddProject = findViewById(R.id.btn_add_project);
        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        projectList = new ArrayList<>();
        projectAdapter = new ProjectAdapter(projectList, project -> {
            Intent intent = new Intent(ProjectListActivity.this, ProjectDetailActivity.class);
            intent.putExtra("projectId", project.getId());
            startActivity(intent);
        }, project -> showEditProjectDialog(project), project -> deleteProject(project.getId()));

        rvProjects.setLayoutManager(new LinearLayoutManager(this));
        rvProjects.setAdapter(projectAdapter);

        loadProjects();

        btnAddProject.setOnClickListener(v -> showAddProjectDialog());
    }

    private void loadProjects() {
        projectList.clear();
        Cursor cursor = dbHelper.getProjects(userId);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("project_id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
            @SuppressLint("Range") String startDate = cursor.getString(cursor.getColumnIndex("start_date"));
            @SuppressLint("Range") String endDate = cursor.getString(cursor.getColumnIndex("end_date"));
            projectList.add(new Project(id, name, description, startDate, endDate, userId));
        }
        cursor.close();
        projectAdapter.notifyDataSetChanged();

        // Mostrar u ocultar estado vacío
        ConstraintLayout emptyStateContainer = findViewById(R.id.empty_state_container);
        TextView recentProjectsTitle = findViewById(R.id.tv_recent_projects);
        RecyclerView recyclerView = findViewById(R.id.rv_projects);

        if (projectList.isEmpty()) {
            emptyStateContainer.setVisibility(View.VISIBLE);
            recentProjectsTitle.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateContainer.setVisibility(View.GONE);
            recentProjectsTitle.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showAddProjectDialog() {
        // Usar MaterialAlertDialogBuilder en lugar de AlertDialog.Builder para estilo consistente
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        // Inflar la vista personalizada (nuestro layout modernizado)
        View view = getLayoutInflater().inflate(R.layout.dialog_add_project, null);

        // Configurar el diálogo sin título (el título ya está en el layout)
        builder.setView(view);

        // Referencias a los componentes del layout
        TextInputEditText etName = view.findViewById(R.id.et_project_name);
        TextInputEditText etDescription = view.findViewById(R.id.et_project_description);
        TextInputEditText etStartDate = view.findViewById(R.id.et_project_start_date);
        TextInputEditText etEndDate = view.findViewById(R.id.et_project_end_date);

        // Referencia al botón de guardar para usarlo en lugar del botón del diálogo
        MaterialButton btnSave = view.findViewById(R.id.btn_save_project);

        // Calendario para los selectores de fecha
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Configurar DatePicker con Material Design
        etStartDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha de inicio")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);
                etStartDate.setText(sdf.format(calendar.getTime()));
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER_START");
        });

        etEndDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha de fin")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);
                etEndDate.setText(sdf.format(calendar.getTime()));
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER_END");
        });

        // Crear el diálogo sin botones (usaremos el botón del layout)
        AlertDialog dialog = builder.create();

        // Configurar el botón de guardar
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();

            // Validar datos
            if (name.isEmpty()) {
                etName.setError("Campo requerido");
                return;
            }

            if (description.isEmpty()) {
                etDescription.setError("Campo requerido");
                return;
            }

            if (startDate.isEmpty()) {
                etStartDate.setError("Campo requerido");
                return;
            }

            if (endDate.isEmpty()) {
                etEndDate.setError("Campo requerido");
                return;
            }

            // Validar que la fecha de inicio sea anterior a la fecha de fin
            try {
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);

                if (start != null && end != null && start.after(end)) {
                    etEndDate.setError("La fecha de fin debe ser posterior a la fecha de inicio");
                    return;
                }
            } catch (ParseException e) {
                Log.e("AddProject", "Error al parsear fechas", e);
            }

            if (dbHelper.addProject(name, description, startDate, endDate, userId)) {
                loadProjects();
                dialog.dismiss();

                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Proyecto creado correctamente",
                        Snackbar.LENGTH_SHORT
                ).show();
            } else {
                // Mostrar mensaje de error
                Snackbar.make(
                                findViewById(android.R.id.content),
                                "Error al crear el proyecto",
                                Snackbar.LENGTH_SHORT
                        ).setBackgroundTint(getResources().getColor(R.color.error_color, null))
                        .show();
            }
        });

        // Mostrar el diálogo
        dialog.show();
    }
    private void showEditProjectDialog(Project project) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_project, null);
        builder.setView(view);

        TextInputEditText etName = view.findViewById(R.id.et_project_name);
        TextInputEditText etDescription = view.findViewById(R.id.et_project_description);
        TextInputEditText etStartDate = view.findViewById(R.id.et_project_start_date);
        TextInputEditText etEndDate = view.findViewById(R.id.et_project_end_date);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_project);

        // Prellenar campos con datos del proyecto existente
        etName.setText(project.getName());
        etDescription.setText(project.getDescription());
        etStartDate.setText(project.getStartDate());
        etEndDate.setText(project.getEndDate());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // DatePickers para fechas
        etStartDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha de inicio")
                    .setSelection(getDateInMillis(project.getStartDate()))
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);
                etStartDate.setText(sdf.format(calendar.getTime()));
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER_START_EDIT");
        });

        etEndDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha de fin")
                    .setSelection(getDateInMillis(project.getEndDate()))
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);
                etEndDate.setText(sdf.format(calendar.getTime()));
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER_END_EDIT");
        });

        AlertDialog dialog = builder.create();

        btnSave.setText("Actualizar");
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Campo requerido");
                return;
            }

            if (description.isEmpty()) {
                etDescription.setError("Campo requerido");
                return;
            }

            if (startDate.isEmpty()) {
                etStartDate.setError("Campo requerido");
                return;
            }

            if (endDate.isEmpty()) {
                etEndDate.setError("Campo requerido");
                return;
            }

            try {
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);

                if (start != null && end != null && start.after(end)) {
                    etEndDate.setError("La fecha de fin debe ser posterior a la de inicio");
                    return;
                }
            } catch (ParseException e) {
                Log.e("EditProject", "Error al parsear fechas", e);
            }

            if (dbHelper.updateProject(project.getId(), name, description, startDate, endDate)) {
                loadProjects();
                dialog.dismiss();

                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Proyecto actualizado correctamente",
                        Snackbar.LENGTH_SHORT
                ).show();
            } else {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Error al actualizar el proyecto",
                        Snackbar.LENGTH_SHORT
                ).setBackgroundTint(getResources().getColor(R.color.error_color, null)).show();
            }
        });

        dialog.show();
    }

    private long getDateInMillis(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date parsedDate = sdf.parse(date);
            return parsedDate != null ? parsedDate.getTime() : 0;
        } catch (ParseException e) {
            Log.e("ProjectListActivity", "Error al parsear fecha", e);
            return 0;
        }
    }

    private void deleteProject(int projectId) {
        if (dbHelper.deleteProject(projectId)) {
            loadProjects();
        }
    }

}