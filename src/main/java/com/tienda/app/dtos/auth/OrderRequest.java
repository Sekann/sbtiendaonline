package com.tienda.app.dtos.auth;

import com.tienda.app.models.OrderItem;

import java.util.List;

public class OrderRequest {
    private Long userId;
    private List<OrderItem> orderItems;

    public OrderRequest() {
    }

    public OrderRequest(Long userId, List<OrderItem> orderItems) {
        this.userId = userId;
        this.orderItems = orderItems;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
