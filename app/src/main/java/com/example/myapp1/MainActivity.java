package com.example.myapp1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp1.adapter.TaskListAdapter;
import com.example.myapp1.data.TaskList;
import com.example.myapp1.register.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_tag";

    private TextView tvLogout;
    private FirebaseAuth auth;
    private FirebaseFirestore database;

    private Button btnCreateNewList;

    private EditText edSearch;

    private TaskListAdapter adapter;
    private RecyclerView rvTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateNewList = findViewById(R.id.btnCreateNewList);
        edSearch = findViewById(R.id.edSearch);
        tvLogout = findViewById(R.id.tvLogout);
        rvTasks = findViewById(R.id.rvTasks);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        setupRecycler();

        // load list
        loadList(auth.getCurrentUser().getUid());


        edSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        btnCreateNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewListActivity.class));
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setupRecycler() {
        adapter = new TaskListAdapter();
        rvTasks.setAdapter(adapter);

        adapter.setOnItemClickListener(new TaskListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id, String title, int size) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("list_title", title);
                intent.putExtra("list_id", id);
                intent.putExtra("list_size", size);
                startActivity(intent);
            }
        });
    }

    private void loadList(String uid) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        database.collection("list")
                .whereEqualTo("uid", uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value != null) {
                            List<TaskList> list = value.toObjects(TaskList.class);

                            if (list.size() != 0) {
                                adapter.setList(list);
                            } else {
                                Toast.makeText(MainActivity.this, "No List", Toast.LENGTH_SHORT).show();
                            }

                        }
                        progressDialog.dismiss();
                    }
                });

//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        Log.d(TAG, "onSuccess: get all the list");
//                        Log.d(TAG, "onSuccess: " + queryDocumentSnapshots.toObjects(TaskList.class).size());
//
//
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: " + e.getMessage());
//                        progressDialog.dismiss();
//
//                    }
//                });

    }
}