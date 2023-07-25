package org.example;

import java.io.*;
import java.util.*;

public class OrderBook {

    private static final ArrayList<Product> askList = new ArrayList<>();
    private static final ArrayList<Product> bidList = new ArrayList<>();
    private static final LinkedList<String> writeList = new LinkedList<>();
    private static Product product;
    private static int bestAskPrice;
    private static int bestBidPrice;
    private static int bestAskSize;
    private static int bestBidSize;
    private static int spreadSize;
    private static int indexOfBestAsk;
    private static int indexOfBestBid;

    public static void getBestAsk() {
        indexOfBestAsk = -1;
        bestAskPrice = askList.get(0).getPrice();
        int productListSize = askList.size();
        for (int i = 0; i < productListSize; i++) {
            product = askList.get(i);
            if (product.getSize() != 0) {
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
    }

    public static void getBestBid() {
        indexOfBestBid = -1;
        bestBidPrice = bidList.get(0).getPrice();
        int productListSize = bidList.size();
        for (int i = 0; i < productListSize; i++) {
            product = bidList.get(i);
            if (product.getSize() != 0) {
                if (indexOfBestBid == -1) {
                    bestBidPrice = product.getPrice();
                    bestBidSize = product.getSize();
                    indexOfBestBid = i;
                }
                if (product.getPrice() > bidList.get(indexOfBestBid).getPrice()) {
                    bestBidPrice = product.getPrice();
                    bestBidSize = product.getSize();
                    indexOfBestBid = i;
                }
            }
        }
    }

    public static void getProductSize(int price) {
        spreadSize = 0;
        for (int i = 0; i < askList.size(); i++) {
            if (askList.get(i).getPrice() == price) {
                spreadSize = askList.get(i).getSize();
                return;
            }
        }
        for (int i = 0; i < bidList.size(); i++) {
            if (bidList.get(i).getPrice() == price) {
                spreadSize = bidList.get(i).getSize();
                return;
            }
        }
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
                    updateOrdersBooks();
                }
                case "q" -> {
                    String secondWord = words[1];
                    switch (secondWord) {
                        case "best_bid", "best_ask" -> writeOutputFile(secondWord);
                        case "size" -> writeOutputFile(words[2]);
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
        checkingAndUpdate(bidList);
    }

    public static void updateAskBook() {
        checkingAndUpdate(askList);
    }

    public static void checkingAndUpdate(ArrayList<Product> list) {
        if (list.isEmpty()) {
            list.add(product);
        } else {
            int count = 0;
            int bidListSize = list.size();
            for (int i = 0; i < bidListSize; i++) {
                if (list.get(i).getPrice() == product.getPrice()) {
                    list.get(i).setSize(product.getSize());
                    break;
                } else {
                    count++;
                }
            }
            if (count == bidListSize) {
                list.add(product);
            }
        }
    }


    public static List<String> readInputFile() {
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

    public static void writeOutputFile(String command) {
        if (command.equals("best_bid")) {
            getBestBid();
            writeList.add(bestBidPrice + "," + bestBidSize + "\n");
        } else if (command.equals("best_ask")) {
            getBestAsk();
            writeList.add(bestAskPrice + "," + bestAskSize + "\n");
        } else {
            getProductSize(Integer.parseInt(command));
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


    public static int getBigAskSize(int size) {
        if (size > bestAskSize) {
            size = size - bestAskSize;
            askList.get(indexOfBestAsk).setSize(0);
        } else {
            askList.get(indexOfBestAsk).setSize(bestAskSize - size);
            size = 0;
        }
        return size;
    }

    public static int getBigBidSize(int size) {
        if (size > bestBidSize) {
            size = size - bestBidSize;
            bidList.get(indexOfBestBid).setSize(0);
        } else {
            bidList.get(indexOfBestBid).setSize(bestBidSize - size);
            size = 0;
        }
        return size;
    }

    public static void removeBid(int size) {
        getBestBid();
        if ((bestBidSize - size) < 0) {
            while (size > 0) {
                size = getBigBidSize(size);
                getBestBid();
            }
        } else {
            bidList.get(indexOfBestBid).setSize(bestBidSize - size);
        }

    }

    public static void removeAsk(int size) {
        getBestAsk();
        if ((bestAskSize - size) < 0) {
            while (size > 0) {
                size = getBigAskSize(size);
                getBestAsk();
            }
        } else {
            askList.get(indexOfBestAsk).setSize(bestAskSize - size);
        }
    }
}
