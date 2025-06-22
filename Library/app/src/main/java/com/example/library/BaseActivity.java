package com.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.library.user.Role;
import com.example.library.user.RoleCallback;
import com.example.library.user.UserUtils;
import com.google.firebase.auth.FirebaseAuth;

public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Alt sınıfın layout'unu ayarla
        setContentView(getLayoutResourceId());

        // Toolbar'ı başlat
        myToolbar = findViewById(R.id.customToolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }
        setupLogoutButton();
        setupHomeButton();
    }

    private void setupHomeButton() {

        Button buttonHome = findViewById(R.id.buttonHome);

        buttonHome.setOnClickListener(v -> {
            UserUtils.getUserRole(new RoleCallback() {
                @Override
                public void onRoleReceived(Role role) {
                    Intent intent;
                    switch (role) {
                        case LIBRARY_ADMIN:
                            intent = new Intent(BaseActivity.this, AdminActivity.class);
                            break;
                        default:
                            intent = new Intent(BaseActivity.this, UserHomeActivity.class);
                            break;
                    }

                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(BaseActivity.this, "Rol alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Alt sınıflar bu metodu override ederek kendi layout'larını sağlar
    protected abstract int getLayoutResourceId();

    protected void setupLogoutButton() {
        Button logoutButton = findViewById(R.id.buttonLogout);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                Intent intent = new Intent(BaseActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Tüm stack'i temizle
                startActivity(intent);
                finish();
                FirebaseAuth.getInstance().signOut();
            });
        }
    }
}
