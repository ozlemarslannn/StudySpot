package com.example.library;

import static com.example.library.common.CommonConstants.FIREBASE_URL;
import static com.example.library.common.CommonConstants.FIREBASE_USERS_PATH;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.library.common.CommonConstants;
import com.example.library.user.RoleCallback;
import com.example.library.user.User;
import com.example.library.user.UserCallback;
import com.example.library.user.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHomeActivity extends BaseActivity {

    public static final String ROLE = "role";
    private Button btnViewRooms;
    private Button btnMyReservations;
    private TextView textViewWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnViewRooms = findViewById(R.id.btnViewRooms);
        btnMyReservations = findViewById(R.id.btnMyReservations);
        textViewWelcome = findViewById(R.id.textViewWelcome);
        UserUtils.getUser(new UserCallback() {
            @Override
            public void onUserReceived(User user) {
                textViewWelcome.setText("Hoşgeldiniz: " + user.getNameSurName());
            }

            @Override
            public void onError(Exception e) {

            }
        });


        Intent intent = new Intent(UserHomeActivity.this, ReservationTimeActivity.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            DatabaseReference userRef = FirebaseDatabase.getInstance(FIREBASE_URL)
                    .getReference(FIREBASE_USERS_PATH)
                    .child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    intent.putExtra(ROLE, snapshot.child(CommonConstants.FIREBASE_ROLE_PATH).getValue(String.class));
                    System.out.println();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        btnViewRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Burada öğrenciye özel oda listeleme sayfasına geçiş yapılabilir

                startActivity(intent);
            }
        });

        btnMyReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Akademik personele özel oda listeleme sayfası açılır
                Intent intent = new Intent(UserHomeActivity.this, ReservationsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_user_home;
    }
}
