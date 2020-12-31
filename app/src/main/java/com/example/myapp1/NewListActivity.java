package com.example.myapp1;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp1.data.TaskList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewListActivity extends AppCompatActivity {

    private static final String TAG = "NewListActivity_tag";
    private EditText etEmailListTitle;
    private Button btnNewList;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        etEmailListTitle = findViewById(R.id.etListTitle);
        btnNewList = findViewById(R.id.btnNewList);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etEmailListTitle.getText().toString().trim();

                if (title.equals("")) {
                    Toast.makeText(NewListActivity.this, "Add list Title", Toast.LENGTH_SHORT).show();
                } else {

                    final ProgressDialog progressDialog = new ProgressDialog(NewListActivity.this);
                    progressDialog.setMessage("Loading ...");
                    progressDialog.show();

                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    DocumentReference docRef = database.collection("list")
                            .document();

                    TaskList newList = new TaskList(docRef.getId(), title, 0, auth.getUid(), "" + System.currentTimeMillis());


                    docRef.set(newList)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: ");
                                    progressDialog.dismiss();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: ");
                                    Toast.makeText(NewListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        });

    }
}