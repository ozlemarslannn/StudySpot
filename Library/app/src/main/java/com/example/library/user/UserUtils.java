package com.example.library.user;

import static com.example.library.common.CommonConstants.FIREBASE_ROLE_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_URL;
import static com.example.library.common.CommonConstants.FIREBASE_USERS_PATH;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserUtils {
    public static void getUserRole(RoleCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance(FIREBASE_URL)
                    .getReference(FIREBASE_USERS_PATH)
                    .child(user.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Role role = dataSnapshot.child(FIREBASE_ROLE_PATH).getValue(Role.class);
                    if (role != null) {
                        callback.onRoleReceived(role);
                    } else {
                        callback.onError(new Exception("Rol bulunamadı"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.toException());
                }
            });
        } else {
            callback.onError(new Exception("Kullanıcı oturumu yok"));
        }
    }

    public static void getUser(UserCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance(FIREBASE_URL)
                    .getReference(FIREBASE_USERS_PATH)
                    .child(user.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        callback.onUserReceived(user);
                    } else {
                        callback.onError(new Exception("User bulunamadı"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.toException());
                }
            });
        } else {
            callback.onError(new Exception("Kullanıcı oturumu yok"));
        }
    }

}
