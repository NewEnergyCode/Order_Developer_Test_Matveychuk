package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class OrderBook {

    private final HashMap<Integer, String> cl = new HashMap<>();
    private final ArrayList<Product> productList = new ArrayList<>();
    private BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + File.separator + "outputFile.txt"));
    private FileWriter file;

    public OrderBook() throws IOException {
    }

    public Product getBestAsk() {
        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.stream().filter(product -> product.getType().equals("ask"))
                    .min(Comparator.comparingInt(Product::getPrice)).orElse(null);
        }

    }

    public Product getBestBid() {
        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.stream().filter(product -> product.getType().equals("bid"))
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


    public void updateOrdersBook(Product product) {
        switch (product.getType()) {
            case "bid" -> {
                if (productList.isEmpty()) {
                    productList.add(product);
                } else {
                    int count = 0;
                    for (Product i : productList) {
                        if (i.getPrice().equals(product.getPrice()) && i.getType().equals(product.type)) {
                            productList.get(productList.indexOf(i)).setSize(product.getSize());
                            break;
                        } else if ((i.getPrice() > getBestAsk().getPrice()) && i.getType().equals(product.type)) {
                            System.out.println("Incorrect input file. Bid > Ask. Try again.");
                            break;
                        } else {
                            count++;
                        }
                    }
                    if (count == productList.size()) {
                        productList.add(product);
                    }
                }
            }
            case "ask" -> {
                if (productList.isEmpty()) {
                    productList.add(product);
                } else {
                    int count = 0;
                    for (Product i : productList) {
                        if (i.getPrice().equals(product.getPrice()) && i.getType().equals(product.type)) {
                            productList.get(productList.indexOf(i)).setSize(product.getSize());
                            break;
                        } else if ((i.getPrice() < getBestBid().getPrice()) && i.getType().equals(product.type)) {
                            System.out.println("Incorrect input file. Bid > Ask. Try again.");
                            break;
                        } else {
                            count++;
                        }
                    }
                    if (count == productList.size()) {
                        productList.add(product);
                    }
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
        if (command.equals("best_bid") | command.equals("best_ask")) {
            writer.write(product.getPrice() + "," + product.getSize());
            writer.newLine();
            writer.flush();
        } else if (command.equals("size")) {
            writer.write(product.getSize().toString());
            writer.newLine();
            writer.flush();
        }
    }

    public void remove(String input, Integer size) {
        String type;
        if (input.equals("sell")) {
            type = "bid";
        } else if (input.equals("size")) {
            type = "ask";
        } else {
            type = input;
        }
        Product rem = productList.stream().filter(product -> product.getType().equals(type))
                .max(Comparator.comparingInt(Product::getPrice)).get();
        productList.stream().filter(product -> product.getType().equals(type))
                .max(Comparator.comparingInt(Product::getPrice)).get().setSize(rem.getSize() - size);
    }

    public void commandExecution() throws IOException {
        for (int i = 1; i < cl.size() + 1; i++) {
            String[] words = cl.get(i).split(",");

            switch (words[0]) {
                case "u" -> {
                    updateOrdersBook(new Product(Integer.valueOf(words[1]), Integer.valueOf(words[2]), words[3]));
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

}
