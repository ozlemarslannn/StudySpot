package com.example.library;

import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.room.RoomAdapterType;

public class RoomViewHolder extends RecyclerView.ViewHolder {

        TextView textViewEndTime,textViewRoomType, textViewAllowedUsers, textViewPeopleLimit, textViewDuration, textViewReservationTime, textViewReleasedTime, textViewReservedBy;
        Button reserveButton;
        Button releaseReservationButton;
        CardView cardView;
        Spinner endTimeSpinner;

        public RoomViewHolder(@NonNull View itemView, RoomAdapterType adapterType) {
            super(itemView);
            textViewRoomType = itemView.findViewById(R.id.textViewRoomType);
            textViewAllowedUsers = itemView.findViewById(R.id.textViewAllowedUsers);
            textViewPeopleLimit = itemView.findViewById(R.id.textViewPeopleLimit);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            if (adapterType.isReserved()){
                releaseReservationButton = itemView.findViewById(R.id.releaseReservationsButton);
                textViewReservationTime = itemView.findViewById(R.id.textViewReservationTime);
                textViewReleasedTime = itemView.findViewById(R.id.textViewReleasedTime);
                cardView = itemView.findViewById(R.id.reservationCardView);
            }else if (adapterType.isManaged()){
                releaseReservationButton = itemView.findViewById(R.id.managementReleaseReservationsButton);
                textViewReservationTime = itemView.findViewById(R.id.textViewManagementReservationTime);
                textViewReleasedTime = itemView.findViewById(R.id.textViewManagementReleasedTime);
                textViewReservedBy = itemView.findViewById(R.id.textViewManagementReservedBy);
                cardView = itemView.findViewById(R.id.managementCardView);
            }
            else {
                reserveButton = itemView.findViewById(R.id.reserveButton);
                cardView = itemView.findViewById(R.id.viewRoomCardView);
                textViewEndTime = itemView.findViewById(R.id.textViewEndTime);
                endTimeSpinner = itemView.findViewById(R.id.endTimeSpinner);

            }
        }
    }