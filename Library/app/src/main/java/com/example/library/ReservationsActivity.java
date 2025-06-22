package com.example.library;

import static com.example.library.common.CommonConstants.FIREBASE_ROOMS_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_URL;
import static com.example.library.common.CommonConstants.FIREBASE_USER_ROOM_RESERVATION_PATH;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.reservation.RoomReservation;
import com.example.library.room.Room;
import com.example.library.room.RoomAdapterType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReservationsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter reservationAdapter;
    private DatabaseReference roomDatabaseReference;
    private List<RoomReservation> roomReservationList;
    private ValueEventListener userRefListener;
    private ValueEventListener roomListener;
    private DatabaseReference userRef;
    private DatabaseReference userReservationDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.recyclerViewRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_ROOMS_PATH);
        userReservationDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_USER_ROOM_RESERVATION_PATH);

        roomReservationList = new ArrayList<>();
        reservationAdapter = new ReservationAdapter(this, roomReservationList, RoomAdapterType.RESERVE, null);
        recyclerView.setAdapter(reservationAdapter);

        getUserRoomReservations();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reservations;
    }
    private void getUserRoomReservations() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userReservationDatabaseReference
                .orderByChild("ownerUserId")
                .equalTo(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            RoomReservation reservation = child.getValue(RoomReservation.class);

                            if (reservation != null) {
                                roomReservationList.add(reservation);
                                reservationAdapter.notifyDataSetChanged();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Reservations", "Error retrieving reservations", error.toException());
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (roomListener != null) {
            roomDatabaseReference.removeEventListener(roomListener);
        }
        if (userRefListener != null) {
            userRef.removeEventListener(userRefListener);
        }
    }
}
