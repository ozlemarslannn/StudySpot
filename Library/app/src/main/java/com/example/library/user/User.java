package com.example.library.user;

import com.google.firebase.database.Exclude;

public class User {
    private String uid;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private Role role;

    // Firebase için boş yapıcı
    public User() {}

    public User(String name, String surname, String email, String password, String phone, String gender, Role role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
    }
    public User(String uid, String name, String surname, String email, String password, String phone, String gender, Role role) {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getter'lar
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public Role getRole() {
        return role;
    }

     @Exclude
    public String getNameSurName() {
        return this.getName() + " " + this.getSurname();
    }
}
