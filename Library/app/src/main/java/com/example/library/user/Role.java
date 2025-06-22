package com.example.library.user;

public enum Role {
    STUDENT("Student"),
    ACADEMIC_STAFF("Academic Staff"),
    LIBRARY_ADMIN("Library Administrator"),
    UNKNOWN("UNKNOWN");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static Role of(String roleStr){
//        return Arrays.stream(Role.values()).filter(roleInStream -> roleInStream.getRole().equals(roleStr)).findFirst().get();
        for (Role value : Role.values()) {
            if (value.getRole().equalsIgnoreCase(roleStr)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
