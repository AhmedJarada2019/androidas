package com.example.myapp1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp1.adapter.TodoAdapter;
import com.example.myapp1.data.ToDo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity_tag";

    private String listTitle;
    private String listId;
    private int listSize;

    private ImageView btnBack;
    private TextView tvListTitle;
    private Button btnCreateNewTask;
    private RecyclerView rvTodos;

    private FirebaseFirestore database;

    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rvTodos = findViewById(R.id.rvTodos);
        btnBack = findViewById(R.id.btnBack);
        tvListTitle = findViewById(R.id.tvListTitle);
        btnCreateNewTask = findViewById(R.id.btnCreateNewTask);

        setupRecycler();

        listTitle = getIntent().getStringExtra("list_title");
        listId = getIntent().getStringExtra("list_id");
        tvListTitle.setText(listTitle);

        database = FirebaseFirestore.getInstance();

        loadList();

        btnCreateNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, NewTaskActivity.class);
                intent.putExtra("title", listTitle);
                intent.putExtra("list_id", listId);
                intent.putExtra("list_size", listSize);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupRecycler() {

        adapter = new TodoAdapter();
        rvTodos.setAdapter(adapter);

        adapter.setOnCheckedListener(new TodoAdapter.OnChecked() {
            @Override
            public void onChecked(final ToDo todo) {

                database.collection("task")
                        .document(todo.getId())
                        .update("checked", !todo.isChecked())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: check the todo " + todo.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getMessage());
                            }
                        });
            }

            @Override
            public void onItemClicked(String id, String title, String description, String date) {
                Intent intent = new Intent(ListActivity.this, TaskActivity.class);

                intent.putExtra("task_title", title);
                intent.putExtra("task_description", description);
                intent.putExtra("task_date", date);
                intent.putExtra("task_id", id);

                startActivity(intent);
            }
        });

    }

    private void loadList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        database.collection("task")
                .whereEqualTo("list_id", listId)
                .orderBy("date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        Log.d(TAG, "onSuccess: list task get all the list");

                        if (error != null) {
                            Log.d(TAG, "onEvent: error " + error.getMessage());
                            return;
                        }

                        if (value != null) {
                            Log.d(TAG, "onEvent: list != null");

                            List<ToDo> list = value.toObjects(ToDo.class);

                            if (list.size() != 0) {
                                Log.d(TAG, "onEvent: list size " + list.size());

                                adapter.setList(list);
                                listSize = list.size();
                            } else {
                                Toast.makeText(ListActivity.this, "No List", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });

    }


}