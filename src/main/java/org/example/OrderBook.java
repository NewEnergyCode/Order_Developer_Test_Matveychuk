package org.example;

import java.io.*;
import java.util.*;

public class OrderBook {

    private static final ArrayList<Product> productList = new ArrayList<>();
    private static final LinkedList<String> writeList = new LinkedList<>();
    private static Product product;
    private static int bigSize;
    private static int firstRem;
    private static int bestAskPrice;
    private static int bestBidPrice;
    private static int bestAskSize;
    private static int bestBidSize;
    private static int spreadSize;
    private static int indexOfBestAsk;
    private static int indexOfBestBid;

    public static Product getBestAsk() {
        indexOfBestAsk = -1;
        bestAskPrice = productList.get(0).getPrice();
        int productListSize = productList.size();
        for (int i = 0; i < productListSize; i++) {
            product = productList.get(i);
            if (product.getType().equals("ask") && product.getSize() != 0) {
                if (indexOfBestAsk == -1) {
                    bestAskPrice = product.getPrice();
                    bestAskSize = product.getSize();
                    indexOfBestAsk = i;
                }
                if (product.getPrice() < bestAskPrice) {
                    bestAskPrice = product.getPrice();
                    bestAskSize = product.getSize();
                    indexOfBestAsk = i;
                }
            }
        }
        return productList.get(indexOfBestAsk);
    }

    public static Product getBestBid() {
        indexOfBestBid = -1;
        bestBidPrice = productList.get(0).getPrice();
        int productListSize = productList.size();
        for (int i = 0; i < productListSize; i++) {
            product = productList.get(i);
            if (product.getType().equals("bid") && product.getSize() != 0) {
                if (indexOfBestBid == -1) {
                    bestBidPrice = product.getPrice();
                    bestBidSize = product.getSize();
                    indexOfBestBid = i;
                }
                if (product.getPrice() > productList.get(indexOfBestBid).getPrice()) {
                    bestBidPrice = product.getPrice();
                    bestBidSize = product.getSize();
                    indexOfBestBid = i;
                }
            }
        }
        return productList.get(indexOfBestBid);
    }

    public static Product getProductSize(int price) {
        int productListSize = productList.size();
        boolean turn = false;
        for (int i = 0; i < productListSize; i++) {
            if (productList.get(i).getPrice() == price) {
                spreadSize = productList.get(i).getSize();
                turn = true;
                return productList.get(i);
            }
        }
        if (!turn) {
            spreadSize = 0;
        }
        return null;
    }

    public void commandExecution(List<String> cl) throws IOException {
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
                    if (productList.isEmpty()) {
                        productList.add(product);
                    } else {
                        updateOrdersBook(product);
                    }
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

    public static void updateOrdersBook(Product product) {
        int count = 0;
        int productListSize = productList.size();
        for (int i = 0; i < productListSize; i++) {
            if (productList.get(i).getPrice() == product.getPrice()) {
                productList.get(i).setSize(product.getSize());
                break;
            } else {
                count++;
            }
        }
        if (count == productListSize) {
            productList.add(product);
        }
    }


    public static List<String> readInputFile() {
        List<String> cl = new LinkedList<>();
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
        } else if (command.equals("best_bid")) {
            writeList.add(bestBidPrice + "," + bestBidSize + "\n");
        } else if (command.equals("best_ask")) {
            writeList.add(bestAskPrice + "," + bestAskSize + "\n");
        } else if (command.equals("size")) {
            writeList.add(spreadSize + "\n");
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
        firstRem = bestBidSize;
        if ((firstRem - size) < 0) {
            while (bigSize > 0) {
                bigSize = getBigSize(bigSize, product);
                product = getBestBid();
            }
        } else {
            productList.get(indexOfBestBid).setSize(firstRem - size);
        }

    }

    public static int getBigSize(int bigSize, Product product) {
        int indexOfProduct = productList.indexOf(product);
        if (bigSize > product.getSize()) {
            bigSize = bigSize - product.getSize();
            productList.get(indexOfProduct).setSize(0);
        } else {
            productList.get(indexOfProduct).setSize(product.getSize() - bigSize);
            bigSize = 0;
        }
        return bigSize;
    }

    public static void removeAsk(int size) {
        bigSize = size;
        product = getBestAsk();
        firstRem = bestAskSize;
        if ((firstRem - size) < 0) {
            while (bigSize > 0) {
                bigSize = getBigSize(bigSize, product);
                product = getBestAsk();
            }
        } else {
            productList.get(indexOfBestAsk).setSize(firstRem - size);
        }
    }
}
