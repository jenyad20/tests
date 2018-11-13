package com.yevgeny.trading.book;

import java.util.*;

import static com.yevgeny.trading.book.OrderType.MARKET;

public class OrderBook {

    private List<Order> bids;
    private List<Order> asks;

    public OrderBook() {
        bids = new ArrayList<>();
        asks = new ArrayList<>();
    }

    public void addOrder(Order order){

    }

    public void modifyOrder(Order order){

    }

    public void deleteOrder(Order order){

    }

    public void processOrder(Order order){

    }

    public Order getBestBid(){
        return null;
    }

    public Order getBestOffer(){
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
//        Queue<Order> orderQueue = new PriorityQueue<>();
        TreeMap <Integer, Order> map = new TreeMap<>();
        TreeSet<Order> set = new TreeSet<>();
        Order order1 = new Order(1, false, 8, 10, 25, "test", MARKET);
        Order order2 = new Order(1, false, 6, 5, 25, "test", MARKET);
        Order order3 = new Order(1, false, 5, 20, 25, "test", MARKET);
        Order order4 = new Order(1, false, 3, 20, 25, "test", MARKET);
//        orderQueue.add(order1);
//        orderQueue.add(order2);
//        orderQueue.add(order3);

//        map.putIfAbsent(order1.getId(), order1);
//        map.putIfAbsent(order2.getId(), order2);
//        map.putIfAbsent(order3.getId(), order3);
//        map.putIfAbsent(order4.getId(), order4);
        set.add(order1);
        set.add(order2);
        set.add(order3);
        set.add(order4);


        order2.setPrice(30);
        order3.setId(2);

        Thread.sleep(2000);

        System.out.println(set.contains(order2));
        System.out.println(set.remove(order4));

//        System.out.println(set.first());
        System.out.println(set.pollFirst());
        System.out.println(set.pollFirst());
        System.out.println(set.pollFirst());
        System.out.println(set.pollFirst());


//        System.out.println(map.pollFirstEntry().getValue());
//        System.out.println(orderQueue.poll());
//        System.out.println(orderQueue.poll());
//        System.out.println(orderQueue.poll());
    }
}
