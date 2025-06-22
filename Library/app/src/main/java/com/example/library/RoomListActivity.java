package com.example.library;

import static com.example.library.common.CommonConstants.FIREBASE_ROLE_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_ROOMS_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_URL;
import static com.example.library.common.CommonConstants.FIREBASE_USERS_PATH;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.room.RoomType;
import com.example.library.user.Role;
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

public class RoomListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private DatabaseReference roomDatabaseReference;
    private List<Room> roomList;
    private ValueEventListener userRefListener;
    private ValueEventListener roomListener;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        roomDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_ROOMS_PATH);
//        addStaticRooms();
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String startTime = intent.getStringExtra(ReservationTimeActivity.START_TIME);
        String roomType = intent.getStringExtra(ReservationTimeActivity.ROOM_TYPE);
        if (startTime!=null){
            TextView tvReservationStartTime = findViewById(R.id.tvReservationStartTime);
            tvReservationStartTime.setVisibility(View.VISIBLE);
            tvReservationStartTime.setText(String.format("%s %s", tvReservationStartTime.getText(), startTime));
        }

        recyclerView = findViewById(R.id.recyclerViewRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, roomList, RoomAdapterType.VIEW, startTime);
        recyclerView.setAdapter(roomAdapter);

        getUserRoleAndFilterRooms(startTime, roomType);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_room_list;
    }

    private void getUserRoleAndFilterRooms(String startTime, String roomType) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userRef = FirebaseDatabase.getInstance(FIREBASE_URL)
                    .getReference(FIREBASE_USERS_PATH)
                    .child(user.getUid());

            userRefListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.child(FIREBASE_ROLE_PATH).getValue(String.class);
                    if (role != null) {
                        filterRoomsBasedOnRole(role.toLowerCase(), startTime, roomType); // role küçük harfe dönüştürülerek geçiliyor
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("RoomListActivity", "Error getting user role", databaseError.toException());
                }
            };
            userRef.addListenerForSingleValueEvent(userRefListener);
        }
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
    private void filterRoomsBasedOnRole(String role, String startTime, String roomType) {
        roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Room room = snapshot.getValue(Room.class);
                    room.setKey(snapshot.getKey());
                    if (room != null) {
                        if ( RoomType.of(roomType)==room.getType() ){
                            roomList.add(room);
                        }
//                        if ( (Role.of(role)==Role.STUDENT && room.getType() != RoomType.ACADEMIC)
//                                || (Role.of(role)==Role.ACADEMIC_STAFF && room.getType() != RoomType.INDIVIDUAL) ){
//                            roomList.add(room);
//                        }
                    }
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("RoomListActivity", "Error retrieving rooms", databaseError.toException());
            }
        };
        roomDatabaseReference.addValueEventListener(roomListener);
    }


}
