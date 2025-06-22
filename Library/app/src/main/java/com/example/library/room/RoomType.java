package com.example.library.room;

import com.example.library.user.Role;

public enum RoomType {
    ACADEMIC,
    INDIVIDUAL,
    GROUP,
    MEETING,
    UNKNOWN;


    public static RoomType of(String roomType) {
        for (RoomType value : RoomType.values()) {
            if (value.toString().equalsIgnoreCase(roomType)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
