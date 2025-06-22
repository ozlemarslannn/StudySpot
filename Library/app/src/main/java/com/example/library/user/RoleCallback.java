package com.example.library.user;

public interface RoleCallback {
    void onRoleReceived(Role role);
    void onError(Exception e);
}
