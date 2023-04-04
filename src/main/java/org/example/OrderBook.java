package org.example;

import java.io.*;
import java.util.*;

public class OrderBook {

    private final Map<Integer, String> cl = new HashMap<>();
    private final ArrayList<Product> productList = new ArrayList<>();
    private BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + File.separator + "output.txt"));
    private FileWriter file;
    private boolean lastCommand;

    public OrderBook() throws IOException {
    }

    public Product getBestAsk() {
        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.stream().filter(product -> product.getType().equals("ask"))
                    .filter(product -> product.getSize() != 0)
                    .min(Comparator.comparingInt(Product::getPrice)).orElse(null);
        }

    }

    public Product getBestBid() {
        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.stream().filter(product -> product.getType().equals("bid"))
                    .filter(product -> product.getSize() != 0)
                    .max(Comparator.comparingInt(Product::getPrice)).orElse(null);
        }
    }

    public Product getProductSize(Integer price) {
        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.stream().filter(product -> product.getPrice().equals(price))
                    .findAny().orElse(null);
        }
    }

    public void commandExecution() throws IOException {
        int last = 0;
        for (Map.Entry<Integer, String> entry : cl.entrySet()) {
            String[] words = cl.get(entry.getKey()).split(",");
            if (words[0].equals("q"))
                last = entry.getKey();
        }
        for (int i = 1; i < cl.size() + 1; i++) {
            String[] words = cl.get(i).split(",");
            if (i == (last)) {
                lastCommand = true;
            }
            switch (words[0]) {
                case "u" -> {
                    if ((Integer.parseInt(words[1]) > Math.pow(10, 9)) | (Integer.parseInt(words[2]) > Math.pow(10, 8))
                            | (Integer.parseInt(words[2]) < 0) | (Integer.parseInt(words[1]) < 1)) {
                        System.out.println("Incorrect input file. " + words[1] + " < 1. Unlimited. Try again.");
                        continue;
                    } else {
                        if (productList.isEmpty()) {
                            productList.add(new Product(Integer.valueOf(words[1]), Integer.valueOf(words[2]), words[3]));
                            updateOrdersBook(new Product(Integer.valueOf(words[1]), Integer.valueOf(words[2]), words[3]));
                        } else {
                            updateOrdersBook(new Product(Integer.valueOf(words[1]), Integer.valueOf(words[2]), words[3]));
                        }
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
                        remove(words[1], Integer.valueOf(words[2]));
                    }
                    if (words[1].equals("sell")) {
                        remove(words[1], Integer.valueOf(words[2]));
                    }
                }
            }
        }
    }

    public void updateOrdersBook(Product product) {
        Integer bestAsk = 0;
        if (getBestAsk() == null) {
            bestAsk = Integer.valueOf((int) Math.pow(10, 9));
        } else {
            bestAsk = getBestAsk().getPrice();
        }
        Integer bestBid = 0;
        if (getBestBid() == null) {
            bestBid = 0;
        } else {
            bestBid = getBestBid().getPrice();
        }

        switch (product.getType()) {
            case "bid" -> {

                int count = 0;
                for (Product i : productList) {
                    if (i.getPrice().equals(product.getPrice()) && i.getType().equals(product.type)) {
                        productList.get(productList.indexOf(i)).setSize(product.getSize());
                        break;
                    } else if ((product.getPrice() >= bestAsk)) {
                        System.out.println("Incorrect input file. Bid >= Ask. Try again.");
                        break;
                    } else if ((product.getPrice() > Math.pow(10, 9)) | (product.getSize() > Math.pow(10, 8))
                            | (product.getSize() < 0) | (product.getPrice() < 1)) {
                        System.out.println("Incorrect input file. Unlimited. Try again.");
                        break;
                    } else {
                        count++;
                    }
                }
                if (count == productList.size()) {
                    productList.add(product);
                }

            }
            case "ask" -> {
                int count = 0;
                for (Product i : productList) {
                    if (i.getPrice().equals(product.getPrice()) && i.getType().equals(product.getType())) {
                        productList.get(productList.indexOf(i)).setSize(product.getSize());
                        break;
                    } else if ((product.getPrice() <= bestBid)) {
                        System.out.println("Incorrect input file. Ask <= Bid. Try again.");
                        break;
                    } else if ((product.getPrice() > Math.pow(10, 9)) | (product.getSize() > Math.pow(10, 8))
                            | (product.getSize() < 0) | (product.getPrice() < 1)) {
                        System.out.println("Incorrect input file. Unlimited. Try again.");
                        break;
                    } else {
                        count++;
                    }
                }
                if (count == productList.size()) {
                    productList.add(product);
                }
            }

            default -> {
                productList.add(product);
            }
        }
    }


    public void readInputFile(File input) throws FileNotFoundException {
        cl.clear();
        Integer key = 0;
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            while ((line = br.readLine()) != null) {
                key++;
                cl.put(key, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeOutputFile(Product product, String command) throws IOException {
        if (product == null) {
            writer.flush();
        } else if (command.equals("best_bid") | command.equals("best_ask")) {
            writer.write(product.getPrice() + "," + product.getSize());
            if (!lastCommand) {
                writer.write("\n");
            }
            writer.flush();
        } else if (command.equals("size")) {
            writer.write(product.getSize().toString());
            if (!lastCommand) {
                writer.write("\n");
            }
            writer.flush();
        }
    }

    public void remove(String input, Integer size) {
        String type;
        if (input.equals("sell")) {
            type = "bid";
            Product rem = productList.stream().filter(product -> product.getType().equals(type))
                    .filter(product -> product.getSize() != 0)
                    .max(Comparator.comparingInt(Product::getPrice)).get();
            if ((rem.getSize() - size) < 0) {
                System.out.println("Applications are over. Sorry.");
            } else {
                productList.stream().filter(product -> product.getType().equals(type))
                        .filter(product -> product.getSize() != 0)
                        .max(Comparator.comparingInt(Product::getPrice)).get().setSize(rem.getSize() - size);
            }
        } else if (input.equals("buy")) {
            type = "ask";
            Product rem = productList.stream().filter(product -> product.getType().equals(type))
                    .filter(product -> product.getSize() != 0)
                    .min(Comparator.comparingInt(Product::getPrice)).get();
            if ((rem.getSize() - size) < 0) {
                System.out.println("Applications are over. Sorry.");
            } else {
                productList.stream().filter(product -> product.getType().equals(type))
                        .filter(product -> product.getSize() != 0)
                        .min(Comparator.comparingInt(Product::getPrice)).get().setSize(rem.getSize() - size);
            }
        }
    }

}
