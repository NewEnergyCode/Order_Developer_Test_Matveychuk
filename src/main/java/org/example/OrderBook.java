package org.example;

import java.io.*;
import java.util.*;

public class OrderBook {

    private final ArrayList<Product> productList = new ArrayList<>();
    private final LinkedList<String> writeList = new LinkedList<>();
    private Product product;
    private int bigSize;
    private int firstRem;

    public Product getBestAsk() {
        int bestAsk = -1;
        for (int i = 0; i < productList.size(); i++) {
            product = productList.get(i);
            if (product.getType().equals("ask") && product.getSize() != 0) {
                if (bestAsk == -1) {
                    bestAsk = i;
                }
                if (productList.get(bestAsk).getPrice() > product.getPrice()) {
                    bestAsk = i;
                }
            }
        }
        return productList.get(bestAsk);
    }


    public Product getBestBid() {
        int bestBid = -1;
        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            if (product.getType().equals("bid") && product.getSize() != 0) {
                if (bestBid == -1) {
                    bestBid = i;
                }
                if (product.getPrice() > productList.get(bestBid).getPrice()) {
                    bestBid = i;
                }
            }
        }
        return productList.get(bestBid);
    }

    public Product getProductSize(int price) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getPrice() == price) {
                return productList.get(i);
            }
        }
        return null;
    }

    public void commandExecution(ArrayList<String> cl) {
        for (int i = 0; i < cl.size(); i++) {
            String[] words = cl.get(i).split(",");
            switch (words[0]) {
                case "u" -> {
                    int word1 = Integer.parseInt(words[1]);
                    int word2 = Integer.parseInt(words[2]);
                    product = new Product(word1, word2, words[3]);
                    if (productList.isEmpty()) {
                        productList.add(product);
                        updateOrdersBook(product);
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

    public void updateOrdersBook(Product product) {
        int count = 0;
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getPrice() == product.getPrice()) {
                productList.get(i).setSize(product.getSize());
                break;
            } else {
                count++;
            }
        }
        if (count == productList.size()) {
            productList.add(product);
        }
    }


    public ArrayList<String> readInputFile() {
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

    public void writeOutputFile(Product product, String command) {
        if (product == null) {
            writeList.add("0" + "\n");
        } else if (command.equals("best_bid") | command.equals("best_ask")) {
            writeList.add(product.getPrice() + "," + product.getSize() + "\n");
        } else if (command.equals("size")) {
            writeList.add(product.getSize() + "\n");
        }
    }

    public void writeAllOutput(LinkedList<String> writeList) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + File.separator + "output.txt"));
            for (int i = 0; i < writeList.size(); i++) {
                writer.write(writeList.get(i));
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeOrder(String input, int size) {
        if (input.equals("sell")) {
            removeBid(size);
        } else if (input.equals("buy")) {
            removeAsk(size);
        }
    }


    public void removeBid(int size) {
        bigSize = size;
        product = getBestBid();
        firstRem = product.getSize();
        if ((firstRem - size) < 0) {
            while (bigSize > 0) {
                bigSize = getBigSize(bigSize, product);
                product = getBestBid();
            }
        } else {
            productList.get(productList.indexOf(product)).setSize(firstRem - size);
        }

    }

    public int getBigSize(int bigSize, Product product) {
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

    public void removeAsk(int size) {
        bigSize = size;
        product = getBestAsk();
        firstRem = product.getSize();
        if ((firstRem - size) < 0) {
            while (bigSize > 0) {
                bigSize = getBigSize(bigSize, product);
                product = getBestAsk();
            }
        } else {
            productList.get(productList.indexOf(product)).setSize(firstRem - size);
        }
    }
}
