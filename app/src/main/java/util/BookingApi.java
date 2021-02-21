package util;

import android.app.Application;

public class BookingApi extends Application {
    private String username;
    private String userId;
    private static BookingApi instance;

    public static BookingApi getInstance() {
        if (instance == null)
            instance = new BookingApi();
        return instance;

    }

    public BookingApi(){}


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
