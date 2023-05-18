package org.example;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        OrderBook orderBook = new OrderBook();
        orderBook.commandExecution(orderBook.readInputFile());

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Run time: " + executionTime + " ms");
    }
}