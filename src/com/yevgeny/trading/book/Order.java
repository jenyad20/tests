package com.yevgeny.trading.book;

public class Order implements Comparable<Order>{

    private long timestamp;
    private boolean isBid;
    private int id;
    private double price;
    private int quantity;
    private String venue;
    private OrderType type;

    public Order(long timestamp, boolean isBid, int id, double price, int quantity, String venue, OrderType type) {
        this.timestamp = timestamp;
        this.isBid = isBid;
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.venue = venue;
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isBid() {
        return isBid;
    }

    public void setBid(boolean bid) {
        isBid = bid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Order{" +
                "timestamp=" + timestamp +
                ", isBid=" + isBid +
                ", id=" + id +
                ", price=" + price +
                ", quantity=" + quantity +
                ", venue='" + venue + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public int compareTo(Order order) {
        if(this.price == order.price) return Integer.compare(this.id, order.id);
        return Double.compare(this.price, order.price);
    }
}
