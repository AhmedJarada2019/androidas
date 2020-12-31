package com.example.myapp1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaskActivity extends AppCompatActivity {
    private static final String TAG = "TaskActivity_tag";
    String task_title;
    String task_description;
    String task_date;
    String task_id;
    private ImageView btnBack;
    private EditText etTaskTitle, etTaskDescription;
    private TextView btnEdit;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        btnBack = findViewById(R.id.btnBack);
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        btnEdit = findViewById(R.id.btnEdit);

        database = FirebaseFirestore.getInstance();


        Intent i = getIntent();

        task_title = i.getStringExtra("task_title");
        task_description = i.getStringExtra("task_description");
        task_date = i.getStringExtra("task_date");
        task_id = i.getStringExtra("task_id");

        etTaskTitle.setText(task_title);
        etTaskDescription.setText(task_description);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: task id  : " + task_id);
                Log.d(TAG, "onClick: task_date  : " + task_date);

                String title = etTaskTitle.getText().toString().trim();
                String description = etTaskDescription.getText().toString().trim();

                if (!title.equals(task_title)) {
                    database.collection("task")
                            .document(task_id)
                            .update("title", title)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: update task title");

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: error " + e.getMessage());
                                }
                            });
                }


                if (!description.equals(task_description)) {
                    database.collection("task")
                            .document(task_id)
                            .update("description", description)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: success to create task");

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: error " + e.getMessage());
                                }
                            });
                }
                finish();
            }
        });
    }
}