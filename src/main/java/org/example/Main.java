package org.example;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        OrderBook orderBook = new OrderBook();
        orderBook.readInputFile(new File(System.getProperty("user.dir") + File.separator + "input.txt"));
        orderBook.commandExecution();

    }
}