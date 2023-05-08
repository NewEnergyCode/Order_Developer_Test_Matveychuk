package org.example;

import java.io.*;
import java.util.*;

public class OrderBook {


    private final ArrayList<Product> productList = new ArrayList<>();
    private final FileWriter outFile = new FileWriter(System.getProperty("user.dir") + File.separator + "output.txt");

    public OrderBook() throws IOException {
    }

    public Product getBestAsk() {
        Product bestAsk = null;
        for (Product product : productList) {
            if (product.getType().equals("ask") && product.getSize() != 0) {
                if (bestAsk == null) {
                    bestAsk = product;
                }
                for (Product value : productList) {
                    if (value.getType().equals("ask") && value.getSize() != 0) {
                        if (product.getPrice() < value.getPrice()) {
                            bestAsk = product;
                        }
                    }
                }
            }
        }
        return bestAsk;
    }


    public Product getBestBid() {
        return productList.stream()
                .filter(product -> product.getType().equals("bid"))
                .filter(product -> product.getSize() != 0)
                .max(Comparator.comparingInt(Product::getPrice)).orElse(null);
    }

    public Product getProductSize(Integer price) {
        return productList.stream()
                .filter(product -> product.getPrice().equals(price))
                .findAny().orElse(null);
    }

    public void commandExecution(ArrayList<String> cl) throws IOException {
        for (int i = 0; i < cl.size(); i++) {
            String[] words = cl.get(i).split(",");
            switch (words[0]) {
                case "u" -> {
                    if (productList.isEmpty()) {
                        productList.add(new Product(Integer.valueOf(words[1]), Integer.valueOf(words[2]), words[3]));
                        updateOrdersBook(new Product(Integer.valueOf(words[1]), Integer.valueOf(words[2]), words[3]));
                    } else {
                        updateOrdersBook(new Product(Integer.valueOf(words[1]), Integer.valueOf(words[2]), words[3]));
                    }
                }
                case "q" -> {

                    switch (words[1]) {
                        case "best_bid" -> writeOutputFile(getBestBid(), words[1]);
                        case "best_ask" -> writeOutputFile(getBestAsk(), words[1]);
                        case "size" -> writeOutputFile(getProductSize(Integer.valueOf(words[2])), words[1]);
                    }
                }
                case "o" -> {
                    if (words[1].equals("buy")) {
                        removeOrder(words[1], Integer.valueOf(words[2]));
                    }
                    if (words[1].equals("sell")) {
                        removeOrder(words[1], Integer.valueOf(words[2]));
                    }
                }
            }
        }
    }

    public void updateOrdersBook(Product product) {
        int count = 0;
        for (Product i : productList) {
            if (i.getPrice().equals(product.getPrice())) {
                productList.get(productList.indexOf(i)).setSize(product.getSize());
                productList.get(productList.indexOf(i)).setType(product.getType());
                break;
            } else {
                count++;
            }
        }
        if (count == productList.size()) {
            productList.add(product);
        }
    }


    public ArrayList<String> readInputFile() throws IOException {
        ArrayList<String> cl = new ArrayList<>();
        File input = new File(System.getProperty("user.dir") + File.separator + "input.txt");
        String line;
        BufferedReader br = new BufferedReader(new FileReader(input));
        while ((line = br.readLine()) != null) {
            cl.add(line);
        }
        br.close();
        return cl;
    }

    public void writeOutputFile(Product product, String command) {
        PrintWriter writer = new PrintWriter(outFile);
        if (product == null) {
            writer.write("0" + "\n");
        } else if (command.equals("best_bid") | command.equals("best_ask")) {
            writer.write(product.getPrice() + "," + product.getSize() + "\n");
        } else if (command.equals("size")) {
            writer.write(product.getSize().toString() + "\n");
        }
        writer.flush();
    }

    public void removeOrder(String input, Integer size) {
        if (input.equals("sell")) {
            Integer bigSize = size;
            Product firstRem = getBestBid();
            if ((firstRem.getSize() - size) < 0) {
                while (bigSize > 0) {
                    if (bigSize > getBestBid().getSize()) {
                        bigSize = bigSize - getBestBid().getSize();
                        removeBid("bid", 0);
                    } else {
                        removeBid("bid", getBestBid().getSize() - bigSize);
                        bigSize = 0;
                    }
                }
            } else {
                removeBid("bid", firstRem.getSize() - size);
            }
        } else if (input.equals("buy")) {
            Integer bigSize = size;
            Product firstRem = getBestAsk();
            if ((firstRem.getSize() - size) < 0) {
                while (bigSize > 0) {
                    if (bigSize > getBestAsk().getSize()) {
                        bigSize = bigSize - getBestAsk().getSize();
                        removeAsk("ask", 0);
                    } else {
                        removeAsk("ask", getBestAsk().getSize() - bigSize);
                        bigSize = 0;
                    }
                }
            } else {
                removeAsk("ask", firstRem.getSize() - size);
            }
        }
    }

    public void removeBid(String type, Integer size) {
        productList.stream()
                .filter(product -> product.getType().equals(type))
                .filter(product -> product.getSize() != 0)
                .max(Comparator.comparingInt(Product::getPrice)).get().setSize(size);
    }

    public void removeAsk(String type, Integer size) {
        productList.stream()
                .filter(product -> product.getType().equals(type))
                .filter(product -> product.getSize() != 0)
                .min(Comparator.comparingInt(Product::getPrice)).get().setSize(size);
    }
}
