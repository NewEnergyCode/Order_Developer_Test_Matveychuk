package org.example;

import java.io.*;
import java.util.*;

public class OrderBook {

    private final TreeSet<Product> askList = new TreeSet<>(Comparator.comparingInt(Product::getPrice));
    private final TreeSet<Product> bidList = new TreeSet<>(Comparator.comparingInt(Product::getPrice));


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
            result.append(askList.first().getPrice()).append(",").append(askList.first().getSize());
            return result.toString();
        }
        return "";
    }

    public String getBestBid() {
        if (!bidList.isEmpty()) {
            StringBuilder result = new StringBuilder();
            result.append(bidList.last().getPrice()).append(",").append(bidList.last().getSize());
            return result.toString();
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

//    public void commandExecution(String cl) throws IOException {
//        StringTokenizer tokenizer = new StringTokenizer(cl, ",");
//        String command = tokenizer.nextToken();
//        String secondWord;
//        switch (command) {
//            case "u" -> {
//                int word1 = Integer.parseInt(tokenizer.nextToken());
//                int word2 = Integer.parseInt(tokenizer.nextToken());
//                String type = tokenizer.nextToken();
//                Product product = new Product(word1, word2, type);
//                updateOrdersBooks(product);
//            }
//            case "q" -> {
//                secondWord = tokenizer.nextToken();
//                switch (secondWord) {
//                    case "best_bid", "best_ask" -> writeAllOutput(writeOutputFile(secondWord));
//                    case "size" -> writeAllOutput(writeOutputFile(tokenizer.nextToken()));
//                }
//            }
//            case "o" -> {
//                secondWord = tokenizer.nextToken();
//                if (secondWord.equals("buy") || secondWord.equals("sell")) {
//                    removeOrder(secondWord, Integer.parseInt(tokenizer.nextToken()));
//                }
//            }
//        }
//    }

    //    public void commandExecution(String cl) throws IOException {
//        int firstComma = cl.indexOf(",");
//        int secondComma = cl.indexOf(",", firstComma + 1);
//        String command = cl.substring(0, firstComma);
//        switch (command) {
//            case "u" -> {
//                int thirdComma = cl.indexOf(",", secondComma + 1);
//                int word1 = Integer.parseInt(cl.substring(firstComma + 1, secondComma));
//                int word2 = Integer.parseInt(cl.substring(secondComma + 1, thirdComma));
//                String type = cl.substring(thirdComma + 1);
//                Product product = new Product(word1, word2, type);
//                updateOrdersBooks(product);
//            }
//            case "q" -> {
//                String secondWord = cl.substring(firstComma + 1);
//                if (secondWord.equals("best_bid") || secondWord.equals("best_ask")) {
//                    writeAllOutput(writeOutputFile(secondWord));
//                } else {
//                    String size = cl.substring(secondComma + 1);
//                    writeAllOutput(writeOutputFile(size));
//                }
//            }
//            case "o" -> {
//                String secondWord = cl.substring(firstComma + 1, secondComma);
//                String size = cl.substring(secondComma + 1);
//                removeOrder(secondWord, Integer.parseInt(size));
//            }
//        }
//    }
    public void commandExecution(String cl) throws IOException {
        int firstComma = cl.indexOf(",");
        int secondComma = cl.indexOf(",", firstComma + 1);
        char command = cl.charAt(0);

        if (command == 'u') {
            int thirdComma = cl.indexOf(",", secondComma + 1);
            int word1 = Integer.parseInt(cl.substring(firstComma + 1, secondComma));
            int word2 = Integer.parseInt(cl.substring(secondComma + 1, thirdComma));
            char type = cl.charAt(thirdComma + 1);
            Product product = new Product(word1, word2, type);
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


    public void checkingAndUpdate(TreeSet<Product> orderBook, Product product) {
        for (Product p : orderBook) {
            if (p.getPrice() == product.getPrice()) {
                orderBook.remove(p);
                return;
            }
        }
        if (product.getSize() != 0) {
            orderBook.add(product);
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
            Product bestBid = bidList.last();
            if (bestBid.getSize() <= size) {
                size -= bestBid.getSize();
                bidList.remove(bestBid);
            } else {
                bestBid.setSize(bestBid.getSize() - size);
                size = 0;
            }
        }
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
