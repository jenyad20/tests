package com.yevgeny.trading.book;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class OrderBook {

    private TreeSet<Order> bids;
    private TreeSet<Order> asks;
    private Map<Integer, Order> orders;
    private AtomicInteger bidsTotalQuantity = new AtomicInteger(0);
    private AtomicInteger asksTotalQuantity = new AtomicInteger(0);

    private static final Logger LOGGER = Logger.getLogger( OrderBook.class.getName() );

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

        LOGGER.info("order book created");
    }

    public void addOrder(Order order) throws Exception {
        if(orders.containsKey(order.getId())) throw new Exception("order already exists");
        orders.put(order.getId(), order);
        if(order.isBid()) {
            bids.add(order);
            bidsTotalQuantity.addAndGet(order.getQuantity());
            LOGGER.info("added BID " + order);
        }
        else {
            asks.add(order);
            asksTotalQuantity.addAndGet(order.getQuantity());
            LOGGER.info("added ASK " + order);
        }
    }

    public synchronized void modifyOrder(Order order) throws Exception {
        Order curr = orders.getOrDefault(order.getId(), null);
        if(curr == null) throw new Exception("trying to modify non existent order");
        if(!order.equals(curr)) throw new Exception("illegal changes while trying to modify order");
        int diff = order.getQuantity() - curr.getQuantity();
        LOGGER.info("modifying order " + curr);
        LOGGER.info("modifying order quantity from " + curr.getQuantity() + " to " + order.getQuantity());
        curr.setQuantity(order.getQuantity());
        if(order.isBid()){
            bidsTotalQuantity.addAndGet(diff);
        } else {
            asksTotalQuantity.addAndGet(diff);
        }
        LOGGER.info("modified Order " + curr);
    }

    private synchronized void modifyOrder(Order order, int quantity){
        if(order.isBid()){
            bidsTotalQuantity.addAndGet(quantity);
        } else {
            asksTotalQuantity.addAndGet(quantity);
        }

        order.setQuantity(order.getQuantity() + quantity);
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

        LOGGER.info("removed order " + order);
    }

    private void handleMarketOrder(Order order) throws Exception {
        LOGGER.info("processing MARKET order " + order);
        int quantity = order.getQuantity();
        TreeSet<Order> set;
        if(order.isBid()){
            if (quantity > asksTotalQuantity.get()) throw new QuantityException("not enough asks");
            set = asks;
        } else {
            if (quantity > bidsTotalQuantity.get()) throw new QuantityException("not enough bids");
            set = bids;
        }
        Order curr;
        while (quantity > 0){
            curr = set.first();
            if(curr.getQuantity() <= quantity){
                deleteOrder(curr);
                quantity -= curr.getQuantity();
            } else {
                modifyOrder(curr, -1 * quantity);

                break;
            }
        }
    }

    private void handleLimitOrder(Order order) throws Exception {
        LOGGER.info("processing LIMITED order " + order);
        int quantity = order.getQuantity();
        int multiplier = 1;
        TreeSet<Order> set;
        if(order.isBid()){
            set = asks;
        } else {
            set = bids;
            multiplier = -1;
        }

        while (quantity > 0 && set.size() > 0) {
            Order curr = set.first();
            if (curr == null || ((order.getPrice() - curr.getPrice()) * multiplier) < 0)
                break;

            if (curr.getQuantity() <= quantity) {
                deleteOrder(curr);
                quantity -= curr.getQuantity();
            } else {
                modifyOrder(curr, -1 * quantity);
                quantity = 0;
                break;
            }
        }

        if(quantity > 0){
            order.setQuantity(quantity);
            addOrder(order);
        }
    }

    public synchronized void processOrder(Order order) throws Exception {

        LOGGER.info("processing order " + order);

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

    public int getBidsTotalQuantity() {
        return bidsTotalQuantity.get();
    }

    public int getAsksTotalQuantity() {
        return asksTotalQuantity.get();
    }

    public int getBidsCount(){
        return bids.size();
    }

    public int getAsksCount(){
        return asks.size();
    }

    public int getTotalOrders(){
        return orders.size();
    }
}
