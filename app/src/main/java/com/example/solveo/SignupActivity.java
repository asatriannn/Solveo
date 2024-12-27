package com.example.solveo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.solveo.User;


public class SignupActivity extends AppCompatActivity {
    EditText editemail, editpassword, editfname;
    Button signupbtn;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    ViewGroup logIn;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        logIn = findViewById(R.id.login);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        editemail = findViewById(R.id.Email);
        editpassword = findViewById(R.id.Password);
        editfname = findViewById(R.id.FullName);
        signupbtn = findViewById(R.id.signupbtn);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, fname;

                email = editemail.getText().toString();
                password = editpassword.getText().toString();
                fname = editfname.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignupActivity.this, "Email cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SignupActivity.this, "Password cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(fname)){
                    Toast.makeText(SignupActivity.this, "Full name cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                // Get the newly created user
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) {
                                    // Update the user's display name
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fname)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(profileTask -> {
                                                if (profileTask.isSuccessful()) {
                                                    // Save additional user info in Realtime Database
                                                    String userId = user.getUid();
                                                    User userData = new User(fname, email);
                                                    databaseReference.child(userId).setValue(userData)
                                                            .addOnCompleteListener(dbTask -> {
                                                                if (dbTask.isSuccessful()) {
                                                                    Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                                    // Redirect to login or main activity
                                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(SignupActivity.this, "Failed to save user info.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(SignupActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}

