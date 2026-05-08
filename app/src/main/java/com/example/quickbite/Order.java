package com.example.quickbite;

import java.util.List;

public class Order {
    private String orderId;
    private String userId;
    private String userName;
    private List<CartItem> items;
    private double totalPrice;
    private String status; // Pending, Processing, Out for Delivery, Completed
    private long timestamp;

    public Order() {}

    public Order(String orderId, String userId, String userName, List<CartItem> items, double totalPrice, String status, long timestamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}