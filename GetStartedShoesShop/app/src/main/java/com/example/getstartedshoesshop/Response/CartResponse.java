package com.example.getstartedshoesshop.Response;

import com.example.getstartedshoesshop.Model.Cart;

public class CartResponse {
    private boolean success;
    private Cart data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Cart getData() {
        return data;
    }

    public void setData(Cart data) {
        this.data = data;
    }
}
