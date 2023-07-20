package org.example;

import java.io.*;
import java.util.*;

public class OrderBook {

    private static final ArrayList<Product> askList = new ArrayList<>();
    private static final ArrayList<Product> bidList = new ArrayList<>();
    private static final LinkedList<String> writeList = new LinkedList<>();
    private static Product product;
    private static int bigSize;
    private static int firstRem;

    public static Product getBestAsk() {
        int bestAsk = 0;
        int minAsk = askList.get(0).getPrice();
        int productListSize = askList.size();
        for (int i = 0; i < productListSize; i++) {
            product = askList.get(i);
            if (product.getSize() != 0) {
                if (product.getPrice() < minAsk) {
                    bestAsk = i;
                }
            }
        }
        return askList.get(bestAsk);
    }


    public static Product getBestBid() {
        int bestBid = 0;
        int maxBid = bidList.get(0).getPrice();
        int productListSize = bidList.size();
        for (int i = 0; i < productListSize; i++) {
            product = bidList.get(i);
            if (product.getSize() != 0) {
                if (product.getPrice() > maxBid) {
                    bestBid = i;
                }
            }
        }
        return bidList.get(bestBid);
    }

    public static Product getProductSize(int price) {
        if (!askList.isEmpty()) {
            for (int i = 0; i < askList.size(); i++) {
                if (askList.get(i).getPrice() == price) {
                    return askList.get(i);
                }
            }
        }
        if (!bidList.isEmpty()) {
            for (int i = 0; i < bidList.size(); i++) {
                if (bidList.get(i).getPrice() == price) {
                    return askList.get(i);
                }
            }
        }
        return null;
    }

    public void commandExecution(ArrayList<String> cl) throws IOException {
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
                    product = new Product(word1, word2, words[3]);
                    updateOrdersBooks();
                }
                case "q" -> {
                    String secondWord = words[1];
                    switch (secondWord) {
                        case "best_bid" -> writeOutputFile(getBestBid(), secondWord);
                        case "best_ask" -> writeOutputFile(getBestAsk(), secondWord);
                        case "size" -> writeOutputFile(getProductSize(Integer.parseInt(words[2])), secondWord);
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
        writeAllOutput(writeList);
    }

    public static void updateOrdersBooks() {
        if (product.getType().equals("bid")) {
            updateBidBook();
        } else {
            updateAskBook();
        }
    }

    public static void updateBidBook() {
        if (bidList.isEmpty()) {
            bidList.add(product);
        } else {
            int count = 0;
            int bidListSize = bidList.size();
            for (int i = 0; i < bidListSize; i++) {
                if (bidList.get(i).getPrice() == product.getPrice()) {
                    bidList.get(i).setSize(product.getSize());
                    break;
                } else {
                    count++;
                }
            }
            if (count == bidListSize) {
                bidList.add(product);
            }
        }
    }

    public static void updateAskBook() {

        if (askList.isEmpty()) {
            askList.add(product);
        } else {
            int count = 0;
            int askListSize = askList.size();
            for (int i = 0; i < askListSize; i++) {
                if (askList.get(i).getPrice() == product.getPrice()) {
                    askList.get(i).setSize(product.getSize());
                    break;
                } else {
                    count++;
                }
            }
            if (count == askListSize) {
                askList.add(product);
            }
        }
    }


    public static ArrayList<String> readInputFile() {
        ArrayList<String> cl = new ArrayList<>();
        File input = new File(System.getProperty("user.dir") + File.separator + "input.txt");
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(input));
            while ((line = br.readLine()) != null) {
                cl.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cl;
    }

    public static void writeOutputFile(Product product, String command) {
        if (product == null) {
            writeList.add("0" + "\n");
        } else if (command.equals("best_bid") | command.equals("best_ask")) {
            writeList.add(product.getPrice() + "," + product.getSize() + "\n");
        } else if (command.equals("size")) {
            writeList.add(product.getSize() + "\n");
        }
    }

    public static void writeAllOutput(LinkedList<String> writeList) throws IOException {
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + File.separator + "output.txt"));
//            for (int i = 0; i < writeList.size(); i++) {
//                writer.write(writeList.get(i));
//                writer.flush();
//            }
//            writer.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        int writeListSize = writeList.size();
        FileWriter writer = new FileWriter(System.getProperty("user.dir") + File.separator + "output.txt");
        for (int i = 0; i < writeListSize; i++) {
            writer.write(writeList.get(i));
            writer.flush();
        }
        writer.close();
    }

    public static void removeOrder(String input, int size) {
        if (input.equals("sell")) {
            removeBid(size);
        } else if (input.equals("buy")) {
            removeAsk(size);
        }
    }


    public static void removeBid(int size) {
        bigSize = size;
        product = getBestBid();
        firstRem = product.getSize();
        if ((firstRem - size) < 0) {
            while (bigSize > 0) {
                bigSize = getBigBidSize(bigSize);
                product = getBestBid();
            }
        } else {
            bidList.get(bidList.indexOf(product)).setSize(firstRem - size);
        }

    }

    public static int getBigBidSize(int bigSize) {
        int indexOfProduct = bidList.indexOf(product);
        int productGetSize = product.getSize();
        if (bigSize > productGetSize) {
            bigSize = bigSize - productGetSize;
            bidList.get(indexOfProduct).setSize(0);
        } else {
            bidList.get(indexOfProduct).setSize(productGetSize - bigSize);
            bigSize = 0;
        }
        return bigSize;
    }

    public static int getBigAskSize(int bigSize) {
        int indexOfProduct = askList.indexOf(product);
        int productGetSize = product.getSize();
        if (bigSize > productGetSize) {
            bigSize = bigSize - productGetSize;
            askList.get(indexOfProduct).setSize(0);
        } else {
            askList.get(indexOfProduct).setSize(productGetSize - bigSize);
            bigSize = 0;
        }
        return bigSize;
    }

    public static void removeAsk(int size) {
        bigSize = size;
        product = getBestAsk();
        firstRem = product.getSize();
        if ((firstRem - size) < 0) {
            while (bigSize > 0) {
                bigSize = getBigAskSize(bigSize);
                product = getBestAsk();
            }
        } else {
            askList.get(askList.indexOf(product)).setSize(firstRem - size);
        }
    }
}
