package com.example.projectmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PasswordRecoveryActivity extends AppCompatActivity {
    private EditText etUsername;
    private Button btnRecover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        etUsername = findViewById(R.id.et_username);
        btnRecover = findViewById(R.id.btn_recover);

        btnRecover.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa el nombre de usuario", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Instrucciones enviadas a " + username, Toast.LENGTH_SHORT).show();
            finish();
        });

        // Optional: Add a back button to return to the login screen
        findViewById(R.id.tv_back_to_login).setOnClickListener(v -> {
            startActivity(new Intent(PasswordRecoveryActivity.this, LoginActivity.class));

        });
    }
}