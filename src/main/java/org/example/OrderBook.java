package org.example;

import java.io.*;
import java.util.TreeMap;

public class OrderBook {

    private final TreeMap<Integer, Integer> askList = new TreeMap<>();
    private final TreeMap<Integer, Integer> bidList = new TreeMap<>();

    private final FileWriter writer;

    {
        try {
            writer = new FileWriter(System.getProperty("user.dir") + File.separator + "output.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBestAsk() {
        if (!askList.isEmpty()) {
            StringBuilder result = new StringBuilder();
            result.append(askList.firstKey()).append(",").append(askList.firstEntry().getValue());
            return result.toString();
        }
        return "";
    }

    public String getBestBid() {
        if (!bidList.isEmpty()) {
            StringBuilder result = new StringBuilder();
            result.append(bidList.lastKey()).append(",").append(bidList.lastEntry().getValue());
            return result.toString();
        }
        return "";
    }

    public int getProductSize(int price) {
        if (askList.containsKey(price)) {
            return askList.getOrDefault(price, 0);
        } else if (bidList.containsKey(price)) {
            return bidList.getOrDefault(price, 0);
        } else {
            return 0;
        }
    }
    public void commandExecution(String cl) throws IOException {
        int firstComma = cl.indexOf(",");
        int secondComma = cl.indexOf(",", firstComma + 1);
        char command = cl.charAt(0);
        if (command == 'u') {
            int thirdComma = cl.indexOf(",", secondComma + 1);
            int price = Integer.parseInt(cl.substring(firstComma + 1, secondComma));
            int size = Integer.parseInt(cl.substring(secondComma + 1, thirdComma));
            char type = cl.charAt(thirdComma + 1);
            Product product = new Product(price, size, type);
            updateOrdersBooks(product);
        } else if (command == 'q') {
            char type = cl.charAt(7);
            if (type == 'b' || type == 'a') {
                writeAllOutput(writeOutputFile(type));
            } else {
                int size = Integer.parseInt(cl.substring(secondComma + 1));
                writeAllOutput(String.valueOf(getProductSize(size)));
            }
        } else if (command == 'o') {
            char type = cl.charAt(2);
            int size = Integer.parseInt(cl.substring(secondComma + 1));
            removeOrder(type, size);
        }

    }

    public void updateOrdersBooks(Product product) {
        if (product.getType() == 'b') {
            checkingAndUpdate(bidList, product);
        } else {
            checkingAndUpdate(askList, product);
        }
    }


    public void checkingAndUpdate(TreeMap<Integer, Integer> orderBook, Product product) {
        if (product.getSize() == 0) {
            orderBook.remove(product.getPrice());
        } else {
            orderBook.put(product.getPrice(), product.getSize());
        }
    }


    public void readInputFile() {
        File input = new File(System.getProperty("user.dir") + File.separator + "input.txt");
        try (FileReader fileReader = new FileReader(input);
             BufferedReader br = new BufferedReader(fileReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                commandExecution(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeOutputFile(char command) {
        if (command == 'b') {
            return getBestBid();
        } else if (command == 'a') {
            return getBestAsk();
        } else {
            return String.valueOf(getProductSize(Integer.parseInt(String.valueOf(command))));
        }
    }

    public void writeAllOutput(String writeList) throws IOException {
        writer.write(writeList + "\n");
        writer.flush();
    }

    public void removeOrder(char input, int size) {
        if (input == 's') {
            removeBid(size);
        } else if (input == 'b') {
            removeAsk(size);
        }
    }

    public void removeBid(int size) {
        while (!bidList.isEmpty() && size > 0) {
            if (bidList.lastEntry().getValue() <= size) {
                size -= bidList.lastEntry().getValue();
                bidList.remove(bidList.lastKey());
            } else {
                bidList.replace(bidList.lastKey(), bidList.lastEntry().getValue() - size);
                size = 0;
            }
        }
    }

    public void removeAsk(int size) {
        while (!askList.isEmpty() && size > 0) {
            if (askList.firstEntry().getValue() <= size) {
                size -= askList.firstEntry().getValue();
                askList.remove(askList.firstKey());
            } else {
                askList.replace(askList.firstKey(), askList.firstEntry().getValue() - size);
                size = 0;
            }
        }
    }
}
