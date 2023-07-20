package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

public class Main {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        OrderBook orderBook = new OrderBook();
        orderBook.commandExecution(OrderBook.readInputFile());

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Run time: " + executionTime + " ms");
//        Scanner scanner = new Scanner(System.in);
//        scanner.nextInt();
    }
}