package com.example.library;

import static com.example.library.common.CommonConstants.FIREBASE_ROOMS_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_URL;
import static com.example.library.common.CommonConstants.FIREBASE_USER_ROOM_RESERVATION_PATH;

import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.reservation.RoomReservation;
import com.example.library.room.Room;
import com.example.library.room.RoomAdapterType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminRoomManagementActivity extends BaseActivity{

    private DatabaseReference roomReservationDatabaseReference;
    private RecyclerView recyclerView;
    private List<RoomReservation> roomReservationList;
    private ReservationAdapter reservationAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        roomReservationDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_USER_ROOM_RESERVATION_PATH);
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.recyclerViewAdminRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        roomReservationList = new ArrayList<>();
        reservationAdapter = new ReservationAdapter(this, roomReservationList, RoomAdapterType.MANAGEMENT, null);
        recyclerView.setAdapter(reservationAdapter);

        getAllRooms();
    }

    private void getAllRooms() {
        roomReservationDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomReservationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RoomReservation room = snapshot.getValue(RoomReservation.class);
                    if (room != null) {
                        roomReservationList.add(room);
                        reservationAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("RoomListActivity", "Error retrieving rooms", databaseError.toException());
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_admin_room_management;
    }
}
