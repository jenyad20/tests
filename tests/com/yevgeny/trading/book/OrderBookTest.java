package com.yevgeny.trading.book;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jenya on 11/14/18.
 */
public class OrderBookTest {

    private OrderBook orderBook = new OrderBook();


    @Test
    public void addOrder() throws Exception {
        Order bid = new Order(1, true, 1, 10, 20, "venue1", OrderType.LIMIT);
        Order ask = new Order(1, false, 2, 10, 25, "venue1", OrderType.LIMIT);

        orderBook.addOrder(bid);
        assertTrue(orderBook.getBidsCount() == 1);
        assertTrue(orderBook.getAsksCount() == 0);
        assertTrue(orderBook.getBidsTotalQuantity() == 20);
        assertTrue(orderBook.getAsksTotalQuantity() == 0);


        orderBook.addOrder(ask);
        assertTrue(orderBook.getBidsCount() == 1);
        assertTrue(orderBook.getAsksCount() == 1);
        assertTrue(orderBook.getBidsTotalQuantity() == 20);
        assertTrue(orderBook.getAsksTotalQuantity() == 25);

        try {
            orderBook.addOrder(ask);
            fail("managed to add order with existing id");
        } catch (Exception e){
            assertTrue(orderBook.getTotalOrders() == 2);
        }
    }

    @Test
    public void modifyOrder() throws Exception {
        Order ask = new Order(1, false, 2, 10, 25, "venue1", OrderType.LIMIT);
        orderBook.addOrder(ask);
        assertTrue(orderBook.getAsksTotalQuantity() == 25);
        ask = new Order(1, false, 2, 10, 15, "venue1", OrderType.LIMIT);
        orderBook.modifyOrder(ask);
        assertTrue(orderBook.getAsksTotalQuantity() == 15);

        try {
            ask = new Order(1, false, 2, 10, 25, "venue2", OrderType.LIMIT);
            orderBook.modifyOrder(ask);
            fail("managed to modify order passing a different venue name");
        } catch (Exception e){
            assertTrue(orderBook.getAsksTotalQuantity() == 15);
        }

        try {
            ask = new Order(1, false, 3, 10, 25, "venue3", OrderType.LIMIT);
            orderBook.modifyOrder(ask);
            fail("managed to modify non existing order");
        } catch (Exception e){
            assertTrue(orderBook.getAsksCount() == 1);
        }
    }

    @Test
    public void deleteOrder() throws Exception {
        Order ask = new Order(1, false, 2, 10, 25, "venue1", OrderType.LIMIT);
        Order bid = new Order(1, true, 1, 10, 20, "venue1", OrderType.LIMIT);
        orderBook.addOrder(ask);
        orderBook.addOrder(bid);
        assertTrue(orderBook.getBidsCount() == 1);
        assertTrue(orderBook.getAsksCount() == 1);
        assertTrue(orderBook.getBidsTotalQuantity() == 20);
        assertTrue(orderBook.getAsksTotalQuantity() == 25);
        assertTrue(orderBook.getTotalOrders() == 2);
        ask = new Order(1, false, 2, 10, 25, "venue1", OrderType.LIMIT);
        orderBook.deleteOrder(ask);
        assertTrue(orderBook.getBidsCount() == 1);
        assertTrue(orderBook.getAsksCount() == 0);
        assertTrue(orderBook.getBidsTotalQuantity() == 20);
        assertTrue(orderBook.getAsksTotalQuantity() == 0);
        assertTrue(orderBook.getTotalOrders() == 1);
    }

    @Test
    public void processOrder() throws Exception {
        Order bid1 = new Order(1, true, 1, 10, 25, "venue1", OrderType.LIMIT);
        Order ask1 = new Order(2, false, 2, 10, 30, "venue1", OrderType.MARKET);
        Order ask2 = new Order(3, false, 3, 15, 30, "venue1", OrderType.LIMIT);
        Order bid2 = new Order(4, true, 4, 10, 35, "venue1", OrderType.MARKET);
        Order bid3 = new Order(5, true, 5, 9, 45, "venue1", OrderType.LIMIT);
        Order bid4 = new Order(6, true, 6, 7, 55, "venue1", OrderType.LIMIT);
        Order ask3 = new Order(7, false, 7, 10, 50, "venue1", OrderType.MARKET);
        Order ask4 = new Order(8, false, 9, 8, 50, "venue1", OrderType.LIMIT);
        Order ask5 = new Order(9, false, 10, 11, 50, "venue1", OrderType.LIMIT);
        Order ask6 = new Order(10, false, 11, 13, 50, "venue1", OrderType.LIMIT);
        Order bid7 = new Order(11, true, 12, 7, 50, "venue1", OrderType.MARKET);
        Order bid8 = new Order(12, true, 13, 14, 90, "venue1", OrderType.LIMIT);

        orderBook.processOrder(bid1);
        assertTrue(orderBook.getBidsCount() == 1);
        assertTrue(orderBook.getAsksCount() == 0);
        assertTrue(orderBook.getBidsTotalQuantity() == 25);
        assertTrue(orderBook.getAsksTotalQuantity() == 0);
        assertTrue(orderBook.getTotalOrders() == 1);

        try {
            orderBook.processOrder(ask1);
            fail("processed ask order with insufficient bids");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        orderBook.processOrder(ask2);
        assertTrue(orderBook.getBidsCount() == 1);
        assertTrue(orderBook.getAsksCount() == 1);
        assertTrue(orderBook.getBidsTotalQuantity() == 25);
        assertTrue(orderBook.getAsksTotalQuantity() == 30);
        assertTrue(orderBook.getTotalOrders() == 2);

        try {
            orderBook.processOrder(bid2);
            fail("processed nid order with insufficient asks");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        orderBook.processOrder(bid3);
        orderBook.processOrder(bid4);
        orderBook.processOrder(ask3);

        assertTrue(orderBook.getBidsTotalQuantity() == 75);
        assertTrue(orderBook.getBestBid().equals(bid3));
        assertTrue(orderBook.getBestBid().getQuantity() == 20);

        orderBook.processOrder(ask4);
        assertTrue(orderBook.getBidsTotalQuantity() == 55);
        assertTrue(orderBook.getBestBid().equals(bid4));
        assertTrue(orderBook.getBestBid().getQuantity() == 55);
        assertTrue(orderBook.getBidsCount() == 1);
        assertTrue(orderBook.getBestOffer().equals(ask4));
        assertTrue(orderBook.getBestOffer().getQuantity() == 30);

        orderBook.processOrder(ask5);
        orderBook.processOrder(ask6);
        orderBook.processOrder(bid7);

        assertTrue(orderBook.getAsksTotalQuantity() == 110);
        assertTrue(orderBook.getBestOffer().equals(ask5));
        assertTrue(orderBook.getBestOffer().getQuantity() == 30);

        orderBook.processOrder(bid8);
        assertTrue(orderBook.getAsksTotalQuantity() == 30);
        assertTrue(orderBook.getBestOffer().equals(ask2));
        assertTrue(orderBook.getBestOffer().getQuantity() == 30);
        assertTrue(orderBook.getBestBid().equals(bid8));
        assertTrue(orderBook.getBestBid().getQuantity() == 10);
    }

    @Test
    public void getBestBid() throws Exception {
        Order bid1 = new Order(1, true, 2, 10, 25, "venue1", OrderType.LIMIT);
        Order bid2 = new Order(1, true, 1, 10, 35, "venue2", OrderType.LIMIT);
        Order bid3 = new Order(1, true, 4, 10, 18, "venue3", OrderType.LIMIT);

        orderBook.addOrder(bid1);
        orderBook.addOrder(bid2);
        orderBook.addOrder(bid3);

        assertTrue(orderBook.getBestBid().equals(bid2));
    }

    @Test
    public void getBestOffer() throws Exception {
        Order ask1 = new Order(1, false, 2, 10, 25, "venue1", OrderType.LIMIT);
        Order ask2 = new Order(1, false, 1, 10, 15, "venue2", OrderType.LIMIT);
        Order ask3 = new Order(1, false, 4, 10, 18, "venue3", OrderType.LIMIT);

        orderBook.addOrder(ask1);
        orderBook.addOrder(ask2);
        orderBook.addOrder(ask3);

        assertTrue(orderBook.getBestOffer().equals(ask2));
    }

}