package org.example;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException {
        OrderBook orderBook = new OrderBook();
        orderBook.commandExecution(orderBook.readInputFile());
    }
}