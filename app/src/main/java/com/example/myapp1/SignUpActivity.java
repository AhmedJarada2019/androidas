package com.example.myapp1.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp1.MainActivity;
import com.example.myapp1.R;
import com.example.myapp1.data.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity_tag";
    private TextView login;
    private Button btnCreate;
    private EditText etUsername, etEmail, etPassword;
    private FirebaseAuth auth;
    private FirebaseFirestore mDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        login = findViewById(R.id.tvLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnCreate = findViewById(R.id.btnCreate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();

        if (username.equals("")) {
            Toast.makeText(this, "Enter username !!", Toast.LENGTH_SHORT).show();
        } else if (email.equals("")) {
            Toast.makeText(this, "Enter Email !!", Toast.LENGTH_SHORT).show();
        } else if (password.equals("")) {
            Toast.makeText(this, "Enter password !!", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.show();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            createNewUser(username, authResult.getUser().getUid());


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createNewUser(String username, String uid) {

        User user = new User(username, uid);

        mDatabase.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: new user created");
                        progressDialog.dismiss();

                        Toast.makeText(SignUpActivity.this, "Welcome !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: error " + e.getMessage());
                    }
                });
    }

}