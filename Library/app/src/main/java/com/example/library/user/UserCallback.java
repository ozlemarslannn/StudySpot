package com.example.library.user;

public interface UserCallback {
    void onUserReceived(User user);
    void onError(Exception e);
}
