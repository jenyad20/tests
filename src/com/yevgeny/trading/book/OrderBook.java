package com.yevgeny.trading.book;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class OrderBook {

    private TreeSet<Order> bids;
    private TreeSet<Order> asks;
    private Map<Integer, Order> orders;
    private AtomicInteger bidsTotalQuantity = new AtomicInteger(0);
    private AtomicInteger asksTotalQuantity = new AtomicInteger(0);


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
        if(order.isBid()) {
            bids.add(order);
            bidsTotalQuantity.addAndGet(order.getQuantity());
        }
        else {
            asks.add(order);
            asksTotalQuantity.addAndGet(order.getQuantity());
        }
    }

    public synchronized void modifyOrder(Order order) {
        Order curr = orders.getOrDefault(order.getId(), null);
        int diff = order.getQuantity() - curr.getQuantity();
        curr.setQuantity(order.getQuantity());
        if(order.isBid()){
            bidsTotalQuantity.addAndGet(diff);
        } else {
            asksTotalQuantity.addAndGet(diff);
        }
    }

    public synchronized void deleteOrder(Order order){
        order = orders.getOrDefault(order.getId(), null);
        if(order == null) return;
        if(order.isBid()) bids.remove(order);
        else asks.remove(order);
        orders.remove(order.getId());

        if(order.isBid()){
            bidsTotalQuantity.addAndGet(order.getQuantity() * -1);
        } else {
            asksTotalQuantity.addAndGet(order.getQuantity() * -1);
        }
    }

    private void handleMarketOrder(Order order) throws QuantityException {
        int quantity = order.getQuantity();
        TreeSet<Order> set;
        if(order.isBid()){
            if (quantity > asksTotalQuantity.get()) throw new QuantityException("not enough asks");
            set = asks;
        } else {
            if (quantity > asksTotalQuantity.get()) throw new QuantityException("not enough bits");
            set = bids;
        }
        Order curr;
        while (quantity > 0){
            curr = set.first();
            if(curr.getQuantity() <= quantity){
                deleteOrder(curr);
                quantity -= curr.getQuantity();
            } else {
                curr.setQuantity(curr.getQuantity() - quantity);
                modifyOrder(curr);

                break;
            }
        }
    }

    private void handleLimitOrder(Order order){
        int quantity = order.getQuantity();
        int multiplier = 1;
        TreeSet<Order> set;
        if(order.isBid()){
            set = asks;
        } else {
            set = bids;
            multiplier = -1;
        }

        while (quantity > 0) {
            Order curr = set.first();
            if (curr == null || ((order.getPrice() - curr.getPrice()) * multiplier) < 0)
                break;

            if (curr.getQuantity() <= quantity) {
                deleteOrder(curr);
                quantity -= curr.getQuantity();
            } else {
                curr.setQuantity(curr.getQuantity() - quantity);
                modifyOrder(curr);
                quantity = 0;
                break;
            }
        }

        if(quantity > 0){
            order.setQuantity(quantity);
            addOrder(order);
        }
    }

    public synchronized void processOrder(Order order) throws QuantityException {

        switch (order.getType()){
            case MARKET:
                handleMarketOrder(order);
                break;
            case LIMIT:
                handleLimitOrder(order);
                break;
        }

    }

    public Order getBestBid(){
        return bids.first();
    }

    public Order getBestOffer(){
        return asks.first();
    }


}
