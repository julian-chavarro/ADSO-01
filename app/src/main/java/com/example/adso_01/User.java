package com.example.adso_01;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String password;

    // 🔥 ESTE ES OBLIGATORIO
    public User() {}

    @Ignore
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

