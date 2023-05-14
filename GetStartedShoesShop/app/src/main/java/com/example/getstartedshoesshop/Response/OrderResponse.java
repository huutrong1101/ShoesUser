package com.example.getstartedshoesshop.Response;

import com.example.getstartedshoesshop.Model.Cart;
import com.example.getstartedshoesshop.Model.Order;


public class OrderResponse {
    private boolean success;
    private Order data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Order getData() {
        return data;
    }

    public void setData(Order data) {
        this.data = data;
    }
}
