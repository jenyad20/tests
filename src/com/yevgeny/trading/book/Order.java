package com.yevgeny.trading.book;

public class Order{

    private final long timestamp;
    private final boolean isBid;
    private final int id;
    private final double price;
    private volatile int quantity;
    private final String venue;
    private final OrderType type;

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

    public boolean isBid() {
        return isBid;
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
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

    public OrderType getType() {
        return type;
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

}
