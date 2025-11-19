package com.example.oopproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

public class RegisterActivity extends AppCompatActivity {

    EditText editName, editEmail, editPassword, editConfirm;
    Button btnRegister;
    TextView textLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirm = findViewById(R.id.editConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        textLogin = findViewById(R.id.textLogin);

        btnRegister.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String pass = editPassword.getText().toString().trim();
            String confirm = editConfirm.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 保存用户资料
            UserStorage.saveUser(this, name, email, pass);

            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        textLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }
}
