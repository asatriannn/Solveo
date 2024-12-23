package com.example.solveo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private EditText editemail, editpassword, editfname;
    private Button signupbtn;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private ViewGroup logIn;

    private static final String TAG = "SignupActivity";

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
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
        logIn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
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

        signupbtn.setOnClickListener(view -> {
            String email = editemail.getText().toString().trim();
            String password = editpassword.getText().toString().trim();
            String fname = editfname.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignupActivity.this, "Email cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(SignupActivity.this, "Password cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(fname)) {
                Toast.makeText(SignupActivity.this, "Full name cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User registration successful
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Update profile with the user's full name
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fname)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(profileTask -> {
                                            if (profileTask.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                                // Save additional data to Firebase Realtime Database
                                                String userId = user.getUid();
                                                User userData = new User(fname, email);

                                                databaseReference.child(userId).setValue(userData)
                                                        .addOnCompleteListener(dbTask -> {
                                                            if (dbTask.isSuccessful()) {
                                                                Log.d(TAG, "User data saved to database.");
                                                                Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                                                                // Redirect to login
                                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Log.e(TAG, "Database error: ", dbTask.getException());
                                                                Toast.makeText(SignupActivity.this, "Failed to save user info.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Log.e(TAG, "Profile update failed: ", profileTask.getException());
                                                Toast.makeText(SignupActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Log.e(TAG, "User registration failed: ", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
