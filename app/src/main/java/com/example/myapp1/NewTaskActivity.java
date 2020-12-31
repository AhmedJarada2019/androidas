package com.example.myapp1;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp1.data.ToDo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewTaskActivity extends AppCompatActivity {

    private static final String TAG = "NewTaskActivity_tag";

    private FirebaseFirestore database;

    private ImageView btnBack;
    private TextView btnAddTask;

    private String listTitle;
    private String listId;
    private int listSize;

    private EditText etTaskTitle, etTaskDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        btnBack = findViewById(R.id.btnBack);
        btnAddTask = findViewById(R.id.btnAddTask);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskTitle = findViewById(R.id.etTaskTitle);


        database = FirebaseFirestore.getInstance();

        listTitle = getIntent().getStringExtra("title");
        listId = getIntent().getStringExtra("list_id");
        listSize = getIntent().getIntExtra("list_size", 0);


        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etTaskTitle.getText().toString().trim();
                String description = etTaskDescription.getText().toString().trim();
                if (title.equals("")) {
                    Toast.makeText(NewTaskActivity.this, "Add Title !", Toast.LENGTH_SHORT).show();
                } else if (description.equals("")) {
                    Toast.makeText(NewTaskActivity.this, "Add description !", Toast.LENGTH_SHORT).show();
                } else {
                    addNewTask(title, description);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addNewTask(String title, String description) {

        final ProgressDialog progressDialog = new ProgressDialog(NewTaskActivity.this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        final DocumentReference docRef = database
                .collection("task")
                .document();

        ToDo toDo = new ToDo(docRef.getId(), title, description, false, "" + System.currentTimeMillis(), listId);

        docRef.set(toDo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: add new Todo " + docRef.getId());
                        updateListSize(listTitle, listSize);
                        progressDialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(NewTaskActivity.this, "Failed to add new ToDo ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateListSize(String listTitle, int size) {
        database.collection("list")
                .document(listId)
                .update("size", ++size)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: update");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }
}