package com.example.library;

import static com.example.library.common.CommonConstants.FIREBASE_ROOMS_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_URL;
import static com.example.library.common.CommonConstants.FIREBASE_USERS_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_USER_ROOM_RESERVATION_PATH;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.common.CommonConstants;
import com.example.library.room.Room;
import com.example.library.room.RoomType;
import com.example.library.user.Role;
import com.example.library.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class WelcomeActivity extends AppCompatActivity {
    private Button loginButton, registerButton;
    private DatabaseReference roomReservationDatabaseReference;
    private DatabaseReference roomDatabaseReference;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        initializeDb();
        AndroidThreeTen.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Butonları tanımla
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Giriş butonuna tıklanınca LoginActivity'e geç
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Kayıt butonuna tıklanınca RegisterActivity'e geç
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void initializeDb(){
        removeAllRoomReservations();
//        addStaticRooms();
//        addUsers();
    }

    private void addUsers() {
        mAuth = FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_USERS_PATH);
//        removeAllUsers();
        User user = new User("samet", "student","samett@hotmail.com", "123456", "1234567890", "M", Role.STUDENT);
        addUser(user);
        user = new User("ali", "academic","alii@hotmail.com", "123456", "1234567890", "M", Role.ACADEMIC_STAFF);
        addUser(user);
        user = new User("admin", "admin","adminn@hotmail.com", "123456", "1234567890", "M", Role.LIBRARY_ADMIN);
        addUser(user);

    }
    private void addUser(User user){

        // Firebase Auth ile kullanıcı oluşturma
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();

                            userDatabaseReference.child(uid).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(WelcomeActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(WelcomeActivity.this, "Failed to save user info", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(WelcomeActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addStaticRooms() {
        roomDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_ROOMS_PATH);
        removeAllRooms();
//        Room room1 = new Room("room1", "Akademik Çalışma Odası", 1, 1, 1, 3, 1, 2);
//        Room room2 = new Room("room2","Bireysel Çalışma Odası", 2, 1, 1, 5, 1, 2);
//        Room room3 = new Room("room3","Grup Çalışma Odası", 3, 2, 4, 10, 1, 3);
//        Room room4 = new Room("room4","Toplantı Odası", 3, 4, 10, 2, 2, 4);
//            public Room(String key, String type, int allowed_users, int min_people, int max_people, int total_rooms, int min_duration, int max_duration) {

//        Room room1 = new Room("room1", "Akademik Çalışma Odası", 1, 1, 3, 2);
//        Room room2 = new Room("room2","Bireysel Çalışma Odası", 1, 1, 5, 2);
//        Room room3 = new Room("room3","Grup Çalışma Odası", 2, 4, 10, 3);
//        Room room4 = new Room("room4","Toplantı Odası", 4, 10, 2, 4);
//            public Room(String key, String type, int min_people, int max_people, int total_rooms, int max_duration) {

        for (int i = 1; i <= 3; i++) {
            Room room = new Room("ACADEMIC-"+i, RoomType.ACADEMIC, 1, 1, 2);
            roomDatabaseReference.child(room.getKey()).setValue(room);
        }
        for (int i = 1; i <= 5; i++) {
            Room room = new Room("INDIVIDUAL-"+i, RoomType.INDIVIDUAL, 1, 1, 2);
            roomDatabaseReference.child(room.getKey()).setValue(room);
        }
        for (int i = 1; i <= 5; i++) {
            Room room = new Room("GROUP-"+i, RoomType.GROUP, 2, 4, 3);
            roomDatabaseReference.child(room.getKey()).setValue(room);
        }
        for (int i = 1; i <= 2; i++) {
            Room room = new Room("MEETING-"+i, RoomType.MEETING, 4, 10, 4);
            roomDatabaseReference.child(room.getKey()).setValue(room);
        }

        Toast.makeText(this, "Odalar Firebase'e eklendi!", Toast.LENGTH_SHORT).show();
    }


    private void removeAllRooms() {
        roomDatabaseReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "All room_reservations deleted successfully.");
                    } else {
                        Log.e("Firebase", "Failed to delete room_reservations", task.getException());
                    }
                });
    }
    private void removeAllRoomReservations() {
        roomReservationDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_USER_ROOM_RESERVATION_PATH);
        roomReservationDatabaseReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "All room_reservations deleted successfully.");
                    } else {
                        Log.e("Firebase", "Failed to delete room_reservations", task.getException());
                    }
                });
    }
    private void removeAllUsers() {
        userDatabaseReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "All user deleted successfully.");
                    } else {
                        Log.e("Firebase", "Failed to delete user", task.getException());
                    }
                });
    }
}
