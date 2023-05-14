package com.example.getstartedshoesshop.Response;

import com.example.getstartedshoesshop.Model.User;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("user")
    private User user;

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }
}
