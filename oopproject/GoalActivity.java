package com.example.oopproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GoalActivity extends AppCompatActivity {

    EditText editGoal;
    Button btnSaveGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        editGoal = findViewById(R.id.editGoal);
        btnSaveGoal = findViewById(R.id.btnSaveGoal);

        btnSaveGoal.setOnClickListener(v -> {
            String goalText = editGoal.getText().toString().trim();

            if (goalText.isEmpty()) {
                Toast.makeText(this, "Please enter a goal!", Toast.LENGTH_SHORT).show();
                return;
            }

            int goal = Integer.parseInt(goalText);

            // TODO: 未来可存入数据库或 SharedPreferences
            Toast.makeText(this, "Goal Saved: " + goal + " kcal", Toast.LENGTH_SHORT).show();
        });
    }
}
