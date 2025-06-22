package com.example.library.room;

import com.google.firebase.database.Exclude;

public class Room {
    private String key;
    private RoomType type;
    private int minPeople;
    private int maxPeople;
    private int maxDuration;
    @Exclude
    private String reservationId;
    @Exclude
    public String getReservationId() {
        return reservationId;
    }

    @Exclude
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    public Room() {
        // Firebase için boş constructor
    }

    public Room(String key, RoomType type, int minPeople, int maxPeople, int maxDuration) {
        this.key = key;
        this.type = type;
        this.minPeople = minPeople;
        this.maxPeople = maxPeople;
        this.maxDuration = maxDuration;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public int getMinPeople() {
        return minPeople;
    }

    public void setMinPeople(int minPeople) {
        this.minPeople = minPeople;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;
        return key.equals(room.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
