package com.example.library.reservation;

import com.example.library.room.RoomType;
import com.google.firebase.database.Exclude;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class RoomReservation {
    private String reservationsId;
    private String ownerUserId;
    private String roomId;
    private RoomType roomType;

    private String reservationTime;
    private String releaseTime;
    private String startTime;
    private String endTime;
    private List<String> invitees = new ArrayList<>();
    private String releasedDate;



    public RoomReservation() {
    }
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public RoomReservation(String reservationsId, String ownerUserId, String roomId, RoomType roomType,
                           LocalDateTime reservationTime, LocalDateTime releaseTime, LocalDateTime startTime, LocalDateTime endTime, List<String> invitees, LocalDateTime releasedDate) {
        this.reservationsId = reservationsId;
        this.ownerUserId = ownerUserId;
        this.roomId = roomId;
        this.roomType = roomType;
        this.reservationTime = formatDateTime(reservationTime);
        this.releaseTime = formatDateTime(releaseTime);
        this.startTime = formatDateTime(startTime);
        this.endTime = formatDateTime(endTime);
        this.invitees = invitees;
        this.releasedDate = formatDateTime(releasedDate);
    }
    public RoomReservation(String reservationsId, String ownerUserId, String roomId, RoomType roomType,
                           LocalDateTime startTime, LocalDateTime endTime, List<String> invitees) {
        this.reservationsId = reservationsId;
        this.ownerUserId = ownerUserId;
        this.roomId = roomId;
        this.roomType = roomType;
        this.reservationTime = formatDateTime(LocalDateTime.now());
        this.startTime = formatDateTime(startTime);
        this.endTime = formatDateTime(endTime);
        this.invitees = invitees;
    }

    public static String formatDateTime(LocalDateTime now) {
        return now.format(dateTimeFormatter);
    }
    public static String formatDate(LocalDate now) {
        return now.format(dateFormatter);
    }
    public static LocalDateTime formatDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
    }
    public static LocalDate formatDate(String dateStr) {
        return LocalDate.parse(dateStr, dateFormatter);
    }
    public LocalDateTime getReservationTimeAsDateTime(){
        return formatDateTime(reservationTime);
    }


    public String getReservationsId() {
        return reservationsId;
    }

    public void setReservationsId(String reservationsId) {
        this.reservationsId = reservationsId;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<String> invitees) {
        this.invitees = invitees;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }

    @Exclude
    public void setReleasedDate(LocalDateTime releasedDate) {
        if (releasedDate!=null) {
            this.releasedDate = releasedDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        }
    }

    @Exclude
    public boolean isReservationCancelled(){
        return releasedDate!=null;
    }

    @Exclude
    public LocalDateTime getReservationDateAsLocalDateTime() {
        return LocalDateTime.parse(reservationTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Exclude
    public LocalDateTime getReleasedDateAsLocalDateTime() {
        return LocalDateTime.parse(releasedDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public void release() {
        this.setReleasedDate(LocalDateTime.now());
    }

    public boolean isStillActive() {
        return !isReservationCancelled() && formatDateTime(this.getEndTime()).isAfter(LocalDateTime.now());
    }
}
