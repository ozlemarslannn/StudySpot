package com.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.user.User;
import com.example.library.user.UserCallback;
import com.example.library.user.UserUtils;

public class AdminActivity extends BaseActivity {

    private Button btnManageRooms;
    private TextView textViewAdminWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnManageRooms = findViewById(R.id.btnManageRooms);
        textViewAdminWelcome = findViewById(R.id.textViewAdminWelcome);
        UserUtils.getUser(new UserCallback() {
            @Override
            public void onUserReceived(User user) {
                textViewAdminWelcome.setText("Hoşgeldiniz: " + user.getNameSurName());
            }

            @Override
            public void onError(Exception e) {

            }
        });

        btnManageRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Admin için oda yönetim ekranı açılır
                Intent intent = new Intent(AdminActivity.this, AdminRoomManagementActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_admin;
    }
}
