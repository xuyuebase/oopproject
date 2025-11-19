package com.example.oopproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editAge, editHeight, editWeight, editGender, editActivity, editBio;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        editGender = findViewById(R.id.editGender);
        editActivity = findViewById(R.id.editActivity);
        editBio = findViewById(R.id.editBio);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            UserProfile.save(
                    this,
                    editName.getText().toString(),
                    editAge.getText().toString(),
                    editHeight.getText().toString(),
                    editWeight.getText().toString(),
                    editGender.getText().toString(),
                    editActivity.getText().toString(),
                    editBio.getText().toString()
            );

            Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
            finish(); // 返回主页
        });
    }
}
