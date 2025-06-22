package com.example.library;

import static com.example.library.common.CommonConstants.DELIMITER;
import static com.example.library.common.CommonConstants.FIREBASE_ROOMS_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_ROOM_ID_PATH;
import static com.example.library.common.CommonConstants.FIREBASE_URL;
import static com.example.library.common.CommonConstants.FIREBASE_USER_ROOM_RESERVATION_PATH;
import static com.example.library.reservation.RoomReservation.formatDateTime;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomViewHolder> {
    public static final int GREEN = Color.parseColor("#4CAF50");
    public static final int DARK = Color.parseColor("#FF424242");
    public static final int LIGHT = Color.parseColor("#FFFFFFFF");

    private final Context context;
    private final List<Room> roomList;
    private DatabaseReference roomDatabaseReference;
    private DatabaseReference userReservationDatabaseReference;
    private RoomAdapterType adapterType;
    private String startTime;



    public RoomAdapter(Context context, List<Room> roomList, RoomAdapterType adapterType, String startTime) {
        this.startTime = startTime;
        this.context = context;
        this.roomList = roomList;
        this.adapterType = adapterType;
        roomDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_ROOMS_PATH);
        userReservationDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_USER_ROOM_RESERVATION_PATH);
//        removeAllReservations();

    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int roomItem;
        roomItem = R.layout.room_item;
        View view = LayoutInflater.from(context).inflate(roomItem, parent, false);
        return new RoomViewHolder(view, adapterType);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.textViewRoomType.setText("Oda: " + room.getKey());
        holder.textViewPeopleLimit.setText("Kişi Sınırı: " + room.getMinPeople() + " - " + room.getMaxPeople());
        holder.textViewDuration.setText("Süre: " + "1" + " - " + room.getMaxDuration() + " saat");
        List<String> endTimes = new ArrayList<>();
        LocalDateTime start = RoomReservation.formatDateTime(this.startTime);

        for (int i = 1; i <= room.getMaxDuration(); i++) {
            endTimes.add(RoomReservation.formatDateTime(start.plusHours(i)));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, endTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.endTimeSpinner.setAdapter(adapter);


        userReservationDatabaseReference.orderByChild("ownerUserId")
                .equalTo(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasActiveReservation = false;

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            RoomReservation reservation = childSnapshot.getValue(RoomReservation.class);

                            if (reservation != null &&
                                    reservation.getRoomId().equals(room.getKey()) &&
                                    reservation.isStillActive()) {
                                hasActiveReservation = true;
                                break;
                            }
                        }

                        if (hasActiveReservation) {
                            holder.cardView.setCardBackgroundColor(GREEN);
                        } else {
                            holder.cardView.setCardBackgroundColor(LIGHT);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Reservation check cancelled: " + error.getMessage());
                    }
                });




        holder.reserveButton.setOnClickListener(v -> {
            reserveRoom(room, this.startTime, holder);
        });
    }

    private void reserveRoom(Room room, String startTimeStr, RoomViewHolder holder) {
        LocalDateTime endTime = formatDateTime(holder.endTimeSpinner.getSelectedItem().toString());
        LocalDateTime startTime = formatDateTime(startTimeStr);
        userReservationDatabaseReference.orderByChild(FIREBASE_ROOM_ID_PATH).equalTo(room.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isConflict = false;

                        for (DataSnapshot reservationSnapshot : snapshot.getChildren()) {
                            RoomReservation reservation = reservationSnapshot.getValue(RoomReservation.class);

                            if (reservation != null && reservation.getStartTime() != null && reservation.getEndTime() != null) {
                                LocalDateTime existingStart = RoomReservation.formatDateTime(reservation.getStartTime());
                                LocalDateTime existingEnd = RoomReservation.formatDateTime(reservation.getEndTime());

                                // Çakışma kontrolü
                                if (!(endTime.minusSeconds(1).isBefore(existingStart) || startTime.isAfter(existingEnd.minusSeconds(1)))) {
                                    if (reservation.isReservationCancelled()) {
                                        Log.e("Firebase", "Reservation exist but cancelled: ");
                                    }else {
                                        isConflict = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (isConflict) {
                            // Uyarı göster veya butonu kapat
                            Toast.makeText(context, "Seçilen zaman aralığında rezervasyon var!", Toast.LENGTH_LONG).show();
                        } else {
                            // Uygun, rezervasyon yapılabilir
                            RoomReservation roomReservation = new RoomReservation(getReservationKey(room, startTime), FirebaseAuth.getInstance().getCurrentUser().getUid(), room.getKey(), room.getType(),
                                    startTime, endTime, new ArrayList<>());

                            userReservationDatabaseReference.child(roomReservation.getReservationsId())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                // Zaten bu ID ile bir kayıt varsa hata ver
                                                Log.e("Firebase", "Bu ID ile zaten bir rezervasyon var: " + roomReservation.getReservationsId());
                                                Toast.makeText(context, "Bir kullanıcı bir oda tipinden aynı gün yalnızca 1 tane rezervasyon oluşturabilir!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Kayıt yoksa yaz
                                                userReservationDatabaseReference
                                                        .child(roomReservation.getReservationsId())
                                                        .setValue(roomReservation)
                                                        .addOnSuccessListener(aVoid ->
                                                                Toast.makeText(context, "Rezervasyon oluşturuldu", Toast.LENGTH_SHORT).show()
                                                        )
                                                        .addOnFailureListener(e ->
                                                                Toast.makeText(context, "Rezervasyon hatası: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                                        );
                                                holder.cardView.setCardBackgroundColor(GREEN);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Firebase", "Database hatası: " + error.getMessage());
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Hata yönetimi
                    }
                });
    }

    private void validateAndCompleteReservation(Room room) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        String reservationKey = getReservationKey(room, RoomReservation.formatDateTime(startTime));

        DatabaseReference reservationRef = userReservationDatabaseReference.child(reservationKey);

        reservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(context, "Bu odayı bugün zaten rezerve ettiniz.", Toast.LENGTH_SHORT).show();
                } else {
                    // Rezerve et
//                    reservationRef.setValue(new RoomReservation(user.getUid(), room.getKey(), now));

                    LocalDateTime start = LocalDateTime.now();
                    RoomReservation value = new RoomReservation("reservationsId",user.getUid(), room.getKey(), room.getType(), start, start.plusHours(2), Collections.emptyList());
                    reservationRef.setValue(value);
                    roomDatabaseReference.child(room.getKey()).setValue(room)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(context, room.getKey() + " id'li oda başarıyla rezerve edildi!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Güncelleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Reservation check cancelled: " + error.getMessage());
            }
        });
    }


    @NonNull
    private static String getReservationKey(Room room, LocalDateTime startTime) {
        return getReservationKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), room, startTime.toLocalDate());
    }
    @NonNull
    private static String getReservationKey(String userId, Room room, LocalDate reservationTime) {
        String formattedDate = RoomReservation.formatDate(reservationTime);
        return userId + DELIMITER + room.getType() + DELIMITER + formattedDate;
    }

    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
    }


}
