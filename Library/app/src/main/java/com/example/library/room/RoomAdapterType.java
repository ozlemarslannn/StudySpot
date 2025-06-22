package com.example.library.room;

public enum RoomAdapterType {
    VIEW,
    RESERVE,
    MANAGEMENT;

    public boolean isReserved(){
        return this==RESERVE;
    }

    public boolean isManaged() {
        return this==MANAGEMENT;
    }
}
