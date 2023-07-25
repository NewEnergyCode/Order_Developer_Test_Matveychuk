package org.example;

import java.io.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class OrderBook {

    private static final TreeSet<Product> askList = new TreeSet<>(Comparator.comparingInt(Product::getPrice));
    private static final TreeSet<Product> bidList = new TreeSet<>(Comparator.comparingInt(Product::getPrice));


    public String getBestAsk() {
        if (!askList.isEmpty()) {
            return askList.first().getPrice() + "," + askList.first().getSize();
        }
        return "";
    }

    public String getBestBid() {
        if (!bidList.isEmpty()) {
            return bidList.last().getPrice() + "," + bidList.last().getSize();
        }
        return "";
    }

    public int getProductSize(int price) {
        for (Product product : askList) {
            if (product.getPrice() == price) {
                return product.getSize();
            }
        }
        for (Product product : bidList) {
            if (product.getPrice() == price) {
                return product.getSize();
            }
        }
        return 0;
    }

    public List<String> commandExecution(List<String> cl) throws IOException {
        List<String> writeList = new LinkedList<>();
        String[] words;
        int word1;
        int word2;
        int clSize = cl.size();
        for (int i = 0; i < clSize; i++) {
            words = cl.get(i).split(",");
            switch (words[0]) {
                case "u" -> {
                    word1 = Integer.parseInt(words[1]);
                    word2 = Integer.parseInt(words[2]);
                    Product product = new Product(word1, word2, words[3]);
                    updateOrdersBooks(product);
                }
                case "q" -> {
                    String secondWord = words[1];
                    switch (secondWord) {
                        case "best_bid", "best_ask" -> writeList.add(writeOutputFile(secondWord));
                        case "size" -> writeList.add(writeOutputFile(words[2]));
                    }
                }
                case "o" -> {
                    String secondWord = words[1];
                    if (secondWord.equals("buy") || secondWord.equals("sell")) {
                        removeOrder(secondWord, Integer.parseInt(words[2]));
                    }
                }
            }
        }
        return writeList;
    }

    public void updateOrdersBooks(Product product) {
        if (product.getType().equals("bid")) {
            checkingAndUpdate(bidList, product);
        } else {
            checkingAndUpdate(askList, product);
        }

    }


    public void checkingAndUpdate(TreeSet<Product> orderBook, Product product) {
        orderBook.removeIf(p -> p.getPrice() == product.getPrice());
        if (product.getSize() != 0) {
            orderBook.add(product);
        }
    }


    public List<String> readInputFile() {
        List<String> cl = new LinkedList<>();
        File input = new File(System.getProperty("user.dir") + File.separator + "input.txt");
        String line;
        try {
            FileReader fileReader = new FileReader(input);
            BufferedReader br = new BufferedReader(fileReader);
            while ((line = br.readLine()) != null) {
                cl.add(line);
            }
            br.close();
            fileReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cl;
    }

    public String writeOutputFile(String command) {
        if (command.equals("best_bid")) {
            return getBestBid();
        } else if (command.equals("best_ask")) {
            return getBestAsk();
        } else {
            return String.valueOf(getProductSize(Integer.parseInt(command)));
        }
    }

    public void writeAllOutput(List<String> writeList) throws IOException {
        FileWriter writer = new FileWriter(System.getProperty("user.dir") + File.separator + "output.txt");
        for (String line : writeList) {
            writer.write(line + "\n");
            writer.flush();
        }
        writer.close();
    }

    public void removeOrder(String input, int size) {
        if (input.equals("sell")) {
            removeBid(size);
        } else if (input.equals("buy")) {
            removeAsk(size);
        }
    }

    public void removeBid(int size) {
        while (!bidList.isEmpty() && size > 0) {
            Product bestBid = bidList.last();
            if (bestBid.getSize() <= size) {
                size -= bestBid.getSize();
                bidList.remove(bestBid);
            } else {
                bestBid.setSize(bestBid.getSize() - size);
                size = 0;
            }
        }
        System.out.println(bidList.last());
    }

    public void removeAsk(int size) {
        while (!askList.isEmpty() && size > 0) {
            Product bestAsk = askList.first();
            if (bestAsk.getSize() <= size) {
                size -= bestAsk.getSize();
                askList.remove(bestAsk);
            } else {
                bestAsk.setSize(bestAsk.getSize() - size);
                size = 0;
            }
        }
    }
}
