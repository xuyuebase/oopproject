package com.example.oopproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin, btnGotoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGotoRegister = findViewById(R.id.btnGotoRegister);

        btnLogin.setOnClickListener(v -> {

            String inputEmail = editEmail.getText().toString().trim();
            String inputPass = editPassword.getText().toString().trim();

            String savedEmail = UserStorage.getEmail(this);
            String savedPass = UserStorage.getPassword(this);

            if (savedEmail.isEmpty()) {
                Toast.makeText(this, "No account found. Please register.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputEmail.equals(savedEmail) && inputPass.equals(savedPass)) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                // ⭐ 不跳 Profile，直接 Dashboard
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Wrong email or password!", Toast.LENGTH_SHORT).show();
            }
        });

        btnGotoRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }
}
