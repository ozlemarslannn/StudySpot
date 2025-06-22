package com.example.library;

import static com.example.library.common.CommonConstants.FIREBASE_ROLE_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_USERS_PATH;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.library.common.CommonConstants;
import com.example.library.user.Role;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView loginSuccessText;
    private TextView registerLink;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(CommonConstants.FIREBASE_URL).getReference(FIREBASE_USERS_PATH);

        // XML view'ları bağlama
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        buttonLogin = findViewById(R.id.loginButton);
        loginSuccessText = findViewById(R.id.textViewLoginSuccess);
        registerLink = findViewById(R.id.registerLink);

        // Giriş butonuna tıklayınca
        buttonLogin.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> forwardToRegisterPage());
    }

    private void forwardToRegisterPage() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase ile giriş yapma
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            checkUserRole(uid); // Kullanıcının rolünü kontrol et ve yönlendir
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String uid) {
        mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Role role = snapshot.child(FIREBASE_ROLE_PATH).getValue(Role.class);
                    Log.e("LoginActivity", "User role: " + role);

                    if (role == null || role==Role.UNKNOWN) {
                        Toast.makeText(LoginActivity.this, "Role not found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    loginSuccessText.setVisibility(View.VISIBLE);

                    // 1 saniye sonra ilgili sayfaya yönlendir
                    new Handler().postDelayed(() -> {
                        Intent intent;
                        switch (role) {
                            case LIBRARY_ADMIN:
                                intent = new Intent(LoginActivity.this, AdminActivity.class);
                                break;
                            default:
                                intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                                break;
                        }

                        startActivity(intent);
                        finish();
                    }, 1000); // 1 saniye bekle
                } else {
                    Toast.makeText(LoginActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
