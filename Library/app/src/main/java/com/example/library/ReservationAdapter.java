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
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ReservationAdapter extends RecyclerView.Adapter<RoomViewHolder> {
    public static final int GREEN = Color.parseColor("#4CAF50");
    public static final int DARK = Color.parseColor("#FF424242");
    public static final int LIGHT = Color.parseColor("#FFFFFFFF");

    private final Context context;
    private final List<RoomReservation> roomReservationList;
    private DatabaseReference roomDatabaseReference;
    private DatabaseReference userReservationDatabaseReference;
    private RoomAdapterType adapterType;
    private String startTime;



    public ReservationAdapter(Context context, List<RoomReservation> roomReservationList, RoomAdapterType adapterType, String startTime) {
        this.startTime = startTime;
        this.context = context;
        this.roomReservationList = roomReservationList;
        this.adapterType = adapterType;
        roomDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_ROOMS_PATH);
        userReservationDatabaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(FIREBASE_USER_ROOM_RESERVATION_PATH);

    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int roomItem;
        if (adapterType.isReserved()){
            roomItem = R.layout.reserved_room_item;
        }else if (adapterType.isManaged()){
            roomItem = R.layout.management_room_item;
        }else {
            roomItem = R.layout.room_item;
        }
        View view = LayoutInflater.from(context).inflate(roomItem, parent, false);
        return new RoomViewHolder(view, adapterType);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomReservation roomReservation = roomReservationList.get(position);
        //todo define this
        Room room = getRoomFromRoomId(roomReservation.getReservationsId());

        holder.textViewRoomType.setText("Oda: " + roomReservation.getRoomId());
        holder.textViewPeopleLimit.setText("Kişi Sınırı: " + room.getMinPeople() + " - " + room.getMaxPeople());
        holder.textViewDuration.setText("Süre: " + "1" + " - " + room.getMaxDuration() + " saat");

        if (adapterType.isReserved()) {
            holder.releaseReservationButton.setOnClickListener(v -> {
                releaseReservationForRoom(roomReservation, holder);
            });
            userReservationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (roomReservation!=null) {
                        holder.textViewReservationTime.setText("Rezervasyon Saati: " + roomReservation.getStartTime() + "-" + roomReservation.getEndTime());
                        holder.textViewReleasedTime.setText("Rezervasyon Sonlandırma Saati: " + roomReservation.getReleasedDate());
                        if (roomReservation.isReservationCancelled()) {
                            holder.cardView.setCardBackgroundColor(DARK);
                        }else {
                            holder.cardView.setCardBackgroundColor(LIGHT);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if (adapterType.isManaged()){
            holder.releaseReservationButton.setOnClickListener(v -> {
                releaseReservationForRoom(roomReservation, holder);
            });
            if (roomReservation!=null) {
                holder.textViewReservationTime.setText("Rezervasyon Saati: " + roomReservation.getStartTime() + "-" + roomReservation.getEndTime());
                holder.textViewReleasedTime.setText("Rezervasyon Sonlandırma Saati: " + roomReservation.getReleasedDate());
                holder.textViewReservedBy.setText("Rezervasyon Sahibi: " + roomReservation.getOwnerUserId());
                if (roomReservation.isReservationCancelled()) {
                    holder.cardView.setCardBackgroundColor(DARK);
                }else {
                    holder.cardView.setCardBackgroundColor(LIGHT);
                }
            }
        }
    }

    private Room getRoomFromRoomId(String reservationsId) {
        return new Room();
    }

    private void releaseReservationForRoom(RoomReservation roomReservation, RoomViewHolder holder) {
        DatabaseReference userRoomRes = userReservationDatabaseReference.child(roomReservation.getReservationsId());
        if (roomReservation==null){
            Toast.makeText(context, "Bu oda zaten rezerve edilmemiş!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (roomReservation.isReservationCancelled()) {
            Toast.makeText(context, "Bu rezervasyon zaten sonlandırılmış!", Toast.LENGTH_SHORT).show();
        }else {
            roomReservation.release();
            userRoomRes.setValue(roomReservation);

            userReservationDatabaseReference.child(roomReservation.getReservationsId()).setValue(roomReservation)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, roomReservation.getReservationsId() + " id'li oda rezervasyonu başarıyla sonlandırıldı!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Güncelleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            holder.cardView.setCardBackgroundColor(DARK);
//                                notifyDataSetChanged();
        }
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
        return roomReservationList != null ? roomReservationList.size() : 0;
    }


}
