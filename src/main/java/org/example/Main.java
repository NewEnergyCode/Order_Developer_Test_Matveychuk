package org.example;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        OrderBook orderBook = new OrderBook();
        List<String> result = orderBook.commandExecution(orderBook.readInputFile());
        orderBook.writeAllOutput(result);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Run time: " + executionTime + " ms");
//        Scanner scanner = new Scanner(System.in);
//        scanner.nextInt();
    }
}