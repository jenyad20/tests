package com.yevgeny.trading.book;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class OrderBook {

    private TreeSet<Order> bids;
    private TreeSet<Order> asks;
    private Map<Integer, Order> orders;

    public OrderBook() {
        bids = new TreeSet<>((o1, o2) -> {
            if(o1.getPrice() == o2.getPrice()) return Integer.compare(o1.getId(), o2.getId());
            return Double.compare(o2.getPrice(), o1.getPrice());
        });

        asks = new TreeSet<>((o1, o2) -> {
            if(o1.getPrice() == o2.getPrice()) return Integer.compare(o1.getId(), o2.getId());
            return Double.compare(o1.getPrice(), o2.getPrice());
        });
        orders = new ConcurrentHashMap<>();
    }

    public void addOrder(Order order){
        orders.put(order.getId(), order);
        if(order.isBid()) bids.add(order);
        else asks.add(order);
    }

    public void modifyOrder(Order order) throws Exception {
        Order curr = orders.getOrDefault(order.getId(), null);
        if(curr == null) throw new Exception("not found");
        curr.setQuantity(order.getQuantity());
    }

    public void deleteOrder(Order order){
        order = orders.getOrDefault(order.getId(), null);
        if(order == null) return;
        if(order.isBid()) bids.remove(order);
        else asks.remove(order);
        orders.remove(order.getId());
    }

    public void processOrder(Order order){

    }

    public Order getBestBid(){
        return bids.first();
    }

    public Order getBestOffer(){
        return asks.first();
    }


}
